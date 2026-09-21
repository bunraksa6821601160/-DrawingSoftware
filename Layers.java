import javax.swing.*;
import java.awt.*;

public class Layers extends JPanel {
    private DrawingPanel drawingPanel;
    private JPanel layerPanel;

    public Layers(DrawingPanel Panel) {
        drawingPanel = Panel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Layers", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 18));

        layerPanel = new JPanel();
        layerPanel.setLayout(new BoxLayout(layerPanel, BoxLayout.Y_AXIS));

        refreshLayers();
    
        

        JButton addButton = new JButton("+");
        JButton deleteButton = new JButton("-");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

         // ปุ่ม +
        addButton.addActionListener(e -> {
            drawingPanel.addLayer();
            refreshLayers();
        });

        // ปุ่ม -
        deleteButton.addActionListener(e -> {
        int index = drawingPanel.getCurrentLayer();

        if (drawingPanel.getLayerCount() > 1) {
            drawingPanel.removeLayer(index);
            refreshLayers();
    }
});
        add(title, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(layerPanel);
        scrollPane.setPreferredSize(new Dimension(180, 500));
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(190, 600));
    }

    private void refreshLayers() {
    layerPanel.removeAll();

    for (int i = 0; i < drawingPanel.getLayerCount(); i++) {

        int index = i;
        Layer layer = drawingPanel.getLayer(index);

        // แถวของแต่ละ Layer
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 5));

        row.setPreferredSize(new Dimension(185, 40));
        row.setMaximumSize(new Dimension(185, 40));

        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        

        // ปุ่มเปิด/ปิด
        JButton visibleButton = new JButton();
        visibleButton.setPreferredSize(new Dimension(60, 30));

       

        if (layer.isVisible()) {
            visibleButton.setText("On");
        } else {
            visibleButton.setText("Off");
        }

        // ปุ่มชื่อ Layer
        JButton layerButton = new JButton(layer.getName());
        layerButton.setPreferredSize(new Dimension(110, 30));

        // ถ้าเป็น Layer ที่กำลังเลือกอยู่
        if (index == drawingPanel.getCurrentLayer()) {
            row.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            row.setBackground(Color.CYAN);
        }

        // กดชื่อ Layer
        layerButton.addActionListener(e -> {
            drawingPanel.setCurrentLayer(index);
            refreshLayers();
        });

        // กดเปิด/ปิด
        visibleButton.addActionListener(e -> {
            layer.setVisible(!layer.isVisible());

            if (layer.isVisible()) {
                visibleButton.setText("On");
            } else {
                visibleButton.setText("Off");
            }

            drawingPanel.repaint();
        });

        row.add(visibleButton);
        row.add(layerButton);

        layerPanel.add(row);
    }

    layerPanel.revalidate();
    layerPanel.repaint();
}
}
