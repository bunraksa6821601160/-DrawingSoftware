import javax.swing.*;
import java.awt.*;

public class Tools extends JPanel {
    int width = 40 ;
    int height = 40 ;
    public Tools(){
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);//up->down

        JButton penButton = new JButton(resizeIcon("./pic/pen.png", width, height));
        JButton pencilButton = new JButton(resizeIcon("./pic/pencil.png", width, height));
        JButton eraserButton = new JButton(resizeIcon("./pic/eraser.png",  width, height));
        JButton lineButton = new JButton(resizeIcon("./pic/line.png",  width, height));
        JButton circleButton = new JButton(resizeIcon("./pic/circle.png",  width, height));
        JButton rectangle = new JButton(resizeIcon("./pic/rectangle.png", width, height));
        JButton color = new JButton("Color");

        Dimension buttonSize = new Dimension(width, height);
        penButton.setPreferredSize(buttonSize);
        pencilButton.setPreferredSize(buttonSize);
        eraserButton.setPreferredSize(buttonSize);
        lineButton.setPreferredSize(buttonSize);
        circleButton.setPreferredSize(buttonSize);
        rectangle.setPreferredSize(buttonSize);
        color.setPreferredSize(buttonSize);

        

        penButton.setToolTipText("Pen");
        pencilButton.setToolTipText("Pencil");
        eraserButton.setToolTipText("Eraser");
        lineButton.setToolTipText("Line");
        circleButton.setToolTipText("Circle");
        rectangle.setToolTipText("Rectangle");
        color.setToolTipText("Color");


        toolBar.add(penButton);
        toolBar.add(pencilButton);
        toolBar.add(eraserButton);
        toolBar.add(lineButton);
        toolBar.add(circleButton);
        toolBar.add(rectangle);
        toolBar.add(color);


        //Size
        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new BorderLayout());

        JLabel sizeLabel = new JLabel("Size");

        JSlider sizeSlider = new JSlider(JSlider.VERTICAL,1, 50, 5);

        sizePanel.add(sizeLabel, BorderLayout.NORTH);
        sizePanel.add(sizeSlider, BorderLayout.CENTER);




        add(toolBar);
        add(sizePanel);
    }
    private ImageIcon resizeIcon(String path, int width, int height) {
    ImageIcon icon = new ImageIcon(path);
    Image image = icon.getImage();
    Image resized = image.getScaledInstance(
        width, height, Image.SCALE_SMOOTH
    );
    return new ImageIcon(resized);
}
}
