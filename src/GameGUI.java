
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 *
 */
class GameGUI {

    //the framework
    private static JLabel worldDisplay;
    private static JTextArea chatBox;
    private static JFrame window;
    private static BorderLayout bl;
    //controls input
    private static final Set<Character> keysPressed = new HashSet<>();
    public static Timer timer = new Timer();
    public static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            checkForInput();
            worldDisplay.repaint();
        }
    };

    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                makeGUI();
            }
        });
    }

    public static void makeGUI() {
        window = new JFrame("JPRPG");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //
        bl = new BorderLayout();
        window.setLayout(bl);
        //
        addWorldDisplay(window.getContentPane());
        addChatBox(window.getContentPane());
        //
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed.add(e.getKeyChar());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed.remove(e.getKeyChar());
            }
        });
        //
        window.pack();
        window.setVisible(true);
        worldDisplay.setVisible(true);
        chatBox.setVisible(true);
    }

    public static void addWorldDisplay(Container pane) {
        worldDisplay = new JLabel() {
            @Override
            public void paint(Graphics g) {
                //draw the boundary of the worldDisplay
                g.setColor(Color.red);
                g.drawRect(worldDisplay.getBounds().x,
                        worldDisplay.getBounds().y,
                        worldDisplay.getBounds().height,
                        worldDisplay.getBounds().width);
                //draw the boundary of the chatBox
                g.setColor(Color.BLUE);
                g.drawRect(chatBox.getBounds().x,
                        chatBox.getBounds().y,
                        chatBox.getBounds().height,
                        chatBox.getBounds().width);
            }
        };

        //set sizes
        worldDisplay.setPreferredSize(new Dimension(1000, 960));
        worldDisplay.setMinimumSize(new Dimension(1000, 960));
        worldDisplay.setMaximumSize(new Dimension(1000, 960));
        window.getContentPane().add(worldDisplay,bl.WEST);

        //start the time to check for input
        timer.schedule(task, 0, 50);
    }

    public static void addChatBox(Container pane) {
        chatBox = new JTextArea("Why won't this display?");
        window.getContentPane().add(chatBox,bl.EAST);
        //set sizes
        chatBox.setMinimumSize(new Dimension(440, 960));
        chatBox.setMaximumSize(new Dimension(440, 960));
        chatBox.setPreferredSize(new Dimension(440, 960));
        System.out.println(chatBox.toString());
    }

    private static void checkForInput() {
        if (keysPressed.contains('w')) {
            //add action here
        }
        //right movement
        if (keysPressed.contains('s')) {
            //add action here
        }
        //left movement
        if (keysPressed.contains('a')) {
            //add action here
        }
        //down movement
        if (keysPressed.contains('d')) {
            //add action here
        }
    }
}
