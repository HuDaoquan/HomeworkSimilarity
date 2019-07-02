package pers.hdq.similarity;

import java.util.HashMap;

import org.json.JSONObject;

import com.baidu.aip.nlp.AipNlp;

/**
 * 百度API 每次只能查五组词，高了要收费。仅供参考，程序并未使用此类
 * 
 * @author Administrator
 *
 */
public class BaiduAPI {
	// 设置APPID/AK/SK
	// 注册百度人工智能平台https://console.bce.baidu.com/?_=1561988268546&fromai=1#/aip/overview获取下面三行数据
	public static final String APP_ID = "16251199";
	public static final String API_KEY = "ca5OkBC4X6Drsfk3Qv6cyQWl";
	public static final String SECRET_KEY = "du5bWy38Iez2DO2m7QeL6eHBXecoqhvk";
	public static final AipNlp client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);

	public static void main(String[] args) {
		// 初始化一个AipNlp

//        // 调用接口
//        String text = "百度是一家高科技公司";
//        JSONObject res = client.lexer(text, null);
//        System.out.println(res.toString(2));
		BaiduAPI BA = new BaiduAPI();
		BA.sample("北京", "上海");
	}

	public void sample(String word1, String word2) {
//	    String word1 = "北京";
//	    String word2 = "上海";

		// 传入可选参数调用接口
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put("mode", 0);

		// 词义相似度
		JSONObject res = client.wordSimEmbedding(word1, word2, options);

		System.out.println(res.toString(2));

	}

	public String wordSample(String text1, String text2) {
//        String text1 = "浙富股份";
//        String text2 = "万事通自考网";
		// 传入可选参数调用接口
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put("model", "CNN");

		// 短文本相似度
		JSONObject res = client.simnet(text1, text2, options);
//		double a = res.getDouble("score");
		System.out.println(res.toString(2));
		return res.toString(2);

	}

}
