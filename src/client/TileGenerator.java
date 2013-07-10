package client;

import java.awt.Color;
import java.awt.Graphics;


class TileGenerator {
    
    final Color GRASS = new Color(70,127,65);
    final Color SKIN = new Color(232,159,173);
    final Color WATER = new Color(47,128,179);
    final Color DIRT = new Color(140,115,84);
    final Color IRON = new Color(98,102,108);
    final int rows = 10;
    final int cols = 10;
    int levels = 1;
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
    private void emptySquare(Graphics g, int xPos, int yPos){
        g.setColor(GRASS);
        g.fillRect(xPos, yPos, 90, 90);
        g.setColor(Color.lightGray);
        g.drawString("~//\\//\\", xPos, yPos);
        
    }
    
    //------------People----------------
    private void admin(Graphics g, int xPos, int yPos){
        g.setColor(Color.GREEN);
        g.fillOval(xPos+25, yPos+25, 45, 45);
        g.setColor(Color.BLACK);
        g.drawString("@dmin", xPos+30, yPos+48);
    }
    private void player(Graphics g, int xPos, int yPos){
        g.setColor(SKIN);
        g.fillOval(xPos+35, yPos+10, 20, 20);//---------------------------------Head
        g.setColor(Color.BLACK);
        g.drawOval(xPos+35, yPos+10, 20, 20);//---------------------------------Head outline
        g.setColor(Color.RED);
        g.fillRect(xPos+35, yPos+30, 20, 20);//---------------------------------Body
        g.setColor(Color.BLACK);
        g.drawRect(xPos+35, yPos+30, 20, 20);//---------------------------------Body outline
        g.setColor(SKIN);
        g.fillRect(xPos+55, yPos+30, 5, 15);//----------------------------------Right arm
        g.setColor(Color.BLACK);
        g.drawRect(xPos+55, yPos+30, 5, 15);//----------------------------------Right arm outline
        g.setColor(SKIN);
        g.fillRect(xPos+30, yPos+30, 5, 15);//----------------------------------Left arm
        g.setColor(Color.BLACK);
        g.drawRect(xPos+30, yPos+30, 5, 15);//----------------------------------Left arm outline
        g.setColor(Color.RED);
        g.fillRect(xPos+35, yPos+50, 10, 30);//---------------------------------Left leg
        g.setColor(Color.BLACK);
        g.drawRect(xPos+35, yPos+50, 10, 30);//---------------------------------Left leg outline
        g.setColor(Color.RED);
        g.fillRect(xPos+45, yPos+50, 10, 30);//---------------------------------Right leg
        g.setColor(Color.BLACK);
        g.drawRect(xPos+45, yPos+50, 10, 30);//---------------------------------Right leg outline
        g.setColor(Color.BLACK);
        g.fillRect(xPos+35, yPos+80, 21, 5);//---------------------------------Right leg outline
    }
}
