import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Server {
public static void main(String[] args) throws  Exception {
    DatagramSocket socket = new DatagramSocket(5040);

    Server1 server1 = new Server1(socket);
    server1.data();
}



















}
