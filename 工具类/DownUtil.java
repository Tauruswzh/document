package jnpf.util;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author JNPF开发平台组
 * @version V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date 2021/3/16 10:51
 */
@Slf4j
public class DownUtil {
    private DownUtil(){
        throw new IllegalStateException("DownUtil class");
    }

    private static final String UTF_8 = String.valueOf(StandardCharsets.UTF_8);
    private static final String ISO_8859_1 = String.valueOf(StandardCharsets.ISO_8859_1);

    /**
     * 下载excel
     *
     * @param fileName excel名称
     * @param workbook
     */
    public static void dowloadExcel(Workbook workbook, String fileName) {
        try {
            HttpServletResponse response = ServletUtil.getResponse();
            assert response != null;
            response.setCharacterEncoding(UTF_8);
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, UTF_8));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 下载文件
     *
     * @param file     文件
     * @param fileName 订单信息.pdf
     */
    public static void dowloadFile(File file, String fileName) {
        HttpServletResponse response = ServletUtil.getResponse();
        HttpServletRequest request = ServletUtil.getRequest();
        try (InputStream is = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(is)
             ) {
            assert response != null;
            response.setCharacterEncoding(UTF_8);
            response.setContentType("application/x-download");
            //编码的文件名字,关于中文乱码的改造
            String codeFileName = "";
            String agent = request.getHeader("USER-AGENT").toLowerCase();
            if (-1 != agent.indexOf("msie") || -1 != agent.indexOf("trident")) {
                //IE
                codeFileName = URLEncoder.encode(fileName, UTF_8);
            } else if (-1 != agent.indexOf("mozilla")) {
                //火狐，谷歌
                codeFileName = new String(fileName.getBytes(UTF_8), ISO_8859_1);
            } else {
                codeFileName = URLEncoder.encode(fileName, UTF_8);
            }
            response.setHeader("Content-Disposition", "attachment;filename=\"" + codeFileName + "\"");
            @Cleanup OutputStream os = response.getOutputStream();
            int i;
            byte[] buff = new byte[1024 * 8];
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     *
     * @param paths    路径
     * @param fileName 订单信息.pdf
     */
    public static void dowloadFile(String paths, String fileName) {
        HttpServletResponse response = ServletUtil.getResponse();
        HttpServletRequest request = ServletUtil.getRequest();
        try (InputStream is = new FileInputStream(new File(XSSEscape.escapePath(paths)));
             BufferedInputStream bis = new BufferedInputStream(is)
        ){
            assert response != null;
            response.setCharacterEncoding(UTF_8);
            response.setContentType("text/plain");
            if (fileName.contains(".svg")) {
                response.setContentType("image/svg+xml");
            }
            //编码的文件名字,关于中文乱码的改造
            String codeFileName = "";
            String agent = request.getHeader("USER-AGENT").toLowerCase();
            if (-1 != agent.indexOf("msie") || -1 != agent.indexOf("trident")) {
                //IE
                codeFileName = URLEncoder.encode(fileName, UTF_8);
            } else if (-1 != agent.indexOf("mozilla")) {
                //火狐，谷歌
                codeFileName = new String(fileName.getBytes(UTF_8), ISO_8859_1);
            } else {
                codeFileName = URLEncoder.encode(fileName, UTF_8);
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(codeFileName.getBytes(), UTF_8));
            @Cleanup OutputStream os = response.getOutputStream();
            int i;
            byte[] buff = new byte[1024 * 8];
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示验证码
     */
    public static void downCode(Integer stringLength) {
        HttpServletResponse response = DownUtil.getResponse();
        CodeUtil codeUtil = new CodeUtil();
        codeUtil.getRandcode(response, stringLength);
    }

    /**
     * 流返回界面
     */
    public static void write(BufferedImage image) {
        try {
            HttpServletResponse response = DownUtil.getResponse();
            //将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "PNG", response.getOutputStream());
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 设置img的response
     */
    public static HttpServletResponse getResponse() {
        HttpServletResponse response = ServletUtil.getResponse();
        assert response != null;
        response.setCharacterEncoding(UTF_8);
        //设置相应类型,告诉浏览器输出的内容为图片
        response.setContentType("image/jpeg");
        //设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        return response;
    }

    /**
     * 显示预览的pdf
     */
    public static void dowloadFile(File file) {
        try (InputStream in = new FileInputStream(file);
             OutputStream  outputStream = new BufferedOutputStream(Objects.requireNonNull(ServletUtil.getResponse()).getOutputStream())
        ){
            //读取指定路径下面的文件
            Objects.requireNonNull(ServletUtil.getResponse()).setContentType("application/pdf;charset=utf-8");
            //编码的文件名字,关于中文乱码的改造
            String codeFileName = "";
            String agent = ServletUtil.getRequest().getHeader("USER-AGENT").toLowerCase();
            if (-1 != agent.indexOf("msie") || -1 != agent.indexOf("trident")) {
                //IE
                codeFileName = URLEncoder.encode(file.getName(), UTF_8);
            } else if (-1 != agent.indexOf("mozilla")) {
                //火狐，谷歌
                codeFileName = new String(file.getName().getBytes(UTF_8), ISO_8859_1);
            } else {
                codeFileName = URLEncoder.encode(file.getName(), UTF_8);
            }
            ServletUtil.getResponse().setHeader("Content-Disposition", "filename=" + codeFileName);

            //创建存放文件内容的数组
            byte[] buff = new byte[1024];
            //所读取的内容使用n来接收
            int n;
            //当没有读取完时,继续读取,循环
            while ((n = in.read(buff)) != -1) {
                //将字节数组的数据全部写入到输出流中
                outputStream.write(buff, 0, n);
            }
            //强制将缓存区的数据进行输出
            outputStream.flush();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 显示文件
     */
    public static void dowloadFile(String file) {
        HttpServletResponse response = ServletUtil.getResponse();
        assert response != null;

        try (InputStream in = new FileInputStream(file);
             ServletOutputStream outputStream1 = response.getOutputStream();
             OutputStream outputStream = new BufferedOutputStream(outputStream1);
        ){
            //创建存放文件内容的数组
            byte[] buff = new byte[1024];
            //所读取的内容使用n来接收
            int n;
            //当没有读取完时,继续读取,循环
            while ((n = in.read(buff)) != -1) {
                //将字节数组的数据全部写入到输出流中
                outputStream.write(buff, 0, n);
            }
            //强制将缓存区的数据进行输出
            outputStream.flush();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 下载svg
     *
     * @param file
     */
    public static void dowloadSvgFile(File file) {
        try (InputStream in = new FileInputStream(file);
             OutputStream outputStream = new BufferedOutputStream(ServletUtil.getResponse().getOutputStream())
        ){
            //读取指定路径下面的文件
            Objects.requireNonNull(ServletUtil.getResponse()).setContentType("image/svg+xml;charset=utf-8");
            //编码的文件名字,关于中文乱码的改造
            String codeFileName = "";
            String agent = ServletUtil.getRequest().getHeader("USER-AGENT").toLowerCase();
            if (-1 != agent.indexOf("msie") || -1 != agent.indexOf("trident")) {
                //IE
                codeFileName = URLEncoder.encode(file.getName(), UTF_8);
            } else if (-1 != agent.indexOf("mozilla")) {
                //火狐，谷歌
                codeFileName = new String(file.getName().getBytes(UTF_8), ISO_8859_1);
            } else {
                codeFileName = URLEncoder.encode(file.getName(), UTF_8);
            }
            ServletUtil.getResponse().setHeader("Content-Disposition", "filename=" + codeFileName);

            //创建存放文件内容的数组
            byte[] buff = new byte[1024];
            //所读取的内容使用n来接收
            int n;
            //当没有读取完时,继续读取,循环
            while ((n = in.read(buff)) != -1) {
                //将字节数组的数据全部写入到输出流中
                outputStream.write(buff, 0, n);
            }
            //强制将缓存区的数据进行输出
            outputStream.flush();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 下载文件
     *
     * @param str
     * @param fileName
     */
    public static Boolean downloadFile(String str, String fileName) {
        HttpServletResponse response = ServletUtil.getResponse();
        OutputStream os = null;
        try {
            assert response != null;
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(), ISO_8859_1));
            byte[] bytes = str.getBytes(UTF_8);
            os = response.getOutputStream();
            // 将字节流传入到响应流里,响应到浏览器
            os.write(bytes);
            os.close();
            return true;
        } catch (Exception ex) {
            log.error("导出失败:", ex);
            throw new RuntimeException("导出失败");
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (IOException ioEx) {
                log.error("导出失败:", ioEx);
            }
        }
    }
}
