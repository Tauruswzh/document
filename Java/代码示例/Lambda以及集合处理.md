lambda以及集合处理


###1.集合处理
分割集合
size: 分割完集合数量
ListUtils.partition(collection,size);

###2.lambda处理
1、遍历循环
```text
List<CompanyInspectStock> stocks = iCompanyInspectStockClient.selectList(inspectStock);
if (!CollectionUtils.isEmpty(stocks)){
    stocks.forEach(i->{
        //逻辑操作
      return;	//跳出循环
    });
}
```

2、过滤
```text
List<PatientCaseInspectHistory> waitChecks = inspectHistories.stream()
  .filter(x -> x.getState().equals(CaseInspectHistoryStateEnum.WAIT_CHECK.code()))
  .collect(Collectors.toList());

```

3、取值,去重
```text
List<Long> patientIds = pageInfo.getList().stream()
              .map(PatientCaseReport::getPatientId)
              .distinct().collect(Collectors.toList());
```

4、将list中的Bigdecimal数值相加
```text
discountPostPrice = discounts.stream()
                    .filter(x -> x.getDiscountType().equals(OrderPriceDiscountType.CHANGE_POSTPRICE.value()))
                    .map(OrderPriceDiscount::getDiscountPrice).reduce(BigDecimal.ZERO,BigDecimal::add);
```

5、获取单值
```text
Optional<HospitalInfo> first = hospitalInfos.stream()
                          .filter(x -> x.getId().equals(i.getHospitalId())).findFirst();
//赋值
first.ifPresent(hospitalInfo -> resVo.setHospitalName(hospitalInfo.getHospitalName()));
```

```text
 Optional<String> first = stores.stream().filter(store -> store.getId().equals(vo.getStoreId()))
   .map(StoreInfo::getStoreName).findFirst();
  if (first.isPresent()){
      vo.setStoreName(first.get());
  }
```

6、两List保留重复数据
```text
Set<Long> storeByCtiyIds = OrderOtherModelCommon.getStoreByCtiyId(orderAddress.getCityId());
Set<Long> storeByCateIds = OrderOtherModelCommon.getStoreByCateId(companyOrderInfolVo.getCateIdTwo());

//保留重复数据 [A.retainAll(B) 将A中包含B的数据保留]
storeByCateIds.retainAll(storeByCtiyIds);
```

7、排序

正序
```text
list=list.stream().sorted(Comparator.comparing(VipCardVo::getVipCardType)).collect(Collectors.toList());
```

倒序
```text
list=list.stream().sorted(Comparator.comparing(VipCardVo::getVipCardType).reversed())
  .collect(Collectors.toList());
```

```text
//排序 降序
list.sort((a, b) -> b.getSendTime().compareTo(a.getSendTime()));
```

升序
```text
List<EngCouponListVo> collect = list.parallelStream().sorted(Comparator.comparing(EngCouponListVo::getIsShow)).collect(Collectors.toList());
```

最大
```text
Optional<PatientCaseInspectHistory> first = alreadyDiagnosis.stream()
  .max(Comparator.comparing(PatientCaseInspectHistory::getDiagnosisTime));

```

综合
```text
//最近诊断时间 诊断时间倒序
List<PatientCaseInspectHistory> alreadyDiagnosis = inspectHistories.stream()
                            
  .filter(x -> x.getState().equals(CaseInspectHistoryStateEnum.ALREADY_DIAGNOSIS.code()))
                            
  .sorted(Comparator.comparing(PatientCaseInspectHistory::getDiagnosisTime).reversed())
                            
  .collect(Collectors.toList());
```



8、过滤并转换对象
```text
List<A> collect = new ArrayList<>();

List<B> injectionVos = collect.stream()
  .map(data -> {
    B b = new B();
    BeanCopierUtil.copy(data, b);
    return b;
}).collect(Collectors.toList());
```

9、enum转map
```text
Arrays.stream(CaseInspectHistoryStateEnum.values())
  .collect(Collectors.toMap(CaseInspectHistoryStateEnum::code, CaseInspectHistoryStateEnum::message));
```

10、判断
```text
public static Boolean isEngineerRole() {
        Set<SysRole> userRoles = getCurrentUserRoles();
        return userRoles.stream().anyMatch(x -> UserRoleTypeEnum.FIVE.value() == x.getRoleType());
    }
```

11、分组
```text
Map<String, List<Person>> personGroupMap = personList.stream().collect(Collectors.groupingBy(Person::getName));
        System.out.println("personGroupMap:" + personGroupMap);
```