package pers.hdq.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * 
 * @ClassName: WriteExcel 
 * @Description: 生成并写操作excel表格，可以扩展自定义表格格式（列宽、颜色等）。将比较数据写入excel表中存储。
 * @author HuDaoquan
 * @date 2019年7月1日 下午9:29:20 
 * @version v1.0
 */
public class WriteExcel {
	String[] sheetName = { "详细结果", "简略结果", "抄袭名单" };

	// main方法测试
	public static void main(String[] args) {
	}

	// 创建excel文件
	public void writeEXCEL(String excelPath, String sheetName, List<?> values) throws IOException {
		// TODO 自动生成的方法存根
		File f = new File(excelPath);
		if (!f.exists()) {
			creatEXcel(excelPath);
		}
		writeAppend(excelPath, sheetName, values);
	}

	@SuppressWarnings({ "resource", "null" })
	public void creatEXcel(String excelPath) {
		File file = new File(excelPath);
		// 定义表头
//	String[] title = { "序号", "姓名", "年龄" };
		// 创建excel工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 创建工作表sheet
		String[] t = { "判定结果", "文档1", "文档2", "余弦相似度", "Jaccard相似度", "图片相似度", "加权相似度" };
		String[] t1 = { "抄袭名单" };
		List<String[]> titles = new ArrayList<String[]>();
		titles.add(t);
		titles.add(t);
		titles.add(t1);
		HSSFSheet sheet;
		for (int i = 0; i < 3; i++) {
			sheet = workbook.createSheet(sheetName[i]);
			// 创建第一行
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = null;
			// 插入第一行数据的表头
			for (int j = 0; j < titles.get(i).length; j++) {
				cell = row.createCell(j);
				cell.setCellValue(titles.get(i)[j]);
			}
			try {
				file.createNewFile();
				// 将excel写入
				FileOutputStream stream = FileUtils.openOutputStream(file); // POI 包的工具类
				workbook.write(stream);
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("resource")
	public void writeAppend(String excelPath, String sheetName, List<?> values) throws IOException {
		FileInputStream fs = new FileInputStream((excelPath)); // 获取head.xls
		POIFSFileSystem ps = new POIFSFileSystem(fs); // 使用POI提供的方法得到excel的信息
		HSSFWorkbook wb = new HSSFWorkbook(ps);
		HSSFSheet sheet1;
		if (sheetName == "详细结果") {
			sheet1 = wb.getSheetAt(0); // 获取到工作表，因为一个excel可能有多个工作表
		} else if (sheetName == "简略结果") {
			sheet1 = wb.getSheetAt(1); // 获取到工作表，因为一个excel可能有多个工作表
		} else if (sheetName == "抄袭名单") {
			sheet1 = wb.getSheetAt(2); // 获取到工作表，因为一个excel可能有多个工作表
		} else {
			sheet1 = wb.getSheetAt(3); // 获取到工作表，因为一个excel可能有多个工作表
		}
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		HSSFRow row1 = sheet1.getRow(0); // 获取第一行（excel中的行默认从0开始，所以这就是为什么，一个excel必须有字段列头），即，字段列头，便于赋值
//		System.out.println(sheet1.getLastRowNum() + " " + row1.getLastCellNum()); // 分别得到最后一行的行号，和一条记录的最后一个单元格
		FileOutputStream out = new FileOutputStream((excelPath)); // 向head.xls中写数据
		row1 = sheet1.createRow((short) (sheet1.getLastRowNum() + 1)); // 在现有行号后追加数据
		for (int i = 0; i < values.size(); i++) {
			if (i < 3) {
				row1.createCell(i).setCellValue((String) values.get(i));
				// 设置第一个（从0开始）单元格的数据
			} else {
				row1.createCell(i).setCellValue((Double) values.get(i));
				row1.getCell(i).setCellStyle(cellStyle);
			}
//
		}
		out.flush();
		wb.write(out);
		out.close();
//		System.out.println(row1.getPhysicalNumberOfCells() + " " + row1.getLastCellNum());
	}
}