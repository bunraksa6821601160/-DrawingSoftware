import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {

    public MenuPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JMenuBar menuBar = new JMenuBar();

        // 1. File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();  //เพิ่มเส้นแบ่งแนวนอน (Menu Separator Line) แทรกระหว่างรายการเมนู
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);

        // 2. Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem howToItem = new JMenuItem("How to use");

        helpMenu.add(aboutItem);
        helpMenu.add(howToItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        add(menuBar);

        // 3. ปุ่ม Undo / Redo บน MenuPanel
        JButton undoBtn = new JButton("Undo (Ctrl+Z)");
        JButton redoBtn = new JButton("Redo (Ctrl+Y)");

        add(undoBtn);
        add(redoBtn);

    }
}
