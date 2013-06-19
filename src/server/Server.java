package server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

    private int ports[];
    private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);

    public Server(int ports[]) throws IOException {
        this.ports = ports;

        configure_selector();
    }

    private void configure_selector() throws IOException {
        // create a selector that will by used for multiplexing. The selector
        // registers the socketserverchannel as
        // well as all socketchannels that are created
        Selector selector = Selector.open();

        // Open a listener on each port, and register each one
        // with the selector
        for (int i = 0; i < ports.length; ++i) {
            // create a new serversocketchannel. The channel is unbound.
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // mark the serversocketchannel as non blocking
            ssc.configureBlocking(false);
            ServerSocket ss = ssc.socket();
            // bind the channel to an address. The channel starts listening to
            // incoming connections.
            InetSocketAddress address = new InetSocketAddress(ports[i]);
            ss.bind(address);

            // register the serversocketchannel with the selector. The OP_ACCEPT
            // option marks
            // a selection key as ready when the channel accepts a new connection.
            // When the
            // socket server accepts a connection this key is added to the list of
            // selected keys of the selector.
            // when asked for the selected keys, this key is returned and hence we
            // know that a new connection has been accepted.
            SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Going to listen on " + ports[i]);
        }

        while (true) {
            // wait for the selected keys
            int num = selector.select();

            // the select method returns with a list of selected keys
            Set selectedKeys = selector.selectedKeys();
            Iterator it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                // the selection key could either by the socketserver informing
                // that a new connection has been made, or
                // a socket client that is ready for read/write
                // we use the properties object attached to the channel to find
                // out the type of channel.

                if ((key.readyOps() & SelectionKey.OP_ACCEPT)
                        == SelectionKey.OP_ACCEPT) {
                    // a new connection has been obtained. This channel is
                    // therefore a socket server.
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    // accept the new connection on the server socket. Since the
                    // server socket channel is marked as non blocking
                    // this channel will return null if no client is connected.
                    SocketChannel sc = ssc.accept();
                    // set the client connection to be non blocking
                    sc.configureBlocking(false);
                    // Add the new connection to the selector
                    SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
                    it.remove();

                    System.out.println("Got connection from " + sc);
                } else if ((key.readyOps() & SelectionKey.OP_READ)
                        == SelectionKey.OP_READ) {
                    // Read the data
                    SocketChannel sc = (SocketChannel) key.channel();
                    //echoBuffer.clear();
                    sc.write(echoBuffer);

                    // Echo data
                    int bytesEchoed = 0;
                    while (true) {
                        // data is available for read
                        // buffer for reading
                        echoBuffer.clear();

                        int number_of_bytes = sc.read(echoBuffer);

                        if (number_of_bytes <= 0) {
                            // the key is automatically invalidated once the
                            // channel is closed
                            break;
                        }

                        // the channel is non blocking so keep it open till the
                        // count is >=0
                        echoBuffer.flip();

                        sc.write(echoBuffer);
                        bytesEchoed += number_of_bytes;
                    }

                    System.out.println("Echoed " + bytesEchoed + " from " + sc);

                    // once a key is handled, it needs to be removed
                    it.remove();
                }

            }
        }
    }

    static public void main(String args[]) throws Exception {
        if (args.length <= 0) {
            System.err.println("Usage: java MultiPortEcho port [port port ...]");
            System.exit(1);
        }

        int ports[] = new int[args.length];

        for (int i = 0; i < args.length; ++i) {
            ports[i] = Integer.parseInt(args[i]);
        }

        new Server(ports);
    }
}