import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
public static void main(String[] args) throws IOException {
    DatagramSocket socket = new DatagramSocket(5040);
    boolean running = true;
    byte[] buf = new byte[1024];

    GetAndSend getAndSend = new GetAndSend();
    getAndSend.Receive(socket, running, buf);
}
}


