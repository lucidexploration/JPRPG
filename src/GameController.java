
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Anarmat
 */
class GameController {

    private Socket sock;//this is the socket connecting us to the server
    private PrintWriter output;//this is the output stream
    private BufferedReader input;//the input stream

    public GameController() throws UnknownHostException, IOException {
        //connects to the server
        //this will need to be updated
        this.sock = new Socket("132.123.12.123", 7171);
        this.output = new PrintWriter(this.sock.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    /*
     * Recieves input from the server and parses it to know what to update.
     */
    public void recieveInput() throws IOException {
        String in = input.readLine();
    }

    /*
     * Sends packet to server that says that attackerID is physically
     * attacking targetID.
     */
    public void attack(int attackerID, int targetID) {
        output.println("++ATTACK++" + " " + attackerID + " " + targetID);
        output.flush();
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
    public void chat(String text) {
        output.println("++CHAT++" + " " + text);
        output.flush();
    }

    /*
     * Updates chat window.
     */
    public void recieveChat(String text) {
    }

    /*
     * Moves moverID to target location, then sends packet to the server
     * for the server to verify the movement.
     */
    public void movement(int moverID, int xPos, int yPos) {
        output.println("++MOVE++" + " " + moverID + " " + xPos + " " + yPos);
        output.flush();
    }

    public void recieveMovement(int moverID, int xPos, int yPos) {
    }

    /*
     * Sends acc# and pw to server for the server to verify.
     */
    public void logIn(int accNumber, String password) {
        output.println("++LOGIN++" + accNumber + " " + password);
        output.flush();
    }
}