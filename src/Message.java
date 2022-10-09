import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Message extends Thread{
    /*
    DatagramPacket packet1, packet2;
    String ip;
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
                        if(packet2 != null)
                        System.out.println("Данные успешно отправлены :)");
                        else System.out.println("Данные не отправлены");
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
            int port = packet1.getPort();
            str = new String(packet1.getData(), 0, packet1.getLength());
            if(str!=null) {
                System.out.println("Данные успешно получены :)");
                System.out.println("Ip: " + ip);
                System.out.println("Port: " + port);
                return str;
            }
        } catch (Exception e) {
            System.out.println("Ошибка получения данных :(");
            e.printStackTrace();
            return null;
        }
        return str;
    }

     */

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
                                DatagramPacket packet1 = new DatagramPacket(bytes, bytes.length, address, port);
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
