断言Assert


###包
import org.springframework.util.Assert;

###使用
判断对象
Assert.notNull(s1, "当 s1 为空已抛出异常");
Assert.isNull(s1, "当 s1 不为空抛出异常");

判断boolean
Assert.isTrue(s1, "当 s1 不为 true 抛出异常"); 

判断集合,数组,map
Assert.notEmpty(s1, "当 s1 集合未包含元素时抛出异常"); 

判断字符串
Assert.hasLength(s1, "当 s1 为 null 或长度为 0 时抛出异常");
Assert.hasText(s1, "当 s1 不能为 null 且必须至少包含一个非空格的字符，否则抛出异常");

判断类
Assert.isInstanceOf(Class type, Object obj, "如果 obj 不能被正确造型为 clazz 指定的类将抛出异常");


> Assert.notNull：   只有输入空null，才会报异常；输入空字符""、空格" "不报异常；
> Assert.hasLength： 如果输入空null、空字符""报异常；输入空格" "不报异常；
> Assert.hasText：   只有输入空null、空字符""、空格" "都会报异常；(不把空格算作内容)

###示例
String s1 = null;   //hasText,notNull,hasLength均报异常
String s1 = "";     //hasText报异常,notNull不报异常,hasLength报异常
String s1 = " ";    //一个空格，hasText报异常,notNull不报异常,hasLength不报异常
String s1 = "   ";  //三个空格，hasText报异常,notNull不报异常,hasLength不报异常

Assert.isInstanceOf(String.class,s1);   //不报异常
Assert.isInstanceOf(Object.class,s1);   //不报异常
Assert.isInstanceOf(Integer.class,s1);  //报异常

Assert.isTrue(1 < 2, "1<2需要为true");     //不报异常
Assert.isTrue(1 > 2, "1<2需要为true");     //报异常