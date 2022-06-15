package pers.hdq.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import pers.hdq.model.DocFileEntity;

/**
 * @author HuDaoquan
 * @version v1.0
 * @ClassName: WordPicture
 * @Description: 自动提取doc文档和docx文档中的图片存入文档对应文件夹，若已存在文件夹则清空重写数据（防止重复）
 * @date 2019年7月1日 下午9:44:35
 */
public class WordPicture {
    
    public static List<String> getWordPicture(DocFileEntity docEntity) {
        List<String> picturePathList = new ArrayList<>();
        File file = new File(docEntity.getAbsolutePath());
        
        InputStream is = null;
        HWPFDocument doc = null;
        XWPFDocument docx = null;
        POIXMLTextExtractor extractor = null;
        try {
            is = new FileInputStream(file);
            if (docEntity.getAbsolutePath().endsWith(".doc")) {
                doc = new HWPFDocument(is);
                // 文档图片
                PicturesTable picturesTable = doc.getPicturesTable();
                List<Picture> pictures = picturesTable.getAllPictures();
                // 创建存储图片的文件夹，已存在则清空
                File fileP = new File(docEntity.getPictureParentPath());
                if (!fileP.exists()) {
                    fileP.mkdirs();
                } else {
                    delAllFile(docEntity.getPictureParentPath()); // 删除完里面所有内容
                }
                // 将图片存入本地
                int index = 0;
                for (Picture picture : pictures) {
                    File F = new File(fileP, "图片" + (index++) + "." + picture.suggestFileExtension());
                    picturePathList.add(F.getAbsolutePath());
                    OutputStream out = new FileOutputStream(F);
                    picture.writeImageContent(out);
                    out.close();
                }
            } else if (docEntity.getAbsolutePath().endsWith("docx")) {
                docx = new XWPFDocument(is);
                extractor = new XWPFWordExtractor(docx);
                File fileP = new File(docEntity.getPictureParentPath());
                // 创建存储图片的文件夹
                if (!fileP.exists()) {
                    fileP.mkdirs();
                } else {
                    delAllFile(docEntity.getPictureParentPath()); // 删除完里面所有内容
                }
                // 文档图片
                List<XWPFPictureData> pictures = docx.getAllPictures();
                for (XWPFPictureData picture : pictures) {
                    byte[] bytev = picture.getData();
                    // 输出图片到磁盘
//					System.out.println("picture.getFileName():-----" + picture.getFileName()); // picture.getFileName():-----image1.png
                    File F = new File(fileP, picture.getFileName());
                    picturePathList.add(F.getAbsolutePath());
                    FileOutputStream out = new FileOutputStream(F);
                    out.write(bytev);
                    out.close();
                }
            } else {
                System.out.println("此文件不是word文件！");
            }
        } catch (IOException e) {
            System.out.println("打开文件失败:请检查文件是否有特殊格式:" + e);
        } finally {
            try {
                if (extractor != null) {
                    extractor.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                System.out.println("关闭文件失败:不影响:" + e);
            }
        }
        return picturePathList;
    }
    
    /**
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
        }
    }
    
    public static void main(String[] args) {
        String path = "D:\\我的文档\\桌面\\测试\\1500890226  李芝强  微机原理第4次实验 .doc";
        WordPicture wp = new WordPicture();
        
    }
}
