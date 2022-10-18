Linux下Java读取文件路径


一般文件路径在windows中用 \ 表示，但是在其他系统平台下比如linux中就不是 \ 
所以java给我们提供了一个与平台无关的表示路径的常量 File.separator :
在windows中则表示 \ 比如现在有一个文件在D:\java\src\myjava中， 如何用绝对路径访问呢？ 

现在建立一个目录： 
File fDir=new File(File.separator);  //File.separator表示根目录，比如现在就表示在D盘下。 
String strFile="java"+File.separator+"src"+File.separator+"myjava"; //这个就是绝对路径 
File f=new File(fDir,strFile);