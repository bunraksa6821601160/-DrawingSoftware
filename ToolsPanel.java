import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ToolsPanel extends JPanel {
    private JButton colorPreviewBtn;
    private JButton selectedButton;
    private int sizeIcon = 40;

    public ToolsPanel(DrawingModel model) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Tools"));

        //ปุ่มเลือกเครื่องมือวาด
        JPanel toolsGrid = new JPanel(new GridLayout(3, 2, 4, 4));

        //ล็อกขนาดคงที่ไว้ที่ $115 \times 140$ พิกเซลเสมอ แม้ผู้ใช้จะทำการขยายหรือย่อขนาดหน้าต่าง
        //1.กำหนด ขนาดที่ต้องการ/เหมาะสมที่สุด (Preferred Size) ให้มีความกว้าง 115 พิกเซล และความสูง 140 พิกเซล
        //2.กำหนด ขีดจำกัดขนาดสูงสุด (Maximum Size) ไม่ให้เกินกว้าง 115 และสูง 140 พิกเซล
        toolsGrid.setPreferredSize(new Dimension(115, 140));
        toolsGrid.setMaximumSize(new Dimension(115, 140));

        JButton pencilBtn = new JButton();
        JButton eraserBtn = new JButton();
        JButton lineBtn = new JButton();
        JButton rectBtn = new JButton();
        JButton circleBtn = new JButton();
        JButton textBtn = new JButton();

        //ทำให้ปุ่มเปิดปิดได้
        JButton[] toolButtons = { pencilBtn, eraserBtn, lineBtn,rectBtn, circleBtn, textBtn};
        for (JButton btn : toolButtons) {
            btn.addActionListener(e -> {
                if (selectedButton != null) {
                    selectedButton.setBackground(null);
                }

                selectedButton = btn;
                selectedButton.setBackground(Color.CYAN);
            });
            //เลือกดินสอตอนแรก
            selectedButton = pencilBtn;
            pencilBtn.setBackground(Color.CYAN);
        }
        

        //รูปicon
        pencilBtn.setIcon(resizeIcon("./pic_icon/pencil.png", sizeIcon, sizeIcon));
        eraserBtn.setIcon(resizeIcon("./pic_icon/eraser.png", sizeIcon, sizeIcon));
        lineBtn.setIcon(resizeIcon("./pic_icon/line.png", sizeIcon, sizeIcon));
        rectBtn.setIcon(resizeIcon("./pic_icon/rectangle.png", sizeIcon, sizeIcon));
        circleBtn.setIcon(resizeIcon("./pic_icon/circle.png", sizeIcon, sizeIcon));
        textBtn.setIcon(resizeIcon("./pic_icon/text.png", sizeIcon, sizeIcon));

        //เมาส์ชี้ขึ้นข้อความ
        pencilBtn.setToolTipText("Pencil");
        eraserBtn.setToolTipText("Eraser");
        lineBtn.setToolTipText("Line");
        rectBtn.setToolTipText("Rectangle");
        circleBtn.setToolTipText("Circle");
        textBtn.setToolTipText("Text");

        pencilBtn.addActionListener(e -> model.setCurrentTool(DrawnShape.ShapeType.PENCIL));
        eraserBtn.addActionListener(e -> model.setCurrentTool(DrawnShape.ShapeType.ERASER));
        lineBtn.addActionListener(e -> model.setCurrentTool(DrawnShape.ShapeType.LINE));
        rectBtn.addActionListener(e -> model.setCurrentTool(DrawnShape.ShapeType.RECTANGLE));
        circleBtn.addActionListener(e -> model.setCurrentTool(DrawnShape.ShapeType.CIRCLE));
        textBtn.addActionListener(e -> model.setCurrentTool(DrawnShape.ShapeType.TEXT));

        toolsGrid.add(pencilBtn);
        toolsGrid.add(eraserBtn);
        toolsGrid.add(lineBtn);
        toolsGrid.add(rectBtn);
        toolsGrid.add(circleBtn);
        toolsGrid.add(textBtn);

        add(toolsGrid);
        add(Box.createVerticalStrut(10));


        // ปรับขนาด + ความเข้ม
        JPanel settingPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        // SizePanel
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setBorder(BorderFactory.createTitledBorder("Size"));

        JSlider sizeSlider = new JSlider(JSlider.VERTICAL, 1, 50, (int) model.getPencilSize());

        sizeSlider.setMajorTickSpacing(10);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(false);

        JLabel sizeLabel = new JLabel(String.valueOf(sizeSlider.getValue()));
        sizeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        sizeSlider.addChangeListener(e -> {
            int size = sizeSlider.getValue();
            model.setPencilSize(size);
            model.setEraserSize(size);
            sizeLabel.setText(String.valueOf(size));
        });

        sizePanel.add(sizeSlider, BorderLayout.CENTER);
        sizePanel.add(sizeLabel, BorderLayout.SOUTH);


        // Opacity
        JPanel opacityPanel = new JPanel(new BorderLayout());
        opacityPanel.setBorder(BorderFactory.createTitledBorder("Opacity"));

        JSlider opacitySlider = new JSlider(JSlider.VERTICAL, 0, 100, 100);

        //1.กำหนด ระยะห่างของขีดวัดหลัก (Major Ticks) ให้เกิดขึ้นทุกๆ 10 หน่วย
        //2.สั่งให้ วาด/แสดงขีดวัด (Ticks) บนตัวสไลเดอร์
        //3.ซ่อนตัวเลขกำกับสเกล (Labels) ไม่ให้แสดงบนสไลเดอร์
        opacitySlider.setMajorTickSpacing(10);
        opacitySlider.setPaintTicks(true);
        opacitySlider.setPaintLabels(false);

        JLabel opacityLabel = new JLabel("100%");
        opacityLabel.setHorizontalAlignment(SwingConstants.CENTER);

        opacitySlider.addChangeListener(e -> {
            int value = opacitySlider.getValue();
            model.setOpacity(value / 100.0f);
            opacityLabel.setText(opacitySlider.getValue() + "%");
        });

        opacityPanel.add(opacitySlider, BorderLayout.CENTER);
        opacityPanel.add(opacityLabel, BorderLayout.SOUTH);


        // เอา 2 ช่องมาอยู่ข้างกัน
        settingPanel.add(sizePanel);
        settingPanel.add(opacityPanel);

        add(settingPanel);

        add(Box.createVerticalStrut(5));
        add(Box.createVerticalStrut(10));


        //ปุ่มเลือกสี
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton colorPickerBtn = new JButton("Color");
        colorPreviewBtn = new JButton("  ");
        colorPreviewBtn.setBackground(model.getCurrentColor());
        
        // แล้วใช้คำสั่งพวกนี้ปรับแต่งปุ่มพรีวิวสีให้สวยงามแทน
        //1.ปิดไม่ให้ปุ่มนี้สามารถ รับโฟกัส (Focus) จากคีย์บอร์ด ช่วยไม่ให้ปุ่มไปแย่งโฟกัสจากคอมโพเนนต์อื่น
        //2.กำหนดให้ปุ่มมี ความทึบแสง (Non-transparent)
        //3.วาดเส้นขอบ (Border) รอบตัวปุ่ม
        colorPreviewBtn.setFocusable(false);
        colorPreviewBtn.setOpaque(true);
        colorPreviewBtn.setBorderPainted(true); // แสดงขอบปุ่มให้ดูเป็นช่องสี

        // สร้าง Listener กลางสำหรับเปิด JColorChooser เพื่อลดโค้ดซ้ำซ้อน
        ActionListener chooseColorAction = e -> {
            Color chosen = JColorChooser.showDialog(this, "Select a brush color.", model.getCurrentColor());
            if (chosen != null) {
                model.setCurrentColor(chosen);
                colorPreviewBtn.setBackground(chosen);
            }
        };

        // ผูก Listener ให้ทั้ง 2 ปุ่มทำงานเหมือนกัน
        colorPickerBtn.addActionListener(chooseColorAction);
        colorPreviewBtn.addActionListener(chooseColorAction);

        colorPanel.add(colorPickerBtn);
        colorPanel.add(colorPreviewBtn);
        add(colorPanel);

    }
    
    /**
     * ปรับขนาดภาพเพื่อไปใส่ในปุ่ม
     * @param path
     * @param width
     * @param height
     * @return ภาพ icon สำหรับ Tool ต่างๆ
     */
    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage().getScaledInstance(
            width, height, Image.SCALE_SMOOTH
        );
        return new ImageIcon(image);
    }
}