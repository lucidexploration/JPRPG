package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/*
 * This file does one thing and one thing only. It creates the original
 * empty map file that the server will use.
 * 
 * The file is 146,478 KB in size. Make sure you have space.
 */

public class EmptyMapMaker {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    doThis();
                } catch (IOException ex) {
                    System.out.println("Couldnt write to file.");
                }
            }

            private void doThis() throws IOException {
                File file = new File((System.getProperty("user.home") + "//JPRPG//map.___"));
                Scanner scanner = null;
                try {
                    scanner = new Scanner(file);
                } catch (FileNotFoundException ex) {
                    try {
                        file.createNewFile();
                        scanner = new Scanner(file);
                    } catch (IOException ex1) {
                        file.mkdirs();
                    }
                }
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                int x =0;
                while(x<10000000){
                    out.write("000¬00¬000¬"+"\n");
                    x++;
                }
                
            }
        });
    }
}
