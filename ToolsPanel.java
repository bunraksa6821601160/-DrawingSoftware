import java.awt.*;
import javax.swing.*;

public class ToolsPanel extends JPanel {
    private JButton colorPreviewBtn;
    private JButton selectedButton;
    private int sizeIcon = 40;

    public ToolsPanel(DrawingModel model) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Tools"));

        // 1. ปุ่มเลือกเครื่องมือวาด
        JPanel toolsGrid = new JPanel(new GridLayout(3, 2, 4, 4));

        toolsGrid.setPreferredSize(new Dimension(115, 140));
        toolsGrid.setMaximumSize(new Dimension(115, 140));

        JButton pencilBtn = new JButton();
        JButton eraserBtn = new JButton();
        JButton lineBtn = new JButton();
        JButton rectBtn = new JButton();
        JButton circleBtn = new JButton();
        JButton textBtn = new JButton();

        //
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

        // Size
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setBorder(BorderFactory.createTitledBorder("Size"));

        JSlider sizeSlider = new JSlider(
        JSlider.VERTICAL, 1, 50, (int) model.getPencilSize()
);

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

        JSlider opacitySlider = new JSlider(
        JSlider.VERTICAL, 0, 100, 100
);

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

        // // 4. ปุ่มเลือกสี
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton colorPickerBtn = new JButton("Color");
        colorPreviewBtn = new JButton("  ");
        colorPreviewBtn.setBackground(model.getCurrentColor());
        colorPreviewBtn.setEnabled(false);

        colorPickerBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "เลือกสีแปรงวาด", model.getCurrentColor());
            if (chosen != null) {
                model.setCurrentColor(chosen);
                colorPreviewBtn.setBackground(chosen);
            }
        });

        colorPanel.add(colorPickerBtn);
        colorPanel.add(colorPreviewBtn);
        add(colorPanel);

    }
    
    private ImageIcon resizeIcon(String path, int width, int height) {
    ImageIcon icon = new ImageIcon(path);
    Image image = icon.getImage().getScaledInstance(
        width, height, Image.SCALE_SMOOTH
    );
    return new ImageIcon(image);
}
}