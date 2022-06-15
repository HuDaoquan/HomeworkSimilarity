package pers.hdq.similarity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pers.hdq.util.IKUtils;

/**
 * @author HuDaoquan
 * @version v1.0
 * @ClassName: Jaccard
 * @Description: 杰卡德相似度算法计算两个字符相似度，余弦的补充
 * @date 2019年7月1日 下午9:41:33
 */
public class Jaccard {
    public static void main(String[] args) {
        String s1 = "我我我我我我我我我我我我我我我喜欢文学，喜欢地理，也喜欢化学";
        String s2 = "我不喜欢文学，喜欢化学";
        
        List<String> list1 = IKUtils.segStr(s1, true);
        List<String> list2 = IKUtils.segStr(s2, true);
        
        System.out.println(list1 + "\n与\n" + list2 + "\n的余弦相似度为：" + Jaccard.jaccardSimilarity(list1, list2));
        System.out.println();
    }
    
    /**
     * 求并集交集并返回杰卡德系数
     *
     * @param list1 分好词的List集合1
     * @param list1 分词分好的List集合2
     */
    public static double jaccardSimilarity(List<String> list1, List<String> list2) {
        Set<String> set1 = new HashSet<>();
        // 求并集
        set1.addAll(list1);
        set1.addAll(list2);
        if (set1.isEmpty()) {
            return 0;
        }
        // 求交集
        Set<String> set2 = new HashSet<>(list2);
        set2.retainAll(list1);
        
        //杰卡德相似度= 交集/并集
        double j = set2.size() / set1.size();
        // 清空，防止后续调用形成干扰
        set1.clear();
        set2.clear();
        return j;
    }
}
