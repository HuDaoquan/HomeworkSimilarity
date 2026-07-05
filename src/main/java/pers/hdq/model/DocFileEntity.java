package pers.hdq.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 文本类
 *
 * @Author: HuDaoquan
 * @Email: 1455523026@qq.com
 * @Date: 2022/6/14 14:25
 * @Version 1.0
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class DocFileEntity {
    /**
     * 绝对路径
     **/
    private String absolutePath;
    /**
     * 文件名
     **/
    private String fileName;
    
    /**
     * 本文所有图片的父路径
     **/
    // private String pictureParentPath;
    
    /**
     * 分词结果
     **/
    private List<String> wordList;
    
    /**
     * 图片hash指纹
     **/
    private List<String> pictureHashList;
    
    /**
     * 原始文本按句子拆分的结果，用于标记重复内容
     **/
    private String[] sentences;
    
    /**
     * 原始完整文本（未分词），用于定位重复片段
     **/
    private String rawText;
    
}
