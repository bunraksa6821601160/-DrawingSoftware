import java.awt.*;
import java.util.List;
import javax.swing.*;

public class Layers extends JPanel {
    private DrawingModel model;
    private DefaultListModel<String> listModel;
    private JList<String> layerJList;
    private JButton toggleAllBtn;

    //Layers(DrawingModel model)//อันเก่า
    public Layers(DrawingModel model, PaintPanel paintPanel) {
        this.model = model;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Layers"));

        listModel = new DefaultListModel<>();
        layerJList = new JList<>(listModel);
        layerJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        layerJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selected = layerJList.getSelectedIndex();
                if (selected >= 0) {
                    model.setActiveLayerIndex(selected);
                }
            }
        });

        add(new JScrollPane(layerJList), BorderLayout.CENTER);

        // Control Panel สำหรับจัดการ Layer
        JPanel controlPanel = new JPanel(new GridLayout(4, 2, 3, 3));

        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Delete");
        JButton upBtn = new JButton("Up");
        JButton downBtn = new JButton("Down");
        JButton toggleSingleBtn = new JButton("On/Off");
        JButton renameBtn = new JButton("Rename");
        toggleAllBtn = new JButton("on"); // ปุ่มหลักสลับสถานะทุกเลเยอร์

        addBtn.addActionListener(e -> {
            model.addLayer();
            updateLayerList();
        });

        deleteBtn.addActionListener(e -> {
            model.removeActiveLayer();
            paintPanel.repaint();//เพิ่มมา
            updateLayerList();
        });

        upBtn.addActionListener(e -> {moveLayer(-1); paintPanel.repaint(); });
        downBtn.addActionListener(e -> {moveLayer(1); paintPanel.repaint();});

        toggleSingleBtn.addActionListener(e -> {
            Layer active = model.getActiveLayer();
            if (active != null) {
                active.setVisible(!active.isVisible());
                paintPanel.repaint();//เพิ่มมา
                updateLayerList();
            }
        });

        renameBtn.addActionListener(e -> {
            Layer active = model.getActiveLayer();
            if (active != null) {
                String newName = JOptionPane.showInputDialog(this, "Name the new layer:", active.getName());
                if (newName != null && !newName.trim().isEmpty()) {
                    active.setName(newName.trim());
                    updateLayerList();
                }
            }
        });

        toggleAllBtn.addActionListener(e -> {
            model.toggleGlobalVisibility();
            toggleAllBtn.setText(model.isGlobalVisible() ? "on" : "off");
            paintPanel.repaint();//เพิ่มมา
            updateLayerList();
        });

        controlPanel.add(addBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(upBtn);
        controlPanel.add(downBtn);
        controlPanel.add(toggleSingleBtn);
        controlPanel.add(renameBtn);
        controlPanel.add(new JLabel("Global:"));
        controlPanel.add(toggleAllBtn);

        add(controlPanel, BorderLayout.SOUTH);
        updateLayerList();
    }

    private void moveLayer(int direction) {
        int idx = model.getActiveLayerIndex();
        List<Layer> layers = model.getLayers();
        int newIdx = idx + direction;

        if (idx >= 0 && newIdx >= 0 && newIdx < layers.size()) {
            model.saveStateForUndo();
            Layer temp = layers.get(idx);
            layers.set(idx, layers.get(newIdx));
            layers.set(newIdx, temp);
            model.setActiveLayerIndex(newIdx);
            updateLayerList();
        }
    }

    public void updateLayerList() {
        listModel.clear();
        List<Layer> layers = model.getLayers();
        for (Layer l : layers) {
            String status = l.isVisible() ? "[ON]" : "[OFF]";
            listModel.addElement(status + " " + l.getName());
        }
        int activeIdx = model.getActiveLayerIndex();
        if (activeIdx >= 0 && activeIdx < listModel.size()) {
            layerJList.setSelectedIndex(activeIdx);
        }
    }
}