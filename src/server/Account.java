package server;

public class Account {
    private int accNumber;
    private String password;
    private Characters myChar;
    
    public Account(int accNumber, String password, String charName){
        this.accNumber=accNumber;
        this.password=password;
        this.myChar = new Characters(charName, 1);
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
