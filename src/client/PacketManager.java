package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

class PacketManager extends GameGUI {

    private Socket sock;//------------------------------------------------------this is the socket connecting us to the server
    private PrintWriter output;//-----------------------------------------------this is the output stream
    private BufferedReader input;//---------------------------------------------the input stream

    public PacketManager() throws UnknownHostException, IOException {
        //----------------------------------------------------------------------connects to the server
        //----------------------------------------------------------------------this will need to be updated with the servers ip.
        sock = new Socket("localhost", 7171);
        output = new PrintWriter(sock.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    /*
     * -------------------------------------------------------------------------Recieves input from the server and parses it to know what to update.
     */
    public void recieveInput() throws IOException {
        if (input.ready()) {
            String message = input.readLine();
            String[] splits = message.split("=--=");
            System.out.println("recieved : " + message);
            if (splits[0].equals("chat")) {
                String name = splits[1];
                String words = splits[2];
                recieveChat(name, words);
            }
            if (splits[0].equals("login")) {
                String name = splits[1];
                int x = Integer.parseInt(splits[2]);
                int y = Integer.parseInt(splits[3]);
                int hp = Integer.parseInt(splits[4]);
                int totalhp = Integer.parseInt(splits[5]);
                int mana = Integer.parseInt(splits[6]);
                int totalmana = Integer.parseInt(splits[7]);
                recieveLogIn(name, x, y, hp, totalhp, mana, totalmana);
            }

            //------------------------------------------------------------------We recieved a monster packet.
            if (splits[0].equals("monsterInRange")) {
                //--------------------------------------------------------------Setup the variables.
                String monsterName = splits[1];
                int monsterX = Integer.parseInt(splits[2]);
                int monsterY = Integer.parseInt(splits[3]);
                int monsterHP = Integer.parseInt(splits[4]);
                int monsterTotalHP = Integer.parseInt(splits[5]);
                int monsterMP = Integer.parseInt(splits[6]);
                int monsterTotalMP = Integer.parseInt(splits[7]);
                //--------------------------------------------------------------Now deal with it.
                updateMonster(monsterName, monsterX, monsterY, monsterHP, monsterTotalHP, monsterMP, monsterTotalMP);
            }
            if (splits[0].equals("objectInRange")) {
                //--------------------------------------------------------------Setup the variables.
                String objectType = splits[1];
                int objectX = Integer.parseInt(splits[2]);
                int objectY = Integer.parseInt(splits[3]);
                int objectZ = Integer.parseInt(splits[4]);
                //--------------------------------------------------------------Now deal with it.
                updateObject(objectType, objectX, objectY, objectZ);
            }
        }
    }
    private void updateObject(String objectType, int objectX, int objectY, int objectZ) {
        String objectID;
        objectID = ""+objectX+","+objectY+","+objectZ;
        if (!GameGUI.gameClient.map.containsKey(objectID)) {//-------If we don't have it, add it to the map.
            GameGUI.gameClient.map.put(objectID, new Tile(0));
            GameGUI.gameClient.map.get(objectID).setObjectID(Integer.valueOf(objectType));
            
        } else {//---------------------------------------------------------------If we already have it, just update it's variables.
            GameGUI.gameClient.map.get(objectID).setObjectID(Integer.valueOf(objectType));
        }
    }

    private void updateMonster(String monsterName, int monsterX, int monsterY, int hp, int hptotal, int mp, int mptotal) {
        if (monsterName.equals(GameGUI.playerCon.returnName())) {
            GameGUI.playerCon.setPos(monsterX, monsterY);
            return;
        }
        if (!GameGUI.gameClient.returnNPCMap().containsKey(monsterName)) {//-------If we don't have it, add it to the map.
            GameGUI.gameClient.returnNPCMap().put(monsterName, new Monster(monsterName, monsterX, monsterY, hp, hptotal, mp, mptotal));
        } else {//---------------------------------------------------------------If we already have it, just update it's variables.
            GameGUI.gameClient.returnNPCMap().get(monsterName).setX(monsterX);
            GameGUI.gameClient.returnNPCMap().get(monsterName).setY(monsterY);
            GameGUI.gameClient.returnNPCMap().get(monsterName).setHP(hp);
            GameGUI.gameClient.returnNPCMap().get(monsterName).setTotalHP(hptotal);
            GameGUI.gameClient.returnNPCMap().get(monsterName).setMana(mp);
            GameGUI.gameClient.returnNPCMap().get(monsterName).setTotalMana(mptotal);
        }
    }

    public void createAccount(int accNumber, String password, String username) {
        output.println("create=--=" + accNumber + "=--=" + password + "=--=" + username + "=--=");
    }

    /*
     * Sends packet to server that says that attackerID is physically
     * attacking targetID.
     */
    public void sendAttack(int attackerID, int targetID) {
        output.println("attack=--=" + attackerID + "=--=" + targetID + "=--=");
    }

    /*
     * Updates player/monster informations.
     */
    public void recieveAttack(int targetID, int damage) {
    }

    /*
     * Sends packet to the server containing whatever was typed in to the
     * chat box when the return key was pressed.
     */
    public void sendChat(String name, String text) {
        output.println("chat=--=" + name + "=--=" + text + "=--=");
        System.out.println("sent : " + "chat=--=" + name + "=--=" + text + "=--=");
    }

    /*
     * Updates chat window.
     */
    public void recieveChat(String charName, String text) {
        GameGUI.chatBox.append("\n" + charName + ":" + text);
    }

    /*
     * Moves moverID to target location, then sends packet to the server
     * for the server to verify the movement.
     */
    public void sendMoveLeft() {
        output.println("move=--=left=--=");
    }

    public void sendMoveRight() {
        output.println("move=--=right=--=");
    }

    public void sendMoveUp() {
        output.println("move=--=up=--=");
    }

    public void sendMoveDown() {
        output.println("move=--=down=--=");
    }

    public void recieveMovement(int moverID, int xPos, int yPos) {
    }

    /*
     * Sends acc# and pw to server for the server to verify.
     */
    public void logIn(int accNumber, String password) {
        System.out.println("sent : " + accNumber + " " + password);
        output.println("login=--=" + accNumber + "=--=" + password + "=--=" + "\n");
    }

    public void recieveLogIn(String charName, int x, int y, int hp, int totalhp, int mana, int totalmana) {
        //----------------------------------------------------------------------Update Character Information on Client
        GameGUI.playerCon.setName(charName);
        GameGUI.playerCon.setPos(x, y);
        GameGUI.playerCon.setHP(hp);
        GameGUI.playerCon.setTotalHP(totalhp);
        GameGUI.playerCon.setMana(mana);
        GameGUI.playerCon.setTotalMana(totalmana);
        //----------------------------------------------------------------------Since we are logged in, we no longer need a few client components, so let's hide them.
        GameGUI.loginBox.setVisible(false);
        GameGUI.passwordBox.setVisible(false);
        GameGUI.createAccount.setVisible(false);
        GameGUI.loginButton.setVisible(false);
        GameGUI.chatboxInput.setVisible(true);
        //----------------------------------------------------------------------And show input for another.
        GameGUI.statusPanelLeft.setVisible(true);
        //----------------------------------------------------------------------Allow input in gameWindow
        window.setFocusable(true);
        window.requestFocus();
    }
}