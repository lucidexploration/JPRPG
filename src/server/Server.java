package server;


import java.io.*;
import java.net.*;

public class Server extends Thread {

    private int port;

    protected Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serversocket = new ServerSocket(this.port);
            System.out.println("Listening on " + serversocket);
            while (true) {
                //accept a client connection
                Socket clientsocket = serversocket.accept();
                System.out.println("Connected " + clientsocket);

                // interpret the packets
                new Echo(clientsocket).start();
            }
        } catch (IOException ie) {
        }
    }

    private class Echo extends Thread {

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Echo(Socket socket) {
            this.socket = socket;

            //create the streams to read and write to
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException ex) {
            }
        }

        @Override
        public void run() {
                try {
                    //get the message from the client
                    String input = in.readLine();

                    //send the message back to the client
                    out.println(input);
                    out.flush();

                    //print the message to the terminal
                    System.out.println("echoed from " + socket + ":" + input);

                    //close the streams and the socket
                    in.close();
                    out.close();
                    socket.close();
                } catch (EOFException e) {
                } catch (IOException e) {
                }
        }
    }
}
