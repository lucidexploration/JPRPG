
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 *
 */
class GameGUI {

    //------------the gui-----------------
    private static JFrame window;
    private static JLabel worldDisplay;
    private static JTextArea chatBox;
    private static JTextArea statusDisplay;
    //login boxes/buttons
    //chat
    private static JTextField chatboxInput;
    //login
    private static JTextField loginBox;
    private static JTextField passwordBox;
    private static JButton loginButton;
    private static JButton createAccount;
    //account creation
    private static JPanel accCreationPanel;
    private static JTextField accCreationBox;
    private static JTextField passCreationBox;
    private static JButton accCreationButton;
    //layout manager
    private static GridBagLayout gbl;
    //controls input
    private static final Set<Character> keysPressed = new HashSet<>();
    public static Timer timer = new Timer();
    public static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
                gc.returnGameController().recieveInput(chatBox);
            } catch (IOException ex) {
            }
            checkForInput();
            worldDisplay.repaint();
        }
    };
    //-----------the game classses----------------
    private static GameClient gc;

    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    loadData();
                } catch (UnknownHostException ex) {
                    System.exit(-1);
                } catch (IOException ex) {
                    System.exit(-2);
                }
                //---------create the gui-------------
                makeGUI();
            }
        });
    }

    public static void makeGUI() {
        window = new JFrame("JPRPG");
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //create the layout
        gbl = new GridBagLayout();
        window.setLayout(gbl);
        //methods to add parts to frame/window
        addWorldDisplay(window.getContentPane());
        addStatusDisplay(window.getContentPane());
        addChatBox(window.getContentPane());
        addLoginBoxes(window.getContentPane());
        //listener for keypresses
        //eventually this will have to check for focus to decide what to do
        //and where to do it at
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
        worldDisplay.setVisible(true);
        chatBox.setVisible(true);
    }

    public static void addWorldDisplay(Container pane) {
        worldDisplay = new JLabel() {
            @Override
            public void paint(Graphics g) {
                //draw things in proper order
                //world on bottom, items ontop of world, players standing
                //ontop of everything
                drawWorld(g);
//                drawItems();
                drawPlayersNPCS(g);
            }

            private void drawWorld(Graphics g) {
                int x = 0;
                int y = 0;
                int windowWidth = 900;
                int windowHeight = 900;
                int tileWidth = 90;
                int tileHeight = 90;

                while (x * tileWidth < windowWidth) {
                    while (y * tileHeight < windowHeight) {
                        gc.returnTileGenerator().emptySquare(g, x * tileWidth, y * tileHeight);
                        g.setColor(Color.red);
                        g.drawRect(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
                        y++;
                    }
                    x++;
                    y = 0;
                }
            }

            private void drawPlayersNPCS(Graphics g) {
                int x = 0;
                int y = 0;
                int windowWidth = 900;
                int windowHeight = 900;
                int tileWidth = 90;
                int tileHeight = 90;

                while (x * tileWidth < windowWidth) {
                    while (y * tileHeight < windowHeight) {
                        gc.returnTileGenerator().returnNPC(9, g, x * tileWidth, y * tileHeight);
                        g.setColor(Color.red);
                        g.drawRect(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
                        y++;
                    }
                    x++;
                    y = 0;
                }
            }
        };

        //set size
        worldDisplay.setPreferredSize(new Dimension(900, 900));
        //add worldDisplay with proper constraints
        GridBagConstraints wc = new GridBagConstraints();
        //position
        wc.gridx = 0;
        wc.gridy = 0;
        wc.gridwidth = 2;
        wc.fill = GridBagConstraints.BOTH;
        //add to frame
        window.getContentPane().add(worldDisplay, wc);

        //start the time to check for input
        timer.schedule(task, 0, 50);
    }

    private static void addStatusDisplay(Container contentPane) {
        int health = 0;
        int mana = 0;
        statusDisplay = new JTextArea("Current Health : " + health + "\n"
                + "Current Mana : " + mana);
        GridBagConstraints sdc = new GridBagConstraints();
        //position
        sdc.gridx = 2;
        sdc.gridy = 0;
        sdc.anchor = GridBagConstraints.NORTH;
        //add it to frame
        window.getContentPane().add(statusDisplay, sdc);
        //set size
        statusDisplay.setPreferredSize(new Dimension(400, 200));
        statusDisplay.setEditable(false);
    }

    public static void addChatBox(Container pane) {
        chatBox = new JTextArea("This is the chatbox");
        chatboxInput = new JTextField("Type shit here");
        chatboxInput.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gc.returnGameController().chat(chatboxInput.getText());
            }
        });
        chatBox.setEditable(false);
        //add chatbox with constraints
        GridBagConstraints cc = new GridBagConstraints();
        GridBagConstraints cbic = new GridBagConstraints();
        //chatbox position
        cc.gridx = 2;
        cc.gridy = 0;
        cc.anchor = GridBagConstraints.CENTER;
        cc.fill = 0;
        //chatboxInput position
        cbic.gridx = 2;
        cbic.gridy = 0;
        cbic.anchor = GridBagConstraints.SOUTH;
        cbic.fill = 2;
        //add them to frame
        window.getContentPane().add(chatboxInput, cbic);
        window.getContentPane().add(chatBox, cc);
        //set sizes
        chatBox.setPreferredSize(new Dimension(400, 500));
    }

    private static void addLoginBoxes(Container contentPane) {
        GridBagConstraints lc = new GridBagConstraints();
        loginBox = new JTextField("Insert Acc. number here");
        loginBox.setAutoscrolls(false);
        passwordBox = new JTextField("Insert password here");
        passwordBox.setAutoscrolls(false);
        loginButton = new JButton("Login");
        //add buttons and boxes with constraints
        //----------------login box-----------------
        GridBagConstraints ltc = new GridBagConstraints();
        ltc.anchor = GridBagConstraints.WEST;
        //position
        ltc.gridx = 2;
        ltc.gridy = 2;
        //add to frame
        window.getContentPane().add(loginBox, ltc);
        //------------password box-----------------
        GridBagConstraints pbc = new GridBagConstraints();
        //position
        pbc.gridx = 2;
        pbc.gridy = 2;
        pbc.anchor = GridBagConstraints.CENTER;
        //add to frame
        window.getContentPane().add(passwordBox, pbc);
        //-------------login button----------------
        //when pressed, sends info to server for server to verify
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    gc.returnGameController().logIn(Integer.parseInt(loginBox.getText()), passwordBox.getText());
                } catch (NumberFormatException x) {
                    JOptionPane.showMessageDialog(window, "The acc number must be numbers!!!");
                }
            }
        });
        GridBagConstraints lbc = new GridBagConstraints();
        //position
        lbc.gridx = 2;
        lbc.gridy = 2;
        //add to frame
        lbc.anchor = GridBagConstraints.EAST;
        window.getContentPane().add(loginButton, lbc);
        //-------------account creation button and window-----------
        createAccount = new JButton("Create Account");
        createAccount.setText("Create Account");
        accCreationPanel = new JPanel();
        accCreationBox = new JTextField("Insert Acc Number here");
        passCreationBox = new JTextField("Insert password here");
        accCreationButton = new JButton("Send to Server");
        //add shit to the panel that displays when popped up
        accCreationPanel.add(accCreationBox);
        accCreationPanel.add(passCreationBox);
        accCreationPanel.add(accCreationButton);
        //when the accCreationButton is pressed, send the info to the server
        accCreationButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //add code here to send info to the server
                gc.returnGameController().createAccount(Integer.valueOf(accCreationBox.getText()), passCreationBox.getText(), "Bob");
            }
        });
        //make window popup on button press
        createAccount.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(null, accCreationPanel,
                        "Insert acc number and password",
                        JOptionPane.OK_CANCEL_OPTION);
            }
        });
        //now align everything
        GridBagConstraints acc = new GridBagConstraints();
        acc.gridx = 0;
        acc.gridy = 2;
        acc.anchor = GridBagConstraints.WEST;
        window.getContentPane().add(createAccount, acc);
    }

    private static void checkForInput() {
        if (keysPressed.contains('w')) {
            //add action here
        }
    }

    private static void loadData() throws UnknownHostException, IOException {
        gc = new GameClient();
    }
}
