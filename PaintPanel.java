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
    private List<Point> freehandPoints = new ArrayList<>(); //เก็บจุดหลายๆจุด

    // 1. ขนาดกระดาษตั้งต้น (Base Canvas Size)
    private int BASE_WIDTH = 800;
    private int BASE_HEIGHT = 600;

    public PaintPanel(DrawingModel model) {
        this.model = model;

        // กำหนดสีพื้นหลังภายนอกกระดาษ
        setBackground(Color.LIGHT_GRAY);
        
        // อัปเดตขนาดเริ่มต้น
        updateCanvasSize();

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                //ดึงเลเยอร์ที่เลือกอยู่มาใช้
                Layer activeLayer = model.getActiveLayer();
                //เช็คว่ามีหรือมองเห็นหรือเปล่า เพราะ ถ้ามองไม่เห็นไม่ควรวาดได้
                if (activeLayer == null || !activeLayer.isVisible()) return;


                //แปลงพิกัดตำแหน่งเมาส์ (Mouse Event) ให้สอดคล้องกับสเกล (Scale/Zoom) ของหน้าจอ
                Point p = scalePoint(e.getPoint());

                // ป้องกันไม่ให้จุดเริ่มต้นอยู่นอกกระดาษ
                if (!isInsideCanvas(p)) return;

                //เช็ตค่าไว้ก่อน ใช้ทีหลัง
                startPoint = p;
                currentPoint = p;
                
                
                //ดูว่าตอนนี้ใช้เครื่องมืออะไรอยู่
                DrawnShape.ShapeType tool = model.getCurrentTool();

                if (tool == DrawnShape.ShapeType.PENCIL || tool == DrawnShape.ShapeType.ERASER) {
                    freehandPoints.clear(); //ลบจุดเก่าทั้งหมด
                    freehandPoints.add(p);
                } else if (tool == DrawnShape.ShapeType.TEXT) {
                    // ตรวจสอบว่าคลิกโดนข้อความเดิมเพื่อลบหรือไม่

                    DrawnShape targetText = null; //ใช้เก็บรูปทรงที่เป็นข้อความ

                    //ตรวจทุก shape ใน layer นั้น
                    for (DrawnShape s : activeLayer.getShapes()) {
                        if (s.getType() == DrawnShape.ShapeType.TEXT && s.containsText(p)) {
                            targetText = s;
                            break; //เจอข้อความแล้วออกจากลูป
                        }
                    }

                    //มีข้อความเดิมอยู่ขึ้นหน้าต่างว่าจะลบไหม
                    if (targetText != null) {
                        int opt = JOptionPane.showConfirmDialog(PaintPanel.this, "Do you want to delete this message?", "Delete message", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            model.saveStateForUndo(); //บันทึกสำหรับทำ undo
                            activeLayer.removeShape(targetText); //ลบข้อความนั้นจากเลเยอร์ที่เลือกอยู่
                            repaint();
                        }
                    } else {
                        // พิมพ์ข้อความใหม่
                        String input = JOptionPane.showInputDialog(PaintPanel.this, "Please enter the text to print:");
                        if (input != null && !input.trim().isEmpty()) {
                            model.saveStateForUndo(); //บันทึกสำหรับทำ undo

                            //สร้างข้อความใหม่
                            DrawnShape textShape = new DrawnShape(input, p, new Font("SansSerif", Font.PLAIN, (int)model.getPencilSize()+10), model.getCurrentColor());
                            textShape.setOpacity(model.getOpacity());

                            activeLayer.addShape(textShape); //เพิ่มข้อความไปเก็บที่เลเยอร์ที่เลือกอยู่
                            repaint();
                        }
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                
                //ดึงเลเยอร์ที่เลือกอยู่มาใช้
                Layer activeLayer = model.getActiveLayer();
                //เช็คว่ามีหรือมองเห็นหรือเปล่า เพราะ ถ้ามองไม่เห็นไม่ควรวาดได้
                if (activeLayer == null || !activeLayer.isVisible()) return;

                //แปลงพิกัดตำแหน่งเมาส์ (Mouse Event) ให้สอดคล้องกับสเกล (Scale/Zoom) ของหน้าจอ
                Point p = scalePoint(e.getPoint());

                currentPoint = p;

                DrawnShape.ShapeType tool = model.getCurrentTool();
                if (tool == DrawnShape.ShapeType.PENCIL || tool == DrawnShape.ShapeType.ERASER) {
                    freehandPoints.add(p); //เก็บจุดไว้รอวาดต่อ
                }

            repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                //ดึงเลเยอร์ที่เลือกอยู่มาใช้
                Layer activeLayer = model.getActiveLayer();
                //เช็คว่ามีหรือมองเห็นหรือเปล่า เพราะ ถ้ามองไม่เห็นไม่ควรวาดได้
                if (activeLayer == null || !activeLayer.isVisible()) return;

                //แปลงพิกัดตำแหน่งเมาส์ (Mouse Event) ให้สอดคล้องกับสเกล (Scale/Zoom) ของหน้าจอ
                Point p = scalePoint(e.getPoint());

                currentPoint = p;

                DrawnShape.ShapeType tool = model.getCurrentTool();
                model.saveStateForUndo();

                //เพิ่ม Shape ใหม่ลงในเลเยอร์ที่เลือกอยู่
                if (tool == DrawnShape.ShapeType.PENCIL) {
                    //หากเป็นการคลิกจุดเดียว ให้เพิ่มจุดเดิมซ้ำ เพื่อให้วาดเป็นจุดได้
                    if (freehandPoints.size() == 1) {
                        freehandPoints.add(new Point(p.x, p.y));
                    }
                    DrawnShape shape = new DrawnShape(DrawnShape.ShapeType.PENCIL, freehandPoints, model.getCurrentColor(), model.getPencilSize());
                    shape.setOpacity(model.getOpacity());
                    activeLayer.addShape(shape);
                } else if (tool == DrawnShape.ShapeType.ERASER) { 
                    // หากเป็นการคลิกจุดเดียว ให้เพิ่มจุดเดิมซ้ำ เพื่อให้วาดเป็นจุดได้
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

                freehandPoints.clear(); //ปล่อยปุ่มแปลว่าวาดเสร็จแล้วลบจุดเก่าทิ้ง
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

    /**
     * แปลงพิกัดตำแหน่งเมาส์ (Mouse Event) ให้สอดคล้องกับสเกล (Scale/Zoom) ของหน้าจอ
     */
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

    /**
     * ป้องกันไม่ให้จุดเริ่มต้นอยู่นอกกระดาษ
     */
    private boolean isInsideCanvas(Point p) {
        return p.x >= 0 && p.x <= BASE_WIDTH && p.y >= 0 && p.y <= BASE_HEIGHT;
    }
    
    /**
     *คำนวณขนาด PreferredSize ของ Panel ตามอัตราซูม
     */
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

        //เคลียร์พื้นที่ทั้งหมดของ Panel ด้วยสีเทาอ่อนก่อน (แก้ปัญหาทิ้งคราบ/เส้นขาว)
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // นำค่า Zoom Scale มาคำนวณการย่อ-ขยาย
        double zoom = model.getZoomScale();
        g2d.scale(zoom, zoom);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // ตัดขอบไม่ให้การวาดล้นออกจากกระดาษ
        // จำกัดพื้นที่วาดให้อยู่ในกระดาษ
        g2d.clipRect(0, 0, BASE_WIDTH, BASE_HEIGHT);
;
        
        // วาดกระดาษแบบตารางโปร่งใส (Checkerboard)
        int tileSize = 50; // ขนาดของช่องตาราง
        Color color1 = Color.WHITE; // สีขาว
        Color color2 = new Color(220, 220, 220); // สีเทาอ่อน (ให้ตัดกับขาวและเด่นกว่าพื้นหลัง)

        //วาดตาราง
        for (int y = 0; y < BASE_HEIGHT; y += tileSize) {
            for (int x = 0; x < BASE_WIDTH; x += tileSize) {
                // คำนวณช่วงกว้าง/ยาว ของช่องสุดท้ายไม่ให้เกินขอบ BASE_WIDTH/BASE_HEIGHT
                int w = Math.min(tileSize, BASE_WIDTH - x);
                int h = Math.min(tileSize, BASE_HEIGHT - y);

                // สลับสีตารางแบบหมากรุก
                if ((x / tileSize + y / tileSize) % 2 == 0) {
                    g2d.setColor(color1);
                } else {
                    g2d.setColor(color2);
                }
                g2d.fillRect(x, y, w, h);
            }
        }

        //วาดทุกเลเยอร์
        drawLayersAndPreview(g2d);
        
        //ปิดใช้ g2d เพื่อคืนทรัพยากรในระบบ
        g2d.dispose();
    }

    public void drawLayersAndPreview(Graphics2D g2d) {
        // 1. วาดแต่ละ Layer ลงบน Buffer แยกต่างหาก
        for (Layer layer : model.getLayers()) {

            //วาดเฉพาะเลเยอร์ที่เห็น
            if (layer.isVisible()) {
                BufferedImage layerBuffer = new BufferedImage(BASE_WIDTH, BASE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics2D gLayer = layerBuffer.createGraphics();
            
                //1.เปิดใช้งานระบบ Anti-Aliasing (ลบรอบหยัก)
                //2.ควบคุม ตำแหน่งและพิกัดความหนาของเส้นวาดให้แม่นยำแบบย่อยจุด (Sub-pixel Precision)
                //3.การเรนเดอร์ภาพโดยเน้นคุณภาพสูงสุด (Quality Over Speed)
                gLayer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gLayer.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                gLayer.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // หากมีรูปภาพพื้นหลังของ Layer
                if (layer.getBackgroundImage() != null) {
                    // ปิดการเกลี่ยสีซ้ำสำหรับภาพที่โหลดเข้ามา (ป้องกันภาพฟุ้งซ้ำซ้อน)
                    gLayer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                
                    gLayer.drawImage(layer.getBackgroundImage(), 0, 0, null);
            
                    // คืนค่า Interpolation กลับเป็น Bicubic หรือ Bilinear สำหรับการวาด Shape ต่อไป
                    gLayer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                }

                // วาด Shapes ทั้งหมดของ Layer นี้
                for (DrawnShape shape : layer.getShapes()) {
                    shape.drawShape(gLayer);
                }

                //เปลี่ยน Preview ยางลบขณะลากเมาส์ให้เป็นเส้นโค้ง smooth
                if (layer == model.getActiveLayer() && startPoint != null && currentPoint != null) {
                    if (model.getCurrentTool() == DrawnShape.ShapeType.ERASER && freehandPoints.size() > 1) {
                        gLayer.setComposite(AlphaComposite.Clear);
                        gLayer.setStroke(new BasicStroke(model.getEraserSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    
                        Path2D path = createSmoothPath(freehandPoints);
                        gLayer.draw(path);
                    }
                }
                gLayer.dispose();

                //นำ Layer ที่วาดเสร็จแล้วมาแปะลงบนผืนกระดาษหลัก
                g2d.drawImage(layerBuffer, 0, 0, null);
            }
        }

        //วาด Preview สำหรับเครื่องมืออื่นๆ (ดินสอ, สี่เหลี่ยม, วงกลม)
        if (startPoint != null && currentPoint != null && model.getCurrentTool() != DrawnShape.ShapeType.ERASER) {

            //1.เปิดใช้งานระบบ Anti-Aliasing (ลบรอบหยัก)
            //2.ควบคุม ตำแหน่งและพิกัดความหนาของเส้นวาดให้แม่นยำแบบย่อยจุด (Sub-pixel Precision)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            //ดึงค่า Opacity
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, model.getOpacity()));
            //ดึงสี
            g2d.setColor(model.getCurrentColor());
            //กำหนดลักษณะเส้น โดย CAP_ROUND ทำให้ สุดปลายโค้งมน และ JOIN_ROUND จุดเชื่อมต่อโค้งมน
            g2d.setStroke(new BasicStroke(model.getPencilSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            DrawnShape.ShapeType tool = model.getCurrentTool();
            if (tool == DrawnShape.ShapeType.LINE) {
                g2d.draw(new Line2D.Float(startPoint, currentPoint));

            } else if (tool == DrawnShape.ShapeType.RECTANGLE) {
                g2d.draw(makeRectangle(startPoint, currentPoint));

            } else if (tool == DrawnShape.ShapeType.CIRCLE) {
                g2d.draw(makeEllipse(startPoint, currentPoint));

            } else if (tool == DrawnShape.ShapeType.PENCIL && freehandPoints.size() > 1) {
                //เปลี่ยน Preview ดินสอให้เป็นเส้นโค้ง smooth เช่นกัน
                Path2D path = createSmoothPath(freehandPoints);
                g2d.draw(path);
            }
            
        }
    
        // คืนค่า Composite กลับเสมอ
        g2d.setComposite(AlphaComposite.SrcOver);
    }

    
    //ช่วยสร้าง Path2D แบบโค้งมนด้วย Quad Curve (Bézier)
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
