package pers.hdq.util;


import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author HuDaoquan
 * @version v1.0
 * @ClassName: FileUtils
 * @Description: 自动识别txt文件编码格式， 提取txt文档和word文档中的文字
 * @date 2019年7月1日 下午9:42:38
 */
public class FileUtils {
    /**
     * 判断文件编码格式（GBK，UTF-8，ASCI，Unicode<UTF-16LE>，Unicode<UTF-16LE>）
     *
     * @param path
     *
     * @return
     */
    public static String getFileEncode(String path) {
        String charset = "asci";
        byte[] first3Bytes = new byte[3];
        BufferedInputStream bis = null;
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(path));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset;
            }
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "Unicode";// UTF-16LE
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "Unicode";// UTF-16BE
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0) {
                        break;
                    }
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                    {
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                        // 双字节 (0xC0 - 0xDF) (0x80 - 0xBF),也可能在GB编码内
                        {
                            continue;
                        } else {
                            break;
                        }
                    } else if (0xE0 <= read && read <= 0xEF) { // 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                // TextLogger.getLogger().info(loc + " " + Integer.toHexString(read));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ex) {
                }
            }
        }
        return charset;
    }
    
    /**
     * 读取Word文档中的文字值
     *
     * @param path 需要读取的文件的绝对路径
     *
     * @return 给定Word文档中的文字内容
     */
    public static String readWordtxt(String path) {
        String buffer = "";
        try {
            if (path.endsWith(".doc")) {
                InputStream is = new FileInputStream(path);
                WordExtractor ex = new WordExtractor(is);
                buffer = ex.getText().trim();
                ex.close();
            } else if (path.endsWith("docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(path);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                buffer = extractor.getText();
                extractor.close();
            } else {
                System.out.println("此文件不是word文件！");
            }
        } catch (Exception e) {
            System.out.println("打开文件报错" + e);
        }
        return buffer;
    }
    
    /**
     * 通过路径获取文件的内容，这个方法因为用到了字符串作为载体，为了正确读取文件（不乱码），只能读取文本文件，安全方法！
     */
    public static String readTxt(String path) {
        String data = null;
        File file = new File(path);
        // 获取文件编码格式
        String code = getFileEncode(path);
        InputStreamReader isr = null;
        try {
            // 根据编码格式解析文件
            if ("asci".equals(code)) {
                // 这里采用GBK编码，而不用环境编码格式，因为环境默认编码不等于操作系统编码
                // code = System.getProperty("file.encoding");
                code = "GBK";
            }
            isr = new InputStreamReader(new FileInputStream(file), code);
            // 读取文件内容
            int length = -1;
            char[] buffer = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((length = isr.read(buffer, 0, 1024)) != -1) {
                sb.append(buffer, 0, length);
            }
            data = new String(sb);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("getFile IO Exception:" + e.getMessage());
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("getFile IO Exception:" + e.getMessage());
            }
        }
//		System.err.println("txt:"+data);
        return data;
    }
    
    /**
     * 判断是Word还是txt；判断文件是否存在，调用本类中的其余方法读取文档文字内容
     *
     * @param filePath 传入文件绝对路径
     *
     * @return 返回读取后的文字字符串
     */
    public static String readFile(String filePath) {
        File file = new File(filePath);
        String SB = null;
        if (file.exists() && file.isFile()) {
            if (filePath.endsWith(".txt")) {
                SB = readTxt(filePath);
            } else if (filePath.endsWith(".doc") || filePath.endsWith(".docx")) {
                SB = readWordtxt(filePath);
            } else {
                System.err.println("给定文件不是txt文档" + filePath);
            }
            SB = SB.replaceAll("\r|\n", "");// 删除换行符
//			return SB;
        } else {
            System.err.println("给定文件不存在");
        }
        return SB;
    }
    
    public static void main(String[] args) {
        String str1 = null;
        String str2 = null;
        
        String filePath1 = "F:\\大三\\大二\\大二下\\第4次微机原理\\1500890217  何荣  微机原理第4次实验 .doc";
        String filePath2 = "F:\\大三\\大二\\大二下\\第4次微机原理\\1500890250  聂彩娥  微机原理第4次实验.doc";
        
        str1 = readFile(filePath1);
        str2 = readFile(filePath2);
        
        List<String> list1 = IKUtils.segStr(str1, false);
        List<String> list2 = IKUtils.segStr(str2, false);
        
        System.out.println(list1);
        System.out.println(list2);
    }
}
