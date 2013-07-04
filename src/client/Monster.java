package client;

public class Monster {
    private int xPos;
    private int yPos;
    private String name;
    private int hp;
    private int totalHP;
    private int mp;
    private int totalMP;
    
    public Monster(String monsterName, int monsterX, int monsterY, int monsterHP, int monsterTotalHP, int monsterMP, int monsterTotalMana){
        this.xPos=monsterX;
        this.yPos=monsterY;
        this.hp=monsterHP;
        this.totalHP=monsterTotalHP;
        this.mp=monsterMP;
        this.totalMP=monsterTotalMana;
    }
    
    public String returnName(){
        return name;
    }
    
    public void setName(String mname){
        this.name=mname;
    }
    
    public int returnX(){
        return this.xPos;
    }
    
    public void setX(int newX){
        this.xPos=newX;
    }
    
    public int returnY(){
        return this.yPos;
    }
    
    public void setY(int newY){
        this.yPos=newY;
    }
    
    public int returnHP(){
        return this.hp;
    }
    
    public void setHP(int newHP){
        this.hp =newHP;
    }
    
    public int returnTotalHP(){
        return this.totalHP;
    }
    
    public void setTotalHP(int newTotalHP){
        this.totalHP=newTotalHP;
    }
    
    public int returnMana(){
        return this.mp;
    }
    
    public void setMana(int newMana){
        this.mp=newMana;
    }
    
    public int returnTotalMana(){
        return this.totalMP;
    }
    
    public void setTotalMana(int newTotalMana){
        this.totalMP=newTotalMana;
    }
}
