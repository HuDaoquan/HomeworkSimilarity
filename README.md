# HomeworkSimilarity

## 项目描述:

本地作业查重系统。程序入口文件为src/main/java/pers.hdq.ui包中的UIhdq.java 文件。这是一个图形化界面。
**如果下载后运行发现报ik配置项空指针,尝试将lib目录设置为资源目录**,如下图
![img.png](img.png)

实现对本地某一目录下所有word文档和txt文档进行两两之间的相似度计算。并将完整结果简略结果以及抄袭名单输出为excel文件。  
图片相似度采用PHash算法，文字相似度采用jaccard相似度和余弦相似度结合进行计算。  
程序已经生成了32位和64位exe文件，直接解压后选择作业查重x32和作业查重x64文件夹即可运行（下载链接：链接: https://pan.baidu.com/s/1PA1x786sXzsr0J4cJI5z-A 提取码: umfm）。

详细图文说明也在上面的百度网盘链接中，PPT初步了解，文档3,4章详细介绍系统设计和实现。

**release分支为最新分支**,已合并dev1.0.2分支

### 提示:

    查询多个大文本时,请在jvm启动参数中调大内存限制 -Xmx4096m -Xms1024m   (具体数值依据电脑配置决定)

## 版本变更记录:

### dev1.0.2 更新时间2022-06-15

> 1.将ik停止词由读取文件变为set初始化,提高效率
>
> 2.删除冗余代码
>
> 3.新增查重模式,模式1即所选目录所有文档两两比较;模式2要求所选目录中必须有一个"今年"文件夹存今年的文档,一个"往年"文件夹存往年的文档;今年文档两两比较,然后再将今年文档与往年文档分别比较;减少往年文档互相比较的过程.

### dev1.0.1 更新时间2022-06-14

> 1.本次重构文件处理流程,每个文件只打开一次,效率极大提高(1000个文档从1个小时提高到1分钟不到)
>
> 2.当详细结果超过20万时,详细结果不导出到excel中
>
> 3.当某个文档和其他文档最大相似度有多个时,简略结果只保留这个文档的10个最相似文档

### dev1.0.0 更新时间2022-06-13

> 1.本次将导出excel的方法换成阿里easyExcel库,效率极大提高(150万行数据很快便导出完成)

