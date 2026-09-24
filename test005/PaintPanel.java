import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class PaintPanel extends JPanel {
    private DrawingModel model;
    private Point startPoint;
    private Point currentPoint;
    private List<Point> freehandPoints = new ArrayList<>();

    public PaintPanel(DrawingModel model) {
        this.model = model;
        setPreferredSize(new Dimension(1000, 800)); // ขนาด Canvas ตั้งต้น
        setBackground(Color.WHITE);
        setBorder(new LineBorder(Color.DARK_GRAY, 2)); // เพิ่มกรอบรอบ Canvas

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = scalePoint(e.getPoint());
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
                                "ต้องการลบข้อความนี้ใช่หรือไม่?", "ลบข้อความ", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            model.saveStateForUndo();
                            activeLayer.removeShape(targetText);
                            repaint();
                        }
                    } else {
                        // พิมพ์ข้อความใหม่
                        String input = JOptionPane.showInputDialog(PaintPanel.this, "กรอกข้อความที่ต้องการพิมพ์:");
                        if (input != null && !input.trim().isEmpty()) {
                            model.saveStateForUndo();
                            DrawnShape textShape = new DrawnShape(input, p, new Font("SansSerif", Font.PLAIN, 18), model.getCurrentColor());
                            activeLayer.addShape(textShape);
                            repaint();
                        }
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = scalePoint(e.getPoint());
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
                currentPoint = p;

                Layer activeLayer = model.getActiveLayer();
                if (activeLayer == null || !activeLayer.isVisible()) return;

                DrawnShape.ShapeType tool = model.getCurrentTool();
                model.saveStateForUndo();

                if (tool == DrawnShape.ShapeType.PENCIL) {
                    activeLayer.addShape(new DrawnShape(DrawnShape.ShapeType.PENCIL, freehandPoints, model.getCurrentColor(), model.getPencilSize()));
                } else if (tool == DrawnShape.ShapeType.ERASER) {
                    activeLayer.addShape(new DrawnShape(DrawnShape.ShapeType.ERASER, freehandPoints, getBackground(), model.getEraserSize()));
                } else if (tool == DrawnShape.ShapeType.LINE) {
                    activeLayer.addShape(new DrawnShape(DrawnShape.ShapeType.LINE, new Line2D.Float(startPoint, currentPoint), model.getCurrentColor(), model.getPencilSize()));
                } else if (tool == DrawnShape.ShapeType.RECTANGLE) {
                    activeLayer.addShape(new DrawnShape(DrawnShape.ShapeType.RECTANGLE, makeRectangle(startPoint, currentPoint), model.getCurrentColor(), model.getPencilSize()));
                } else if (tool == DrawnShape.ShapeType.CIRCLE) {
                    activeLayer.addShape(new DrawnShape(DrawnShape.ShapeType.CIRCLE, makeEllipse(startPoint, currentPoint), model.getCurrentColor(), model.getPencilSize()));
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // นำค่า Zoom Scale มาคำนวณการย่อ-ขยาย
        double zoom = model.getZoomScale();
        g2d.scale(zoom, zoom);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // วาด Layer ทุกตัวตามลำดับถ้าสถานะเป็น visible (On)
        for (Layer layer : model.getLayers()) {
            if (layer.isVisible()) {
                if (layer.getBackgroundImage() != null) {
                    g2d.drawImage(layer.getBackgroundImage(), 0, 0, null);
                }
                for (DrawnShape shape : layer.getShapes()) {
                    shape.draw(g2d);
                }
            }
        }

        // วาด Preview ขณะผู้ใช้ลากเม้าส์
        if (startPoint != null && currentPoint != null) {
            g2d.setColor(model.getCurrentTool() == DrawnShape.ShapeType.ERASER ? getBackground() : model.getCurrentColor());
            g2d.setStroke(new BasicStroke(
                model.getCurrentTool() == DrawnShape.ShapeType.ERASER ? model.getEraserSize() : model.getPencilSize(),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
            ));

            DrawnShape.ShapeType tool = model.getCurrentTool();
            if (tool == DrawnShape.ShapeType.LINE) {
                g2d.draw(new Line2D.Float(startPoint, currentPoint));
            } else if (tool == DrawnShape.ShapeType.RECTANGLE) {
                g2d.draw(makeRectangle(startPoint, currentPoint));
            } else if (tool == DrawnShape.ShapeType.CIRCLE) {
                g2d.draw(makeEllipse(startPoint, currentPoint));
            } else if ((tool == DrawnShape.ShapeType.PENCIL || tool == DrawnShape.ShapeType.ERASER) && freehandPoints.size() > 1) {
                for (int i = 0; i < freehandPoints.size() - 1; i++) {
                    Point p1 = freehandPoints.get(i);
                    Point p2 = freehandPoints.get(i + 1);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        g2d.dispose();
    }
}
