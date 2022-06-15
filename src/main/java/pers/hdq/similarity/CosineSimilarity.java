package pers.hdq.similarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pers.hdq.util.IKUtils;

/**
 * @author HuDaoquan
 * @version v1.0
 * @ClassName: CosineSimilarity
 * @Description: 余弦相似度算法计算两个字符串相似度
 * @date 2019年7月1日 下午9:40:56
 */
public class CosineSimilarity {
    
    /**
     * 词频统计
     *
     * @param content1
     * @param content2
     */
    public static Map<String, int[]> wordCount(List<String> content1, List<String> content2) {
        int[] tempArray;
        Map<String, int[]> vectorMap = new HashMap<>();
        // vectorMap.clear();
        for (int i = 0; i < content1.size(); i++) {
            // content1.get(i)分好词后的第i个词
            if (vectorMap.containsKey(content1.get(i))) { // 判断第i个词有没有在vectorMap中，如果在则在加一 实现统计词频
                vectorMap.get(content1.get(i))[0]++; // 会get到一个String类型
            } else {
                tempArray = new int[2];
                tempArray[0] = 1;
                tempArray[1] = 0;
                vectorMap.put(content1.get(i), tempArray);
            }
        }
        for (int i = 0; i < content2.size(); i++) {
            if (vectorMap.containsKey(content2.get(i))) {
                vectorMap.get(content2.get(i))[1]++;
            } else {
                tempArray = new int[2];
                tempArray[0] = 0;
                tempArray[1] = 1;
                vectorMap.put(content2.get(i), tempArray);
            }
        }
        return vectorMap;
    }
    
    /**
     * 求余弦相似度
     *
     * @param content1
     * @param content2
     *
     * @return double型结果
     */
    public static double sim(List<String> content1, List<String> content2) {
        Map<String, int[]> vectorMap = wordCount(content1, content2);
        double result = 0D;
        if (sqrtMulti(vectorMap) == 0) {
            return result;
        }
        result = pointMulti(vectorMap) / sqrtMulti(vectorMap);
        return result;
    }
    
    /**
     * 求分母
     *
     * @param paramMap
     *
     * @return
     */
    private static double sqrtMulti(Map<String, int[]> paramMap) {
        double result;
        double result1 = squares(paramMap); // 分母上的平方和
        result = Math.sqrt(result1);
//		System.out.println(result+"===="+result1);
        return result;
    }
    
    // 求平方和
    private static double squares(Map<String, int[]> paramMap) {
        double result1 = 0D;
        double result2 = 0D;
        Set<String> keySet = paramMap.keySet();
        for (String key : keySet) {
            int temp[] = paramMap.get(key);
//			System.out.println("0----------"+temp[0]);
//			System.err.println("1----------"+temp[1]);
            result1 += (temp[0] * temp[0]);
            result2 += (temp[1] * temp[1]);
        }
        return result1 * result2;
    }
    
    // 点乘法求分子
    private static double pointMulti(Map<String, int[]> paramMap) {
        double result = 0D;
        for (String key : paramMap.keySet()) {
            int temp[] = paramMap.get(key);
            result += (temp[0] * temp[1]);
        }
        return result;
    }
    
    /**
     * @param args
     *
     * @throws
     * @Title: main
     * @Description: main函数仅仅是单个类进行测试时使用
     * @author HuDaoquan
     */
    public static void main(String[] args) {
        String s1 = "微软是一家美国公司，是世界级公司";
        String s2 = "阿里是一家中国公司，是中国的巨头公司";
        // IK分词
        
        List<String> list1 = IKUtils.segStr(s1, false);
        List<String> list2 = IKUtils.segStr(s2, false);
        CosineSimilarity similarity = new CosineSimilarity();
        double simValue = sim(list1, list2);
        System.out.println(list1 + "\n与\n" + list2 + "\n的余弦相似度为：" + simValue);
    }
}
