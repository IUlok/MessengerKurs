import serializable.Message;
import serializable.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class ChatPanel extends JPanel {
    private final String myUser;
    private User toUser;

    private final JButton sendButton;
    private final JTextField messagePole;

    private final MessagesPanel messagesPanel;
    private final JPanel sendMessagePanel;

    private final ObjectOutputStream out = Main.getOutputStream();
    private final ObjectInputStream in = Main.getInputStream();

    public ChatPanel(ChatFrame chatFrame) {
        this.myUser = chatFrame.myUser;
        messagePole = new JTextField();
        sendButton = new JButton("Отправить");
        messagePole.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) sendMessage();

            }
        });
        setLayout(new BorderLayout());
        messagePole.setPreferredSize(new Dimension(350, 30));
        sendButton.addActionListener(e -> sendMessage());
        messagesPanel = new MessagesPanel();

        sendMessagePanel = new JPanel();
        sendMessagePanel.setVisible(false);
        sendMessagePanel.add(messagePole);
        sendMessagePanel.add(sendButton);
        add(sendMessagePanel, BorderLayout.SOUTH);
        JScrollPane scrollbar = new JScrollPane(messagesPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollbar, BorderLayout.CENTER);
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

    private void sendMessage(){
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
            if(!response.equals("OK")) return;
            messagesPanel.getMessages().add(message);
        } catch(IOException ex) {
            messagePole.setText("");
        }
        messagesPanel.revalidate();
        messagesPanel.repaint();
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
                revalidate();
                repaint();
            } catch(ClassNotFoundException e) {
                System.err.println("ERROR: ошибка получения результата getMessagesInChat");
            }
        } catch (IOException ie) {
            System.err.println("Исключение IOException: " + ie.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Courier", Font.BOLD, 20));
        int currentX = 10;
        int currentY = 30;
        int dy = 30;
        if(messages != null) {
            for (Message msg: messages) {
                String messageText = msg.getSenderName() + ": " + msg.getText();
                g.setColor(new Color(49, 58, 68));
                g.fillRoundRect(5, currentY-20, messageText.length()*12, 25, 10, 10);
                g.setColor(Color.WHITE);
                g.drawString(messageText, currentX, currentY);
                currentY += dy;
            }
            setPreferredSize(new Dimension(getWidth(), currentY));
        }
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public List<Message> getMessages() {
        return messages;
    }
}