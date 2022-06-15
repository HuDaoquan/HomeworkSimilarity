package pers.hdq.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
        
        System.out.println(list1 + "\n与\n" + list2 + "\n的杰卡德相似度为：" + Jaccard.jaccardSimilarity(list1, list2));
        System.out.println();
    }
    
    /**
     * 求并集交集并返回杰卡德系数
     *
     * @param list1 分好词的List集合1
     * @param list1 分词分好的List集合2
     */
    public static double jaccardSimilarity(List<String> list1, List<String> list2) {
        //中间值
        Map<String, String> list2Map = new HashMap<>();
        // 并集
        Map<String, String> unionMap = new HashMap<>();
        // 交集
        Map<String, String> intersectionMap = new HashMap<>();
        
        list2.forEach(i2 -> {
            list2Map.put(i2, i2);
            unionMap.put(i2, i2);
        });
        list1.forEach(i1 -> {
            //如果不为空，则证明list1和list2都拥有该数据，交集
            if (list2Map.get(i1) != null) {
                intersectionMap.put(i1, i1);
            }
            // 并集中没有它，就要添加到并集中
            unionMap.putIfAbsent(i1, i1);
        });
        
        
        if (unionMap.isEmpty()) {
            return 0;
        }
        
        //杰卡德相似度= 交集/并集
        double j = intersectionMap.size() / (double) unionMap.size();
        // 清空，防止后续调用形成干扰
        list2Map.clear();
        unionMap.clear();
        intersectionMap.clear();
        return j;
    }
}
