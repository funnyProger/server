import java.io.IOException;
import java.net.DatagramSocket;

public class Server {
public static void main(String[] args) throws IOException {
    DatagramSocket socket = new DatagramSocket(5040);
    boolean running = true;
    byte[] buf = new byte[1024];
    Message message = new Message();
    message.ReceiveAndSend(socket, running, buf);
}
}


