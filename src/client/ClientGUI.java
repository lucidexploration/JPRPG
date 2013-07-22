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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

class ClientGUI {

    //--------------------------------------------------------------------------The overlay
    public static JPanel overlay;
    //--------------------------------------------------------------------------The game window.
    public static JFrame window;
    //--------------------------------------------------------------------------World display objects.
    public static JLabel worldDisplay;
    //--------------------------------------------------------------------------System status objects
    public static JPanel statusPanelLeft;
    public static JPanel statusPanelRight;
    //--------------------------------------------------------------------------Status display objects.
    public static JTextArea statusDisplay;
    public static double totalHealth;
    public static double currentHealth;
    public static double totalMana;
    public static double currentMana;
    //--------------------------------------------------------------------------Chat objects.
    public static JScrollPane chatScrollPane;
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
    public static JTextField accNumberBox;
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
    static long nextSecond = System.currentTimeMillis() + 1000;
    static int frameInLastSecond = 0;
    static int framesInCurrentSecond = 0;
    //--------------------------------------------------------------------------Timer for input
    public static Timer timer = new Timer();//----------------------------------The time checks for input from server, or player.
    public static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            //------------------------------------------------------------------Check for packets from server.
            try {
                gameClient.returnGameController().recieveInput();
            } catch (IOException ex) {
                //--------------------------------------------------------------This will occur if the socket closes. In that case, exit client.
                System.exit(-1);
            }
            //------------------------------------------------------------------Redraw the world.
            worldDisplay.repaint();
            if ((System.currentTimeMillis() - lastSent) >= 1000) {
                canSend = true;
            }
            cleanOutOfRangeMonsters();
        }

        private void cleanOutOfRangeMonsters() {
            Iterator iter =gameClient.returnNPCMap().keySet().iterator();
            while(iter.hasNext()){
                String nextmonster = (String) iter.next();
                int playerX=playerCon.returnX();
                int playerY=playerCon.returnY();
                int monsterX=gameClient.returnNPCMap().get(nextmonster).returnX();
                int monsterY=gameClient.returnNPCMap().get(nextmonster).returnY();
                int diffX = playerX-monsterX;
                int diffY = playerY - monsterY;
                if((diffX<-4||diffX>5)&&(diffY<-4||diffY>5))
                    gameClient.returnNPCMap().remove(nextmonster);
            }
        }
    };
    //--------------------------------------------------------------------------Add the game client.
    public static GameClient gameClient;
    public static PlayerController playerCon;
    public static TileGenerator tileGen;
    public static Map<String, ClientMonster> npcMap;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    //----------------------------------------------------------Load the data
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
    //--------------------------------------------------------------------------Load the different client parts.
    private static void loadData() throws UnknownHostException, IOException {
        gameClient = new GameClient();
        playerCon = gameClient.returnPlayerCon();
        tileGen = gameClient.returnTileGenerator();
        npcMap = gameClient.returnNPCMap();
    }

    //=================================================================================================================================================================================
    private static void makeGUI() {
        //----------------------------------------------------------------------Create the Window.
        window = new JFrame("JPRPG");
        //----------------------------------------------------------------------set the windows options
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setFocusable(false);
        //----------------------------------------------------------------------Draw the games background.
        window.setContentPane(new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(TileGenerator.backGround, 0, 0, null);
            }
        });
        //----------------------------------------------------------------------Set the frames contentPane to a variable for easier access.
        Container cp = window.getContentPane();
        //----------------------------------------------------------------------Add the window's layout.
        layoutManager = new GridBagLayout();
        objectPosition = new GridBagConstraints();
        cp.setLayout(layoutManager);
        //----------------------------------------------------------------------Add game parts to the Window.
        addWorldDisplay(cp);
        addSystemStatusLeft(cp);
        addStatusDisplay(cp);
        addChatBox(cp);
        addTabbedPane(cp);
        addOverlay(cp);
        addLoginBoxes(cp);
        //----------------------------------------------------------------------Listen for Keypresses.
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (canSend) {
                            gameClient.returnGameController().sendMoveLeft();
                            lastSent = System.currentTimeMillis();
                            canSend = false;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (canSend) {
                            gameClient.returnGameController().sendMoveRight();
                            lastSent = System.currentTimeMillis();
                            canSend = false;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (canSend) {
                            gameClient.returnGameController().sendMoveUp();
                            lastSent = System.currentTimeMillis();
                            canSend = false;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
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
    
    //--------------------------------------------------------------------------Add the overlay with the login screen.
    private static void addOverlay(Container cp) {
        overlay = new JPanel(){
            @Override
            public void paint(Graphics g){
                
                g.drawImage(tileGen.overlay, 0, 0, null);
                super.paint(g);
                g.dispose();
            }
        };
        overlay.setPreferredSize(new Dimension(1600,920));
        overlay.setOpaque(false);
        //----------------------------------------------------------------------position the overlay so that it draws over everything until told not too.
        objectPosition.gridx = 0;
        objectPosition.gridy = 0;
        objectPosition.fill = GridBagConstraints.BOTH;
        objectPosition.gridheight = 3;
        objectPosition.gridwidth = 3;
        cp.add(overlay,objectPosition);
        overlay.setVisible(true);
        window.validate();
    }

    //=================================================================================================================================================================================
    private static void addWorldDisplay(Container cp) {
        worldDisplay = new JLabel() {
            //------------------------------------------------------------------Draw the world in order 1)World 2)Items 3)Creatures--------\\
            @Override
            public void paint(Graphics g) {
                drawWorld(g);
                drawPlayersNPCS(g);
                drawMapObjects(g);

                long currentTime = System.currentTimeMillis();
                if (currentTime > nextSecond) {
                    nextSecond += 1000;
                    frameInLastSecond = framesInCurrentSecond;
                    framesInCurrentSecond = 0;
                }
                framesInCurrentSecond++;
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
                        tileGen.returnTile(0, g, xTile * tileWidth, yTile * tileHeight);
                        g.setColor(Color.black);
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

                g.setColor(Color.black);
                //--------------------------------------------------------------This stupid math below keeps names centered above the players character, regardless of length.
                g.drawString(playerCon.returnName(), (5 * 90) + ((90 - playerCon.returnName().length()) / 2) - playerCon.returnName().length() * 2, (5 * 90) + 30);
                tileGen.returnNPC(g, 1, 5 * 90, 5 * 90);
                Iterator iter = npcMap.keySet().iterator();
                while (true) {
                    while (iter.hasNext()) {
                        String currentMonster = (String) iter.next();
                        int baseX = playerCon.returnX() - 5;
                        int baseY = playerCon.returnY() - 5;
                        int monsterX = npcMap.get(currentMonster).returnX() - baseX;
                        int monsterY = npcMap.get(currentMonster).returnY() - baseY;
                        g.setColor(Color.red);
                        //------------------------------------------------------This stupid math below keeps names centered above the players character, regardless of length.
                        g.drawString(npcMap.get(currentMonster).returnName(), (monsterX * 90) + ((90 - npcMap.get(currentMonster).returnName().length()) / 2) - npcMap.get(currentMonster).returnName().length() * 2, (monsterY * 90) + 30);
                        tileGen.returnNPC(g, 0, monsterX * tileWidth, monsterY * tileHeight);
                    }
                    break;
                }
            }

            //------------------------------------------------------------------Draw the items using information recieved from server
            private void drawMapObjects(Graphics g) {
                if (!gameClient.map.isEmpty()) {
                    Iterator iter = gameClient.map.keySet().iterator();
                    String nextObject = "";
                    while (iter.hasNext()) {
                        nextObject = (String) iter.next();
                        String[] objectProperties = nextObject.split(",");
                        int baseX = playerCon.returnX() - 5;
                        int baseY = playerCon.returnY() - 5;
                        int x = Integer.valueOf(objectProperties[0]);
                        int y = Integer.valueOf(objectProperties[1]);
                        int z = Integer.valueOf(objectProperties[2]);
                        int itemX = x - baseX;
                        int itemY = y - baseY;
                        int object = gameClient.map.get(nextObject).returnObject();
                        tileGen.returnObject(g, object, itemX * 90, itemY * 90);
                    }
                }
            }
        };

        //----------------------------------------------------------------------Set world display area Options
        worldDisplay.setPreferredSize(new Dimension(900, 900));
        worldDisplay.setVisible(false);
        //----------------------------------------------------------------------position the world within the window and add it
        objectPosition.gridx = 0;
        objectPosition.gridy = 0;
        objectPosition.fill = GridBagConstraints.BOTH;
        objectPosition.gridheight = 3;
        cp.add(worldDisplay, objectPosition);
        objectPosition.gridheight = 1;
        window.validate();

        //----------------------------------------------------------------------Start the time to check for Input
        timer.schedule(task, 0, 1);
    }

    //============================================================================================================================================================================
    //--------------------------------------------------------------------------Add system status display.
    private static void addSystemStatusLeft(Container cp) {
        objectPosition.gridx = 0;
        objectPosition.gridy = 2;
        objectPosition.gridheight=1;
        objectPosition.gridwidth=1;
        statusPanelLeft = new JPanel() {
            @Override
            public void paint(Graphics g) {
                g.drawString(frameInLastSecond + " fps", 0, 15);
            }
        };
        statusPanelLeft.setPreferredSize(new Dimension(900, 20));
        statusPanelLeft.setVisible(false);
        cp.add(statusPanelLeft, objectPosition);
        window.validate();
    }

    //=================================================================================================================================================================================
    private static void addStatusDisplay(Container cp) {
        //----------------------------------------------------------------------Setup the status display variables
        statusDisplay = new JTextArea() {
            @Override
            public void paint(Graphics g) {//-----------------------------------Draw the display area.
                drawCharacterStatus(g);
                drawMiniMap(g);
            }

            private void drawCharacterStatus(Graphics g) {
                int barWidth = 100;
                int barHeight = 10;
                float hpPercent = (((float) playerCon.returnHP() / (float) playerCon.returnTotalHP()) * 100);//The bar health and mana bars are 100 pixels wide. The green bar will be a fraction of this width.
                float mpPercent = (((float) playerCon.returnMana() / (float) playerCon.returnTotalMana()) * 100);//This fraction is decided by the fraction of health remaining.

                g.drawImage(TileGenerator.statusBackground, 0, 0, null);
                //--------------------------------------------------------------Name
                g.setColor(Color.black);
                g.drawString(playerCon.returnName(), 25, 25);
                //--------------------------------------------------------------Draw HealthBar
                g.setColor(Color.black);
                g.drawString("Health : " + playerCon.returnHP() + "/" + playerCon.returnTotalHP(), 25, 45);
                g.setColor(Color.red);
                g.fillRect(125, 35, barWidth, barHeight);
                g.setColor(Color.green);
                g.fillRect(125, 35, (int) hpPercent, barHeight);
                g.setColor(Color.black);
                g.drawRect(125, 35, barWidth, barHeight);
                //--------------------------------------------------------------Draw ManaBar
                g.setColor(Color.black);
                g.drawString("Mana : " + playerCon.returnMana() + "/" + playerCon.returnTotalMana(), 25, 65);
                g.setColor(Color.red);
                g.fillRect(125, 55, barWidth, barHeight);
                g.setColor(Color.green);
                g.fillRect(125, 55, (int) mpPercent, barHeight);
                g.setColor(Color.black);
                g.drawRect(125, 55, barWidth, barHeight);
            }

            private void drawMiniMap(Graphics g) {
                int miniMapWidth = 360;
                int miniMapHeight = 270;
                int pixelWidth = 3;
                int pixelHeight = 4;

                g.setColor(Color.BLACK);
                g.drawString("Minimap", 180, 145);
            }
        };
        statusDisplay.setPreferredSize(new Dimension(400, 400));
        statusDisplay.setEditable(false);
        statusDisplay.setFocusable(false);
        statusDisplay.setVisible(false);
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
        //----------------------------------------------------------------------Create the chatbox and set options
        chatBox = new JTextArea("Welcome to JPRPG.");

        chatBox.setEditable(false);
        chatBox.setLineWrap(true);
        chatBox.setWrapStyleWord(true);
        chatBox.setVisible(false);
        DefaultCaret caret = (DefaultCaret) chatBox.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        //----------------------------------------------------------------------Position the scrollpane containing the chatbox
        objectPosition.gridx = 2;
        objectPosition.gridy = 1;
        objectPosition.anchor = GridBagConstraints.SOUTH;
        objectPosition.fill = GridBagConstraints.BOTH;

        //----------------------------------------------------------------------Create the scrollpane and set options.
        chatScrollPane = new JScrollPane(chatBox);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setAutoscrolls(true);
        chatScrollPane.setFocusable(false);
        chatScrollPane.setVisible(false);
        cp.add(chatScrollPane, objectPosition);
        window.validate();


        //----------------------------------------------------------------------CHATBOX INPUT
        chatboxInput = new JTextField("Chat here.");
        chatboxInput.setVisible(false);

        //----------------------------------------------------------------------Send Chat when enter pressed.
        chatboxInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!chatboxInput.getText().trim().isEmpty()) {
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
        tabbedPane.setVisible(false);
        tabbedPane.setFocusable(false);
        tabbedPane.setPreferredSize(new Dimension(300, 900));

        //----------------------------------------------------------------------Position and add pane
        objectPosition.gridx = 3;
        objectPosition.gridy = 0;
        objectPosition.fill = GridBagConstraints.BOTH;
        objectPosition.anchor = GridBagConstraints.CENTER;
        objectPosition.gridheight = 2;
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
        loginBox.setPreferredSize(new Dimension(190, 20));
        overlay.add(loginBox);
        window.validate();
        //----------------------------------------------------------------------Password box.
        loginBox.setPreferredSize(new Dimension(190, 20));
        overlay.add(passwordBox);
        window.validate();
        //----------------------------------------------------------------------When login button is pressed, send the login information to the server.
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!loginBox.getText().trim().isEmpty() && !passwordBox.getText().trim().isEmpty()) {
                    try {
                        gameClient.returnGameController().logIn(Integer.parseInt(loginBox.getText().trim()), passwordBox.getText().trim());
                    } catch (NumberFormatException x) {//---------------------------This occurs if non number characters are put into the Account Number field.
                        JOptionPane.showMessageDialog(window, "The acc number must be numbers!!!");
                    }
                }
            }
        });
        //----------------------------------------------------------------------Login button.
        overlay.add(loginButton, objectPosition);
        window.validate();
        //----------------------------------------------------------------------Account creation buttons and windows.
        createAccount = new JButton("Create Account");
        createAccount.setFocusable(false);
        createAccount.setText("Create Account");
        accCreationPanel = new JPanel(){
            @Override
            public void paint(Graphics g){
                super.paint(g);
                tileGen.returnNPC(g, 0, 100, 200);
            }
        };
        accCreationPanel.setPreferredSize(new Dimension(400,400));
        accNumberBox = new JTextField("Insert Acc Number here");
        passCreationBox = new JTextField("Insert password here");
        accCreationButton = new JButton("Send to Server");
        nameCreationBox = new JTextField("Insert name here");
        extraCreationBox = new JTextField("extra");

        accCreationPanel.add(accNumberBox, objectPosition);
        accCreationPanel.add(passCreationBox, objectPosition);
        accCreationPanel.add(accCreationButton, objectPosition);
        accCreationPanel.add(nameCreationBox, objectPosition);
        accCreationPanel.add(extraCreationBox,objectPosition);
        //----------------------------------------------------------------------Send new account information when the button is pressed.
        accCreationButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //--------------------------------------------------------------add code here to send info to the server
                if (!accNumberBox.getText().isEmpty() && !passCreationBox.getText().isEmpty() && !nameCreationBox.getText().isEmpty()) {
                    gameClient.returnGameController().createAccount(
                            Integer.valueOf(accNumberBox.getText()),
                            passCreationBox.getText().trim(),
                            nameCreationBox.getText().trim());
                }
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
        overlay.add(createAccount);
        window.validate();
    }
}
