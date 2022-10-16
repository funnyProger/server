import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.*;

public class Server1 {
    Server server = new Server();


    DatagramSocket socket;
    String[] datagramPackets = new String[1];
    String[] CheckDatagramPackets = new String[10];

    int a = -1;
    int b = 0;

    Timer timer = new Timer();



    Server1(DatagramSocket socket) {
        this.socket = socket;
    }


    public void data() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Timer(datagramPackets, timer, socket, CheckDatagramPackets);           //запуск таймер
                } catch (InterruptedException e) {
                    System.out.println("Не удалось запустить таймер :(");
                }
                while (true) {

                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);

                    System.out.println("Ожидаем запрос на получение данных...");
                    try {
                        socket.receive(packet);                                                  //получение любых данных
                        System.out.println("Данные получены :]");
                        String string = new String(packet.getData(), 0, packet.getLength());
                        InetAddress _address = packet.getAddress();
                        String address = _address.toString();
                        System.out.println("Запрос клиента: " + string);



                        if (string.equals("ДА")) {
                            for (int i = 0; i < datagramPackets.length; i++) {

                                String[] s = datagramPackets[i].split("/");
                                if (address.equals("/" + s[1])) {
                                    CheckDatagramPackets[b] = datagramPackets[i];
                                    System.out.println("Элемент списка для отправления конечных данных с индексом "  + b + ": " + CheckDatagramPackets[b]);
                                    b++;
                                }
                            }

                        } else {
                            a++;
                            Receive(datagramPackets, a, packet);
                        }


                    } catch (Exception e) {
                        System.out.println("Данные не получены :(");

                    }
                }

            }
        }).start();
    }

    public void Receive(String[] datagramPackets, int a, DatagramPacket packet) {

        System.out.println("Работает метод проверки списка ->");
        String str = new String(packet.getData(), 0, packet.getLength());
        String address = packet.getAddress().toString();
        int _port = packet.getPort();
        String port = Integer.toString(_port);

        /*
        Кусок кода до конца этого методы описывает алгоритм заполнения списка клиентов, кто хоть раз обращался к серверу.
        Если клиент обращается к серверу первый раз, то он добавляется в список как новый пользователь;
        Если этот клиент уже обращался к серверу, то элемент списка с таким же ip меняется на новый
         */


        if (datagramPackets[0] != null) {
            for (int i = 0; i < datagramPackets.length; i++) {
                String[] s = datagramPackets[i].split("/");
                if (address.equals("/" + s[1])) {

                    //алгоритм получения строки нужного вида, для помещения ее в список всех, кто делал запрос к серверу (datagramPackets)
                    String tmp = "";
                    String[] k = str.split("/");
                    for (int d = 0; d < k.length; d++) {
                        if (k[d].equals("YES")) {      //если в запросе пользователя найдется индикатор подписки на рассылку (YES), то мы собираем новый элемент списка для рассылки
                            for (int j = 0; j < k.length; j++) {
                                if (!k[j].equals("YES")) {
                                    if (j == k.length - 2) {
                                        tmp = k[j];
                                    } else {
                                        tmp = k[j] + "/";
                                    }
                                }
                            }

                        }
                    }

                    datagramPackets[i] = s[1] + "/" + s[2] + tmp;
                }
            }


        } else {
            System.out.println("IP клиента: " + address);
            System.out.println("Порт клиента: " + port);
            System.out.println("Зарос клиента: " + str);
            PacketList(str, address, port, datagramPackets, a);
        }
    }


    public void PacketList(String str, String address, String port, String[] datagramPackets, int a) {
        /*
        Данный метод добавляет пользователя в список клиентов, которые хоть раз обращались к серверу
        Для добавления в список клиента нужно привести данные к определенному виду, потому что в списке они будут храниться в виде - /ip/port/данные1/данные2/данные3 и т.д..
        (данные1, данные2, данные3 и т.д. - данные, которые хочет получать клиент, к примеру TIME, DATE и др.)
        Если список для пользователей переполнен, вызываем метод увеличения списка (increasePacketArraySize)
         */

        System.out.println("Работает метод добавления данных в список ->");
        String tmp = "";
        String[] s = str.split("/");
        for (int i = 0; i < s.length; i++) {
            if (s[i].equals("YES")) {        //если в запросе пользователя найдет индикатор подписки на рассылку (YES), то мы собираем новый элемент списка для рассылки
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
        /*
        Данный метод увеличивает размер общего списка
        Так как действующий клиент не был добавлен в список по причине его переполненности, мы вызываем метод добавления клиента в список еще раз, передавая ему увеличенный список
         */

        System.out.println("Работает метод увеличения размера списка ->");
        String[] array = new String[datagramPackets.length + 10];
        for(int k = 0; k < datagramPackets.length; k++) {
            array[k] = datagramPackets[k];
        }
        datagramPackets = array;
        PacketList(str, address, port, datagramPackets, a);
    }



    public void Timer(String[] datagramPackets, Timer timer, DatagramSocket socket, String[] CheckDatagramPackets) throws InterruptedException {

        /*
        Данный метод запуская таймер, в котором каждые 3000 мили секунд (3 сек) срабатывает проверка связи с клиентами из общего списка (datagramPackets)
        Если клиент ответил, на запрос сервера "Живой" (ответ "ДА"), то мы добавляем этого клиента в новый список (CheckDatagramPackets), если не ответил, не добавляем
        Затем мы вызываем метод, отвечающий за отправку конечных данных
         */

        System.out.println("Работает метод таймера ->");

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
                                System.out.println("Не удалось выполнить запрос к клиенту :(");
                            }
                        }

                        System.out.println("Завершился опрос всех пользователей!");

                        sendData(CheckDatagramPackets, socket);
                        System.out.println("Вызван метод отправки данных активным пользователям");

                    }

                }
            }, 3000, 3000);

        } catch (Exception e) {
            System.out.println("Не получилось запустить поток таймера :(");
        }

    }


    public void sendData(String[] CheckDatagramPackets, DatagramSocket socket)  {

        /*
        Данный метод отвечает за отправку конечных данных пользователям из нового списка (CheckDatagramPackets)
        Для этого мы собираем строку из нужных данных и отправляем ее клиенту
        Клиент ее обработает нужным образом и выводит данные на экран телефона
         */


        if (CheckDatagramPackets[0] != null) {
            for (int c = 0; c < CheckDatagramPackets.length; c++) {
                if (CheckDatagramPackets[c] != null) {
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



}
