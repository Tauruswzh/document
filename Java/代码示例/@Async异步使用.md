异步方法


##### 1、启动类添加注解
@EnableAsync
```java
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync		 //开启异步
public class HealthFacadeInspectApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthFacadeInspectApplication.class, args);
    }
}
```

##### 2、方法使用注解
controller
Future类：
```java
public class test{
        @PostMapping(value = "/pricePreview")
        @ApiOperation(value="费用预览_价格详情")
        public InspectCasePriceDetailsVo pricePreview(
          @NotNull(message = "病历id不能为空") 
          @RequestParam("patientCaseHistoryId") Long patientCaseHistoryId) {
            //校验
            PatientCaseHistory history = inspectPatientCaseHistoryService.checkPatientCase(patientCaseHistoryId);
            if (history == null){
                throw new BusinessException(ResultCodeEnum.INSPECT_CASE_GET_NULL);
            }
    
            InspectCasePriceDetailsVo resultVo = new InspectCasePriceDetailsVo();
            //patient_case_his_cure
            Future<InspectCasePriceDetailsVo> cureResult = inspectPatientCaseHistoryService
              .getHisCureFuture(patientCaseHistoryId);
            InspectCasePriceDetailsVo cureDetails = getFutureData(cureResult);
            if (cureDetails != null){
                resultVo.setCaseCuretotalPrice(cureDetails.getCaseCuretotalPrice());
                resultVo.setCaseHisCures(cureDetails.getCaseHisCures());
                resultVo.setCaseHisCureCount(cureDetails.getCaseHisCureCount());
            }
    
            //patient_case_his_west
            Future<InspectCasePriceDetailsVo> westResult = inspectPatientCaseHistoryService
              .getHisWestFuture(patientCaseHistoryId);
            InspectCasePriceDetailsVo westDetails = getFutureData(westResult);
            if (westDetails != null){
                resultVo.setCaseHisWesttotalPrice(westDetails.getCaseHisWesttotalPrice());
                resultVo.setCaseHisWestCount(westDetails.getCaseHisWestCount());
                resultVo.setCaseHisWests(westDetails.getCaseHisWests());
            }
          
            return resultVo;
        }
}
```

serviceImpl
@Async注解：
```java
public class test{
    @Override
    @Async		//异步注解
    public Future<InspectCasePriceDetailsVo> getHisCureFuture(Long patientCaseHistoryId) {
        InspectCasePriceDetailsVo vo = new InspectCasePriceDetailsVo();
        //patient_case_his_cure
        //业务逻辑
        return new AsyncResult<>(vo);
    }
}
```

解析数据
```java
public class test{
    private InspectCasePriceDetailsVo getFutureData(Future<InspectCasePriceDetailsVo> futureResult) {
        InspectCasePriceDetailsVo vo;
        while (true) {		//1.一直循环等待，直到结束
            //2.判断是否结束
            if (futureResult.isDone()) {
                try {
                    vo = futureResult.get();
                } catch (Exception e) {
                    throw new BusinessException(ExceptionUtils.getStackTrace(e));
                }
                break;   //3.获取到值就立即跳出循环
            }
        }
        return vo;
    }
}
```

通用范型方法
```java
public class test{
 		//获取对象
    private <T> T getFutureData(Future<T> futureResult) {
        T vo;
        while (true) {
            //判断是否结束
            if (futureResult.isDone()) {
                try {
                    vo = futureResult.get();
                } catch (Exception e) {
                    throw new BusinessException(ExceptionUtils.getStackTrace(e));
                }
                break;
            }
        }
        return vo;
    }
}
```

好处：
逻辑是异步处理，不再同步处理比较耗时，可以多个一起并行处理，完成后进行数据组装统一返回。