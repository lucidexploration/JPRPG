package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

class GameClient {
    
    private TileGenerator tileGen;
    private GameController gameCon;
    private Map<String,Monster> monsterMap;
    
    public GameClient() throws UnknownHostException, IOException{
        tileGen = new TileGenerator();
        gameCon = new GameController();
        monsterMap = new HashMap<>(100);
    }
    
    public GameController returnGameController(){
        return this.gameCon;
    }
    
    public TileGenerator returnTileGenerator(){
        return this.tileGen;
    }
    
    public Map<String, Monster> returnMap(){
        return this.monsterMap;
    }
}
