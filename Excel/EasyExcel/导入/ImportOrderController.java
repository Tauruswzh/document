package com.xx.order.controller.platform;

import com.xx.common.constant.BaseValueConstant;
import com.xx.common.enums.BusinessTypeEnum;
import com.xx.common.enums.ExceptionEnum;
import com.xx.common.exception.XXException;
import com.xx.common.vo.ResultResponse;
import com.xx.order.annotation.ImportAnnotation;
import com.xx.order.entity.dto.ImportOrderParamDto;
import com.xx.order.entity.vo.ItemVo;
import com.xx.order.ifeign.IStoreItemClient;
import com.xx.order.service.FileOperationService;
import com.xx.order.service.OrderInfoFacadeService;
import com.xx.order.util.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
* 文件名: PlatformOrderController.java
* 作者: xiahao
* 时间: 2021/6/2 下午4:52
* 描述: 平台导入订单处理
*/

@Validated
@RestController
@RequestMapping("/platform/order")
@Api(value = "importOrderController", tags = "平台导入订单处理")
public class ImportOrderController {
    @Autowired
    private OrderInfoFacadeService orderInfoFacadeService;
    @Autowired
    private IStoreItemClient iStoreItemClient;
    @Autowired
    private FileOperationService fileOperationService;

    /**
    * 方法名:  importTemplate
    * 作者/时间: xiahao-2021/6/7
    * 描述: 导入模版
    * 参数: ul
    * 返回:
    */
    @ApiOperation(value = "导入模版")
    @PostMapping("/importTemplate")
    public ResultResponse<String> exportTemplate() {
        //头下拉数据
        //获取自定义注解字段
        Field[] fields = ImportOrderParamDto.class.getDeclaredFields();
        Map<Integer, String[]> map = new HashMap<>();
        // 响应字段对应的下拉集合
        List<Map<Integer, String[]>> mapList = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            ImportAnnotation annotation = field.getAnnotation(ImportAnnotation.class);
            if (!Objects.isNull(annotation)){
                //固定的
                String[] source = annotation.source();
                if (source.length > 0) {
                    map.put(i,source);
                    mapList.add(map);
                }

                //变量
                String name = annotation.name();
                if (StringUtils.isNotBlank(name) && name.equals("serviceCode")){
                    List<String> fieldList = new ArrayList<>();
                    // 查询出所有的平台的服务编码
                    List<ItemVo> itemVos = iStoreItemClient.queryStoreItem();
                    if (!CollectionUtils.isEmpty(itemVos)) {
                        itemVos.forEach(itemVo -> fieldList.add(itemVo.getItemId() + BaseValueConstant.CODE_SPLIT + itemVo.getItemDesc()));
                    }
                    String[] params = fieldList.toArray(new String[fieldList.size()]);
                    map.put(i,params);
                    mapList.add(map);
                }
            }
        }

        //填充的示例数据
        List<ImportOrderParamDto> list = new ArrayList<>();
        ImportOrderParamDto testModel = new ImportOrderParamDto();
        testModel.setDispatchFlag("手动转派");
        testModel.setCustomerName("示例");
        testModel.setCustomerPhone("14762228888");
        testModel.setAppointTimeBegin("yyyy-MM-dd HH:mm 或 2020/12/12 12:12 或 yyyy年MM月dd日 HH时mm分");
        testModel.setAppointTimeEnd("yyyy-MM-dd HH:mm 或 2020/12/12 12:12 或 yyyy年MM月dd日 HH时mm分");
        testModel.setServiceCode("1");
        testModel.setServiceNum("1");
        testModel.setOrderPrice("201.00");
        testModel.setOrderContent("24小时专业空调清洗");
        testModel.setProvinceName("江苏省");
        testModel.setCityName("南京市");
        testModel.setAreaName("江宁区");
        testModel.setDetailInfo("百家湖花园2201室");
        list.add(testModel);
        String fileName = "订单导入模板" + System.currentTimeMillis() + ".xls";
        String filePath = "./datafile" + File.separator + "templatefile" + File.separator + fileName;
        ExcelUtil.writeWithTemplate(filePath, list, mapList);

        //生成文件
        MultipartFile file = ExcelUtil.getMultipartFile(filePath);
        String path = fileOperationService.uploadFile(file, BusinessTypeEnum.BUSINESS_ORDER.getCode());
        //删除模版文件
        ExcelUtil.delAllFile(new File(filePath));
        return ResultResponse.success(path);
    }

    /**
    * 方法名:  importOrder
    * 作者/时间: xiahao-2021/6/7
    * 描述: 导入订单
    * 参数: ul
    * 返回:
    */
    @ApiOperation(value = "导入订单")
    @PostMapping(value = "/importOrder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultResponse<String> importOrder(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new XXException(ExceptionEnum.IMPORT_EMPTY_ERROR);
        }
        //判断文件大小 1M
        if (file.getSize() > 1048576){
            throw new XXException(ExceptionEnum.IMPORT_BIG_DATE_ERROR);
        }
        List<Object> objects = null;
        try {
            objects = ExcelUtil.readExcel(file, new ImportOrderParamDto(), 1, 1);
        } catch (Exception e) {
            throw new XXException(ExceptionEnum.READ_FILE_ERROR);
        }
        if (CollectionUtils.isEmpty(objects)) {
            throw new XXException(ExceptionEnum.IMPORT_EMPTY_ERROR);
        }

        //判断条数
        if (objects.size() > 2000){
            throw new XXException(ExceptionEnum.IMPORT_BIG_DATE_ERROR);
        }
        List<ImportOrderParamDto> list = new ArrayList<>();
        objects.forEach(i -> {
            ImportOrderParamDto testModel = (ImportOrderParamDto) i;
            list.add(testModel);
        });

        //批量创建订单
        orderInfoFacadeService.importOrder(list);
        return ResultResponse.success(BaseValueConstant.SUCCESS);
    }

}
