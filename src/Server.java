import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
public static void main(String[] args) throws IOException {
    Server server = new Server();
    DatagramSocket socket = new DatagramSocket(5040);
    boolean running = true;
    byte[] buf = new byte[1024];
    server.ReceiveAndSend(socket, running, buf);
}
    public void ReceiveAndSend(DatagramSocket socket, boolean running, byte[] buf) {
        System.out.println("Сервер запущен :]");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    System.out.println("Ожидаем новый запрос :]");
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(packet);
                        System.out.println("Данные получены :]");
                    } catch (IOException e) {
                        System.out.println("Данные не получены :(");
                        e.printStackTrace();
                    }
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    System.out.println("IP: " + address.toString());
                    System.out.println("PORT: " + port);
                    String str = new String(packet.getData(), 0, packet.getLength());

                    switch (str) {
                        case "Привет" -> {
                            String message = "Рады вам :]";
                            byte[] bytes = message.getBytes();
                            DatagramPacket packet1 = new DatagramPacket(bytes, bytes.length, address, 1234);
                            try {
                                socket.send(packet1);
                                System.out.println("Данные отправлены :]");

                            } catch (IOException e) {
                                System.out.println("Данные не отправлены :(");
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
        }).start();

    }


}


