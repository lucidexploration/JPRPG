package server;

public class Account {
    private int accNumber;
    private String password;
    private Characters myChar;
    
    public Account(int accNumber, String password, String charName,int x,int y,int z,int accountType,int hp,int hpTotal,int mana,int manaTotal){
        this.accNumber=accNumber;
        this.password=password;
        this.myChar = new Characters(charName,x,y,z,accountType,hp,hpTotal,mana,manaTotal);
    }
    
    public int returnAccNumber(){
        return this.accNumber;
    }
    
    public String returnPassword(){
        return this.password;
    }
    
    public Characters returnChar(){
        return this.myChar;
    }
}
