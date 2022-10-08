import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Message {
    DatagramPacket packet1, packet2;
    DatagramSocket socket;
    public Message() {
        try{
            socket = new DatagramSocket(5040);
        }catch (Exception e){
         e.printStackTrace();
        }
    }

    public boolean sendMessage(String _str) {
        if(socket != null & _str != null) {
            try {
                InetAddress ipClient = packet1.getAddress();
                switch (_str) {
                    case "Получить данные" -> {
                        String tmp = "Вы получили какие-то данные :)";
                        byte[] bytes = tmp.getBytes();
                        packet2 = new DatagramPacket(bytes, bytes.length, ipClient, 1234);
                        socket.send(packet2);
                        System.out.println("Данные успешно отправлены :)");
                        return true;
                    }
                    case "Привет" -> {
                        String tmp = "Приветствую, рады вам :)";
                        byte[] bytes = tmp.getBytes();
                        packet2 = new DatagramPacket(bytes, bytes.length, ipClient, 1234);
                        socket.send(packet2);
                        System.out.println("Данные успешно отправлены :)");
                        return true;
                    }
                    case "Закрыть сокет" -> {
                        String tmp = "Сокет сервера успешно закрыт :)";
                        byte[] bytes = tmp.getBytes();
                        packet2 = new DatagramPacket(bytes, bytes.length, ipClient, 1234);
                        socket.send(packet2);
                        System.out.println("Данные успешно отправлены :)");
                        socket.close();
                        return false;
                    }
                }
            }catch (Exception e) {
                System.out.println("Не удалось отправить данные :(");
                return false;
            }
        }
        return false;
    }

    public String getMessage() throws IOException {
        String str;

        byte[] data = new byte[1024];
        packet1 = new DatagramPacket(data, data.length);
        try {
            System.out.println("Пуск");
            socket.receive(packet1);
            String ip = packet1.getAddress().getHostAddress(); // Получить IP-адрес
            int port = packet1.getPort();
            str = new String(packet1.getData(), 0, packet1.getLength());
            System.out.println("Данные успешно получены :)");
            System.out.println("Ip: " + ip);
            System.out.println("Port: " + port);
            return str;
        } catch (Exception e) {
            System.out.println("Ошибка получения данных :(");
            e.printStackTrace();
            return null;
        }
    }
}
