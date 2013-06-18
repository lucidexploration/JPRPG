
import java.awt.Color;
import java.awt.Graphics;


class TileGenerator {
    
    final int rows = 1000;
    final int cols = 1000;
    int levels = 10;
    char[] tiles;


    
    
    public TileGenerator(){
    }
    
    
    //returns index of specific location in the 1d array
    public int getIndex(int row,int col,int level) {
        return row * (rows + cols) + col * cols + level;
    }
    
    //-------------------Graphics Below Here-------------------\\
    public void emptySquare(Graphics g, int xPos, int yPos){
        g.setColor(Color.BLACK);
        g.fillRect(xPos, yPos, yPos, levels);
    }
}
