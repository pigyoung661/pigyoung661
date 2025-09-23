import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

public class PlagiarismCheckerTest {

    // 测试文本清洗功能
    @Test
    public void testCleanText() {
        // 包含干扰字符、多余空格的文本
        String text = "  这是一龘段包丽含干医扰字腥符的碉文本  包含  多 余 空 格  ";
        String cleaned = TextProcessor.cleanText(text);
        assertEquals("这是一段包含干扰字符的文本 包含 多 余 空 格", cleaned);

        // 空文本处理
        assertEquals("", TextProcessor.cleanText(null));
        assertEquals("", TextProcessor.cleanText("   "));
    }

    // 测试句子分割功能
    @Test
    public void testSplitIntoSentences() {
        String text = "这是第一句。这是第二句！这是第三句？这是最后一句";
        List<String> sentences = TextProcessor.splitIntoSentences(text);
        assertEquals(4, sentences.size());
        assertEquals("这是第一句。", sentences.get(0));
        assertEquals("这是最后一句", sentences.get(3));

        // 空文本处理
        assertTrue(TextProcessor.splitIntoSentences("").isEmpty());
    }

    // 测试同义词替换功能
    @Test
    public void testSynonymReplacement() {
        List<String> words = Arrays.asList("周天", "马铃薯", "观看", "迅速");
        List<String> normalized = SimilarityCalculator.normalizeWords(words);
        assertEquals(Arrays.asList("星期天", "土豆", "看", "快速"), normalized);

        // 非同义词保留原词
        List<String> nonSynonyms = Arrays.asList("测试", "代码");
        assertEquals(nonSynonyms, SimilarityCalculator.normalizeWords(nonSynonyms));
    }

    // 测试句子相似度计算（LCS算法）
    @Test
    public void testTwoSentencesSimilarity() {
        // 完全相同的句子
        double sim1 = SimilarityCalculator.calculateTwoSentencesSimilarity("今天天气晴朗", "今天天气晴朗");
        assertEquals(1.0, sim1, 0.001);

        // 包含同义词的句子
        double sim2 = SimilarityCalculator.calculateTwoSentencesSimilarity("周天去看电影", "星期天去观看影片");
        assertEquals(0.6, sim2, 0.1);

        // 完全不同的句子
        double sim3 = SimilarityCalculator.calculateTwoSentencesSimilarity("苹果是红色的", "香蕉是黄色的");
        assertTrue(sim3 < 0.5);
    }

    // 测试句子级整体相似度
    @Test
    public void testSentenceSimilarity() {
        List<String> orig = Arrays.asList("今天天气很好。", "我要去公园。");
        List<String> plag = Arrays.asList("今日天气不错。", "我要去花园。");
        double similarity = SimilarityCalculator.calculateSentenceSimilarity(orig, plag);
        assertEquals(1.0, similarity, 0.001); // 两个句子都匹配

        List<String> plag2 = Arrays.asList("这是无关的句子。");
        double similarity2 = SimilarityCalculator.calculateSentenceSimilarity(orig, plag2);
        assertEquals(0.0, similarity2, 0.001);
    }

    // 测试词频相似度（余弦相似度）
    @Test
    public void testWordFrequencySimilarity() {
        // 词频完全相同
        double sim1 = SimilarityCalculator.calculateWordFrequencySimilarity("苹果 香蕉 苹果", "香蕉 苹果 苹果");
        assertEquals(1.0, sim1, 0.001);

        // 完全无重叠
        double sim3 = SimilarityCalculator.calculateWordFrequencySimilarity("苹果 香蕉", "猫 狗");
        assertEquals(0.0, sim3, 0.001);
    }

    // 测试整体相似度计算
    @Test
    public void testCalculateSimilarity() {
        // 完全抄袭
        String orig = "今天是周一，天气晴朗。我要去公园散步。";
        String plag = "今日是星期一，天气晴。我要去花园行走。";
        double sim1 = SimilarityCalculator.calculateSimilarity(orig, plag);
        assertEquals(1.0, sim1, 0.1);

        // 部分抄袭（50%句子匹配）
        String plag2 = "今天天气很好。我要去学校上课。";
        double sim2 = SimilarityCalculator.calculateSimilarity(orig, plag2);
        assertTrue(sim2 > 0.4 && sim2 < 0.6);

        // 完全不抄袭
        String plag3 = "这是一段与原文完全无关的文本。内容和结构都不同。";
        double sim3 = SimilarityCalculator.calculateSimilarity(orig, plag3);
        assertTrue(sim3 < 0.1);
    }
}