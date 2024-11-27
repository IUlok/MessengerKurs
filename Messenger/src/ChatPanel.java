import serializable.Message;
import serializable.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChatPanel extends JPanel {
    private List<Message> messages;
    private String myUser;
    private User toUser;

    private JButton sendButton;
    private JTextField messagePole;

    private ObjectOutputStream out = Main.getOutputStream();
    private ObjectInputStream in = Main.getInputStream();

    public ChatPanel(ChatFrame chatFrame) {
        this.toUser = chatFrame.currentUser;
        this.myUser = chatFrame.myUser;

        messagePole = new JTextField();
        messagePole.setPreferredSize(new Dimension(350, 30));
        messagePole.setVisible(false);

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
            } catch(IOException ex) {
                messagePole.setText("");
            }
        });
        sendButton.setVisible(false);

        JPanel sendMessagePanel = new JPanel();

        MessagesPanel messagesPanel = new MessagesPanel();

        sendMessagePanel.add(messagePole);
        sendMessagePanel.add(sendButton);
        add(sendMessagePanel, BorderLayout.SOUTH);
        add(messagesPanel, BorderLayout.CENTER);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public void activate() {
        sendButton.setVisible(true);
        messagePole.setVisible(true);
    }
}

class MessagesPanel extends JPanel {

}