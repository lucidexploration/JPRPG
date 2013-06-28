package server;

import java.net.InetAddress;


class Characters {
    private String name;
    private int charType;
    private int xPos;
    private int yPos;
    private int zPos;
    private InetAddress address;
    
    public Characters(String name,int charType){
        this.name=name;
        this.charType=charType;
        this.xPos=0;
        this.yPos=0;
        this.zPos=0;
    }
    
    public String returnName(){
        return this.name;
    }
    
    public int returnCharType(){
        return this.charType;
    }
    
    public int returnX(){
        return this.xPos;
    }
    
    public int returnY(){
        return this.yPos;
    }
    
    public int returnZ(){
        return this.zPos;
    }
    
    public void setAddress(InetAddress address){
        this.address=address;
    }
    
    public InetAddress returnAddress(){
        return this.address;
    }
}
