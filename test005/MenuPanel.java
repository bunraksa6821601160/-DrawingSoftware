import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuPanel extends JPanel {
    private DrawingModel model;
    private PaintPanel paintPanel;
    private File currentSaveFile;

    public MenuPanel(DrawingModel model, PaintPanel paintPanel) {
        this.model = model;
        this.paintPanel = paintPanel;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JMenuBar menuBar = new JMenuBar();

        // 1. File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As");

        newItem.addActionListener(e -> {
            // สร้าง JFrame หน้าต่างใหม่
            SwingUtilities.invokeLater(() -> new Gui().setVisible(true));
        });

        openItem.addActionListener(e -> openPNGImage());
        saveItem.addActionListener(e -> savePNGImage(false));
        saveAsItem.addActionListener(e -> savePNGImage(true));

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);

        // 2. Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem howToItem = new JMenuItem("How to use");

        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Java Swing Drawing Application\nVersion 1.0", "About", JOptionPane.INFORMATION_MESSAGE));
        howToItem.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "วิธีใช้งานเบื้องต้น:\n" +
                "1. เลือกเครื่องมือและขนาดจาก ToolsPanel\n" +
                "2. วาดภาพบน PaintPanel ใน Active Layer ปัจจุบัน\n" +
                "3. ลบข้อความได้โดยคลิกเครื่องมือ Text แล้วกดเลือกข้อความบน Canvas\n" +
                "4. ใช้ Ctrl+Z เพื่อ Undo และ Ctrl+Y เพื่อ Redo", 
                "How to use", JOptionPane.INFORMATION_MESSAGE));

        helpMenu.add(aboutItem);
        helpMenu.add(howToItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        add(menuBar);

        // 3. ปุ่ม Undo / Redo บน MenuPanel
        JButton undoBtn = new JButton("Undo (Ctrl+Z)");
        JButton redoBtn = new JButton("Redo (Ctrl+Y)");

        Action undoAction = new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.undo();
                paintPanel.repaint();
            }
        };

        Action redoAction = new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.redo();
                paintPanel.repaint();
            }
        };

        undoBtn.addActionListener(undoAction);
        redoBtn.addActionListener(redoAction);

        add(undoBtn);
        add(redoBtn);

        // 4. การผูก Key Shortcuts (Ctrl+Z และ Ctrl+Y)
        setupShortcuts(undoAction, redoAction);
    }

    private void setupShortcuts(Action undoAction, Action redoAction) {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "UndoShort");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "RedoShort");

        actionMap.put("UndoShort", undoAction);
        actionMap.put("RedoShort", redoAction);
    }

    private void openPNGImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                Layer active = model.getActiveLayer();
                if (active != null && img != null) {
                    model.saveStateForUndo();
                    active.setBackgroundImage(img); // ใส่ใน Active Layer เลเยอร์เดียว
                    paintPanel.repaint();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "ไม่สามารถเปิดไฟล์รูปภาพได้", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void savePNGImage(boolean saveAs) {
        if (saveAs || currentSaveFile == null) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if (!f.getName().toLowerCase().endsWith(".png")) {
                    f = new File(f.getAbsolutePath() + ".png");
                }
                currentSaveFile = f;
            } else {
                return;
            }
        }

        try {
            BufferedImage image = new BufferedImage(paintPanel.getWidth(), paintPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            paintPanel.paint(g2d);
            g2d.dispose();
            ImageIO.write(image, "PNG", currentSaveFile);
            JOptionPane.showMessageDialog(this, "บันทึกไฟล์เรียบร้อยแล้ว", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดในการบันทึกไฟล์", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
