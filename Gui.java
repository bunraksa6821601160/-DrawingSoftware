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

    DrawingPanel drawingPanel = new DrawingPanel();
    cp.add(drawingPanel,BorderLayout.CENTER);
    
//ทำให้toolอยู่ตรงกลางทางซ้าย
    JPanel leftPanel = new JPanel(new GridBagLayout());
    Tools tools = new Tools();
    leftPanel.add(tools);
    cp.add(leftPanel, BorderLayout.WEST);


}
    
public void Finally(){
    setSize(1000,700);
    setVisible(true);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
}

}

