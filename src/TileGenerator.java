
import java.awt.Color;
import java.awt.Graphics;


class TileGenerator {
    
    final int rows = 1000;
    final int cols = 1000;
    int levels = 10;
    char[] tiles;


    
    
    public TileGenerator(){
        tiles = new char[rows*cols*levels];
    }
    
    
    //returns index of specific location in the 1d array
    public int getIndex(int row,int col,int level) {
        return row * (rows + cols) + col * cols + level;
    }
    
    public void returnTile(int tileNumber, Graphics g, int xPos, int yPos){
        if(tileNumber==000)
            emptySquare(g,xPos,yPos);
        else
            emptySquare(g,xPos,yPos);
    }
    public void returnNPC(int npcNumber,Graphics g, int xPos, int yPos){
        if(npcNumber==666)
            admin(g,xPos,yPos);
        else
            player(g,xPos,yPos);
            
    }
    
    //-------------------Graphics Below Here-------------------\\
    //-------------Tiles--------------
    public void emptySquare(Graphics g, int xPos, int yPos){
        g.setColor(Color.BLACK);
        g.fillRect(xPos, yPos, 90, 90);
    }
    
    //------------People----------------
    public void admin(Graphics g, int xPos, int yPos){
        g.setColor(Color.GREEN);
        g.fillOval(xPos+25, yPos+25, 45, 45);
        g.setColor(Color.BLACK);
        g.drawString("@dmin", xPos+30, yPos+48);
    }
    public void player(Graphics g, int xPos, int yPos){
        g.setColor(Color.ORANGE);
        g.fillOval(xPos+25, yPos+25, 45, 45);
        g.setColor(Color.BLACK);
        g.drawString("@", xPos+44, yPos+48);
    }
}
