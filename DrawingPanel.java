import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class DrawingPanel extends JPanel{
    private ArrayList<Layer> layers = new ArrayList<>();

    private int currentLayer = 0;
    private double zoom = 1.0; 
    
    private final int paperWidth = 700;
    private final int paperHeight = 550;

    public DrawingPanel(){
        setBackground(Color.LIGHT_GRAY);
        //สร้างLayerแรก
        layers.add(new Layer("Layer 1"));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e){
                // ตรวจว่าคลิกอยู่บนกระดาษหรือไม่
                if (isInsidePaper(e.getPoint())) {

                    ArrayList<Point> line = new ArrayList<>();
                    line.add(screenToPaper(e.getPoint()));
                    layers.get(currentLayer).addLine(line);

                    repaint();
                }
    
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e){

                  // วาดเฉพาะตอนเมาส์อยู่บนกระดาษ
                if (isInsidePaper(e.getPoint())) {

                    Layer layer = layers.get(currentLayer);
                    ArrayList<ArrayList<Point>> lines = layer.getLines();
                    lines.get(lines.size() - 1).add(screenToPaper(e.getPoint()));

                    repaint();
                }

            }
        });
    }

    //คำนวนขนาดกระดาษ
    private Rectangle getPaperBounds() {

    int width = (int)(paperWidth * zoom);
    int height = (int)(paperHeight * zoom);

    int paperX = (getWidth() - width) / 2;
    int paperY = (getHeight() - height) / 2;

    return new Rectangle(paperX, paperY, width, height);
}

      // ตรวจสอบว่าจุดอยู่ในกระดาษหรือไม่
    private boolean isInsidePaper(Point p) {
    return getPaperBounds().contains(p);
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Rectangle paper = getPaperBounds();

          // กระดาษสีขาว
        g.setColor(Color.WHITE);
        g.fillRect(paper.x,paper.y,paper.width,paper.height);

        // ขอบกระดาษ
        g.setColor(Color.GRAY);
        g.drawRect(paper.x,paper.y,paper.width,paper.height);

        g.setColor(Color.black);

        for (Layer layer : layers) {
            if(!layer.isVisible()){
                continue ;
            }
        for (ArrayList<Point> line : layer.getLines()) {
            for(int i=1 ;i<line.size() ; i++ ){
            Point p1 = line.get(i-1);
            Point p2 = line.get(i);

            int x1 = paper.x + (int)(p1.x * zoom);
                int y1 = paper.y + (int)(p1.y * zoom);

                int x2 = paper.x + (int)(p2.x * zoom);
                int y2 = paper.y + (int)(p2.y * zoom);

            g.drawLine(x1, y1, x2, y2);
            }
        }
}
    }
    public void addLayer() {
     String name = "Layer " + (layers.size() + 1);

    layers.add(new Layer(name));

    currentLayer = layers.size() - 1;

    repaint();
}

    public void removeLayer(int index) {
    if (layers.size() <= 1) {
        return;
    }
    if (index >= 0 && index < layers.size()) {

        layers.remove(index);

        if (currentLayer >= layers.size()) {
            currentLayer = layers.size() - 1;
        }

        repaint();
    }
}
    public void setCurrentLayer(int index) {

    if (index >= 0 && index < layers.size()) {
        currentLayer = index;
    }
}

    public int getLayerCount() {
    return layers.size();
}

    public void zoomIn() {
    if (zoom < 3.0) {
        zoom += 0.1;
        revalidate(); //คำนวนขนาดจัดวางcomponentหม่
        repaint();
    }
}

    public void zoomOut() {
    if (zoom > 0.3) {
        zoom -= 0.1;
        revalidate(); //คำนวนขนาดจัดวางcomponentหม่
        repaint();
    }
}

    public int getZoom() {
    return (int) Math.round(zoom * 100);
}
    private Point screenToPaper(Point p) {
    Rectangle paper = getPaperBounds();

    int x = (int)((p.x - paper.x) / zoom);
    int y = (int)((p.y - paper.y) / zoom);

    return new Point(x, y);
}

    @Override
    public Dimension getPreferredSize() {
    return new Dimension(
        (int)(paperWidth * zoom),
        (int)(paperHeight * zoom)
    );
}

    public Layer getLayer(int index) {
    return layers.get(index);
}

    public int getCurrentLayer() {
    return currentLayer;
}

}

