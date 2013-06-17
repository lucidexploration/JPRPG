package server;

public class ServerRunner {
    
    public static void main(String args[]){
        run();
        }
    
    public static void run(){
        new Server(7171).start();
    }
}