import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

//public class DrawnShape implements Serializable {
public class DrawnShape{
    public enum ShapeType { PENCIL, ERASER, LINE, RECTANGLE, CIRCLE, TEXT }

    private ShapeType type;
    private Shape shape;
    private List<Point> points;
    private String text;
    private Point textPosition;
    private Color color;
    private float strokeWidth;
    private Font font;
    private float opacity = 1.0f;

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

    public void draw(Graphics2D g2d) {

        // 1. เปิด Anti-Aliasing และ Stroke Pure ทุกครั้งที่เริ่มวาด Shape
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        if (type == ShapeType.ERASER) {
            // [แก้จุดที่ 1] สั่งให้ลบ Pixel ใน Buffer ให้โปร่งใส
            g2d.setComposite(AlphaComposite.Clear);
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    
            if (points != null && points.size() > 1) {
                Path2D path = createSmoothPath(points);
                g2d.draw(path);
            }
            // คืนค่าโหมดการวาดกลับเป็นปกติ
            g2d.setComposite(AlphaComposite.SrcOver);

        } else {
            // [แก้จุดที่ 2] กำหนดค่า Opacity
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

            g2d.setColor(color);
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

        // [แก้ไข] คืนค่าโหมดการวาดกลับเป็นปกติทันทีหลังวาดรูปทรงนั้นเสร็จ
        g2d.setComposite(AlphaComposite.SrcOver);
        }
    }

    public boolean contains(Point p) {
        if (type == ShapeType.TEXT && textPosition != null && text != null) {
            Rectangle bounds = new Rectangle(textPosition.x, textPosition.y - 15, text.length() * 10, 20);

            // เรียกใช้ contains ของ java.awt.Rectangle (คนละตัวกัน)
            return bounds.contains(p);
        }
        if (shape != null) {
            // เรียกใช้ contains ของ java.awt.Shape (คนละตัวกัน)
            return shape.contains(p);
        }
        return false;
    }

    //Opacity
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    // [เพิ่มเมธอดนี้] ช่วยสร้าง Path2D แบบโค้งมนด้วย Quad Curve (Bézier)
    private Path2D createSmoothPath(List<Point> pts) {
        Path2D path = new Path2D.Float();
        if (pts == null || pts.isEmpty()) return path;

        path.moveTo(pts.get(0).x, pts.get(0).y);

        if (pts.size() == 2) {
            path.lineTo(pts.get(1).x, pts.get(1).y);
        } else {
            for (int i = 1; i < pts.size() - 1; i++) {
                Point p1 = pts.get(i);
                Point p2 = pts.get(i + 1);

                // หาจุดกึ่งกลางระหว่างจุดปัจจุบันกับจุดถัดไป
                double midX = (p1.x + p2.x) / 2.0;
                double midY = (p1.y + p2.y) / 2.0;

                // ดัดเส้นโค้งผ่านจุด p1 ไปหยุดที่จุดกึ่งกลาง
                path.quadTo(p1.x, p1.y, midX, midY);
            }
        // เชื่อมจุดสุดท้าย
        Point last = pts.get(pts.size() - 1);
        path.lineTo(last.x, last.y);
        }
        return path;
    }
}