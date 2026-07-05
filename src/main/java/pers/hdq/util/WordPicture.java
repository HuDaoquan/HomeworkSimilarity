package pers.hdq.util;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import pers.hdq.model.DocFileEntity;
import pers.hdq.picture.PHash;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HuDaoquan
 * @version v1.0
 * @ClassName: WordPicture
 * @Description: 自动提取doc文档和docx文档中的图片存入文档对应文件夹，若已存在文件夹则清空重写数据（防止重复）
 * @date 2019年7月1日 下午9:44:35
 */
public class WordPicture {
    
    public static List<String> getWordPicture(DocFileEntity docEntity) {
        List<String> pictureHashList = new ArrayList<>();
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
                // 获取每张图片哈希指纹
                for (Picture picture : pictures) {
                    InputStream pictureFile = new ByteArrayInputStream(picture.getContent());
                    pictureHashList.add(PHash.getFeatureValue(pictureFile));
                    pictureFile.close();
                }
            } else if (docEntity.getAbsolutePath().endsWith("docx")) {
                docx = new XWPFDocument(is);
                extractor = new XWPFWordExtractor(docx);
                // 文档图片
                List<XWPFPictureData> pictures = docx.getAllPictures();
                for (XWPFPictureData picture : pictures) {
                    byte[] bytev = picture.getData();
                    InputStream pictureFile = new ByteArrayInputStream(bytev);
                    // 获取图片哈希指纹
                    pictureHashList.add(PHash.getFeatureValue(pictureFile));
                    pictureFile.close();
                }
            } else {
                System.out.println("此文件不是word文件！");
            }
        } catch (Exception e) {
            System.out.println("打开文件失败:请检查文件是否有特殊格式:" + e);
        } finally {
            try {
                if (extractor != null) {
                    extractor.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                System.out.println("关闭文件失败:不影响:" + e);
            }
        }
        return pictureHashList;
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
        String path = "F:\\桌面\\查重图片\\今年\\文档1.docx";
        
        DocFileEntity docEntity = DocFileEntity.builder()
                .fileName("文档")
                .absolutePath(path).build();
        getWordPicture(docEntity);
        
    }
}
