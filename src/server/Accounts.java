package server;

import java.net.Socket;
import java.net.SocketAddress;

public class Accounts {
    private int accNumber;
    private String password;
    private Character myChar;
    public String[] sendBack;
    private Socket mySock;
    private SocketAddress myAddress;
    
    public Accounts(int accNumber, String password, String charName,int x,int y,int z,int accountType,int hp,int hpTotal,int mana,int manaTotal){
        this.accNumber=accNumber;
        this.password=password;
        this.myChar = new Character(charName,x,y,z,accountType,hp,hpTotal,mana,manaTotal);
        this.sendBack = new String[100];
        //----------------------------------------------------------------------Initialize the sendBack[] so that nothing is null.
        int o = 0;
        while(o<sendBack.length){
            sendBack[o]="";
            o++;
        }
    }
    
    public int returnAccNumber(){
        return this.accNumber;
    }
    
    public String returnPassword(){
        return this.password;
    }
    
    public Character returnChar(){
        return this.myChar;
    }
    public void setSocket(Socket sock){
        this.mySock=sock;
    }
    public Socket returnSocket(){
        return this.mySock;
    }
    public void setAddress(SocketAddress i){
        this.myAddress=i;
    }
    public SocketAddress returnAddress(){
        return this.myAddress;
    }
}
