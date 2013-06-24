
import java.io.IOException;
import java.net.UnknownHostException;

class GameClient {
    
    private TileGenerator tileGen;
    private CreatureController creatureCon;
    private GameController gameCon;
    
    public GameClient() throws UnknownHostException, IOException{
        tileGen = new TileGenerator();
        creatureCon = new CreatureController();
        gameCon = new GameController();
    }
    
    public GameController returnGameController(){
        return gameCon;
    }
    
    public TileGenerator returnTileGenerator(){
        return tileGen;
    }
}
