import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Path2D;

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
        if(type == ShapeType.ERASER){
            // [แก้จุดที่ 1] สั่งให้ลบ Pixel ใน Buffer ให้โปร่งใส
            g2d.setComposite(AlphaComposite.Clear);
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
            if (points != null && points.size() > 1) {
                for (int i = 0; i < points.size() - 1; i++) {
                    Point p1 = points.get(i);
                    Point p2 = points.get(i + 1);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
            // คืนค่าโหมดการวาดกลับเป็นปกติ
            g2d.setComposite(AlphaComposite.SrcOver);
        }else{
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));

            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if (type == ShapeType.TEXT && text != null) {
                g2d.setFont(font);
                g2d.drawString(text, textPosition.x, textPosition.y);
            } else if (type == ShapeType.PENCIL && points != null && points.size() > 1) {
                
            //ทำให้วาดเป็นเส้น
            Path2D path = new Path2D.Float();

            path.moveTo(points.get(0).x, points.get(0).y);

            for (int i = 1; i < points.size(); i++) {
                path.lineTo(points.get(i).x, points.get(i).y);
    }

    g2d.draw(path);
            } else if (shape != null) {
                g2d.draw(shape);
            }
            g2d.setComposite(AlphaComposite.SrcOver);
        }
       g2d.setComposite(AlphaComposite.SrcOver); 
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
}