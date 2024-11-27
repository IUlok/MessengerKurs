import serializable.Message;
import serializable.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class ChatPanel extends JPanel {
    private String myUser;
    private User toUser;

    private JButton sendButton;
    private JTextField messagePole;

    private MessagesPanel messagesPanel;
    private JPanel sendMessagePanel;

    private final ObjectOutputStream out = Main.getOutputStream();
    private final ObjectInputStream in = Main.getInputStream();

    public ChatPanel(ChatFrame chatFrame) {
        this.myUser = chatFrame.myUser;

        messagePole = new JTextField();
        messagePole.setPreferredSize(new Dimension(350, 30));

        setLayout(new BorderLayout());

        sendButton = new JButton("Отправить");

        sendButton.addActionListener(e -> {
            try {
                String msg = messagePole.getText();
                Message message = new Message(myUser, toUser.getUserName(), msg);
                out.write("sendMessage\n".getBytes(StandardCharsets.UTF_8));
                out.flush();
                out.writeObject(message);
                out.flush();
                messagePole.setText("");
                Scanner sc = new Scanner(in);
                String response = sc.nextLine();
                if(!response.equals("OK")) System.out.println("НЕ ОКЕЙ!!!!!!!!");
            } catch(IOException ex) {
                messagePole.setText("");
            }
        });

        sendMessagePanel = new JPanel();
        sendMessagePanel.setVisible(false);

        messagesPanel = new MessagesPanel();

        sendMessagePanel.add(messagePole);
        sendMessagePanel.add(sendButton);
        add(sendMessagePanel, BorderLayout.SOUTH);
        add(messagesPanel, BorderLayout.CENTER);
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
        messagesPanel.setToUser(toUser);
    }

    public void activate() {
        sendMessagePanel.setVisible(true);
    }

    public void getMessagesFromServer() {
        messagesPanel.getMessagesFromServer();
    }
}

class MessagesPanel extends JPanel {

    private List<Message> messages;
    private User toUser;

    private final ObjectOutputStream out = Main.getOutputStream();
    private final ObjectInputStream in = Main.getInputStream();

    public void getMessagesFromServer() {
        try {
            out.write("getMessagesInChat\n".getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.writeObject(toUser.getUserID());
            out.flush();

            try {
                messages = (List<Message>) in.readObject();
                System.out.println(messages);
                repaint();
            } catch(ClassNotFoundException e1) {
                System.err.println("ERROR: ошибка получения результата getMessagesInChat");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
}