import java.awt.*;
import javax.swing.*;

public class Gui extends JFrame {

    Container cp;

    private DrawingModel model;
    private ToolsPanel toolsPanel;
    private MenuPanel menuPanel;
    private PaintPanel paintPanel;
    private ZoomPanel zoomPanel;
    private Layers layersPanel;

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
        // 2. สร้าง Data Model หลัก
        model = new DrawingModel();

        // 3. สร้าง UI Panel แต่ละส่วน
        paintPanel = new PaintPanel(model);
        toolsPanel = new ToolsPanel(model);
        layersPanel = new Layers(model,paintPanel);
        menuPanel = new MenuPanel();
        zoomPanel = new ZoomPanel(model, paintPanel);

        //เพิ่มมา
        toolsPanel.setPreferredSize(new Dimension(140, 0));
        layersPanel.setPreferredSize(new Dimension(220, 0));

        // MenuPanel อยู่ด้านบน (NORTH)
        add(menuPanel, BorderLayout.NORTH);

        // ToolsPanel อยู่ด้านซ้าย (WEST)
        add(toolsPanel, BorderLayout.WEST);

        JPanel canvasWrapper = new JPanel(new GridBagLayout());
        canvasWrapper.setBackground(Color.LIGHT_GRAY);
        canvasWrapper.add(paintPanel);

        // PaintPanel อยู่ตรงกลาง ครอบด้วย JScrollPane เพื่อให้มี Scrollbar เลื่อนได้ (CENTER)
        //JScrollPane scrollPane = new JScrollPane(paintPanel);
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // จุดเริ่มต้นการทำงานของโปรแกรม (Main Method)
    public static void main(String[] args) {
        new Gui();
    }
}
