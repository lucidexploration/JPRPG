package server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

    private int ports[];
    private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);
    private Map<Integer, Account> accounts;
    private Map<Integer, Monsters> monsters;
    private int nextAccNumber = 1;

    public Server(int ports[]) throws IOException {
        //create new objects
        this.monsters = new HashMap<>(200);
        this.accounts = new HashMap<>(200);
        this.ports = ports;

        //do shit
        loadAccounts();
        loadMap();
        loadMonsters();
        configure_selector();
    }

    private void loadAccounts() throws IOException {
        Scanner scanner;
        File file = new File("C://temp//accounts.txt");
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {
            file.createNewFile();
            scanner = new Scanner(file);
        }
        while (scanner.hasNext()) {
            int accNumber = scanner.nextInt();
            nextAccNumber = accNumber + 1;
            String password = scanner.next();
            String name = scanner.next();
            accounts.put(accNumber, new Account(accNumber, password, name));
            System.out.println("This is whats loaded in accounts at start : " + accounts.keySet());
        }
    }

    private void loadMap() {
        //load map from file
        //if file doesnt exist, create new blank file
        //map squares can have properties such as :
        //----blocked
        //----water
        //----spawnpoint
        //----
        //on exit, must write server info to file
    }

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

                if ((key.readyOps() & SelectionKey.OP_ACCEPT)== SelectionKey.OP_ACCEPT) {
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
                } 
                else if ((key.readyOps() & SelectionKey.OP_READ)== SelectionKey.OP_READ) {
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
                        String[] splits = message.split(",");
                        //-----------Interpret Packets--------------------
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

                        //-------------Chat-----------------
                        if (splits[0].equals("chat")) {
                            //do chat shit
                            String name = splits[1];
                            String text = splits[2];
                            String sendBack = "chat," + name + "," + text + ","+"\r";
                            echoBuffer.clear();
                            echoBuffer.put(sendBack.getBytes());
                        }

                        //

                        if (number_of_bytes <= 0) {
                            break;
                        }

                        //
                        //
                        echoBuffer.flip();
                        sc.write(echoBuffer);
                        System.out.println("sent : "+new String(echoBuffer.array()));
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