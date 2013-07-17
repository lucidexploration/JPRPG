package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

class TileGenerator {

    //--------------------------------------------------------------------------Preset colors.
    private Color GRASS = new Color(70, 127, 65);
    private Color SKIN = new Color(232, 159, 173);
    private Color WATERCOLOR = new Color(47, 128, 179);
    private Color DIRT = new Color(140, 115, 84);
    private Color IRON = new Color(98, 102, 108);
    //--------------------------------------------------------------------------Map Objects
    private int rows = 10;
    private int cols = 10;
    private int levels = 1;
    private char[] tiles;
    //--------------------------------------------------------------------------Map Sprites
    final int TREE = 1;
    final int WATER = 0;
    //--------------------------------------------------------------------------Sprites
    private Image grass;
    private Image tree;
    public Image character;
    public static Image statusBackground;
    public static Image backGround;
    public static Image overlay;

    public TileGenerator() throws IOException {
        grass = ImageIO.read(getClass().getResourceAsStream("/assets/grass.gif"));
        tree = ImageIO.read(getClass().getResourceAsStream("/assets/tree.gif"));
        character = ImageIO.read(getClass().getResourceAsStream("/assets/character.gif"));
        statusBackground = ImageIO.read(getClass().getResourceAsStream("/assets/statusbackground.png"));
        backGround = ImageIO.read(getClass().getResourceAsStream("/assets/background.png"));
        overlay = ImageIO.read(getClass().getResourceAsStream("/assets/overlay.png"));
        tiles = new char[rows * cols * levels];
    }

    //returns index of specific location in the 1d array
    public int getIndex(int row, int col, int level) {
        return row * (rows + cols) + col * cols + level;
    }

    public void returnTile(int tileNumber, Graphics g, int xPos, int yPos) {
        if (tileNumber == 000) {
            emptySquare(g, xPos, yPos);
        } else {
            emptySquare(g, xPos, yPos);
        }
    }

    public void returnNPC(Graphics g, int npcNumber, int xPos, int yPos) {
        if (npcNumber == 666) {
            admin(g, xPos, yPos);
        } else {
            player(g, xPos, yPos);
        }
    }

    public void returnObject(Graphics g, int object, int xPos, int yPos) {
        g.drawImage(tree, xPos, yPos, null);
    }

    //-------------------Graphics Below Here-------------------\\
    //-------------Tiles--------------
    private void emptySquare(Graphics g, int xPos, int yPos) {
        g.drawImage(grass, xPos, yPos, null);

    }

    //------------People----------------
    private void admin(Graphics g, int xPos, int yPos) {
        g.drawImage(character, xPos, yPos, null);
    }

    private void player(Graphics g, int xPos, int yPos) {
        g.drawImage(character, xPos, yPos, null);
    }
}
