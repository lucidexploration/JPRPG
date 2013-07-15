package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


class Loader {
    
    static void loadAll() {
        loadAccounts();
        loadMap();
        loadObjects();
        loadMonsters();
    }
    
    //=============================================================================================================================================================================
    //-----------Load Accounts-if folder or files dont exist-Create them------\\
    private static void loadAccounts() {
        File dir = new File(System.getProperty("user.home") + "//JPRPG//");
        File file = new File(dir, "accounts.txt");
        BufferedReader scanner = null;
        String parse = "";
        try {
            scanner = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {//----------------------------------If file doesnt exist, create it.
            try {
                file.createNewFile();
                scanner = new BufferedReader(new FileReader(file));
            } catch (IOException ex1) {//---------------------------------------If folders dont exist, create them.
                file.mkdirs();
            }
        }
        try {
            while ((parse = scanner.readLine()) != null) {//--------------------As long as there is more in the file, keep reading.
                String[] info = parse.split(",");//-----------------------------Parse the line just read from the file.

                //--------------------------------------------------------------Setup the variables.
                int accNumber = Integer.parseInt(info[0]);
                String password = info[1];
                String name = info[2];
                int x = Integer.parseInt(info[3]);
                int y = Integer.parseInt(info[4]);
                int z = Integer.parseInt(info[5]);
                int accountType = Integer.parseInt(info[6]);
                int hp = Integer.parseInt(info[7]);
                int hpTotal = Integer.parseInt(info[8]);
                int mana = Integer.parseInt(info[9]);
                int manaTotal = Integer.parseInt(info[10]);

                //--------------------------------------------------------------Add the variables to server memory on the accounts map.
                Server.accounts.put(accNumber, new Account(accNumber, password, name, x, y, z, accountType, hp, hpTotal, mana, manaTotal));
            }
        } catch (IOException ex) {
            Server.console.append("Scanner couldnt read the fucking line." + "\n");
        }
        try {
            scanner.close();
        } catch (IOException ex) {
            Server.console.append("scanner never even opened" + "\n");
        }
        Server.console.append("These accounts loaded : " + Server.accounts.keySet() + "\n");
    }

//==================================================================================================================================================================================
    //--------------------------------------------------------------------------LOAD THE MAP. If it doesn't exist, you need to run the empty map maker.
    private static void loadMap() {
        Scanner scanner = null;
        File dir = new File(System.getProperty("user.home") + "//JPRPG//");
        File file = new File(dir, "map.txt");
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {//----------------------------------If file doesnt exist, create it.
            try {
                file.createNewFile();
                scanner = new Scanner(file);
            } catch (IOException ex1) {//---------------------------------------If folders dont exist, create them.
                file.mkdirs();
            }
        }

        while (scanner.hasNext()) {//-------------------------------------------As long as there is more in the file, keep reading.
            String info = scanner.nextLine();
            String[] infoSplit = info.split(",");
            String id = infoSplit[0];
            int tileType = Integer.parseInt(infoSplit[1]);
            Server.map.put(id, new Tile(tileType));
        }
        scanner.close();
        Server.console.append("Map tiles loaded. \n");
    }
    
    //==============================================================================================================================================================================
    //--------------------------------------------------------------------------LOAD Items/objects. If monster file doesnt exist. Create it.
    
    private static void loadObjects() {
        Scanner scanner = null;
        File dir = new File(System.getProperty("user.home") + "//JPRPG//");
        File file = new File(dir, "objects.txt");
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {//----------------------------------If file doesnt exist, create it.
            try {
                file.createNewFile();
                scanner = new Scanner(file);
            } catch (IOException ex1) {//---------------------------------------If folders dont exist, create them.
                file.mkdirs();
            }
        }
        int x = 2;
        while (scanner.hasNext()) {//-------------------------------------------As long as there is more in the file, keep reading.
            String info = scanner.nextLine();
            String[] infoSplit = info.split(",");
            String id = infoSplit[0];
            int objectType = Integer.parseInt(infoSplit[1]);
            Server.map.get(id).setObjectID(objectType);
            while(x<infoSplit.length){
                Server.map.get(id).objectPile.put(x, Integer.parseInt(infoSplit[x]));
                x++;
            }
        }
        scanner.close();
        Server.console.append("Objects have been loaded. \n");
    }

//==================================================================================================================================================================================
    //--------------------------------------------------------------------------LOAD MONSTERS. If monster file doesnt exist. Create it.
    private static void loadMonsters() {
        //load monsters from file
        //check map for spawnpoints and spawn a monster there if
        //-one hasnt been spawned in 1 minute and last one is dead
    }
}
