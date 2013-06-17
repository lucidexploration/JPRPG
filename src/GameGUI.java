
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
    private static JTextArea statusDisplay;
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
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //
        gbl = new GridBagLayout();
        window.setLayout(gbl);
        //
        addWorldDisplay(window.getContentPane());
        addStatusDisplay(window.getContentPane());
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
    
    private static void addStatusDisplay(Container contentPane) {
        int health = 0;
        int mana = 0;
        statusDisplay = new JTextArea("Current Health : "+health+"\n"
                +"Current Mana : "+mana);
        GridBagConstraints sdc = new GridBagConstraints();
        sdc.gridx=2;
        sdc.gridy=0;
        sdc.anchor=sdc.NORTH;
        window.getContentPane().add(statusDisplay,sdc);
        statusDisplay.setMaximumSize(new Dimension(400,200));
        statusDisplay.setPreferredSize(new Dimension(400,200));
        statusDisplay.setEditable(false);
    }

    public static void addChatBox(Container pane) {
        chatBox = new JTextArea("This is the chatbox");
        chatBox.setEditable(false);
        //add chatbox with constraints
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 2;
        cc.gridy = 0;
        cc.anchor=cc.EAST;
        //cc.gridheight = 1;
        window.getContentPane().add(chatBox, cc);
        //set sizes
        chatBox.setMaximumSize(new Dimension(400, 400));
        chatBox.setPreferredSize(new Dimension(400, 400));
    }

    private static void addLoginBoxes(Container contentPane) {
        GridBagConstraints lc = new GridBagConstraints();
        loginBox = new JTextField("Insert Acc. number here");
        passwordBox = new JTextField("Insert password here");
        loginButton = new JButton("Login");
        //add buttons and boxes with constraints
        GridBagConstraints ltc = new GridBagConstraints();
        ltc.anchor=ltc.WEST;
        ltc.gridx = 2;
        ltc.gridy = 2;
        window.getContentPane().add(loginBox, ltc);
        GridBagConstraints pbc = new GridBagConstraints();
        pbc.gridx = 2;
        pbc.gridy = 2;
        pbc.anchor=ltc.CENTER;
        window.getContentPane().add(passwordBox, pbc);
        GridBagConstraints lbc = new GridBagConstraints();
        lbc.gridx = 2;
        lbc.gridy = 2;
        lbc.anchor=ltc.EAST;
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
