package pers.hdq.function;

import java.io.File;
import java.util.Vector;

import pers.hdq.traverse.FileUtils;

/**
 * 
 * @ClassName: CountData 
 * @Description: 统计单个文档字数，单个文档图片数；计算总文字比较数、图片比较次数。不影响功能
 * @author HuDaoquan
 * @date 2019年7月1日 下午9:11:40 
 * @version v1.0
 */
public class CountData {
	public static void main(String[] args) {
		String path = "F:\\大三\\大二\\大二下\\第2次计算机网络"; // F:\大三\大二\大二下\第2次计算机网络
		bianliPic(path);
		bianliWord(path);
	}

	public static void bianliPic(String path) {
		File file = new File(path);
		File[] subFile = file.listFiles();
		Vector<Integer> sum = new Vector<Integer>();
		for (int i = 0; i < subFile.length; i++) {
			if (subFile[i].isDirectory()) { // 判断是文件还是文件夹
//				recursionPhoto(subFile[i].getAbsolutePath(), vecFile); // 文件夹则递归
				File file1 = new File(subFile[i].getAbsolutePath());
				File[] subFile1 = file1.listFiles();
				int k = 0;// 存单个文件图片数量

				for (int j = 0; j < subFile1.length; j++) {
					String fileName1 = subFile1[j].getName();
					if (fileName1.endsWith(".jpg") || fileName1.endsWith(".png") || fileName1.endsWith(".jpeg")
							|| fileName1.endsWith(".PNG")) {
						k++;
					}
				}
				sum.add(k);
			}

		}

		int max = 0;
		int M = 0;
		int nm;

		nm = sum.get(sum.size() - 1);
		for (int a = 0; a < sum.size() - 1; a++) {
			nm += sum.get(a);
			if (sum.get(a) > max)
				max = sum.get(a);
			for (int b = 1; b < sum.size(); b++)
				M += sum.get(a) * sum.get(b);
		}

		System.out.println("共有" + sum.size() + " 个文档" + nm + "张图，单个文档图片最多有： " + max + "张，共需要比较" + M + "次图片");
		System.err.println(sum.toString());
	}

	public static void bianliWord(String path) {
		FileUtils F = new FileUtils();

		Vector<Integer> slist = new Vector<Integer>(); // 存储文档绝对路径集合
		Vector<String> vecFile = new Vector<String>(); // 存储文档绝对路径集合
		Vector<String> vs = recursionWord(path, vecFile); // 将遍历了的文档 绝对路径存入数组，方便调用
		for (int i = 0; i < vs.size(); i++) {
			String str1 = F.readFile(vs.get(i));// 读取文件，返回文本字符串
			int s = str1.length();
			slist.add(s);
		}
		int max = 0;
		int min = slist.get(0);
		long sum = slist.get(slist.size() - 1);
		for (int a = 0; a < slist.size(); a++) {
			if (max < slist.get(a))
				max = slist.get(a);
			if (min > slist.get(a))
				min = slist.get(a);
			sum += slist.get(a) * (slist.size() - a - 1);
		}
		int n = slist.size() - 1 + (slist.size() - 1) * (slist.size() - 2) / 2;
		System.out.println("共" + slist.size() + "篇文档，单篇最多" + max + "个字，最少" + min + "字，比较" + n + "次" + sum + "字");
		System.err.println(slist.toString());
	}

	public static Vector<String> recursionWord(String root, Vector<String> vecFile) {
		File file = new File(root);
		File[] subFile = file.listFiles();
		for (int i = 0; i < subFile.length; i++) {
			String fileName = subFile[i].getName();
			if (subFile[i].isDirectory()) { // 判断是文件还是文件夹
				recursionWord(subFile[i].getAbsolutePath(), vecFile); // 文件夹则递归
			} else if (fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".txt")) {
				String AP = subFile[i].getAbsolutePath();// 绝对路径
//				System.out.println(AP);
				vecFile.add(AP);
			}
		}

		return vecFile;
	}
}
