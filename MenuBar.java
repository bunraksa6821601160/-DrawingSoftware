import javax.swing.*;
import java.awt.*;

public class MenuBar {
    public JMenuBar createMenuBar(){
        JMenuBar mb = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem newItem = new JMenuItem("New");
    JMenuItem openItem = new JMenuItem("Open");
    JMenuItem saveItem = new JMenuItem("Save");
    JMenuItem saveasItem = new JMenuItem("Save As");
    JMenuItem exitItem = new JMenuItem("Exit");

    JMenu EditMenu = new JMenu("Edit");
    JMenuItem copyItem = new JMenuItem("Copy");
    JMenuItem pasteItem = new JMenuItem("Paste");
    JMenuItem cutItem = new JMenuItem("Cut");


    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = new JMenuItem("About");
    JMenuItem howtoItem = new JMenuItem("How to Use");

    JMenu undoMenu = new JMenu("Undo");
    JMenu redoMenu = new JMenu("Redo");

    Font font = new Font("Tahoma",Font.PLAIN,16);
    fileMenu.setFont(font);
    EditMenu.setFont(font);
    helpMenu.setFont(font);
    undoMenu.setFont(font);
    redoMenu.setFont(font);

    newItem.setFont(font);
    openItem.setFont(font);
    saveItem.setFont(font);
    saveasItem.setFont(font);
    exitItem.setFont(font);

    copyItem.setFont(font);
    pasteItem.setFont(font);
    cutItem.setFont(font);
    
    aboutItem.setFont(font);
    howtoItem.setFont(font);

    fileMenu.add(newItem);
    fileMenu.add(openItem);
    fileMenu.add(saveItem);
    fileMenu.add(saveasItem);
    fileMenu.add(exitItem);

    EditMenu.add(copyItem);
    EditMenu.add(pasteItem);
    EditMenu.add(cutItem);

    helpMenu.add(aboutItem);
    helpMenu.add(howtoItem);


    mb.add(fileMenu);
    mb.add(EditMenu);
    mb.add(helpMenu);
    mb.add(undoMenu);
    mb.add(redoMenu);

    return mb;
}
    }
