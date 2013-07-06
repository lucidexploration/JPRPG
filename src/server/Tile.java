package server;

class Tile {
    private int tileID;
    private int tileType;
    private int extraID;
    
    public Tile(int id) {
        this.tileID=id;
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
