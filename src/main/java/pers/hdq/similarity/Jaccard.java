package pers.hdq.similarity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pers.hdq.ik.IKWordSegmentation;

/**
 * 
 * @ClassName: Jaccard 
 * @Description: 杰卡德相似度算法计算两个字符相似度，余弦的补充
 * @author HuDaoquan
 * @date 2019年7月1日 下午9:41:33 
 * @version v1.0
 */
public class Jaccard {
	public static void main(String[] args) {
		String s1 = "我我我我我我我我我我我我我我我喜欢文学，喜欢地理，也喜欢化学";
		String s2 = "我不喜欢文学，喜欢化学";
		IKWordSegmentation IK = new IKWordSegmentation();
		List<String> list1 = IK.segStr(s1, true); // 使用ik分词
		List<String> list2 = IK.segStr(s2, true);
		Jaccard J = new Jaccard();
		System.out.println(list1 + "\n与\n" + list2 + "\n的余弦相似度为：" + J.jaccardSimilarity(list1, list2));
		System.out.println();
	}

	/**
	 * 求并集交集并返回杰卡德系数
	 *
	 * @param list1 分好词的List集合1
	 * @param list1 分词分好的List集合2
	 */
	public double jaccardSimilarity(List<String> list1, List<String> list2) {
		Set<String> set1 = new HashSet<>();
		Set<String> set2 = new HashSet<>();
		set1.addAll(list1);
		set1.addAll(list2); // 求并集
		//
		set2.addAll(list2);
		set2.retainAll(list1); // 求交集
		double mergeNum = set1.size();// 并集元素个数
		double commonNum = set2.size();// 相同元素个数（交集）
		double j = commonNum / mergeNum;
		set1.clear();// 清空，防止后续调用形成干扰
		set2.clear();
		return j;
	}
}
