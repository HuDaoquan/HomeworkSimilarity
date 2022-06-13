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
 * 抄袭名单
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
public class PlagiarizeEntity {
    /**
     * 抄袭文件名
     **/
    @ColumnWidth(15)
    @ExcelProperty(value = "抄袭文件名", index = 0)
    private String docName;
    
    
}
