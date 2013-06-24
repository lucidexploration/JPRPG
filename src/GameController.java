
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JTextArea;

class GameController {

    private Socket sock;//this is the socket connecting us to the server
    private PrintWriter output;//this is the output stream
    private BufferedReader input;//the input stream

    public GameController() throws UnknownHostException, IOException {
        //connects to the server
        //this will need to be updated
        sock = new Socket("localhost", 7171);
        output = new PrintWriter(sock.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    /*
     * Recieves input from the server and parses it to know what to update.
     */
    public void recieveInput(JTextArea j) throws IOException {
        if (input.ready()) {
            String message = input.readLine();
            String[] splits = message.split(",");
            System.out.println("recieved : " + message);
            if (splits[0].equals("chat")) {
                recieveChat(j, splits[1], splits[2]);
            }
        }
    }

    public void createAccount(int accNumber, String password, String username) {
        output.println("create" + "," + accNumber + "," + password + "," + username + ",");
    }

    /*
     * Sends packet to server that says that attackerID is physically
     * attacking targetID.
     */
    public void attack(int attackerID, int targetID) {
        output.println("attack" + "," + attackerID + "," + targetID + ",");
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
    public void chat(String name, String text) {
        output.println("chat," + name + "," + text + ",");
        System.out.println("sent : " + "chat," + name + "," + text + ",\n");
    }

    /*
     * Updates chat window.
     */
    public void recieveChat(JTextArea j, String charName, String text) {
        j.append("\n" + charName + ":" + text);
    }

    /*
     * Moves moverID to target location, then sends packet to the server
     * for the server to verify the movement.
     */
    public void movement(int moverID, int xPos, int yPos) {
        output.println("move" + "," + moverID + "," + xPos + "," + yPos + ",");
    }

    public void recieveMovement(int moverID, int xPos, int yPos) {
    }

    /*
     * Sends acc# and pw to server for the server to verify.
     */
    public void logIn(int accNumber, String password) {
        output.println("login" + "," + accNumber + "," + password + ",");
    }
}