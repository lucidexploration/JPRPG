
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JLabel;
/**
 *
 * @author Anarmat
 */
class GameGUI {
    
    //the framework
    private static JLabel worldDisplay;
    private static JFrame window;
    //controls input
    private static final Set<Character> keysPressed = new HashSet<>();
    public static Timer timer = new Timer();
    public static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            finalLoop();
            worldDisplay.repaint();
        }
    };

    

    //empty command required to compile
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
        addToPane(window.getContentPane());
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
        window.pack();
        window.setVisible(true);
        window.setPreferredSize(new Dimension(1440, 960));
    }

    public static void addToPane(Container pane) {
        worldDisplay = new JLabel() {
            @Override
            public void paint(Graphics g) {
                //draw the paddels, score, and boundaries
                
                
                
                //
            }
        };
        
        //set size to 800x600
        //label.setPreferredSize(new Dimension(800, 600));
        window.getContentPane().add(worldDisplay);
        
        //start the time to check for input
        timer.schedule(task, 0, 50);
    }
    
    private static void finalLoop() {
        if (keysPressed.contains('w')) {
            //add space button here

        }
        //right movement
        if (keysPressed.contains('s')) {
            //add down button here

        }
        //left movement
        if (keysPressed.contains('a')) {
            //add left button here
        }
        //down movement
        if (keysPressed.contains('d')) {
            //add right button here
        }
    }
}
