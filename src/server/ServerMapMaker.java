package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class ServerMapMaker {

    public static int xLimit = 500;
    public static int yLimit = 500;

    public static void main(String args[]) throws FileNotFoundException, IOException {
        run();
    }

    private static void run() throws FileNotFoundException, IOException {
        createMap();
    }

    public static String getIndex(Integer x, Integer y, Integer z) {
        byte newX = x.byteValue();
        byte newY = y.byteValue();
        byte newZ = z.byteValue();
        //creating byte array 
        byte[] position = {newX, newY, newZ};

        //creating UUID from byte     
        UUID uuid = UUID.nameUUIDFromBytes(position);
        return uuid.toString();
    }

    private static void createMap() throws FileNotFoundException, IOException {
        int x = -500;
        int y = -500;
        File mapDir = new File(System.getProperty("user.home") + "//JPRPG//");
        File mapFile = new File(mapDir, "map.txt");
        FileWriter mapWriter = new FileWriter(mapFile);
        while (x < xLimit) {
            while (y < yLimit) {
                String tileType = getTileType(x, y, 0);
                if (!tileType.equals("0")) {
                    String writeThis = getIndex(x, y, 0);
                    writeThis = writeThis + "," + tileType;
                    writeThis = writeThis + System.lineSeparator();
                    mapWriter.write(writeThis);
                    mapWriter.flush();
                    writeThis = "";
                }
                y++;
            }
            y = -500;
        x++;
        }
        
    }

    private static String getTileType(int x, int y, int i) {
        if ((x + y) % 7 == 0) {
            return "1";
        }
        if ((x + y) % 3 == 0) {
            return "2";
        } else {
            return "0";
        }
    }
}
