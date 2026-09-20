import javax.swing.*;
import java.awt.*;

public class Layers extends JPanel {
    private DrawingPanel drawingPanel;

    public Layers(DrawingPanel Panel) {
        drawingPanel = Panel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Layers", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 18));

        DefaultListModel<String> model = new DefaultListModel<>();//เก็บข้อมูลของJList
    
        model.addElement("Layer 1");

        // แสดงรายการ Layer
        JList<String> layerList = new JList<>(model);

        //เลือกLayer
        layerList.addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        int index = layerList.getSelectedIndex();

        if (index != -1) {
            drawingPanel.setCurrentLayer(index);
        }
    }
});

        JButton addButton = new JButton("+");
        JButton deleteButton = new JButton("-");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

         // ปุ่ม +
        addButton.addActionListener(e -> {
            drawingPanel.addLayer();
            int number = drawingPanel.getLayerCount();
            model.addElement("Layer " + number);
            layerList.setSelectedIndex(number - 1);
        });

        // ปุ่ม -
        deleteButton.addActionListener(e -> { int index = layerList.getSelectedIndex();
            if (index != -1) {
                model.remove(index);
                drawingPanel.removeLayer(index);
            }
        });


        add(title, BorderLayout.NORTH);
        add(new JScrollPane(layerList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(180, 0));
    }
}
