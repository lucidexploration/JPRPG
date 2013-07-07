package server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server extends ServerRunner {

    //--------------------------------------------------------------------------Server objects
    private static int ports[];
    public static ByteBuffer echoBuffer = ByteBuffer.allocate(1024);
    //--------------------------------------------------------------------------Account objects
    public static Map<Integer, Account> accounts;
    public static Map<Integer, Account> loggedInAccounts;
    //--------------------------------------------------------------------------Creature objects
    private static Map<Integer, Monsters> monsters;
    //--------------------------------------------------------------------------Map objects
    private static Map<String, Tile> map;
    //--------------------------------------------------------------------------Interpet incoming packets.

    //=============================================================================================================================================================================
    //--------------------------------------------------------------------------Constructor
    public Server(int ports[]) throws IOException {
        //create new objects
        Server.monsters = new HashMap<>(200);
        Server.accounts = new HashMap<>(200);
        Server.loggedInAccounts = new HashMap<>(100);
        Server.map = new HashMap<>();
        Server.ports = ports;

        //----------------------------------------------------------------------LOAD THINGS.
        loadAccounts();//-------------------------------------------------------Load the accounts from file.
        loadMap();//------------------------------------------------------------Load the map tiles from file.
        loadMonsters();//-------------------------------------------------------Load the monster spawns from file.

        //----------------------------------------------------------------------Start accepting connections from the clients and deal with them.
        manageConnections();
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
                accounts.put(accNumber, new Account(accNumber, password, name, x, y, z, accountType, hp, hpTotal, mana, manaTotal));
            }
        } catch (IOException ex) {
            console.append("Scanner couldnt read the fucking line." + "\n");
        }
        try {
            scanner.close();
        } catch (IOException ex) {
            console.append("scanner never even opened" + "\n");
        }
        console.append("This is whats loaded in accounts at start : " + accounts.keySet() + "\n");
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

        int x = 0;
        while (scanner.hasNext()) {//-------------------------------------------As long as there is more in the file, keep reading.
            String info = scanner.nextLine();
            String[] infoSplit = info.split(",");
            String id = infoSplit[0];
            int tileType = Integer.parseInt(infoSplit[1]);
            map.put(id, new Tile(tileType));
            x++;
        }
        scanner.close();
        console.append("This is whats loaded on the map at start : " + map.keySet() + "\n");
    }

//==================================================================================================================================================================================
    //--------------------------------------------------------------------------LOAD MONSTERS. If monster file doesnt exist. Create it.
    private static void loadMonsters() {
        //load monsters from file
        //check map for spawnpoints and spawn a monster there if
        //-one hasnt been spawned in 1 minute and last one is dead
    }

//==================================================================================================================================================================================
    private static void manageConnections() throws IOException, ClosedChannelException {

        Selector selector = Selector.open();//----------------------------------Create a selector that will by used for multiplexing. The selector registers the socketserverchannel
        //----------------------------------------------------------------------as well as all socketchannels that are created.



        for (int i = 0; i < ports.length; ++i) {//------------------------------Open a listener on each port specified, and register each one with the selector

            ServerSocketChannel ssc = ServerSocketChannel.open();//-------------Create a new serversocketchannel. The channel is unbound.

            ssc.configureBlocking(false);//-------------------------------------Mark the serversocketchannel as non blocking.
            ServerSocket ss = ssc.socket();
            InetSocketAddress address = new InetSocketAddress(ports[i]);
            ss.bind(address);//-------------------------------------------------Bind the channel to an address. The channel starts listening to incoming connections.

            //------------------------------------------------------------------Register the serversocketchannel with the selector. The OP_ACCEPT
            //------------------------------------------------------------------option marks a selection key as ready when the channel accepts a new connection.
            //------------------------------------------------------------------When the socket server accepts a connection this key is added to the list of
            //------------------------------------------------------------------selected keys of the selector.
            //------------------------------------------------------------------when asked for the selected keys, this key is returned and hence we
            //------------------------------------------------------------------know that a new connection has been accepted.
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            console.append("Going to listen on " + ports[i] + "\n");
        }

        while (true) {

            int num = selector.select();//--------------------------------------Wait for the selected keys.


            Set selectedKeys = selector.selectedKeys();//-----------------------The select method returns with a list of selected keys.
            Iterator it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                //--------------------------------------------------------------The selection key could either by the socketserver informing
                //--------------------------------------------------------------that a new connection has been made, or a socket client that is ready for read/write
                //--------------------------------------------------------------we use the properties object attached to the channel to find out the type of channel.

                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {//A new connection has been obtained.

                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();//This channel is therefore a socket server.

                    //----------------------------------------------------------Accept the new connection on the server socket. Since the server socket channel is marked as non blocking
                    //----------------------------------------------------------this channel will return null if no client is connected.
                    SocketChannel sc = ssc.accept();

                    sc.configureBlocking(false);//------------------------------Set the client connection to be non blocking

                    sc.register(selector, SelectionKey.OP_READ);//Add the new connection to the selector
                    it.remove();

                    console.append("Got connection from " + sc + "\n");

                    //===========================================================================================================================================================
                    //----------------------------------------------------------Now we we the client's input.

                } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {//We now have data, so read the data

                    PacketManager.removeClosedAccounts(loggedInAccounts.keySet().iterator(), selector);//We need to make sure all the loggedInAccounts are still logged in.
                    //----------------------------------------------------------Also need to write these closed accounts to the accounts map for when saving and exiting.

                    SocketChannel sc = (SocketChannel) key.channel();

                    while (true) {
                        int number_of_bytes = 0;
                        try {
                            number_of_bytes = sc.read(echoBuffer);
                            console.append("received: " + new String(echoBuffer.array()).trim() + "\n");
                        } catch (java.io.IOException e) {
                            sc.close();
                            SelectionKey i = sc.keyFor(selector);
                            i.cancel();
                        }
                        String message = new String(echoBuffer.array());

                        PacketManager.interpretPacket(message.trim(), sc, selector);
                        try {
                            if (loggedInAccounts.containsKey(PacketManager.returnOnlineKey(sc.getRemoteAddress()))) {
                                int o = 0;
                                while (o < loggedInAccounts.get(PacketManager.returnOnlineKey(sc.getRemoteAddress())).sendBack.length) {
                                    if (!loggedInAccounts.get(PacketManager.returnOnlineKey(sc.getRemoteAddress())).sendBack[o].isEmpty()) {
                                        sc.register(selector, SelectionKey.OP_WRITE);
                                        break;
                                    }
                                    o++;
                                }
                                break;
                            }
                        } catch (ClosedChannelException e) {
                            sc.close();
                        }
                        if (number_of_bytes <= 0) {//---------------------------If there was nothing else to read this cycle, exit the loop.
                            break;
                        }
                    }

                    //----------------------------------------------------------Everything that could be read, was read, and dealt with, so now remove this from the iterator.
                    it.remove();

                    //=======================================================================================================================================================
                    //----------------------------------------------------------NOW WE WRITE TO THE CLIENT
                } else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    //----------------------------------------------------------Check map of logged in accounts to identify this account.
                    PacketManager.sendToClient(sc);//-----------------------------------------Send the messages.
                    PacketManager.eraseSendback(sc);//----------------------------------------Erase the sent messages.
                    echoBuffer = ByteBuffer.allocate(1024);//-------------------Create a new clean buffer for the next go arround.
                    sc.register(selector, SelectionKey.OP_READ);//--------------Everything has been sent to the client, so now we register this socket for reading again.
                    it.remove();
                }
            }
        }
    }

    //=============================================================================================================================================================================
    public static void saveAndExit() throws IOException {
        //----------------------------------------------------------------------Save map first.
        File mapDir = new File(System.getProperty("user.home") + "//JPRPG//");
        File mapFile = new File(mapDir, "map.txt");
        mapFile.delete();
        mapFile.createNewFile();
        FileWriter mapWriter = new FileWriter(mapFile);
        Iterator mapIterator = map.keySet().iterator();
        while (true) {
            while (mapIterator.hasNext()) {
                String nextTile = (String) mapIterator.next();
                String index = nextTile;
                int tileType = map.get(nextTile).returnType();
                //--------------------------------------------------------------Write map to file.
                String writeThis = index + "," + tileType + System.lineSeparator();
                System.out.println(writeThis);
                mapWriter.write(writeThis);
                mapWriter.flush();
            }
            break;
        }
        //----------------------------------------------------------------------Now save accounts.
        File accDir = new File(System.getProperty("user.home") + "//JPRPG//");
        File accFile = new File(accDir, "accounts.txt");
        accFile.delete();
        accFile.createNewFile();
        FileWriter accWriter = new FileWriter(accFile);
        //----------------------------------------------------------------------This assumes that all accounts, even those logged in, are stored in the accounts map.
        Iterator accIterator = accounts.keySet().iterator();
        while (true) {
            while (accIterator.hasNext()) {
                int nextAcc = (int) accIterator.next();
                //--------------------------------------------------------------Prepare values for write.
                int accountNumber = nextAcc;
                String password = accounts.get(nextAcc).returnPassword();
                String name = accounts.get(nextAcc).returnChar().returnName();
                int xPos = accounts.get(nextAcc).returnChar().returnX();
                int yPos = accounts.get(nextAcc).returnChar().returnY();
                int zPos = accounts.get(nextAcc).returnChar().returnZ();
                int accType = 0;
                int hp = accounts.get(nextAcc).returnChar().returnHP();
                int hpTotal = accounts.get(nextAcc).returnChar().returnTotalHP();
                int mp = accounts.get(nextAcc).returnChar().returnMana();
                int mpTotal = accounts.get(nextAcc).returnChar().returnTotalMana();
                //--------------------------------------------------------------Write to file
                String writeThis = accountNumber + "," + password + "," + name + "," + xPos + "," + yPos + "," + zPos + "," + accType + "," + hp + "," + hpTotal + "," + mp + "," + mpTotal + System.lineSeparator();
                accWriter.write(writeThis);
                accWriter.flush();
                System.out.println(writeThis);
            }
            break;
        }
        //----------------------------------------------------------------------We are done. Now exit.
        System.exit(0);
    }
}