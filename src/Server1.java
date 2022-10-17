import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Server1 {


    DatagramSocket socket;
    String[] datagramPackets = new String[1];
    String[] CheckDatagramPackets = new String[1];


    int a = -1;
    int b = 0;

    Timer timer = new Timer();


    Server1(DatagramSocket socket) {
        this.socket = socket;
    }


    public void data() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Timer(timer, socket);           //запуск таймер
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


                                    if (CheckDatagramPackets[0] != null) {
                                        for (int q = 0; q < CheckDatagramPackets.length; q++) {
                                            String[] p = CheckDatagramPackets[q].split("/");
                                            if (address.equals("/" + p[1])) {
                                                CheckDatagramPackets[q] = datagramPackets[i];
                                            } else {
                                                if (b < CheckDatagramPackets.length) {
                                                    CheckDatagramPackets[b] = datagramPackets[i];
                                                    System.out.println("Элемент списка для отправления конечных данных с индексом " + b + ": " + CheckDatagramPackets[b]);
                                                    b++;
                                                } else {
                                                    increaseCheckPacketArraySize();
                                                    CheckDatagramPackets[b] = datagramPackets[i];
                                                }
                                            }
                                        }
                                    } else {
                                        CheckDatagramPackets[b] = datagramPackets[i];
                                        System.out.println("Элемент списка для отправления конечных данных с индексом " + b + ": " + CheckDatagramPackets[b]);
                                        b++;
                                    }


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
                String[] q = datagramPackets[i].split("/");
                if (address.equals("/" + q[1])) {

                    //алгоритм получения строки нужного вида, для помещения ее в список всех, кто делал запрос к серверу (datagramPackets)
                    String tmp = "";
                    String[] s = str.split("/");
                    for (int j = 0; j < s.length; j++) {
                        if (!s[j].equals("YES")) {
                            if(!s[j].equals(s[s.length-2])) tmp = s[j] + "/";
                            else tmp = tmp + s[j];
                        }
                    }
                    datagramPackets[i] = q[1] + "/" + q[2] + "/" + tmp;
                }
            }


        } else {
            System.out.println("IP клиента: " + address);
            System.out.println("Порт клиента: " + port);
            System.out.println("Зарос клиента: " + str);
            PacketList(str, address, port, a);
        }
    }


    public void PacketList(String str, String address, String port, int a) {
        /*
        Данный метод добавляет пользователя в список клиентов, которые хоть раз обращались к серверу
        Для добавления в список клиента нужно привести данные к определенному виду, потому что в списке они будут храниться в виде - /ip/port/данные1/данные2/данные3 и т.д..
        (данные1, данные2, данные3 и т.д. - данные, которые хочет получать клиент, к примеру TIME, DATE и др.)
        Если список для пользователей переполнен, вызываем метод увеличения списка (increasePacketArraySize)
         */

        System.out.println("Работает метод добавления данных в список ->");
        int z = 0;
        String tmp = "";
        String[] s = str.split("/");
        for (int i = 0; i < s.length; i++) {
            if (s[i].equals("YES")) {        //если в запросе пользователя найдет индикатор подписки на рассылку (YES), то мы собираем новый элемент списка для рассылки
                z++;
            }   //тут потом нужно будет добавить вариант, если клиент не подписался на рассылку
        }
        if(z != 0) {
            for (int j = 0; j < s.length; j++) {
                if (!s[j].equals("YES")) {
                    if(!s[j].equals(s[s.length-2])) tmp = s[j] + "/";
                    else tmp = tmp + s[j];
                }
            }
        }
        if (a < datagramPackets.length) {
            datagramPackets[a] = address + "/" + port + "/" + tmp;
            System.out.println("Элемент списка с индексом " + a + ": " + datagramPackets[a]);
        } else {
            increasePacketArraySize(str, address, port, a);
        }
    }

    public void increasePacketArraySize(String str, String address, String port, int a) { //увеличение размера массива, хранящего пакеты клиентов
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
        PacketList(str, address, port, a);
    }


    public void increaseCheckPacketArraySize() {
        /*
        Данный метод увеличивает размер списка для отправки данных
        Так как действующий клиент не был добавлен в список по причине его переполненности, мы вызываем метод добавления клиента в список еще раз, передавая ему увеличенный список
         */

        System.out.println("Работает метод увеличения размера списка ->");
        String[] array = new String[CheckDatagramPackets.length + 10];
        for(int k = 0; k < CheckDatagramPackets.length; k++) {
            array[k] = CheckDatagramPackets[k];
        }
        CheckDatagramPackets = array;
    }





    public void Timer(Timer timer, DatagramSocket socket) throws InterruptedException {

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

                    System.out.println("Работает поток таймера!");
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

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        sendData(CheckDatagramPackets, socket);

                    }

                }
            }, 5000, 5000);

        } catch (Exception e) {
            System.out.println("Не получилось запустить поток таймера :(");
        }

    }


    public void sendData(String[] CheckDatagramPackets, DatagramSocket socket) {

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

                    for (int j = 0; j < s.length; j++) { //обрабатываем запрос каждого клиента списка
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
                                case "OS" -> {
                                    String os = System.getProperty("os.name");
                                    sendMessage = sendMessage + os + slash;
                                }
                                case "TIME" -> {
                                    LocalTime time = LocalTime.now();
                                    String str = time.toString();
                                    sendMessage = sendMessage + str + slash;
                                }
                                case "IP" -> {
                                    String ip = "";
                                    try {
                                        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                                        while (interfaces.hasMoreElements()) {
                                            NetworkInterface iface = interfaces.nextElement();
                                            if (iface.isLoopback() || !iface.isUp() || iface.isVirtual())
                                                continue;


                                            Enumeration<InetAddress> addresses = iface.getInetAddresses();
                                            while (addresses.hasMoreElements()) {
                                                InetAddress addr = addresses.nextElement();
                                                if (addr instanceof Inet4Address) {
                                                    ip = addr.getHostAddress();
                                                }
                                            }
                                        }
                                    } catch (SocketException e) {
                                        throw new RuntimeException(e);
                                    }

                                    sendMessage = sendMessage + ip + "/";
                                }
                                case "MEMORY" -> {
                                    String str = "";
                                    File[] roots = File.listRoots();
                                    for (int i = 0; i < roots.length; i++) {
                                        if(i < roots.length - 2) {
                                            str = roots[i].toString() + "-";
                                        } else str = roots[i].toString();
                                    }
                                    sendMessage = sendMessage + str + "/";
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
