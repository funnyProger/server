import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Server {
public static void main(String[] args) throws  Exception {
    Server server = new Server();
    DatagramSocket socket = new DatagramSocket(5040);

    String[] datagramPackets = new String[1];
    String[] CheckDatagramPackets = new String[1];


    System.out.println("Запуск сервера :)");
    int a = -1;
    int b = 0;
    Timer timer = new Timer();
    server.Timer(datagramPackets, timer, socket, CheckDatagramPackets);           //запуск таймер


    while (true) {

        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        System.out.println("Ожидаем запрос на получение данных...");
        try {
            socket.receive(packet);                                                  //получение данных
            System.out.println("Данные получены :]");
            String string = new String(packet.getData(), 0, packet.getLength());
            InetAddress _address = packet.getAddress();
            String address = _address.toString();
            System.out.println("Запрос клиента: " + string);



            if (string.equals("ДА")) {
                for (int i = 0; i < datagramPackets.length; i++) {

                    String[] s = datagramPackets[i].split("/");
                    if (address.equals(s[1])) {
                        CheckDatagramPackets[b] = datagramPackets[i];
                        b++;
                    }
                }


            } else {
                a++;
                server.Receive(datagramPackets, server, a, packet);
            }


        } catch (Exception e) {
            System.out.println("Данные не получены :(");

        }


    }

}
    public void Receive(String[] datagramPackets, Server server, int a, DatagramPacket packet) {

        System.out.println("Работает метод проверки списка ->");
        String str = new String(packet.getData(), 0, packet.getLength());
        String address = packet.getAddress().toString();
        int _port = packet.getPort();
        String port = Integer.toString(_port);
        int indicator = 0;
        if (datagramPackets[0] != null) {
            for (int i = 0; i < datagramPackets.length; i++) {
                String[] s = datagramPackets[i].split("/");
                if (address.equals(s[1])) {
                    datagramPackets[i] = str;
                    indicator = i;
                }
            }
            if (indicator != 0) {
                datagramPackets[indicator] = str;
            } else {
                System.out.println("IP клиента: " + address);
                System.out.println("Порт клиента: " + port);
                System.out.println("Зарос клиента: " + str);
                server.PacketList(str, address, port, datagramPackets, a);
            }

        } else {
            System.out.println("IP клиента: " + address);
            System.out.println("Порт клиента: " + port);
            System.out.println("Зарос клиента: " + str);
            server.PacketList(str, address, port, datagramPackets, a);
        }
    }


    public void PacketList(String str, String address, String port, String[] datagramPackets, int a) {//добавляем клиента в список, кому отправлять периодические данные
    System.out.println("Работает метод добавления данных в список ->");
        String tmp = "";
        String[] s = str.split("/");
        for (int i = 0; i < s.length; i++) {
            if (s[i].equals("YES")) {                   //если в запросе пользователя найдет индикатор подписки на рассылку (YES), то мы собираем новый элемент списка для рассылки
                for (int j = 0; j < s.length; j++) {
                    if (!s[j].equals("YES")) {
                        if (j == s.length - 2) {
                            tmp = s[j];
                        } else {
                            tmp = s[j] + "/";
                        }
                    }
                }

            }   //тут потом нужно будет добавить вариант, если клиент не подписался на рассылку
        }
        if (a < datagramPackets.length) {
            datagramPackets[a] = address + "/" + port + "/" + tmp;
            System.out.println("Элемент списка с индексом " + a + ": " + datagramPackets[a]);
        } else {
            increasePacketArraySize(str, address, port, datagramPackets, a);
        }
    }

    public void increasePacketArraySize(String str, String address, String port, String[] datagramPackets, int a) { //увеличение размера массива, хранящего пакеты клиентов
    System.out.println("Работает метод увеличения размера списка ->");
        String[] array = new String[datagramPackets.length + 10];
        for(int k = 0; k < datagramPackets.length; k++) {
            array[k] = datagramPackets[k];
        }
        datagramPackets = array;
        PacketList(str, address, port, datagramPackets, a);
    }



    public void Timer(String[] datagramPackets, Timer timer, DatagramSocket socket, String[] CheckDatagramPackets) throws InterruptedException {
    System.out.println("Работает метод таймера ->");
    Thread.sleep(2000);

        try {
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {

                    System.out.println("Работает поток таймера");
                    if (datagramPackets[0] != null) {
                        System.out.println("Запуск алгоритма рассылки.");

                        for (int i = 0; i < datagramPackets.length; i++) {//перебираем клиентов
                            String[] s = datagramPackets[i].split("/");

                            try {
                                String str = "Живой";
                                byte[] mess = str.getBytes();
                                InetAddress checkAddress = InetAddress.getByName(s[1]);
                                int checkPort = Integer.parseInt(s[2]);
                                DatagramPacket checkPacket = new DatagramPacket(mess, mess.length, checkAddress, checkPort);
                                socket.send(checkPacket);

                            } catch (Exception e) {
                                System.out.println("Не удалось выполнить отправку данных клиенту :(");
                            }


                        }

                        if (CheckDatagramPackets[0] != null) {
                            for (int c = 0; c < CheckDatagramPackets.length; c++) {
                                String sendMessage = ""; //строка, которая будет содержать отправные данные вместе взятые
                                String slash = "/";
                                String[] s = CheckDatagramPackets[c].split("/");

                                for (int j = 0; j < s.length; j++) { //обрабатываем запрос каждого клиента
                                    if (!s[j].equals("")) { //проверка на пустую строку в массиве строк, содержащем запросы клиента
                                        switch (s[j]) {
                                            case "AGE" -> {
                                                Random random = new Random();
                                                int age = random.nextInt(100);
                                                String message = Integer.toString(age);
                                                sendMessage = sendMessage + message + slash;
                                            }
                                            case "DATE" -> {
                                                Date dateNow = new Date();
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTime(dateNow);
                                                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
                                                sendMessage = sendMessage + dateFormat.format(calendar.getTime()) + slash;
                                            }
                                            case "TIME" -> {
                                                Date date = new Date();
                                                String strDate = Integer.toString((int) date.getTime());
                                                sendMessage = sendMessage + strDate + slash;
                                            }

                                        }
                                    }
                                }


                                byte[] bytes = sendMessage.getBytes();
                                InetAddress address;
                                try {
                                    address = InetAddress.getByName(s[1]);
                                } catch (UnknownHostException e) {
                                    throw new RuntimeException(e);
                                }
                                int port = Integer.parseInt(s[2]);
                                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, address, port);
                                try {
                                    socket.send(datagramPacket);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                System.out.println("Рассылка для элемента списка " + c + " выполнена успешно: " + sendMessage);

                            }
                        }
                    }


                }
            }, 3000, 3000);

        } catch (Exception e) {
            System.out.println("Не получилось запустить поток таймера :(");
        }

    }

}
