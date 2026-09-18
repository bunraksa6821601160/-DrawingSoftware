
public class Layer {
    private int red[][];
    private int green[][];
    private int blue[][];
    private int alpha[][];

    public Layer(int width, int height){
        red = new int[width][height];
        green = new int[width][height];
        blue = new int[width][height];
        alpha = new int[width][height];
    }

    //setter mothod
    public void setRed(int[][] red) {
        this.red = red;
    }

    public void setGreen(int[][] green) {
        this.green = green;
    }

    public void setBlue(int[][] blue) {
        this.blue = blue;
    }

    public void setAlpha(int[][] alpha) {
        this.alpha = alpha;
    }

    //getter method
    public int[][] getRed() {
        return red;
    }

    public int[][] getGreen() {
        return green;
    }

    public int[][] getBlue() {
        return blue;
    }

    public int[][] getAlpha() {
        return alpha;
    }

    
}
