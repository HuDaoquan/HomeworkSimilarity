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
import java.util.Date;

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

import org.apache.commons.lang.time.DateFormatUtils;
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
    private static JTextArea docLocationTextArea;
    private JCheckBox wordBox;
    private JCheckBox picBox;
    // private JCheckBox sortBox;
    private static JTextField textPath;
    
    private Double simThre;
    private JComboBox<String> comboBox;
    // 查重模式 1、所有文档两两比较；2、今年与往年比较（要求所选路径中必须有一个”今年“文件夹、一个“往年”文件夹）
    private JComboBox<String> queryModeBox;
    
    public UIhdq() {
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel1 = new JPanel();
        panel1.setToolTipText("");
        JScrollPane scrollPane1 = new JScrollPane();
        docLocationTextArea = new JTextArea();
        Font x = new Font("仿宋", 0, 15);
        docLocationTextArea.setFont(x);
        docLocationTextArea.setToolTipText("查重结果");
        JLabel label1 = new JLabel();
        label1.setFont(new Font("仿宋", Font.BOLD, 14));
        label1.setToolTipText("");
        JPanel tableShowJPanel = new JPanel();
        Border border = new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(
                new javax.swing.border.EmptyBorder(0, 0, 0, 0), "使用中如有疑问，请联系1455523026@qq.com",
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BOTTOM,
                new java.awt.Font("仿宋", java.awt.Font.BOLD, 15), java.awt.Color.red), getBorder());
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
        JTextPane txtpnrnrncsvexcelrn = new JTextPane();
        txtpnrnrncsvexcelrn.setBackground(SystemColor.controlHighlight);
        txtpnrnrncsvexcelrn.setForeground(Color.BLACK);
        txtpnrnrncsvexcelrn.setFont(new Font("仿宋", Font.BOLD, 16));
        txtpnrnrncsvexcelrn.setEditable(false);
        txtpnrnrncsvexcelrn.setText("使用说明：\r\n  1.查重前请将待查重文档放入文件夹中，然后点击“选择查重路径”按钮选择该文件夹，点击“开始查重”按钮，开始查重；" +
                "\n  2.查重模式1:将对所选路径下所有文档两两比较;  模式2:所选路径中必须有一个”今年“、一个“往年”目录分别存今年和往年文档,今年文档会两两比较（相比模式1少了往年文档互相比较的过程)；" +
                "\n  3.查重结果存储于所选文件夹中以“查重结果”开头的Excel表格中；" +
                "\n  4.“简略结果”表列出每个文件及其最相似文件，详细结果表列出全部结果；抄袭名单会列出相似度超过在选定阈值的文件名。");
        txtpnrnrncsvexcelrn.setToolTipText("使用说明");
        txtpnrnrncsvexcelrn.setBounds(10, 55, 559, 186);
        tableShowJPanel.add(txtpnrnrncsvexcelrn);
        textPath = new JTextField();
        textPath.setFont(new Font("仿宋", Font.PLAIN, 16));
        textPath.setBackground(SystemColor.menu);
        textPath.setEditable(false);
        textPath.setBounds(145, 13, 425, 32);
        tableShowJPanel.add(textPath);
        textPath.setColumns(10);
        JLabel label = new JLabel("您选择的查重路径是：");
        label.setFont(new Font("仿宋", Font.PLAIN, 14));
        label.setBounds(10, 16, 145, 29);
        tableShowJPanel.add(label);
        JButton searchButton = new JButton();
        searchButton.setBounds(598, 14, 145, 33);
        tableShowJPanel.add(searchButton);
        searchButton.setFont(new Font("仿宋", Font.BOLD, 16));
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
        JPanel panel2 = new JPanel();
        panel2.setBounds(608, 74, 132, 193);
        tableShowJPanel.add(panel2);
        // ======== panel2 ========
        {
            panel2.setLayout(new GridLayout(6, 1, 0, 3));
            wordBox = new JCheckBox("打开智能分词");
            wordBox.setFont(new Font("仿宋", Font.PLAIN, 16));
            wordBox.setToolTipText("取消勾选会将每个词语分成最小颗粒。如：“笔记本电脑”=【笔记本电脑, 笔记本, 笔记, 电脑】，提高精度");
            wordBox.setSelected(true);
            panel2.add(wordBox);
            picBox = new JCheckBox("打开图片查重");
            picBox.setFont(new Font("仿宋", Font.PLAIN, 16));
            picBox.setToolTipText("勾选后会对文档中图片进行比较，但会严重降低比较速度，当图片过多时计算会很慢");
            panel2.add(picBox);
            // sortBox = new JCheckBox("打开排序输出");
            // sortBox.setFont(new Font("仿宋", Font.PLAIN, 16));
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
        
        queryModeBox = new JComboBox<String>();
        queryModeBox.setToolTipText("查重模式 \n1、所有文档两两比较；\n2、今年与往年比较（所选路径中必须有一个”今年“文件夹、一个“往年”文件夹）");
        queryModeBox.addItem("选择查重模式");
        queryModeBox.addItem("模式1两两");
        queryModeBox.addItem("模式2今年与往年");
        
        
        JButton beginButton = new JButton("开始查重");
        beginButton.setForeground(Color.BLACK);
        beginButton.setFont(new Font("仿宋", Font.BOLD, 20));
        beginButton.addMouseListener(new MouseAdapter() {
            int index = 1;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                docLocationTextArea.setText("开始处理：\n");
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
                    case "20%":
                        simThre = 0.2;
                        break;
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
                String excelPath =
                        path + "\\查重结果".concat("智能分词-" + "图片查重-" + (String) queryModeBox.getSelectedItem()).concat(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).concat(".xlsx");
                try {
                    switch ((String) queryModeBox.getSelectedItem()) {
                        case "模式2今年与往年":
                            CompareOptimize.getSimilarityMode2(path, wordBox.isSelected(), picBox.isSelected(), simThre, excelPath);
                            break;
                        default:
                            CompareOptimize.getSimilarityMode1(path, wordBox.isSelected(), picBox.isSelected(), simThre, excelPath);
                        
                    }
                    long endTime = System.currentTimeMillis(); // 获取结束时间
                    System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s"); // 输出程序运行时间
                } catch (Exception ex) {
                    System.out.println("计算出错,请检查后重试:" + ex);
                }
                
            }
        });
        panel2.add(comboBox);
        panel2.add(queryModeBox);
        panel2.add(beginButton);
    }
    
    /**
     * 控制台重定向
     *
     * @param
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
