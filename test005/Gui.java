import java.awt.*;
import javax.swing.*;

public class Gui extends JFrame {
    private DrawingModel model;
    private ToolsPanel toolsPanel;
    private MenuPanel menuPanel;
    private PaintPanel paintPanel;
    private ZoomPanel zoomPanel;
    private Layers layersPanel;

    public Gui() {
        // 1. ตั้งค่า JFrame เบื้องต้น
        setTitle("2D Drawing Studio Pro");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 850);
        setLocationRelativeTo(null); // แสดงกลางหน้าจอ

        // 2. สร้าง Data Model หลัก
        model = new DrawingModel();

        // 3. สร้าง UI Panel แต่ละส่วน
        paintPanel = new PaintPanel(model);
        toolsPanel = new ToolsPanel(model);
        menuPanel = new MenuPanel(model, paintPanel);
        zoomPanel = new ZoomPanel(model, paintPanel);
        layersPanel = new Layers(model);

        // 4. จัดวาง Layout ของ Window (BorderLayout)
        setLayout(new BorderLayout());

        // MenuPanel อยู่ด้านบน (NORTH)
        add(menuPanel, BorderLayout.NORTH);

        // ToolsPanel อยู่ด้านซ้าย (WEST)
        add(toolsPanel, BorderLayout.WEST);

        // PaintPanel อยู่ตรงกลาง ครอบด้วย JScrollPane เพื่อให้มี Scrollbar เลื่อนได้ (CENTER)
        JScrollPane scrollPane = new JScrollPane(paintPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // LayersPanel อยู่ด้านขวา (EAST)
        add(layersPanel, BorderLayout.EAST);

        // ZoomPanel อยู่ด้านล่าง (SOUTH)
        add(zoomPanel, BorderLayout.SOUTH);
    }

    // จุดเริ่มต้นการทำงานของโปรแกรม (Main Method)
    public static void main(String[] args) {
        // ตั้งค่า Look and Feel ให้เข้ากับระบบปฏิบัติการ (System Look and Feel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // เรียกใช้งาน GUI บน Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            Gui frame = new Gui();
            frame.setVisible(true);
        });
    }
}
