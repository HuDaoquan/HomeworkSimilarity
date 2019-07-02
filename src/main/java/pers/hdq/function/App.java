package pers.hdq.function;

/**
 * 
 * @ClassName: App 
 * @Description: 调用类，测试功能，可删除
 * @author HuDaoquan
 * @date 2019年7月1日 下午9:10:06 
 * @version v1.0
 */
public class App {
	public static void main(String[] args) {

		String path = "F:\\大三\\大二\\大二下\\第4次微机原理"; // 需要查重的路径
		long startTime = System.currentTimeMillis(); // 获取开始时间
		CompareOptimize com = new CompareOptimize();
		System.err.println("相似度计算结果已存入：" + com.calculateFileSimilarity(path, true, true, false, 0.9));
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s"); // 输出程序运行时间

	}
}
