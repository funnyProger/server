import java.io.IOException;
import java.net.DatagramSocket;

public class Server {
public static void main(String[] args) throws IOException {
Message message = new Message();
boolean bool = true;

while (bool) {
    String str = message.getMessage();
    bool = message.sendMessage(str);
}
}
}


