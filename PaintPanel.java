import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class PaintPanel extends JPanel {
    private DrawingModel model;
    private Point startPoint;
    private Point currentPoint;
    private List<Point> freehandPoints = new ArrayList<>();

    // 1. ขนาดกระดาษตั้งต้น (Base Canvas Size)
    private int BASE_WIDTH = 800;
    private int BASE_HEIGHT = 600;

    public PaintPanel(DrawingModel model) {
        this.model = model;

        // กำหนดสีพื้นหลังภายนอกกระดาษ (เช่น สีเทาอ่อน เพื่อให้เห็นขอบกระดาษชัดเจน)
        setBackground(Color.LIGHT_GRAY); 
        updateCanvasSize(); // อัปเดตขนาดเริ่มต้น

        //setPreferredSize(new Dimension(1000, 780)); // ขนาด Canvas ตั้งต้น
        //setBackground(Color.WHITE);
        //setBorder(new LineBorder(Color.DARK_GRAY, 20)); // เพิ่มกรอบรอบ Canvas

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = scalePoint(e.getPoint());

                //เพิ่มมา
                // ป้องกันไม่ให้จุดเริ่มต้นอยู่นอกกระดาษ
                if (!isInsideCanvas(p)) return;

                p = clampPoint(p);
                startPoint = p;
                currentPoint = p;

                Layer activeLayer = model.getActiveLayer();
                if (activeLayer == null || !activeLayer.isVisible()) return;

                DrawnShape.ShapeType tool = model.getCurrentTool();

                if (tool == DrawnShape.ShapeType.PENCIL || tool == DrawnShape.ShapeType.ERASER) {
                    freehandPoints.clear();
                    freehandPoints.add(p);
                } else if (tool == DrawnShape.ShapeType.TEXT) {
                    // ตรวจสอบว่าคลิกโดนข้อความเดิมเพื่อลบหรือไม่
                    DrawnShape targetText = null;
                    for (DrawnShape s : activeLayer.getShapes()) {
                        if (s.getType() == DrawnShape.ShapeType.TEXT && s.contains(p)) {
                            targetText = s;
                            break;
                        }
                    }

                    if (targetText != null) {
                        int opt = JOptionPane.showConfirmDialog(PaintPanel.this, 
                                "Do you want to delete this message?", "Delete message", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            model.saveStateForUndo();
                            activeLayer.removeShape(targetText);
                            repaint();
                        }
                    } else {
                        // พิมพ์ข้อความใหม่
                        String input = JOptionPane.showInputDialog(PaintPanel.this, "Please enter the text to print:");
                        if (input != null && !input.trim().isEmpty()) {
                            model.saveStateForUndo();
                            //DrawnShape textShape = new DrawnShape(input, p, new Font("SansSerif", Font.PLAIN, 18), model.getCurrentColor());
                            DrawnShape textShape = new DrawnShape(input, p, new Font("SansSerif", Font.PLAIN, (int)model.getPencilSize()+10), model.getCurrentColor());
                            textShape.setOpacity(model.getOpacity());
                            activeLayer.addShape(textShape);
                            repaint();
                        }
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = scalePoint(e.getPoint());
                //เพิ่มมา
                // หนีบพิกัดการลากให้อยู่ในขอบเขตกระดาษเสมอ
                p = clampPoint(p);

                currentPoint = p;

                Layer activeLayer = model.getActiveLayer();
                if (activeLayer == null || !activeLayer.isVisible()) return;

                DrawnShape.ShapeType tool = model.getCurrentTool();
                if (tool == DrawnShape.ShapeType.PENCIL || tool == DrawnShape.ShapeType.ERASER) {
                    freehandPoints.add(p);
                }

            repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = scalePoint(e.getPoint());
                p = clampPoint(p);
                currentPoint = p;

                Layer activeLayer = model.getActiveLayer();
                if (activeLayer == null || !activeLayer.isVisible()) return;

                DrawnShape.ShapeType tool = model.getCurrentTool();
                model.saveStateForUndo();

                if (tool == DrawnShape.ShapeType.PENCIL) {
                    // [แก้ไข] หากเป็นการคลิกจุดเดียว ให้เพิ่มจุดเดิมซ้ำ เพื่อให้วาดเป็นจุดได้
                    if (freehandPoints.size() == 1) {
                        freehandPoints.add(new Point(p.x, p.y));
                    }
                    DrawnShape shape = new DrawnShape(DrawnShape.ShapeType.PENCIL, freehandPoints, model.getCurrentColor(), model.getPencilSize());
                    shape.setOpacity(model.getOpacity());
                    activeLayer.addShape(shape);
                } else if (tool == DrawnShape.ShapeType.ERASER) { 
                    // [แก้ไข] หากเป็นการคลิกจุดเดียว ให้เพิ่มจุดเดิมซ้ำ เพื่อให้วาดเป็นจุดได้
                    if (freehandPoints.size() == 1) {
                        freehandPoints.add(new Point(p.x, p.y));
                    }
                    DrawnShape shape = new DrawnShape(DrawnShape.ShapeType.ERASER, freehandPoints, null, model.getEraserSize());
                    shape.setOpacity(model.getOpacity());
                    activeLayer.addShape(shape);

                } else if (tool == DrawnShape.ShapeType.LINE) {
                    DrawnShape shape = new DrawnShape(DrawnShape.ShapeType.LINE, new Line2D.Float(startPoint, currentPoint), model.getCurrentColor(), model.getPencilSize());
                    shape.setOpacity(model.getOpacity());
                    activeLayer.addShape(shape);

                } else if (tool == DrawnShape.ShapeType.RECTANGLE) {
                    DrawnShape shape = new DrawnShape(DrawnShape.ShapeType.RECTANGLE, makeRectangle(startPoint, currentPoint), model.getCurrentColor(), model.getPencilSize());
                    shape.setOpacity(model.getOpacity());
                    activeLayer.addShape(shape);
                } else if (tool == DrawnShape.ShapeType.CIRCLE) {
                    DrawnShape shape = new DrawnShape(DrawnShape.ShapeType.CIRCLE,makeEllipse(startPoint, currentPoint), model.getCurrentColor(), model.getPencilSize());
                    shape.setOpacity(model.getOpacity());
                    activeLayer.addShape(shape);
                }

                freehandPoints.clear();
                startPoint = null;
                currentPoint = null;
                repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public int getBaseWidth() {
        return BASE_WIDTH;
    }

    

    public int getBaseHeight(){
        return BASE_HEIGHT;
    }


    private Point scalePoint(Point p) {
        double zoom = model.getZoomScale();
        return new Point((int) (p.x / zoom), (int) (p.y / zoom));
    }

    private Rectangle2D.Float makeRectangle(Point p1, Point p2) {
        return new Rectangle2D.Float(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
    }

    private Ellipse2D.Float makeEllipse(Point p1, Point p2) {
        return new Ellipse2D.Float(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
    }

    //เพิ่มมา
    private boolean isInsideCanvas(Point p) {
        return p.x >= 0 && p.x <= BASE_WIDTH && p.y >= 0 && p.y <= BASE_HEIGHT;
    }

    //เพิ่มมา
    private Point clampPoint(Point p) {
        int x = Math.max(0, Math.min(p.x, BASE_WIDTH));
        int y = Math.max(0, Math.min(p.y, BASE_HEIGHT));
        return new Point(x, y);
    }

    //เพิ่มมา
    // 3. เมธอดสำหรับคำนวณขนาด PreferredSize ของ Panel ตามอัตราซูม
    public void updateCanvasSize() {
        double zoom = model.getZoomScale();
        int newWidth = (int) (BASE_WIDTH * zoom);
        int newHeight = (int) (BASE_HEIGHT * zoom);
        
        setPreferredSize(new Dimension(newWidth, newHeight));
        revalidate(); // แจ้ง JScrollPane ให้ปรับ Scrollbar ใหม่
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        model.setIsModified(true);//มีการแก้ไขอย่าลืม save

        Graphics2D g2d = (Graphics2D) g.create();

        //เพิ่มมา
        // 1. เคลียร์พื้นที่ทั้งหมดของ Panel ด้วยสีเทาอ่อนก่อน (แก้ปัญหาทิ้งคราบ/เส้นขาว)
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // นำค่า Zoom Scale มาคำนวณการย่อ-ขยาย
        double zoom = model.getZoomScale();
        g2d.scale(zoom, zoom);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        //เพิ่มมา
        // ตัดขอบไม่ให้การวาดล้นออกจากกระดาษ
        // จำกัดพื้นที่วาดให้อยู่ในกระดาษ
        g2d.clipRect(0, 0, BASE_WIDTH, BASE_HEIGHT);

        //เพิ่มมา
        // 4. วาดแผ่นกระดาษสีขาว (Canvas Paper) ตามขนาดตั้งต้น
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, BASE_WIDTH, BASE_HEIGHT);

        drawLayersAndPreview(g2d);
        
        g2d.dispose();
    }

    public void drawLayersAndPreview(Graphics2D g2d) {
        // 1. วาดแต่ละ Layer ลงบน Buffer แยกต่างหาก
        for (Layer layer : model.getLayers()) {
            if (layer.isVisible()) {
                BufferedImage layerBuffer = new BufferedImage(
                    BASE_WIDTH, BASE_HEIGHT, BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D gLayer = layerBuffer.createGraphics();
            
                // ตั้งค่าเรนเดอร์คุณภาพสูง
                gLayer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gLayer.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                gLayer.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // หากมีรูปภาพพื้นหลังของ Layer
                if (layer.getBackgroundImage() != null) {
                    // ปิดการเกลี่ยสีซ้ำสำหรับภาพที่โหลดเข้ามา (ป้องกันภาพฟุ้งซ้ำซ้อน)
                    gLayer.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION, 
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
                    );
                
                    gLayer.drawImage(layer.getBackgroundImage(), 0, 0, null);
            
                    // คืนค่า Interpolation กลับเป็น Bicubic หรือ Bilinear สำหรับการวาด Shape ต่อไป
                    gLayer.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION, 
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC
                    );
                }

                // วาด Shapes ทั้งหมดของ Layer นี้
                for (DrawnShape shape : layer.getShapes()) {
                    shape.draw(gLayer);
                }

                // [แก้ไขจุดที่ 1] เปลี่ยน Preview ยางลบขณะลากเมาส์ให้เป็นเส้นโค้ง smooth
                if (layer == model.getActiveLayer() && startPoint != null && currentPoint != null) {
                    if (model.getCurrentTool() == DrawnShape.ShapeType.ERASER && freehandPoints.size() > 1) {
                        gLayer.setComposite(AlphaComposite.Clear);
                        gLayer.setStroke(new BasicStroke(model.getEraserSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    
                        Path2D path = createSmoothPath(freehandPoints);
                        gLayer.draw(path);
                    }
                }
                gLayer.dispose();

                // 2. นำ Layer ที่วาดเสร็จแล้วมาแปะลงบนผืนกระดาษหลัก
                g2d.drawImage(layerBuffer, 0, 0, null);
            }
        }

        // 3. วาด Preview สำหรับเครื่องมืออื่นๆ (ดินสอ, สี่เหลี่ยม, วงกลม)
        if (startPoint != null && currentPoint != null && model.getCurrentTool() != DrawnShape.ShapeType.ERASER) {
            // [แก้ไขจุดที่ 2] เปิด Anti-Aliasing ให้ g2d หลัก เพื่อให้ Preview ลื่นเนียน
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, model.getOpacity()));
            g2d.setColor(model.getCurrentColor());
            g2d.setStroke(new BasicStroke(model.getPencilSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            DrawnShape.ShapeType tool = model.getCurrentTool();
            if (tool == DrawnShape.ShapeType.LINE) {
                g2d.draw(new Line2D.Float(startPoint, currentPoint));
            } else if (tool == DrawnShape.ShapeType.RECTANGLE) {
                g2d.draw(makeRectangle(startPoint, currentPoint));
            } else if (tool == DrawnShape.ShapeType.CIRCLE) {
                g2d.draw(makeEllipse(startPoint, currentPoint));
            } else if (tool == DrawnShape.ShapeType.PENCIL && freehandPoints.size() > 1) {
                // [แก้ไขจุดที่ 2] เปลี่ยน Preview ดินสอให้เป็นเส้นโค้ง smooth เช่นกัน
                Path2D path = createSmoothPath(freehandPoints);
                g2d.draw(path);
            }
        }
    
        // คืนค่า Composite กลับเสมอ
        g2d.setComposite(AlphaComposite.SrcOver);
    }

    // [เพิ่มเมธอดนี้ใน PaintPanel.java หากยังไม่มี]
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

                double midX = (p1.x + p2.x) / 2.0;
                double midY = (p1.y + p2.y) / 2.0;

                path.quadTo(p1.x, p1.y, midX, midY);
            }
        Point last = pts.get(pts.size() - 1);
        path.lineTo(last.x, last.y);
        }
        return path;
    }
}
