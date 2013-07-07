package server;

class Tile {
    private int tileID;
    private int tileType;
    private int extraID;
    
    public Tile(int type) {
        this.tileType=type;
    }
    
    public int returnID(){
        return this.tileID;
    }
    
    public int returnType(){
        return this.tileType;
    }
    
    public int returnExtra(){
        return this.extraID;
    }
}
