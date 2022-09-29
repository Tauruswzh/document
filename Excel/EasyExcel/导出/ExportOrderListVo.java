package com.xx.order.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.metadata.BaseRowModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 文件名: ExportOrderListVo.java
 * 作者: xuzhenwei
 * 时间: 2021-06-22 11:46
 * 描述: 订单列表导出
 */
@Data
public class ExportOrderListVo extends BaseRowModel implements Serializable {

    @ColumnWidth(30)
    @ExcelProperty(value="订单编号",index=0)
    private String orderNum;

    @ColumnWidth(25)
    @ExcelProperty(value="商机商户名称",index=1)
    private String storeName;

    @ColumnWidth(25)
    @ExcelProperty(value="商户订单号",index=2)
    private String upperOrderNum;

    @ColumnWidth(20)
    @ExcelProperty(value="客户信息",index=3)
    private String customerInfo;

    @ColumnWidth(25)
    @ExcelProperty(value="服务描述",index=4)
    private String orderContent;

    @ColumnWidth(25)
    @ExcelProperty(value="服务内容",index=5)
    private String serviceContent;

    @ColumnWidth(20)
    @ExcelProperty(value="客户预约日期_开始",index=6)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appointTimeBegin;

    @ColumnWidth(20)
    @ExcelProperty(value="客户预约日期_结束",index=7)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appointTimeEnd;

    @ColumnWidth(15)
    @ExcelProperty(value="服务商户名称",index=8)
    private String dispatchStoreName;

    @ColumnWidth(15)
    @ExcelProperty(value="工程师名字",index=9)
    private String engName;

    @ColumnWidth(15)
    @ExcelProperty(value="订单状态",index=10)
    private String orderStateStr;

    @ColumnWidth(20)
    @ExcelProperty(value="创建时间",index=11)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ColumnWidth(20)
    @ExcelProperty(value="完成时间",index=12)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishTime;

    @ColumnWidth(15)
    @ExcelProperty(value="订单金额",index=13)
    private String orderPriceStr;

    @ColumnWidth(15)
    @ExcelProperty(value="省份",index=14)
    private String provinceName;

    @ColumnWidth(15)
    @ExcelProperty(value="地市",index=15)
    private String cityName;

    @ColumnWidth(15)
    @ExcelProperty(value="区县",index=16)
    private String areaName;

    @ColumnWidth(15)
    @ExcelProperty(value="街道",index=17)
    private String streetName;

    @ColumnWidth(15)
    @ExcelProperty(value="详细地址",index=18)
    private String detailInfo;


    /**
     * 主键ID
     * order_info.id
     */
    @ExcelIgnore
    private Long id;

    /**
     * 订单组 1平台录入 2系统对接 3....
     */
    @ExcelIgnore
    private Integer orderGroup;

    /**
     * 订单类型, 1手动转派 2自动转派 3可抢
     */
    @ExcelIgnore
    private Integer dispatchFlag;

    /**
     * 商机商户编号
     */
    @ExcelIgnore
    private String storeNum;

//    /**
//     * 商机商户名称
//     */
//    private String storeName;
//
//    /**
//     * 商户订单号
//     */
//    private String upperOrderNum;

    /**
     * 客户名
     * order_info.customer_name
     */
    @ExcelIgnore
    private String customerName;

    /**
     * 客户手机号
     * order_info.customer_phone
     */
    @ExcelIgnore
    private String customerPhone;

    /**
     * 服务编码
     * order_info.service_code
     */
    @ExcelIgnore
    private String serviceCode;

//    /**
//     * 服务内容
//     * order_info.service_content
//     */
//    private String serviceContent;

    /**
     * 服务数量
     * order_info.service_num
     */
    @ExcelIgnore
    private Integer serviceNum;

//    /**
//     * 订单描述
//     * order_info.order_content
//     */
//    private String orderContent;

//    /**
//     * 客户预约日期_开始
//     * order_info.appoint_time_begin
//     */
//    private Date appointTimeBegin;

//    /**
//     * 客户预约日期_结束
//     * order_info.appoint_time_end
//     */
//    private Date appointTimeEnd;

    /**
     * 订单金额
     * order_info.order_price
     */
    @ExcelIgnore
    @JsonSerialize(using = PriceBigDecimalSerialize.class)
    private BigDecimal orderPrice;

    /**
     * 订单状态 1	待分配 2待预约 3待上门 4服务中 10已完成 11已取消
     * order_info.order_state
     */
    @ExcelIgnore
    private Integer orderState;

    /**
     * 异常状态: 0正常 1取消中....
     * order_info.exception_state
     */
    @ExcelIgnore
    private Integer exceptionState;

    @ExcelIgnore
    private String exceptionStateStr;

//    /**
//     * 订单完成时间
//     * order_info.finish_time
//     */
//    private Date finishTime;
//
//    /**
//     * 创建时间
//     * order_info.create_time
//     */
//    private Date createTime;

    /**
     * 更新时间
     * order_info.update_time
     */
    @ExcelIgnore
    private Date updateTime;

    /**
     * 租户
     * order_info.tenant_id
     */
    @ExcelIgnore
    private Long tenantId;

//    /**
//     * 省份
//     * order_address.province_name
//     */
//    private String provinceName;
//
//    /**
//     * 地市
//     * order_address.city_name
//     */
//    private String cityName;
//
//    /**
//     * 区县
//     * order_address.area_name
//     */
//    private String areaName;
//
//    /**
//     * 街道
//     * order_address.street_name
//     */
//    private String streetName;
//
//    /**
//     * 详细地址
//     * order_address.detail_info
//     */
//    private String detailInfo;

//    private String orderStateStr;


    /**
     * 转派方式 1派单 2抢单
     * order_dispatch.dispatch_mode
     */
    @ExcelIgnore
    private Integer dispatchMode;

    /**
     * 转派类型 1商户 2工程师
     * order_dispatch.dispatch_type
     */
    @ExcelIgnore
    private Integer dispatchType;

    /**
     * 服务编码 平台
     * order_dispatch.platform_service_code
     */
    @ExcelIgnore
    private String platformServiceCode;

    /**
     * 服务编码 下游
     * order_dispatch.down_service_code
     */
    @ExcelIgnore
    private String downServiceCode;

    /**
     * 服务商户ID
     */
    @ExcelIgnore
    private Long dispatchStoreId;

    /**
     * 服务商户编号
     */
    @ExcelIgnore
    private String dispatchStoreNum;

//    /**
//     * 服务商户名称
//     */
//    private String dispatchStoreName;

    /**
     * 服务商户转派时间
     */
    @ExcelIgnore
    private Date storeDispatchTime;

    /**
     * 工程师ID
     * order_dispatch.eng_id
     */
    @ExcelIgnore
    private Long engId;

//    /**
//     * 工程师名称
//     * order_dispatch.eng_name
//     */
//    private String engName;

    /**
     * 工程师转派时间
     * order_dispatch.eng_dispatch_time
     */
    @ExcelIgnore
    private Date engDispatchTime;


    private static final long serialVersionUID = 1L;
}