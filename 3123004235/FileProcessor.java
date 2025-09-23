// FileProcessor.java
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.AccessDeniedException;

public class FileProcessor {
    /**
     * 读取文件内容
     * @param filePath 文件路径
     * @return 文件内容字符串
     * @throws IOException 读取失败时抛出异常
     */
    public static String readFile(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        try {
            byte[] data = Files.readAllBytes(Paths.get(filePath));
            return new String(data, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new NoSuchFileException("文件不存在: " + filePath);
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException("没有权限读取文件: " + filePath);
        } catch (IOException e) {
            throw new IOException("读取文件失败: " + filePath + "，原因: " + e.getMessage(), e);
        }
    }

    /**
     * 写入内容到文件
     * @param filePath 文件路径
     * @param content 要写入的内容
     * @throws IOException 写入失败时抛出异常
     */
    public static void writeFile(String filePath, String content) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        if (content == null) {
            content = ""; // 允许写入空内容，但不允许content为null
        }

        try {
            Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8));
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException("没有权限写入文件: " + filePath);
        } catch (IOException e) {
            throw new IOException("写入文件失败: " + filePath + "，原因: " + e.getMessage(), e);
        }
    }
}