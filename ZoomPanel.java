import javax.swing.*;
import java.awt.*;


public class ZoomPanel extends JPanel {
    private JLabel zoomLabel;

    public ZoomPanel(DrawingPanel drawing){
        

        setLayout(new FlowLayout());

        JButton zoomOut = new JButton("-");
        zoomLabel = new JLabel("100%");
        JButton zoomIn = new JButton("+");

         zoomOut.addActionListener(e -> {
            drawing.zoomOut();
            zoomLabel.setText(drawing.getZoom() + "%");
        });

        zoomIn.addActionListener(e -> {
            drawing.zoomIn();
            zoomLabel.setText(drawing.getZoom() + "%");
        });

        add(zoomOut);
        add(zoomLabel);
        add(zoomIn);
    }
}
