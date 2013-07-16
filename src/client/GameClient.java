package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

class GameClient {

    private TileGenerator tileGen;
    private ClientPacketManager gameCon;
    private Map<String, ClientMonster> monsterMap;
    public Map<String, ClientTile> map;
    private PlayerController playerCon;

    public GameClient() throws UnknownHostException, IOException {
        tileGen = new TileGenerator();
        gameCon = new ClientPacketManager();
        monsterMap = new HashMap<>();
        playerCon = new PlayerController();
        map = new HashMap<>();
    }

    public ClientPacketManager returnGameController() {
        return this.gameCon;
    }

    public TileGenerator returnTileGenerator() {
        return this.tileGen;
    }

    public Map<String, ClientMonster> returnNPCMap() {
        return this.monsterMap;
    }

    public PlayerController returnPlayerCon() {
        return this.playerCon;
    }
}
