package pers.hdq.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO
 *
 * @Author: HuDaoquan
 * @Email: hudaoquan@enn.cn
 * @Date: 2022/6/13 12:27
 * @Version 1.0
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@ContentRowHeight(15)
@HeadRowHeight(30)
public class SimilarityOutEntity {
    /**
     * 判定结果
     **/
    @ColumnWidth(15)
    @ExcelProperty(value = "判定结果", index = 0)
    private String judgeResult;
    /**
     * 文件名1
     **/
    @ColumnWidth(25)
    @ExcelProperty(value = "文件名1", index = 1)
    private String leftDocName;
    /**
     * 被比较的文件名
     **/
    @ColumnWidth(25)
    @ExcelProperty(value = "文件名2", index = 2)
    private String rightDocName;
    
    /**
     * jaccard相似度
     **/
    @ColumnWidth(10)
    @ExcelProperty(value = "jaccard相似度", index = 3)
    private String jaccardSim;
    
    /**
     * 余弦相似度
     **/
    @ColumnWidth(10)
    @ExcelProperty(value = "余弦相似度", index = 4)
    private Double conSim;
    /**
     * 图片相似度
     **/
    @ColumnWidth(10)
    @ExcelProperty(value = "图片相似度", index = 5)
    private Double avgPicSim;
    
    /**
     * 加权相似度
     **/
    @ColumnWidth(10)
    @ExcelProperty(value = "加权相似度", index = 6)
    private Double weightedSim;
}
