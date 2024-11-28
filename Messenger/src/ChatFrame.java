import serializable.Message;
import serializable.User;
import javax.swing.Timer;

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
    //User currentUser;
    String myUser;
    boolean flag = false;
    ChatPanel chatPanel;
    private Timer timer;

    public ChatFrame (String username) {
        myUser = username;
        setTitle("ErroriestMsg");
        setSize(1000, 1000);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2, 0, 0)); // 1 строка, 2 столбца, отступы 0 пикселей
        try {
            out.write("getUsers\n".getBytes(StandardCharsets.UTF_8));
            out.flush();
            List<User> users;
            try {
                users = (List<User>) in.readObject();
                for (User user:users) {
                    if(user.getUserName().equals(myUser)) dlm.add(0, "Избранное");
                    else dlm.add(0, user.getUserName());
                }
                JList<String> userList = new JList<String>(dlm);
                userList.addListSelectionListener(e -> {
                    if(e.getValueIsAdjusting()) {
                        return;
                    }
                    String username1 = userList.getSelectedValue();
                    User user1 = null;
                    if(username1.equals("Избранное")) username1 = myUser;
                    for (User user:users) {
                        if (user.getUserName().equals(username1)) {
                            user1 = user;
                            break;
                        }
                    }
                    chatPanel.setToUser(user1);
                    chatPanel.getMessagesFromServer();
                    chatPanel.activate();
                });
                userList.setBackground(new Color(49, 58, 68));
                userList.setForeground(Color.WHITE);
                add(userList);
                chatPanel = new ChatPanel(this);
                add(chatPanel);

            } catch(ClassNotFoundException e) {
                System.err.println("ERROR: ошибка получения результата getUsers");
                return;
            }
        }
        catch (IOException e) {
            System.out.println("Ошибка установки соединения: " + e.getMessage());
        }
        setLocationRelativeTo(null); // Центрирование окна
        setVisible(true);
        timer = new Timer(1000, e -> {
            if (chatPanel.getToUser() == null) return;
            chatPanel.getMessagesFromServer();
            chatPanel.repaint();
        });
        timer.start();
    }
}