package pers.hdq.ik;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * 
 * @ClassName: IKWordSegmentation 
 * @Description: 调用IK分词器接口实现去停止词分词，停止词存入./ext.dic
 * @author HuDaoquan
 * @date 2019年7月1日 下午9:14:41 
 * @version v1.0
 */
public class IKWordSegmentation {
	// 停用词词表
	public static final String stopWordTable = "./ext.dic";

	/**
	 * 
	 * @Title: segStr 
	 * @Description: TODO
	 * @param content 传入需要分词的字符串
	 * @param B 选择是否智能分词，为false将精细到最小颗粒分词
	 * @return  分完词的 List<String>                                                                                                                                                                                      
	 * @author HuDaoquan
	 * @throws
	 */
	public List<String> segStr(String content, Boolean B) {
		List<String> list = new ArrayList<String>();
		try {
			// 读入停用词文件
			BufferedReader StopWordFileBr = new BufferedReader(
					// 由于要导出，不能直接new file stopWordTable
					// 利用 this.getClass().getClassLoader().getResourceAsStream获取路径
					new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("ext.dic")));
			// 用来存放停用词的集合
			Set<String> stopWordSet = new HashSet<String>();
			// 初如化停用词集
			String stopWord = null;
			for (; (stopWord = StopWordFileBr.readLine()) != null;) {
				stopWordSet.add(stopWord);
			}
			// 创建分词对象
			StringReader sr = new StringReader(content);
			IKSegmenter ik = new IKSegmenter(sr, B);
			Lexeme lex = null;
			// 分词
			while ((lex = ik.next()) != null) {
				// 去除停用词
				if (stopWordSet.contains(lex.getLexemeText())) {
					continue;
				}
				list.add(lex.getLexemeText());
			}
			// 关闭流
			StopWordFileBr.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return list;
	}

	public static void main(String[] args) {
		IKWordSegmentation IK = new IKWordSegmentation();
		System.out.println(IK.segStr("笔记本电脑", false));
	}
}
