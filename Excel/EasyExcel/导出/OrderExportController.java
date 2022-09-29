package com.xx.order.controller.export;

import com.github.pagehelper.PageInfo;
import com.xx.common.dto.PageDto;
import com.xx.common.enums.ExceptionEnum;
import com.xx.common.exception.XXException;
import com.xx.common.util.DateUtil;
import com.xx.common.vo.ResultResponse;
import com.xx.order.entity.dto.PageOrderListParamDto;
import com.xx.order.entity.enums.ExportCenterTypeEnum;
import com.xx.order.entity.vo.ExportOrderListVo;
import com.xx.order.entity.vo.PageOrderListVo;
import com.xx.order.ifeign.IExportCenterClient;
import com.xx.order.ifeign.IFileOperationClient;
import com.xx.order.service.OrderInfoFacadeService;
import com.xx.order.util.ExcelExportUtil;
import com.xx.security.core.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 文件名: ExportStoreOrderListController.java
 * 作者: xiahao
 * 时间: 2020/6/30 18:59
 * 描述: 订单导出
 */
@RestController
@RequestMapping("/export")
@Slf4j
public class OrderExportController {
    @Autowired
    private IFileOperationClient iFileOperationClient;
    @Autowired
    private IExportCenterClient iExportCenterClient;
    @Autowired
    private OrderInfoFacadeService orderInfoFacadeService;


    /**
     * 方法名:  exportOrderList
     * 作者/时间: xiahao-2020/6/30
     * 描述: 导出店铺端的订单列表
     * 参数: ul
     * 返回:
     */
    @PostMapping("/orderList")
    public ResultResponse<String> exportOrderList(@RequestBody @Valid PageOrderListParamDto dto, HttpServletResponse response) {
        //开始-结束时间 一个月
        Date beginTime = dto.getBeginTime();
        Date endTime = dto.getEndTime();
        if (Objects.nonNull(beginTime) && Objects.nonNull(endTime)){
            long finalDate = DateUtil.addMonth(beginTime, 1).getTime();
            if (finalDate < endTime.getTime()){
                throw new XXException(ExceptionEnum.EXPORT_TIME_ERROR);
            }
        }

        log.info("订单列表导出 begin....");

        //判断角色
        Long loginId = UserUtil.getLoginId();

        //商户列表
        if (UserUtil.isStore()){
            String storeNum = UserUtil.getStoreNum();
            dto.setStoreNum(storeNum);
            log.info("商户订单列表 .......storeNum:{}", storeNum);
        }

        //文件名
        String fileName = "订单列表" + DateUtil.format(new Date(), DateUtil.FORMAT_YYYMMDDHHMMSS_NOSTYLE) + loginId + ".xls";


        ExcelExportUtil<ExportOrderListVo> excelBatchUtil =
                new ExcelExportUtil<ExportOrderListVo>(iFileOperationClient, iExportCenterClient) {

                    //分页查询
                    @Override
                    protected List<ExportOrderListVo> getExportPageData(Object condtion, int current) {
                        PageDto<PageOrderListParamDto> dto = new PageDto<>();
                        dto.setPageNum(current);
                        dto.setPageSize(100);
                        dto.setData((PageOrderListParamDto) condtion);

                        PageInfo<PageOrderListVo> pageInfo = orderInfoFacadeService.exportOrderList(dto);
                        if (Objects.isNull(pageInfo) || CollectionUtils.isEmpty(pageInfo.getList())) {
                            return null;
                        }
                        //最终返回数据
                        List<ExportOrderListVo> list = new ArrayList<>();
                        pageInfo.getList().forEach(i->{
                            ExportOrderListVo vo = new ExportOrderListVo();
                            BeanUtils.copyProperties(i,vo);
                            vo.setOrderPriceStr(String.format("%.2f", i.getOrderPrice()));
                            vo.setCustomerInfo(i.getCustomerName() +" "+ i.getCustomerPhone());
                            list.add(vo);
                        });
                        return list;
                    }
                };

        //导出
        boolean export = excelBatchUtil.export(fileName, ExportCenterTypeEnum.ORDER.value(), loginId, dto, ExportOrderListVo.class);
        log.info("订单列表导出 end....");
        if (export){
            return ResultResponse.success(fileName);
        }
        return ResultResponse.error("导出失败");
    }
}
