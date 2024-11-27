import serializable.Message;
import serializable.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

class ChatFrame extends JFrame {
    Socket socket = Main.getSocket();
    ObjectOutputStream out = Main.getOutputStream();
    ObjectInputStream in = Main.getInputStream();
    DefaultListModel<String> dlm = new DefaultListModel<String>();
    DefaultListModel<String> dlmchat = new DefaultListModel<String>();
    List<Message> messages;
    User currentUser;
    String myUser;
    boolean flag = false;
    ChatPanel chatPanel = new ChatPanel(this);

    public ChatFrame (String username) {
        myUser = username;
        setTitle("Мессенджер");
        setSize(1000, 1000);
        setResizable(false);
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
                list.addListSelectionListener(e -> {

                    if(e.getValueIsAdjusting()) {
                        return;
                    }

                    String username1 = list.getSelectedValue();
                    User user1 = null;
                    for (User user:users) {
                        if (user.getUserName().equals(username1)) {
                            user1 = user;
                            break;
                        }
                    }
                    currentUser = user1;
                    // Получение списка сообщений
                    getMessagesList(user1);
                    flag = true;
                    chatPanel.activate();
                });
                list.setBackground(new Color(49, 58, 68));
                list.setForeground(Color.WHITE);
                add(list);

                if (flag) {
                    for (Message message : messages) {
                        String out1 = message.getSenderName() + " : " + message.getText();
                        dlm.add(0, out1);
                    }
                }
                JList<String> listChats = new JList<String>(dlmchat);
                listChats.setBackground(Color.lightGray);

                add(chatPanel);

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

    private void getMessagesList (User user1) {
        try {
            out.write("getMessagesInChat\n".getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.writeObject(user1.getUserID());
            out.flush();

            try {
                messages = (List<Message>) in.readObject();
                System.out.println(messages);
            } catch(ClassNotFoundException e1) {
                System.err.println("ERROR: ошибка получения результата getMessagesInChat");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}