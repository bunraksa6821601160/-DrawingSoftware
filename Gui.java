import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class Gui extends JFrame {

    Container cp;

    private DrawingModel model;
    private ToolsPanel toolsPanel;
    private MenuPanel menuPanel;
    private PaintPanel paintPanel;
    private ZoomPanel zoomPanel;
    private LayersPanel layersPanel;

    public Gui() {
        super("2D Drawing Studio Pro");

        Initial();
        setComponent();
        Finally();
    }
    public void Initial(){
        cp = getContentPane();
        cp.setLayout(new BorderLayout());
    }

    public void setComponent(){
        //สร้าง Data Model หลัก
        model = new DrawingModel();

        //สร้าง UI Panel แต่ละส่วน
        paintPanel = new PaintPanel(model);
        toolsPanel = new ToolsPanel(model);
        layersPanel = new LayersPanel(model,paintPanel);
        menuPanel = new MenuPanel(model, paintPanel, layersPanel);
        zoomPanel = new ZoomPanel(model, paintPanel);

        //กำหนดขนาดของ toolsPanel และ layersPanel
        toolsPanel.setPreferredSize(new Dimension(140, 0));
        layersPanel.setPreferredSize(new Dimension(220, 0));

        // MenuPanel อยู่ด้านบน (NORTH)
        add(menuPanel, BorderLayout.NORTH);

        // ToolsPanel อยู่ด้านซ้าย (WEST)
        add(toolsPanel, BorderLayout.WEST);

        //ใช้เก็บ paintPanel
        JPanel canvasWrapper = new JPanel(new GridBagLayout());
        canvasWrapper.setBackground(Color.LIGHT_GRAY);
        canvasWrapper.add(paintPanel);

        // PaintPanel อยู่ตรงกลาง ครอบด้วย JScrollPane เพื่อให้มี Scrollbar เลื่อนได้ (CENTER)
        JScrollPane scrollPane = new JScrollPane(canvasWrapper);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // LayersPanel อยู่ด้านขวา (EAST)
        add(layersPanel, BorderLayout.EAST);

        // ZoomPanel อยู่ด้านล่าง (SOUTH)
        add(zoomPanel, BorderLayout.SOUTH);
    }

    public void Finally(){
        setSize(1280, 800);
        setLocationRelativeTo(null); // แสดงกลางหน้าจอ
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //ดัก Event ตอนกดปุ่มปิดหน้าต่าง (ปุ่ม X)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });

        setVisible(true);
    }

    //แสดง Dialog ยืนยันก่อนปิดโปรแกรม
    private void confirmAndExit() {
        // เช็กว่ามีการแก้ไขงานหรือไม่ (ถ้าใน DrawingModel มีสถานะ isModified())
        boolean isModified = model.isModified(); // *สมมติว่าใน DrawingModel มีสถานะเก็บไว้

        if (isModified) {
            int option = JOptionPane.showConfirmDialog(
                this,
                "You have unsaved work. Would you like to save your changes before exiting?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (option == JOptionPane.YES_OPTION) {
                // เรียกใช้เมธอดเซฟภาพจาก menuPanel (หรือ paintPanel)
                boolean saveSuccess = menuPanel.savePNGImage(false); 
                if (saveSuccess) {
                    this.dispose(); // ปิดหน้าต่างเมื่อบันทึกสำเร็จ
                }
            } else if (option == JOptionPane.NO_OPTION) {
                this.dispose(); // ปิดโดยไม่บันทึก
            }
            // ถ้าเลือก CANCEL หรือกดปิด Pop-up จะไม่ทำอะไรเลย (เปิดหน้าต่างทำงานต่อ)
        } else {
            this.dispose(); // ถ้างานไม่มีการเปลี่ยนแปลง ปิดได้ทันที
        }
    }

    // จุดเริ่มต้นการทำงานของโปรแกรม (Main Method)
    public static void main(String[] args) {
        new Gui();
    }
}
