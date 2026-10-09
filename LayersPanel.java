import java.awt.*;
import java.util.List;
import javax.swing.*;

public class LayersPanel extends JPanel {

    private DrawingModel model ;
    private DefaultListModel<String> listModel; //ใช้แสดงเลเยอร์ที่มีอยู่
    private JList<String> layerJList;
    private JButton toggleAllBtn;

    public LayersPanel(DrawingModel model, PaintPanel paintPanel) {
        this.model = model ;

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Layers"));

        // รายการ Layer
        listModel = new DefaultListModel<>();
        layerJList = new JList<>(listModel);
        layerJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//ทำให้เลือกได้ตัวเดียว

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

        //----------------ActionListener-------------------------

        //ปุ่ม Add
        addBtn.addActionListener(e -> {
            model.addLayer();
            updateLayerList();
        });

        //ปุ่ม Delete
        deleteBtn.addActionListener(e -> {
            model.removeActiveLayer();
            paintPanel.repaint();//เพิ่มมา
            updateLayerList();
        });

        //ปุ่ม Up
        upBtn.addActionListener(e -> {moveLayer(-1); paintPanel.repaint(); });

        //ปุ่ม Down
        downBtn.addActionListener(e -> {moveLayer(1); paintPanel.repaint();});

        //ปุ่ม On/Off
        toggleSingleBtn.addActionListener(e -> {
            model.saveStateForUndo();
            Layer active = model.getActiveLayer();
            if (active != null) {
                active.setVisible(!active.isVisible());
                paintPanel.repaint();
                updateLayerList();
            }
        });

        //ปุ่ม Rename
        renameBtn.addActionListener(e -> {
            model.saveStateForUndo();
            Layer active = model.getActiveLayer();
            if (active != null) {
                String newName = JOptionPane.showInputDialog(this, "Name the new layer:", active.getName());
                if (newName != null && !newName.trim().isEmpty()) {
                    active.setName(newName.trim());
                    updateLayerList();
                }
            }
        });

        //ปุ่ม Global
        toggleAllBtn.addActionListener(e -> {
            model.toggleGlobalVisibility();
            toggleAllBtn.setText(model.isGlobalVisible() ? "on" : "off");
            paintPanel.repaint();//เพิ่มมา
            updateLayerList();
        });

        //เพิ่มปุ่มลงใน controlPanel
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

    /**
     * เคลื่อนย้ายเลเยอร์
     * @param direction
     */
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

    /**
     * อัปเดตเลเยอร์ให้เป็นปัจจุบัน
     */
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