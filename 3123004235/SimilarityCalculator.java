import java.util.*;

// SimilarityCalculator.java
public class SimilarityCalculator {
    // 降低句子匹配阈值以提高灵敏度
    private static final double SENTENCE_MATCH_THRESHOLD = 0.5;
    private static final Map<String, String> SYNONYM_MAP = TextProcessor.getSynonymMap();

    public static double calculateSimilarity(String originalText, String plagiarizedText) {
        String origClean = TextProcessor.cleanText(originalText);
        String plagClean = TextProcessor.cleanText(plagiarizedText);

        List<String> origSentences = TextProcessor.splitIntoSentences(origClean);
        List<String> plagSentences = TextProcessor.splitIntoSentences(plagClean);

        if (origSentences.isEmpty() || plagSentences.isEmpty()) {
            return 0.0;
        }

        double sentenceSimilarity = calculateSentenceSimilarity(origSentences, plagSentences);
        double wordFreqSimilarity = calculateWordFrequencySimilarity(origClean, plagClean);

        // 调整权重比例，增加词频权重
        double finalSimilarity = 0.7 * sentenceSimilarity + 0.3 * wordFreqSimilarity;

        return Math.max(0.0, Math.min(1.0, finalSimilarity));
    }

    static double calculateSentenceSimilarity(List<String> origSentences, List<String> plagSentences) {
        int totalMatches = 0;

        for (String plagSent : plagSentences) {
            double maxSimilarity = 0.0;

            for (String origSent : origSentences) {
                double similarity = calculateTwoSentencesSimilarity(origSent, plagSent);
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                }

                if (maxSimilarity >= 0.9) {
                    break;
                }
            }

            if (maxSimilarity >= SENTENCE_MATCH_THRESHOLD) {
                totalMatches++;
            }
        }

        return (double) totalMatches / plagSentences.size();
    }

    static double calculateTwoSentencesSimilarity(String sentence1, String sentence2) {
        List<String> words1 = TextProcessor.segmentSentence(sentence1);
        List<String> words2 = TextProcessor.segmentSentence(sentence2);

        if (words1.isEmpty() && words2.isEmpty()) return 1.0;
        if (words1.isEmpty() || words2.isEmpty()) return 0.0;

        List<String> normalized1 = normalizeWords(words1);
        List<String> normalized2 = normalizeWords(words2);

        int lcsLength = calculateLCSLength(normalized1, normalized2);

        // 使用平均长度作为分母，提高相似度计算准确性
        int avgLength = (normalized1.size() + normalized2.size()) / 2;
        return avgLength == 0 ? 0.0 : (double) lcsLength / avgLength;
    }

    static List<String> normalizeWords(List<String> words) {
        List<String> normalized = new ArrayList<>();
        for (String word : words) {
            normalized.add(SYNONYM_MAP.getOrDefault(word, word));
        }
        return normalized;
    }

    private static int calculateLCSLength(List<String> list1, List<String> list2) {
        int m = list1.size();
        int n = list2.size();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (list1.get(i - 1).equals(list2.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    static double calculateWordFrequencySimilarity(String text1, String text2) {
        List<String> words1 = TextProcessor.segmentSentence(text1);
        List<String> words2 = TextProcessor.segmentSentence(text2);

        // 对词语进行标准化处理，使词频计算考虑同义词
        List<String> normalized1 = normalizeWords(words1);
        List<String> normalized2 = normalizeWords(words2);

        Map<String, Integer> freq1 = getWordFrequency(normalized1);
        Map<String, Integer> freq2 = getWordFrequency(normalized2);

        Set<String> allWords = new HashSet<>(freq1.keySet());
        allWords.addAll(freq2.keySet());

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String word : allWords) {
            int f1 = freq1.getOrDefault(word, 0);
            int f2 = freq2.getOrDefault(word, 0);

            dotProduct += f1 * f2;
            norm1 += f1 * f1;
            norm2 += f2 * f2;
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private static Map<String, Integer> getWordFrequency(List<String> words) {
        Map<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }
        return frequency;
    }
}