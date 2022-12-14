package com.xx.order.service.impl;

import com.xx.common.constant.BaseValueConstant;
import com.xx.common.enums.ExceptionEnum;
import com.xx.common.exception.XXException;
import com.xx.oauth.vo.UserStoreInfoVo;
import com.xx.order.controller.strategy.StrategyContext;
import com.xx.order.entity.dao.OrderAddress;
import com.xx.order.entity.dao.OrderDispatch;
import com.xx.order.entity.dao.OrderInfo;
import com.xx.order.entity.dao.OrderStateExtend;
import com.xx.order.entity.dto.ImportOrderDto;
import com.xx.order.entity.dto.ImportOrderParamDto;
import com.xx.order.entity.dto.RedeployConfDto;
import com.xx.order.entity.dto.ThirdCreateOrderDto;
import com.xx.order.entity.enums.CallModeEnum;
import com.xx.order.entity.enums.DelFlagEnum;
import com.xx.order.entity.enums.DispatchFlagEnum;
import com.xx.order.entity.enums.DispatchModeEnum;
import com.xx.order.entity.enums.DispatchTypeEnum;
import com.xx.order.entity.enums.ExceptionStateEnum;
import com.xx.order.entity.enums.OrderGroupEnum;
import com.xx.order.entity.enums.OrderStateEnum;
import com.xx.order.entity.enums.OrderStateExtendEnum;
import com.xx.order.entity.vo.RedeployConfVo;
import com.xx.order.ifeign.IOrderInfoClient;
import com.xx.order.ifeign.IStoreInfoItemClient;
import com.xx.order.ifeign.IStoreInfoRelationClient;
import com.xx.order.service.OrderInfoFacadeService;
import com.xx.order.util.OrderNumUtil;
import com.xx.order.util.ThirdCallBackUtil;
import com.xx.security.core.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class OrderInfoFacadeServiceImpl implements OrderInfoFacadeService {
    @Autowired
    private IOrderInfoClient iOrderInfoClient;
    @Autowired
    private StrategyContext strategyContext;
    @Autowired
    private IStoreInfoRelationClient iStoreInfoRelationClient;
    @Autowired
    private IStoreInfoItemClient iStoreInfoItemClient;


    /**
     * ?????????:  importOrder
     * ??????/??????: xiahao-2021/6/7
     * ??????: ????????????
     * ??????: ul
     * ??????:
     */
    @Override
    public void importOrder(List<ImportOrderParamDto> list) {
        //??????????????????
        if (CollectionUtils.isEmpty(list)){
            return;
        }

        UserStoreInfoVo storeInfo = UserUtil.getStoreInfo();

        List<OrderInfo> orderInfos = new ArrayList<>();
        List<OrderAddress> orderAddresses = new ArrayList<>();
        List<OrderDispatch> dispatches = new ArrayList<>();
        List<OrderStateExtend> stateExtends = new ArrayList<>();
        List<ThirdCreateOrderDto> createOrderDtos = new ArrayList<>();

        //????????????
        List<Integer> nullRows = new ArrayList<>();
        List<Integer> lengthRows = new ArrayList<>();
        List<Integer> timeRows = new ArrayList<>();
        List<Integer> mobileRows = new ArrayList<>();

        //????????????
        for (int x = 0; x < list.size(); x++) {
            ImportOrderParamDto i = list.get(x);

            //??????
            int count = x+2;
            //??????????????????
            if (StringUtils.isBlank(i.getDispatchFlag()) || StringUtils.isBlank(i.getCustomerName()) || StringUtils.isBlank(i.getCustomerPhone())
                    || StringUtils.isBlank(i.getAppointTimeBegin()) || StringUtils.isBlank(i.getAppointTimeEnd()) || StringUtils.isBlank(i.getServiceCode())
                    || StringUtils.isBlank(i.getOrderPrice())
                    || StringUtils.isBlank(i.getProvinceName()) || StringUtils.isBlank(i.getCityName()) || StringUtils.isBlank(i.getAreaName())
                    || StringUtils.isBlank(i.getDetailInfo())) {
                nullRows.add(count);
                continue;
            }
            //???????????? ???????????????????????????
            if (i.getCustomerName().length() > 20 || i.getCustomerPhone().length() > 20
                    || i.getProvinceName().length() > 128 || i.getCityName().length() > 128
                    || i.getAreaName().length() > 128 || i.getDetailInfo().length() > 250
                    || (StringUtils.isNotBlank(i.getOrderContent()) && i.getOrderContent().length() > 1000)
                    || (StringUtils.isNotBlank(i.getUpperOrderNum()) && i.getUpperOrderNum().length() > 64)) {
                lengthRows.add(count);
                continue;
            }
            //??????????????????
            if (!i.getCustomerPhone().matches(BaseValueConstant.PHONE_CHECK)){
                mobileRows.add(count);
                continue;
            }

            //????????????
            Date userApplyTimeBegin = null;
            Date userApplyTimeEnd = null;
            try {
                userApplyTimeBegin = dataChange(i.getAppointTimeBegin());
                userApplyTimeEnd = dataChange(i.getAppointTimeEnd());
            }catch (Exception e){
                timeRows.add(count);
                continue;
            }
            if (Objects.isNull(userApplyTimeBegin) || Objects.isNull(userApplyTimeEnd)){
                timeRows.add(count);
                continue;
            }
            //?????????????????????????????????
            String[] serviceStr = i.getServiceCode().split(BaseValueConstant.CODE_SPLIT);
            if (serviceStr.length < BaseValueConstant.TWO){
                lengthRows.add(count);
                continue;
            }

            //???????????????
            String orderNum = OrderNumUtil.getOrderNum(OrderGroupEnum.FROM_PLATFORM.value());

            OrderInfo info = new OrderInfo();
            switch (i.getDispatchFlag()) {
                case "????????????":
                    info.setDispatchFlag(DispatchFlagEnum.IS_SELF.value());
                    break;
                case "????????????":
                    info.setDispatchFlag(DispatchFlagEnum.IS_AUTO.value());
                    break;
                case "?????????":
                    info.setDispatchFlag(DispatchFlagEnum.IS_ROB.value());
                    break;
            }
            info.setTenantId(storeInfo.getTenantId());
            info.setOrderNum(orderNum);
            info.setOrderGroup(OrderGroupEnum.FROM_PLATFORM.value());
            info.setStoreNum(storeInfo.getStoreNum());
            info.setStoreName(storeInfo.getStoreName());
            info.setUpperOrderNum(i.getUpperOrderNum());
            info.setCustomerName(i.getCustomerName());
            info.setCustomerPhone(i.getCustomerPhone());
            info.setServiceCode(serviceStr[0]);
            info.setServiceContent(serviceStr[1]);
            info.setServiceNum(i.getServiceNum()==null? 1:Integer.parseInt(i.getServiceNum()));
            info.setOrderContent(i.getOrderContent()==null? BaseValueConstant.NULL:i.getOrderContent());
            info.setAppointTimeBegin(userApplyTimeBegin);
            info.setAppointTimeEnd(userApplyTimeEnd);
            info.setOrderPrice(new BigDecimal(i.getOrderPrice()));
            info.setOrderState(OrderStateEnum.WAIT_DISTRIBUTE.value());
            info.setExceptionState(ExceptionStateEnum.NORMAL.value());
            info.setDeleteFlag(DelFlagEnum.UN_DELETE.value());
            info.setCreateId(storeInfo.getLoginId());
            info.setCreateTime(new Date());
            info.setUpdateId(storeInfo.getLoginId());
            info.setUpdateTime(new Date());

            //????????????
            OrderAddress address = new OrderAddress();
            address.setOrderNum(orderNum);
            address.setProvinceName(i.getProvinceName());
            address.setCityName(i.getCityName());
            address.setAreaName(i.getAreaName());
            address.setDetailInfo(i.getDetailInfo());

            //?????????
            OrderDispatch dispatch = new OrderDispatch();
            dispatch.setOrderNum(orderNum);
            dispatch.setPlatformServiceCode(info.getServiceCode());//??????????????????
            dispatch.setCreateId(storeInfo.getLoginId());
            dispatch.setCreateTime(new Date());
            dispatch.setUpdateId(storeInfo.getLoginId());
            dispatch.setUpdateTime(new Date());


            // ???????????????????????????
            if (info.getDispatchFlag().equals(DispatchFlagEnum.IS_AUTO.value())) {
                RedeployConfDto redeployConfDto = new RedeployConfDto();
                redeployConfDto.setFormStoreNum(storeInfo.getStoreNum());
                redeployConfDto.setStoreItemNum(info.getServiceCode());
                redeployConfDto.setCityName(i.getCityName());
                List<RedeployConfVo> vos = iStoreInfoRelationClient.getRedeployConf(redeployConfDto);

                //???????????????
                if (!CollectionUtils.isEmpty(vos)){
                    RedeployConfVo vo = vos.get(0);

                    //??????????????????
                    OrderStateExtend stateExtend = new OrderStateExtend();
                    stateExtend.setOrderNum(orderNum);
                    stateExtend.setStateType(OrderStateExtendEnum.IS_DISPATCH.value());
                    stateExtend.setStateValue(BaseValueConstant.ONE);
                    stateExtend.setCreateTime(new Date());
                    stateExtend.setUpdateTime(new Date());
                    stateExtends.add(stateExtend);

                    dispatch.setDispatchMode(DispatchModeEnum.IS_DISPATCH.value());
                    dispatch.setDispatchType(DispatchTypeEnum.STORE.value());
                    dispatch.setStoreId(vo.getStoreId());
                    dispatch.setStoreNum(vo.getToStoreNum());
                    dispatch.setStoreName(vo.getToStoreName());
                    dispatch.setDownServiceCode(vo.getStoreItemNum());
                    dispatch.setStoreDispatchTime(new Date());

                    //???????????????????????????????????????
                    //????????? ????????? ????????? ?????????
                    if (vo.getToCallMode().equals(CallModeEnum.TO_STORE.value())){
                        info.setOrderState(OrderStateEnum.WAIT_RECEIVE.value());
                        // ???????????????????????????
                        ThirdCreateOrderDto dto = new ThirdCreateOrderDto();
                        BeanUtils.copyProperties(info,dto);
                        dto.setServiceCode(vo.getStoreItemNum());
                        dto.setUserApplyTimeBegin(String.valueOf(userApplyTimeBegin));
                        dto.setUserApplyTimeEnd(String.valueOf(userApplyTimeEnd));
                        dto.setOrderNum(orderNum);

                        dto.setProvinceName(i.getProvinceName());
                        dto.setCityName(i.getCityName());
                        dto.setAreaName(i.getAreaName());
                        dto.setDetailInfo(i.getDetailInfo());
                        createOrderDtos.add(dto);
                    }
                }
            }

            orderInfos.add(info);
            orderAddresses.add(address);
            dispatches.add(dispatch);
        }

        //????????????
        if (!CollectionUtils.isEmpty(nullRows) || !CollectionUtils.isEmpty(lengthRows)
                || !CollectionUtils.isEmpty(timeRows)
                || !CollectionUtils.isEmpty(mobileRows)){
            StringBuilder sb = new StringBuilder();
            if (!CollectionUtils.isEmpty(nullRows)){
                String nullStr = ExceptionEnum.IMPORT_ARG_EMPTY_ERROR.message()+": ??????: "+nullRows.toString();
                sb.append(nullStr).append("\r\n");
            }
            if (!CollectionUtils.isEmpty(lengthRows)){
                String lengthStr = ExceptionEnum.IMPORT_ARG_LENGTH_ERROR.message()+": ??????: "+lengthRows.toString();
                sb.append(lengthStr).append("\r\n");
            }
            if (!CollectionUtils.isEmpty(timeRows)){
                String timeStr = ExceptionEnum.IMPORT_ARG_DATE_ERROR.message()+": ??????: "+timeRows.toString();
                sb.append(timeStr).append("\r\n");
            }
            if (!CollectionUtils.isEmpty(mobileRows)){
                String mobileStr = ExceptionEnum.MOBILE_MATCHES_ERROR.message()+": ??????: "+mobileRows.toString();
                sb.append(mobileStr).append("\r\n");
            }

            throw new XXException(ExceptionEnum.IMPORT_ARG_EMPTY_ERROR.value(),sb.toString());
        }

        //??????????????????
        ImportOrderDto dto = ImportOrderDto.builder()
                .orderInfos(orderInfos)
                .orderAddresses(orderAddresses)
                .dispatches(dispatches)
                .stateExtends(stateExtends)
                .build();
        int i = iOrderInfoClient.importOrder(dto);
        if (i <= 0){
            throw new XXException(ExceptionEnum.IMPORT_ORDER_FAILED);
        }

        //???????????????
        if (!CollectionUtils.isEmpty(createOrderDtos)){
          ThirdCallBackUtil.callDownCircle(createOrderDtos, OrderStateEnum.WAIT_DISTRIBUTE.value(), OrderStateEnum.WAIT_DISTRIBUTE.desc());
        }
    }


    private Date dataChange(String str) throws Exception {
        Assert.notNull(str,"param [str] is null");

        if (!str.contains("-") && !str.contains("/") && !str.contains("???")){
            return null;
        }

        SimpleDateFormat simpleDateFormat = null;
        if (str.contains("-")){
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        if (str.contains("/")){
            simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        }
        if (str.contains("???")){
            simpleDateFormat = new SimpleDateFormat("yyyy???MM???dd??? HH???mm???");
        }
        if (simpleDateFormat != null){
            return simpleDateFormat.parse(str);
        }
        return null;
    }
}