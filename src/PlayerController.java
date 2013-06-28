
import java.util.HashMap;


class PlayerController {
    private HashMap creatureMap;
    private HashMap playerMap;
    private String name = "";
    private int hitpoints = 0;
    private int mana = 0;
    private int xPos = 0;
    private int yPos = 0;
    private int zPos = 0;
    
    public PlayerController(String name,int hp, int mp, int x, int y, int z){
        this.name=name;
        this.hitpoints=hp;
        this.mana=mp;
        this.xPos=x;
        this.yPos=y;
        this.zPos=z;
    }
}
