日期类 Calender


首先创建一个Calendar类的实例对象，Calendar类属于java.util包
Calendar calendar = Calendar.getInstance();

获取当前年份、月份和日期等
```shell script
// 获取当前年
int year = calendar.get(Calendar.YEAR);  
// 获取当前月
int month = calendar.get(Calendar.MONTH) + 1;  
// 获取当前日
int day = calendar.get(Calendar.DATE);  
// 获取当前小时
int hour = calendar.get(Calendar.HOUR_OF_DAY);  
// 获取当前分钟
int minute = calendar.get(Calendar.MINUTE);  
// 获取当前秒
int second = calendar.get(Calendar.SECOND);  
// 获取当前是本周第几天
int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);  
// 获取当前是本月第几天
int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);  
// 获取当前是本年第几天
int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);


//java获取当前月的天数
Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
int day=aCalendar.getActualMaximum(Calendar.DATE);
  
/** 获取当月的天数*/
Calendar a = Calendar.getInstance();
a.set(Calendar.DATE, 1);
a.roll(Calendar.DATE, -1);
int maxDate = a.get(Calendar.DATE);

//获取某月天数
 public static int getDaysOfMonth(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
}

#获取当月的第一天和最后一天的字符串
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
// 获取当月第一天
calendar = Calendar.getInstance();
calendar.add(Calendar.MONTH, 0);
calendar.set(Calendar.DAY_OF_MONTH, 1);  
String firstday = format.format(calendar.getTime());  
// 获取当月最后一天
calendar = Calendar.getInstance();  
calendar.add(Calendar.MONTH, 1);  
calendar.set(Calendar.DAY_OF_MONTH, 0);  
String lastday = format.format(calendar.getTime());  
// 打印结果字符串
System.out.println("本月第一天和最后一天分别是：" + firstday + " 和 " + lastday + "。");


#另外也可以使用Date类的实例对象配合SimpleDateFormat类的实例对象来获取当前日期字符串
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
Date date = new Date(); 
System.out.println("当前日期字符串：" + format.format(date) + "。");
```


#### 日期 和 日历 的转换
示例：给 1988-11-22 12:22:30 加上10天
```shell script
//获取到日期
String time = "1988-11-22 12:22:30";
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Date date = format.parse(time);

//将日期转成日历
Calendar calendar = Calendar.getInstance();
calendar.setTimeInMillis(date.getTime());
//calendar.setDate(date);

//加10天
calendar.add(Calendar.DATE, 10);
//减10天
calendar.add(Calendar.DATE, -10);

//获取毫秒值
long mills = calendar.getTimeInMillis();
```