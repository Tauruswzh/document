Gson 转对象 linkedTreeMap异常


```java
public class test{
        Gson gson = new  Gson();

       /**
       * 方法名:  jsonToBeanWithType
       * 描述: 解决LinkedTreeMap
       * 参数: Type type = new TypeToken<OrderMQMegDto<CompanyExamineMqMsgDto>>() {}.getType();
       */
       public static <T> T jsonToBeanWithType(String str, Type type) {
         T t = null;
         if (gson != null) {
             t = gson.fromJson(str, type);
         }
         return t;
       } 

    public static void main(String[] args){
        OrderMQMegDto<CompanyExamineMqMsgDto> dto = new OrderMQMegDto<>();
        dto.setMsgType(2);
        dto.setCreateTime(new Date());
        dto.setPrimaryKey("TestC12012030932000001");

        CompanyExamineMqMsgDto cdto = new CompanyExamineMqMsgDto();
        cdto.setOrderNum("TestC12012030932000001");
        cdto.setCompanyRemark("不给");
        cdto.setAuditFlag(3);
        cdto.setExamineId(1026L);
        cdto.setExamineTime(new Date());
        dto.setData(cdto);


        String objJson = GsonUtil.beanToJson(dto);
        System.out.println(objJson);

        Type type = new TypeToken<OrderMQMegDto<CompanyExamineMqMsgDto>>() {}.getType();
        OrderMQMegDto<CompanyExamineMqMsgDto> dto2 = GsonUtil.jsonToBeanWithType(objJson, type);
        System.out.println(">>>>>>>>>"+dto2);

        CompanyExamineMqMsgDto data = dto2.getData();
        System.out.println(data);
    }
}
```