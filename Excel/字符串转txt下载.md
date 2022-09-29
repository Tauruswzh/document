字符串转txt下载

```java
public class test{
    public static void main(String[] args){
        String str = "第一行："+"123123"+"\n\n" + "第二行："+"123123"+"\n\n" + "第三行："+"123123"+"\n\n" ;
        String fileName = "文件名.txt";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(str.getBytes());
    
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        writeResponse(request,response,inputStream,fileName,contentType);
    }


    public static void writeResponse(HttpServletRequest request, HttpServletResponse response, InputStream inputStream,
                                     String fileName, String contentType){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //输出流
        try (InputStream i = inputStream; OutputStream o = response.getOutputStream()){
            //设置head
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\"",URLEncoder.encode(fileName, "UTF-8")));

            byte[] chars = new byte[2048];
            int read;
            while (-1 != (read = i.read(chars,0, chars.length))){
                o.write(chars,0, read);
            }
        }catch (Exception e){
            log.error("向页面输出文件异常, e:{}",e.getMessage());
            throw new RuntimeException("向页面输出文件异常, e:"+e.getMessage());
        }

        stopWatch.stop();
        log.info("向页面输出文件, cost:{}",stopWatch.getTotalTimeSeconds());
    }
}
```

