package pers.hdq.util;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;

import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;


import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * easyExcel 工具类(仅导出)中可自定义样式格式等
 *
 * @Author: HuDaoquan
 * @Email: hudaoquan@enn.cn
 * @Date: 2021/5/22 14:18
 * @Version 1.0
 */
public class EasyExcelUtil {
    
    
    /**
     * 导出 Excel ：一个 sheet，带表头.
     *
     * @param dataMap  各个Sheet数据
     * @param filepath 导出的文件名
     *
     * @throws Exception 异常
     */
    public static void writeExcel(String filepath, Map<String, List<T>> dataMap) throws Exception {
        //调用工具类,导出excel
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 颜色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        // 字体
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setWrapped(true);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠中对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        ExcelWriter excelWriter = EasyExcel.write(filepath).excelType(ExcelTypeEnum.XLSX).registerWriteHandler(horizontalCellStyleStrategy).build();
        AtomicInteger sheetNo = new AtomicInteger();
        dataMap.forEach((sheetName, dataList) -> {
            WriteSheet sheetDetail = EasyExcel.writerSheet(sheetNo.get(), sheetName).head(dataList.get(0).getClass()).build();
            excelWriter.write(dataList, sheetDetail);
            sheetNo.getAndIncrement();
        });
        excelWriter.finish();
        
    }
    
    
}

