import javax.swing.*;
import java.awt.*;

public class Gui extends JFrame{
    Container cp ;
public Gui(){
    super("Drawing");
    ImageIcon img = new ImageIcon("./pic/Icon.png");
    setIconImage(img.getImage());
    Intial();
    setComponent();
    Finally();
}

public void Intial(){
    cp = getContentPane();
    cp.setLayout(new BorderLayout());
}

public void setComponent(){
    MenuBar menu = new MenuBar();
    setJMenuBar(menu.createMenuBar());

//พื้นที่วาด    
    // DrawingPanel drawingPanel = new DrawingPanel();
    // cp.add(drawingPanel,BorderLayout.CENTER);
    
    DrawingPanel drawingPanel = new DrawingPanel();
    JScrollPane scrollPane = new JScrollPane(drawingPanel);

    drawingPanel.setPreferredSize(new Dimension(800, 600));
    cp.add(scrollPane, BorderLayout.CENTER);

//ทำให้toolอยู่ตรงกลางทางซ้าย
    JPanel leftPanel = new JPanel(new GridBagLayout());
    Tools tools = new Tools();
    leftPanel.add(tools);
    cp.add(leftPanel, BorderLayout.WEST);

//zoom
    ZoomPanel zoomPanel = new ZoomPanel(drawingPanel);
    cp.add(zoomPanel, BorderLayout.SOUTH);
    
//Layers
    JPanel rightPanel = new JPanel(new GridBagLayout());
    Layers layers = new Layers(drawingPanel);
    rightPanel.add(layers);

    cp.add(rightPanel, BorderLayout.EAST);
}
    
public void Finally(){
    setSize(1000,700);
    setVisible(true);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
}

}

