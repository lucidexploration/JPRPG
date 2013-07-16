package server;

import java.util.HashMap;
import java.util.Map;

class ServerTile {
    private int tileID;
    private int tileType;
    private int objectID;
    Map<Integer,Integer> objectPile;
    
    public ServerTile(int type) {
        this.tileType=type;
        this.objectID=0;
        this.tileType=0;
        this.objectPile = new HashMap<>();
    }
    
    public int returnID(){
        return this.tileID;
    }
    
    public int returnType(){
        return this.tileType;
    }
    
    public int returnObject(){
        return this.objectID;
    }
    
    public void setObjectID(int object){
        this.objectID=object;
    }
}
