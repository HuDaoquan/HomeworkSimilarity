package pers.hdq.util;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TODO
 *
 * @Author: HuDaoquan
 * @Email: hudaoquan@enn.cn
 * @Date: 2022/6/14 13:14
 * @Version 1.0
 */
public class WordUtil {
    public static void main(String[] args) throws Exception {
        
        FileOutputStream out = null;
        for (int i = 1; i < 1001; i++) {
            XWPFDocument doc = new XWPFDocument();// 创建Word文件
            
            XWPFParagraph p = doc.createParagraph();// 新建一个段落
            p.setAlignment(ParagraphAlignment.CENTER);// 设置段落的对齐方式
            
            XWPFRun r = p.createRun();//创建段落文本
            r.setText("过去一年是党和国家历史上具有里程碑意义的一年。以习近平同志为核心的党中央团结带领全党全国各族人民，隆重庆祝中国共产党成立一百周年，胜利召开党的十九届六中全会、制定党的第三个历史决议，如期打赢脱贫攻坚战，如期全面建成小康社会、实现第一个百年奋斗目标，开启全面建设社会主义现代化国家、向第二个百年奋斗目标进军新征程。一年来，面对复杂严峻的国内外形势和诸多风险挑战，全国上下共同努力，统筹疫情防控和经济社会发展，全年主要目标任务较好完成，“十四五”实现良好开局，我国发展又取得新的重大成就。" + i);
            out = new FileOutputStream("D:\\桌面\\查重\\文档" + i + ".docx");
            doc.write(out);
        }
        out.close();
    }
    
}
