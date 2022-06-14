package pers.hdq.picture;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author HuDaoquan
 * @version v1.0
 * @ClassName: SaveHash
 * @Description: 优化后的PHash算法，新增返回hash指纹功能，配合调用算法，极大提高程序效率
 * @date 2019年7月1日 下午9:32:28
 */
public class SaveHash {
    public static void main(String[] args) {
    }
    
    /**
     * 计算平均值(只保留左上角8*8)，获得特征hash值
     *
     * @param imagePath
     *
     * @return
     */
    public String getFeatureValue(String imagePath) {
        // 缩小尺寸，简化色彩
        int[][] grayMatrix = getGrayPixel(imagePath, 32, 32);
        // 计算DCT
        grayMatrix = DCT(grayMatrix, 32);
        // 缩小DCT，计算平均值(只保留左上角8*8)
        int[][] newMatrix = new int[8][8];
        double average = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                newMatrix[i][j] = grayMatrix[i][j];
//				System.out.print(newMatrix[i][j] + "\t");
                average += grayMatrix[i][j];
            }
//			System.out.println();
        }
        average /= 64.0;
        // 计算hash值
        String hash = "";
//		System.out.println("ave"+average);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (newMatrix[i][j] < average) {
                    hash += '0';
                } else {
                    hash += '1';
                }
            }
        }
        return hash;
    }
    
    /**
     * 获取灰度值
     *
     * @param imagePath
     * @param width
     * @param height
     *
     * @return
     */
    public int[][] getGrayPixel(String imagePath, int width, int height) {
        BufferedImage bi = null;
        try {
            bi = resizeImage(imagePath, width, height, BufferedImage.TYPE_INT_RGB);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int minx = bi.getMinX();
        int miny = bi.getMinY();
        int[][] matrix = new int[width - minx][height - miny];
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = bi.getRGB(i, j);
                int red = (pixel & 0xff0000) >> 16; // 首先将颜色值与十六进制表示的00ff0000进行“与”运算，运算结果除了表示红色的数字值之外，GGBB部分颜色都为0，在将结果向右移位16位，得到的就是红色值。所以这句代码主要用来从一个颜色中抽取其组成色---红色的值。
                int green = (pixel & 0xff00) >> 8;
                int blue = (pixel & 0xff);
                int gray = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                matrix[i][j] = gray;
            }
        }
        return matrix;
    }
    
    /**
     * 缩放图片尺寸
     *
     * @param srcImgPath
     * @param width
     * @param height
     * @param imageType
     *
     * @return
     * @throws IOException
     */
    public BufferedImage resizeImage(String srcImgPath, int width, int height, int imageType) throws IOException {
        File srcFile = new File(srcImgPath);
        BufferedImage srcImg = ImageIO.read(srcFile);
        BufferedImage buffImg = null;
        buffImg = new BufferedImage(width, height, imageType);
        buffImg.getGraphics().drawImage(srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        return buffImg;
    }
    
    /**
     * 用于计算pHash的相似度<br>
     * 相似度为1时，图片最相似
     *
     * @param str1
     * @param str2
     *
     * @return
     */
    public static double getSimilarity(String hash1, String hash2) {
        int num = 0;
        for (int i = 0; i < 64; i++) {
            if (hash1.charAt(i) == hash2.charAt(i)) {
                num++; // 求出相同的位数
            }
        }
//		System.out.println("相似数：" + num + "   总长度：" + hash1.length());
        double sim = 0;
        // 对相似度进行调整
        switch (num / 10) {
            case 6:
                sim = Math.pow((double) num / hash1.length(), 2);
                break;
            case 5:
                if (num > 54) {
                    sim = Math.pow((double) num / hash1.length(), 3);
                } else {
                    sim = Math.pow((double) num / hash1.length(), 4);
                }
                break;
            case 4:
                if (num > 44) {
                    sim = Math.pow((double) num / hash1.length(), 3.5);
                } else {
                    sim = Math.pow((double) num / hash1.length(), 3);
                }
                break;
            case 3:
                sim = Math.pow((double) num / hash1.length(), 2.5);
                break;
            default:
                sim = Math.pow((double) num / hash1.length(), 3);
                break;
        }
        return sim;
    }
    
    /**
     * 离散余弦变换（DCT）
     *
     * @param pix 原图像的数据矩阵
     * @param n   原图像(n*n)的高或宽
     *
     * @return 变换后的矩阵数组
     */
    public int[][] DCT(int[][] pix, int n) {
        double[][] iMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                iMatrix[i][j] = (pix[i][j]);
            }
        }
        double[][] quotient = coefficient(n); // 求系数矩阵
        double[][] quotientT = transposingMatrix(quotient, n); // 转置系数矩阵
        double[][] temp = new double[n][n];
        temp = matrixMultiply(quotient, iMatrix, n);
        iMatrix = matrixMultiply(temp, quotientT, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                pix[i][j] = (int) (iMatrix[i][j]);
//				System.out.print(pix[i][j] +"\r");
            }
//			System.out.println();
        }
        return pix;
    }
    
    /**
     * 求离散余弦变换的系数矩阵
     *
     * @param n n*n矩阵的大小
     *
     * @return 系数矩阵
     */
    private static double[][] coefficient(int n) {
        double[][] coeff = new double[n][n];
        double sqrt = 1.0 / Math.sqrt(n);
        for (int i = 0; i < n; i++) {
            coeff[0][i] = sqrt;
        }
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < n; j++) {
                coeff[i][j] = Math.sqrt(2.0 / n) * Math.cos(i * Math.PI * (j + 0.5) / n);
            }
        }
        return coeff;
    }
    
    /**
     * 矩阵转置
     *
     * @param matrix 原矩阵
     * @param n      矩阵(n*n)的高或宽
     *
     * @return 转置后的矩阵
     */
    private static double[][] transposingMatrix(double[][] matrix, int n) {
        double nMatrix[][] = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                nMatrix[i][j] = matrix[j][i];
            }
        }
        return nMatrix;
    }
    
    /**
     * 矩阵相乘
     *
     * @param A 矩阵A
     * @param B 矩阵B
     * @param n 矩阵的大小n*n
     *
     * @return 结果矩阵
     */
    private double[][] matrixMultiply(double[][] A, double[][] B, int n) {
        double nMatrix[][] = new double[n][n];
        int t = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                t = 0;
                for (int k = 0; k < n; k++) {
                    t += A[i][k] * B[k][j];
                }
                nMatrix[i][j] = t;
            }
        }
        return nMatrix;
    }
}
