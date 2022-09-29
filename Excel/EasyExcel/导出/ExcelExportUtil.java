package com.xx.order.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.xx.common.enums.BusinessTypeEnum;
import com.xx.common.exception.XXException;
import com.xx.common.util.DateUtil;
import com.xx.order.entity.dao.ExportCenter;
import com.xx.order.entity.dto.ExportBaseDto;
import com.xx.order.entity.enums.ExportCenterStateEnum;
import com.xx.order.ifeign.IExportCenterClient;
import com.xx.order.ifeign.IFileOperationClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* 文件名: ExcelBatchUtil.java
* 作者: xiahao
* 时间: 2020/7/1 14:19
* 描述: 批量导出Excel
*/
@Slf4j
public abstract class ExcelExportUtil<T> {

    private final IFileOperationClient iFileOperationClient;
    private final IExportCenterClient iExportCenterClient;

    /**
     * iFileOperationClient 文件上传Feign
     * iExportCenterClient 下载中心Feign
    */
    public ExcelExportUtil(IFileOperationClient iFileOperationClient, IExportCenterClient iExportCenterClient){
        this.iFileOperationClient = iFileOperationClient;
        this.iExportCenterClient = iExportCenterClient;
    }

    /**
    * 方法名: getExportPageData
    * 描述: 分页查询数据
    * 参数:
     * condtion 条件
     * current 当前页码
    */
    protected abstract List<T> getExportPageData(Object condtion, int current);

    /**
    * 方法名: export
    * 描述: 导出
    * 参数:
     * fileName 文件名
     * fileType 文件类型
     * userId 用户ID
     * baseDto 导出参数基础类
     * realT 最终导出结果集类型
    */
    public boolean export(String fileName, Integer fileType, Long userId, ExportBaseDto baseDto, Class<T> realT) {
        Assert.notNull(fileName,"param 'fileName' is null");
        Assert.notNull(userId,"param 'userId' is null");
        Assert.notNull(baseDto,"param 'baseDto' is null");
        Assert.notNull(fileType,"param 'fileType' is null");

        String filePath = "./datafile" + File.separator + "exportfile" + File.separator + fileName;

        //插入下载中心表
        Long centerId = insertExportCenter(fileName, fileType, userId, baseDto);
        if (Objects.isNull(centerId)) return false;

        ThreadPoolUtil.getInstance().execute(() -> {
            try{
                exportBatchExcel(baseDto, realT, filePath, fileName);
                MultipartFile file = getMultipartFile(filePath);

                log.info(fileName +"列表导出 .... 转换文件完成, 开始上传文件....");
                String path = iFileOperationClient.uploadFile(file, BusinessTypeEnum.BUSINESS_ORDER.getCode());
                log.info(fileName +"列表导出 .... 上传文件完成, 开始更新记录表....path:{}",path);

                //更新下载中心
                updateExportCenter(centerId, path, false);
            }catch (Exception e){
                //更新下载中心
                updateExportCenter(centerId, null, true);

                log.info(fileName +"列表导出异常  e:{}", ExceptionUtils.getStackTrace(e));
            }finally {
                //删除原始记录
                delAllFile(new File(filePath));
            }
        });

        return true;
    }

    //插入下载中心
    private Long insertExportCenter(String fileName, Integer fileType, Long userId, ExportBaseDto baseDto) {
        ExportCenter ec = new ExportCenter();
        ec.setFileName(fileName);
        ec.setFileType(fileType);
        ec.setBeginTime(DateUtil.toLocalDateTime(Objects.isNull(baseDto.getBeginTime())? new Date(): baseDto.getBeginTime()));
        ec.setEndTime(DateUtil.toLocalDateTime(Objects.isNull(baseDto.getEndTime())? new Date(): baseDto.getEndTime()));
        ec.setFileState(ExportCenterStateEnum.DOING.value());
        ec.setCreateId(userId);
        ec.setRequestTime(LocalDateTime.now());
        Long centerId = iExportCenterClient.insertSelective(ec);
        if (Objects.isNull(centerId)){
            return null;
        }
        return centerId;
    }
    //更新下载中心
    private void updateExportCenter(Long centerId, String path, boolean failed) {
        ExportCenter exportCenter = iExportCenterClient.selectByPrimaryKey(centerId);
        if (failed){
            exportCenter.setFileState(ExportCenterStateEnum.FAILED.value());
        }else {
            if (StringUtils.isNotBlank(path)){
                exportCenter.setFilePath(path);
                exportCenter.setFinishTime(LocalDateTime.now());
                exportCenter.setFileState(ExportCenterStateEnum.SUCCESS.value());
            }else {
                exportCenter.setFileState(ExportCenterStateEnum.FAILED.value());
            }
        }
        iExportCenterClient.updateByPrimaryKeySelective(exportCenter);
    }


    //批量导出
    private void exportBatchExcel(Object condtion, Class<T> realT, String filePath, String sheetName){
        //判断文件路径 无 则创建
        File exFile = new File(filePath);
        if (!exFile.getParentFile().exists()) {
            exFile.getParentFile().mkdirs();
        }

        ExcelWriter writer = EasyExcel.write(filePath, realT).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

        try {
            int current = 1;
            while (true){
                //分页查询
                List<T> exportDatas = getExportPageData(condtion, current);
                if (CollectionUtils.isEmpty(exportDatas)){
                    if (current == 1){
                        writer.write(exportDatas, writeSheet);
                    }
                    break;
                }
                writer.write(exportDatas, writeSheet);
                log.info("列表导出，循环页码 current:{}",current);

                //还有数据 继续查询
                current++;
            }

            writer.finish();
        } catch (Exception e) {
            log.error("excel文件导出失败, 失败原因：{}", ExceptionUtils.getStackTrace(e));
            throw new XXException(ExceptionUtils.getStackTrace(e));
        }finally {
            if(writer != null){
                writer.finish();
            }
        }
    }
    //将File 转成 MultipartFile
    private static MultipartFile getMultipartFile(String filePath){
        Assert.notNull(filePath,"file to multipartFile param : 'filePath' not null");

        File file = new File(filePath);

        try (FileInputStream fileInputStream = new FileInputStream(file)){
            return new MockMultipartFile("file",file.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);
        } catch (Exception e) {
            log.error("File 转成 MultipartFile 失败, 失败原因：{}", ExceptionUtils.getStackTrace(e));
            throw new XXException(ExceptionUtils.getStackTrace(e));
        }
    }
    //删除文件
    private void delAllFile( File directory){
        if (!directory.isDirectory()){ //文件
            directory.delete();
        }else {                     //文件夹
            File[] files = directory.listFiles(); //获取所有的文件

            //空文件夹
            assert files != null;
            if (files.length == 0){
                directory.delete();
                return;
            }

            //删除子文件和子文件夹
            for (File file : files){
                if (file.isDirectory()){
                    delAllFile(file);
                }else {
                    file.delete();
                }
            }

            //删除文件夹本身
            directory.delete();
        }
    }
}
