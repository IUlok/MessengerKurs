import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RegistrationForm extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField repeatPasswordField;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    public RegistrationForm() {

        setTitle("Окно регистрации");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10)); // 3 строки, 2 столбца, отступы 10 пикселей

        // Добавление надписей и полей ввода
        add(new JLabel("Имя пользователя:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Повтор пароля:"));
        repeatPasswordField = new JPasswordField();
        add(passwordField);

        // Кнопка-текст "Войти в существующий аккаунт"
        JButton loginButton = new JButton("Войти в существующий аккаунт");
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.addActionListener(e -> {
            new AuthorizationForm();
            dispose();
        });
        add(loginButton);

        // Кнопка "Вход"
        JButton registerButton = new JButton("Вход");
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String repeatedPassword = new String(repeatPasswordField.getPassword());

            if(!password.equals(repeatedPassword)) {
                JOptionPane.showMessageDialog(RegistrationForm.this, "Пароли не совпадают!");
            }

            if (isValidRegistration(username, password)) { // Заглушка для проверки
                JOptionPane.showMessageDialog(RegistrationForm.this, "Вход успешен!");
                dispose(); // Закрытие окна
                ChatFrame chat = new ChatFrame(username);
            } else {
                JOptionPane.showMessageDialog(RegistrationForm.this, "Неверный логин или пароль.");
            }
        });
        registerButton.setBackground(new Color(49, 58, 68));
        registerButton.setForeground(Color.WHITE);
        add(registerButton);

        setLocationRelativeTo(null); // Центрирование окна
        setVisible(true);

        boolean connStatus = connectToServer();
        if(!connStatus) {
            // Заменить на диалоговое окно
            System.err.println("Ошибка подключения к серверу");
            dispose();
        }
    }

    private boolean connectToServer() {
        try {
            socket = new Socket(Main.address, Main.port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch(IOException e) {
            return false;
        }
        Main.setInputStream(in);
        Main.setOutputStream(out);
        Main.setSocket(socket);
        return true;
    }

    /* Попытка регистрации аккаунта username с паролем password.
    * При удачной регистрации возвращает true, иначе - false*/
    private boolean isValidRegistration(String username, String password) {
        String s = null;
        Scanner scanner = new Scanner(in);

        if (username.isBlank() || password.isBlank()) {
            return false;
        }
        try {
            out.write(("createUser " + username + " " + password + "\n").getBytes());
            out.flush();

            s = scanner.nextLine();
        }
        catch(IOException e2) {
            // Заменить на диалоговое окно
            System.out.println("Ошибка отправки запроса");
            System.out.println(e2.getMessage());
            scanner.close();
            return false;
        }

        if(s.equals("OK")) {
            return scanner.nextLine().equals("OK");
        } else {
            return false;
        }
    }
}
