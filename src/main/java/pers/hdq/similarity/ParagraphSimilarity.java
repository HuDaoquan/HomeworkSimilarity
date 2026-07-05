package pers.hdq.similarity;

import java.util.*;

public class ParagraphSimilarity {

    public static class DuplicateFragment {
        private String text;
        private String rightText;
        private int leftStartIndex;
        private int leftEndIndex;
        private int rightStartIndex;
        private int rightEndIndex;
        private double similarity;

        public DuplicateFragment(String text, String rightText, int leftStartIndex, int leftEndIndex,
                                 int rightStartIndex, int rightEndIndex, double similarity) {
            this.text = text;
            this.rightText = rightText;
            this.leftStartIndex = leftStartIndex;
            this.leftEndIndex = leftEndIndex;
            this.rightStartIndex = rightStartIndex;
            this.rightEndIndex = rightEndIndex;
            this.similarity = similarity;
        }

        public String getText() { return text; }
        public String getRightText() { return rightText; }
        public double getSimilarity() { return similarity; }
        public int getLeftStartIndex() { return leftStartIndex; }
        public int getLeftEndIndex() { return leftEndIndex; }
        public int getRightStartIndex() { return rightStartIndex; }
        public int getRightEndIndex() { return rightEndIndex; }
    }

    public static List<DuplicateFragment> findDuplicateFragments(String text1, String text2, int minFragmentLength, double threshold) {
        List<DuplicateFragment> fragments = new ArrayList<>();
        String[] sentences1 = splitSentences(text1);
        String[] sentences2 = splitSentences(text2);

        for (int i = 0; i < sentences1.length; i++) {
            for (int j = 0; j < sentences2.length; j++) {
                if (sentences1[i].length() < minFragmentLength || sentences2[j].length() < minFragmentLength) {
                    continue;
                }
                //  杰卡德相似度
                // double jaccardSim = Jaccard.jaccardSimilarity(
                //     Arrays.asList(sentences1[i].split("")),
                //     Arrays.asList(sentences2[j].split(""))
                // );
                /*  余弦相似度*/
                double conSim = CosineSimilarity.sim(
                        Arrays.asList(sentences1[i].split("")),
                        Arrays.asList(sentences2[j].split(""))
                );
                // double sim = (conSim + jaccardSim) / 2;
                double sim = conSim;
                
                if (sim > threshold) {
                    fragments.add(new DuplicateFragment(
                        sentences1[i], sentences2[j], i, i, j, j, sim
                    ));
                }
            }
        }
        return fragments;
    }

    private static String[] splitSentences(String text) {
        return text.split("[。！？；\\.!\\?;]");
    }
}
