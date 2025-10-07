package echoserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.io.IOException;

// bats testing command:
// bats test/Echo_client.bats
// bats test/Echo_server_and_client.bats

public class EchoClient {
    // Use socket 6013 for this lab
    public static final int portNumber = 6013;

    public static void main(String[] args) throws IOException {
        // Default to localhost if no server is specified
        // if args.length == 0 then server = "127.0.0.1" else server = args[0]
        String server = (args.length == 0) ? "127.0.0.1" : args[0];

        // connect to the server/port
        // use try to auto-close the socket when done
        try (Socket socket = new Socket(server, portNumber)) {
            // keep as byte-based streams no text
            // It works as: System.input -> (client) -> socket -> (server) -> socket -> (client) -> System.output
            InputStream input = System.in;
            OutputStream output = System.out;
            InputStream sockIn = socket.getInputStream();
            OutputStream sockOut = socket.getOutputStream();

            // Read one byte from input, write one byte to the server
            int b;
            while ((b = input.read()) != -1) {
                sockOut.write(b);
            }

            // after write all byte to server, flush to push out any buffered bytes
            // shutdownOutput to signal the server that the client is not going to sent it anymore data
            sockOut.flush();
            socket.shutdownOutput();

            // receive echoed bytes from server and write to output
            // Read echoed bytes from server
            // !!! small note that the server does not wait to receive all byte from client before it starts sending back
            // !!! it starts sending back as soon as it receives a byte (start echoes as it reads)
            // write one byte to output at a time
            while ((b = sockIn.read()) != -1) {
                output.write(b);
            }
            
            // flush output to push out any buffered bytes
            output.flush();

            // provide some minimal error handling
        } catch (ConnectException ce) {
            System.err.println("We were unable to connect to " + server);
            System.err.println("You should make sure the server is running.");
        } catch (IOException ioe) {
            System.err.println("Unexpected I/O exception: " + ioe);
        }
    }
}
