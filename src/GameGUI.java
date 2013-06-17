
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 *
 */
class GameGUI {

    //the framework
    private static JFrame window;
    private static JLabel worldDisplay;
    private static JTextArea chatBox;
    //login boxes/buttons
    private static JTextField loginBox;
    private static JTextField passwordBox;
    private static JButton loginButton;
    //layout manager
    private static GridBagLayout gbl;
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
        gbl = new GridBagLayout();
        window.setLayout(gbl);
        //
        addWorldDisplay(window.getContentPane());
        addChatBox(window.getContentPane());
        addLoginBoxes(window.getContentPane());
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
        worldDisplay.setPreferredSize(new Dimension(1000, 940));
        worldDisplay.setMinimumSize(new Dimension(1000, 940));
        worldDisplay.setMaximumSize(new Dimension(1000, 940));
        //add worldDisplay with proper constraints
        GridBagConstraints wc = new GridBagConstraints();
        wc.gridx = 0;
        wc.gridy = 0;
        wc.gridwidth = 2;
        wc.fill = wc.BOTH;
        window.getContentPane().add(worldDisplay, wc);

        //start the time to check for input
        timer.schedule(task, 0, 50);
    }

    public static void addChatBox(Container pane) {
        chatBox = new JTextArea("Why won't this display?");
        //add chatbox with constraints
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 2;
        cc.gridy = 0;
        cc.gridheight = 2;
        cc.fill = cc.BOTH;
        window.getContentPane().add(chatBox, cc);
        //set sizes
        chatBox.setMinimumSize(new Dimension(440, 940));
        chatBox.setMaximumSize(new Dimension(440, 940));
        chatBox.setPreferredSize(new Dimension(440, 940));
    }

    private static void addLoginBoxes(Container contentPane) {
        GridBagConstraints lc = new GridBagConstraints();
        loginBox = new JTextField("insert acc number here");
        loginBox.setMinimumSize(new Dimension(100, 20));
        passwordBox = new JTextField("inser password here");
        passwordBox.setMinimumSize(new Dimension(100, 20));
        loginButton = new JButton("Login");
        //add buttons and boxes with constraints
        GridBagConstraints ltc = new GridBagConstraints();
        ltc.gridx = 0;
        ltc.gridy = 2;
        window.getContentPane().add(loginBox, ltc);
        GridBagConstraints pbc = new GridBagConstraints();
        pbc.gridx = 1;
        pbc.gridy = 2;
        window.getContentPane().add(passwordBox, pbc);
        GridBagConstraints lbc = new GridBagConstraints();
        lbc.gridx = 2;
        lbc.gridy = 2;
        window.getContentPane().add(loginButton, lbc);
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
