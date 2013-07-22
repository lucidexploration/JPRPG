package server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.UUID;

public class ServerPacketManager {

    public ServerPacketManager() {
    }

    public static void interpretPacket(String packet, SocketChannel sc, Selector selector) throws IOException {
        String[] splits = packet.split("=--=");
        //create account
        if (splits[0].equals("create")) {
            ServerGUI.console.append(Server.accounts.keySet().toString() + "\n");
            Server.echoBuffer = ByteBuffer.allocate(1024);
        }

        //----------------------------------------------------------------------LOGIN
        if (splits[0].equals("login")) {//--------------------------------------If this is a login packet
            ServerGUI.console.append(Server.accounts.keySet().toString() + "\n");

            //------------------------------------------------------------------And if we have this account
            if (Server.accounts.containsKey(Integer.parseInt(splits[1]))) {

                //--------------------------------------------------------------And if the password matches
                if (Server.accounts.get(Integer.parseInt(splits[1])).returnPassword().equals(splits[2])) {

                    //----------------------------------------------------------Prepare to send the login information
                    int x = Server.accounts.get(Integer.parseInt(splits[1])).returnChar().returnX();
                    int y = Server.accounts.get(Integer.parseInt(splits[1])).returnChar().returnY();
                    String name = Server.accounts.get(Integer.parseInt(splits[1])).returnChar().returnName();
                    int hp = Server.accounts.get(Integer.parseInt(splits[1])).returnChar().returnHP();
                    int totalhp = Server.accounts.get(Integer.parseInt(splits[1])).returnChar().returnTotalHP();
                    int mana = Server.accounts.get(Integer.parseInt(splits[1])).returnChar().returnMana();
                    int totalmana = Server.accounts.get(Integer.parseInt(splits[1])).returnChar().returnTotalMana();

                    //----------------------------------------------------------DO IDENTIFICATION WORK
                    //----------------------------------------------------------Assign accounts socket for later writing.
                    Server.accounts.get(Integer.parseInt(splits[1])).setSocket(sc.socket());
                    //----------------------------------------------------------Assign accounts socketAddress for identifying for writing.
                    Server.accounts.get(Integer.parseInt(splits[1])).setAddress(sc.getRemoteAddress());
                    //----------------------------------------------------------Add this account to the logged on accounts map.
                    Server.loggedInAccounts.put((Integer) Integer.parseInt(splits[1]), Server.accounts.get(Integer.parseInt(splits[1])));
                    //----------------------------------------------------------Now add it all to the sendBack string.
                    String sendBack = "login=--=" + name + "=--=" + x + "=--=" + y + "=--=" + hp + "=--=" + totalhp + "=--=" + mana + "=--=" + totalmana + "=--=\n";

                    //-----------------------------------------------------------Write the string to the accounts sendBack[]
                    int b = 0;
                    while (true) {
                        if (Server.accounts.get(Integer.parseInt(splits[1])).sendBack[b].isEmpty()) {
                            Server.accounts.get(Integer.parseInt(splits[1])).sendBack[b] = sendBack;
                            break;
                        }
                        b++;
                        break;
                    }
                    ServerGUI.console.append("login added: " + Server.loggedInAccounts.keySet() + "\n");
                    ServerGUI.onlineList.append(name + "\n");
                }
            }
            //------------------------------------------------------------------Now tell everyone arround us that we have logged in.
            int myKey = returnOnlineKey(sc.getRemoteAddress());
            String monsterName = Server.loggedInAccounts.get(myKey).returnChar().returnName();
            int monsterX = Server.loggedInAccounts.get(myKey).returnChar().returnX();
            int monsterY = Server.loggedInAccounts.get(myKey).returnChar().returnY();
            int monsterHP = Server.loggedInAccounts.get(myKey).returnChar().returnHP();
            int monsterTotalHP = Server.loggedInAccounts.get(myKey).returnChar().returnTotalHP();
            int monsterMP = Server.loggedInAccounts.get(myKey).returnChar().returnMana();
            int monsterTotalMP = Server.loggedInAccounts.get(myKey).returnChar().returnTotalMana();
            String sendBack = "monsterInRange=--=" + monsterName + "=--=" + monsterX + "=--=" + monsterY + "=--=" + monsterHP + "=--=" + monsterTotalHP + "=--=" + monsterMP + "=--=" + monsterTotalMP + "=--=+\n";

            notifyAllInRange(myKey, selector, sendBack, 2);
            sendTilesArround(myKey,0);
            sendObjectsArround(myKey);
        }

        //----------------------------------------------------------------------ATTACK
        if (splits[0].equals("attack")) {
            //do attack shit
            //sc.register(selector, SelectionKey.OP_WRITE);
        }

        //----------------------------------------------------------------------CHAT
        if (splits[0].equals("chat")) {
            String name = splits[1];
            String text = splits[2];

            //------------------------------------------------------------------Add everything to sendBack string.
            String sendBack = "chat=--=" + name + "=--=" + text + "=--=" + "\n";
            writeToAllOnline(selector, sendBack);
        }

        //----------------------------------------------------------------------MOVEMENT
        if (splits[0].equals("move")) {
            String direction = splits[1];
            //------------------------------------------------------------------Setup Variables
            int myKey = returnOnlineKey(sc.getRemoteAddress());
            String monsterName = Server.loggedInAccounts.get(myKey).returnChar().returnName();
            int monsterX;
            int monsterY;
            int monsterHP = Server.loggedInAccounts.get(myKey).returnChar().returnHP();
            int monsterTotalHP = Server.loggedInAccounts.get(myKey).returnChar().returnTotalHP();
            int monsterMP = Server.loggedInAccounts.get(myKey).returnChar().returnMana();
            int monsterTotalMP = Server.loggedInAccounts.get(myKey).returnChar().returnTotalMana();
            String sendBack;

            switch (direction) {
                case "left":
                    //----------------------------------------------------------Update this characters position.
                    Server.loggedInAccounts.get(myKey).returnChar().decX();
                    //----------------------------------------------------------Prepare message for write to other players.
                    monsterX = Server.loggedInAccounts.get(myKey).returnChar().returnX();
                    monsterY = Server.loggedInAccounts.get(myKey).returnChar().returnY();
                    sendBack = "monsterInRange=--=" + monsterName + "=--=" + monsterX + "=--=" + monsterY + "=--=" + monsterHP + "=--=" + monsterTotalHP + "=--=" + monsterMP + "=--=" + monsterTotalMP + "=--=+\n";
                    sendTilesArround(myKey,1);
                    sendObjectsArround(myKey);
                    notifyAllInRange(myKey, selector, sendBack, 0);
                    break;
                case "right":
                    //----------------------------------------------------------Update this characters position.
                    Server.loggedInAccounts.get(myKey).returnChar().incX();
                    //----------------------------------------------------------Prepare message for write to other players.
                    monsterX = Server.loggedInAccounts.get(myKey).returnChar().returnX();
                    monsterY = Server.loggedInAccounts.get(myKey).returnChar().returnY();
                    sendBack = "monsterInRange=--=" + monsterName + "=--=" + monsterX + "=--=" + monsterY + "=--=" + monsterHP + "=--=" + monsterTotalHP + "=--=" + monsterMP + "=--=" + monsterTotalMP + "=--=+\n";
                    sendTilesArround(myKey,2);
                    sendObjectsArround(myKey);
                    notifyAllInRange(myKey, selector, sendBack, 0);
                    break;
                case "up":
                    //----------------------------------------------------------Update this characters position.
                    Server.loggedInAccounts.get(myKey).returnChar().decY();
                    monsterX = Server.loggedInAccounts.get(myKey).returnChar().returnX();
                    monsterY = Server.loggedInAccounts.get(myKey).returnChar().returnY();
                    //----------------------------------------------------------Prepare message for write to other players.
                    sendBack = "monsterInRange=--=" + monsterName + "=--=" + monsterX + "=--=" + monsterY + "=--=" + monsterHP + "=--=" + monsterTotalHP + "=--=" + monsterMP + "=--=" + monsterTotalMP + "=--=+\n";
                    sendTilesArround(myKey,3);
                    sendObjectsArround(myKey);
                    notifyAllInRange(myKey, selector, sendBack, 0);
                    break;
                case "down":
                    //-----------------------------------------------------------Update this characters position.
                    Server.loggedInAccounts.get(myKey).returnChar().incY();
                    monsterX = Server.loggedInAccounts.get(myKey).returnChar().returnX();
                    monsterY = Server.loggedInAccounts.get(myKey).returnChar().returnY();
                    //----------------------------------------------------------Prepare message for write to other players.
                    sendBack = "monsterInRange=--=" + monsterName + "=--=" + monsterX + "=--=" + monsterY + "=--=" + monsterHP + "=--=" + monsterTotalHP + "=--=" + monsterMP + "=--=" + monsterTotalMP + "=--=+\n";
                    sendTilesArround(myKey,4);
                    sendObjectsArround(myKey);
                    notifyAllInRange(myKey, selector, sendBack, 0);
                    break;
                default:
                    break;
            }
        }
    }

    //===========================================================================================================================================================================
    public static void notifyAllInRange(int myKey, Selector selector, String sendBack, int bitSwitch) throws ClosedChannelException {
        int o = 0;
        Iterator keys = Server.loggedInAccounts.keySet().iterator();//----------This iterator contains the list of logged in accounts to cycle through.
        boolean wroteString = false;//------------------------------------------This boolean lets us know when we have written to the current clients sendback[]

        while (keys.hasNext()) {//----------------------------------------------While there are more people logged on
            //------------------------------------------------------------------Prepare variables.
            int currentKey = (Integer) keys.next();
            int xDiff = Server.loggedInAccounts.get(myKey).returnChar().returnX() - Server.loggedInAccounts.get(currentKey).returnChar().returnX();
            int yDiff = Server.loggedInAccounts.get(myKey).returnChar().returnY() - Server.loggedInAccounts.get(currentKey).returnChar().returnY();
            int zDiff = Server.loggedInAccounts.get(myKey).returnChar().returnZ() - Server.loggedInAccounts.get(currentKey).returnChar().returnZ();


            //------------------------------------------------------------------If the account is within range.
            if ((xDiff <= 5 || xDiff >= -4) && (yDiff <= 5 || yDiff >= -4) && (zDiff == 0)) {

                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //--------------------------------------------------------------If we are sending to just others, or, both parties.
                if (bitSwitch == 0 || bitSwitch == 2) {
                    while (!wroteString) {//------------------------------------As long as we haven't written the string to this account
                        if (Server.loggedInAccounts.get(currentKey).sendBack[o].isEmpty()) {//If this slot is open, write to it.
                            Server.loggedInAccounts.get(currentKey).sendBack[o] = sendBack;
                            wroteString = true;//-------------------------------Now that we have written to it. Exit.
                        }
                        o++;//--------------------------------------------------Increase iterator.
                    }
                    //----------------------------------------------------------Now that we have written to this account, we need to register it for writing.
                    if (currentKey != myKey) {
                        Server.loggedInAccounts.get(currentKey).returnSocket().getChannel().register(selector, SelectionKey.OP_WRITE);
                    }
                }
                o = 0;//--------------------------------------------------------We exited the above loop, so reset the iterator.
                wroteString = false;//------------------------------------------Reset this too.

                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //--------------------------------------------------------------If we are sending to just ourself, or both parties.
                if (bitSwitch == 1 || bitSwitch == 2) {
                    String monsterName = Server.loggedInAccounts.get(currentKey).returnChar().returnName();
                    int monsterX = Server.loggedInAccounts.get(currentKey).returnChar().returnX();
                    int monsterY = Server.loggedInAccounts.get(currentKey).returnChar().returnY();
                    int monsterHP = Server.loggedInAccounts.get(currentKey).returnChar().returnHP();
                    int monsterTotalHP = Server.loggedInAccounts.get(currentKey).returnChar().returnTotalHP();
                    int monsterMP = Server.loggedInAccounts.get(currentKey).returnChar().returnMana();
                    int monsterTotalMP = Server.loggedInAccounts.get(currentKey).returnChar().returnTotalMana();

                    String sendBackToMe = "monsterInRange=--=" + monsterName + "=--=" + monsterX + "=--=" + monsterY + "=--=" + monsterHP + "=--=" + monsterTotalHP + "=--=" + monsterMP + "=--=" + monsterTotalMP + "=--=+\n";

                    while (!wroteString) {//------------------------------------As long as we haven't written the string to this account

                        //------------------------------------------------------If this slot is open, write to it.
                        if (Server.loggedInAccounts.get(myKey).sendBack[o].isEmpty()) {
                            Server.loggedInAccounts.get(myKey).sendBack[o] = sendBackToMe;
                            wroteString = true;//-------------------------------Now that we have written to it. Exit.
                        }
                        o++;//--------------------------------------------------Increase iterator.
                    }
                }
                o = 0;//--------------------------------------------------------We exited the above loop, so reset the iterator.
                wroteString = false;//------------------------------------------Reset this too.
            }
        }

        //----------------------------------------------------------------------Here at the end, if we had to send to current client, we need to register him for writing.
        if (bitSwitch == 1 || bitSwitch == 2) {
            Server.loggedInAccounts.get(myKey).returnSocket().getChannel().register(selector, SelectionKey.OP_WRITE);
        }
    }

    //===========================================================================================================================================================================
    //--------------------------------------------------------------------------Sends map tiles arround player to player
    //--------------------------------------------------------------------------For flag : 
    //--------------------------------------------------------------------------    0==all tiles
    //--------------------------------------------------------------------------    1==left column
    //--------------------------------------------------------------------------    2==right column
    //--------------------------------------------------------------------------    3==top row
    //--------------------------------------------------------------------------    4==bottom row
    private static void sendTilesArround(int playersKey, int flag) {
        int playerX = Server.loggedInAccounts.get(playersKey).returnChar().returnX();
        int playerY = Server.loggedInAccounts.get(playersKey).returnChar().returnY();
        int zPos = Server.loggedInAccounts.get(playersKey).returnChar().returnZ();
        int xRange = playerX - 6;
        int yRange = playerY - 6;
        boolean wroteString = false;
        int o = 0;
        //----------------------------------------------------------------------Check all tiles in range for objects.
        while (xRange < playerX + 4) {
            while (yRange < playerY + 4) {
                //--------------------------------------------------------------See if there is an object at this position.
                if (Server.map.containsKey(getIndex(xRange, yRange, zPos))) {
                        //----------------------------------------------------------If so, send this object to the client.
                        int tileHere = Server.map.get(getIndex(xRange, yRange, zPos)).returnType();
                        String sendBackToMe = "tileAt=--=" + tileHere + "=--=" + xRange + "=--=" + yRange + "=--=" + zPos + "=--=" + "\n";
                        Server.console.append(sendBackToMe + ".      Sent to : " + Server.accounts.get(playersKey).returnChar().returnName() + "\n");

                        //----------------------------------------------------------As long as we haven't written the string to this account
                        while (!wroteString) {

                            //------------------------------------------------------If this slot is open, write to it.
                            if (Server.loggedInAccounts.get(playersKey).sendBack[o].isEmpty()) {
                                Server.loggedInAccounts.get(playersKey).sendBack[o] = sendBackToMe;
                                wroteString = true;//-------------------------------Now that we have written to it. Exit.
                            }
                            o++;//--------------------------------------------------Increase iterator.
                        }
                        wroteString = false;
                    
                }
                yRange++;
            }
            yRange = playerY - 4;
            xRange++;
        }
    }
    
    
    private static void sendObjectsArround(int playersKey) {
        int playerX = Server.loggedInAccounts.get(playersKey).returnChar().returnX();
        int playerY = Server.loggedInAccounts.get(playersKey).returnChar().returnY();
        int zPos = Server.loggedInAccounts.get(playersKey).returnChar().returnZ();
        int xRange = playerX - 6;
        int yRange = playerY - 6;
        boolean wroteString = false;
        int o = 0;
        //----------------------------------------------------------------------Check all tiles in range for objects.
        while (xRange < playerX + 4) {
            while (yRange < playerY + 4) {
                //--------------------------------------------------------------See if there is an object at this position.
                if (Server.map.containsKey(getIndex(xRange, yRange, zPos))) {
                    if (Server.map.get(getIndex(xRange, yRange, zPos)).returnObject() != 0) {
                        //----------------------------------------------------------If so, send this object to the client.
                        int objectHere = Server.map.get(getIndex(xRange, yRange, zPos)).returnObject();
                        String sendBackToMe = "objectInRange=--=" + objectHere + "=--=" + xRange + "=--=" + yRange + "=--=" + zPos + "=--=" + "\n";
                        Server.console.append(sendBackToMe + ".      Sent to : " + Server.accounts.get(playersKey).returnChar().returnName() + "\n");

                        //----------------------------------------------------------As long as we haven't written the string to this account
                        while (!wroteString) {

                            //------------------------------------------------------If this slot is open, write to it.
                            if (Server.loggedInAccounts.get(playersKey).sendBack[o].isEmpty()) {
                                Server.loggedInAccounts.get(playersKey).sendBack[o] = sendBackToMe;
                                wroteString = true;//-------------------------------Now that we have written to it. Exit.
                            }
                            o++;//--------------------------------------------------Increase iterator.
                        }
                        wroteString = false;
                    }
                }
                yRange++;
            }
            yRange = playerY - 4;
            xRange++;
        }
    }

    //============================================================================================================================================================================
    //--------------------------------------------------------------------------Primarily used for chat and system messages.
    //--------------------------------------------------------------------------Sends the message to everyone online.
    public static void writeToAllOnline(Selector selector, String message) throws ClosedChannelException {
        int i = 0;
        Iterator next = Server.loggedInAccounts.keySet().iterator();
        String sendBack = message;
        boolean wroteString = false;
        while (true) {//--------------------------------------------------------Keep going until done.
            while (next.hasNext()) {//------------------------------------------While there are more people logged on.
                int theKey = (Integer) next.next();
                while (!wroteString) {

                    //----------------------------------------------------------As long as we haven't written the string to this account.
                    if (Server.loggedInAccounts.get(theKey).sendBack[i].isEmpty()) {//If this slot is open, write to it.
                        Server.loggedInAccounts.get(theKey).sendBack[i] = sendBack;
                        wroteString = true;//-----------------------------------Now that we have written to it. Exit.
                    }
                    i++;//------------------------------------------------------Increase iterator.
                }
                //--------------------------------------------------------------Now that we have written to this account, we need to register it for writing.
                Server.loggedInAccounts.get(theKey).returnSocket().getChannel().register(selector, SelectionKey.OP_WRITE);
                i = 0;//--------------------------------------------------------We exited the above loop, so reset the iterator.
                wroteString = false;//------------------------------------------Reset this too.
            }
            break;//------------------------------------------------------------We have written to all accounts, so exit the loop.
        }
    }

    //======================================================================================================================================================================
    //--------------------------------------------------------------------------Returns the id of the provided socketAddress for accountsLoggedIn.
    public static int returnOnlineKey(SocketAddress address) {
        Iterator iterator = Server.loggedInAccounts.keySet().iterator();
        int o = 0;
        int theKey = 0;
        while (iterator.hasNext()) {//------------------------------------------If there is more in the list, continue.
            int currentKey = (Integer) iterator.next();
            if (Server.loggedInAccounts.get(currentKey).returnAddress() == address) {//If this socket's address is equal to an address in the list, keep going.
                theKey = currentKey;
                break;
            }
        }
        return theKey;
    }

    //====================================================================================================================================================================
    //--------------------------------------------------------------------------Sends clients entire sendBack[].
    public static void sendToClient(SocketChannel sc) throws IOException {
        int o = 0;
        int theKey = (int) returnOnlineKey(sc.getRemoteAddress());
        boolean done = false;

        while (!done) {
            if (Server.loggedInAccounts.get(theKey).sendBack[o].isEmpty()) {
                break;
            }
            String message = Server.loggedInAccounts.get(theKey).sendBack[o];
            ServerGUI.console.append("Sent : " + message + "      To : " + Server.loggedInAccounts.get(theKey).returnChar().returnName() + "\n");
            Server.echoBuffer = ByteBuffer.allocate(1024);
            Server.echoBuffer.put(message.getBytes());
            Server.echoBuffer.flip();
            sc.write(Server.echoBuffer);
            if (o >= Server.loggedInAccounts.get(theKey).sendBack.length) {
                done = true;
            }
            o++;
        }
    }

    //====================================================================================================================================================================
    //--------------------------------------------------------------------------Clear clients entire sendBack[].
    public static void eraseSendback(SocketChannel sc) throws IOException {
        int o = 0;
        int theKey = (int) returnOnlineKey(sc.getRemoteAddress());
        int stopHere = Server.loggedInAccounts.get(theKey).sendBack.length;

        while (o < stopHere) {
            Server.loggedInAccounts.get(theKey).sendBack[o] = "";
            o++;
        }
    }

    //==================================================================================================================================================================================
    //--------------------------------------------------------------------------Returns the map index of specified X,Y,Z.
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
}
