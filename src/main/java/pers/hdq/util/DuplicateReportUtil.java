package pers.hdq.util;

import pers.hdq.model.SimilarityOutEntity;
import pers.hdq.similarity.ParagraphSimilarity;
import pers.hdq.similarity.ParagraphSimilarity.DuplicateFragment;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateReportUtil {

    public static void generateHtmlReport(String outputPath,
                                          String doc1Path, String doc1Name, String[] doc1Sentences,
                                          String doc2Path, String doc2Name, String[] doc2Sentences,
                                          List<DuplicateFragment> fragments) {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "UTF-8"))) {
            pw.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            pw.println("<title>重复内容标记报告</title>");
            pw.println("<style>");
            pw.println("body { font-family: 'Microsoft YaHei', sans-serif; margin: 20px; }");
            pw.println(".highlight { background-color: #FFF176; padding: 2px 4px; border-radius: 3px; color: #333; }");
            pw.println(".high-sim { background-color: #FFCDD2; border-left-color: #E53935; color: #B71C1C; }");
            pw.println(".medium-sim { background-color: #FFF9C4; border-left-color: #FF9800; color: #333; }");
            pw.println(".doc-section { margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 8px; }");
            pw.println(".doc-title { font-size: 18px; font-weight: bold; color: #333; margin-bottom: 10px; }");
            pw.println(".fragment-list { margin-top: 15px; }");
            pw.println(".fragment-item { padding: 8px; margin: 5px 0; background: #FFF8E1; border-left: 3px solid #FF9800; color: #333; }");
            pw.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            pw.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            pw.println("th { background-color: #4CAF50; color: white; }");
            pw.println("</style></head><body>");

            pw.println("<h1>📋 重复内容标记报告</h1>");

            pw.println("<table><tr><th>文档</th><th>路径</th></tr>");
            pw.println("<tr><td>文档1</td><td>" + doc1Name + "</td></tr>");
            pw.println("<tr><td>文档2</td><td>" + doc2Name + "</td></tr></table>");

            // 文档1带高亮的全文
            pw.println("<div class='doc-section'>");
            pw.println("<div class='doc-title'>📄 " + doc1Name + " (高亮标记)</div>");
            pw.println("<p>" + highlightSentences(doc1Sentences, fragments, true) + "</p>");
            pw.println("</div>");

            // 文档2带高亮的全文
            pw.println("<div class='doc-section'>");
            pw.println("<div class='doc-title'>📄 " + doc2Name + " (高亮标记)</div>");
            pw.println("<p>" + highlightSentences(doc2Sentences, fragments, false) + "</p>");
            pw.println("</div>");

            // 重复片段列表
            pw.println("<div class='fragment-list'>");
            pw.println("<h2>🔍 重复片段详情</h2>");
            for (DuplicateFragment f : fragments) {
                String cssClass = f.getSimilarity() > 0.8 ? "fragment-item high-sim" : "fragment-item medium-sim";
                pw.println("<div class='" + cssClass + "'>");
                pw.println("相似度: " + String.format("%.1f%%", f.getSimilarity() * 100));
                pw.println("<br>内容: " + escapeHtml(f.getText()));
                pw.println("</div>");
            }
            pw.println("</div>");

            pw.println("</body></html>");
        } catch (Exception e) {
            System.err.println("生成HTML报告失败: " + e);
        }
    }

    private static String highlightSentences(String[] sentences, List<DuplicateFragment> fragments, boolean isLeft) {
        StringBuilder sb = new StringBuilder();
        Set<Integer> highlightedIndices = new HashSet<>();
        for (DuplicateFragment f : fragments) {
            if (isLeft) {
                highlightedIndices.add(f.getLeftStartIndex());
            } else {
                highlightedIndices.add(f.getRightStartIndex());
            }
        }
        for (int i = 0; i < sentences.length; i++) {
            String sentence = escapeHtml(sentences[i]);
            if (highlightedIndices.contains(i)) {
                sb.append("<span class='highlight'>").append(sentence).append("</span>");
            } else {
                sb.append(sentence);
            }
            if (i < sentences.length - 1) {
                sb.append("。");
            }
        }
        return sb.toString();
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
