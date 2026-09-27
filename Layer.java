import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Layer {
    private String name;
    private boolean visible = true; // true = On, false = Off
    private List<DrawnShape> shapes = new ArrayList<>();
    private BufferedImage backgroundImage;

    public Layer(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public List<DrawnShape> getShapes() { return shapes; }
    public void addShape(DrawnShape shape) { shapes.add(shape); }
    public void removeShape(DrawnShape shape) { shapes.remove(shape); }

    public BufferedImage getBackgroundImage() { return backgroundImage; }
    public void setBackgroundImage(BufferedImage img) { this.backgroundImage = img; }

    public Layer cloneLayer() {
        Layer cloned = new Layer(this.name);
        cloned.setVisible(this.visible);
    
        // คัดลอกรายการรูปทรง (DrawnShape) ทั้งหมด
        for (DrawnShape shape : this.shapes) {
            cloned.getShapes().add(shape); 
        }
    
        // คัดลอกภาพ Background Image (ถ้ามี)
        if (this.backgroundImage != null) {
            //มันเขียนยาวเฉยๆ ไม่ต้องตกใจ มันวิธีเขียนโดยไม่ต้อง import
            java.awt.image.BufferedImage imgCopy = new java.awt.image.BufferedImage(
                this.backgroundImage.getWidth(),
                this.backgroundImage.getHeight(),
                this.backgroundImage.getType() != 0 ? this.backgroundImage.getType() : java.awt.image.BufferedImage.TYPE_INT_ARGB
            );
            java.awt.Graphics g = imgCopy.getGraphics();
            g.drawImage(this.backgroundImage, 0, 0, null);
            g.dispose();
            cloned.setBackgroundImage(imgCopy);
        }
    
        return cloned;
    }
}