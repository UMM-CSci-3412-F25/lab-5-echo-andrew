package echoserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

// bats testing command:
// bats test/Echo_server.bats
// bats test/Echo_server_and_client.bats

public class EchoServer {
    // Use socket 6013 for this lab
    public static final int portNumber = 6013;

    public static void main(String[] args) {
        // create a new server socket that listen on the specified port number
        // try here for server socket to auto close when meet error or program ends
        try (ServerSocket server = new ServerSocket(portNumber)) {

            // run forever, common for server style services
            // accepting client connections during this loop
            while (true) {

                // I use try here for auto close without having to call client.close() when the blocks end or exceptions happen
                // server.accept() will block until a client connects to the server
                // when a client connects then it will return a new socket dedicated to the client
                try (Socket client = server.accept()) {

                    // byte streams to read from and write to the client
                    InputStream input = client.getInputStream();
                    OutputStream output = client.getOutputStream();

                    // Server will read a byte from client and immediately write same byte back to client
                    // loop ends when the client signals EOF by calling shutdownOutput()
                    int b;
                    while ((b = input.read()) != -1) {
                        output.write(b);
                    }

                    // flush output to push out any buffered bytes
                    // shutdownOutput to signal the client that the server is not going to send it anymore data
                    output.flush();
                    client.shutdownOutput();

                } catch (IOException perClient) {
                    System.err.println(perClient);
                }
            }
        } catch (IOException ioe) {
            System.err.println("We caught an unexpected exception: " + ioe);
        }
    }
}
