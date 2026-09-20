import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class DrawingPanel extends JPanel{
    private ArrayList<Layer> layers = new ArrayList<>();

    private int currentLayer = 0;
    
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
                    line.add(e.getPoint());
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
                    lines.get(lines.size() - 1).add(e.getPoint());

                    repaint();
                }

            }
        });
    }

    //คำนวนขนาดกระดาษ
    private Rectangle getPaperBounds() {

    int paperX = (getWidth() - paperWidth) / 2;
    int paperY = (getHeight() - paperHeight) / 2;

    return new Rectangle(paperX,paperY,paperWidth,paperHeight);
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
               for (ArrayList<Point> line : layer.getLines()) {
        for(int i=1 ;i<line.size() ; i++ ){
            Point p1 = line.get(i-1);
            Point p2 = line.get(i);

            g.drawLine(p1.x,p1.y,p2.x,p2.y);
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

}

