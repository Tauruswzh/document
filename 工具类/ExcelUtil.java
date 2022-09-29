package com.hellozj.order.util;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.hellozj.common.enums.ExceptionEnum;
import com.hellozj.common.exception.HelloZjException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
* 文件名: ExcelUtil.java
* 作者: xiahao
* 时间: 2020/7/1 14:19
* 描述: 导出Excel
*/
@Slf4j
public class ExcelUtil {
    private static Sheet initSheet;

    static {
        initSheet = new Sheet(1, 0);
        initSheet.setSheetName("sheet");
    }


    /**
     * 生成excle
     * @param filePath 绝对路径, 如：/home/chenmingjian/Downloads/aaa.xlsx
     * @param data 数据源
     */
    public static void writeWithTemplate(String filePath, List<? extends BaseRowModel> data){
        Assert.notNull(filePath,"export excel param : 'filePath' not null");
        Assert.notEmpty(data,"export excel param : 'list' not empty");
        writeWithTemplateAndSheet(filePath,data,null);
    }

    /**
     * 生成excle
     * @param filePath 绝对路径, 如：/home/chenmingjian/Downloads/aaa.xlsx
     * @param data 数据源
     * @param sheet excle页面样式
     */
    public static void writeWithTemplateAndSheet(String filePath, List<? extends BaseRowModel> data, Sheet sheet){
        if(CollectionUtils.isEmpty(data)){
            return;
        }

        sheet = (sheet != null) ? sheet : initSheet;
        sheet.setClazz(data.get(0).getClass());

        OutputStream outputStream = null;
        ExcelWriter writer = null;
        try {
            File exFile = new File(filePath);
            if (!exFile.getParentFile().exists()) {
                exFile.getParentFile().mkdirs();
            }

            outputStream = new FileOutputStream(filePath);
            writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);
            writer.write(data,sheet);

            outputStream.flush();
            } catch (Exception e) {
            log.error("excel文件导出失败, 失败原因：{}", e);
            throw new HelloZjException(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if(writer != null){
                    writer.finish();
                }

                if(outputStream != null){
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error("excel文件导出流关闭异常：{}", e);
            }
        }
    }

    /**
    * 方法名:  getMultipartFile
    * 作者/时间: xiahao-2020/7/1
    * 描述: 将File 转成 MultipartFile
    * 参数: ul
    * 返回:
    */
    public static MultipartFile getMultipartFile(String filePath){
        Assert.notNull(filePath,"file to multipartFile param : 'filePath' not null");

        File file = new File(filePath);

        FileInputStream fileInputStream = null;
        MultipartFile multipartFile = null;
        try {
            fileInputStream = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file",file.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);
        } catch (Exception e) {
            log.error("File 转成 MultipartFile 失败, 失败原因：{}", e);
            throw new HelloZjException(ExceptionUtils.getStackTrace(e));
        }finally {
            try {
                if (fileInputStream != null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                log.error("File 转成 MultipartFile 流关闭异常：{}", e);
            }
        }
        return multipartFile;
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel       文件
     * @param rowModel    实体类映射，继承 BaseRowModel 类
     * @param sheetNo     sheet 的序号 从1开始
     * @param headLineNum 表头行数，默认为1
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel, int sheetNo, int headLineNum) {
        Assert.notNull(excel,"file to multipartFile param : 'excel' not null");
        Assert.notNull(rowModel,"file to BaseRowModel param : 'rowModel' not null");

        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);

        if (sheetNo <= 0){
            sheetNo = 1;
        }
        if (headLineNum <= 0){
            headLineNum = 1;
        }
        reader.read(new Sheet(sheetNo, headLineNum, rowModel.getClass()));

        return excelListener.getDatas();
    }

    /**
     * 返回 ExcelReader
     *
     * @param excel 需要解析的 Excel 文件
     * @param excelListener new ExcelListener()
     */
    private static ExcelReader getReader(MultipartFile excel, ExcelListener excelListener) {
        String filename = excel.getOriginalFilename();

        if (filename == null || (!filename.toLowerCase().endsWith(".xls") && !filename.toLowerCase().endsWith(".xlsx"))) {
            throw new HelloZjException(ExceptionEnum.IMPORT_EXCEL_ERROR);
        }
        InputStream inputStream;

        try {
            inputStream = new BufferedInputStream(excel.getInputStream());

            return new ExcelReader(inputStream, null, excelListener, false);
        } catch (IOException e) {
            log.error("解析Excel 失败, 失败原因：{}", e);
            throw new HelloZjException(ExceptionUtils.getStackTrace(e));
        }
    }


    //region 流有问题
   /* public static MultipartFile writeWithTemplateAndSheet(List<? extends BaseRowModel> data,String fileName){
        Assert.notNull(fileName,"export excel param : 'fileName' not null");
        Assert.notEmpty(data,"export excel param : 'list' not empty");

        Sheet sheet = initSheet;
        sheet.setClazz(data.get(0).getClass());

        ExcelWriter writer = null;
        MultipartFile multipartFile = null;
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            writer.write(data,sheet);
            out.flush();

            byte [] bookByteAry = out.toByteArray();

            multipartFile = new MockMultipartFile("file",fileName,ContentType.APPLICATION_OCTET_STREAM.toString(),bookByteAry);

        } catch (Exception e) {
            log.error("excel文件导出失败, 失败原因：{}", e);
            throw new HelloZjException(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if(writer != null){
                    writer.finish();
                }

                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                log.error("excel文件导出流关闭异常：{}", e);
            }
        }
        if (multipartFile == null){
            throw new HelloZjException("转换 MultipartFile 失败");
        }
        return multipartFile;
    }*/
    //endregion

}


/**
* 文件名: ExcelListener.java
* 作者: xiahao
* 时间: 2020/7/25 12:04
* 描述: excelListener
*/
class ExcelListener extends AnalysisEventListener {
    //自定义用于暂时存储data
    //private List<Object> datas = Collections.synchronizedList(new ArrayList<>());
    private List<Object> datas = new ArrayList<>();

    /**
     * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
     */
    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        datas.add(o);
    }

    /**
     * 读取完之后的操作
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }
}
