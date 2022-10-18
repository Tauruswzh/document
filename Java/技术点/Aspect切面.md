Aspect切面

AOP为Aspect Oriented Programming的缩写
Spring AOP构建在动态代理基础之上，因此，**Spring对AOP的支持局限于方法拦截**。

意为：
面向切面编程，通过预编译方式和运行期动态代理实现程序功能的统一维护的一种技术.
AOP是OOP的延续，是软件开发中的一个热点，也是Spring框架中的一个重要内容，是函数式编程的一种衍生范型。
利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。

> AOP：Aspect Oriented Programming（面向切面编程），OOP是面向对象编程，AOP是在OOP基础之上的一种更高级的设计思想。
> OOP和AOP之间也存在一些区别，OOP侧重于对象的提取和封装。----封装对象
> AOP侧重于方面组件，方面组件可以理解成封装了通用功能的组件，方面组件可以通过配置方式，灵活地切入到某一批目标对象方法上。----封装功能

在spring AOP中业务逻辑仅仅只关注业务本身，将日志记录，性能统计，安全控制，事务处理，异常处理等代码从业务逻辑代码中划分出来，
通过对这些行为的分离，我们希望可以将它们独立到非指导业务逻辑的方法中，进而改变这些行为的时候不影响业务逻辑的代码。

AOP实现的关键，在于AOP框架自动创建的AOP代理，AOP代理主要分为**静态代理**和**动态代理**，
静态代理的代表为AspectJ；
而动态代理则以Spring AOP为代表。



#####静态代理：AspectJ
AspectJ是什么？
> Eclipse AspectJ is a seamless aspect-oriented extension to the Java™ programming language. 
>It is Java platform compatible easy to learn and use.

AspectJ是Java的扩展，用于实现面向切面编程。有自己的编辑器ajc。
所谓的静态代理: 就是AOP框架会在编译阶段生成AOP代理类，因此也称为编译时增强。
它会在编译阶段将Aspect织入Java字节码中，运行的时候就是经过增强之后的AOP对象。
proceed方法就是回调执行被代理类中的方法。



#####动态代理：Spring AOP
1.与AspectJ的静态代理不同，Spring AOP使用的是动态代理。
所谓的动态代理: 就是说AOP框架不会去修改字节码，而是在内存中临时为方法生成一个AOP对象，
这个AOP对象包含了目标对象的全部方法，并且在特定的切点做了[增强处理]回调原对象的方法。[织入]


2.Spring AOP中的动态代理，主要有两种方式：
>JDK动态代理
>CGLIB动态代理

JDK动态代理通过[反射]来接收被代理的类，并且[要求被代理的类必须实现一个接口]。
JDK动态代理的核心是InvocationHandler接口和Proxy类。[如果目标类没有实现接口，那么Spring AOP会选择使用CGLIB来动态代理目标类]


3.CGLIB（Code Generation Library），是一个代码生成的类库，可以在运行时动态地[生成目标类的子类]
注意，CGLIB是通过[继承]的方式做的动态代理，因此如果某个类被标记为final，那么它是无法使用CGLIB做动态代理的。

[Spring AOP默认是使用JDK动态代理，如果代理的类没有接口则会使用CGLib代理]

那么JDK代理和CGLib代理我们该用哪个呢？
- 如果是**单例的我们最好使用CGLib代理**，如果是多例的我们最好使用JDK代理

原因：
- JDK在创建代理对象时的性能要高于CGLib代理，而生成代理对象的运行性能却比CGLib的低。
- 如果是单例的代理，推荐使用CGLib


[使用动态代理实质上就是: 调用时拦截对象方法，对方法进行改造、增强！不能改变方法的元素参数，返回值类型，方法名等
AOP代理类在切点动态地 织入 了增强处理]


注解：
>@Aspect:作用是把当前类标识为一个切面供容器读取
>@Pointcut：Pointcut是植入Advice的触发条件。每个Pointcut的定义包括2部分，
>一是表达式，二是方法签名。
>方法签名必须是 public及void型。可以将Pointcut中的方法看作是一个被Advice引用的助记符，因为表达式不直观，因此我们可以通过方法签名的方式为 此表达式命名。
>因此Pointcut中的方法只需要方法签名，而不需要在方法体内编写实际代码。
>
>@Around：环绕增强，相当于MethodInterceptor
>@AfterReturning：后置增强，相当于AfterReturningAdvice，方法正常退出时执行
>@Before：标识一个前置增强方法，相当于BeforeAdvice的功能，相似功能的还有
>@AfterThrowing：异常抛出增强，相当于ThrowsAdvice
>@After: final增强，不管是抛出异常或者正常退出都会执行


[@Around 能够修改被切方法的返回结果，但是不能更改被切方法的返回值类型]
白话：切面增强 与 被切方法 必须返回值类型一致或是其子类，否则会包`ClassCastException`


使用：
1: 添加切面注解
```java
//切面注解
@Aspect  
@Component
public class ResultAspect {
    //...
}
```

2: 切点 @Pointcut：
1: 指定切面方法
```java
@Aspect 
@Component
public class ResultAspect {
    @Pointcut("execution(public * com.rest.module..*.*(..))")
    public void getMethods() {
    }
}
```
注：execution表达式第一个*表示匹配任意的方法返回值，
..(两个点)表示零个或多个，
第一个..表示module包及其子包,
第二个*表示所有类, 
第三个*表示所有方法，
第二个..表示方法的任意参数个数


2: 指定注解
```java
@Aspect 
@Component
public class ResultAspect {
    @Pointcut("@annotation(com.rest.utils.SysPlatLog)")
    public void getMethods() {
    }
}
```
注：在这里，自定义了一个注解类，方法加注解即可使用


3: 通知增强方法：
@Before：标识一个前置增强方法
```java
@Aspect 
@Component
public class ResultAspect {
    @Pointcut("@annotation(com.rest.utils.SysPlatLog)")
    public void getMethods() {
    }

    @Before(value = "getMethods()")
    public void permissionCheck(JoinPoint point) {
      MethodSignature signature = (MethodSignature) point.getSignature();  //拦截的方法
     	Class declaringType = signature.getDeclaringType();//方法返回类型
    	Method method = signature.getMethod();//方法
      String methodName = method.getName();		//方法名
      
      List<Object> args = Arrays.asList(point.getArgs());//参数
      Class<?> aClass= point.getTarget().getClass();//拦截的实体类
      aClass.getAnnotation(xx.class);	//类上的注解
      method.getAnnotation(xx.class);	//方法上的注解  
    }
}
```

@Around：环绕增强
```java
@Aspect 
@Component
public class ResultAspect {
    @Pointcut("@annotation(com.rest.utils.SysPlatLog)")
    public void getMethods() {
    }

    @Around(value = "getMethods()")
     public Object around(ProceedingJoinPoint point) throws Throwable {
       Object[] args = point.getArgs();//参数
       Object object = point.proceed(args);//方法执行返回结果
       Object proceed = point.proceed();//方法返回结果
       
       MethodSignature signature = (MethodSignature) point.getSignature();  //拦截的方法
     	 Class declaringType = signature.getDeclaringType();//方法返回类型
    	 Method method = signature.getMethod();//方法
       String methodName = method.getName();		//方法名
       
       Class<?> aClass= point.getTarget().getClass();				//拦截的实体类
       aClass.getAnnotation(xx.class);	//类上的注解
       method.getAnnotation(xx.class);	//方法上的注解  
     }
}
```

@AfterReturning：后置增强
```java
@Aspect 
@Component
public class ResultAspect {
    @Pointcut("@annotation(com.rest.utils.SysPlatLog)")
    public void getMethods() {
    }

    //returning="参数名" + 指定参数 = 获取方法返回结果
     @AfterReturning(value = "getMethods()",returning="returnValue")
     public void log(JoinPoint point, Object returnValue) {
        //...
    }
}
```

@AfterThrowing：异常抛出增强
```java
@Aspect 
@Component
public class ResultAspect {
    @Pointcut("@annotation(com.rest.utils.SysPlatLog)")
    public void getMethods() {
    }

    @AfterThrowing(value = "getMethods()",throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        //...
    }
}
```

@After: final增强，不管是抛出异常或者正常退出都会执行
```java
@Aspect 
@Component
public class ResultAspect {
    @Pointcut("@annotation(com.rest.utils.SysPlatLog)")
    public void getMethods() {
    }

     @After(value = "getMethods()")
     public void releaseResource(JoinPoint point) {
        //...
     }
}
```