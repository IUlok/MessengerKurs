import serializable.Message;
import serializable.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

class ChatFrame extends JFrame {
    ObjectOutputStream out;
    ObjectInputStream in;
    DefaultListModel<String> dlm = new DefaultListModel<String>();

    public ChatFrame (Socket socket, ObjectInputStream in, ObjectOutputStream out, String username) {
        setTitle("Мессенджер");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2, 0, 0)); // 1 строки, 2 столбца, отступы 0 пикселей
        try {

            out.write("getUsers\n".getBytes(StandardCharsets.UTF_8));
            out.flush();

            List<User> users;

            try {
                users = (List<User>) in.readObject();
                for (User user:users) {
                    dlm.add(0, user.getUserName());
                }
                JList<String> list = new JList<String>(dlm);
                list.setBackground(new Color(22, 33, 43));
                list.setForeground(Color.WHITE);
                add(list);

                JList<String> listChats = new JList<String>();
                listChats.setBackground(new Color(14, 22, 33));
                add(listChats);

            } catch(ClassNotFoundException e) {
                System.err.println("ERROR: ошибка получения результата getUsers");
                return;
            }
        }
        catch (IOException e) {
            System.out.println("Ошибка установки соединения");
            e.printStackTrace();
        }
        setLocationRelativeTo(null); // Центрирование окна
        setVisible(true);
    }
}
