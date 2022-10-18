单例多例


###1.scope
spring中bean的scope属性，有如下5种类型：
singleton       表示在spring容器中的单例，通过spring容器获得该bean时总是返回唯一的实例
prototype       表示每次获得bean都会生成一个新的对象
request         表示在一次http请求内有效（只适用于web应用）
session         表示在一个用户会话内有效（只适用于web应用）
globalSession   表示在全局会话内有效（只适用于web应用）

在多数情况，我们只会使用singleton和prototype两种scope
[如果在spring配置文件内未指定scope属性，默认为singleton]


###2.单例的原因
1、为了性能。
2、不需要多例。

1、单例不用每次都new，当然快了。
2、不需要实例会让很多人迷惑，因为spring mvc官方也没明确说不可以多例。
不需要的原因是看开发者怎么用了，如果你给controller中定义很多的属性，那么单例肯定会出现竞争访问了。

```java
@Controller
@RequestMapping("/demo/lsh/ch5")
@Scope("prototype")
public class MultViewController {
    private static int st = 0;      //静态的
    private int index = 0;    //非静态
   
    @RequestMapping("/test")
    public String test() {
        System.out.println(st++ + " | " + index++);
        return "/lsh/ch5/test";
    }
}
```
单例的：
0 | 0
1 | 1
2 | 2
3 | 3
4 | 4

改为多例的：
0 | 0
1 | 0
2 | 0
3 | 0
4 | 0

[最佳实践：定义一个非静态成员变量时候，则通过注解@Scope("prototype")，将其设置为多例模式]