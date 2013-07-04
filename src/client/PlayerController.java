package client;

class PlayerController {
    private String name = "";
    private int totalHitPoints = 0;
    private int hitpoints = 0;
    private int totalMana = 0;
    private int mana = 0;
    private int xPos;
    private int yPos;
    
    //--------------------------------------------------------------------------Positioning
    //--------------------------------------------------------------------------Set Pos
    public void setPos(int x, int y){
        this.xPos=x;
        this.yPos=y;
    }
    //--------------------------------------------------------------------------Return xPos
    public int returnX(){
        return this.xPos;
    }
    
    public void incX(){
        this.xPos++;
    }
    
    public void decX(){
        this.xPos--;
    }

    //--------------------------------------------------------------------------Return yPos
    public int returnY(){
        return this.yPos;
    }
    
    public void incY(){
        this.yPos++;
    }
    
    public void decY(){
        this.yPos--;
    }
    
    //--------------------------------------------------------------------------Name
    //--------------------------------------------------------------------------Set Name
    public void setName(String name){
        this.name=name;
    }
    //--------------------------------------------------------------------------Return Name
    public String returnName(){
        return this.name;
    }
    //--------------------------------------------------------------------------HP
    //--------------------------------------------------------------------------Set HP
    public void setHP(int hp){
        this.hitpoints=hp;
    }
    //--------------------------------------------------------------------------Return HP
    public int returnHP(){
        return this.hitpoints;
    }
    //--------------------------------------------------------------------------Set Total HP
    public void setTotalHP(int myhp){
        this.totalHitPoints=myhp;
    }
    //--------------------------------------------------------------------------Return Total HP
    public int returnTotalHP(){
        return this.totalHitPoints;
    }
    //--------------------------------------------------------------------------Mana
    //--------------------------------------------------------------------------Set Mana
    public void setMana(int mana){
        this.mana=mana;
    }
    //--------------------------------------------------------------------------Return Mana
    public int returnMana(){
        return this.mana;
    }
    //--------------------------------------------------------------------------Set Total Mana
    public void setTotalMana(int mymana){
        this.totalMana = mymana;
    }
    //--------------------------------------------------------------------------Return Total Mana
    public int returnTotalMana(){
        return this.totalMana;
    }
    
}
