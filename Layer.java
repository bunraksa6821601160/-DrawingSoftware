import java.awt.*;
import java.util.*;

public class Layer {
    private String name ;
    private boolean visible = true;
    // เก็บเส้นที่วาดใน Layer นี้
    private ArrayList<ArrayList<Point>> lines ;

    public Layer(String name){
        this.name = name ;
        lines = new ArrayList<>();
    }

    public String getName(){
        return name ;
    }

    public ArrayList<ArrayList<Point>> getLines() {
        return lines;
    }

     public void addLine(ArrayList<Point> line) {
        lines.add(line);
    }
    public boolean isVisible() {
    return visible;
}

    public void setVisible(boolean visible) {
    this.visible = visible;
}
}
