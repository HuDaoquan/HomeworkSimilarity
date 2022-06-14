package pers.hdq.function;

import org.apache.commons.lang.time.DateFormatUtils;
import pers.hdq.ik.IKWordSegmentation;
import pers.hdq.model.DocFileEntity;
import pers.hdq.model.PlagiarizeEntity;
import pers.hdq.model.SimilarityOutEntity;
import pers.hdq.picture.SaveHash;
import pers.hdq.similarity.CosineSimilarity;
import pers.hdq.similarity.Jaccard;
import pers.hdq.traverse.FileUtils;
import pers.hdq.traverse.WordPicture;
import pers.hdq.util.EasyExcelUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 文档相似度计算
 *
 * @Author: HuDaoquan
 * @Email: 1455523026@qq.com
 * @Date: 2019/6/13 12:27
 * @Version 1.0
 */
public class CompareOptimize {
    public static void main(String[] args) {
        /*  需要查重的路径*/
        String path = "D:\\桌面\\查重大文本";
        /*  获取开始时间*/
        long startTime = System.currentTimeMillis();
        
        System.err.println("相似度计算结果已存入：" + calculateFileSimilarity(path, true, false, 0.5));
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
        Map<String, String> allPictureHashMap = new TreeMap<String, String>();
        /*  文件读取*/
        FileUtils fileUtils = new FileUtils();
        /*  余弦相似度*/
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        /*  Jaccard相似度*/
        Jaccard jaccard = new Jaccard();
        DecimalFormat df = new DecimalFormat("0.00%");
        /*  读取Word中的图片类*/
        WordPicture wordPicture = new WordPicture();
        IKWordSegmentation ikWordSegmentation = new IKWordSegmentation();
        //导出的excel文档
        String excelPath =
                path + "\\查重结果".concat(ikFlag.toString() + "智能分词-" + pictureSimFlag.toString() + "图片查重").concat(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).concat(".xlsx");
        
        /*  递归遍历目录；获取所有文档绝对路径*/
        List<String> allDocAbsolutePath = recursionWord(path);
        //总计算次数
        int sumCount = (allDocAbsolutePath.size() - 1) * allDocAbsolutePath.size() / 2;
        
        System.out.println("开始计算文本相似度,共计" + allDocAbsolutePath.size() + "个文件,需计算" + sumCount + "次");
        //存储所有图片的绝对路径
        // List<String> allPictureAbsolutePath = new ArrayList<>(allDocAbsolutePath.size());
        List<DocFileEntity> allDocEntity = new ArrayList<>(allDocAbsolutePath.size());
        //遍历处理所有文件
        for (String s : allDocAbsolutePath) {
            // allPictureAbsolutePath.addAll(oneDocPicturePath);
            //获取文件名、文件路径等信息
            DocFileEntity docEntity = getDocFileName(s);
            //将每个文档的文本返回
            String text = fileUtils.readFile(s);
            /*  去除数字和字母*/
            text = text.replaceAll("[0-9a-zA-Z]", "");
            docEntity.setChineseText(text);
            /*  使用IK分词器分词*/
            List<String> wordList = ikWordSegmentation.segStr(text, ikFlag);
            docEntity.setWordList(wordList);
            // 比较图片相似度
            if (pictureSimFlag) {
                /*  将图片写入本地文档，并返回绝对路径*/
                List<String> oneDocPicturePath = wordPicture.getWordPicture(docEntity);
                List<String> oneDocPictureHash = new ArrayList<>(oneDocPicturePath.size());
                // 计算图片的hash指纹
                oneDocPicturePath.forEach(pictureFile -> oneDocPictureHash.add(saveHash.getFeatureValue(pictureFile)));
                docEntity.setPictureHash(oneDocPictureHash);
                System.out.println(docEntity.getFileName() + "包含" + oneDocPicturePath.size() + "张图片");
            }
            allDocEntity.add(docEntity);
        }
        
        // 冒泡排序原理遍历比较文件
        // 已经比较过的文档数量
        int finishDocCount = 0;
        // sheet2中简略结果数据
        List<SimilarityOutEntity> sortMaxResultList = new ArrayList<>(allDocAbsolutePath.size());
        // sheet1中详细所有数据
        List<SimilarityOutEntity> detailList = new ArrayList<>(allDocAbsolutePath.size());
        // sheet3中抄袭名单
        List<PlagiarizeEntity> plagiarizeEntityList = new ArrayList<>();
        //将小数格式化为百分数
        NumberFormat numFormat = NumberFormat.getPercentInstance();
        // 遍所有文档信息冒泡原理两两比较文档相似度
        for (int i = 0; i < allDocEntity.size() - 1; i++) {
            // 文档1与其后所有文档的相似度
            List<SimilarityOutEntity> docLeftAllSimList = new ArrayList<>();
            // 文档1
            DocFileEntity docLeft = allDocEntity.get(i);
            for (int j = i + 1; j < allDocEntity.size(); j++) {
                DocFileEntity docRight = allDocEntity.get(j);
                // 比较文本相似度
                
                /*  余弦相似度*/
                double conSim = cosineSimilarity.sim(docLeft.getWordList(), docRight.getWordList());
                double jaccardSim = jaccard.jaccardSimilarity(docLeft.getWordList(), docRight.getWordList());
                double textSim = (conSim + jaccardSim) / 2;
                String judgeResult = "";
                /*  存图片相似度*/
                double avgPicSim = 0;
                /*  存最终结果*/
                double weightedSim = 0;
                if (pictureSimFlag) {
                    
                    // 文档1中每张图片与文档2中所有图片相似度的最大值的集合
                    List<Double> docLeftAllPictureMaxSim = new ArrayList<>(docLeft.getPictureHash().size());
                    for (String hashLeft : docLeft.getPictureHash()) {
                        List<Double> leftDocPictureSimList = new ArrayList<>(docLeft.getPictureHash().size());
                        for (String hashRight : docRight.getPictureHash()) {
                            double pictureSim = SaveHash.getSimilarity(hashLeft, hashRight);
                            leftDocPictureSimList.add(pictureSim);
                            /*  找到某张图相似度超过90%就不再比较后面了，直接比较文档1的下一张图*/
                            if (pictureSim > 0.9) {
                                break;
                            }
                        }
                        // 求出文档1中某张图片与文档2中所有图片相似度的最大值
                        double docLeftPictureKSimMax =
                                leftDocPictureSimList.stream().max(Comparator.comparing(Double::doubleValue)).orElse(0D);
                        docLeftAllPictureMaxSim.add(docLeftPictureKSimMax);
                    }
                    // 求出文档1的所有图片相似度均值作为本次的图片相似度
                    avgPicSim = docLeftAllPictureMaxSim.stream().collect(Collectors.averagingDouble(Double::doubleValue));
                    // 如果任意一个文本图片为空，则总相似度不考虑图片相似度
                    if (docLeft.getPictureHash().isEmpty() && docRight.getPictureHash().isEmpty()) {
                        /*  将文本相似度结果平方，，调整相似度*/
                        weightedSim = (Math.pow(textSim, 1.5) + avgPicSim);
                    } else {
                        /*  将文本相似度结果算1.5次方，，调整相似度*/
                        weightedSim = Math.pow(textSim, 1.5) * 0.6 + avgPicSim * 0.4;
                    }
                } else {
                    // 不计算图片相似度
                    textSim = (conSim + jaccardSim) / 2;
                    /*  将文本相似度结果平方，，调整相似度*/
                    weightedSim = (Math.pow(textSim, 1.5) + avgPicSim);
                }
                
                if (weightedSim > threshold || jaccardSim > 0.90 || conSim > 0.95 || avgPicSim > 0.90) {
                    judgeResult = "存在抄袭可能";
                    //抄袭名单
                    plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(docLeft.getFileName()).build());
                    plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(docRight.getFileName()).build());
                }
                finishDocCount++;
                System.out.println(docLeft.getFileName() + "  与  " + docRight.getFileName() + "\n\tJac相似度为:" + df.format(jaccardSim)
                        + "\n\t余弦相似度为:" + df.format(conSim) + "\n\t图片相似度为:" + df.format(avgPicSim) + "\n\t加权相似度为:"
                        + df.format(weightedSim) + "\n  参考判定:" + judgeResult + "\n还有" + (sumCount - finishDocCount) +
                        "份数据需要比较");
                
                
                SimilarityOutEntity cellSimEntity = SimilarityOutEntity.builder()
                        .judgeResult(judgeResult)
                        .conSim(numFormat.format(conSim))
                        .avgPicSim(numFormat.format(avgPicSim))
                        .jaccardSim(numFormat.format(jaccardSim))
                        .leftDocName(docLeft.getFileName())
                        .weightedSim(numFormat.format(weightedSim))
                        .rightDocName(docRight.getFileName())
                        .build();
                
                docLeftAllSimList.add(cellSimEntity);
            }
            if (allDocEntity.size() < 200) {
                detailList.addAll(docLeftAllSimList);
            }
            
            
            // 找出和文档1最相似的文档，先降序排序
            docLeftAllSimList = docLeftAllSimList.stream().sorted(Comparator.comparing(SimilarityOutEntity::getWeightedSim, Comparator.reverseOrder())).collect(Collectors.toList());
            /*  求出每个文档的最大值，如果最大值有多个，只保留10个*/
            int m = 0;
            for (SimilarityOutEntity similarityOutEntity : docLeftAllSimList) {
                if (m >= 10) {
                    break;
                }
                if (similarityOutEntity.getWeightedSim().equals(docLeftAllSimList.get(0).getWeightedSim())) {
                    /*  加入后期排序*/
                    sortMaxResultList.add(similarityOutEntity);
                    m++;
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
        if (detailList.isEmpty()) {
            SimilarityOutEntity similarityOutEntity =
                    SimilarityOutEntity.builder().judgeResult("本次比较共计" + allDocAbsolutePath.size() + "个文件,详细结果将超过" + +sumCount +
                            "行,防止excel崩溃,此次详细结果不输出,请参考简略结果").build();
            detailList.add(similarityOutEntity);
        }
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
    
    public static DocFileEntity getDocFileName(String path) {
        File docFile = new File(path);
        String name = docFile.getName();
        DocFileEntity docEntity = DocFileEntity.builder()
                .fileName(name)
                .absolutePath(docFile.getAbsolutePath())
                /*  将文件名中空格去除，否则新建文件夹报错*/
                /*  父路径\\无空格无后缀文件名*/
                .pictureParentPath(docFile.getParent() + "\\" + name.replaceAll(" ", "").split("\\.")[0])
                .build();
        return docEntity;
    }
    
    /**
     * 遍历文件夹中的文本文件
     *
     * @param root 遍历的跟路径
     *
     * @return List<String> 存储有所有文本文件绝对路径的字符串数组
     */
    public static List<String> recursionWord(String root) {
        List<String> allDocAbsolutePath = new ArrayList<>();
        File file = new File(root);
        File[] subFile = file.listFiles();
        if (subFile != null) {
            for (File value : subFile) {
                String fileName = value.getName();
                /*  判断是文件还是文件夹*/
                if (value.isDirectory()) {
                    /*  文件夹则递归*/
                    List<String> childPathList = recursionWord(value.getAbsolutePath());
                    allDocAbsolutePath.addAll(childPathList);
                } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".txt")) {
                    /*  绝对路径*/
                    String absolutePath = value.getAbsolutePath();
                    allDocAbsolutePath.add(absolutePath);
                }
            }
        }
        return allDocAbsolutePath;
    }
    
    /**
     * 遍历文件夹中的图片
     *
     * @param root 遍历的跟路径
     *
     * @return 存储有所有文本文件绝对路径的字符串数组
     */
    public static List<String> recursionPhoto(String root) {
        List<String> picturePathList = new ArrayList<>();
        File file = new File(root);
        File[] subFile = file.listFiles();
        if (subFile != null) {
            for (File value : subFile) {
                String fileName = value.getName();
                /*  判断是文件还是文件夹*/
                if (value.isDirectory()) {
                    /*  文件夹则递归*/
                    List<String> childPath = recursionPhoto(value.getAbsolutePath());
                    picturePathList.addAll(childPath);
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg")
                        || fileName.endsWith(".PNG")) {
                    /*  绝对路径*/
                    String absolutePath = value.getAbsolutePath();
                    picturePathList.add(absolutePath);
                }
            }
        }
        return picturePathList;
    }
}
