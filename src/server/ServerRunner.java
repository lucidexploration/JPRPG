package server;

import java.io.IOException;

public class ServerRunner {
    
    public static void main(String args[]) throws IOException{
        run();
        }
    
    public static void run() throws IOException{
        int[] ports = new int[1];
        ports[0]=7171;
        new Server(ports);
    }
    

}