import java.awt.*;
import javax.swing.*;

public class ZoomPanel extends JPanel {
    private DrawingModel model;
    private PaintPanel paintPanel;
    private JLabel zoomLabel;

    public ZoomPanel(DrawingModel model, PaintPanel paintPanel) {
        this.model = model;
        this.paintPanel = paintPanel;
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBorder(BorderFactory.createEtchedBorder());

        JButton zoomOutBtn = new JButton("-");
        JButton zoomInBtn = new JButton("+");
        JSlider zoomSlider = new JSlider(25, 300, 100); // 25% ถึง 300%
        zoomLabel = new JLabel("100%");

        zoomSlider.addChangeListener(e -> {
            double scale = zoomSlider.getValue() / 100.0;
            model.setZoomScale(scale);
            zoomLabel.setText(zoomSlider.getValue() + "%");
            paintPanel.repaint();
        });

        zoomOutBtn.addActionListener(e -> {
            int current = zoomSlider.getValue();
            zoomSlider.setValue(Math.max(25, current - 10));
        });

        zoomInBtn.addActionListener(e -> {
            int current = zoomSlider.getValue();
            zoomSlider.setValue(Math.min(300, current + 10));
        });

        add(new JLabel("Zoom: "));
        add(zoomOutBtn);
        add(zoomSlider);
        add(zoomInBtn);
        add(zoomLabel);
    }
}
