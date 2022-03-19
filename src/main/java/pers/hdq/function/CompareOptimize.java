package pers.hdq.function;

import org.apache.commons.lang.time.DateFormatUtils;
import pers.hdq.ik.IKWordSegmentation;
import pers.hdq.picture.SaveHash;
import pers.hdq.similarity.CosineSimilarity;
import pers.hdq.similarity.Jaccard;
import pers.hdq.traverse.FileUtils;
import pers.hdq.traverse.WordPicture;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class CompareOptimize {
    public static void main(String[] args) {
        String path = "D:\\我的文档\\桌面\\测"; // 需要查重的路径
        long startTime = System.currentTimeMillis(); // 获取开始时间
        CompareOptimize com = new CompareOptimize();
        System.err.println("相似度计算结果已存入：" + com.calculateFileSimilarity(path, false, false, false, 0.9));
        long endTime = System.currentTimeMillis(); // 获取结束时间
        System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s"); // 输出程序运行时间
    }
    
    /**
     * @param path          需要查重的文件夹
     * @param IntelligentIK 是否打开智能分词，为false显示最小粒度分词结果
     * @param pictureSim    是否计算文档中图片相似度，为是会增加准确率，但会极大增加运算时间
     * @param isSort        是否排序输出，为是将多出25%的运算时间。
     * @param simThre       相似度阈值
     *
     * @return 返回写入比较结果的csv文件的路径
     */
    
    public String calculateFileSimilarity(String path, Boolean IntelligentIK, Boolean pictureSim, Boolean isSort,
                                          Double simThre) {
        SaveHash SH = new SaveHash(); // Phash算法修改
        Map<String, String> savePicHash = new TreeMap<String, String>(); // 存储所有图片Hash指纹
        FileUtils F = new FileUtils(); // 文件读取
        CosineSimilarity cs = new CosineSimilarity(); // 余弦相似度
        Jaccard J = new Jaccard(); // Jaccard相似度
        DecimalFormat df = new DecimalFormat("0.00%");
//		CsvFilePrinter CP = new CsvFilePrinter(); // 写csv
        WriteExcel WE = new WriteExcel();
        WordPicture WP = new WordPicture(); // 读取Word中的图片类
        IKWordSegmentation IK = new IKWordSegmentation();

//		String csvFilePath = path
//				+ "\\文本相似度".concat(IntelligentIK.toString() + "智能分词-" + pictureSim.toString() + "图片查重")
//						.concat(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).concat(".csv");// csv路径
//		String csvePath = path + "\\疑似抄袭名单".concat(IntelligentIK.toString() + "智能分词-" + pictureSim.toString() + "图片查重")
//				.concat(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).concat(".csv");// csv路径
        
        String ExcelPath = path + "\\查重结果".concat(IntelligentIK.toString() + "智能分词-" + pictureSim.toString() + "图片查重")
                .concat(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).concat(".xls");// csv路径
        List<Result> results = new ArrayList<Result>(); // 将结果类存入List
        Set<String> simName = new HashSet<String>(); // 抄袭名单
        Vector<String> vecFile = new Vector<String>(); // 存储文档绝对路径集合
        Vector<String> vs = recursionWord(path, vecFile); // 将遍历了的文档 绝对路径存入数组，方便调用
        int suma = (vs.size() - 1) * vs.size() / 2;
        // 比较图片相似度
        if (pictureSim) {
            for (int f = 0; f < vs.size(); f++) {
                WP.readWordPicture(vs.get(f)); // 将图片写入本地文档
            }
            Vector<String> vecFilePicHash1 = new Vector<String>(); // 存储文档中的图片绝对路径集合
            Vector<String> vsPicHash1 = recursionPhoto(path, vecFilePicHash1); // 将遍历了的图片绝对路径存入
            System.out.println("\n正在处理图片，请稍后");
            for (int d = 0; d < vsPicHash1.size(); d++) {
                File file = new File(vsPicHash1.get(d));
                String picAbsolutePath = file.getAbsolutePath();
                String hashValues = SH.getFeatureValue(vsPicHash1.get(d));
                savePicHash.put(picAbsolutePath, hashValues);
            }
            System.out.println("总共有" + vs.size() + "个文件" + vsPicHash1.size() + "张图片，常规最多需要3-5分钟");
        } else {
            System.out.println("总共有" + vs.size() + "个文件，需要比较" + suma + "次" + "\n大概需要" + suma / 30 + "秒");
        }
        System.out.println("开始计算文本相似度");
        // 冒泡排序原理遍历比较文件
        int dex = 1;
        List<Result> sortMaxRList = new ArrayList<Result>();
        for (int i = 0; i < vs.size() - 1; i++) {
            List<Double> simMax = new ArrayList<Double>();
            List<Result> tempRList = new ArrayList<Result>();
            for (int j = i + 1; j < vs.size(); j++) {
                // 比较文本相似度
                String str1 = F.readFile(vs.get(i));// 读取文件，返回文本字符串
                String str2 = F.readFile(vs.get(j));
                str1 = str1.replaceAll("[0-9a-zA-Z]", ""); // 去除数字和字母
                str2 = str2.replaceAll("[0-9a-zA-Z]", "");
                List<String> list1 = IK.segStr(str1, IntelligentIK); // 使用IK分词器分词
                List<String> list2 = IK.segStr(str2, IntelligentIK);
                double conSim = cs.sim(list1, list2); // 余弦相似度
                double JaccSim = J.jaccardSimilarity(list1, list2);
                Result R = new Result();// 每次都是新的结果
                String fileName1 = getFileName(vs.get(i))[0];
                String fileName2 = getFileName(vs.get(j))[0];
                String crib = "";
                double avgPicSim = 0;// 存图片相似度的
                double sim = 0; // 存最终结果
                if (pictureSim) {
//					System.out.println("getFileName(vs.get(i))[1]---------" + getFileName(vs.get(i))[1]);
                    Vector<String> vecFilePic1 = new Vector<String>(); // 存储文档i的图片绝对路径集合
                    Vector<String> vsPic1 = recursionPhoto(getFileName(vs.get(i))[1], vecFilePic1); // 将遍历了的tupian
                    // 绝对路径存入数组，方便调用
                    Vector<String> vecFilePic2 = new Vector<String>(); // 存储文档j的图片绝对路径集合
                    Vector<String> vsPic2 = recursionPhoto(getFileName(vs.get(j))[1], vecFilePic2); // 将遍历了的tupian
                    // 绝对路径存入数组，方便调用
                    List<Double> lt2 = new ArrayList<>();
//					System.out.println("getFileName(vs.get(j))[1]---------" + getFileName(vs.get(j))[1]);
//					System.err.println(fileName1 + "  " + vsPic1.size());
                    for (int k = 0; k < vsPic1.size(); k++) {
//						System.err.println(fileName2 + "  " + vsPic2.size());
                        List<Double> lt1 = new ArrayList<>();
                        for (int s = 0; s < vsPic2.size(); s++) {
                            double picsim;
                            String hash1 = savePicHash.get(vsPic1.get(k));
                            String hash2 = savePicHash.get(vsPic2.get(s));
                            picsim = SH.getSimilarity(hash1, hash2);
                            lt1.add(picsim);
                            if (picsim > 0.9) // 找到某张图相似度超过90%就不再比较后面了
							{
								break;
							}
                        }
                        // 求出最大值
//						System.out.println(lt1.size());
                        double max = 0;
                        if (!lt1.isEmpty()) {
                            max = lt1.get(0);
                            // System.out.println(lt1.size());
                            for (int c = 1; c < lt1.size(); c++) {
                                if (max < lt1.get(c)) {
                                    max = lt1.get(c);
                                }
                            }
                        }
                        lt2.add(max);
                    }
                    // 求出图片相似度均值
                    double sum = 0;
                    for (int d = 0; d < lt2.size(); d++) {
                        sum += lt2.get(d);
                    }
                    if (lt2.size() == 0) {
                        avgPicSim = 0;
                    } else {
                        avgPicSim = sum / lt2.size();// 图片相似度
                    }
//				System.out.println("avgPicSim==="+avgPicSim);
                    double txtsim = (conSim + JaccSim) / 2;
                    if (vsPic2.isEmpty() && vsPic1.isEmpty()) {
                        sim = (Math.pow(txtsim, 1.5) + avgPicSim);// 将文本相似度结果平方，，调整相似度
                    } else {
                        sim = Math.pow(txtsim, 1.5) * 0.6 + avgPicSim * 0.4;// 将文本相似度结果算1.5次方，，调整相似度
                    }
                } else {
                    // 不计算图片相似度
                    double txtsim = (conSim + JaccSim) / 2;
                    sim = (Math.pow(txtsim, 1.5) + avgPicSim);// 将文本相似度结果平方，，调整相似度
                }
                simMax.add(sim);
                if (sim > simThre || JaccSim > 0.90 || conSim > 0.95 || avgPicSim > 0.90) {
                    crib = "高度抄袭";
                }
                if (!isSort) {
                    System.out.println(fileName1 + "  与  " + fileName2 + "\n\tJac相似度为:" + df.format(JaccSim)
                            + "\n\t余弦相似度为:" + df.format(conSim) + "\n\t图片相似度为:" + df.format(avgPicSim) + "\n\t加权相似度为:"
                            + df.format(sim) + "\n  参考判定:" + crib + "\n还有" + (suma - dex) + "份数据需要比较");
                    dex++;
                    // 写入csv的内容
//					String[] values = { crib, fileName1, fileName2, conSim + "", JaccSim + "", avgPicSim + "",
//							sim + "" };//写CSV
                    List<Object> valuesList = new ArrayList<>();
                    valuesList.add(crib);
                    valuesList.add(fileName1);
                    valuesList.add(fileName2);
                    valuesList.add(conSim);
                    valuesList.add(JaccSim);
                    valuesList.add(avgPicSim);
                    valuesList.add(sim);
                    try {
//						CP.CsvWrite(csvFilePath, values, true);
                        WE.writeEXCEL(ExcelPath, "详细结果", valuesList);
                        valuesList.clear();
                    } catch (IOException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }
                }
                R.setName1(fileName1);
                R.setName2(fileName2);
                R.setConSim(conSim);
                R.setJaccardSim(JaccSim);
                R.setAvgPicSim(avgPicSim);
                R.setSim(sim);
                R.setCrib(crib);
                results.add(R);
                tempRList.add(R);
            }
            for (Result p : results) {
                if (getFileName(vs.get(i))[0].equals(p.getName2())) {
                    // 比如第5份文档，要加入1-4与5的比较结果；交换文件名变成5与1-4比较，防止重复值干扰
                    String temp = p.getName1();
                    p.setName1(p.getName2());
                    p.setName2(temp);
                    tempRList.add(p);
                }
            }
            
            Collections.sort(tempRList);
//			Result rTemp = tempRList.get(0); // 求出每个人的最大值
            
            for (Result rTemp : tempRList) {
//					System.err.println(rTemp.getSim() + "  " + tempRList.get(0).getSim() + "  "
//							+ (rTemp.getSim() == tempRList.get(0).getSim()) + "  ---   "
//							+ (rTemp.getSim().equals(tempRList.get(0).getSim())));
                if (rTemp.getSim().equals(tempRList.get(0).getSim())) {
                    sortMaxRList.add(rTemp); // 加入后期排序
                    if (!isSort) {
                        List<Object> valuesList = new ArrayList<>();
                        valuesList.add(rTemp.getCrib());
                        valuesList.add(rTemp.getName1());
                        valuesList.add(rTemp.getName2());
                        valuesList.add(rTemp.getConSim());
                        valuesList.add(rTemp.getJaccardSim());
                        valuesList.add(rTemp.getAvgPicSim());
                        valuesList.add(rTemp.getSim());
                        try {
//					CP.CsvWrite(csvFilePath, values, true);
                            WE.writeEXCEL(ExcelPath, "简略结果", valuesList);
                        } catch (IOException e) {
                            // TODO 自动生成的 catch 块
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        // 不要手残跑来优化，排序的输出要等所有循环结束才可以！！
        
        if (isSort) {
            // 排序简略结果
            Collections.sort(sortMaxRList);
            System.out.println("输出" + sortMaxRList.size());
            for (int b = 0; b < sortMaxRList.size(); b++) {
                List<Object> valuesList = new ArrayList<>();
                valuesList.add(sortMaxRList.get(b).getCrib());
                valuesList.add(sortMaxRList.get(b).getName1());
                valuesList.add(sortMaxRList.get(b).getName2());
                valuesList.add(sortMaxRList.get(b).getConSim());
                valuesList.add(sortMaxRList.get(b).getJaccardSim());
                valuesList.add(sortMaxRList.get(b).getAvgPicSim());
                valuesList.add(sortMaxRList.get(b).getSim());
                try {
//					CP.CsvWrite(csvFilePath, values, true);
                    
                    WE.writeEXCEL(ExcelPath, "简略结果", valuesList);
                } catch (IOException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
            
            // 排序详细结果
            Collections.sort(results);
            for (Result p : results) {
                System.err
                        .println(p.getName1() + " 与 " + p.getName2() + "\n\tJaccard相似度为：" + df.format(p.getJaccardSim())
                                + "\n\t余弦相似度为：" + df.format(p.getConSim()) + "\n\t图片相似度为：" + df.format(p.getAvgPicSim())
                                + "\n\t加权相似度为：" + df.format(p.getSim()) + "\n抄袭判定：" + p.getCrib());
                // 写入本地的内容
//				String[] values = { p.getCrib(), p.getName1(), p.getName2(), p.getConSim() + "", p.getJaccardSim() + "",
//						p.getAvgPicSim() + "", p.getSim() + "" };//写CSV
                List<Object> valuesList = new ArrayList<>();
                valuesList.add(p.getCrib());
                valuesList.add(p.getName1());
                valuesList.add(p.getName2());
                valuesList.add(p.getConSim());
                valuesList.add(p.getJaccardSim());
                valuesList.add(p.getAvgPicSim());
                valuesList.add(p.getSim());
                try {
//					CP.CsvWrite(csvFilePath, values, true);
                    WE.writeEXCEL(ExcelPath, "详细结果", valuesList);
                    valuesList.clear();
                } catch (IOException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
            
        }
//提取抄袭名单
        for (Result p : results) {
            if (p.getSim() > simThre || p.getJaccardSim() > 0.90 || p.getConSim() > 0.95 || p.getAvgPicSim() > 0.90) {
                simName.add(p.getName1());
                simName.add(p.getName2());
            }
        }
        for (String str : simName) {
            List<Object> valuesList = new ArrayList<>();
            valuesList = Arrays.asList(str);
            try {
//				CP.CsvWriteName(csvePath, str, true);
                WE.writeEXCEL(ExcelPath, "抄袭名单", valuesList);
//
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        System.err.println("疑似抄袭名单:" + simName.toString());
        return ExcelPath;
    }
    
    /**
     * 绝对路径中提取文件名,注意FN[1]的获取要与图片处理模块中的新建文件夹路径的获取方法一致！
     */
    public String[] getFileName(String path) {
        File f = new File(path);
//		String Fname = f.getName()//.replaceAll(" ", ""); // 将双空格替换为“-”
        String fn = f.getName().replaceAll(" ", ""); // 空格必须替换，否则新建文件夹报错
        // 获取文件名
        String temp[] = fn.split("\\.");
        String fname = "\\" + temp[0];
        String pf = f.getParent();// 父路径
        String[] FN = {fn, pf + fname};// 拼接好，方便遍历用
//		System.err.println("---" + Fname);
        return FN;
    }
    
    /**
     * 遍历文件夹中的文本文件
     *
     * @param root 遍历的跟路径
     *
     * @return 存储有所有文本文件绝对路径的字符串数组
     */
    public Vector<String> recursionWord(String root, Vector<String> vecFile) {
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
    
    /**
     * 遍历文件夹中的图片
     *
     * @param root 遍历的跟路径
     *
     * @return 存储有所有文本文件绝对路径的字符串数组
     */
    public Vector<String> recursionPhoto(String root, Vector<String> vecFile) {
        File file = new File(root);
        File[] subFile = file.listFiles();
        for (int i = 0; i < subFile.length; i++) {
            String fileName = subFile[i].getName();
            if (subFile[i].isDirectory()) { // 判断是文件还是文件夹
                recursionPhoto(subFile[i].getAbsolutePath(), vecFile); // 文件夹则递归
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg")
                    || fileName.endsWith(".PNG")) {
                String AP = subFile[i].getAbsolutePath();// 绝对路径
//				System.out.println(AP);
                vecFile.add(AP);
            }
        }
        return vecFile;
    }
}
