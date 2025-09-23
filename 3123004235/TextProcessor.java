import java.util.*;
import java.util.regex.Pattern;

public class TextProcessor {
    // 常见干扰字符集合

    private static final Set<Character> NOISE_CHARS = new HashSet<>(Arrays.asList(
            // 原有字符
            '丽', '医', '腥', '碉', '钡', '酝', '怆', '桀', '铐', '蹬',
            '髓', '镣', '瓤', '巍', '麝', '龌', '鬓', '魇', '鞑', '躐',

            // 补充生僻字
            '龘', '靐', '齉', '齾', '爩', '鱻', '麤', '龗', '灪', '吁',
            '龖', '厵', '滟', '爨', '癵', '驫', '麣', '纞', '虋', '讟',
            '钃', '鸜', '麷', '鞻', '韽', '韾', '顟', '顠', '饙', '饙',
            '騳', '騱', '骉', '鸙', '鸘', '齉', '黸', '鼺', '鼉', '黼',
            '黻', '盱', '怸', '扅', '扆', '厶', '乸', '乿', '亄', '亹',
            '亾', '亽', '亼', '兛', '兞', '兝', '兡', '兣', '冭', '冪',
            '冸', '冹', '冺', '冾', '冿', '凨', '凩', '凪', '凫', '凼',
            '刄', '刈', '刉', '刋', '刌', '刏', '刐', '刑', '刓', '刔',
            '刕', '刜', '刞', '刟', '刡', '刢', '刣', '刦', '刧', '刨',
            '坔', '坕', '坙', '坸', '坹', '坺', '坽', '坾', '坿', '垀',
            '垁', '垇', '垈', '垉', '垊', '垍', '垎', '垏', '垐', '垑',
            '垔', '垕', '垖', '垗', '垘', '垙', '垚', '垜', '垝', '垞',
            '祎', '仡', '仝', '仞', '仟', '伋', '仦', '仧', '伛', '伜',
            '伡', '伨', '伩', '伬', '伭', '伮', '伱', '伳', '伵', '伷',
            '伹', '伻', '伾', '伿', '佀', '佁', '佂', '佄', '佅', '佇',
            '侊', '侌', '侎', '侐', '侒', '侓', '侕', '侙', '侚', '侜',
            '侞', '侟', '侠', '価', '侢', '侤', '侭', '侰', '侱', '侲',
            '昦', '昩', '昪', '昫', '昬', '昮', '昰', '昲', '昳', '昴',
            '昶', '昷', '昸', '昹', '昺', '昻', '昽', '昿', '晀', '晁',
            '時', '晄', '晅', '晆', '晇', '晈', '晊', '晍', '晎', '晐',
            '琟', '琧', '琩', '琫', '琭', '琯', '琱', '琲', '琸', '琹',
            '琻', '琽', '琾', '琿', '瑀', '瑂', '瑃', '瑄', '瑅', '瑇',
            '瑈', '瑉', '瑊', '瑌', '瑍', '瑎', '瑏', '瑐', '瑑', '瑒'
    ));

    // 中文标点符号（用于句子分割）
    private static final Set<Character> CHINESE_PUNCTUATION = new HashSet<>(Arrays.asList(
            '。', '！', '？', '；', '：', '，', '.', '!', '?', ';', ':'
    ));

    // 停用词集合
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "的", "了", "是", "在", "有", "和", "就", "都", "而", "及",
            "与", "也", "不", "很", "还", "又", "只", "这", "那", "我",
            "你", "他", "她", "它", "我们", "你们", "他们", "她们", "它们"
    ));

    /**
     * 清洗文本：移除干扰字符、多余空格
     * @param text 原始文本
     * @return 清洗后的文本
     */
    public static String cleanText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        StringBuilder cleaned = new StringBuilder();
        for (char c : text.toCharArray()) {
            // 移除干扰字符
            if (NOISE_CHARS.contains(c)) {
                continue;
            }
            // 替换多个空格为单个空格
            if (Character.isWhitespace(c)) {
                if (cleaned.length() > 0 && !Character.isWhitespace(cleaned.charAt(cleaned.length() - 1))) {
                    cleaned.append(' ');
                }
            } else {
                cleaned.append(c);
            }
        }
        return cleaned.toString().trim();
    }

    /**
     * 将文本分割为句子
     * @param text 清洗后的文本
     * @return 句子列表
     */
    public static List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        if (text.isEmpty()) {
            return sentences;
        }

        StringBuilder currentSentence = new StringBuilder();
        for (char c : text.toCharArray()) {
            currentSentence.append(c);
            // 遇到标点符号则分割句子
            if (CHINESE_PUNCTUATION.contains(c)) {
                String sentence = currentSentence.toString().trim();
                if (!sentence.isEmpty()) {
                    sentences.add(sentence);
                }
                currentSentence.setLength(0);
            }
        }

        // 添加最后一个句子
        String lastSentence = currentSentence.toString().trim();
        if (!lastSentence.isEmpty()) {
            sentences.add(lastSentence);
        }

        return sentences;
    }

    /**
     * 句子分词（过滤停用词）
     * @param sentence 句子
     * @return 词语列表
     */
    public static List<String> segmentSentence(String sentence) {
        List<String> words = new ArrayList<>();
        if (sentence.isEmpty()) {
            return words;
        }

        // 简单中文分词（按字分词，适合短文本）
        for (char c : sentence.toCharArray()) {
            String word = String.valueOf(c);
            if (!STOP_WORDS.contains(word) && !word.trim().isEmpty()) {
                words.add(word);
            }
        }

        return words;
    }

    /**
     * 获取同义词映射（针对示例中的同义词）
     * @return 同义词映射表
     */
    public static Map<String, String> getSynonymMap() {
        Map<String, String> synonymMap = new HashMap<>();
        // 添加常见同义词对
        // 时间相关
        synonymMap.put("周天", "星期天");
        synonymMap.put("周一", "星期一");
        synonymMap.put("周二", "星期二");
        synonymMap.put("周三", "星期三");
        synonymMap.put("周四", "星期四");
        synonymMap.put("周五", "星期五");
        synonymMap.put("周六", "星期六");
        synonymMap.put("明日", "明天");
        synonymMap.put("昨日", "昨天");
        synonymMap.put("今儿", "今天");
        synonymMap.put("立马", "立刻");
        synonymMap.put("马上", "立刻");
        synonymMap.put("即刻", "立刻");
        synonymMap.put("瞬间", "刹那");
        synonymMap.put("稍后", "稍后");
        synonymMap.put("清晨", "早晨");
        synonymMap.put("傍晚", "黄昏");
        synonymMap.put("子时", "半夜");
        synonymMap.put("午时", "中午");
        synonymMap.put("转瞬", "转眼");
        synonymMap.put("良久", "很久");
        synonymMap.put("往昔", "过去");
        synonymMap.put("未来", "将来");
        synonymMap.put("稍后", "过会儿");
        synonymMap.put("凌晨", "拂晓");
        synonymMap.put("正午", "中午");

        // 天气相关
        synonymMap.put("晴朗", "晴");
        synonymMap.put("下雨", "降雨");
        synonymMap.put("下雪", "降雪");
        synonymMap.put("刮风", "吹风");
        synonymMap.put("多云", "阴天");
        synonymMap.put("炎热", "酷热");
        synonymMap.put("寒冷", "严寒");
        synonymMap.put("暖和", "温暖");
        synonymMap.put("雷暴", "雷雨");
        synonymMap.put("冰雹", "雹子");
        synonymMap.put("雾霭", "雾气");
        synonymMap.put("霜冻", "冰冻");
        synonymMap.put("闷热", "湿热");
        synonymMap.put("凉爽", "凉快");
        synonymMap.put("狂风", "大风");
        synonymMap.put("细雨", "小雨");

        // 动作相关
        synonymMap.put("观看", "看");
        synonymMap.put("聆听", "听");
        synonymMap.put("品尝", "尝");
        synonymMap.put("行走", "走");
        synonymMap.put("奔跑", "跑");
        synonymMap.put("飞翔", "飞");
        synonymMap.put("攀登", "爬");
        synonymMap.put("购置", "买");
        synonymMap.put("售卖", "卖");
        synonymMap.put("休憩", "休息");
        synonymMap.put("进食", "吃");
        synonymMap.put("饮用", "喝");
        synonymMap.put("抵达", "到达");
        synonymMap.put("启程", "出发");
        synonymMap.put("寻觅", "寻找");
        synonymMap.put("瞧见", "看见");
        synonymMap.put("知晓", "知道");
        synonymMap.put("思索", "思考");
        synonymMap.put("呼喊", "叫");
        synonymMap.put("赞扬", "表扬");
        synonymMap.put("责备", "批评");
        synonymMap.put("培育", "培养");
        synonymMap.put("研习", "学习");
        synonymMap.put("投掷", "扔");
        synonymMap.put("抓取", "抓");
        synonymMap.put("踩踏", "踩");
        synonymMap.put("跳跃", "跳");
        synonymMap.put("躺卧", "躺");
        synonymMap.put("坐落", "坐");
        synonymMap.put("关闭", "关");
        synonymMap.put("开启", "开");
        synonymMap.put("折断", "折");
        synonymMap.put("撕裂", "撕");
        synonymMap.put("涂抹", "涂");
        synonymMap.put("擦拭", "擦");
        synonymMap.put("洗涤", "洗");
        synonymMap.put("晾晒", "晒");
        synonymMap.put("烹饪", "煮");
        synonymMap.put("切割", "切");
        synonymMap.put("咀嚼", "嚼");
        synonymMap.put("吞咽", "咽");
        synonymMap.put("呼吸", "喘气");
        synonymMap.put("睡眠", "睡觉");
        synonymMap.put("苏醒", "醒来");
        synonymMap.put("哭泣", "哭");
        synonymMap.put("欢笑", "笑");
        synonymMap.put("争吵", "吵架");
        synonymMap.put("交谈", "说话");
        synonymMap.put("叮嘱", "嘱咐");
        synonymMap.put("驳斥", "反驳");
        synonymMap.put("承认", "认可");
        synonymMap.put("否认", "不承认");
        synonymMap.put("允许", "准许");
        synonymMap.put("禁止", "不许");
        synonymMap.put("跟随", "跟着");
        synonymMap.put("引领", "带领");
        synonymMap.put("等待", "等候");
        synonymMap.put("催促", "催");
        synonymMap.put("放弃", "舍弃");
        synonymMap.put("坚持", "保持");
        synonymMap.put("获得", "得到");
        synonymMap.put("失去", "丢失");
        synonymMap.put("增加", "增多");
        synonymMap.put("减少", "变少");
        synonymMap.put("瞅瞅", "看看");
        synonymMap.put("瞧瞧", "看看");
        synonymMap.put("瞄瞄", "看看");
        synonymMap.put("瞥瞥", "看看");
        synonymMap.put("打量", "观察");
        synonymMap.put("端详", "细看");
        synonymMap.put("浏览", "翻看");
        synonymMap.put("阅览", "阅读");
        synonymMap.put("诵读", "朗读");
        synonymMap.put("背诵", "记诵");
        synonymMap.put("默写", "默记");
        synonymMap.put("抄写", "誊写");
        synonymMap.put("誊抄", "抄写");
        synonymMap.put("绘画", "画画");
        synonymMap.put("描绘", "描画");
        synonymMap.put("勾勒", "勾画");
        synonymMap.put("涂抹", "涂画");
        synonymMap.put("雕刻", "雕琢");
        synonymMap.put("塑造", "打造");
        synonymMap.put("建造", "修建");
        synonymMap.put("修筑", "修建");
        synonymMap.put("拆除", "拆掉");
        synonymMap.put("毁坏", "破坏");
        synonymMap.put("损坏", "弄坏");
        synonymMap.put("修复", "修补");
        synonymMap.put("修补", "缝补");
        synonymMap.put("缝制", "缝制");
        synonymMap.put("编织", "编结");
        synonymMap.put("折叠", "对折");
        synonymMap.put("展开", "铺开");
        synonymMap.put("收拾", "整理");
        synonymMap.put("打扫", "清扫");

        // 物品相关
        synonymMap.put("影片", "电影");
        synonymMap.put("电视机", "电视");
        synonymMap.put("移动电话", "手机");
        synonymMap.put("马铃薯", "土豆");
        synonymMap.put("西红柿", "番茄");
        synonymMap.put("自行车", "单车");
        synonymMap.put("计算机", "电脑");
        synonymMap.put("因特网", "互联网");
        synonymMap.put("巴士", "公交车");
        synonymMap.put("的士", "出租车");
        synonymMap.put("诞辰", "生日");
        synonymMap.put("居所", "住所");
        synonymMap.put("钞票", "钱");
        synonymMap.put("书本", "书");
        synonymMap.put("电冰箱", "冰箱");
        synonymMap.put("洗衣机", "洗衣器");
        synonymMap.put("微波炉", "微波灶");
        synonymMap.put("吸尘器", "吸尘机");
        synonymMap.put("吹风机", "电吹风");
        synonymMap.put("眼镜", "眼镜儿");
        synonymMap.put("手表", "腕表");
        synonymMap.put("钢笔", "钢笔");
        synonymMap.put("橡皮", "橡皮擦");
        synonymMap.put("书包", "书袋");
        synonymMap.put("衣服", "衣裳");
        synonymMap.put("裤子", "裤装");
        synonymMap.put("鞋子", "鞋");
        synonymMap.put("帽子", "帽儿");
        synonymMap.put("袜子", "袜儿");
        synonymMap.put("米饭", "白饭");
        synonymMap.put("面条", "面");
        synonymMap.put("馒头", "馍馍");
        synonymMap.put("包子", "包儿");
        synonymMap.put("饺子", "饺儿");
        synonymMap.put("医院", "病院");
        synonymMap.put("学校", "学堂");
        synonymMap.put("商店", "店铺");
        synonymMap.put("银行", "钱庄");
        synonymMap.put("邮局", "邮政局");
        synonymMap.put("车站", "车站");
        synonymMap.put("机场", "航空港");
        synonymMap.put("公园", "花园");
        synonymMap.put("广场", "广场");

        // 情感与状态
        synonymMap.put("喜悦", "快乐");
        synonymMap.put("愤怒", "气愤");
        synonymMap.put("忧虑", "担心");
        synonymMap.put("恐惧", "害怕");
        synonymMap.put("惊讶", "吃惊");
        synonymMap.put("疲惫", "累");
        synonymMap.put("饥饿", "饿");
        synonymMap.put("口渴", "渴");
        synonymMap.put("健康", "安康");
        synonymMap.put("疾病", "生病");
        synonymMap.put("强壮", "健壮");
        synonymMap.put("虚弱", "孱弱");
        synonymMap.put("聪明", "聪慧");
        synonymMap.put("愚蠢", "愚笨");
        synonymMap.put("勇敢", "英勇");
        synonymMap.put("胆怯", "胆小");
        synonymMap.put("诚实", "老实");
        synonymMap.put("虚伪", "虚假");
        synonymMap.put("善良", "和善");
        synonymMap.put("凶恶", "凶狠");

        // 程度与数量
        synonymMap.put("极其", "非常");
        synonymMap.put("格外", "特别");
        synonymMap.put("略微", "稍微");
        synonymMap.put("全部", "全体");
        synonymMap.put("部分", "局部");
        synonymMap.put("多数", "大多");
        synonymMap.put("少数", "少许");
        synonymMap.put("大量", "许多");
        synonymMap.put("少量", "少许");
        synonymMap.put("整个", "全部");
        synonymMap.put("个别", "单独");
        synonymMap.put("约莫", "大约");
        synonymMap.put("几乎", "差不多");
        synonymMap.put("全部", "所有");
        synonymMap.put("唯独", "只有");
        synonymMap.put("倘若", "如果");

        // 方位与空间
        synonymMap.put("前方", "前面");
        synonymMap.put("后方", "后面");
        synonymMap.put("左侧", "左边");
        synonymMap.put("右侧", "右边");
        synonymMap.put("内部", "里面");
        synonymMap.put("外部", "外面");
        synonymMap.put("上方", "上面");
        synonymMap.put("下方", "下面");
        synonymMap.put("附近", "周边");
        synonymMap.put("远处", "远方");
        synonymMap.put("中央", "中间");
        synonymMap.put("角落", "拐角");

        // 其他常用同义词
        synonymMap.put("并且", "而且");
        synonymMap.put("然而", "但是");
        synonymMap.put("景致", "景色");
        synonymMap.put("巨大", "庞大");
        synonymMap.put("渺小", "微小");
        synonymMap.put("迅速", "快速");
        synonymMap.put("缓慢", "迟缓");
        synonymMap.put("美丽", "漂亮");
        synonymMap.put("丑陋", "难看");
        synonymMap.put("愉悦", "高兴");
        synonymMap.put("悲伤", "难过");
        synonymMap.put("肥胖", "胖");
        synonymMap.put("瘦削", "瘦");
        synonymMap.put("宽敞", "宽阔");
        synonymMap.put("狭窄", "狭小");
        synonymMap.put("清洁", "洁净");
        synonymMap.put("肮脏", "污秽");
        synonymMap.put("整洁", "整齐");
        synonymMap.put("杂乱", "混乱");
        synonymMap.put("有序", "整齐");
        synonymMap.put("无序", "混乱");
        synonymMap.put("聚集", "集合");
        synonymMap.put("聚拢", "聚集");
        synonymMap.put("分散", "散开");
        synonymMap.put("散开", "分散");
        synonymMap.put("移动", "挪动");
        synonymMap.put("挪动", "移动");
        synonymMap.put("搬运", "运送");
        synonymMap.put("运送", "运输");
        synonymMap.put("携带", "带");
        synonymMap.put("捎带", "顺带");
        synonymMap.put("丢弃", "扔掉");
        synonymMap.put("抛弃", "丢弃");
        synonymMap.put("保存", "保留");
        synonymMap.put("留存", "保存");
        synonymMap.put("储存", "存储");
        synonymMap.put("消耗", "耗费");
        synonymMap.put("耗费", "消耗");
        synonymMap.put("节省", "节约");
        synonymMap.put("节约", "节省");
        synonymMap.put("浪费", "挥霍");
        synonymMap.put("挥霍", "浪费");
        synonymMap.put("获取", "获得");
        synonymMap.put("获得", "得到");
        synonymMap.put("给予", "给");
        synonymMap.put("赠予", "赠送");
        synonymMap.put("接受", "接收");
        synonymMap.put("拒绝", "回绝");
        synonymMap.put("同意", "答应");
        synonymMap.put("答应", "同意");
        synonymMap.put("反对", "否决");
        synonymMap.put("批准", "同意");
        synonymMap.put("否决", "反对");
        synonymMap.put("允许", "许可");
        synonymMap.put("禁止", "不许");
        synonymMap.put("命令", "指令");
        synonymMap.put("请求", "恳求");
        synonymMap.put("恳求", "请求");
        synonymMap.put("要求", "需求");
        synonymMap.put("需求", "需要");
        synonymMap.put("需要", "需求");
        synonymMap.put("供给", "供应");
        synonymMap.put("供应", "提供");
        synonymMap.put("提供", "供应");
        synonymMap.put("缺乏", "缺少");
        synonymMap.put("缺少", "缺乏");
        synonymMap.put("充足", "充裕");
        synonymMap.put("充裕", "充足");
        synonymMap.put("丰富", "丰盛");
        synonymMap.put("丰盛", "丰富");
        synonymMap.put("稀少", "稀缺");
        synonymMap.put("稀缺", "稀少");
        synonymMap.put("困难", "艰难");
        synonymMap.put("艰难", "困难");
        synonymMap.put("容易", "简单");
        synonymMap.put("简单", "容易");
        synonymMap.put("复杂", "繁杂");
        synonymMap.put("繁杂", "复杂");
        synonymMap.put("简单", "简易");
        synonymMap.put("简易", "简单");
        synonymMap.put("详细", "细致");
        synonymMap.put("细致", "详细");
        synonymMap.put("简略", "简单");
        synonymMap.put("概括", "归纳");
        synonymMap.put("归纳", "概括");
        synonymMap.put("总结", "归纳");
        synonymMap.put("分析", "剖析");
        synonymMap.put("剖析", "分析");
        synonymMap.put("研究", "钻研");
        synonymMap.put("钻研", "研究");
        synonymMap.put("学习", "研习");
        synonymMap.put("研习", "学习");
        synonymMap.put("教导", "教诲");
        synonymMap.put("教诲", "教导");
        synonymMap.put("教育", "培育");
        synonymMap.put("培育", "培养");
        synonymMap.put("培养", "培育");
        synonymMap.put("训练", "锻炼");
        synonymMap.put("锻炼", "训练");
        synonymMap.put("练习", "操练");
        synonymMap.put("操练", "练习");
        synonymMap.put("掌握", "把握");
        synonymMap.put("把握", "掌握");
        synonymMap.put("了解", "知晓");
        synonymMap.put("知晓", "了解");
        synonymMap.put("明白", "清楚");
        synonymMap.put("清楚", "明白");
        synonymMap.put("糊涂", "迷糊");
        synonymMap.put("迷糊", "糊涂");
        synonymMap.put("困惑", "疑惑");
        synonymMap.put("疑惑", "困惑");
        synonymMap.put("确定", "肯定");
        synonymMap.put("肯定", "确定");
        synonymMap.put("否定", "否认");
        synonymMap.put("否认", "否定");
        synonymMap.put("正确", "对");
        synonymMap.put("错误", "错");
        synonymMap.put("准确", "精确");
        synonymMap.put("精确", "准确");
        synonymMap.put("偏差", "误差");
        synonymMap.put("误差", "偏差");
        synonymMap.put("真实", "实在");
        synonymMap.put("实在", "真实");
        synonymMap.put("虚假", "虚伪");
        synonymMap.put("虚伪", "虚假");
        synonymMap.put("诚实", "老实");
        synonymMap.put("老实", "诚实");
        synonymMap.put("狡猾", "狡诈");
        synonymMap.put("狡诈", "狡猾");
        synonymMap.put("善良", "和善");
        synonymMap.put("和善", "善良");
        synonymMap.put("凶恶", "凶狠");
        synonymMap.put("凶狠", "凶恶");
        synonymMap.put("友好", "友善");
        synonymMap.put("友善", "友好");
        synonymMap.put("敌对", "对立");
        synonymMap.put("对立", "敌对");
        synonymMap.put("团结", "联合");
        synonymMap.put("联合", "团结");
        synonymMap.put("分裂", "分开");
        synonymMap.put("分开", "分裂");
        synonymMap.put("合作", "协作");
        synonymMap.put("协作", "合作");
        synonymMap.put("竞争", "竞赛");
        synonymMap.put("竞赛", "竞争");
        synonymMap.put("胜利", "成功");
        synonymMap.put("成功", "胜利");
        synonymMap.put("失败", "失利");
        synonymMap.put("失利", "失败");
        synonymMap.put("坚持", "保持");
        synonymMap.put("保持", "坚持");
        synonymMap.put("放弃", "舍弃");
        synonymMap.put("舍弃", "放弃");
        synonymMap.put("努力", "奋力");
        synonymMap.put("奋力", "努力");
        synonymMap.put("懈怠", "松懈");
        synonymMap.put("松懈", "懈怠");
        synonymMap.put("勤奋", "勤勉");
        synonymMap.put("勤勉", "勤奋");
        synonymMap.put("懒惰", "懒散");
        synonymMap.put("懒散", "懒惰");
        synonymMap.put("积极", "主动");
        synonymMap.put("主动", "积极");
        synonymMap.put("消极", "被动");
        synonymMap.put("被动", "消极");
        synonymMap.put("迅速", "快速");
        synonymMap.put("快速", "迅速");
        synonymMap.put("缓慢", "迟缓");
        synonymMap.put("迟缓", "缓慢");
        synonymMap.put("匆忙", "仓促");
        synonymMap.put("仓促", "匆忙");
        synonymMap.put("悠闲", "清闲");
        synonymMap.put("清闲", "悠闲");
        synonymMap.put("紧张", "紧迫");
        synonymMap.put("紧迫", "紧张");
        synonymMap.put("放松", "松懈");
        synonymMap.put("松懈", "放松");
        synonymMap.put("安全", "平安");
        synonymMap.put("平安", "安全");
        synonymMap.put("危险", "危急");
        synonymMap.put("危急", "危险");
        synonymMap.put("健康", "安康");
        synonymMap.put("安康", "健康");
        synonymMap.put("疾病", "病患");
        synonymMap.put("病患", "疾病");
        synonymMap.put("治疗", "医治");
        synonymMap.put("医治", "治疗");
        synonymMap.put("康复", "痊愈");
        synonymMap.put("痊愈", "康复");
        synonymMap.put("死亡", "逝世");
        synonymMap.put("逝世", "死亡");
        synonymMap.put("出生", "降生");
        synonymMap.put("降生", "出生");
        synonymMap.put("成长", "生长");
        synonymMap.put("生长", "成长");
        synonymMap.put("衰老", "苍老");
        synonymMap.put("苍老", "衰老");
        synonymMap.put("年轻", "年青");
        synonymMap.put("年青", "年轻");
        synonymMap.put("古老", "古旧");
        synonymMap.put("古旧", "古老");
        synonymMap.put("崭新", "全新");
        synonymMap.put("全新", "崭新");
        synonymMap.put("陈旧", "老旧");
        synonymMap.put("老旧", "陈旧");
        synonymMap.put("美丽", "漂亮");
        synonymMap.put("漂亮", "美丽");
        synonymMap.put("丑陋", "难看");
        synonymMap.put("难看", "丑陋");
        synonymMap.put("英俊", "潇洒");
        synonymMap.put("潇洒", "英俊");
        synonymMap.put("丑陋", "丑恶");
        synonymMap.put("丑恶", "丑陋");
        synonymMap.put("明亮", "光亮");
        synonymMap.put("光亮", "明亮");
        synonymMap.put("黑暗", "昏暗");
        synonymMap.put("昏暗", "黑暗");
        synonymMap.put("干净", "洁净");
        synonymMap.put("洁净", "干净");
        synonymMap.put("肮脏", "污秽");
        synonymMap.put("污秽", "肮脏");
        synonymMap.put("宽敞", "宽阔");
        synonymMap.put("宽阔", "宽敞");
        synonymMap.put("狭窄", "狭小");
        synonymMap.put("狭小", "狭窄");
        synonymMap.put("高大", "魁梧");
        synonymMap.put("魁梧", "高大");
        synonymMap.put("矮小", "瘦小");
        synonymMap.put("瘦小", "矮小");
        synonymMap.put("肥胖", "臃肿");
        synonymMap.put("臃肿", "肥胖");
        synonymMap.put("瘦削", "瘦弱");
        synonymMap.put("瘦弱", "瘦削");
        synonymMap.put("强壮", "强健");
        synonymMap.put("强健", "强壮");
        synonymMap.put("虚弱", "衰弱");
        synonymMap.put("衰弱", "虚弱");
        synonymMap.put("坚硬", "坚固");
        synonymMap.put("坚固", "坚硬");
        synonymMap.put("柔软", "柔嫩");
        synonymMap.put("柔嫩", "柔软");
        synonymMap.put("沉重", "繁重");
        synonymMap.put("繁重", "沉重");
        synonymMap.put("轻盈", "轻快");
        synonymMap.put("轻快", "轻盈");
        synonymMap.put("粗糙", "毛糙");
        synonymMap.put("毛糙", "粗糙");
        synonymMap.put("光滑", "平滑");
        synonymMap.put("平滑", "光滑");
        synonymMap.put("温暖", "暖和");
        synonymMap.put("暖和", "温暖");
        synonymMap.put("寒冷", "严寒");
        synonymMap.put("严寒", "寒冷");
        synonymMap.put("炎热", "酷热");
        synonymMap.put("酷热", "炎热");
        synonymMap.put("凉爽", "凉快");
        synonymMap.put("凉快", "凉爽");
        synonymMap.put("干燥", "干旱");
        synonymMap.put("干旱", "干燥");
        synonymMap.put("潮湿", "湿润");
        synonymMap.put("湿润", "潮湿");
        synonymMap.put("香甜", "甘甜");
        synonymMap.put("甘甜", "香甜");
        synonymMap.put("苦涩", "苦涩");
        synonymMap.put("辛辣", "麻辣");
        synonymMap.put("麻辣", "辛辣");
        synonymMap.put("酸溜溜", "酸");
        synonymMap.put("咸津津", "咸");
        synonymMap.put("香喷喷", "香");
        synonymMap.put("臭烘烘", "臭");
        synonymMap.put("看电影", "观看影片");
        synonymMap.put("观看影片", "看电影");
        return synonymMap;
    }
}
