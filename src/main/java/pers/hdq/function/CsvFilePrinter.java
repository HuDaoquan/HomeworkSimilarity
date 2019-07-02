package pers.hdq.function;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;

/**
 * 
 * @ClassName: CsvFilePrinter 
 * @Description: 将结果写成csv文件，已升级为写成excel表格，此类已废弃，仅供参考
 * @author HuDaoquan
 * @date 2019年7月1日 下午9:25:17 
 * @version v1.0
 */
public class CsvFilePrinter {
	private CSVPrint csvPrint;

	/**
	 * 写CSV文件用
	 *
	 * @param fileName 文件绝对路径
	 * @param values   要写入的字符串
	 * @param append   是否支持追加
	 * @return
	 * @throws IOException
	 */
	public void CsvWrite(String fileName, String[] values, boolean append) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			csvPrint = new CSVPrinter(new FileWriter(fileName, append));// 别动这行代码！
			csvPrint.writeln(new String[] { "判定结果", "文档1", "文档2", "余弦相似度", "Jaccard相似度", "图片相似度", "加权相似度" });
		} else { // 如果所操作的csv文件已存在，就要看是追加数据还是覆盖数据
			if (!append) {// 如果传入的是不追加，那就直接覆盖原始数据，创建表头
				csvPrint = new CSVPrinter(new FileWriter(fileName, append)); // 此处可不能和上方第36行合并
				csvPrint.writeln(new String[] { "判定结果", "文档1", "文档2", "余弦相似度", "Jaccard相似度", "图片相似度", "加权相似度" });
			}
		}
		// 然后开始写数据
		csvPrint.writeln(values);// 写数据
	}

	public void CsvWriteName(String fileName, String values, boolean append) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			csvPrint = new CSVPrinter(new FileWriter(fileName, append));// 别动这行代码！
			csvPrint.writeln(new String[] { "疑似抄袭文件" });
		} else { // 如果所操作的csv文件已存在，就要看是追加数据还是覆盖数据
			if (!append) {// 如果传入的是不追加，那就直接覆盖原始数据，创建表头
				csvPrint = new CSVPrinter(new FileWriter(fileName, append)); // 此处可不能和上方第36行合并
				csvPrint.writeln(new String[] { "疑似抄袭文件" });
			}
		}
		// 然后开始写数据
		csvPrint.writeln(values);// 写数据
	}

	public static void main(String[] args) throws Exception {
		String csvFile = "文本相似度1".concat("-").concat(DateFormatUtils.format(new Date(), "yyyyMMdd")).concat(".csv");
		CsvFilePrinter print = new CsvFilePrinter();
		String[] values = new String[] { "50001", "C914", Integer.toString(-80),
				DateFormatUtils.format(new Date(), "yyyy-MM-dd"), "w", "WW" };
		print.CsvWrite(csvFile, values, true);
	}
}
