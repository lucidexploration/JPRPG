package server;

class Tile {
    private int tileID;
    private int tileType;
    private int extraID;
    
    public Tile(int id, int type, int extra) {
        this.tileID=id;
        this.tileType=type;
        this.extraID=extra;
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
