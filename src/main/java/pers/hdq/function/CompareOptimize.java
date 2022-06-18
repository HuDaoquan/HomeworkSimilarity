package pers.hdq.function;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang.time.DateFormatUtils;
import pers.hdq.model.DocFileEntity;
import pers.hdq.model.PlagiarizeEntity;
import pers.hdq.model.SimilarityOutEntity;
import pers.hdq.picture.SaveHash;
import pers.hdq.similarity.CosineSimilarity;
import pers.hdq.similarity.Jaccard;
import pers.hdq.util.EasyExcelUtil;
import pers.hdq.util.FileUtils;
import pers.hdq.util.IKUtils;
import pers.hdq.util.WordPicture;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    /**
     * 文件读取线程池，核心线程数1，最大线程数4;优先创建线程
     **/
    static ExecutorService fileThreadPool = new ThreadPoolExecutor(4, 6, 50L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100), new ThreadFactoryBuilder().setNameFormat("doc-ini-pool-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());
    
    
   
   /*
   corePoolSize：当在方法execute(Runnable)中提交了一个新任务，并且运行的线程少于 corePoolSize 时，即使其他工作线程处于空闲状态，也会创建一个新线程来处理该请求。
   如果运行的线程数多于 corePoolSize 但少于 maximumPoolSize，则仅当队列已满时才会创建新线程
    maximumPoolSize：最大线程数：线程池中最多允许创建 maximumPoolSize 个线程
    keepAliveTime：存活时间：如果经过 keepAliveTime 时间后，超过核心线程数的线程还没有接受到新的任务，那就回收
    unit：keepAliveTime 的时间单位
    workQueue：存放待执行任务的队列：当提交的任务数超过核心线程数大小后，再提交的任务就存放在这里。它仅仅用来存放被 execute 方法提交的 Runnable 任务
        JDK7提供了7个阻塞队列。分别是：
            ArrayBlockingQueue ：一个由数组结构组成的有界阻塞队列。先进先出（FIFO）
            LinkedBlockingQueue ：一个由链表结构组成的有界阻塞队列。先进先出（FIFO）
            PriorityBlockingQueue ：一个支持优先级排序的无界阻塞队列。
            DelayQueue：基于PriorityQueue实现的支持延时获取元素的阻塞队列。
            SynchronousQueue：一个不存储元素的阻塞队列。
            LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。先进先出（FIFO）
            LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。支持FIFO和FILO两种操作方式

    threadFactory：线程工程：用来创建线程工厂。比如这里面可以自定义线程名称
    handler： 拒绝策略：当队列里面放满了任务、最大线程数的线程都在工作时，这时继续提交的任务线程池就处理不了，应该执行怎么样的拒绝策略。
   //四种拒绝策略
            new ThreadPoolExecutor.AbortPolicy() // 不执行新任务，直接抛出异常，提示线程池已满,默认策略
            new ThreadPoolExecutor.CallerRunsPolicy() // 哪来的去哪里！由调用线程处理该任务
            new ThreadPoolExecutor.DiscardPolicy() //不执行新任务，也不抛出异常
            new ThreadPoolExecutor.DiscardOldestPolicy() //丢弃队列最前面的任务，然后重新提交被拒绝的任务。
    */
    
    
    /**
     * 将小数格式化为百分数
     **/
    static DecimalFormat numFormat = new DecimalFormat("0.00%");
    
    public static void main(String[] args) throws Exception {
        /*  需要查重的路径*/
        String path = "F:\\桌面\\查重图片";
        /*  获取开始时间*/
        long startTime = System.currentTimeMillis();
        String excelPath =
                path + "\\查重结果".concat("智能分词-" + "图片查重-模式2").concat(DateFormatUtils.format(new Date(),
                        "yyyyMMddHHmmss")).concat(".xlsx");
        getSimilarityMode2(path, true, false, 0.5, excelPath);
        /*  获取结束时间*/
        long endTime = System.currentTimeMillis();
        /*  输出程序运行时间*/
        System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s");
    }
    
    
    /**
     * 递归遍历入参path目录下所有文档，并两两比较相似度
     *
     * @param path           需要查重的文件夹
     * @param ikFlag         是否打开智能分词，为false显示最小粒度分词结果
     * @param pictureSimFlag 是否计算文档中图片相似度，为是会增加准确率，但会极大增加运算时间
     * @param threshold      相似度阈值
     * @param excelPath      excel绝对路径
     *
     * @author HuDaoquan
     * @date 2022/6/15 14:50
     **/
    public static void getSimilarityMode1(String path, Boolean ikFlag, Boolean pictureSimFlag,
                                          Double threshold, String excelPath) throws Exception {
        
        System.out.println("开始扫描文档,当前时间:" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        /*  递归遍历目录；获取所有文档绝对路径*/
        List<String> allDocAbsolutePath = recursionWord(path);
        //总计算次数
        int sumCount = (allDocAbsolutePath.size() - 1) * allDocAbsolutePath.size() / 2;
        // 存储所有文档
        List<DocFileEntity> allDocEntityList = Collections.synchronizedList(new ArrayList<>(allDocAbsolutePath.size()));
        
        
        CountDownLatch cdl = new CountDownLatch(allDocAbsolutePath.size());
        //遍历处理所有文件
        for (String s : allDocAbsolutePath) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    allDocEntityList.add(getDocEntity(s, pictureSimFlag, ikFlag));
                    //计数器递减
                    cdl.countDown();
                }
            };
            //执行线程
            fileThreadPool.execute(run);
        }
        
        //线程执行完后再执行主线程
        try {
            cdl.await();
        } catch (InterruptedException e) {
            System.out.println("阻塞子线程中断异常:" + e);
        }
        //关闭线程池
        fileThreadPool.shutdown();
        System.out.println("文档读取完成,开始计算相似度,共计" + allDocAbsolutePath.size() + "个文件,需计算" + sumCount + "次,当前时间:" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        
        
        int detailSize = sumCount;
        if (sumCount > 100000) {
            detailSize = 1;
        }
        // sheet1中详细所有数据
        List<SimilarityOutEntity> detailList = new ArrayList<>(detailSize);
        // sheet2中简略结果数据
        List<SimilarityOutEntity> sortMaxResultList = new ArrayList<>(allDocAbsolutePath.size());
        // sheet3中抄袭名单
        List<PlagiarizeEntity> plagiarizeEntityList = new ArrayList<>();
        // 已经比较过的文档数量
        int finishDocCount = 0;
        
        // 遍所有文档信息冒泡原理两两比较文档相似度
        for (int i = 0; i < allDocEntityList.size() - 1; i++) {
            // 文档1与其后所有文档的相似度
            List<SimilarityOutEntity> docLeftAllSimList = new ArrayList<>();
            // 文档1
            DocFileEntity docLeft = allDocEntityList.get(i);
            for (int j = i + 1; j < allDocEntityList.size(); j++) {
                // 被比较文本
                DocFileEntity docRight = allDocEntityList.get(j);
                // 比较文本相似度
                SimilarityOutEntity cellSimEntity = comparingTwoDoc(docLeft, docRight, pictureSimFlag, threshold, plagiarizeEntityList, sumCount, finishDocCount);
                finishDocCount = cellSimEntity.getFinishDocCount();
                docLeftAllSimList.add(cellSimEntity);
            }
            if (sumCount <= 100000) {
                // 相似度实体加到详细结果中
                detailList.addAll(docLeftAllSimList);
            }
            // 找出和文档1最相似的文档，先降序排序
            docLeftAllSimList =
                    docLeftAllSimList.stream().sorted(Comparator.comparing(SimilarityOutEntity::getWeightedSimDouble,
                            Comparator.reverseOrder())).collect(Collectors.toList());
            /*  求出每个文档的最大值，如果最大值有多个，只保留10个*/
            int m = 0;
            for (SimilarityOutEntity similarityOutEntity : docLeftAllSimList) {
                if (m >= 10) {
                    break;
                }
                if (similarityOutEntity.getWeightedSimDouble().equals(docLeftAllSimList.get(0).getWeightedSimDouble())) {
                    /*  将相似度实体加入简略结果*/
                    sortMaxResultList.add(similarityOutEntity);
                    m++;
                }
            }
        }
        // 排序并导出excel
        sortAndImportExcel(excelPath, sumCount, detailList, sortMaxResultList, plagiarizeEntityList);
    }
    
    /**
     * 将几个sheet表数据排序去重并输出excel
     *
     * @param excelPath            excel绝对路径
     * @param sumCount             总计算次数
     * @param detailList           详细名单
     * @param sortMaxResultList    简略名单
     * @param plagiarizeEntityList 抄袭名单
     *
     * @author HuDaoquan
     * @date 2022/6/15 14:14
     **/
    private static void sortAndImportExcel(String excelPath, int sumCount, List<SimilarityOutEntity> detailList, List<SimilarityOutEntity> sortMaxResultList, List<PlagiarizeEntity> plagiarizeEntityList) {
        
        // 排序详细结果
        detailList = detailList.stream().sorted(Comparator.comparing(SimilarityOutEntity::getWeightedSimDouble, Comparator.reverseOrder())).collect(Collectors.toList());
        // 排序简略结果
        sortMaxResultList = sortMaxResultList.stream().sorted(Comparator.comparing(SimilarityOutEntity::getWeightedSimDouble, Comparator.reverseOrder())).collect(Collectors.toList());
        // 去重抄袭名单
        plagiarizeEntityList = plagiarizeEntityList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PlagiarizeEntity::getDocName))), ArrayList::new));
        
        if (detailList.isEmpty()) {
            SimilarityOutEntity similarityOutEntity =
                    SimilarityOutEntity.builder().judgeResult("本次比较详细结果将超过" + sumCount + "行,防止excel崩溃,此次详细结果不输出,请参考简略结果").build();
            detailList.add(similarityOutEntity);
        }
        System.out.println("相似度计算完成,开始导出excel文件,当前时间:" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        EasyExcelUtil.writeExcel(excelPath, detailList, sortMaxResultList, plagiarizeEntityList);
        System.err.println("相似度计算结果已存入：" + excelPath);
    }
    
    /**
     * 查重方式2：今年的文档两两比较，今年的与往年的比较；往年的互相之间不需要比较
     *
     * @param path           待查重文件夹
     * @param ikFlag         ik智能分词开关
     * @param pictureSimFlag 图片相似度开关
     * @param threshold      重复度判定阈值
     * @param excelPath      导出的excel绝对路径
     *
     * @author HuDaoquan
     * @date 2022/6/15 13:15
     **/
    public static void getSimilarityMode2(String path, Boolean ikFlag, Boolean pictureSimFlag,
                                          Double threshold, String excelPath) throws Exception {
        System.out.println("开始扫描文档,当前时间:" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        /*  递归遍历目录；获取所有今年文档绝对路径*/
        List<String> thisYearDocAbsolutePath = recursionWord(path + "\\今年");
        // 往年文档路径
        List<String> historyYearDocAbsolutePath = recursionWord(path + "\\往年");
        //总计算次数
        int sumCount =
                (thisYearDocAbsolutePath.size() - 1) * thisYearDocAbsolutePath.size() / 2 + thisYearDocAbsolutePath.size() * historyYearDocAbsolutePath.size();
        
        // 存储今年文档
        List<DocFileEntity> thisYearDocEntityList =
                Collections.synchronizedList(new ArrayList<>(thisYearDocAbsolutePath.size()));
        // 存储往年文档
        List<DocFileEntity> historyYearDocEntityList =
                Collections.synchronizedList(new ArrayList<>(historyYearDocAbsolutePath.size()));
        // 线程计数器
        CountDownLatch thisYearCdl = new CountDownLatch(thisYearDocAbsolutePath.size());
        //遍历处理所有今年文档
        for (String s : thisYearDocAbsolutePath) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    //获取今年文档实体
                    thisYearDocEntityList.add(getDocEntity(s, pictureSimFlag, ikFlag));
                    //计数器递减
                    thisYearCdl.countDown();
                }
            };
            //执行线程
            fileThreadPool.execute(run);
        }
        
        CountDownLatch historyCdl = new CountDownLatch(historyYearDocAbsolutePath.size());
        // 遍历处理所有往年文档
        for (String s : historyYearDocAbsolutePath) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    //获取往年文档实体
                    historyYearDocEntityList.add(getDocEntity(s, pictureSimFlag, ikFlag));
                    //计数器递减
                    historyCdl.countDown();
                }
            };
            //执行线程
            fileThreadPool.execute(run);
        }
        
        
        //线程执行完后再执行主线程
        try {
            thisYearCdl.await();
            historyCdl.await();
        } catch (InterruptedException e) {
            System.out.println("阻塞子线程中断异常:" + e);
        }
        System.out.println("今年文档数量:" + thisYearDocEntityList.size());
        System.out.println("往年文档数量:" + historyYearDocEntityList.size());
        //关闭线程池
        fileThreadPool.shutdown();
        System.out.println("开始计算相似度,需计算" + sumCount + "次,当前时间:" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        // 详情名单初始长度
        int detailSize = sumCount;
        if (sumCount > 100000) {
            detailSize = 1;
        }
        // sheet1中详细所有数据
        List<SimilarityOutEntity> detailList = new ArrayList<>(detailSize);
        // sheet2中简略结果数据
        List<SimilarityOutEntity> sortMaxResultList = new ArrayList<>(thisYearDocEntityList.size());
        // sheet3中抄袭名单
        List<PlagiarizeEntity> plagiarizeEntityList = new ArrayList<>();
        
        // 已经比较过的文档数量
        int finishDocCount = 0;
        // 冒泡排序原理遍历比较文件，遍所有文档信息冒泡原理两两比较文档相似度
        for (int i = 0; i < thisYearDocEntityList.size(); i++) {
            // 文档1与其他被比较的所有文档的相似度
            List<SimilarityOutEntity> docLeftAllSimList = new ArrayList<>();
            // 文档1
            DocFileEntity docLeft = thisYearDocEntityList.get(i);
            //今年的文档
            for (int j = i + 1; j < thisYearDocEntityList.size(); j++) {
                //被比较文档
                DocFileEntity docRight = thisYearDocEntityList.get(j);
                // 比较两个文档相似度，返回相似度实体
                SimilarityOutEntity cellSimEntity = comparingTwoDoc(docLeft, docRight, pictureSimFlag, threshold, plagiarizeEntityList, sumCount, finishDocCount);
                finishDocCount = cellSimEntity.getFinishDocCount();
                docLeftAllSimList.add(cellSimEntity);
            }
            //往年文档
            for (int j = 0; j < historyYearDocEntityList.size(); j++) {
                //被比较文档
                DocFileEntity docRight = historyYearDocEntityList.get(j);
                // 比较两个文档相似度，返回相似度实体
                SimilarityOutEntity cellSimEntity = comparingTwoDoc(docLeft, docRight, pictureSimFlag, threshold, plagiarizeEntityList, sumCount, finishDocCount);
                finishDocCount = cellSimEntity.getFinishDocCount();
                docLeftAllSimList.add(cellSimEntity);
            }
            if (sumCount <= 100000) {
                detailList.addAll(docLeftAllSimList);
            }
            // 找出和文档1最相似的文档，先降序排序
            docLeftAllSimList = docLeftAllSimList.stream().sorted(Comparator.comparing(SimilarityOutEntity::getWeightedSimDouble, Comparator.reverseOrder())).collect(Collectors.toList());
            /*  求出每个文档的最大值，如果最大值有多个，只保留10个*/
            int m = 0;
            for (SimilarityOutEntity similarityOutEntity : docLeftAllSimList) {
                if (m >= 10) {
                    break;
                }
                if (similarityOutEntity.getWeightedSimDouble().equals(docLeftAllSimList.get(0).getWeightedSimDouble())) {
                    /*  加入后期排序*/
                    sortMaxResultList.add(similarityOutEntity);
                    m++;
                }
            }
        }
        
        sortAndImportExcel(excelPath, sumCount, detailList, sortMaxResultList, plagiarizeEntityList);
    }
    
    /**
     * 比较两个文档的相似度，返回相似度实体
     *
     * @param docLeft              文档1
     * @param docRight             文档2
     * @param pictureSimFlag       图片相似度
     * @param threshold            相似度判定阈值
     * @param plagiarizeEntityList 抄袭名单
     * @param sumCount             总比较次数
     * @param finishDocCount       已比较次数
     *
     * @return {@link SimilarityOutEntity} 计算得到的相似度实体
     * @author HuDaoquan
     * @date 2022/6/15 13:38
     **/
    public static SimilarityOutEntity comparingTwoDoc(DocFileEntity docLeft, DocFileEntity docRight, Boolean pictureSimFlag,
                                                      Double threshold, List<PlagiarizeEntity> plagiarizeEntityList, int sumCount,
                                                      int finishDocCount) {
        
        /*  余弦相似度*/
        double conSim = CosineSimilarity.sim(docLeft.getWordList(), docRight.getWordList());
        // // 杰卡德相似度
        double jaccardSim = Jaccard.jaccardSimilarity(docLeft.getWordList(), docRight.getWordList());
        double textSim = (conSim + jaccardSim) / 2;
        // 判断结果
        String judgeResult = "";
        /*  存图片相似度*/
        double avgPicSim = 0D;
        /*  存最终加权相似度*/
        double weightedSim;
        if (pictureSimFlag) {
            // 文档1中每张图片与文档2中所有图片相似度的最大值的集合
            List<Double> docLeftAllPictureMaxSim = new ArrayList<>(docLeft.getPictureHashList().size());
            for (String hashLeft : docLeft.getPictureHashList()) {
                List<Double> leftDocPictureSimList = new ArrayList<>(docLeft.getPictureHashList().size());
                for (String hashRight : docRight.getPictureHashList()) {
                    double pictureSim = SaveHash.getSimilarity(hashLeft, hashRight);
                    leftDocPictureSimList.add(pictureSim);
                    /*  找到某张图相似度超过90%就不再比较后面了，直接比较文档1的下一张图*/
                    if (pictureSim > 0.9) {
                        break;
                    }
                }
                // 求出文档1中某张图片与文档2中所有图片相似度的最大值
                double docLeftOnePictureSimMax =
                        leftDocPictureSimList.stream().max(Comparator.comparing(Double::doubleValue)).orElse(0D);
                docLeftAllPictureMaxSim.add(docLeftOnePictureSimMax);
            }
            // 求出文档1的所有图片相似度均值作为本次的图片相似度
            avgPicSim = docLeftAllPictureMaxSim.stream().collect(Collectors.averagingDouble(Double::doubleValue));
            // 如果任意一个文本图片为空，则总相似度不考虑图片相似度
            if (docLeft.getPictureHashList().isEmpty() && docRight.getPictureHashList().isEmpty()) {
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
        
        if (weightedSim > threshold || jaccardSim > 0.90 || conSim > 0.90 || avgPicSim > 0.90) {
            judgeResult = "疑似抄袭";
            //抄袭名单
            plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(docLeft.getAbsolutePath()).build());
            plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(docRight.getAbsolutePath()).build());
        }
        finishDocCount++;
        System.out.println(docLeft.getFileName() + "  与  " + docRight.getFileName() + "\n\tJac相似度为:" + numFormat.format(jaccardSim)
                + "\n\t余弦相似度为:" + numFormat.format(conSim) + "\n\t图片相似度为:" + numFormat.format(avgPicSim) + "\n\t加权相似度为:"
                + numFormat.format(weightedSim) + "\n  参考判定:" + judgeResult + "\n还有" + (sumCount - finishDocCount) +
                "份数据需要比较");
        
        return SimilarityOutEntity.builder()
                .judgeResult(judgeResult)
                .conSim(numFormat.format(conSim))
                .avgPicSim(numFormat.format(avgPicSim))
                .jaccardSim(numFormat.format(jaccardSim))
                .leftDocName(docLeft.getAbsolutePath())
                .weightedSim(numFormat.format(weightedSim))
                .rightDocName(docRight.getAbsolutePath())
                .weightedSimDouble(weightedSim)
                .finishDocCount(finishDocCount)
                .build();
        
        
    }
    
    /**
     * 传入文档绝对路径，返回文档实体（绝对路径、图片路径、分词结果、图片hash结果等）
     *
     * @param path           文档绝对路径
     * @param pictureSimFlag 是否处理图片
     * @param ikFlag         ik智能分词开关
     *
     * @return {@link DocFileEntity}返回文档实体（绝对路径、图片路径、分词结果、图片hash结果等）
     * @author HuDaoquan
     * @date 2022/6/15 13:10
     **/
    public static DocFileEntity getDocEntity(String path, Boolean pictureSimFlag, Boolean ikFlag) {
        File docFile = new File(path);
        String name = docFile.getName();
        DocFileEntity docEntity = DocFileEntity.builder()
                .fileName(name)
                .absolutePath(docFile.getAbsolutePath())
                /*  父路径\\无空格无后缀文件名*/
                // .pictureParentPath(docFile.getParent() + "\\" + name.replaceAll(" ", "").split("\\.")[0])
                .build();
        //将每个文档的文本分词后返回,去除数字和字母，使用IK分词器分词
        docEntity.setWordList(IKUtils.segStr(FileUtils.readFile(path).replaceAll("[0-9a-zA-Z]", ""), ikFlag));
        // 比较图片相似度
        if (pictureSimFlag) {
            // 计算文档中图片的hash指纹
            List<String> oneDocPictureHashList = WordPicture.getWordPicture(docEntity);
            docEntity.setPictureHashList(oneDocPictureHashList);
            System.out.println(docEntity.getFileName() + "的图片数量为:" + oneDocPictureHashList.size());
        }
        return docEntity;
    }
    
    /**
     * 遍历文件夹中的文本文件
     *
     * @param root 遍历的跟路径
     *
     * @return List<String> 存储有所有文本文件绝对路径的字符串数组
     */
    public static List<String> recursionWord(String root) throws Exception {
        List<String> allDocAbsolutePathList = new ArrayList<>();
        File file = new File(root);
        if (!file.exists()) {
            throw new Exception("文件夹不存在:" + root);
        }
        File[] subFile = file.listFiles();
        if (subFile != null) {
            for (File value : subFile) {
                String fileName = value.getName();
                /*  判断是文件还是文件夹*/
                if (value.isDirectory()) {
                    /*  文件夹则递归*/
                    List<String> childPathList = recursionWord(value.getAbsolutePath());
                    allDocAbsolutePathList.addAll(childPathList);
                } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".txt")) {
                    /*  绝对路径*/
                    String absolutePath = value.getAbsolutePath();
                    allDocAbsolutePathList.add(absolutePath);
                }
            }
        }
        return allDocAbsolutePathList;
    }
    
}
