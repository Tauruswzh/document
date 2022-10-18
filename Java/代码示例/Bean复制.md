Bean复制

1.最快
Bean之间直接get/set

2.底层字节码，get/set
source:源
target:目标
useConverter:自定义转换

BeanCopier copier = BeanCopier.create(source.class, target.class, false);
copier.copy(Object target, Object resource, null);

[源码](https://blog.csdn.net/loushuiyifan/article/details/82461955)

3.较慢
BeanUtils.copyProperties(Object source, Object target);