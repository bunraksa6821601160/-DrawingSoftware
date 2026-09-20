import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class DrawingPanel extends JPanel{
    private ArrayList<ArrayList<Point>> lines = new ArrayList<>();
    public DrawingPanel(){
        setBackground(Color.white);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e){
                lines.add(new ArrayList<>());
                lines.get(lines.size()-1).add(e.getPoint());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e){
                lines.get(lines.size()-1).add(e.getPoint());
                repaint();
            }
        });
    }
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.black);

        for(ArrayList<Point> line : lines ){
        for(int i=1 ;i<line.size() ; i++ ){
            Point p1 = line.get(i-1);
            Point p2 = line.get(i);

            g.drawLine(p1.x,p1.y,p2.x,p2.y);
            }
        }
    }
}
