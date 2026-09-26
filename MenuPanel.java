import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MenuPanel extends JPanel {
    private DrawingModel model;
    private PaintPanel paintPanel;
    private File currentSaveFile;

    //MenuPanel(DrawingModel model, PaintPanel paintPanel) ของเก่า
    public MenuPanel(DrawingModel model, PaintPanel paintPanel, Layers layers) {
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
        fileMenu.addSeparator();  //เพิ่มเส้นแบ่งแนวนอน (Menu Separator Line) แทรกระหว่างรายการเมนู
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);

        // 2. Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem howToItem = new JMenuItem("How to use");

        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Java Swing Drawing Application\nVersion 1.0", "About", JOptionPane.INFORMATION_MESSAGE));
        howToItem.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Basic Usage:\n" +
                "1. Select a tool and size from ToolsPanel\n" +
                "2. Draw on PaintPanel within the current Active Layer\n" +
                "3. Delete text by selecting the Text tool and clicking the text on the Canvas\n" +
                "4. Use Ctrl+Z to Undo and Ctrl+Y to Redo", 
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
                layers.updateLayerList();//เพิ่มมา
                paintPanel.repaint();
            }
        };

        Action redoAction = new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.redo();
                layers.updateLayerList();//เพิ่มมา
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
                JOptionPane.showMessageDialog(this, "Failed to open image file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void savePNGImage(boolean saveAs) {
        if (saveAs || currentSaveFile == null) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if (!f.getName().toLowerCase().endsWith(".jpg")) {
                    f = new File(f.getAbsolutePath() + ".jpg");
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
            JOptionPane.showMessageDialog(this, "File saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
