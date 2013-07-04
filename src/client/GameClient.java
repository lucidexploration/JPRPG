package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

class GameClient {
    
    private TileGenerator tileGen;
    private GameController gameCon;
    public Map<String,Monster> monsterMap;
    
    public GameClient() throws UnknownHostException, IOException{
        tileGen = new TileGenerator();
        gameCon = new GameController();
        monsterMap = new HashMap<>(100);
    }
    
    public GameController returnGameController(){
        return gameCon;
    }
    
    public TileGenerator returnTileGenerator(){
        return tileGen;
    }
}
