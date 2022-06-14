package pers.hdq.function;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.ss.formula.functions.T;
import pers.hdq.ik.IKWordSegmentation;
import pers.hdq.model.PlagiarizeEntity;
import pers.hdq.model.SimilarityOutEntity;
import pers.hdq.picture.SaveHash;
import pers.hdq.similarity.CosineSimilarity;
import pers.hdq.similarity.Jaccard;
import pers.hdq.traverse.FileUtils;
import pers.hdq.traverse.WordPicture;
import pers.hdq.util.EasyExcelUtil;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;

public class CompareOptimize {
    public static void main(String[] args) {
        /*  需要查重的路径*/
        String path = "D:\\我的文档\\桌面\\测";
        /*  获取开始时间*/
        long startTime = System.currentTimeMillis();
        
        System.err.println("相似度计算结果已存入：" + calculateFileSimilarity(path, false, false, 0.9));
        /*  获取结束时间*/
        long endTime = System.currentTimeMillis();
        /*  输出程序运行时间*/
        System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s");
    }
    
    /**
     * @param path           需要查重的文件夹
     * @param ikFlag         是否打开智能分词，为false显示最小粒度分词结果
     * @param pictureSimFlag 是否计算文档中图片相似度，为是会增加准确率，但会极大增加运算时间
     * @param threshold      相似度阈值
     *
     * @return 返回写入比较结果的csv文件的路径
     */
    
    public static String calculateFileSimilarity(String path, Boolean ikFlag, Boolean pictureSimFlag,
                                                 Double threshold) {
        // Phash算法修改
        SaveHash saveHash = new SaveHash();
        /*  存储所有图片Hash指纹*/
        Map<String, String> savePicHash = new TreeMap<String, String>();
        /*  文件读取*/
        FileUtils fileUtils = new FileUtils();
        /*  余弦相似度*/
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        /*  Jaccard相似度*/
        Jaccard jaccard = new Jaccard();
        DecimalFormat df = new DecimalFormat("0.00%");
        /*  写csv*/
        WriteExcel writeExcel = new WriteExcel();
        /*  读取Word中的图片类*/
        WordPicture wordPicture = new WordPicture();
        IKWordSegmentation ikWordSegmentation = new IKWordSegmentation();
        
        String excelPath =
                path + "\\查重结果".concat(ikFlag.toString() + "智能分词-" + pictureSimFlag.toString() + "图片查重").concat(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).concat(".xlsx");
        // 将结果类存入List
        // List<Result> results = new ArrayList<Result>();
        /*  抄袭名单*/
        Set<String> simName = new HashSet<String>();
        /*  存储文档绝对路径集合*/
        Vector<String> vecFile = new Vector<String>();
        /*  将遍历了的文档 绝对路径存入数组，方便调用*/
        Vector<String> allDocAbsolutePath = recursionWord(path, vecFile);
        //总计算次数
        int suma = (allDocAbsolutePath.size() - 1) * allDocAbsolutePath.size() / 2;
        // 比较图片相似度
        if (pictureSimFlag) {
            for (String s : allDocAbsolutePath) {
                /*  将图片写入本地文档*/
                wordPicture.readWordPicture(s);
            }
            /*  存储文档中的图片绝对路径集合*/
            Vector<String> vecFilePicHash1 = new Vector<>();
            /*  将遍历了的图片绝对路径存入*/
            Vector<String> vsPicHash1 = recursionPhoto(path, vecFilePicHash1);
            System.out.println("\n正在处理图片，请稍后");
            for (String s : vsPicHash1) {
                File file = new File(s);
                String picAbsolutePath = file.getAbsolutePath();
                String hashValues = saveHash.getFeatureValue(s);
                savePicHash.put(picAbsolutePath, hashValues);
            }
            System.out.println("总共有" + allDocAbsolutePath.size() + "个文件" + vsPicHash1.size() + "张图片，常规最多需要3-5分钟");
        } else {
            System.out.println("总共有" + allDocAbsolutePath.size() + "个文件，需要比较" + suma + "次" + "\n大概需要" + suma / 30 + "秒");
        }
        System.out.println("开始计算文本相似度");
        // 冒泡排序原理遍历比较文件
        // 已经比较过的文档数量
        int finishDocCount = 0;
        // sheet2中简略结果数据
        List<SimilarityOutEntity> sortMaxResultList = new ArrayList<>(allDocAbsolutePath.size());
        // sheet1中详细所有数据
        List<SimilarityOutEntity> detailList = new ArrayList<>(allDocAbsolutePath.size());
        // sheet3中抄袭名单
        List<PlagiarizeEntity> plagiarizeEntityList = new ArrayList<>();
        for (int i = 0; i < allDocAbsolutePath.size() - 1; i++) {
            List<SimilarityOutEntity> doc1AllSimList = new ArrayList<>();
            for (int j = i + 1; j < allDocAbsolutePath.size(); j++) {
                // 比较文本相似度
                /*  读取文件，返回文本字符串*/
                String str1 = fileUtils.readFile(allDocAbsolutePath.get(i));
                String str2 = fileUtils.readFile(allDocAbsolutePath.get(j));
                /*  去除数字和字母*/
                str1 = str1.replaceAll("[0-9a-zA-Z]", "");
                str2 = str2.replaceAll("[0-9a-zA-Z]", "");
                /*  使用IK分词器分词*/
                List<String> list1 = ikWordSegmentation.segStr(str1, ikFlag);
                List<String> list2 = ikWordSegmentation.segStr(str2, ikFlag);
                /*  余弦相似度*/
                double conSim = cosineSimilarity.sim(list1, list2);
                double jaccardSim = jaccard.jaccardSimilarity(list1, list2);
                /*  每次都是新的结果*/
                String fileName1 = getFileName(allDocAbsolutePath.get(i))[0];
                String fileName2 = getFileName(allDocAbsolutePath.get(j))[0];
                String judgeResult = "";
                /*  存图片相似度的*/
                double avgPicSim = 0;
                /*  存最终结果*/
                double weightedSim = 0;
                if (pictureSimFlag) {
                    /*  存储文档i的图片绝对路径集合*/
                    Vector<String> vecFilePic1 = new Vector<>();
                    /*  将遍历了的图片绝对路径存入数组，方便调用*/
                    Vector<String> doc1PicPath = recursionPhoto(getFileName(allDocAbsolutePath.get(i))[1], vecFilePic1);
                    
                    /*  存储文档j的图片绝对路径集合*/
                    Vector<String> vecFilePic2 = new Vector<String>();
                    /*  将遍历了的图片绝对路径存入数组，方便调用*/
                    Vector<String> doc2PicPath = recursionPhoto(getFileName(allDocAbsolutePath.get(j))[1], vecFilePic2);
                    // 文档1中每张图片与文档2中所有图片相似度的最大值的集合
                    List<Double> doc1AllPictureMaxSim = new ArrayList<>(doc1PicPath.size());
                    for (String s : doc1PicPath) {
                        List<Double> pictureSimListOneDocList = new ArrayList<>(doc2PicPath.size());
                        String hash1 = savePicHash.get(s);
                        for (String value : doc2PicPath) {
                            double pictureSim;
                            String hash2 = savePicHash.get(value);
                            pictureSim = saveHash.getSimilarity(hash1, hash2);
                            pictureSimListOneDocList.add(pictureSim);
                            /*  找到某张图相似度超过90%就不再比较后面了*/
                            if (pictureSim > 0.9) {
                                break;
                            }
                        }
                        // 求出文档1中第k张图的最大相似度
                        double doc1PictureKSimMax =
                                pictureSimListOneDocList.stream().max(Comparator.comparing(Double::doubleValue)).orElse(0D);
                        doc1AllPictureMaxSim.add(doc1PictureKSimMax);
                    }
                    // 求出文档1的所有图片相似度均值
                    double sum = doc1AllPictureMaxSim.stream().collect(Collectors.averagingDouble(Double::doubleValue));
                    
                    double textSim = (conSim + jaccardSim) / 2;
                    if (doc2PicPath.isEmpty() && doc1PicPath.isEmpty()) {
                        /*  将文本相似度结果平方，，调整相似度*/
                        weightedSim = (Math.pow(textSim, 1.5) + avgPicSim);
                    } else {
                        /*  将文本相似度结果算1.5次方，，调整相似度*/
                        weightedSim = Math.pow(textSim, 1.5) * 0.6 + avgPicSim * 0.4;
                    }
                } else {
                    // 不计算图片相似度
                    double textSim = (conSim + jaccardSim) / 2;
                    /*  将文本相似度结果平方，，调整相似度*/
                    weightedSim = (Math.pow(textSim, 1.5) + avgPicSim);
                }
                
                if (weightedSim > threshold || jaccardSim > 0.90 || conSim > 0.95 || avgPicSim > 0.90) {
                    judgeResult = "高度抄袭";
                    //抄袭名单
                    plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(fileName1).build());
                    plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(fileName2).build());
                }
                finishDocCount++;
                System.out.println(fileName1 + "  与  " + fileName2 + "\n\tJac相似度为:" + df.format(jaccardSim)
                        + "\n\t余弦相似度为:" + df.format(conSim) + "\n\t图片相似度为:" + df.format(avgPicSim) + "\n\t加权相似度为:"
                        + df.format(weightedSim) + "\n  参考判定:" + judgeResult + "\n还有" + (suma - finishDocCount) + "份数据需要比较");
                
                
                SimilarityOutEntity cellSimEntity = SimilarityOutEntity.builder()
                        .judgeResult(judgeResult)
                        .conSim(conSim)
                        .avgPicSim(avgPicSim)
                        .jaccardSim(jaccardSim)
                        .leftDocName(fileName1)
                        .weightedSim(weightedSim)
                        .rightDocName(fileName2)
                        .build();
                
                detailList.add(cellSimEntity);
                doc1AllSimList.add(cellSimEntity);
            }
            
            for (SimilarityOutEntity p : detailList) {
                if (getFileName(allDocAbsolutePath.get(i))[0].equals(p.getRightDocName())) {
                    // 比如第5份文档，要加入1-4与5的比较结果；交换文件名变成5与1-4比较，防止重复值干扰
                    String temp = p.getLeftDocName();
                    p.setLeftDocName(p.getRightDocName());
                    p.setRightDocName(temp);
                    doc1AllSimList.add(p);
                }
            }
            //相似度降序排序
            doc1AllSimList = doc1AllSimList.stream().sorted(Comparator.comparing(SimilarityOutEntity::getWeightedSim, Comparator.reverseOrder())).collect(Collectors.toList());
            
            /*  求出每个文档的最大值，假如抄袭了多份，可能有多个*/
            for (SimilarityOutEntity similarityOutEntity : doc1AllSimList) {
                if (similarityOutEntity.getWeightedSim().equals(doc1AllSimList.get(0).getWeightedSim())) {
                    /*  加入后期排序*/
                    sortMaxResultList.add(similarityOutEntity);
                }
            }
        }
        
        // 排序简略结果
        sortMaxResultList = sortMaxResultList.stream().sorted(Comparator.comparing(SimilarityOutEntity::getWeightedSim, Comparator.reverseOrder())).collect(Collectors.toList());
        
        // 排序详细结果
        detailList = detailList.stream().sorted(Comparator.comparing(SimilarityOutEntity::getWeightedSim, Comparator.reverseOrder())).collect(Collectors.toList());
        // 去重抄袭名单
        plagiarizeEntityList = plagiarizeEntityList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PlagiarizeEntity::getDocName))), ArrayList::new));
        System.out.println("比较完成开始写文件");
        
        EasyExcelUtil.writeExcel(excelPath, detailList, sortMaxResultList, plagiarizeEntityList);
        return excelPath;
    }
    
    /**
     * 绝对路径中提取文件名,注意FN[1]的获取要与图片处理模块中的新建文件夹路径的获取方法一致！
     */
    public static String[] getFileName(String path) {
        File f = new File(path);
        /*  将空格替换为“-”，空格必须替换，否则新建文件夹报错*/
        String fn = f.getName().replaceAll(" ", "");
        // 获取文件名
        String[] temp = fn.split("\\.");
        String fileName = "\\" + temp[0];
        /*  父路径*/
        String pf = f.getParent();
        /*  拼接好，方便遍历用*/
        String[] FN = {fn, pf + fileName};
        return FN;
    }
    
    /**
     * 遍历文件夹中的文本文件
     *
     * @param root 遍历的跟路径
     *
     * @return 存储有所有文本文件绝对路径的字符串数组
     */
    public static Vector<String> recursionWord(String root, Vector<String> vecFile) {
        File file = new File(root);
        File[] subFile = file.listFiles();
        if (subFile != null) {
            for (File value : subFile) {
                String fileName = value.getName();
                /*  判断是文件还是文件夹*/
                if (value.isDirectory()) {
                    /*  文件夹则递归*/
                    recursionWord(value.getAbsolutePath(), vecFile);
                } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".txt")) {
                    /*  绝对路径*/
                    String absolutePath = value.getAbsolutePath();
                    vecFile.add(absolutePath);
                }
            }
        }
        return vecFile;
    }
    
    /**
     * 遍历文件夹中的图片
     *
     * @param root 遍历的跟路径
     *
     * @return 存储有所有文本文件绝对路径的字符串数组
     */
    public static Vector<String> recursionPhoto(String root, Vector<String> vecFile) {
        File file = new File(root);
        File[] subFile = file.listFiles();
        if (subFile != null) {
            for (File value : subFile) {
                String fileName = value.getName();
                /*  判断是文件还是文件夹*/
                if (value.isDirectory()) {
                    /*  文件夹则递归*/
                    recursionPhoto(value.getAbsolutePath(), vecFile);
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg")
                        || fileName.endsWith(".PNG")) {
                    /*  绝对路径*/
                    String AP = value.getAbsolutePath();
                    //				System.out.println(AP);
                    vecFile.add(AP);
                }
            }
        }
        return vecFile;
    }
}
