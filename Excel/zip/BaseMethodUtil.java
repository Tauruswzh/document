package com.water.board.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;


/**
 * 文件名: BaseMethodUtil.java
 * 作者: xiahao
 * 时间: 2020/9/21 下午11:12
 * 描述: 公共方法
 */

@Component
@Slf4j
public class BaseMethodUtil {


  /*  @Autowired
    private IOrderInfoClient orderInfoFeign;
    private static IOrderInfoClient orderInfoFeignStatic;

    @PostConstruct
    public void init() {
        orderInfoFeignStatic = orderInfoFeign;
    }*/


    /**
     * 方法名:  writeToText
     * 作者/时间: xiahao-2020/9/21
     * 描述: 读取字符串,生成文件
     * 参数:
     * content  : 文件数据
     * filePath : 文件路径
     * folderName   : 文件夹名
     * fileName     : 文件名
     * fileSuffix   : 文件后缀
     * 返回:
     */
    public static void writeToText(String content, String filePath, String folderName, String fileName, String fileSuffix) {
        Assert.notNull(filePath, "param : 'filePath' not null");
        Assert.notNull(folderName, "param : 'folderName' not null");
        Assert.notNull(fileName, "param : 'fileName' not null");
        Assert.notNull(fileSuffix, "param : 'fileSuffix' not null");

        try {
            // 生成的文件路径
            String path = filePath + folderName + File.separator + fileName + fileSuffix;
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close();
            fw.close();
        } catch (Exception e) {
            log.info("writeToText failed .... error:{}", ExceptionUtils.getStackTrace(e));
        }
    }


    /**
    * 方法名:  toZip
    * 作者/时间: xiahao-2020/9/22
    * 描述: 压缩zip
    * 参数:
     * srcDir 压缩文件夹路径
     * targetPath 压缩文件路径
     * KeepDirStructure  是否保留原来的目录结构,
     *      true:保留目录结构;
     *      false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
    * 返回:
    */
    public static void toZip(String srcDir, String targetPath, boolean KeepDirStructure) {
        log.info("toZip begin .... ");

        File sourceFile = null;
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(targetPath)))) {
            sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
        } catch (Exception e) {
            log.info("toZip failed .... error:{}", ExceptionUtils.getStackTrace(e));
        }finally {
            //删除原始文件夹
            if (sourceFile != null){
                delAllFile(sourceFile);
            }
        }
        log.info("toZip end .... ");
    }


    /**
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @param KeepDirStructure  是否保留原来的目录结构,
     *                          true:保留目录结构;
     *                          false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[BUFFER_SIZE];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            //是文件夹
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),true);
                    } else {
                        compress(file, zos, file.getName(),false);
                    }

                }
            }
        }
    }

    /**
     * 方法名:  setStringFormatZero
     * 作者/时间: xiahao-2020/9/21
     * 描述: 字符串补零
     * 参数: ul
     * 返回:
     */
    public static String setStringFormatZero(String num, int key) {
        return String.format(num, key);
    }


    /**
     * 删除文件或文件夹
     * @param directory
     */
    public static void delAllFile(File directory){
        if (!directory.isDirectory()){  //文件
            directory.delete();
        } else{                         //文件夹
            File[] files = directory.listFiles();

            // 空文件夹
            assert files != null;
            if (files.length == 0){
                directory.delete();
                return;
            }

            // 删除子文件夹和子文件
            for (File file : files){
                if (file.isDirectory()){
                    delAllFile(file);
                } else {
                    file.delete();
                }
            }

            // 删除文件夹本身
            directory.delete();
        }
    }

}
