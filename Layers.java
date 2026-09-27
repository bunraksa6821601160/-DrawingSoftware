import java.awt.*;
import java.util.List;
import javax.swing.*;

public class Layers extends JPanel {

    private DrawingModel model;
    private DefaultListModel<String> listModel;
    private JList<String> layerJList;
    private JButton toggleAllBtn;

    public Layers(DrawingModel model, PaintPanel paintPanel) {
        this.model = model;

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Layers"));

        // รายการ Layer
        listModel = new DefaultListModel<>();
        layerJList = new JList<>(listModel);
        layerJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // เลือก Layer
        layerJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selected = layerJList.getSelectedIndex();

                if (selected >= 0) {
                    model.setActiveLayerIndex(selected);
                }
            }
        });

        add(new JScrollPane(layerJList), BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new GridLayout(4, 2, 3, 3));

        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Delete");
        JButton upBtn = new JButton("Up");
        JButton downBtn = new JButton("Down");
        JButton toggleSingleBtn = new JButton("On/Off");
        JButton renameBtn = new JButton("Rename");

        toggleAllBtn = new JButton("on");

        // ยังไม่ให้ปุ่มทำงานจริง
        controlPanel.add(addBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(upBtn);
        controlPanel.add(downBtn);
        controlPanel.add(toggleSingleBtn);
        controlPanel.add(renameBtn);
        controlPanel.add(new JLabel("Global:"));
        controlPanel.add(toggleAllBtn);

        add(controlPanel, BorderLayout.SOUTH);

        // แสดง Layer ที่มีอยู่
        updateLayerList();
    }

    public void updateLayerList() {
        listModel.clear();

        List<Layer> layers = model.getLayers();

        for (Layer layer : layers) {
            String status = layer.isVisible() ? "[ON]" : "[OFF]";
            listModel.addElement(status + " " + layer.getName());
        }

        // เลือก Active Layer
        int activeIdx = model.getActiveLayerIndex();

        if (activeIdx >= 0 && activeIdx < listModel.size()) {
            layerJList.setSelectedIndex(activeIdx);
        }
    }
}