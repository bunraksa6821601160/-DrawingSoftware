import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * ทำหน้าที่เป็นรูปทรงหนึ่งชิ้น
 * DrawnShape
 */
public class DrawnShape{
    public enum ShapeType { PENCIL, ERASER, LINE, RECTANGLE, CIRCLE, TEXT } // ประเภทของเครื่องมือ

    private ShapeType type; // บอกว่าวัตถุชิ้นนี้สร้างจากอะไร เพื่อจะเรียกใช้ได้ถูก
    private Shape shape; //เก็บรูปร่าง
    private List<Point> points; // เก็บจุดหลายจุด ใช้ทำเส้น
    private String text; // เก็ยข้อความ
    private Point textPosition; // ตาแหน่งข้อความ
    private Color color; // เก็บสี
    private float strokeWidth; //เก็บขนาด เช่น ขนาดดินสอ
    private Font font; // เก็บ ฟอนต์ตัวอักษร
    private float opacity = 1.0f; // เก็บความโปร่งใส

    // Constructor สำหรับ Shape ทั่วไป (Line, Rect, Circle)
    public DrawnShape(ShapeType type, Shape shape, Color color, float strokeWidth) {
        this.type = type;
        this.shape = shape;
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    // Constructor สำหรับ Pencil และ Eraser Freehand
    public DrawnShape(ShapeType type, List<Point> points, Color color, float strokeWidth) {
        this.type = type;
        this.points = new ArrayList<>(points);
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    // Constructor สำหรับ Text
    public DrawnShape(String text, Point pos, Font font, Color color) {
        this.type = ShapeType.TEXT;
        this.text = text;
        this.textPosition = pos;
        this.font = font;
        this.color = color;
    }

    public ShapeType getType() { return type; }

    /**
     * ใช้วาดรูปทรงของวัตถุนี้
     * @param g2d
     */
    public void drawShape(Graphics2D g2d) {

        //1.เปิดใช้งานระบบ Anti-Aliasing (ลบรอบหยัก)
        //2.ควบคุม ตำแหน่งและพิกัดความหนาของเส้นวาดให้แม่นยำแบบย่อยจุด (Sub-pixel Precision)
        //3.การเรนเดอร์ภาพโดยเน้นคุณภาพสูงสุด (Quality Over Speed)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (type == ShapeType.ERASER) {

            // สั่งให้ลบ Pixel ใน Buffer ให้โปร่งใส
            g2d.setComposite(AlphaComposite.Clear);

            //กำหนดลักษณะเส้น โดย CAP_ROUND ทำให้ สุดปลายโค้งมน และ JOIN_ROUND จุดเชื่อมต่อโค้งมน
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    
            //วาดเส้นที่เกิดจากยางลบโดยใช้ Path2D 
            if (points != null && points.size() > 1) {
                Path2D path = createSmoothPath(points);
                g2d.draw(path);
            }

            // คืนค่าโหมดการวาดกลับเป็นปกติ
            g2d.setComposite(AlphaComposite.SrcOver);

        } else {
            //กำหนดค่า Opacity
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

            //กำหนดสี
            g2d.setColor(color);
            
            //กำหนดลักษณะเส้น โดย CAP_ROUND ทำให้ สุดปลายโค้งมน และ JOIN_ROUND จุดเชื่อมต่อโค้งมน
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if (type == ShapeType.TEXT && text != null) {
                g2d.setFont(font);
                g2d.drawString(text, textPosition.x, textPosition.y);
            } else if (type == ShapeType.PENCIL && points != null && points.size() > 1) {
                Path2D path = createSmoothPath(points);
                g2d.draw(path);
            } else if (shape != null) {
                g2d.draw(shape);
            }

        //คืนค่าโหมดการวาดกลับเป็นปกติทันทีหลังวาดรูปทรงนั้นเสร็จ
        g2d.setComposite(AlphaComposite.SrcOver);
        }
    }

    /**
     * เช็คว่ากดโดนตัวอักษรไหม
     * @param p จุดที่ต้องการตรวจ หรือ ที่เมาส์กด
     * @return กดโดนส่ง true ถ้าไม่โดนส่ง false
     */
    public boolean containsText(Point p) {
        if (type == ShapeType.TEXT && textPosition != null && text != null) {
            int fontSize = font.getSize();

            int ascent = (int) (fontSize * 0.85);        // ระยะดันขอบบนขึ้นไปจากจุด Baseline
            int width  = (int) (text.length() * fontSize * 0.6); // ความกว้างโดยประมาณต่อตัวอักษร
            int height = (int) (fontSize * 1.15);        // ความสูงรวม (รวมระยะสระ/วรรณยุกต์)

            Rectangle bounds = new Rectangle(textPosition.x, textPosition.y - ascent, width, height);

            // เรียกใช้ contains ของ java.awt.Rectangle (คนละตัวกัน)
            return bounds.contains(p);
        }
        if (shape != null) {
            // เรียกใช้ contains ของ java.awt.Shape (คนละตัวกัน)
            return shape.contains(p);
        }
        return false;
    }

    // กำนดค่า Opacity
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    
    /**
     * ช่วยสร้าง Path2D แบบโค้งมนด้วย Quad Curve (Bézier)
     * @param pts จุดทั้งหมด
     * @return path 
     */
    private Path2D createSmoothPath(List<Point> pts) {
        Path2D path = new Path2D.Float();
        if (pts == null || pts.isEmpty()) return path;

        //กำหนดจุดเริ่มต้น
        path.moveTo(pts.get(0).x, pts.get(0).y);

        if (pts.size() == 2) {
            //วาด
            path.lineTo(pts.get(1).x, pts.get(1).y);
        } else {
            for (int i = 1; i < pts.size() - 1; i++) {
                Point p1 = pts.get(i);
                Point p2 = pts.get(i + 1);

                // หาจุดกึ่งกลางระหว่างจุดปัจจุบันกับจุดถัดไป
                double midX = (p1.x + p2.x) / 2.0;
                double midY = (p1.y + p2.y) / 2.0;

                // วาดเส้นโค้งกำลังสอง (Quadratic Bézier Curve)โดย ดัดเส้นโค้งผ่านจุด p1 ไปหยุดที่จุดกึ่งกลาง
                path.quadTo(p1.x, p1.y, midX, midY);
            }
            // เชื่อมจุดสุดท้าย ต้องมีเพราะ quadTo เชื่อมถึงแค่จุดกึ่งกลางก่อนจุดสุดท้าย
            Point last = pts.get(pts.size() - 1);
            path.lineTo(last.x, last.y);
        }
        return path;
    }
}