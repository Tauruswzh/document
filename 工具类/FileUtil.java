package jnpf.util;

import jnpf.exception.JnpfException;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author JNPF开发平台组
 * @version V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date 2021/3/16 10:51
 */
@Slf4j
public class FileUtil {
    private FileUtil(){
        throw new IllegalStateException("FileUtil class");
    }

    private static final String UTF_8 = String.valueOf(StandardCharsets.UTF_8);
    /**
     * 判断文件夹是否存在
     *
     * @param filePath 文件地址
     * @return
     */
    public static boolean fileIsExists(String filePath) {
        File f = new File(XSSEscape.escapePath(filePath));
        return f.exists();
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean fileIsFile(String filePath) {
        File f = new File(XSSEscape.escapePath(filePath));
        return f.isFile();
    }

    /**
     * 创建文件
     *
     * @param filePath 文件地址
     * @param fileName 文件名
     * @return
     */
    public static boolean createFile(String filePath, String fileName) {
        String strFilePath = XSSEscape.escapePath(filePath + fileName);
        File file = new File(XSSEscape.escapePath(filePath));
        if (!file.exists()) {
            /** 注意这里是 mkdirs()方法 可以创建多个文件夹 */
            file.mkdirs();
        }
        File subfile = new File(jnpf.util.XSSEscape.escapePath(strFilePath));
        if (!subfile.exists()) {
            try {
                return subfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * 创建文件夹
     *
     * @param filePath 文件夹地址
     * @return
     */
    public static void createDirs(String filePath) {
        File file = new File(XSSEscape.escapePath(filePath));
        if (!file.exists()) {
            /** 注意这里是 mkdirs()方法 可以创建多个文件夹 */
            file.mkdirs();
        }
    }

    /**
     * 遍历文件夹下当前文件
     *
     * @param file 地址
     */
    public static List<File> getFile(File file) {
        List<File> list = new ArrayList<>();
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return list;
        } else {
            for (File f : fileArray) {
                if (f.isFile()) {
                    list.add(0, f);
                }
            }
        }
        return list;
    }

    /**
     * 遍历文件夹下所有文件
     *
     * @param file 地址
     */
    public static List<File> getFile(File file, List<File> list) {
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return list;
        } else {
            for (File f : fileArray) {
                if (f.isFile()) {
                    list.add(0, f);
                } else {
                    getFile(f, list);
                }
            }
        }
        return list;
    }

    /**
     * 删除文件或文件夹以及子文件夹和子文件等 【注意】请谨慎调用该方法，避免删除重要文件
     *
     * @param file
     */
    public static void deleteFileAll(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                // 文件
                log.info(file.getAbsolutePath() + " 删除中...");
                try {
                    Files.delete(file.toPath());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                log.info("删除成功！");
            } else {
                // 文件夹
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFileAll(files[i]);
                }
                try {
                    Files.delete(file.toPath());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } else {
            log.info(file.getAbsolutePath() + " 文件不存在！");
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath 文件路径
     */
    public static void deleteFile(String filePath) {
        File file = new File(XSSEscape.escapePath(filePath));
        if (file.exists() && file.isFile()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    /**
     * 打开目录
     *
     * @param path
     */
    public static void open(String path) {
        // 打开输出目录
        try {
            String osName = System.getProperty("os.name");
            if (osName != null) {
                if (osName.contains("Mac")) {
                    Runtime.getRuntime().exec("open " + path);
                } else if (osName.contains("Windows")) {
                    Runtime.getRuntime().exec("cmd /c start " + path);
                } else {
                    log.debug("文件输出目录:" + path);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向文件中添加内容
     *
     * @param strcontent 内容
     * @param filePath   地址
     * @param fileName   文件名
     */
    public static void writeToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        File subfile = new File(strFilePath);
        try (RandomAccessFile raf = new RandomAccessFile(subfile, "rw")){
            /** 将记录指针移动到该文件的最后 */
            raf.seek(subfile.length());
            /** 向文件末尾追加内容 */
            raf.write(strcontent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向文件中添加内容
     *
     * @param is 内容
     * @param filePath   地址
     * @param fileName   文件名
     */
    public static void writeToFile(InputStream is, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        // 每次写入时，都换行写
        File subfile = new File(XSSEscape.escapePath(filePath));
        try (FileOutputStream downloadFile = new FileOutputStream(subfile)){
            int index;
            byte[] bytes = new byte[1024];
            while ((index = is.read(bytes)) != -1) {
                downloadFile.write(bytes, 0, index);
                downloadFile.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改文件内容（覆盖或者添加）
     *
     * @param path    文件地址
     * @param content 覆盖内容
     * @param append  指定了写入的方式，是覆盖写还是追加写(true=追加)(false=覆盖)
     */
    public static void modifyFile(String path, String content, boolean append) {
        try (FileWriter fileWriter = new FileWriter(path, append);
             BufferedWriter writer = new BufferedWriter(fileWriter);
             ){
            writer.append(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     *
     * @param filePath 地址
     * @param filename 名称
     * @return 返回内容
     */
    public static String getString(String filePath, String filename) {
        try (FileInputStream inputStream = new FileInputStream(new File(XSSEscape.escapePath(filePath + filename)));
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader);
        ){
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 复制文件
     *
     * @param fromFile 要复制的文件目录
     * @param toFile   要粘贴的文件目录
     * @return 是否复制成功
     */
    public static boolean copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(jnpf.util.XSSEscape.escapePath(fromFile));
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return false;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();
        //目标目录
        File targetDir = new File(jnpf.util.XSSEscape.escapePath(toFile));
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory()) {
                //如果当前项为子目录 进行递归
                copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");
            } else {
                //如果当前项为文件则进行文件拷贝
                copyFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return true;
    }

    /**
     * 文件拷贝
     * 要复制的目录下的所有非子目录(文件夹)文件拷贝
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    public static boolean copyFile(String fromFile, String toFile) {
        try (InputStream fosfrom = new FileInputStream(XSSEscape.escapePath(fromFile));
             OutputStream fosto = new FileOutputStream(XSSEscape.escapePath(toFile));
             ){
            byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 文件拷贝
     *
     * @param fromFile
     * @param toFile
     * @param fileName
     * @return
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean copyFile(String fromFile, String toFile, String fileName) {
        try (InputStream fosfrom = new FileInputStream(fromFile);
             OutputStream fosto = new FileOutputStream(toFile + fileName);){
            //目标目录
            File targetDir = new File(jnpf.util.XSSEscape.escapePath(toFile));
            //创建目录
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 获取文件输入流
     */
    public static InputStream readFileToInputStream(String path) {
        InputStream inputStream = null;
        try {
            File file = new File(jnpf.util.XSSEscape.escapePath(path));
            inputStream = new FileInputStream(file);
        } catch (IOException e) {
            e.getMessage();
        }
        return inputStream;
    }

    /**
     * 保存文件
     *
     * @param inputStream
     * @param path
     * @param fileName
     */
    public static void write(InputStream inputStream, String path, String fileName) {
        // 输出的文件流保存到本地文件
        File tempFile = new File(XSSEscape.escapePath(path));
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        String newFileName = tempFile.getPath() + File.separator + fileName;
        log.info("保存文件：" + newFileName);

        try (OutputStream os = new FileOutputStream(XSSEscape.escapePath(newFileName))){
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        }catch (Exception e) {
            log.error("生成excel失败");
        } finally {
            // 完毕，关闭所有链接
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("关闭链接失败" + e.getMessage());
            }
        }
    }

    /**
     * 上传文件
     *
     * @param file     文件
     * @param filePath 保存路径
     * @param fileName 保存名称
     */
    public static void upFile(MultipartFile file, String filePath, String fileName) {
        try {
            // 输出的文件流保存到本地文件
            File tempFile = new File(jnpf.util.XSSEscape.escapePath(filePath));
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            File f = new File(filePath, fileName);
            //将上传的文件存储到指定位置
            file.transferTo(f);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 读取文件修改时间
     */
    public static String getCreateTime(String filePath) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File file = new File(XSSEscape.escapePath(filePath));
        // 毫秒数
        long modifiedTime = file.lastModified();
        // 通过毫秒数构造日期 即可将毫秒数转换为日期
        Date date = new Date(modifiedTime);
        return format.format(date);
    }

    /**
     * 获取文件类型
     */
    public static String getFileType(File file) {
        if (file.isFile()) {
            String fileName = file.getName();
            return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        }
        return null;
    }

    /**
     * 获取文件类型
     */
    public static String getFileType(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".") + 1;
        //获取文件的后缀名 jpg
        return fileName.substring(lastIndexOf);
    }

    /**
     * 获取文件大小
     *
     * @param data
     * @return
     */
    public static String getSize(String data) {
        String size = "";
        if (data != null && !StringUtil.isEmpty(data)) {
            long fileS = Long.parseLong(data);
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024) {
                size = df.format((double) fileS) + "BT";
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                size = df.format((double) fileS / 1048576) + "MB";
            } else {
                size = df.format((double) fileS / 1073741824) + "GB";
            }
        } else {
            size = "0BT";
        }
        return size;
    }

    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     * 压缩文件夹
     *
     * @param srcDir           压缩文件夹路径
     * @param outDir           压缩文件路径
     * @param keepDirStructure 是否保留原来的目录结构,
     *                         true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String outDir, boolean keepDirStructure, String... srcDir) {
        try (OutputStream out = new FileOutputStream(new File(XSSEscape.escapePath(outDir)));
             ZipOutputStream zos =new ZipOutputStream(out)
        ){
            List<File> sourceFileList = new ArrayList<>();
            for (String dir : srcDir) {
                File sourceFile = new File(XSSEscape.escapePath(dir));
                sourceFileList.add(sourceFile);
            }
            compress(sourceFileList, zos, keepDirStructure);
        } catch (Exception e) {
            log.error("压缩失败:{}", e.getMessage());
        }
    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param keepDirStructure 是否保留原来的目录结构,
     *                         true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            zos.putNextEntry(new ZipEntry(name));
            try (FileInputStream in = new FileInputStream(sourceFile)){
                int len;
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (keepDirStructure) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    if (keepDirStructure) {
                        compress(file, zos, name + "/" + file.getName(),
                                keepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), keepDirStructure);
                    }
                }
            }
        }
    }

    private static void compress(List<File> sourceFileList, ZipOutputStream zos, boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        for (File sourceFile : sourceFileList) {
            String name = sourceFile.getName();
            if (sourceFile.isFile()) {
                zos.putNextEntry(new ZipEntry(name));
                try (FileInputStream in = new FileInputStream(sourceFile)){
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                    zos.closeEntry();
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                File[] listFiles = sourceFile.listFiles();
                if (listFiles == null || listFiles.length == 0) {
                    if (keepDirStructure) {
                        zos.putNextEntry(new ZipEntry(name + "/"));
                        zos.closeEntry();
                    }
                } else {
                    for (File file : listFiles) {
                        if (keepDirStructure) {
                            compress(file, zos, name + "/" + file.getName(),
                                    keepDirStructure);
                        } else {
                            compress(file, zos, file.getName(),
                                    keepDirStructure);
                        }

                    }
                }
            }
        }
    }

    //=================================判断文件后缀==========================

    /**
     * 允许文件类型
     *
     * @param fileType      文件所有类型
     * @param fileExtension 当前文件类型
     * @return
     */
    public static boolean fileType(String fileType, String fileExtension) {
        String[] allowExtension = fileType.split(",");
        return Arrays.asList(allowExtension).contains(fileExtension.toLowerCase());
    }

    /**
     * 允许图片类型
     *
     * @param imageType     图片所有类型
     * @param fileExtension 当前图片类型
     * @return
     */
    public static boolean imageType(String imageType, String fileExtension) {
        String[] allowExtension = imageType.split(",");
        return Arrays.asList(allowExtension).contains(fileExtension.toLowerCase());
    }

    /**
     * 允许上传大小
     *
     * @param fileSize 文件大小
     * @param maxSize  最大的文件
     * @return
     */
    public static boolean fileSize(Long fileSize, int maxSize) {
        return fileSize > maxSize;
    }

    /**
     * 导入生成临时文件后，获取文件内容
     *
     * @param multipartFile 文件
     * @param filePath      路径
     * @return
     */
    public static String getFileContent(MultipartFile multipartFile, String filePath) {
        //文件名
        String fileName = multipartFile.getName();
        //上传到项目文件路径中
        FileUtil.upFile(multipartFile, filePath, fileName);
        //读取文件文件内容
        return FileUtil.getString(filePath, fileName);
    }

    /**
     * 导入生成临时文件后，获取文件内容
     *
     * @param multipartFile 文件
     * @return
     */
    public static String getFileContent(MultipartFile multipartFile) {
        StringBuilder content = new StringBuilder();
        try {
            @Cleanup InputStream is = multipartFile.getInputStream();
            @Cleanup InputStreamReader isReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            @Cleanup BufferedReader br = new BufferedReader(isReader);
            //循环逐行读取
            while (br.ready()) {
                content.append(br.readLine());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return content.toString();
    }


    /**
     * 判断是否为json格式且不为空
     * @param multipartFile
     * @param type  类型
     * @return
     */
    public static boolean existsSuffix(MultipartFile multipartFile, String type) {
        String originalFilename = multipartFile.getOriginalFilename();
        return StringUtils.isBlank(originalFilename) || !originalFilename.endsWith("." + type) || multipartFile.getSize() < 1;
    }

    /**
     * File转MultipartFile
     *
     * @param file
     * @return
     */
    public static MultipartFile createFileItem(File file) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem("textField", "text/plain", true, file.getName());

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = item.getOutputStream()){
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CommonsMultipartFile(item);
    }

    /**
     * MultipartFile 转 File
     *
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) {
        if (Objects.isNull(file)){
            return null;
        }

        File toFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (InputStream ins  = file.getInputStream();
             OutputStream os = new FileOutputStream(toFile)
        ){
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return toFile;
    }

    /**
     * Workbook 转 MultipartFile
     *
     * @param workbook excel文档
     * @param fileName 文件名
     * @return
     */
    public static MultipartFile workbookToCommonsMultipartFile(Workbook workbook, String fileName) {
        //Workbook转FileItem
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem fileItem = factory.createItem("textField", "text/plain", true, fileName);
        try {
            OutputStream os = fileItem.getOutputStream();
            workbook.write(os);
            os.close();
            //FileItem转MultipartFile
            return new CommonsMultipartFile(fileItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 方法名: writeResponse
     * 描述:   页面流下载
     * 作者:   xiahao
     * 时间:   2022/5/19 16:08
     * 参数:
     */
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

    public static void delAllFile(File directory) throws IOException {
        //如果是文件，直接删除
        if (directory.isFile()){
            Files.delete(directory.toPath());
            return;
        }

        //文件夹
        //子文件夹
        File[] files = directory.listFiles();

        //空文件夹
        assert files != null;
        if (files.length == 0){
            Files.delete(directory.toPath());
            return;
        }

        //删除自文件夹和文件
        for (File file:files){
            if (file.isDirectory()){
                delAllFile(file);
            }else {
                Files.delete(file.toPath());
            }
        }

        //删除文件夹本身
        Files.delete(directory.toPath());
    }


    /**
     * 流转ioToMultipartFile
     */
    public static MultipartFile ioToMultipartFile(byte[] bytes, String fileName) throws JnpfException {
        Assert.notNull(bytes,"bytes cannot null");
        Assert.hasText(fileName,"filename cannot null");

        try (InputStream in = new ByteArrayInputStream(bytes)){
            return new MockMultipartFile("file", fileName, ContentType.APPLICATION_OCTET_STREAM.toString(), in);
        }catch (Exception e){
            throw new JnpfException("流转换MultipartFile异常");
        }
    }

    /**
     * InputStream转成字节数组
     */
    public static byte[] inputStreamToByteArray(InputStream inputStream) throws JnpfException {
        Assert.notNull(inputStream,"inputStream cannot null");

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            byte[] buffer = new byte[1024];
            int num;
            while((num = inputStream.read(buffer)) != -1){
                out.write(buffer,0,num);
            }
            out.flush();
            return out.toByteArray();
        }catch (Exception e){
            throw new JnpfException("InputStream转成字节数组异常");
        }
    }
}
