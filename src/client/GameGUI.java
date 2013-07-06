package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
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
    //--------------------------------------------------------------------------Tabbed objects.
    public static JTabbedPane tabbedPane;
    public static JPanel inventory;
    public static JPanel optionsPane;
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
    public static JTextField nameCreationBox;
    public static JTextField extraCreationBox;
    //--------------------------------------------------------------------------The Layout manager.
    public static GridBagLayout layoutManager;
    public static GridBagConstraints objectPosition;
    //--------------------------------------------------------------------------Input.
    private static long lastSent = System.currentTimeMillis();
    private static boolean canSend = false;
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
            if ((System.currentTimeMillis() - lastSent) >= 1000) {
                canSend = true;
            }

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
        window.setFocusable(false);
        //----------------------------------------------------------------------Add the window's layout.
        layoutManager = new GridBagLayout();
        objectPosition = new GridBagConstraints();
        window.setLayout(layoutManager);
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
                        if (canSend) {
                            gameClient.returnGameController().sendMoveLeft();
                            lastSent = System.currentTimeMillis();
                            canSend = false;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        System.out.println("right pressed");
                        if (canSend) {
                            gameClient.returnGameController().sendMoveRight();
                            lastSent = System.currentTimeMillis();
                            canSend = false;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        System.out.println("up pressed");
                        if (canSend) {
                            gameClient.returnGameController().sendMoveUp();
                            lastSent = System.currentTimeMillis();
                            canSend = false;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        System.out.println("down pressed");
                        if (canSend) {
                            gameClient.returnGameController().sendMoveDown();
                            lastSent = System.currentTimeMillis();
                            canSend = false;
                        }
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
                        gameClient.returnTileGenerator().returnTile(0, g, xTile * tileWidth, yTile * tileHeight);
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
                int tileWidth = 90;
                int tileHeight = 90;

                g.setColor(Color.red);
                g.drawString(playerCon.returnName(), (5 * 90) + 10, (5 * 90) + 10);
                gameClient.returnTileGenerator().returnNPC(1, g, 5 * 90, 5 * 90);
                Iterator iter = gameClient.returnMap().keySet().iterator();
                while (true) {
                    while (iter.hasNext()) {
                        String currentMonster = (String) iter.next();
                        int baseX = playerCon.returnX() - 5;
                        int baseY = playerCon.returnY() - 5;
                        int monsterX = gameClient.returnMap().get(currentMonster).returnX() - baseX;
                        int monsterY = gameClient.returnMap().get(currentMonster).returnY() - baseY;
                        g.setColor(Color.red);
                        g.drawString(gameClient.returnMap().get(currentMonster).returnName(), (monsterX * 90) + 10, (monsterY * 90) + 10);
                        gameClient.returnTileGenerator().returnNPC(0, g, monsterX * tileWidth, monsterY * tileHeight);
                    }
                    break;
                }
            }

            //------------------------------------------------------------------Draw the items using information recieved from server
            private void drawItems() {
            }
        };

        //----------------------------------------------------------------------Set world display area Options
        worldDisplay.setPreferredSize(new Dimension(900, 900));
        //position the world within the window and add it
        objectPosition.gridx = 0;
        objectPosition.gridy = 0;
        objectPosition.fill = GridBagConstraints.BOTH;
        cp.add(worldDisplay, objectPosition);
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
                g.fillOval(100, 150, 200, 200);
                int barWidth = 100;
                int barHeight = 10;
                float hpPercent = (((float) playerCon.returnHP() / (float) playerCon.returnTotalHP()) * 100);//The bar health and mana bars are 100 pixels wide. The green bar will be a fraction of this width.
                float mpPercent = (((float) playerCon.returnMana() / (float) playerCon.returnTotalMana()) * 100);//This fraction is decided by the fraction of health remaining.

                //--------------------------------------------------------------Name
                g.setColor(Color.black);
                g.drawString(playerCon.returnName(), 10, 20);
                //--------------------------------------------------------------Draw HealthBar
                g.setColor(Color.black);
                g.drawString("Health : " + playerCon.returnHP() + "/" + playerCon.returnTotalHP(), 10, 40);
                g.setColor(Color.red);
                g.fillRect(110, 30, barWidth, barHeight);
                g.setColor(Color.green);
                g.fillRect(110, 30, (int) hpPercent, barHeight);
                //--------------------------------------------------------------Draw ManaBar
                g.setColor(Color.black);
                g.drawString("Mana : " + playerCon.returnMana() + "/" + playerCon.returnTotalMana(), 10, 60);
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
        objectPosition.gridx = 2;
        objectPosition.gridy = 0;
        objectPosition.fill = GridBagConstraints.BOTH;
        objectPosition.anchor = GridBagConstraints.NORTH;
        cp.add(statusDisplay, objectPosition);
        window.validate();
    }

    //=================================================================================================================================================================================
    private static void addChatBox(Container cp) {
        chatBox = new JTextArea("Welcome to JPRPG.");
        chatBox.setFocusable(false);
        chatBox.setPreferredSize(new Dimension(400, 500));//--------------------Specifiy the size of the chat box.
        chatboxInput = new JTextField("Chat here.");
        chatboxInput.setVisible(false);
        //----------------------------------------------------------------------Send Chat when enter pressed.
        chatboxInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!chatboxInput.getText().isEmpty()) {
                    String text = chatboxInput.getText();
                    text = text.replaceAll("=--=", ",");
                    gameClient.returnGameController().sendChat(playerCon.returnName(), text);
                    chatboxInput.setText("");
                }
            }
        });
        //----------------------------------------------------------------------If the escape key is pressed, shift focus back to the game world.
        chatboxInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    window.requestFocus();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    window.requestFocus();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    window.requestFocus();
                }
            }
        });
        //----------------------------------------------------------------------Position and add the chatbox
        objectPosition.gridx = 2;
        objectPosition.gridy = 0;
        objectPosition.anchor = GridBagConstraints.SOUTH;
        objectPosition.fill = GridBagConstraints.NONE;
        cp.add(chatBox, objectPosition);
        window.validate();
        //----------------------------------------------------------------------Position and add the chatbox text input
        objectPosition.gridx = 2;
        objectPosition.gridy = 2;
        objectPosition.anchor = GridBagConstraints.SOUTH;
        objectPosition.fill = GridBagConstraints.BOTH;
        cp.add(chatboxInput, objectPosition);
        window.validate();

    }

    //=================================================================================================================================================================================
    private static void addTabbedPane(Container cp) {
        tabbedPane = new JTabbedPane();//---------------------------------------Create the tabbed pane

        //----------------------------------------------------------------------Set tabbed pane options
        tabbedPane.setVisible(true);
        tabbedPane.setFocusable(false);
        tabbedPane.setPreferredSize(new Dimension(300, 900));

        //----------------------------------------------------------------------Position and add pane
        objectPosition.gridx = 3;
        objectPosition.gridy = 0;
        objectPosition.fill = GridBagConstraints.BOTH;
        objectPosition.anchor = GridBagConstraints.CENTER;
        cp.add(tabbedPane, objectPosition);
        //----------------------------------------------------------------------Add inventory to pane
        inventory = new JPanel() {
            @Override
            public void paint(Graphics g) {
                g.setColor(Color.getHSBColor((float) 0.53, (float) 0.5, (float) 0.70));
                g.fillOval(0, 0, 280, 870);
            }
        };
        inventory.setPreferredSize(new Dimension(300, 900));
        tabbedPane.addTab("Inventory", inventory);
        //----------------------------------------------------------------------Add options to pane
        optionsPane = new JPanel() {
            @Override
            public void paint(Graphics g) {
                g.setColor(Color.getHSBColor((float) 0.73, (float) 0.5, (float) 0.70));
                g.fillOval(0, 0, 280, 870);
            }
        };
        optionsPane.setPreferredSize(new Dimension(300, 900));
        tabbedPane.add("Options", optionsPane);
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
        objectPosition.anchor = GridBagConstraints.WEST;
        objectPosition.gridx = 2;
        objectPosition.gridy = 2;
        objectPosition.fill = GridBagConstraints.VERTICAL;
        loginBox.setPreferredSize(new Dimension(190, 20));
        cp.add(loginBox, objectPosition);
        window.validate();
        //----------------------------------------------------------------------Password box.
        objectPosition.gridx = 2;
        objectPosition.gridy = 2;
        objectPosition.anchor = GridBagConstraints.EAST;
        loginBox.setPreferredSize(new Dimension(190, 20));
        cp.add(passwordBox, objectPosition);
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
        objectPosition.gridx = 3;
        objectPosition.gridy = 2;
        objectPosition.anchor = GridBagConstraints.WEST;
        objectPosition.fill = GridBagConstraints.NONE;
        cp.add(loginButton, objectPosition);
        window.validate();
        //----------------------------------------------------------------------Account creation buttons and windows.
        createAccount = new JButton("Create Account");
        createAccount.setFocusable(false);
        createAccount.setText("Create Account");
        accCreationPanel = new JPanel();
        accCreationBox = new JTextField("Insert Acc Number here");
        passCreationBox = new JTextField("Insert password here");
        accCreationButton = new JButton("Send to Server");
        nameCreationBox = new JTextField("Insert name here");
        extraCreationBox = new JTextField("extra");
        //----------------------------------------------------------------------Add account creation input to the panel that displays when popped up and position it all.
        accCreationPanel.setLayout(new GridBagLayout());
        objectPosition.gridx = 0;
        objectPosition.gridy = 0;
        objectPosition.fill = GridBagConstraints.BOTH;
        objectPosition.anchor = GridBagConstraints.CENTER;
        accCreationPanel.add(accCreationBox, objectPosition);
        objectPosition.gridx = 1;
        objectPosition.gridy = 0;
        accCreationPanel.add(passCreationBox, objectPosition);
        objectPosition.gridx = 2;
        objectPosition.gridy = 2;
        objectPosition.anchor = GridBagConstraints.CENTER;
        accCreationPanel.add(accCreationButton, objectPosition);
        objectPosition.gridx = 0;
        objectPosition.gridy = 1;
        accCreationPanel.add(nameCreationBox, objectPosition);
//        creationLayout.gridx=0;
//        creationLayout.gridy=2;
//        accCreationPanel.add(extraCreationBox,creationLayout);
        //----------------------------------------------------------------------Send new account information when the button is pressed.
        accCreationButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //add code here to send info to the server
                gameClient.returnGameController().createAccount(
                        Integer.valueOf(accCreationBox.getText()),
                        passCreationBox.getText(),
                        nameCreationBox.getText());
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
        objectPosition.gridx = 0;
        objectPosition.gridy = 2;
        objectPosition.anchor = GridBagConstraints.WEST;
        objectPosition.fill = objectPosition.NONE;
        cp.add(createAccount, objectPosition);
        window.validate();
    }
}
