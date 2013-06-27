
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
                gameClient.returnGameController().recieveInput(chatBox);
                checkForInput();
            } catch (IOException ex) {
            }
            checkForInput();
            worldDisplay.repaint();
        }
    };
    //-----------the game classses----------------
    private static GameClient gameClient;

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
        //------------------Create the Window---------------------------------\\
        window = new JFrame("JPRPG");
        //----set the windows options
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        //--------add the window's layout
        gbl = new GridBagLayout();
        window.setLayout(gbl);
        Container cp = window.getContentPane();
        //------------------Add game parts to Window--------------------------\\
        addWorldDisplay(cp);
        addStatusDisplay(cp);
        addChatBox(cp);
        addLoginBoxes(cp);
        //--------------------Listen for Keypresses---------------------------\\
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
        //--------------------Finalize the window-----------------------------\\
        window.pack();
    }

    public static void addWorldDisplay(Container cp) {
        worldDisplay = new JLabel() {
            //-----Draw the world in order 1)World 2)Items 3)Creatures--------\\
            @Override
            public void paint(Graphics g) {
                drawWorld(g);
                drawItems();
                drawPlayersNPCS(g);
            }
            //--draw the gameworld using information recieved from server-----\\

            private void drawWorld(Graphics g) {
                int xTile = 0;
                int yTile = 0;
                int windowWidth = 900;
                int windowHeight = 900;
                int tileWidth = 90;
                int tileHeight = 90;

                while (xTile * tileWidth < windowWidth) {
                    while (yTile * tileHeight < windowHeight) {
                        gameClient.returnTileGenerator().emptySquare(g, xTile * tileWidth, yTile * tileHeight);
                        g.setColor(Color.red);
                        g.drawRect(xTile * tileWidth, yTile * tileHeight, tileWidth, tileHeight);
                        yTile++;
                    }
                    xTile++;
                    yTile = 0;
                }
            }
            //-----draw the npc\pcs using information recieved from server----\\

            private void drawPlayersNPCS(Graphics g) {
                int xTile = 0;
                int yTile = 0;
                int windowWidth = 900;
                int windowHeight = 900;
                int tileWidth = 90;
                int tileHeight = 90;

                while (xTile * tileWidth < windowWidth) {
                    while (yTile * tileHeight < windowHeight) {
                        gameClient.returnTileGenerator().returnNPC(9, g, xTile * tileWidth, yTile * tileHeight);
                        g.setColor(Color.red);
                        g.drawRect(xTile * tileWidth, yTile * tileHeight, tileWidth, tileHeight);
                        yTile++;
                    }
                    xTile++;
                    yTile = 0;
                }
            }
            //-----draw the items using information recieved from server------\\

            private void drawItems() {
            }
        };

        //-----------------Set world display area Options---------------------\\
        worldDisplay.setPreferredSize(new Dimension(900, 900));
        //position the world within the window and add it
        GridBagConstraints wc = new GridBagConstraints();
        wc.gridx = 0;
        wc.gridy = 0;
        wc.gridwidth = 2;
        wc.fill = GridBagConstraints.BOTH;
        cp.add(worldDisplay, wc);

        //---------------Start the time to check for Input--------------------\\
        timer.schedule(task, 0, 1);
    }

    private static void addStatusDisplay(Container cp) {
        //-------------------------Setup Variables----------------------------\\
        int health = 10;
        int mana = 10;
        statusDisplay = new JTextArea("Current Health : " + health + "\n"
                + "Current Mana : " + mana) {
            @Override
            public void paint(Graphics g) {
                //draw HealthBar
                g.setColor(Color.black);
                g.drawString("Health : 10/100", 10, 20);
                g.setColor(Color.red);
                g.fillRect(110, 10, 100, 10);
                g.setColor(Color.green);
                g.fillRect(110, 10, 10, 10);
                //draw ManaBar
                g.setColor(Color.black);
                g.drawString("Mana : 10/100", 10, 40);
                g.setColor(Color.red);
                g.fillRect(110, 30, 100, 10);
                g.setColor(Color.green);
                g.fillRect(110, 30, 10, 10);
            }
        };
        statusDisplay.setPreferredSize(new Dimension(400, 200));
        statusDisplay.setEditable(false);
        //--------------position the statusdiplay and add it
        GridBagConstraints sdc = new GridBagConstraints();
        sdc.gridx = 2;
        sdc.gridy = 0;
        sdc.anchor = GridBagConstraints.NORTH;
        cp.add(statusDisplay, sdc);
    }

    public static void addChatBox(Container cp) {
        chatBox = new JTextArea("This is the chatbox");
        chatboxInput = new JTextField("Type shit here");
        chatboxInput.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chatboxInput.getText().length() > 0) {
                    String text = chatboxInput.getText();
                    text = text.replaceAll("¬", ",");
                    gameClient.returnGameController().chat("Bob Barker", text);
                    chatboxInput.setText("");
                }
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
        cp.add(chatboxInput, cbic);
        cp.add(chatBox, cc);
        //set sizes
        chatBox.setPreferredSize(new Dimension(400, 500));
    }

    private static void addLoginBoxes(Container cp) {
        //-------------------Create login buttons and boxes-------------------\\
        loginBox = new JTextField("Insert Acc. number here");
        loginBox.setAutoscrolls(false);
        passwordBox = new JTextField("Insert password here");
        passwordBox.setAutoscrolls(false);
        loginButton = new JButton("Login");
        //add buttons and boxes with constraints
        //---------------------------login box-----------------------------
        GridBagConstraints ltc = new GridBagConstraints();
        ltc.anchor = GridBagConstraints.WEST;
        //position
        ltc.gridx = 2;
        ltc.gridy = 2;
        //add to frame
        cp.add(loginBox, ltc);
        //--------------------------password box-----------------------------
        GridBagConstraints pbc = new GridBagConstraints();
        //position and add to contentPane
        pbc.gridx = 2;
        pbc.gridy = 2;
        pbc.anchor = GridBagConstraints.CENTER;
        cp.add(passwordBox, pbc);
        //---------------------------LOGIN-IN to Server-----------------------\\
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    gameClient.returnGameController().logIn(Integer.parseInt(loginBox.getText()), passwordBox.getText());
                } catch (NumberFormatException x) {
                    JOptionPane.showMessageDialog(window, "The acc number must be numbers!!!");
                }
            }
        });
        GridBagConstraints lbc = new GridBagConstraints();
        //----position the login buttons and add to contentPane
        lbc.gridx = 2;
        lbc.gridy = 2;
        lbc.anchor = GridBagConstraints.EAST;
        cp.add(loginButton, lbc);
        //-------------account creation buttons and windows-----------
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
        //---------------Send Account Creation Info To Server-----------------\\
        accCreationButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //add code here to send info to the server
                gameClient.returnGameController().createAccount(
                        Integer.valueOf(accCreationBox.getText()),
                        passCreationBox.getText(),
                        "Bob");
            }
        });
        //--------------------Pop-Up Account Creation Window------------------\\
        createAccount.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(null,
                        accCreationPanel,
                        "Insert acc number and password",
                        JOptionPane.DEFAULT_OPTION);
            }
        });
        //now align everything
        GridBagConstraints acc = new GridBagConstraints();
        acc.gridx = 0;
        acc.gridy = 2;
        acc.anchor = GridBagConstraints.WEST;
        cp.add(createAccount, acc);
    }

    private static void checkForInput() {
        if (keysPressed.contains('w')) {
            //add actions here
        }
    }

    private static void loadData() throws UnknownHostException, IOException {
        gameClient = new GameClient();
    }
}
