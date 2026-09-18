import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;

// ==========================================
// 1. DOMAIN & MODEL CLASSES (SRP)
// ==========================================

/**
 * เก็บข้อมูลของ Layer ชิ้นเดียว
 */
class Layer {
    private final String name;
    private final BufferedImage image;
    private boolean visible = true;

    public Layer(String name, int width, int height) {
        this.name = name;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public Layer(Layer other) {
        this.name = other.name;
        this.visible = other.visible;
        this.image = new BufferedImage(other.image.getWidth(), other.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = this.image.getGraphics();
        g.drawImage(other.image, 0, 0, null);
        g.dispose();
    }

    public String getName() { return name; }
    public BufferedImage getImage() { return image; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    @Override
    public String toString() {
        return name + (visible ? "" : " (ซ่อน)");
    }
}

/**
 * จัดการ State ของ Layers และ History (Undo/Redo)
 */
class CanvasModel {
    private final List<Layer> layers = new ArrayList<>();
    private final List<List<Layer>> undoStack = new ArrayList<>();
    private final List<List<Layer>> redoStack = new ArrayList<>();
    private final int width;
    private final int height;
    private int activeLayerIndex = 0;
    private final int maxHistory = 20;

    public CanvasModel(int width, int height) {
        this.width = width;
        this.height = height;
        addLayer("Layer 1");
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void addLayer(String name) {
        layers.add(new Layer(name, width, height));
        activeLayerIndex = layers.size() - 1;
    }

    public List<Layer> getLayers() { return layers; }
    public int getActiveLayerIndex() { return activeLayerIndex; }
    public void setActiveLayerIndex(int index) { this.activeLayerIndex = index; }

    public Layer getActiveLayer() {
        if (activeLayerIndex >= 0 && activeLayerIndex < layers.size()) {
            return layers.get(activeLayerIndex);
        }
        return null;
    }

    public void moveLayerUp(int index) {
        if (index < layers.size() - 1) {
            Collections.swap(layers, index, index + 1);
            activeLayerIndex = index + 1;
        }
    }

    public void moveLayerDown(int index) {
        if (index > 0) {
            Collections.swap(layers, index, index - 1);
            activeLayerIndex = index - 1;
        }
    }

    public void saveStateForUndo() {
        List<Layer> snapshot = new ArrayList<>();
        for (Layer l : layers) snapshot.add(new Layer(l));
        undoStack.add(snapshot);
        if (undoStack.size() > maxHistory) undoStack.remove(0);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            List<Layer> snapshot = new ArrayList<>();
            for (Layer l : layers) snapshot.add(new Layer(l));
            redoStack.add(snapshot);

            layers.clear();
            for (Layer l : undoStack.remove(undoStack.size() - 1)) layers.add(new Layer(l));
            if (activeLayerIndex >= layers.size()) activeLayerIndex = layers.size() - 1;
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            List<Layer> snapshot = new ArrayList<>();
            for (Layer l : layers) snapshot.add(new Layer(l));
            undoStack.add(snapshot);

            layers.clear();
            for (Layer l : redoStack.remove(redoStack.size() - 1)) layers.add(new Layer(l));
            if (activeLayerIndex >= layers.size()) activeLayerIndex = layers.size() - 1;
        }
    }

    public BufferedImage getCompositeImage() {
        BufferedImage composite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = composite.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        for (Layer layer : layers) {
            if (layer.isVisible()) {
                g2.drawImage(layer.getImage(), 0, 0, null);
            }
        }
        g2.dispose();
        return composite;
    }
}

// ==========================================
// 2. STRATEGY PATTERN FOR DRAWING TOOLS (OCP, LSP, ISP, DIP)
// ==========================================

interface Tool {
    void drawPoint(Layer layer, Point point, Color color, int size);
    void drawLine(Layer layer, Point start, Point end, Color color, int size);
}

class PencilTool implements Tool {
    @Override
    public void drawPoint(Layer layer, Point point, Color color, int size) {
        if (layer == null || !layer.isVisible()) return;
        Graphics2D g2d = layer.getImage().createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(color);
        g2d.fillOval(point.x - size / 2, point.y - size / 2, size, size);
        g2d.dispose();
    }

    @Override
    public void drawLine(Layer layer, Point start, Point end, Color color, int size) {
        if (layer == null || !layer.isVisible()) return;
        Graphics2D g2d = layer.getImage().createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(start.x, start.y, end.x, end.y);
        g2d.dispose();
    }
}

class EraserTool implements Tool {
    @Override
    public void drawPoint(Layer layer, Point point, Color color, int size) {
        if (layer == null || !layer.isVisible()) return;
        Graphics2D g2d = layer.getImage().createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillOval(point.x - size / 2, point.y - size / 2, size, size);
        g2d.dispose();
    }

    @Override
    public void drawLine(Layer layer, Point start, Point end, Color color, int size) {
        if (layer == null || !layer.isVisible()) return;
        Graphics2D g2d = layer.getImage().createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.Clear);
        g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(start.x, start.y, end.x, end.y);
        g2d.dispose();
    }
}

// ==========================================
// 3. SERVICE FOR EXPORTING FILES (OCP, DIP)
// ==========================================

interface ExportService {
    void export(BufferedImage image, File destination) throws IOException;
    String getExtension();
}

class PngExportService implements ExportService {
    @Override
    public void export(BufferedImage image, File destination) throws IOException {
        ImageIO.write(image, "png", destination);
    }

    @Override
    public String getExtension() { return ".png"; }
}

// ==========================================
// 4. UI COMPONENTS (SRP, DIP)
// ==========================================

/**
 * Component สำหรับวาดภาพ พึ่งพา Abstraction (Tool & CanvasModel)
 */
class CanvasPanel extends JPanel {
    private final CanvasModel model;
    private Tool currentTool = new PencilTool();
    private Color currentColor = Color.BLACK;
    private int currentSize = 5;
    private Point lastPoint = null;

    public CanvasPanel(CanvasModel model) {
        this.model = model;
        setPreferredSize(new Dimension(model.getWidth(), model.getHeight()));
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                model.saveStateForUndo();
                lastPoint = e.getPoint();
                currentTool.drawPoint(model.getActiveLayer(), lastPoint, currentColor, currentSize);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastPoint = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentPoint = e.getPoint();
                if (lastPoint != null) {
                    currentTool.drawLine(model.getActiveLayer(), lastPoint, currentPoint, currentColor, currentSize);
                    repaint();
                }
                lastPoint = currentPoint;
            }
        });
    }

    public void setTool(Tool tool) { this.currentTool = tool; }
    public void setColor(Color color) { this.currentColor = color; }
    public Color getColor() { return currentColor; }
    public void setBrushSize(int size) { this.currentSize = size; }
    public int getBrushSize() { return currentSize; }
    public CanvasModel getModel() { return model; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(model.getCompositeImage(), 0, 0, null);
    }
}

/**
 * Panel ควบคุม Layer
 */
class LayerPanel extends JPanel {
    private final CanvasPanel canvasPanel;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> layerJList = new JList<>(listModel);

    public LayerPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
        setLayout(new BorderLayout());
        setBackground(new Color(216, 175, 230));
        setBorder(new TitledBorder("จัดการ Layer ตรงนี้"));

        refreshLayerList();

        layerJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = layerJList.getSelectedIndex();
                if (index != -1) {
                    canvasPanel.getModel().setActiveLayerIndex(listModel.size() - 1 - index);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(layerJList);
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 2, 2));

        JButton btnAdd = new JButton("+ เพิ่ม");
        JButton btnUp = new JButton("▲ ขึ้น");
        JButton btnDown = new JButton("▼ ลง");
        JButton btnToggle = new JButton("👁️ ซ่อน/แสดง");

        btnAdd.addActionListener(e -> {
            canvasPanel.getModel().addLayer("Layer " + (canvasPanel.getModel().getLayers().size() + 1));
            refreshLayerList();
            canvasPanel.repaint();
        });

        btnUp.addActionListener(e -> {
            int selected = layerJList.getSelectedIndex();
            int actualIndex = listModel.size() - 1 - selected;
            if (selected > 0) {
                canvasPanel.getModel().moveLayerUp(actualIndex);
                refreshLayerList();
                layerJList.setSelectedIndex(selected - 1);
                canvasPanel.repaint();
            }
        });

        btnDown.addActionListener(e -> {
            int selected = layerJList.getSelectedIndex();
            int actualIndex = listModel.size() - 1 - selected;
            if (selected < listModel.size() - 1) {
                canvasPanel.getModel().moveLayerDown(actualIndex);
                refreshLayerList();
                layerJList.setSelectedIndex(selected + 1);
                canvasPanel.repaint();
            }
        });

        btnToggle.addActionListener(e -> {
            int selected = layerJList.getSelectedIndex();
            if (selected != -1) {
                int actualIndex = listModel.size() - 1 - selected;
                Layer l = canvasPanel.getModel().getLayers().get(actualIndex);
                l.setVisible(!l.isVisible());
                canvasPanel.repaint();
                refreshLayerList();
                layerJList.setSelectedIndex(selected);
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnToggle);
        btnPanel.add(btnUp);
        btnPanel.add(btnDown);

        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void refreshLayerList() {
        listModel.clear();
        List<Layer> layers = canvasPanel.getModel().getLayers();
        for (int i = layers.size() - 1; i >= 0; i--) {
            listModel.addElement(layers.get(i).toString());
        }
        int activeInUI = listModel.size() - 1 - canvasPanel.getModel().getActiveLayerIndex();
        layerJList.setSelectedIndex(activeInUI);
    }
}

// ==========================================
// 5. MAIN APPLICATION (ENTRY POINT)
// ==========================================

public class DrawingSoftware extends JFrame {
    private final CanvasPanel canvasPanel;
    private final ExportService exportService = new PngExportService();

    public DrawingSoftware() {
        super("Drawing Software with Layers (SOLID)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        CanvasModel model = new CanvasModel(500, 500);
        canvasPanel = new CanvasPanel(model);
        LayerPanel layerPanel = new LayerPanel(canvasPanel);

        // --- Top Panel (Save, Undo, Redo) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(220, 220, 220));

        JButton btnSave = new JButton("💾 Save PNG");
        JButton btnUndo = new JButton("↩ Undo");
        JButton btnRedo = new JButton("↪ Redo");

        btnSave.addActionListener(e -> exportImage());
        btnUndo.addActionListener(e -> {
            canvasPanel.getModel().undo();
            layerPanel.refreshLayerList();
            canvasPanel.repaint();
        });
        btnRedo.addActionListener(e -> {
            canvasPanel.getModel().redo();
            layerPanel.refreshLayerList();
            canvasPanel.repaint();
        });

        topPanel.add(btnSave);
        topPanel.add(btnUndo);
        topPanel.add(btnRedo);

        // --- Left Panel (Tools) ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(200, 200, 200));
        leftPanel.setPreferredSize(new Dimension(60, 0));

        JToggleButton btnPencil = new JToggleButton("✏️", true);
        JToggleButton btnEraser = new JToggleButton("🧹", false);
        ButtonGroup toolGroup = new ButtonGroup();
        toolGroup.add(btnPencil);
        toolGroup.add(btnEraser);

        btnPencil.addActionListener(e -> canvasPanel.setTool(new PencilTool()));
        btnEraser.addActionListener(e -> canvasPanel.setTool(new EraserTool()));

        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnPencil);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnEraser);

        // --- Right Panel (Color, Size & Layers) ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(280, 0));

        JPanel stylePanel = new JPanel(new GridLayout(3, 1, 5, 5));
        stylePanel.setBackground(new Color(245, 180, 80));
        stylePanel.setBorder(new TitledBorder("จัดการสีและปรับขนาด"));

        JButton btnColor = new JButton("เลือกสี (Color)");
        btnColor.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "เลือกสีแปรง", canvasPanel.getColor());
            if (chosen != null) {
                canvasPanel.setColor(chosen);
                btnColor.setBackground(chosen);
            }
        });

        JSlider sizeSlider = new JSlider(1, 50, canvasPanel.getBrushSize());
        sizeSlider.setOpaque(false);
        JLabel lblSize = new JLabel("ขนาดดินสอ/ยางลบ: " + canvasPanel.getBrushSize(), SwingConstants.CENTER);

        sizeSlider.addChangeListener(e -> {
            canvasPanel.setBrushSize(sizeSlider.getValue());
            lblSize.setText("ขนาดดินสอ/ยางลบ: " + sizeSlider.getValue());
        });

        stylePanel.add(btnColor);
        stylePanel.add(lblSize);
        stylePanel.add(sizeSlider);

        rightPanel.add(stylePanel);
        rightPanel.add(layerPanel);

        // --- Center Canvas Wrapper ---
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(210, 210, 210));
        centerWrapper.add(canvasPanel);

        // Assemble Layout
        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(centerWrapper, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private void exportImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("บันทึกไฟล์");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(exportService.getExtension())) {
                file = new File(file.getAbsolutePath() + exportService.getExtension());
            }
            try {
                exportService.export(canvasPanel.getModel().getCompositeImage(), file);
                JOptionPane.showMessageDialog(this, "บันทึกสำเร็จที่:\n" + file.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดในการบันทึก: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DrawingSoftware().setVisible(true));
    }
}