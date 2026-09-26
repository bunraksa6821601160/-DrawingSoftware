import java.awt.*;
import javax.swing.*;

public class ToolsPanel extends JPanel {
    private DrawingModel model;
    private JButton colorPreviewBtn;

    public ToolsPanel(DrawingModel model) {
        this.model = model;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Tools"));

        // 1. ปุ่มเลือกเครื่องมือวาด
        JPanel toolsGrid = new JPanel(new GridLayout(3, 2, 5, 5));
        JButton pencilBtn = new JButton("Pencil");
        JButton eraserBtn = new JButton("Eraser");
        JButton lineBtn = new JButton("Line");
        JButton rectBtn = new JButton("Rect");
        JButton circleBtn = new JButton("Circle");
        JButton textBtn = new JButton("Text");

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

        // 2. JSlider สำหรับปรับขนาดดินสอ/เส้น
        JPanel pencilSliderPanel = new JPanel(new BorderLayout());
        pencilSliderPanel.setBorder(BorderFactory.createTitledBorder("Pencil Size"));
        JSlider pencilSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, (int) model.getPencilSize());
        pencilSlider.setMajorTickSpacing(5);
        pencilSlider.setPaintTicks(true);
        pencilSlider.setPaintLabels(true);
        pencilSlider.addChangeListener(e -> model.setPencilSize(pencilSlider.getValue()));
        pencilSliderPanel.add(pencilSlider, BorderLayout.CENTER);
        add(pencilSliderPanel);

        add(Box.createVerticalStrut(5));

        // 3. JSlider สำหรับปรับขนาดยางลบ
        JPanel eraserSliderPanel = new JPanel(new BorderLayout());
        eraserSliderPanel.setBorder(BorderFactory.createTitledBorder("Eraser Size"));
        JSlider eraserSlider = new JSlider(JSlider.HORIZONTAL, 5, 60, (int) model.getEraserSize());
        eraserSlider.setMajorTickSpacing(15);
        eraserSlider.setPaintTicks(true);
        eraserSlider.setPaintLabels(true);
        eraserSlider.addChangeListener(e -> model.setEraserSize(eraserSlider.getValue()));
        eraserSliderPanel.add(eraserSlider, BorderLayout.CENTER);
        add(eraserSliderPanel);

        add(Box.createVerticalStrut(10));

        // 4. ปุ่มเลือกสี
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
}
