package server;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class ServerRunner {

    private static JFrame window;
    private static JButton saveAndExit;
    private static JScrollPane consoleScroll;
    private static JScrollPane onlineListScroll;
    public static JTextArea console;
    public static JTextArea onlineList;
    private static GridBagLayout layout = new GridBagLayout();
    private static GridBagConstraints constraints = new GridBagConstraints();
    private static Server server;

    //=========================================================================================================================
    public static void main(String args[]) throws IOException {
        makeGUI();
        run();
    }

    public static void run() throws IOException {
        int[] ports = new int[1];
        ports[0] = 7171;
        server = new Server(ports);
    }

    private static void makeGUI() {
        window = new JFrame("Server");
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container cp = window.getContentPane();
        window.setLayout(layout);
        addConsole(cp);
        addOnlineList(cp);
        addButton(cp);
        window.pack();
        window.setVisible(true);
    }

    private static void addConsole(Container cp) {
        console = new JTextArea() {
        };
        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        consoleScroll = new JScrollPane(console);
        consoleScroll.setPreferredSize(new Dimension(600, 400));
        consoleScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consoleScroll.setAutoscrolls(true);
        cp.add(consoleScroll, constraints);
        window.validate();
    }

    private static void addOnlineList(Container cp) {
        onlineList = new JTextArea() {
        };
        onlineList.setEditable(false);
        onlineList.setLineWrap(true);
        onlineList.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) onlineList.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        constraints.gridx = 1;
        constraints.gridy = 0;
        onlineListScroll = new JScrollPane(onlineList);
        onlineListScroll.setPreferredSize(new Dimension(200, 600));
        onlineListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        onlineListScroll.setAutoscrolls(true);
        cp.add(onlineListScroll, constraints);
        window.validate();
    }

    private static void addButton(Container cp) {
        saveAndExit = new JButton("Save and Exit") {
        };
        saveAndExit.setPreferredSize(new Dimension(600, 200));
        saveAndExit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    server.saveAndExit();
                } catch (IOException ex) {
                    System.out.println("shit done broke");
                }
            }
        });
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy = 1;
        cp.add(saveAndExit, constraints);
        window.validate();
    }
}