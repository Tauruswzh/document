public static void getChart(){
    Workbook workbook = new Workbook();
    workbook.loadFromFile("picture.xlsx");
    //获取第一张工作表
    Worksheet sheet = workbook.getWorksheets().get(0);
    //将图表保存为图片
    BufferedImage[] images = workbook.saveChartAsImage(sheet);
    for (BufferedImage image : images) {
        try {
            ImageIO.write(image, "png", new File("D:\\picture\\ChartToImage.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

