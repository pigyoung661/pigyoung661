public class Main {
    public static void main(String[] args) {
        try {
            // 检查命令行参数
            if (args.length != 3) {
                System.err.println("使用方法: java -jar main.jar [原文文件路径] [抄袭文件路径] [结果文件路径]");
                System.exit(1);
            }

            // 读取文件内容
            String originalText = FileProcessor.readFile(args[0]);
            String plagiarizedText = FileProcessor.readFile(args[1]);

            // 计算相似度
            double similarity = SimilarityCalculator.calculateSimilarity(originalText, plagiarizedText);

            // 格式化结果为百分比，保留两位小数
            String result = String.format("%.2f%%", similarity * 100);

            // 写入结果文件
            FileProcessor.writeFile(args[2], result);

            System.out.println("查重完成，结果: " + result);

        } catch (Exception e) {
            System.err.println("程序错误: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
