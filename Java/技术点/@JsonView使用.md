@JsonView使用


1、JsonView使用
项目中同一个返回对象，不同的接口控制返回不同的字段 （类似@JsonIgnore，但是该注解不能任意控制）
使用步骤：
添加依赖：
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.8.0</version>
    <scope>compile</scope>
</dependency>
```

1.1 在返回实体中使用接口来声明多个视图
1.2 在返回对象字段上（或get方法）指定视图
```java
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;

@Data
public class UserTest implements Serializable {
  	//1.在返回实体中使用接口来声明多个视图
    //通过不同的接口控制给前端显示不同的数据
    public interface viewUserName{}
    //接口继承 （viewUserAge中能显示viewUserName限定的字段）
    public interface viewUserAge extends viewUserName {}

  	//2.在返回对象字段上（或get方法）指定视图
    @JsonView(viewUserName.class)
    private String userName;

    @JsonView(viewUserAge.class)
    private Integer userAge;
}
```


1.3 在controller方法上指定试图
```java
public class test{
    @JsonView(UserTest.viewUserName.class)
    @GetMapping("/userName")
    public UserTest userName() {
        UserTest userTest = new UserTest();
        userTest.setUserName("xiahao");
        userTest.setUserAge(12);
        return userTest;
    }

    @JsonView(UserTest.viewUserAge.class)
    @GetMapping("/userAge")
    public UserTest userAge() {
        UserTest userTest = new UserTest();
        userTest.setUserName("xiahao");
        userTest.setUserAge(12);
        return userTest;
    }
}
```


1.4 测试
```java
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoControllerTest {
//    JUnit4使用Java5中的注解（annotation），以下是JUnit4常用的几个annotation：
//    @Before：初始化方法   对于每一个测试方法都要执行一次（注意与BeforeClass区别，后者是对于所有方法执行一次）
//    @After：释放资源  对于每一个测试方法都要执行一次（注意与AfterClass区别，后者是对于所有方法执行一次）
//    @Test：测试方法，在这里可以测试期望异常和超时时间
//    @Test(expected=ArithmeticException.class)检查被测方法是否抛出ArithmeticException异常
//    @Ignore：忽略的测试方法
//    @BeforeClass：针对所有测试，只执行一次，且必须为static void
//    @AfterClass：针对所有测试，只执行一次，且必须为static void
//    一个JUnit4的单元测试用例执行顺序为：
//    @BeforeClass -> @Before -> @Test -> @After -> @AfterClass;
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
    public void userName() throws Exception {
        String result = mockMvc.perform(
                //get请求 地址
                get("/test/userName").contentType(MediaType.APPLICATION_JSON_UTF8))
                 //期望值
                .andExpect(status().isOk())
                 //返回
                .andReturn().getResponse().getContentAsString();

        System.out.println("userName  " +result);
    }
  	//返回： userName  {"userName":"xiahao"}

    @Test
    public void userAge() throws Exception {
        String result = mockMvc.perform(
                //get请求 地址
                get("/test/userAge").contentType(MediaType.APPLICATION_JSON_UTF8))
                //期望值
                .andExpect(status().isOk())
                //返回
                .andReturn().getResponse().getContentAsString();

        System.out.println("userAge  " +result);
    }
  	//返回：userAge  {"userName":"xiahao","userAge":12}
}
```