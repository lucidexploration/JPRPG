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
    private Map<Integer, Account> loggedInAccounts;
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
        this.loggedInAccounts = new HashMap<>(100);
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
    private void configure_selector() throws IOException, ClosedChannelException {

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

                    //----------------------------------------------------------We need to make sure all the loggedInAccounts are still logged in.
                    Iterator nextCheck = loggedInAccounts.keySet().iterator();
                    while (true) {//--------------------------------------------Keep going until done
                        while (nextCheck.hasNext()) {//------------------------------As long as there are more accounts to check.
                            int theKey;
                            try {
                                theKey = (Integer) nextCheck.next();
                            } catch (ConcurrentModificationException e) {
                                break;
                            }
                            if (loggedInAccounts.get(theKey).returnSocket().isClosed()) {
                                loggedInAccounts.remove(theKey);
                            }
                        }
                        break;//------------------------------------------------We have checked all accounts, so exit the loop.
                    }

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
                            System.out.println(accounts.keySet());
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
                                    int hp = accounts.get(Integer.parseInt(splits[1])).returnChar().returnHP();
                                    int totalhp = accounts.get(Integer.parseInt(splits[1])).returnChar().returnTotalHP();
                                    int mana = accounts.get(Integer.parseInt(splits[1])).returnChar().returnMana();
                                    int totalmana = accounts.get(Integer.parseInt(splits[1])).returnChar().returnTotalMana();

                                    //------------------------------------------DO IDENTIFICATION WORK
                                    //------------------------------------------Assign accounts socket for later writing.
                                    accounts.get(Integer.parseInt(splits[1])).setSocket(sc.socket());
                                    //------------------------------------------Assign accounts socketAddress for identifying for writing.
                                    accounts.get(Integer.parseInt(splits[1])).setAddress(sc.getRemoteAddress());
                                    //------------------------------------------Add this account to the logged on accounts map.
                                    loggedInAccounts.put((Integer) Integer.parseInt(splits[1]), accounts.get(Integer.parseInt(splits[1])));
                                    //------------------------------------------Now add it all to the sendBack string.
                                    String sendBack = "login¬" + name + "¬" + x + "¬" + y + "¬" + hp + "¬" + totalhp + "¬" + mana + "¬" + totalmana + "¬\r";

                                    //------------------------------------------Write the string to the accounts sendBack[]
                                    int b = 0;
                                    while (true) {
                                        if (accounts.get(Integer.parseInt(splits[1])).sendBack[b].length() > 1 || accounts.get(Integer.parseInt(splits[1])).sendBack[b].length() < 3) {
                                            accounts.get(Integer.parseInt(splits[1])).sendBack[b] = sendBack;
                                            break;
                                        }
                                        b++;
                                        if (b >= 10) {
                                            break;
                                        }
                                    }

                                    //------------------------------------------Register this account for writing to send the login information back.
                                    sc.register(selector, SelectionKey.OP_WRITE);
                                    System.out.println("login added: " + loggedInAccounts.keySet());
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
                            writeToAllOnline(selector, sendBack);
                        }

                        //------------------------------------------------------MOVEMENT
                        try {
                            if (splits[0].equals("move")) {
                                String direction = splits[1];
                                int o = 0;
                                Iterator next = loggedInAccounts.keySet().iterator();//This iterator contains the list of logged in accounts to cycle through.

                                switch (direction) {
                                    case "left":
                                        while (true) {//------------------------We need to identify the account.Until we are done, continue
                                            while (next.hasNext()) {//----------If there are more still in the list, continue.
                                                int theKey = (Integer) next.next();
                                                if (loggedInAccounts.get(theKey).returnAddress() == sc.getRemoteAddress()) {//If this socket's address is equal to an address in the list, keep going.
                                                    //------------------------------Update this characters position.
                                                    loggedInAccounts.get(theKey).returnChar().decX();
                                                    //------------------------------Setup Variables
                                                    String monsterName = loggedInAccounts.get(theKey).returnChar().returnName();
                                                    int monsterX = loggedInAccounts.get(theKey).returnChar().returnX();
                                                    int monsterY = loggedInAccounts.get(theKey).returnChar().returnY();
                                                    int monsterHP = loggedInAccounts.get(theKey).returnChar().returnHP();
                                                    int monsterTotalHP = loggedInAccounts.get(theKey).returnChar().returnTotalHP();
                                                    int monsterMP = loggedInAccounts.get(theKey).returnChar().returnMana();
                                                    int monsterTotalMP = loggedInAccounts.get(theKey).returnChar().returnTotalMana();

                                                    //------------------------------Prepare message for write to other players.
                                                    String sendBack = "monsterInRange¬" + monsterName + "¬" + monsterX + "¬" + monsterY + "¬" + monsterHP + "¬" + monsterTotalHP + "¬" + monsterMP + "¬" + monsterTotalMP + "¬+\r";
                                                    notifyAllInRange(theKey, selector, sendBack);
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                        break;
                                    case "right":
                                        while (true) {//----------------------------We need to identify the account.Until we are done, continue
                                            while (next.hasNext()) {//--------------If there are more still in the list, continue.
                                                int theKey = (Integer) next.next();
                                                if (loggedInAccounts.get(theKey).returnAddress() == sc.getRemoteAddress()) {//If this socket's address is equal to an address in the list, keep going.
                                                    //------------------------------Update this characters position.
                                                    loggedInAccounts.get(theKey).returnChar().incX();
                                                    //------------------------------Setup Variables
                                                    String monsterName = loggedInAccounts.get(theKey).returnChar().returnName();
                                                    int monsterX = loggedInAccounts.get(theKey).returnChar().returnX();
                                                    int monsterY = loggedInAccounts.get(theKey).returnChar().returnY();
                                                    int monsterHP = loggedInAccounts.get(theKey).returnChar().returnHP();
                                                    int monsterTotalHP = loggedInAccounts.get(theKey).returnChar().returnTotalHP();
                                                    int monsterMP = loggedInAccounts.get(theKey).returnChar().returnMana();
                                                    int monsterTotalMP = loggedInAccounts.get(theKey).returnChar().returnTotalMana();

                                                    //------------------------------Prepare message for write to other players.
                                                    String sendBack = "monsterInRange¬" + monsterName + "¬" + monsterX + "¬" + monsterY + "¬" + monsterHP + "¬" + monsterTotalHP + "¬" + monsterMP + "¬" + monsterTotalMP + "¬+\r";
                                                    notifyAllInRange(theKey, selector, sendBack);
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                        break;
                                    case "up":
                                        while (true) {//----------------------------We need to identify the account.Until we are done, continue
                                            while (next.hasNext()) {//--------------If there are more still in the list, continue.
                                                int theKey = (Integer) next.next();
                                                if (loggedInAccounts.get(theKey).returnAddress() == sc.getRemoteAddress()) {//If this socket's address is equal to an address in the list, keep going.
                                                    //------------------------------Update this characters position.
                                                    loggedInAccounts.get(theKey).returnChar().decY();
                                                    //------------------------------Setup Variables
                                                    String monsterName = loggedInAccounts.get(theKey).returnChar().returnName();
                                                    int monsterX = loggedInAccounts.get(theKey).returnChar().returnX();
                                                    int monsterY = loggedInAccounts.get(theKey).returnChar().returnY();
                                                    int monsterHP = loggedInAccounts.get(theKey).returnChar().returnHP();
                                                    int monsterTotalHP = loggedInAccounts.get(theKey).returnChar().returnTotalHP();
                                                    int monsterMP = loggedInAccounts.get(theKey).returnChar().returnMana();
                                                    int monsterTotalMP = loggedInAccounts.get(theKey).returnChar().returnTotalMana();

                                                    //------------------------------Prepare message for write to other players.
                                                    String sendBack = "monsterInRange¬" + monsterName + "¬" + monsterX + "¬" + monsterY + "¬" + monsterHP + "¬" + monsterTotalHP + "¬" + monsterMP + "¬" + monsterTotalMP + "¬+\r";
                                                    notifyAllInRange(theKey, selector, sendBack);
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                        break;
                                    case "down":
                                        while (true) {//----------------------------We need to identify the account.Until we are done, continue
                                            while (next.hasNext()) {//--------------If there are more still in the list, continue.
                                                int theKey = (Integer) next.next();
                                                if (loggedInAccounts.get(theKey).returnAddress() == sc.getRemoteAddress()) {//If this socket's address is equal to an address in the list, keep going.
                                                    //------------------------------Setup Variables
                                                    loggedInAccounts.get(theKey).returnChar().incY();
                                                    String monsterName = loggedInAccounts.get(theKey).returnChar().returnName();
                                                    int monsterX = loggedInAccounts.get(theKey).returnChar().returnX();
                                                    int monsterY = loggedInAccounts.get(theKey).returnChar().returnY();
                                                    int monsterHP = loggedInAccounts.get(theKey).returnChar().returnHP();
                                                    int monsterTotalHP = loggedInAccounts.get(theKey).returnChar().returnTotalHP();
                                                    int monsterMP = loggedInAccounts.get(theKey).returnChar().returnMana();
                                                    int monsterTotalMP = loggedInAccounts.get(theKey).returnChar().returnTotalMana();

                                                    //------------------------------Prepare message for write to other players.
                                                    String sendBack = "monsterInRange¬" + monsterName + "¬" + monsterX + "¬" + monsterY + "¬" + monsterHP + "¬" + monsterTotalHP + "¬" + monsterMP + "¬" + monsterTotalMP + "¬+\r";
                                                    notifyAllInRange(theKey, selector, sendBack);
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } catch (ClosedChannelException e) {//------------------If someone closes their socket in the middle of this, handle it.
                            sc.close();
                            break;
                        }

                        if (number_of_bytes <= 0) {//---------------------------If there was nothing else to read this cycle, exit the loop.
                            break;
                        }
                    }

                    //----------------------------------------------------------Everything that could be read, was read, and dealt with, so now remove this from the iterator.
                    it.remove();

                } else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {//NOW WE WRITE TO THE CLIENT
                    SocketChannel sc = (SocketChannel) key.channel();

                    //----------------------------------------------------------Check map of logged in accounts to identify this account.
                    int o = 0;
                    Iterator next = loggedInAccounts.keySet().iterator();//-----This iterator contains the list of logged in accounts to cycle through. ;D
                    while (true) {//--------------------------------------------Cycle through the list until done.
                        while (next.hasNext()) {//------------------------------If there is more in the list, continue.
                            int theKey = (Integer) next.next();
                            if (loggedInAccounts.get(theKey).returnAddress() == sc.getRemoteAddress()) {//If this socket's address is equal to an address in the list, keep going.
                                while (o < loggedInAccounts.get(theKey).sendBack.length) {//Write and send everything on this clients sendBack[].
                                    if (loggedInAccounts.get(theKey).sendBack[o].length() > 1) {//As long as there is something in this slot of the sendBack[], write it.
                                        echoBuffer.clear();//-------------------Reset buffer so that we write to the front of it.
                                        echoBuffer.put(loggedInAccounts.get(theKey).sendBack[0].getBytes());
                                        System.out.println("Sent : " + loggedInAccounts.get(theKey).sendBack[0] + "  To : " + sc.getRemoteAddress());
                                        echoBuffer.flip();//--------------------Flip the echoBuffer for writing.
                                        sc.write(echoBuffer);//-----------------Send to the client.
                                        loggedInAccounts.get(theKey).sendBack[o] = "";//Since we wrote this string to the client, reset it to "".
                                    }
                                    o++;
                                }
                            }
                        }
                        break;
                    }

                    //----------------------------------------------------------Create a new clean buffer for the next go arround.
                    echoBuffer = ByteBuffer.allocate(1024);

                    //----------------------------------------------------------Everything has been sent to the client, so now we register this socket for reading again.
                    sc.register(selector, SelectionKey.OP_READ);
                    it.remove();
                }
            }
        }
    }

    //============================================================================================================================================================================
    private void writeToAllOnline(Selector selector, String message) throws ClosedChannelException {
        int i = 0;
        Iterator next = loggedInAccounts.keySet().iterator();
        String sendBack = message;
        boolean wroteString = false;
        while (true) {//--------------------------------------------------------Keep going until done.
            while (next.hasNext()) {//------------------------------------------While there are more people logged on.
                int theKey = (Integer) next.next();
                while (!wroteString) {

                    //----------------------------------------------------------As long as we haven't written the string to this account.
                    if (loggedInAccounts.get(theKey).sendBack[i].length() < 1) {//If this slot is open, write to it.
                        loggedInAccounts.get(theKey).sendBack[i] = sendBack;
                        wroteString = true;//-----------------------------------Now that we have written to it. Exit.
                    }
                    i++;//------------------------------------------------------Increase iterator.
                }
                //--------------------------------------------------------------Now that we have written to this account, we need to register it for writing.
                loggedInAccounts.get(theKey).returnSocket().getChannel().register(selector, SelectionKey.OP_WRITE);
                i = 0;//--------------------------------------------------------We exited the above loop, so reset the iterator.
                wroteString = false;//------------------------------------------Reset this too.
            }
            break;//------------------------------------------------------------We have written to all accounts, so exit the loop.
        }
    }

    //===========================================================================================================================================================================
    private void notifyAllInRange(int myKey, Selector selector, String sendBack) throws ClosedChannelException {
        int o = 0;
        Iterator next = loggedInAccounts.keySet().iterator();//-----------------This iterator contains the list of logged in accounts to cycle through.
        boolean wroteString = false;
        while (true) {//--------------------------------------------------------Keep going until done
            while (next.hasNext()) {//------------------------------------------While there are more people logged on
                //--------------------------------------------------------------Prepare variables.
                int theKey = (Integer) next.next();
                int xDiff = loggedInAccounts.get(myKey).returnChar().returnX() - loggedInAccounts.get(theKey).returnChar().returnX();
                int yDiff = loggedInAccounts.get(myKey).returnChar().returnY() - loggedInAccounts.get(theKey).returnChar().returnY();
                int zDiff = loggedInAccounts.get(myKey).returnChar().returnZ() - loggedInAccounts.get(theKey).returnChar().returnZ();

                //--------------------------------------------------------------If the account is within range.
                if ((theKey != myKey) && (xDiff <= 10 || xDiff >= -10) && (yDiff <= 10 || yDiff >= -10) && (zDiff == 0)) {
                    while (!wroteString) {//------------------------------------As long as we haven't written the string to this account
                        if (loggedInAccounts.get(theKey).sendBack[o].length() < 1) {//If this slot is open, write to it.
                            loggedInAccounts.get(theKey).sendBack[o] = sendBack;
                            wroteString = true;//-------------------------------Now that we have written to it. Exit.
                        }
                        o++;//--------------------------------------------------Increase iterator.
                    }
                    //----------------------------------------------------------Now that we have written to this account, we need to register it for writing.
                    loggedInAccounts.get(theKey).returnSocket().getChannel().register(selector, SelectionKey.OP_WRITE);
                }
                o = 0;//--------------------------------------------------------We exited the above loop, so reset the iterator.
                wroteString = false;//------------------------------------------Reset this too.
            }
            break;//------------------------------------------------------------We have written to all accounts, so exit the loop.
        }
    }

    //==============================================================================================================================================================================
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