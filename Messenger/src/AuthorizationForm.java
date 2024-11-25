import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class AuthorizationForm extends JFrame {
    public static final String address = "93.100.166.31";
    public static final int port = 23456;
    ObjectOutputStream out;
    ObjectInputStream in;
    Socket socket;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public AuthorizationForm() {
        setTitle("Окно авторизации");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10)); // 3 строки, 2 столбца, отступы 10 пикселей

        // Добавление надписей и полей ввода
        add(new JLabel("Имя пользователя:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        add(passwordField);

        // Кнопка "Вход"
        JButton loginButton = new JButton("Вход");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Здесь должна быть реализована аутентификация
                // ... проверка username и password ...

                if (isValidLogin(username, password)) { // Заглушка для проверки
                    JOptionPane.showMessageDialog(AuthorizationForm.this, "Вход успешен!");
                    dispose(); // Закрытие окна
                    ChatFrame chat = new ChatFrame(socket, in, out, username);
                } else {
                    JOptionPane.showMessageDialog(AuthorizationForm.this, "Неверный логин или пароль.");
                }
            }
        });
        add(new JLabel()); // Пустая метка для выравнивания
        loginButton.setBackground(new Color(49, 58, 68));
        loginButton.setForeground(Color.WHITE);
        add(loginButton);

        setLocationRelativeTo(null); // Центрирование окна
        setVisible(true);
    }

    // Заглушка для проверки учетных данных. ЗАМЕНИТЕ ЭТО на реальную аутентификацию.
    private boolean isValidLogin(String username, String password) {
        // Замените это на логику проверки учетных данных
        String s = null;
        if (username.isBlank() || password.isBlank()) {
            return false;
        }
        try {
            socket = new Socket(address, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.write(("login " + username + " " + password + "\n").getBytes());
            out.flush();

            Scanner scanner = new Scanner(in);
            s = scanner.next();

        }
        catch(OptionalDataException e2) {
            e2.printStackTrace();
            System.out.println(e2.length);
        }
        catch(IOException e2) {
            System.out.println("Ошибка установки соединения");
            e2.getMessage();
        }
        return s.equals("OK");
    }


    public static void main(String[] args) {
        new AuthorizationForm();
    }
}
