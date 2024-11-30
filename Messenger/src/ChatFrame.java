import serializable.Message;
import serializable.User;
import javax.swing.Timer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

class ChatFrame extends JFrame {
    Socket socket = Main.getSocket();
    ObjectOutputStream out = Main.getOutputStream();
    ObjectInputStream in = Main.getInputStream();
    DefaultListModel<String> dlm = new DefaultListModel<String>();
    DefaultListModel<String> dlmchat = new DefaultListModel<String>();
    String myUser;
    boolean flag = false;
    ChatPanel chatPanel;
    private Timer timer;
    public ChatFrame (String username) {
        myUser = username;
        setTitle("ErroriestMsg");
        setSize(1000, 1000);
        setResizable(false);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                try {
                    out.write("disconnect\n".getBytes());
                    out.close();
                    in.close();
                    socket.close();
                } catch (IOException ex) {
                    System.err.println("Исключение: " + ex.getMessage());
                }
                System.exit(0);
            }
        });
        setLayout(new GridLayout(1, 2, 0, 0)); // 1 строка, 2 столбца, отступы 0 пикселей
        // Создание и установка иконки на фрейм
        URL url = getClass().getResource("messengericon.png");
        ImageIcon icon = new ImageIcon(url);
        setIconImage(icon.getImage());
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
                userList.setFont(new Font("Courier", Font.BOLD, 30));
                userList.addListSelectionListener(e -> {
                    if(e.getValueIsAdjusting()) {
                        return;
                    }
                    String username1 = userList.getSelectedValue();
                    User necessaryUser = null;
                    if(username1.equals("Избранное")) username1 = myUser;
                    for (User user:users) {
                        if (user.getUserName().equals(username1)) {
                            necessaryUser = user;
                            break;
                        }
                    }
                    chatPanel.setToUser(necessaryUser);
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