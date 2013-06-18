
import java.io.IOException;
import java.net.UnknownHostException;

class GameClient {
    
    private TileGenerator tg;
    private CreatureController cc;
    private GameController gc;
    
    public GameClient() throws UnknownHostException, IOException{
        tg = new TileGenerator();
        cc = new CreatureController();
        gc = new GameController();
    }
}
