package com.xx.order.entity.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.metadata.BaseRowModel;
import com.xx.order.annotation.ImportAnnotation;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.Serializable;

@Data
public class ImportOrderParamDto extends BaseRowModel implements Serializable {

    @ColumnWidth(10)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "订单类型", index = 0)
    @ImportAnnotation(source = {"手动转派","自动转派","抢单池"})
    private String dispatchFlag;


    @ColumnWidth(15)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "客户名", index = 1)
    private String customerName;


    @ColumnWidth(20)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "客户手机号", index = 2)
    private String customerPhone;


    @ColumnWidth(15)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "客户预约时间_开始", index = 3)
    private String appointTimeBegin;

    @ColumnWidth(15)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "客户预约时间_结束", index = 4)
    private String appointTimeEnd;


    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "服务", index = 5)
    @ImportAnnotation(name = "serviceCode")
    private String serviceCode;


    @ColumnWidth(10)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "服务数量", index = 6)
    private String serviceNum;


    @ColumnWidth(10)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "订单金额", index = 7)
    private String orderPrice;


    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "订单描述", index = 8)
    private String orderContent;


    @ColumnWidth(10)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "省份", index = 9)
    private String provinceName;


    @ColumnWidth(10)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "城市", index = 10)
    private String cityName;


    @ColumnWidth(10)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "区域", index = 11)
    private String areaName;


    @ColumnWidth(50)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "详细地址", index = 12)
    private String detailInfo;


    @ColumnWidth(20)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "商机订单号", index = 13)
    private String upperOrderNum;

    private static final long serialVersionUID = 1L;
}