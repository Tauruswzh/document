使用Filter和Interceptor拦截REST服务


##### 1、过滤器
实现：javax.servlet.Filter
```java
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Date;

//@Component
public class TimeFilter implements Filter {

  //销毁
	@Override
	public void destroy() {
		System.out.println("time filter destroy");
	}

  //过滤
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("time filter start");
		long start = new Date().getTime();
		//向下执行过滤器链
		chain.doFilter(request, response);
		System.out.println("time filter 耗时:"+ (new Date().getTime() - start));
		System.out.println("time filter finish");
	}

  //初始化
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		System.out.println("time filter init");
	}
}
```

过滤器起作用：
1: 直接在过滤器上添加注解 ：@Component
2: 使用web.xml配置 【springboot中使用配置类】「第三方过滤器」

```java
import com.examples.common.filter.TimeFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig {
	
	@Bean
	public FilterRegistrationBean timeFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		
		TimeFilter timeFilter = new TimeFilter();
		registrationBean.setFilter(timeFilter);

		//拦截路径
		List<String> urls = new ArrayList<>();
		urls.add("/*");
		registrationBean.setUrlPatterns(urls);
		
		return registrationBean;
	}
}
```



##### 2、拦截器
相比过滤器，拦截器是SpringMVC自己的，并且能够在拦截中获取到处理器信息方便处理，而过滤器只能过滤地址并不知道处理器只能获取请求的信息。
实现：org.springframework.web.servlet.HandlerInterceptor

```java
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component //必须
public class TimeInterceptor implements HandlerInterceptor {

	//方法处理前 true进入方法 false直接退出不进方法
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler) throws Exception {
		System.out.println("preHandle");
		//获取类名
		System.out.println(((HandlerMethod)handler).getBean().getClass().getName());
		//获取方法名
		System.out.println(((HandlerMethod)handler).getMethod().getName());
		
		request.setAttribute("startTime", new Date().getTime());
		return true;
	}

	//方法处理后，并且方法没有异常才进入
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                         Object handler,ModelAndView modelAndView) throws Exception {
		System.out.println("postHandle");
		Long start = (Long) request.getAttribute("startTime");
		System.out.println("time interceptor 耗时:"+ (new Date().getTime() - start));
	}

	//方法处理后，无论方法有误异常都进入 【注：这里的异常如果有全局异常处理器，则这里获取不到异常信息，被全局捕获】
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex)
			throws Exception {
		System.out.println("afterCompletion");
		Long start = (Long) request.getAttribute("startTime");
		System.out.println("time interceptor 耗时:"+ (new Date().getTime() - start));
		System.out.println("ex is "+ex);
	}
}
```

拦截器起作用：
添加配置类
```java
import com.examples.common.interceptor.TimeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	private TimeInterceptor timeInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
    //排除路径
		registry.addInterceptor(timeInterceptor).excludePathPatterns("/test/create3");
    //包含路径
    registry.addInterceptor(timeInterceptor).addPathPatterns("/test/create3");
    //混合
 		registry.addInterceptor(timeInterceptor).excludePathPatterns("/test/userAge")
    	.addPathPatterns("/test/create3");
	}
}
```

注：拦截器会拦截所有的控制器(controller)中的方法调用，不光自己写的，Spring提供的controller方法也会被拦截。