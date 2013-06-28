package server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    //-----------------------CONFIGURE SERVER---------------------------------\\
    private int ports[];
    private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);
    //-----------------------LOAD ACCOUNTS------------------------------------\\
    private Map<Integer, Account> accounts;
    //-----------------------LOAD CREATURES-----------------------------------\\
    private Map<Integer, Monsters> monsters;
    //-----------------------LOAD MAP-----------------------------------------\\
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

        //do shit
        loadAccounts();
        loadMap();
        loadMonsters();
        configure_selector();
    }

    //-----------Load Accounts-if folder or files dont exist-Create them------\\
    private void loadAccounts() {
        Scanner scanner = null;
        File file = new File((System.getProperty("user.home") + "//JPRPG//accounts.acc"));
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {
            try {
                file.createNewFile();
                scanner = new Scanner(file);
            } catch (IOException ex1) {
                file.mkdirs();
            }
        }
        while (scanner.hasNext()) {
            int accNumber = scanner.nextInt();
            String password = scanner.next();
            String name = scanner.next();
            accounts.put(accNumber, new Account(accNumber, password, name));
            System.out.println("This is whats loaded in accounts at start : " + accounts.keySet());
        }
    }

    //-------------Load Map-if folder or files dont exist-Create them---------\\
    private void loadMap() {
        //load map from file
        //on exit, must write server info to file
        Scanner scanner = null;
        File file = new File((System.getProperty("user.home") + "//JPRPG//map.___"));
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {
            System.out.println("File not Found");
        }

        int x = 0;
        while (scanner.hasNext()) {
            //public Tile(int id, int type, int extra, int x, int y, int z)
            String info = scanner.nextLine();
            String[] infoSplit = info.split("¬");
            int id = Integer.parseInt(infoSplit[0]);
            int type = Integer.parseInt(infoSplit[1]);
            int extra = Integer.parseInt(infoSplit[2]);
            map.put(x, new Tile(id, type, extra));
            x++;
        }
    }

    //will return the map<id> for the specified x,y,z
    public int getIndex(int row, int col, int level) {
        return row * (mapRows + mapCols) + col * mapCols + level;
    }

    //----------Load creatures-if folder or files dont exist-Create them------\\
    private void loadMonsters() {
        //load monsters from file
        //check map for spawnpoints and spawn a monster there if
        //-one hasnt been spawned in 1 minute and last one is dead
    }

    private void configure_selector() throws IOException {
        // create a selector that will by used for multiplexing. The selector
        // registers the socketserverchannel as
        // well as all socketchannels that are created
        Selector selector = Selector.open();

        // Open a listener on each port, and register each one
        // with the selector
        for (int i = 0; i < ports.length; ++i) {
            // create a new serversocketchannel. The channel is unbound.
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // mark the serversocketchannel as non blocking
            ssc.configureBlocking(false);
            ServerSocket ss = ssc.socket();
            // bind the channel to an address. The channel starts listening to
            // incoming connections.
            InetSocketAddress address = new InetSocketAddress(ports[i]);
            ss.bind(address);

            // register the serversocketchannel with the selector. The OP_ACCEPT
            // option marks a selection key as ready when the channel accepts a new connection.
            // When the socket server accepts a connection this key is added to the list of
            // selected keys of the selector.
            // when asked for the selected keys, this key is returned and hence we
            // know that a new connection has been accepted.
            SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Going to listen on " + ports[i]);
        }

        while (true) {
            // wait for the selected keys
            int num = selector.select();

            // the select method returns with a list of selected keys
            Set selectedKeys = selector.selectedKeys();
            Iterator it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                // the selection key could either by the socketserver informing
                // that a new connection has been made, or
                // a socket client that is ready for read/write
                // we use the properties object attached to the channel to find
                // out the type of channel.

                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                    // a new connection has been obtained. This channel is
                    // therefore a socket server.
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    // accept the new connection on the server socket. Since the
                    // server socket channel is marked as non blocking
                    // this channel will return null if no client is connected.
                    SocketChannel sc = ssc.accept();
                    // set the client connection to be non blocking
                    sc.configureBlocking(false);
                    // Add the new connection to the selector
                    SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
                    it.remove();

                    System.out.println("Got connection from " + sc);
                } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    // Read the data
                    SocketChannel sc = (SocketChannel) key.channel();

                    // interpret
                    int bytesEchoed = 0;
                    while (true) {
                        //Clears this buffer.
                        echoBuffer.clear();

                        int number_of_bytes;
                        try {
                            number_of_bytes = sc.read(echoBuffer);
                        } catch (java.io.IOException e) {
                            sc.close();
                            number_of_bytes = -1;
                        }
                        String message = new String(echoBuffer.array());
                        String[] splits = message.split("¬");


                        //-----------------------INTERPRET INCOMING PACKETS-----------------------\\
                        //create account
                        if (splits[0].equals("create")) {
                            accounts.put(Integer.valueOf(splits[1]), new Account(Integer.valueOf(splits[1]), splits[2], splits[3]));
                            System.out.println(accounts.keySet());
                        }

                        //login
                        if (splits[0].equals("login")) {
                            String serverPassword = accounts.get(1).returnPassword();
                            String clientPassword = new String(splits[2].getBytes());
                            if (serverPassword.equals(clientPassword)) {
                                System.out.println("we have this account");
                            } else {
                                System.out.println("no account or wrong info");
                            }
                        }

                        //attack
                        if (splits[0].equals("attack")) {
                            //do attack shit
                        }

                        //chat
                        if (splits[0].equals("chat")) {
                            //do chat shit
                            String name = splits[1];
                            String text = splits[2];
                            String sendBack = "chat¬" + name + "¬" + text + "¬" + "\r";
                            echoBuffer.clear();
                            echoBuffer.put(sendBack.getBytes());
                        }


                        //-----------------------If the bytebuffer is empty, exit----------------\\
                        if (number_of_bytes <= 0) {
                            break;
                        }

                        //-----------------------SEND PACKETS TO CLIENT---------------------------\\
                        echoBuffer.flip();
                        sc.write(echoBuffer);
                        System.out.println("sent : " + new String(echoBuffer.array()));
                        bytesEchoed += number_of_bytes;
                    }

                    System.out.println("Echoed " + bytesEchoed + " from " + sc);

                    // once a key is handled, it needs to be removed
                    it.remove();
                }

            }
        }
    }

    private void spawnPlayer() {
        //load player data and send to client all relevant info
        //such as:
        //--surrounding tiles
        //--surrounding npcs/pcs
        //--stats
        //--chat
    }

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