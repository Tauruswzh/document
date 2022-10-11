java系列产品https://www.e-iceblue.cn/licensing/install-spirepdf-for-java-from-maven-repository.html
maven坐标
首先，在 pom.xml 文件中配置 Maven 仓库路径。

<repositories>
        <repository>
            <id>com.e-iceblue</id>
            <name>e-iceblue</name>
            <url>https://repo.e-iceblue.cn/repository/maven-public/</url>
        </repository>
</repositories>
然后，在 pom.xml 文件中指定 Spire 产品的 Maven 依赖。如下列举了几种产品的配置方式，可参考使用。

如果需要在同一个程序项目中操作多种文件格式，例如：同时操作 Word、Excel 和 PDF 或者其他多种文件格式时，请使用集合包 Spire.Office for Java。否则，在同一程序中使用多个 Spire Jar 包会引起程序冲突，导致异常报错。

配置 Spire.PDF for Java

<dependencies>
    <dependency>
        <groupId> e-iceblue </groupId>
        <artifactId>spire.pdf</artifactId>
        <version>5.4.0</version>
    </dependency>
</dependencies>
配置 Spire.Doc for Java

<dependencies>
    <dependency>
        <groupId> e-iceblue </groupId>
        <artifactId>spire.doc</artifactId>
        <version>5.4.2</version>
    </dependency>
</dependencies>
配置 Spire.XLS for Java

<dependencies>
    <dependency>
        <groupId> e-iceblue </groupId>
        <artifactId>spire.xls</artifactId>
        <version>5.3.3</version>
    </dependency>
</dependencies>