package pers.hdq.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;

import pers.hdq.function.CompareOptimize;

/**
 * @author HuDaoquan
 * @version v1.0
 * @ClassName: UIhdq
 * @Description: 程序可视化界面设计
 * @date 2019年7月1日 下午9:46:52
 */
public class UIhdq extends JPanel {
    private static final long serialVersionUID = 1289965392854758573L;
    private String path = "";
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private static JTextArea docLocationTextArea;
    private JPanel panel2;
    private JButton searchButton;
    private JLabel label1;
    private JPanel tableShowJPanel;
    private JTextPane txtpnrnrncsvexcelrn;
    private JCheckBox wordBox;
    private JCheckBox picBox;
    // private JCheckBox sortBox;
    private static JTextField textPath;
    
    private Double simThre;
    private JComboBox<String> comboBox;
    
    public UIhdq() {
        initComponents();
    }
    
    private void initComponents() {
        panel1 = new JPanel();
        panel1.setToolTipText("");
        scrollPane1 = new JScrollPane();
        docLocationTextArea = new JTextArea();
        Font x = new Font("宋体", 0, 15);
        docLocationTextArea.setFont(x);
        docLocationTextArea.setToolTipText("查重结果");
        label1 = new JLabel();
        label1.setFont(new Font("宋体", Font.BOLD, 14));
        label1.setToolTipText("");
        tableShowJPanel = new JPanel();
        Border border = new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(
                new javax.swing.border.EmptyBorder(0, 0, 0, 0), "使用中如有疑问，请联系1455523026@qq.com",
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BOTTOM,
                new java.awt.Font("宋体", java.awt.Font.BOLD, 15), java.awt.Color.red), getBorder());
        setBorder(border);
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent e) {
                if ("border".equals(e.getPropertyName())) {
                    throw new RuntimeException();
                }
            }
        });
        setLayout(new GridLayout(2, 1, 0, 5));
        // ======== panel1 ========
        {
            panel1.setLayout(null);
            // ======== scrollPane1 ========
            {
                // ---- docLocationTextArea ----
                docLocationTextArea.setEditable(false);
                scrollPane1.setViewportView(docLocationTextArea);
            }
            panel1.add(scrollPane1);
            scrollPane1.setBounds(10, 30, 750, 230);// 结果框大小
            // ---- label1 ----
            label1.setText("查重结果：");
            panel1.add(label1);
            label1.setBounds(10, 0, 87, 25);
            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for (int i = 0; i < panel1.getComponentCount(); i++) {
                    Rectangle bounds = panel1.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel1.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel1.setMinimumSize(preferredSize);
                panel1.setPreferredSize(preferredSize);
            }
        }
        add(panel1);
        // ======== tableShowJPanel ========
        {
            tableShowJPanel.setLayout(null);
            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for (int i = 0; i < tableShowJPanel.getComponentCount(); i++) {
                    Rectangle bounds = tableShowJPanel.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = tableShowJPanel.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                tableShowJPanel.setMinimumSize(preferredSize);
                tableShowJPanel.setPreferredSize(preferredSize);
            }
        }
        add(tableShowJPanel);
        txtpnrnrncsvexcelrn = new JTextPane();
        txtpnrnrncsvexcelrn.setBackground(SystemColor.controlHighlight);
        txtpnrnrncsvexcelrn.setForeground(Color.BLACK);
        txtpnrnrncsvexcelrn.setFont(new Font("仿宋", Font.BOLD, 16));
        txtpnrnrncsvexcelrn.setEditable(false);
        txtpnrnrncsvexcelrn.setText("使用说明：\r\n  1.查重前请将需要两两比较的文档放入同一文件夹中，然后点击“选择查重路径”按钮选择该文件夹，点击“开始查重”按钮，开始查重；"
                + "\n  2.右侧有4个打开辅助选项，鼠标悬停其上查看功能；\r\n  3.查重结果会存储于所选文件夹中以“查重结果”开头的Excel表格中(文件名中的数字表示查重时间，精确到秒)；"
                + "\n  4.结果文件中的“简略结果”表只列出每个文件及其最相似文件的结果，详细结果表列出全部结果；抄袭名单会列出相似度超过您在右侧选择的阈值(默认90%）的名单。");
        txtpnrnrncsvexcelrn.setToolTipText("使用说明");
        txtpnrnrncsvexcelrn.setBounds(10, 55, 559, 186);
        tableShowJPanel.add(txtpnrnrncsvexcelrn);
        textPath = new JTextField();
        textPath.setFont(new Font("宋体", Font.PLAIN, 16));
        textPath.setBackground(SystemColor.menu);
        textPath.setEditable(false);
        textPath.setBounds(145, 13, 425, 32);
        tableShowJPanel.add(textPath);
        textPath.setColumns(10);
        JLabel label = new JLabel("您选择的查重路径是：");
        label.setFont(new Font("宋体", Font.PLAIN, 14));
        label.setBounds(10, 16, 145, 29);
        tableShowJPanel.add(label);
        searchButton = new JButton();
        searchButton.setBounds(598, 14, 145, 33);
        tableShowJPanel.add(searchButton);
        searchButton.setFont(new Font("宋体", Font.BOLD, 16));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 自动生成的方法存根
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = jfc.getSelectedFile();
                    if (file != null) {
                        path = file.getPath();
                        textPath.setText(path);
                    }
                }
            }
        });
        // ---- searchButton ----
        searchButton.setText("选择查重路径");
        panel2 = new JPanel();
        panel2.setBounds(608, 74, 132, 193);
        tableShowJPanel.add(panel2);
        // ======== panel2 ========
        {
            panel2.setLayout(new GridLayout(6, 1, 0, 3));
            wordBox = new JCheckBox("打开智能分词");
            wordBox.setFont(new Font("宋体", Font.PLAIN, 16));
            wordBox.setToolTipText("取消勾选会将每个词语分成最小颗粒。如：“笔记本电脑”=【笔记本电脑, 笔记本, 笔记, 电脑】，提高精度");
            wordBox.setSelected(true);
            panel2.add(wordBox);
            picBox = new JCheckBox("打开图片查重");
            picBox.setFont(new Font("宋体", Font.PLAIN, 16));
            picBox.setToolTipText("勾选后会对文档中图片进行比较，但会严重降低比较速度，当图片过多时计算会很慢");
            panel2.add(picBox);
            // sortBox = new JCheckBox("打开排序输出");
            // sortBox.setFont(new Font("宋体", Font.PLAIN, 16));
            // sortBox.setToolTipText("勾选后输出查重结果会按相似度降序排序！会增加运算时间,不建议勾选");
            // panel2.add(sortBox);
        }
        comboBox = new JComboBox<String>();
        comboBox.setToolTipText("选择相似度阈值");
        comboBox.addItem("选择相似度下限");
        comboBox.addItem("20%");
        comboBox.addItem("30%");
        comboBox.addItem("40%");
        comboBox.addItem("50%");
        comboBox.addItem("60%");
        comboBox.addItem("70%");
        comboBox.addItem("80%");
        comboBox.addItem("90%");
        comboBox.addItem("95%");
        
        JButton beginButton = new JButton("开始查重");
        beginButton.setForeground(Color.BLACK);
        beginButton.setFont(new Font("宋体", Font.BOLD, 20));
        beginButton.addMouseListener(new MouseAdapter() {
            int index = 1;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                docLocationTextArea.setText("正在计算：");
                docLocationTextArea.paintImmediately(docLocationTextArea.getBounds());
                if (index % 2 == 0) {
                    docLocationTextArea.setForeground(Color.BLACK); // 黑色
                } else {
                    docLocationTextArea.setForeground(Color.magenta); // 紫色
                }
                index++;
//获取相似度阈值
                String threshold = (String) comboBox.getSelectedItem();
                switch (threshold) {
                    case "30%":
                        simThre = 0.3;
                        break;
                    case "40%":
                        simThre = 0.4;
                        break;
                    case "50%":
                        simThre = 0.5;
                        break;
                    case "60%":
                        simThre = 0.6;
                        break;
                    case "70%":
                        simThre = 0.7;
                        break;
                    case "80%":
                        simThre = 0.8;
                        break;
                    case "90%":
                        simThre = 0.9;
                        break;
                    case "95%":
                        simThre = 0.95;
                        break;
                    default:
                        simThre = 0.90;
                }
                long startTime = System.currentTimeMillis(); // 获取开始时间
                System.err.println("相似度计算结果已存入：" + CompareOptimize.calculateFileSimilarity(path, wordBox.isSelected(),
                        picBox.isSelected(), simThre));
                long endTime = System.currentTimeMillis(); // 获取结束时间
                System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s"); // 输出程序运行时间
            }
        });
        panel2.add(comboBox);
        panel2.add(beginButton);
    }
    
    /**
     * 控制台重定向
     *
     * @param args
     */
    public static void redirectConsole() {
        OutputStream textAreaStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                docLocationTextArea.append(String.valueOf((char) b));
                docLocationTextArea.paintImmediately(docLocationTextArea.getBounds());// 实时输出
            }
            
            @Override
            public void write(byte b[]) throws IOException {
                docLocationTextArea.append(new String(b));
                docLocationTextArea.paintImmediately(docLocationTextArea.getBounds());// 实时输出
            }
            
            @Override
            public void write(byte b[], int off, int len) throws IOException {
                docLocationTextArea.append(new String(b, off, len));
                docLocationTextArea.paintImmediately(docLocationTextArea.getBounds());// 实时输出
            }
        };
        PrintStream myOut = new PrintStream(textAreaStream);
        System.setOut(myOut);
        System.setErr(myOut);
    }
    
    public static void main(String args[]) {
        redirectConsole();
        try {
            JFrame frame = new JFrame("本地文档查重系统");
            frame.setBounds(300, 200, 800, 600);// 初始界面大小
            frame.getContentPane().add(new UIhdq(), BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
