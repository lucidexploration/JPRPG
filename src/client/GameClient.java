package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

class GameClient {

    private TileGenerator tileGen;
    private PacketManager gameCon;
    private Map<String, Monster> monsterMap;
    public Map<String, Tile> map;
    private PlayerController playerCon;

    public GameClient() throws UnknownHostException, IOException {
        tileGen = new TileGenerator();
        gameCon = new PacketManager();
        monsterMap = new HashMap<>();
        playerCon = new PlayerController();
        map = new HashMap<>();
    }

    public PacketManager returnGameController() {
        return this.gameCon;
    }

    public TileGenerator returnTileGenerator() {
        return this.tileGen;
    }

    public Map<String, Monster> returnNPCMap() {
        return this.monsterMap;
    }

    public PlayerController returnPlayerCon() {
        return this.playerCon;
    }
}
