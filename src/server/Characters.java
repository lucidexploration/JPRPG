package server;

class Characters {

    private String name = "";
    private int totalHitPoints = 0;
    private int hitpoints = 0;
    private int totalMana = 0;
    private int mana = 0;
    private int xPos = 0;
    private int yPos = 0;
    private int zPos = 0;
    private int charType;

    public Characters(String charName,int x,int y,int z,int accountType,int hp,int hpTotal,int mana,int manaTotal) {
        this.name = charName;
        this.charType = accountType;
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.hitpoints=hp;
        this.totalHitPoints=hpTotal;
        this.mana=mana;
        this.totalMana=manaTotal;
    }

    //--------------------------------------------------------------------------Positioning
    //--------------------------------------------------------------------------Set Pos

    public void setPos(int x, int y, int z) {
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
    }
    
    public void incX(){
        this.xPos++;
    }
    
    public void decX(){
        this.xPos--;
    }
    
    public void incY(){
        this.yPos++;
    }
    
    public void decY(){
        this.yPos--;
    }
    
    public void decZ(){
        this.zPos--;
    }
    
    public void incZ(){
        this.zPos++;
    }
    //--------------------------------------------------------------------------Return xPos

    public int returnX() {
        return this.xPos;
    }
    //--------------------------------------------------------------------------Return yPos

    public int returnY() {
        return this.yPos;
    }
    //--------------------------------------------------------------------------Return zPos

    public int returnZ() {
        return this.zPos;
    }

    //--------------------------------------------------------------------------Name
    //--------------------------------------------------------------------------Set Name
    public void setName(String name) {
        this.name = name;
    }
    //--------------------------------------------------------------------------Return Name

    public String returnName() {
        return this.name;
    }
    //--------------------------------------------------------------------------HP
    //--------------------------------------------------------------------------Set HP

    public void setHP(int hp) {
        this.hitpoints = hp;
    }
    //--------------------------------------------------------------------------Return HP

    public int returnHP() {
        return this.hitpoints;
    }
    //--------------------------------------------------------------------------Return Total HP

    public int returnTotalHP() {
        return this.totalHitPoints;
    }
    //--------------------------------------------------------------------------Mana
    //--------------------------------------------------------------------------Set Mana

    public void setMana(int mana) {
        this.mana = mana;
    }
    //--------------------------------------------------------------------------Return Mana

    public int returnMana() {
        return this.mana;
    }
    //--------------------------------------------------------------------------Return Total Mana

    public int returnTotalMana() {
        return this.totalMana;
    }
}
