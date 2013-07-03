package server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

    //-----------------------CONFIGURE SERVER---------------------------------\\
    private int ports[];
    private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);
    //----------------------------ACCOUNTS------------------------------------\\
    private Map<Integer, Account> accounts;
    //----------------------------CREATURES-----------------------------------\\
    private Map<Integer, Monsters> monsters;
    //----------------------------MAP-----------------------------------------\\
    private Map<Integer, Tile> map;
    final int mapRows = 1000;
    final int mapCols = 1000;
    final int mapLevels = 10;

    public Server(int ports[]) throws IOException {
        //create new objects
        this.monsters = new HashMap<>(200);
        this.accounts = new HashMap<>(200);
        this.map = new HashMap<>(mapRows * mapCols * mapLevels);
        this.ports = ports;

        //----------------------------------------------------------------------LOAD THINGS.
        loadAccounts();
        //loadMap();//----------------------------------------------------------Disabled to more quickly debug other things.
        loadMonsters();
        //----------------------------------------------------------------------CLIENT INPUT.
        configure_selector();
    }

//==================================================================================================================================================================================
    //-----------Load Accounts-if folder or files dont exist-Create them------\\
    private void loadAccounts() {
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
            System.out.println("Scanner couldnt read the fucking line");
        }
        try {
            scanner.close();
        } catch (IOException ex) {
            System.out.println("scanner never even opened");
        }
        System.out.println("This is whats loaded in accounts at start : " + accounts.keySet());
    }

//==================================================================================================================================================================================
    //--------------------------------------------------------------------------LOAD THE MAP. If it doesn't exist, you need to run the empty map maker.
    private void loadMap() {
        Scanner scanner = null;
        File file = new File((System.getProperty("user.home") + "//JPRPG//map.___"));
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {
            System.out.println("File not Found");//-----------------------------If file doesnt exist, you must make with EmptyMapMaker.java.
        }

        int x = 0;
        while (scanner.hasNext()) {//-------------------------------------------As long as there is more in the file, keep reading.
            String info = scanner.nextLine();
            String[] infoSplit = info.split("¬");
            int id = Integer.parseInt(infoSplit[0]);
            int type = Integer.parseInt(infoSplit[1]);
            int extra = Integer.parseInt(infoSplit[2]);
            map.put(x, new Tile(id, type, extra));
            x++;
        }
        scanner.close();
    }

//==================================================================================================================================================================================
    //--------------------------------------------------------------------------Returns the map index of specified X,Y,Z.
    public int getIndex(int row, int col, int level) {
        return row * (mapRows + mapCols) + col * mapCols + level;
    }

//==================================================================================================================================================================================
    //--------------------------------------------------------------------------LOAD MONSTERS. If monster file doesnt exist. Create it.
    private void loadMonsters() {
        //load monsters from file
        //check map for spawnpoints and spawn a monster there if
        //-one hasnt been spawned in 1 minute and last one is dead
    }

//==================================================================================================================================================================================
    private void configure_selector() throws IOException {

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

            System.out.println("Going to listen on " + ports[i]);
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

                    System.out.println("Got connection from " + sc);

                } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {//We now have data, so read the data

                    SocketChannel sc = (SocketChannel) key.channel();

                    while (true) {
                        System.out.println("received: " + new String(echoBuffer.array()));
                        echoBuffer.clear();
                        int number_of_bytes = 0;
                        try {
                            number_of_bytes = sc.read(echoBuffer);
                        } catch (java.io.IOException e) {
                            sc.close();
                        }
                        String message = new String(echoBuffer.array());
                        String[] splits = message.split("¬");//-----------------I chose "¬" because it's a very unlikely character to be used by the players.


                        //-----------------------INTERPRET INCOMING PACKETS-----------------------\\
                        //create account
                        if (splits[0].equals("create")) {
                            //accounts.put(Integer.valueOf(splits[1]), new Account(Integer.valueOf(splits[1]), splits[2], splits[3]));
                            System.out.println(accounts.keySet());
                            //SelectionKey newKey = sc.register(selector, SelectionKey.OP_WRITE);
                        }

                        //------------------------------------------------------LOGIN
                        if (splits[0].equals("login")) {//----------------------If this is a login packet
                            System.out.println("yes");
                            System.out.println(accounts.keySet());
                            if (accounts.containsKey(Integer.parseInt(splits[1]))) {//And if we have this account
                                System.out.println("yep");
                                if (accounts.get(Integer.parseInt(splits[1])).returnPassword().equals(splits[2])) {//And if the password matches
                                    System.out.println("yeppers");

                                    //------------------------------------------Prepare to send the login information
                                    int x = accounts.get(Integer.parseInt(splits[1])).returnChar().returnX();
                                    int y = accounts.get(Integer.parseInt(splits[1])).returnChar().returnY();
                                    String name = accounts.get(Integer.parseInt(splits[1])).returnChar().returnName();
                                    int hp;
                                    int totalhp;
                                    int mana;
                                    int totalmana;
                                    accounts.get(Integer.parseInt(splits[1])).returnChar().setAddress(sc.socket().getInetAddress());

                                    //------------------------------------------Now add it all to the sendBack string.
                                    String sendBack = "login¬" + name + "¬" + x + "¬" + y + "¬\r";

                                    //------------------------------------------Now write it all the the buffer.
                                    echoBuffer.clear();
                                    echoBuffer.put(sendBack.getBytes());
                                    sc.register(selector, SelectionKey.OP_WRITE);
                                    System.out.println("login added: " + new String(echoBuffer.array()));
                                }
                            }
                            break;
                        }

                        //------------------------------------------------------ATTACK
                        if (splits[0].equals("attack")) {
                            //do attack shit
                            sc.register(selector, SelectionKey.OP_WRITE);
                        }

                        //------------------------------------------------------CHAT
                        if (splits[0].equals("chat")) {
                            String name = splits[1];
                            String text = splits[2];

                            //--------------------------------------------------Add everything to sendBack string.
                            String sendBack = "chat¬" + name + "¬" + text + "¬" + "\r";

                            echoBuffer.clear();//-------------------------------Make sure we are writing to the front of the buffer, and not some random place.
                            echoBuffer.put(sendBack.getBytes());
                            sc.register(selector, SelectionKey.OP_WRITE);
                        }

                        //------------------------------------------------------MOVEMENT
                        if (splits[0].equals("move")) {
                            String direction = splits[1];
                            switch (direction) {
                                case "left":
                                    break;
                                case "right":
                                    break;
                                case "up":
                                    break;
                                case "down":
                                    break;
                                default:
                                    break;
                            }
                            //echoBuffer.clear();//-------------------------------Make sure we are writing to the front of the buffer, and not some random place.
                            //echoBuffer.put(sendBack.getBytes());
                            //sc.register(selector, SelectionKey.OP_WRITE);
                        }
                        if (number_of_bytes <= 0) {//---------------------------If there was nothing else to read this cycle, exit the loop.
                            break;
                        }
                    }

                    //----------------------------------------------------------Everything that could be read, was read, so now remove this from the iterator.
                    it.remove();

                } else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {//NOW WE WRITE TO THE CLIENT
                    SocketChannel sc = (SocketChannel) key.channel();
                    echoBuffer.flip();//----------------------------------------Reverse the buffer so that the data is at the front of it.

                    //-----------------------SEND PACKETS TO CLIENT---------------------------\\
                    System.out.println(System.currentTimeMillis() + "  sent: " + new String(echoBuffer.array()));
                    sc.write(echoBuffer);

                    //----------------------------------------------------------Create a new clean buffer for the next go arround.
                    echoBuffer = ByteBuffer.allocate(1024);

                    //----------------------------------------------------------Everything has been sent to the client, so now we register this socket for reading again.
                    sc.register(selector, SelectionKey.OP_READ);
                    it.remove();
                }
            }
        }
    }
//==================================================================================================================================================================================

    private void spawnPlayer() {
        //load player data and send to client all relevant info
        //such as:
        //--surrounding tiles
        //--surrounding npcs/pcs
        //--stats
        //--chat
    }

//==================================================================================================================================================================================
    static public void main(String args[]) throws Exception {
        if (args.length <= 0) {
            System.err.println("Usage: java MultiPortEcho port [port port ...]");
            System.exit(1);
        }

        int ports[] = new int[args.length];

        for (int i = 0; i < args.length; ++i) {
            ports[i] = Integer.parseInt(args[i]);
        }

        new Server(ports);
    }
}