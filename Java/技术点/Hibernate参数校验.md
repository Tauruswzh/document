参数校验


####1、路径参数校验
{参数名:正则表达式}
```java
public class test{
    //带正则校验 必须是数字
    @GetMapping("/hello/{id:\\d+}")		//值必穿
    public String hello(@PathVariable(required = true) Integer id) {
        return "hello spring security";
    }
}
```


####2、日期类型
前端直接传递 **时间戳**
后端相应返回 **时间戳** 具体需要显示何种格式 由前端直接解析

实体：
```text
private Date birthDay;
```

接口：
```java
public class test{
    @JsonView(UserTest.viewUserAge.class)
    @PostMapping("/userAge")
    public UserTest create(@RequestBody UserTest test) {
        System.out.println("姓名："+test.getUserName());
        System.out.println("年龄："+test.getUserAge());
        System.out.println("生日："+test.getBirthDay());

        return test;
    }
}
```

测试
```java
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoControllerTest {
//    @Before：初始化方法   对于每一个测试方法都要执行一次（注意与BeforeClass区别，后者是对于所有方法执行一次）
//    每一个测试方法的调用顺序为：
//    @Before -> @Test -> @After;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void create() throws Exception {
        Date date = new Date();
        System.out.println("当前时间戳："+date.getTime());
        //json字符串
        String context = "{\"userName\":\"xiahao\",\"userAge\":12,\"birthDay\":"+date.getTime()+"}";
        String result = mockMvc.perform(
                //get请求 地址
                post("/test/userAge")
                //post传递对象【json字符串】
                .content(context)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //期望值
                .andExpect(status().isOk())
                //返回
                .andReturn().getResponse().getContentAsString();

        System.out.println("对象  " +result);
    }
}
```

返回：
```text
当前时间戳：1609520933346
姓名：xiahao
年龄：12
生日：Sat Jan 02 01:08:53 CST 2021
对象  {"userName":"xiahao","userAge":12,"birthDay":1609520933346}
```



####3、@Valid校验
添加依赖
```xml
<dependency>
   <groupId>javax.validation</groupId>
   <artifactId>validation-api</artifactId>
   <version>2.0.1.Final</version>
</dependency>
<dependency>
   <groupId>org.hibernate.validator</groupId>
   <artifactId>hibernate-validator</artifactId>
   <version>6.0.7.Final</version>
</dependency>
```


#####3.1 对象校验
3.1.1 传输实体添加注解 @NotBlank
```text
    @NotBlank(message = "userName 不能为空")
    private String userName;
```

3.1.2 controller方法添加注解 @Valid
```java
public class test{
    @PostMapping("/create")
    public UserTest create(@RequestBody @Valid UserTest test) {
        return test;
    }
}
```


#####3.2 参数校验
3.2.1 方法参数添加注解 @NotBlank
```java
public class test{
    @PostMapping("/create2")
    public UserTest create2(@NotBlank(message = "userName 不能为空") 
                            @RequestParam(value = "userName") String userName) {
        UserTest test = new UserTest();
        test.setUserName(userName);
        return test;
    }
}
```

3.2.2 controller类上添加注解  @Validated
```java
@RestController
@RequestMapping("/test")
@Validated
public class DemoController {}
```



#####3.3 校验List对象
3.3.1 list对象中的字段添加注解 @NotBlank
```java
public class CompanyInspectPriceDto implements Serializable {
    @NotNull(message = "price不能为空")
    @DecimalMin(value = "0.0")
    private BigDecimal price;
}
```

3.3.2 参数对象添加注解 @Valid @NotEmpty
```java
public class PlatformCompanyPriceAddDto implements Serializable {
    @Valid
    @NotEmpty(message = "companyInspectPriceDtos不能为空")
    private List<CompanyInspectPriceDto> companyInspectPriceDtos;
}
```

3.3.3 controller方法添加注解 @Valid
```java
public class test{
    @ApiOperation(value = "增加_批量")
    @PostMapping(value = "/add")
    public void addPlatformCompanyPrice(@Valid @RequestBody PlatformCompanyPriceAddDto dto) {
        platformCompanyPriceService.addPlatformCompanyPrice(dto);
    }
}
```



#####注：常用注解
| 代码                                                            | 说明                                        |
| -------------------------------------------------------------- | ------------------------------------------- |
| @NotBlack                                                      | 被注解的字符串必须不为null不为空            |
| @NotNull                                                       | 被注解的元素必须不为null                    |
| @NotEmpty                                                      | 被注解的集合必须不为null不为空              |
| @AssertFalse / @AssertTrue                                     | 被注解的元素必须为false / true              |
| @Min(value = 1, message = "最小值为1")                           | 被注解的元素必须为数字，其值最小1           |
| @Max(value = 88, message = "最大值为88")                          | 被注解的元素必须为数字，其值最大88          |
| @Size(min = 0,max = 30,message="xxx")                           | 被注解的元素的个数必须在0-30个 [String类型] |
| @DecimalMin(value = "0.1")                                      | 被注解的元素必须为Bigdecimal，其值最小0.1   |
| @DecimalMax(value = "10000.00")                                 | 被注解的元素必须为Bigdecimal，其值最大10000 |
| @Pattern(regexp = "[0123456789]",message = "hava a error Date") | 被注解的元素必须符合指定的正则表达式        |



获取错误信息
使用参数 BindingResult + @Validated

```java
public class test{
    @PostMapping("/create2")
    public UserTest create2(@NotBlank(message = "userName 不能为空") @RequestParam String userName, 
                            BindingResult errors) {
        if (errors.hasErrors()){
            errors.getAllErrors().stream().forEach(error ->
                    {
                        FieldError fieldError = (FieldError) error;
                        String field = fieldError.getField();
                        System.out.println(field+" : "+error.getDefaultMessage());
                    });
        }
        UserTest test = new UserTest();
        test.setUserName(userName);
        return test;
    }
}
```



#### 4、自定义校验注解
4.1 定义注解
```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})		//注解使用的范围：方法，字段
@Retention(RetentionPolicy.RUNTIME)						//运行时
@Constraint(validatedBy = MyConstraintValidator.class)	//自定义验证类型的注解  validatedBy：处理的类
public @interface MyConstraint {
	String message();
	Class<?>[] groups() default { };						//必须
	Class<? extends Payload>[] payload() default { };		//必须

}
```

4.2 定义注解的处理类
不需要添加 【配置类注解】
```java
public class MyConstraintValidator implements ConstraintValidator<MyConstraint, Object> {

    //1.可以注入类
	@Autowired
	private HelloService helloService; 		
	
    //初始化
	@Override
	public void initialize(MyConstraint constraintAnnotation) {
		System.out.println("my validator init");		
	}

    //2.true 校验通过 false 失败
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		helloService.greeting("tom");
		System.out.println(value);
		return true;											
	}

}
```

4.3 使用
```text
	@MyConstraint(message = "这是一个测试")
	private String username;
```

