package client;

import java.util.Map;

public class ClientTile {

    private int tileID;
    private int tileType;
    private int objectID;
    Map<Integer, Integer> objectPile;

    public ClientTile(int type) {
        this.tileType = type;
    }

    public int returnID() {
        return this.tileID;
    }

    public int returnType() {
        return this.tileType;
    }

    public int returnObject() {
        return this.objectID;
    }

    public void setObjectID(int object) {
        this.objectID = object;
    }
}
