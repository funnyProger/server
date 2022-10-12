import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GetAndSend {
    int a = 0;
    DatagramPacket[] datagramPackets; //массив пакетов данных пользователей


    public void Receive(DatagramSocket socket, boolean running, byte[] buf) {
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
                        PacketList(packet);
                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    Send(socket);
                                } catch (IOException e) {
                                    byte[] b = new byte[1024];
                                    System.out.println("Отправить данные не получилось :(");
                                    Receive(socket, true, b);
                                }
                            }
                        }, 5000, 5000);
                    } catch (IOException e) {
                        System.out.println("Данные не получены :(");
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void increasePacketArraySize() { //увеличение размера массива, хранящего пакеты данных клиентов
        DatagramPacket[] array = new DatagramPacket[datagramPackets.length + 10];
        for(int k = 0; k < datagramPackets.length; k++) {
            array[k] = datagramPackets[k];
        }
        datagramPackets = array;
    }

    public void Send(DatagramSocket socket) throws IOException {
        checkClientsConnection(socket);
        for(int i = 0; i < datagramPackets.length; i++) {
            if(datagramPackets[i] != null) {
                String str = new String(datagramPackets[i].getData(), 0, datagramPackets[i].getLength());
                String[] s = str.split("/");
                for(int j = 0; j < s.length; j++) {
                    switch (s[j]) {
                        case "AGE" -> {
                            Random random = new Random();
                            int age = random.nextInt(100);
                            String message = Integer.toString(age);
                            byte[] bytes = message.getBytes();
                            InetAddress address = datagramPackets[i].getAddress();
                            int port = datagramPackets[i].getPort();
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
        }


    }

    public void checkClientsConnection(DatagramSocket socket) throws IOException {
        for (int i = 0; i < datagramPackets.length; i++) {
            String message = "1";
            byte[] bytes = message.getBytes();
            InetAddress address = datagramPackets[i].getAddress();
            int port = datagramPackets[i].getPort();
            DatagramPacket packet1 = new DatagramPacket(bytes, bytes.length, address, port);
            try {
                socket.send(packet1);
                System.out.println("Рассылка отправлена :]");
            } catch (IOException e) {
                System.out.println("Рассылка не отправлена :(");
                e.printStackTrace();
            }

            byte[] b = new byte[1024];
            DatagramPacket packet2 = new DatagramPacket(b, b.length);
            socket.receive(packet2);
            String string = new String(packet2.getData(), 0, packet2.getLength());
            if(!string.equals(message)) {
                datagramPackets[i] = null;
            }

        }
    }


    public void PacketList(DatagramPacket packet) {
        datagramPackets[a] = packet;
        if(a < datagramPackets.length) a++;
        else {
            increasePacketArraySize();
            a++;
        }
    }





}
