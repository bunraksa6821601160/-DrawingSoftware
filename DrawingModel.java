import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DrawingModel {
    private List<Layer> layers = new ArrayList<>();
    private int activeLayerIndex = 0;

    private DrawnShape.ShapeType currentTool = DrawnShape.ShapeType.PENCIL;
    private Color currentColor = Color.BLACK;
    private float pencilSize = 2.0f;
    private float opacity = 1.0f;
    private float eraserSize = 15.0f;
    private double zoomScale = 1.0; 
    private boolean globalVisibility = true; // ปุ่ม On/Off รวมทุกเลเยอร์

    private Stack<List<Layer>> undoStack = new Stack<>();
    private Stack<List<Layer>> redoStack = new Stack<>();

    public DrawingModel() {
        layers.add(new Layer("Layer 1"));
    }

    // Getters & Setters สำหรับ Layer
    public List<Layer> getLayers() { return layers; }
    
    public Layer getActiveLayer() {
        if (activeLayerIndex >= 0 && activeLayerIndex < layers.size()) {
            return layers.get(activeLayerIndex);
        }
        return null;
    }

    public int getActiveLayerIndex() { return activeLayerIndex; }
    
    public void setActiveLayerIndex(int index) { 
        if (index >= 0 && index < layers.size()) {
            this.activeLayerIndex = index; 
        }
    }

    public void addLayer() {
        saveStateForUndo();
        layers.add(new Layer("Layer " + (layers.size() + 1)));
        activeLayerIndex = layers.size() - 1;
    }

    public void removeActiveLayer() {
        if (layers.size() > 1 && activeLayerIndex >= 0) {
            saveStateForUndo();
            layers.remove(activeLayerIndex);
            activeLayerIndex = Math.max(0, activeLayerIndex - 1);
        }
    }

    public void toggleGlobalVisibility() {
        globalVisibility = !globalVisibility;
        for (Layer l : layers) {
            l.setVisible(globalVisibility);
        }
    }

    public boolean isGlobalVisible() { return globalVisibility; }

    // Control getters/setters
    public DrawnShape.ShapeType getCurrentTool() { return currentTool; }
    public void setCurrentTool(DrawnShape.ShapeType tool) { this.currentTool = tool; }

    public Color getCurrentColor() { return currentColor; }
    public void setCurrentColor(Color color) { this.currentColor = color; }

    public float getPencilSize() { return pencilSize; }
    public void setPencilSize(float size) { this.pencilSize = size; }

    public float getEraserSize() { return eraserSize; }
    public void setEraserSize(float size) { this.eraserSize = size; }

    public double getZoomScale() { return zoomScale; }
    public void setZoomScale(double zoom) { this.zoomScale = zoom; }

    // ==========================================
    // Undo / Redo Mechanism (Deep Copy Snapshot)
    // ==========================================

    /**
     * บันทึกสถานะปัจจุบันลง undoStack
     * ต้องทำ Deep Copy เพื่อไม่ให้ Object อ้างอิงถึงกันเมื่อเกิดการแก้ไข
     */
    public void saveStateForUndo() {
        undoStack.push(cloneLayers(layers));
        redoStack.clear(); // เมื่อมีการวาดหรือทำ Action ใหม่ ให้ล้าง Redo Stack
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            // เก็บสถานะปัจจุบันลง Redo ก่อนเปลี่ยนไปใช้สถานะเก่า
            redoStack.push(cloneLayers(layers));
            
            // ดึงสถานะล่าสุดจาก Undo Stack กลับมาใช้งาน
            layers = undoStack.pop();
            
            // ปรับ activeLayerIndex ไม่ให้เกินขอบเขต
            if (activeLayerIndex >= layers.size()) {
                activeLayerIndex = layers.size() - 1;
            }
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            // เก็บสถานะปัจจุบันลง Undo ก่อนใช้สถานะจาก Redo
            undoStack.push(cloneLayers(layers));
            
            // ดึงสถานะจาก Redo Stack กลับมาใช้งาน
            layers = redoStack.pop();
            
            // ปรับ activeLayerIndex ไม่ให้เกินขอบเขต
            if (activeLayerIndex >= layers.size()) {
                activeLayerIndex = layers.size() - 1;
            }
        }
    }

    /**
     * คัดลอกลิสต์ของ Layer แบบ Deep Copy
     */
    private List<Layer> cloneLayers(List<Layer> original) {
        List<Layer> copy = new ArrayList<>();
        for (Layer l : original) {
            copy.add(l.cloneLayer()); // เรียกใช้ cloneLayer() ของคลาส Layer
        }
        return copy;
    }

    //ความชัดของเส้น
    public float getOpacity() {
    return opacity;
}

    public void setOpacity(float opacity) {
    this.opacity = opacity;
}
}