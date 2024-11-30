import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.net.URL;
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
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new GridLayout(4, 2, 10, 10)); // 2 строки, 4 столбца, отступы 10 пикселей
        // Создание и установка иконки на фрейм
        URL url = getClass().getResource("registrationicon.png");
        ImageIcon icon = new ImageIcon(url);
        setIconImage(icon.getImage());

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

        JLabel repeatPasswordLabel = new JLabel("Повтор пароля:");
        repeatPasswordLabel.setHorizontalAlignment(JLabel.CENTER);
        add(repeatPasswordLabel);
        repeatPasswordField = new JPasswordField();
        add(repeatPasswordField);

        // Кнопка-текст "Войти в существующий аккаунт"
        JButton loginButton = new JButton("Войти в существующий аккаунт");
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setForeground(Color.BLUE);
        loginButton.addActionListener(e -> {
            //disconnectFromServer();
            new AuthorizationForm();
            dispose();
        });
        add(loginButton);

        // Кнопка "Вход"
        JButton registerButton = new JButton("Зарегистрироваться");
        registerButton.addActionListener(e -> {
            checkRegistration();
        });
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) checkRegistration();
            }
        });
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) checkRegistration();
            }
        });
        repeatPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) checkRegistration();
            }
        });

        registerButton.setBackground(new Color(49, 58, 68));
        registerButton.setForeground(Color.WHITE);
        add(registerButton);

        setLocationRelativeTo(null); // Центрирование окна
        setVisible(true);

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

    /* Попытка регистрации аккаунта username с паролем password.
    * При удачной регистрации возвращает true, иначе - false*/
    private boolean isValidRegistration(String username, String password) {
        boolean connStatus = connectToServer();
        if(!connStatus) {
            // Заменить на диалоговое окно
            System.err.println("Ошибка подключения к серверу");
            dispose();
        }
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

    private void checkRegistration(){
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String repeatedPassword = new String(repeatPasswordField.getPassword());

        if(!password.equals(repeatedPassword)) {
            JOptionPane.showMessageDialog(RegistrationForm.this, "Пароли не совпадают!");
            disconnectFromServer();
            return;
        }

        if (isValidRegistration(username, password)) { // Заглушка для проверки
            JOptionPane.showMessageDialog(RegistrationForm.this, "Вуаля! Регистрация прошла успешно!");
            dispose(); // Закрытие окна
            ChatFrame chat = new ChatFrame(username);
        } else {
            JOptionPane.showMessageDialog(RegistrationForm.this, "Пользователь с таким именем уже зарегистрирован!!");
            disconnectFromServer();
        }
    }
}
