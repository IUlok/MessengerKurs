import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class AuthorizationForm extends JFrame {
    ObjectOutputStream out;
    ObjectInputStream in;
    Socket socket;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public AuthorizationForm() {
        setTitle("Окно авторизации");
        setSize(500, 200);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10)); // 3 строки, 1 столбец, но как бы 2, отступы 10 пикселей
        // Добавление надписей и полей ввода
        JLabel nameLabel = new JLabel("Имя пользователя:");
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        add(nameLabel);
        usernameField = new JTextField();
        add(usernameField);

        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setHorizontalAlignment(JLabel.CENTER);
        add(passwordLabel);
        passwordField = new JPasswordField();
        add(passwordField);

        // Кнопка-текст "Создать пользователя"
        JButton createUserButton = new JButton("Создать пользователя");
        createUserButton.setBorderPainted(false);
        createUserButton.setContentAreaFilled(false);
        createUserButton.setForeground(Color.BLUE);
        createUserButton.addActionListener(e -> {
            //disconnectFromServer();
            new RegistrationForm(); // Открытие регистрационной формы
            dispose(); //Закрытие текущей формы
        });
        add(createUserButton);

        // Кнопка "Вход"
        JButton loginButton = new JButton("Вход");
        loginButton.addActionListener(e -> {
            checkLogin();
        });

        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) checkLogin();
            }
        });
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) checkLogin();
            }
        });

        loginButton.setBackground(new Color(49, 58, 68));
        loginButton.setForeground(Color.WHITE);
        add(loginButton);

        setLocationRelativeTo(null); // Центрирование окна
        setVisible(true);


    }

    // Подключение к серверу. При удачном подключении возвращает true, иначе - false
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

    private boolean disconnectFromServer() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch(IOException e) {
            return false;
        }
        return true;
    }

    // Заглушка для проверки учетных данных. ЗАМЕНИТЕ ЭТО на реальную аутентификацию.
    private boolean isValidLogin(String username, String password) {
        boolean connStatus = connectToServer();
        if(!connStatus) {
            // Заменить на диалоговое окно
            System.err.println("Ошибка подключения к серверу");
            dispose();
        }
        // Замените это на логику проверки учетных данных
        String s = null;
        if (username.isBlank() || password.isBlank()) {
            return false;
        }
        try {
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

    private void checkLogin(){
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Здесь должна быть реализована аутентификация
        // ... проверка username и password ...

        if (isValidLogin(username, password)) { // Заглушка для проверки
            JOptionPane.showMessageDialog(AuthorizationForm.this, "Вход успешен!");
            dispose(); // Закрытие окна
            ChatFrame chat = new ChatFrame(username);
        } else {
            JOptionPane.showMessageDialog(AuthorizationForm.this, "Неверный логин или пароль.");
            disconnectFromServer();
        }
    }
}
