
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 *
 */
class GameGUI {

    //--------------------------------------------------------------------------The game window.
    public static JFrame window;
    //--------------------------------------------------------------------------World display objects.
    public static JLabel worldDisplay;
    //--------------------------------------------------------------------------Status display objects.
    public static JTextArea statusDisplay;
    public static double totalHealth;
    public static double currentHealth;
    public static double totalMana;
    public static double currentMana;
    //--------------------------------------------------------------------------Chat objects.
    public static JTextArea chatBox;
    public static JTextField chatboxInput;
    //--------------------------------------------------------------------------Inventory objects.
    public static JTabbedPane tabbedPane;
    public static JTextArea inventory;
    //--------------------------------------------------------------------------Login objects.
    public static JTextField loginBox;
    public static JTextField passwordBox;
    public static JButton loginButton;
    public static JButton createAccount;
    //--------------------------------------------------------------------------Account creation objects.
    public static JPanel accCreationPanel;
    public static JTextField accCreationBox;
    public static JTextField passCreationBox;
    public static JButton accCreationButton;
    //--------------------------------------------------------------------------The Layout manager.
    public static GridBagLayout gbl;
    //--------------------------------------------------------------------------Input.
    public static Timer timer = new Timer();//----------------------------------The time checks for input from server, or player.
    public static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
                gameClient.returnGameController().recieveInput();
            } catch (IOException ex) {
                //--------------------------------------------------------------This will occur if the socket closes. In that case, exit client.
                System.exit(-1);
            }
            worldDisplay.repaint();
        }
    };
    //--------------------------------------------------------------------------Add the game client.
    public static GameClient gameClient;
    public static PlayerController playerCon;

    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    //----------------------------------------------------------Load the data
                    loadPlayer();
                    loadData();
                } catch (UnknownHostException ex) {
                    System.exit(-1);
                } catch (IOException ex) {
                    System.exit(-2);
                }

                //--------------------------------------------------------------Create the gui.
                makeGUI();
            }
        });
    }
    //=================================================================================================================================================================================

    private static void loadData() throws UnknownHostException, IOException {
        gameClient = new GameClient();
    }

    private static void loadPlayer() {
        playerCon = new PlayerController();
    }

    //=================================================================================================================================================================================
    private static void makeGUI() {
        //----------------------------------------------------------------------Create the Window.
        window = new JFrame("JPRPG");
        //----------------------------------------------------------------------set the windows options
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //----------------------------------------------------------------------Add the window's layout.
        gbl = new GridBagLayout();
        window.setLayout(gbl);
        //----------------------------------------------------------------------Set the frames contentPane to a variable for easier access.
        Container cp = window.getContentPane();
        //----------------------------------------------------------------------Add game parts to the Window.
        addWorldDisplay(cp);
        addStatusDisplay(cp);
        addChatBox(cp);
        addTabbedPane(cp);
        addLoginBoxes(cp);
        //----------------------------------------------------------------------Listen for Keypresses.
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        System.out.println("left pressed");
                        break;
                    case KeyEvent.VK_RIGHT:
                        System.out.println("right pressed");
                        break;
                    case KeyEvent.VK_UP:
                        System.out.println("up pressed");
                        break;
                    case KeyEvent.VK_DOWN:
                        System.out.println("down pressed");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        //----------------------------------------------------------------------Finalize the window.
        window.pack();
        window.setVisible(true);
    }

    //=================================================================================================================================================================================
    private static void addWorldDisplay(Container cp) {
        worldDisplay = new JLabel() {
            //------------------------------------------------------------------Draw the world in order 1)World 2)Items 3)Creatures--------\\
            @Override
            public void paint(Graphics g) {
                drawWorld(g);
                drawItems();
                drawPlayersNPCS(g);
            }
            //------------------------------------------------------------------Draw the gameworld using information recieved from server-----\\

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

            //------------------------------------------------------------------Draw the npc\pcs using information recieved from server----\\
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

            //------------------------------------------------------------------Draw the items using information recieved from server
            private void drawItems() {
            }
        };

        //----------------------------------------------------------------------Set world display area Options
        worldDisplay.setPreferredSize(new Dimension(900, 900));
        //position the world within the window and add it
        GridBagConstraints wc = new GridBagConstraints();
        wc.gridx = 0;
        wc.gridy = 0;
        wc.fill = GridBagConstraints.BOTH;
        cp.add(worldDisplay, wc);
        window.validate();

        //----------------------------------------------------------------------Start the time to check for Input
        timer.schedule(task, 0, 1);
    }

    //=================================================================================================================================================================================
    private static void addStatusDisplay(Container cp) {
        //----------------------------------------------------------------------Setup the status display variables
        statusDisplay = new JTextArea() {
            @Override
            public void paint(Graphics g) {//-----------------------------------Draw the display area.
                g.setColor(Color.red);
                g.fillOval(100, 600, 200, 200);
                int barWidth = 100;
                int barHeight = 10;
                int hpPercent = (int) ((currentHealth / totalHealth) * barWidth);//--The bar health and mana bars are 100 pixels wide. The green bar will be a fraction of this width.
                int mpPercent = (int) ((currentMana / totalMana) * barWidth);//------This fraction is decided by the fraction of health remaining.

                //--------------------------------------------------------------Draw HealthBar
                g.setColor(Color.black);
                g.drawString(playerCon.returnName(), 10, 20);
                g.setColor(Color.black);
                g.drawString("Health : " + (int) currentHealth + "/" + (int) totalHealth, 10, 40);
                g.setColor(Color.red);
                g.fillRect(110, 30, barWidth, barHeight);
                g.setColor(Color.green);
                g.fillRect(110, 30, (int) hpPercent, barHeight);
                //--------------------------------------------------------------Draw ManaBar
                g.setColor(Color.black);
                g.drawString("Mana : " + (int) currentMana + "/" + (int) totalMana, 10, 60);
                g.setColor(Color.red);
                g.fillRect(110, 50, barWidth, barHeight);
                g.setColor(Color.green);
                g.fillRect(110, 50, (int) mpPercent, barHeight);
            }
        };
        statusDisplay.setPreferredSize(new Dimension(400, 900));
        statusDisplay.setEditable(false);
        statusDisplay.setFocusable(false);
        //----------------------------------------------------------------------Position the statusdiplay and add it.
        GridBagConstraints sdc = new GridBagConstraints();
        sdc.gridx = 2;
        sdc.gridy = 0;
        sdc.fill=GridBagConstraints.BOTH;
        sdc.anchor = GridBagConstraints.NORTH;
        cp.add(statusDisplay, sdc);
        window.validate();
    }

    //=================================================================================================================================================================================
    private static void addChatBox(Container cp) {
        chatBox = new JTextArea("Welcome to JPRPG.");
        chatBox.setFocusable(false);
        chatBox.setPreferredSize(new Dimension(400, 200));//--------------------Specifiy the size of the chat box.
        chatboxInput = new JTextField("Chat here.");
        chatboxInput.setVisible(false);
        chatboxInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chatboxInput.getText().length() > 0) {
                    String text = chatboxInput.getText();
                    text = text.replaceAll("¬", ",");
                    gameClient.returnGameController().chat("Bob Barker", text);
                    chatboxInput.setText("");
                }
                window.requestFocus();
            }
        });
        //----------------------------------------------------------------------Position and add the chatbox
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 2;
        cc.gridy = 0;
        cc.anchor = GridBagConstraints.CENTER;
        cc.insets = new Insets(0, 0, 100, 0);
        cp.add(chatBox, cc);
        window.validate();
        //----------------------------------------------------------------------Position and add the chatbox text input
        GridBagConstraints cbic = new GridBagConstraints();
        cbic.gridx = 2;
        cbic.gridy = 2;
        cbic.anchor = GridBagConstraints.SOUTH;
        cbic.fill = GridBagConstraints.BOTH;
        cp.add(chatboxInput, cbic);
        window.validate();

    }

    //=================================================================================================================================================================================
    private static void addTabbedPane(Container cp) {
        tabbedPane = new JTabbedPane();//---------------------------------------Create the tabbed pane
        tabbedPane.setVisible(true);//------------------------------------------Set tabbed pane options
        tabbedPane.setPreferredSize(new Dimension(300,900));
        //----------------------------------------------------------------------Position and add pane
        GridBagConstraints paneCon = new GridBagConstraints();
        paneCon.gridx = 3;
        paneCon.gridy = 0;
        paneCon.fill=GridBagConstraints.BOTH;
        paneCon.anchor = GridBagConstraints.CENTER;
        cp.add(tabbedPane, paneCon);
        //----------------------------------------------------------------------Add inventory to pane
        inventory = new JTextArea() {
            @Override
            public void paint(Graphics g) {
                g.setColor(Color.getHSBColor((float)0.53,(float)0.5,(float)0.70));
                g.fillOval(0, 0, 280, 870);
            }
        };
        inventory.setEditable(false);
        inventory.setPreferredSize(new Dimension(300,900));
        tabbedPane.addTab("Inventory", inventory);
        //----------------------------------------------------------------------Add options to pane
        tabbedPane.add("Options",null);
        window.validate();
    }

    //=================================================================================================================================================================================
    private static void addLoginBoxes(Container cp) {
        //----------------------------------------------------------------------Create login buttons and boxes.
        loginBox = new JTextField("Insert Acc. number here");
        loginBox.setAutoscrolls(false);
        passwordBox = new JTextField("Insert password here                            ");
        passwordBox.setAutoscrolls(false);
        loginButton = new JButton("Login");
        //----------------------------------------------------------------------Position and Add Login buttons and boxes.
        //----------------------------------------------------------------------Login box.
        GridBagConstraints ltc = new GridBagConstraints();
        ltc.anchor = GridBagConstraints.WEST;
        ltc.gridx = 2;
        ltc.gridy = 2;
        ltc.fill=GridBagConstraints.VERTICAL;
        loginBox.setPreferredSize(new Dimension(190,20));
        cp.add(loginBox, ltc);
        window.validate();
        //----------------------------------------------------------------------Password box.
        GridBagConstraints pbc = new GridBagConstraints();
        pbc.gridx = 2;
        pbc.gridy = 2;
        pbc.fill=GridBagConstraints.VERTICAL;
        pbc.anchor = GridBagConstraints.LINE_END;
        loginBox.setPreferredSize(new Dimension(190,20));
        cp.add(passwordBox, pbc);
        window.validate();
        //----------------------------------------------------------------------When login button is pressed, send the login information to the server.
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    gameClient.returnGameController().logIn(Integer.parseInt(loginBox.getText()), passwordBox.getText());
                } catch (NumberFormatException x) {//---------------------------This occurs if non number characters are put into the Account Number field.
                    JOptionPane.showMessageDialog(window, "The acc number must be numbers!!!");
                }
            }
        });
        //----------------------------------------------------------------------Login button.
        GridBagConstraints lbc = new GridBagConstraints();
        lbc.gridx = 3;
        lbc.gridy = 2;
        lbc.anchor = GridBagConstraints.WEST;
        cp.add(loginButton, lbc);
        window.validate();
        //----------------------------------------------------------------------Account creation buttons and windows.
        createAccount = new JButton("Create Account");
        createAccount.setText("Create Account");
        accCreationPanel = new JPanel();
        accCreationBox = new JTextField("Insert Acc Number here");
        passCreationBox = new JTextField("Insert password here");
        accCreationButton = new JButton("Send to Server");
        //----------------------------------------------------------------------Add account creation input to the panel that displays when popped up
        accCreationPanel.add(accCreationBox);
        accCreationPanel.add(passCreationBox);
        accCreationPanel.add(accCreationButton);
        //----------------------------------------------------------------------Send new account information when the button is pressed.
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
        //----------------------------------------------------------------------Pop-Up Account Creation Window when create account button is pressed.
        createAccount.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(null,
                        accCreationPanel,
                        "Insert acc number and password",
                        JOptionPane.DEFAULT_OPTION);
            }
        });
        //----------------------------------------------------------------------Add and align the account creation button.
        GridBagConstraints acc = new GridBagConstraints();
        acc.gridx = 0;
        acc.gridy = 2;
        acc.anchor = GridBagConstraints.WEST;
        cp.add(createAccount, acc);
        window.validate();
    }
}
