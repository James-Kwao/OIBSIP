package Main.Display;

import Main.Database.DB;
import Main.utils.DateTimeInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Random;

import static Main.Display.MainFrame.SCREEN_SIZE;
import static java.lang.ClassLoader.getSystemClassLoader;

public class Display extends JPanel {
    private static final Image train;
    private static Image loginImg;
    private static Image signupImg;
    private static Image signUpBackgroundImg;
    private static Image loginBackgroundImg;
    private static Image bookingBackground;
    private static Image toggleVisible;
    private static Image toggleInvisible;
    private static Image logo;

    static {
        try {
            signUpBackgroundImg = ImageIO.read(Objects.requireNonNull(getSystemClassLoader().getResource("res/background.jpg")));
            loginBackgroundImg = ImageIO.read(Objects.requireNonNull(getSystemClassLoader().getResource("res/background2.jpg")));
            bookingBackground = ImageIO.read(Objects.requireNonNull(getSystemClassLoader().getResource("res/background3.jpg")));
            loginImg = ImageIO.read(Objects.requireNonNull(getSystemClassLoader().getResource("res/utils/login.png")));
            signupImg = ImageIO.read(Objects.requireNonNull(getSystemClassLoader().getResource("res/utils/Signup.png")));
            toggleVisible = ImageIO.read(Objects.requireNonNull(getSystemClassLoader().getResource("res/utils/visible.png")));
            toggleInvisible = ImageIO.read(Objects.requireNonNull(getSystemClassLoader().getResource("res/utils/invisible.png")));
            logo = ImageIO.read(Objects.requireNonNull(getSystemClassLoader().getResource("res/utils/logo.png")));
            train = ImageIO.read(Objects.requireNonNull(getSystemClassLoader().getResource("res/utils/train.png")));

            {
                signUpBackgroundImg = signUpBackgroundImg.getScaledInstance(SCREEN_SIZE.width, SCREEN_SIZE.height, Image.SCALE_SMOOTH);
                loginBackgroundImg = loginBackgroundImg.getScaledInstance(SCREEN_SIZE.width, SCREEN_SIZE.height, Image.SCALE_SMOOTH);
                bookingBackground = bookingBackground.getScaledInstance(SCREEN_SIZE.width, SCREEN_SIZE.height, Image.SCALE_SMOOTH);
                toggleInvisible = toggleInvisible.getScaledInstance(18, 15, Image.SCALE_SMOOTH);
                toggleVisible = toggleVisible.getScaledInstance(18, 15, Image.SCALE_SMOOTH);
                loginImg = loginImg.getScaledInstance(loginImg.getWidth(null) - 60,
                        loginImg.getHeight(null) - 20, Image.SCALE_SMOOTH);
                signupImg = signupImg.getScaledInstance(signupImg.getWidth(null) - 60,
                        signupImg.getHeight(null) - 20, Image.SCALE_SMOOTH);
                logo = logo.getScaledInstance(logo.getWidth(null) - 30,
                        logo.getHeight(null) - 40, Image.SCALE_SMOOTH);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final DB database;
    private final int[] availableTrainNumbers;
    private final DateTimeInterface dateTimeInterface;
    private final String[] availableTrainNames;
    private SuperAdminInterface adminDisplay;
    private RoundRectangle2D hoverRec, submitBtn, loginBtn, rememberMe, booked;
    private RoundRectangle2D.Float firstName, lastName, email, password, confirmPassword, username, trainClass,
            departureDate, departureTime, toDestination;
    private JLabel logIn, signUP;
    private Color hoverColor, firstNameClr, lastNameClr, emailClr, passwordClr, confirmPasswordClr,
            usernameClr, buttonColor, rememberMeClr, toggleClr, toDestinationClr;
    private Font font;
    private boolean firstNameBool, lastNameBool, emailBool, passwordBool, confirmPasswordBool,
            usernameBool, shouldRemember, isToggled, toDestinationBool, timeDepartureBool, dateDepartureBool;
    private JTextField firstNameField, lastNameField, emailField, usernameField, toDestinationField,
            hour, min, year, month, day;
    private JPasswordField passwordField, confirmPasswordField;
    private char display, userClass;
    private String dateToDepart, timeToDepart, trainName;
    private int mouseX, mouseY, animate, x_pos;
    private Rectangle2D.Float toggleRec, rememberMeBound;

    public Display() {

        display = 'L';
        userClass = 'A';
        availableTrainNumbers = new int[]{10257, 10057, 55017, 79104, 10025, 99001, 47300, 45050, 80705, 60821, 56042};
        availableTrainNames = new String[]{"Belmond Royal Scotsman", "Shangri-La Express", "Kyushu Seven Star", "Maharajas Express",
                "Indian Pacific", "Golden Arrow", "Flying Scotsman", "Orient Express", "Bullet Train", "Blue Train", "Eurostar"};
        dateToDepart = LocalDate.now().getDayOfMonth() + "\\" + LocalDate.now().getMonth().toString() + "\\" + LocalDate.now().getYear();
        timeToDepart = LocalTime.now().getHour() + ":" + LocalTime.now().getMinute();
        database = new DB("ticketReservation");
        dateTimeInterface = new DateTimeInterface();
        adminDisplay = new SuperAdminInterface(database);

        setLayout(null);
        setBounds(0, 0, getToolkit().getScreenSize().width, getToolkit().getScreenSize().height);
        setDoubleBuffered(true);
        animate = 1;
        x_pos = 210;


        hoverRec = new RoundRectangle2D.Float();

        firstNameClr = lastNameClr = emailClr
                = passwordClr = confirmPasswordClr = usernameClr = new Color(0x063747);
        font = new Font("Book Antiqua", Font.PLAIN, 20);

        if (display == 'S') {
            hoverColor = new Color(0x8E9294);
            toggleClr = new Color(0x2FCAA6);
            initializeSignupComponents();
        } else if (display == 'L') {
            toggleClr = new Color(0xBE1B86);
            rememberMeClr = hoverColor = new Color(0x2FCAA6);
            initializeLoginComponents();
        } else if (display == 'R') {
            initializeReservationComponents();
        }

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                if (display == 'L') {
                    if (rememberMe != null && rememberMe.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else if (rememberMeBound != null && rememberMeBound.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else if (signUP != null && signUP.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else if (loginBtn != null && loginBtn.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else if (toggleRec != null && toggleRec.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else setCursor(Cursor.getDefaultCursor());
                } else if (display == 'S') {
                    if (submitBtn != null && submitBtn.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else if (logIn != null && logIn.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else if (toggleRec != null && toggleRec.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else if (rememberMeBound != null && rememberMeBound.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else setCursor(Cursor.getDefaultCursor());
                } else if (display == 'R') {
                    if (booked != null && booked.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else if (loginBtn != null && loginBtn.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else if (submitBtn != null && submitBtn.getBounds().contains(e.getPoint()))
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (display == 'S') {
                    if (rememberMeBound.getBounds().contains(e.getPoint()) || toggleRec.getBounds().contains(e.getPoint())) {
                        isToggled = !isToggled;
                        char[] echoChars = {passwordField.getEchoChar(), confirmPasswordField.getEchoChar()};
                        for (char c : echoChars) {
                            if (c == '*') {
                                passwordField.setEchoChar((char) 0);
                                confirmPasswordField.setEchoChar((char) 0);
                            } else {
                                passwordField.setEchoChar('*');
                                confirmPasswordField.setEchoChar('*');
                            }
                        }
                    }
                    if (firstName != null && firstName.getBounds().contains(e.getPoint())) {
                        if (firstNameField.getText().isBlank())
                            firstNameBool = !firstNameBool;
                        else {
                            firstNameField.setFocusable(true);
                            firstNameField.setRequestFocusEnabled(true);
                            firstNameField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (firstNameBool) {
                            firstNameField.setVisible(true);
                            firstNameField.setEnabled(true);
                            firstNameField.requestFocusInWindow();
                            firstNameClr = new Color(0x12A3D2);
                        } else {
                            firstNameField.setVisible(false);
                            firstNameClr = new Color(0x063747);
                            firstNameField.setBackground(firstNameClr);
                        }
                    } else {
                        if (firstNameField != null && firstNameField.getText().isBlank()) {
                            firstNameBool = false;
                            firstNameField.setVisible(false);
                            firstNameClr = new Color(0x063747);
                            firstNameField.setBackground(firstNameClr);
                        } else {
                            if (firstNameField != null) {
                                firstNameField.setFocusable(false);
                                firstNameField.setRequestFocusEnabled(false);
                                firstNameField.setBackground(new Color(0x35464B));
                                firstNameField.setHorizontalAlignment(JTextField.LEADING);
                            }
                        }
                    }
                    if (lastName != null && lastName.getBounds().contains(e.getPoint())) {
                        if (lastNameField.getText().isBlank())
                            lastNameBool = !lastNameBool;
                        else {
                            lastNameField.setFocusable(true);
                            lastNameField.setRequestFocusEnabled(true);
                            lastNameField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (lastNameBool) {
                            lastNameField.setVisible(true);
                            lastNameField.setEnabled(true);
                            lastNameField.requestFocusInWindow();
                            lastNameClr = new Color(0x12A3D2);
                        } else {
                            lastNameField.setVisible(false);
                            lastNameClr = new Color(0x063747);
                            lastNameField.setBackground(lastNameClr);
                        }
                    } else {
                        if (lastNameField != null && lastNameField.getText().isBlank()) {
                            lastNameBool = false;
                            lastNameField.setVisible(false);
                            lastNameClr = new Color(0x063747);
                            lastNameField.setBackground(lastNameClr);
                        } else {
                            if (lastNameField != null) {
                                lastNameField.setFocusable(false);
                                lastNameField.setRequestFocusEnabled(false);
                                lastNameField.setBackground(new Color(0x35464B));
                                lastNameField.setHorizontalAlignment(JTextField.LEADING);
                            }
                        }
                    }
                    if (email != null && email.getBounds().contains(e.getPoint())) {
                        if (emailField.getText().isBlank())
                            emailBool = !emailBool;
                        else {
                            emailField.setFocusable(true);
                            emailField.setRequestFocusEnabled(true);
                            emailField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (emailBool) {
                            emailField.setVisible(true);
                            emailField.setEnabled(true);
                            emailField.requestFocusInWindow();
                            emailClr = new Color(0x12A3D2);
                        } else {
                            emailField.setVisible(false);
                            emailClr = new Color(0x063747);
                            emailField.setBackground(emailClr);
                        }
                    } else {
                        if (emailField.getText().isBlank()) {
                            emailBool = false;
                            emailField.setVisible(false);
                            emailClr = new Color(0x063747);
                            emailField.setBackground(emailClr);
                        } else {
                            emailField.setFocusable(false);
                            emailField.setRequestFocusEnabled(false);
                            emailField.setBackground(new Color(0x35464B));
                            emailField.setHorizontalAlignment(JTextField.LEADING);
                        }
                    }
                    if (password != null && password.getBounds().contains(e.getPoint()) &&
                            !toggleRec.getBounds().contains(e.getPoint())) {
                        if (String.valueOf(passwordField.getPassword()).isBlank())
                            passwordBool = !passwordBool;
                        else {
                            passwordField.setFocusable(true);
                            passwordField.setRequestFocusEnabled(true);
                            passwordField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (passwordBool) {
                            passwordField.setVisible(true);
                            passwordField.setEnabled(true);
                            passwordField.requestFocusInWindow();
                            passwordClr = new Color(0x12A3D2);
                        } else {
                            passwordField.setVisible(false);
                            passwordClr = new Color(0x063747);
                            passwordField.setBackground(passwordClr);
                        }
                    } else {
                        if (String.valueOf(passwordField.getPassword()).isBlank()) {
                            passwordBool = false;
                            passwordField.setVisible(false);
                            passwordClr = new Color(0x063747);
                            passwordField.setBackground(passwordClr);
                        } else {
                            passwordField.setFocusable(false);
                            passwordField.setRequestFocusEnabled(false);
                            passwordField.setBackground(new Color(0x35464B));
                            passwordField.setHorizontalAlignment(JTextField.LEADING);
                        }
                    }
                    if (confirmPassword != null && confirmPassword.getBounds().contains(e.getPoint()) &&
                            !rememberMeBound.getBounds().contains(e.getPoint())) {
                        if (confirmPasswordField != null && String.valueOf(confirmPasswordField.getPassword()).isBlank())
                            confirmPasswordBool = !confirmPasswordBool;
                        else {
                            assert confirmPasswordField != null;
                            confirmPasswordField.setFocusable(true);
                            confirmPasswordField.setRequestFocusEnabled(true);
                            confirmPasswordField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (confirmPasswordBool) {
                            confirmPasswordField.setVisible(true);
                            confirmPasswordField.setEnabled(true);
                            confirmPasswordField.requestFocusInWindow();
                            confirmPasswordClr = new Color(0x12A3D2);
                        } else {
                            confirmPasswordField.setVisible(false);
                            confirmPasswordClr = new Color(0x063747);
                            confirmPasswordField.setBackground(confirmPasswordClr);
                        }
                    } else {
                        if (confirmPasswordField != null && String.valueOf(confirmPasswordField.getPassword()).isBlank()) {
                            confirmPasswordBool = false;
                            confirmPasswordField.setVisible(false);
                            confirmPasswordClr = new Color(0x063747);
                            confirmPasswordField.setBackground(confirmPasswordClr);
                        } else {
                            if (confirmPasswordField != null) {
                                confirmPasswordField.setFocusable(false);
                                confirmPasswordField.setRequestFocusEnabled(false);
                                confirmPasswordField.setBackground(new Color(0x35464B));
                                confirmPasswordField.setHorizontalAlignment(JTextField.LEADING);
                            }
                        }
                    }
                    if (username != null && username.getBounds().contains(e.getPoint())) {
                        if (usernameField.getText().isBlank())
                            usernameBool = !usernameBool;
                        else {
                            usernameField.setFocusable(true);
                            usernameField.setRequestFocusEnabled(true);
                            usernameField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (usernameBool) {
                            usernameField.setVisible(true);
                            usernameField.setEnabled(true);
                            usernameField.requestFocusInWindow();
                            usernameClr = new Color(0x12A3D2);
                        } else {
                            usernameField.setVisible(false);
                            usernameClr = new Color(0x063747);
                            usernameField.setBackground(usernameClr);
                        }
                    } else {
                        if (usernameField != null && usernameField.getText().isBlank()) {
                            usernameBool = false;
                            usernameField.setVisible(false);
                            usernameClr = new Color(0x063747);
                            usernameField.setBackground(usernameClr);
                        } else {
                            if (usernameField != null) {
                                usernameField.setFocusable(false);
                                usernameField.setRequestFocusEnabled(false);
                                usernameField.setBackground(new Color(0x35464B));
                                usernameField.setHorizontalAlignment(JTextField.LEADING);
                            }
                        }
                    }
                    if (logIn != null && logIn.getBounds().contains(e.getPoint())) {
                        signupNullified();
                        toggleClr = new Color(0xBE1B86);
                        isToggled = false;
                        display = 'L';
                        setCursor(Cursor.getDefaultCursor());
                    }
                    if (submitBtn != null && submitBtn.getBounds().contains(e.getPoint())) {
                        if (!firstNameField.getText().isBlank() && !lastNameField.getText().isBlank() &&
                                !emailField.getText().isBlank() && !String.valueOf(passwordField.getPassword()).isBlank() &&
                                !String.valueOf(confirmPasswordField.getPassword()).isBlank() && !usernameField.getText().isBlank()) {
                            //check if password and confirm password matches
                            if (String.valueOf(passwordField.getPassword()).matches(String.valueOf(confirmPasswordField.getPassword()))) {
                                firstNameClr = lastNameClr = emailClr = passwordClr = confirmPasswordClr =
                                        new Color(0x12A3D2);
                                int result = database.insert(firstNameField.getText().strip(), lastNameField.getText().strip(),
                                        emailField.getText().strip(), String.valueOf(passwordField.getPassword()),
                                        usernameField.getText().strip());
                                if (result == 0) {
                                    signupNullified();
                                    display = 'R';
                                } else {
                                    firstNameClr = Color.RED;
                                    lastNameClr = Color.RED;
                                    emailClr = Color.RED;
                                    passwordClr = Color.RED;
                                    confirmPasswordClr = Color.RED;
                                    usernameClr = Color.RED;
                                    JOptionPane optionPane = new JOptionPane(new JLabel(
                                            "User already exist, login instead."),
                                            JOptionPane.ERROR_MESSAGE,
                                            JOptionPane.OK_CANCEL_OPTION);
                                    JDialog dialog = optionPane.createDialog(getParent(), "Username or email has been taken");
                                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                                    dialog.setResizable(false);
                                    dialog.setVisible(true);
                                    if (optionPane.getValue() != null && optionPane.getValue().equals(JOptionPane.OK_OPTION)) {
                                        signupNullified();
                                        display = 'L';
                                    }
                                }
                                setCursor(Cursor.getDefaultCursor());
                            } else {
                                passwordClr = Color.RED;
                                confirmPasswordClr = Color.RED;
                            }
                        }
                        if (firstNameField != null && firstNameField.getText().isBlank()) firstNameClr = Color.RED;
                        if (lastNameField != null && lastNameField.getText().isBlank()) lastNameClr = Color.RED;
                        if (emailField != null && emailField.getText().isBlank()) emailClr = Color.RED;
                        if (passwordField != null && String.valueOf(passwordField.getPassword()).isBlank())
                            passwordClr = Color.RED;
                        if (confirmPasswordField != null && String.valueOf(confirmPasswordField.getPassword()).isBlank())
                            confirmPasswordClr = Color.RED;
                        if (usernameField != null && usernameField.getText().isBlank()) usernameClr = Color.RED;
                    }
                }

                if (display == 'L') {
                    //set controls for email
                    if (email != null && email.getBounds().contains(e.getPoint())) {
                        if (emailField.getText().isBlank())
                            emailBool = !emailBool;
                        else {
                            emailField.setFocusable(true);
                            emailField.setRequestFocusEnabled(true);
                            emailField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (emailBool) {
                            emailField.setVisible(true);
                            emailField.setEnabled(true);
                            emailField.requestFocusInWindow();
                            emailClr = new Color(0x2FCAA6);
                        } else {
                            emailField.setVisible(false);
                            emailClr = Color.BLACK;
                            emailField.setBackground(emailClr);
                        }
                    } else {
                        if (emailField != null && emailField.getText().isBlank()) {
                            emailBool = false;
                            emailField.setVisible(false);
                            emailClr = Color.BLACK;
                            emailField.setBackground(emailClr);
                        } else {
                            if (emailField != null) {
                                emailField.setFocusable(false);
                                emailField.setRequestFocusEnabled(false);
                                emailField.setBackground(new Color(0x35464B));
                                emailField.setHorizontalAlignment(JTextField.LEADING);
                            }
                        }
                    }
                    //set controls for password
                    if (password != null && password.getBounds().contains(e.getPoint()) &&
                            !toggleRec.getBounds().contains(e.getPoint())) {
                        if (String.valueOf(passwordField.getPassword()).isBlank())
                            passwordBool = !passwordBool;
                        else {
                            passwordField.setFocusable(true);
                            passwordField.setRequestFocusEnabled(true);
                            passwordField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (passwordBool) {
                            passwordField.setVisible(true);
                            passwordField.setEnabled(true);
                            passwordField.requestFocusInWindow();
                            passwordClr = new Color(0x2FCAA6);
                        } else {
                            passwordField.setVisible(false);
                            passwordClr = Color.BLACK;
                            passwordField.setBackground(passwordClr);
                        }
                    } else {
                        if (passwordField != null && String.valueOf(passwordField.getPassword()).isBlank()) {
                            passwordBool = false;
                            passwordField.setVisible(false);
                            passwordClr = Color.BLACK;
                            passwordField.setBackground(passwordClr);
                        } else {
                            if (passwordField != null) {
                                passwordField.setFocusable(false);
                                passwordField.setRequestFocusEnabled(false);
                                passwordField.setBackground(new Color(0x35464B));
                                passwordField.setHorizontalAlignment(JTextField.LEADING);
                            }
                        }
                    }
                    //set controls for remember me
                    if ((rememberMe != null && rememberMe.getBounds().contains(e.getPoint())) ||
                            (rememberMeBound != null && rememberMeBound.getBounds().contains(e.getPoint()))) {
                        shouldRemember = !shouldRemember;
                    }
                    //set controls for remember me toggle
                    if (toggleRec.getBounds().contains(e.getPoint())) {
                        isToggled = !isToggled;
                        char c = passwordField.getEchoChar();
                        if (c == '*')
                            passwordField.setEchoChar((char) 0);
                        else {
                            passwordField.setEchoChar('*');
                        }
                    }
                    //set controls for login
                    if (loginBtn != null && loginBtn.getBounds().contains(e.getPoint())) {
                        if (emailField != null && emailField.getText().isBlank()) emailClr = Color.RED;
                        if (String.valueOf(passwordField.getPassword()).isBlank()) passwordClr = Color.RED;
                        //check if email and password are registered in database
                        if ((!emailField.getText().isBlank() && !String.valueOf(passwordField.getPassword()).isBlank())) {
                            if ((database.isEmail(emailField.getText()) || database.isUsername(emailField.getText())) &&
                                    database.isPassword(String.valueOf(passwordField.getPassword()))) {
                                loginNullified();
                                if (database.isTopUser()) {
                                    display = 'T';
                                    setCursor(Cursor.getDefaultCursor());
                                } else {
                                    passwordClr = emailClr = new Color(0x2FCAA6);
                                    display = 'R';
                                }
                            } else {
                                emailClr = Color.RED;
                                passwordClr = Color.RED;
                            }
                        }
                    }
                    //set controls for sign up
                    if (signUP != null && signUP.getBounds().contains(e.getPoint())) {
                        loginNullified();
                        toggleClr = new Color(0x2FCAA6);
                        isToggled = false;
                        display = 'S';
                        setCursor(Cursor.getDefaultCursor());
                    }
                }

                if (display == 'R') {
                    //set controls for to departure time
                    if (departureTime != null && departureTime.getBounds().contains(e.getPoint())) {
                        timeDepartureBool = !timeDepartureBool;
                        if (!timeDepartureBool) {
                            if (!String.valueOf(confirmPasswordField.getPassword()).isBlank())
                                confirmPasswordField.setVisible(true);
                            hour.setVisible(false);
                            min.setVisible(false);
                        } else {
                            if (confirmPasswordField.isVisible()) confirmPasswordField.setVisible(false);
                        }
                    } else {
                        if (hour != null) {
                            timeDepartureBool = false;
                            hour.setVisible(false);
                            min.setVisible(false);
                            if (!String.valueOf(confirmPasswordField.getPassword()).isBlank())
                                confirmPasswordField.setVisible(true);
                        }
                    }
                    //set controls for to departure date
                    if (departureDate != null && departureDate.getBounds().contains(e.getPoint())) {
                        dateDepartureBool = !dateDepartureBool;
                        if (!dateDepartureBool) {
                            if (!String.valueOf(confirmPasswordField.getPassword()).isBlank())
                                confirmPasswordField.setVisible(true);
                            day.setVisible(false);
                            month.setVisible(false);
                            year.setVisible(false);
                        } else {
                            if (confirmPasswordBool) confirmPasswordField.setVisible(false);
                        }
                    } else {
                        if (day != null) {
                            dateDepartureBool = false;
                            day.setVisible(false);
                            month.setVisible(false);
                            year.setVisible(false);
                            if (!String.valueOf(confirmPasswordField.getPassword()).isBlank())
                                confirmPasswordField.setVisible(true);
                        }
                    }
                    //set controls for train number
                    if (confirmPassword != null && confirmPassword.getBounds().contains(e.getPoint())) {
                        if (String.valueOf(confirmPasswordField.getPassword()).isBlank())
                            confirmPasswordBool = !confirmPasswordBool;
                        else {
                            confirmPasswordField.setFocusable(true);
                            confirmPasswordField.setRequestFocusEnabled(true);
                            confirmPasswordField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (confirmPasswordBool) {
                            confirmPasswordField.setVisible(true);
                            confirmPasswordField.setEnabled(true);
                            confirmPasswordField.requestFocusInWindow();
                            confirmPasswordClr = Color.WHITE;
                        } else {
                            confirmPasswordField.setVisible(false);
                            confirmPasswordClr = Color.WHITE;
                            confirmPasswordField.setBackground(confirmPasswordClr);
                        }
                    } else {
                        if (confirmPasswordField != null && String.valueOf(confirmPasswordField.getPassword()).isBlank()) {
                            confirmPasswordBool = false;
                            confirmPasswordField.setVisible(false);
                            confirmPasswordClr = Color.WHITE;
                            confirmPasswordField.setBackground(confirmPasswordClr);
                        } else {
                            if (confirmPasswordField != null) {
                                confirmPasswordField.setFocusable(false);
                                confirmPasswordField.setRequestFocusEnabled(false);
                                confirmPasswordField.setBackground(new Color(0x35464B));
                                confirmPasswordField.setHorizontalAlignment(JTextField.CENTER);
                                if (dateDepartureBool || timeDepartureBool) confirmPasswordField.setVisible(false);
                            }
                        }
                    }
                    //set controls for phone number
                    if (password != null && password.getBounds().contains(e.getPoint())) {
                        if (String.valueOf(passwordField.getPassword()).isBlank())
                            passwordBool = !passwordBool;
                        else {
                            passwordField.setFocusable(true);
                            passwordField.setRequestFocusEnabled(true);
                            passwordField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (passwordBool) {
                            passwordField.setVisible(true);
                            passwordField.setEnabled(true);
                            passwordField.requestFocusInWindow();
                            passwordClr = Color.WHITE;
                        } else {
                            passwordField.setVisible(false);
                            passwordClr = Color.WHITE;
                            passwordField.setBackground(passwordClr);
                        }
                    } else {
                        if (passwordField != null && String.valueOf(passwordField.getPassword()).isBlank()) {
                            passwordBool = false;
                            passwordField.setVisible(false);
                            passwordClr = Color.WHITE;
                            passwordField.setBackground(passwordClr);
                        } else {
                            if (passwordField != null) {
                                passwordField.setFocusable(false);
                                passwordField.setRequestFocusEnabled(false);
                                passwordField.setBackground(new Color(0x35464B));
                                passwordField.setHorizontalAlignment(JTextField.CENTER);
                            }
                        }
                    }
                    //set controls for from destination
                    if (username != null && username.getBounds().contains(e.getPoint())) {
                        if (usernameField.getText().isBlank())
                            usernameBool = !usernameBool;
                        else {
                            usernameField.setFocusable(true);
                            usernameField.setRequestFocusEnabled(true);
                            usernameField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (usernameBool) {
                            usernameField.setVisible(true);
                            usernameField.setEnabled(true);
                            usernameField.requestFocusInWindow();
                            usernameClr = Color.WHITE;
                        } else {
                            usernameField.setVisible(false);
                            usernameClr = Color.WHITE;
                            usernameField.setBackground(usernameClr);
                        }
                    } else {
                        if (usernameField != null && usernameField.getText().isBlank()) {
                            usernameBool = false;
                            usernameField.setVisible(false);
                            usernameClr = Color.WHITE;
                            usernameField.setBackground(passwordClr);
                        } else {
                            if (usernameField != null) {
                                usernameField.setFocusable(false);
                                usernameField.setRequestFocusEnabled(false);
                                usernameField.setBackground(new Color(0x35464B));
                                usernameField.setHorizontalAlignment(JTextField.CENTER);
                            }
                        }
                    }
                    //set controls for to destination
                    if (toDestination != null && toDestination.getBounds().contains(e.getPoint())) {
                        if (toDestinationField.getText().isBlank())
                            toDestinationBool = !toDestinationBool;
                        else {
                            toDestinationField.setFocusable(true);
                            toDestinationField.setRequestFocusEnabled(true);
                            toDestinationField.setHorizontalAlignment(JTextField.CENTER);
                        }
                        if (toDestinationBool) {
                            toDestinationField.setVisible(true);
                            toDestinationField.setEnabled(true);
                            toDestinationField.requestFocusInWindow();
                            toDestinationClr = Color.WHITE;
                        } else {
                            toDestinationField.setVisible(false);
                            toDestinationClr = Color.WHITE;
                            toDestinationField.setBackground(toDestinationClr);
                        }
                    } else {
                        if (toDestinationField != null && toDestinationField.getText().isBlank()) {
                            toDestinationBool = false;
                            toDestinationField.setVisible(false);
                            toDestinationClr = Color.WHITE;
                            toDestinationField.setBackground(toDestinationClr);
                        } else {
                            if (toDestinationField != null) {
                                toDestinationField.setFocusable(false);
                                toDestinationField.setRequestFocusEnabled(false);
                                toDestinationField.setBackground(new Color(0x35464B));
                                toDestinationField.setHorizontalAlignment(JTextField.CENTER);
                            }
                        }
                    }
                    //collect booked info
                    if (submitBtn != null && submitBtn.getBounds().contains(e.getPoint())) {
                        //check if all field are entered and booking info
                        if (confirmPasswordField != null && !String.valueOf(confirmPasswordField.getPassword()).isBlank() &&
                                passwordField != null && !String.valueOf(passwordField.getPassword()).isBlank() &&
                                usernameField != null && !usernameField.getText().isBlank() &&
                                toDestinationField != null && !toDestinationField.getText().isBlank() &&
                                !trainName.equals("Not available")) {
                            String details = String.valueOf(passwordField.getPassword()) + database.getDelimiter() +
                                    String.valueOf(confirmPasswordField.getPassword()) + database.getDelimiter() +
                                    trainName + database.getDelimiter() + userClass + database.getDelimiter() +
                                    dateToDepart + database.getDelimiter() + timeToDepart + database.getDelimiter() +
                                    usernameField.getText() + database.getDelimiter() + toDestinationField.getText() +
                                    database.getDelimiter() + PNRGenerator();
                            database.writeIntoUser(details);
                        }

                        if (confirmPasswordField != null && String.valueOf(confirmPasswordField.getPassword()).isBlank())
                            confirmPasswordClr = Color.RED;
                        else if (confirmPasswordField != null && trainName.equals("Not available"))
                            confirmPasswordClr = Color.RED;
                        if (passwordField != null && String.valueOf(passwordField.getPassword()).isBlank())
                            passwordClr = Color.RED;
                        if (usernameField != null && usernameField.getText().isBlank())
                            usernameClr = Color.RED;
                        if (toDestinationField != null && toDestinationField.getText().isBlank())
                            toDestinationClr = Color.RED;
                    }
                    //logout
                    if (loginBtn != null && loginBtn.getBounds().contains(e.getPoint())) {
                        registerNullified();
                        display = 'L';
                        setCursor(Cursor.getDefaultCursor());
                    }
                    //cancel reservation
                    if (booked != null && booked.getBounds().contains(e.getPoint())) {
                        //check if PNR exist in database and remove
                        database.removeData(getParent());
                    }
                }

                if (display == 'T') {
                    adminDisplay.mouseClicked(e);
                    display = adminDisplay.getDisplay();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (hoverRec != null && hoverRec.getBounds().contains(e.getPoint()))
                    hoverColor = hoverColor.darker();

                if (submitBtn != null && submitBtn.contains(e.getPoint()))
                    buttonColor = buttonColor.darker();

                if (loginBtn != null && loginBtn.contains(e.getPoint()))
                    buttonColor = buttonColor.darker();

                if (toggleRec != null && toggleRec.getBounds().contains(e.getPoint()))
                    toggleClr = toggleClr.darker().darker();

                if (rememberMeBound != null && rememberMeBound.getBounds().contains(e.getPoint()))
                    toggleClr = toggleClr.darker().darker();

                if (display == 'T') adminDisplay.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (display == 'S') {
                    toggleClr = new Color(0x2FCAA6);
                    buttonColor = new Color(0x055470);
                    hoverColor = hoverRec.getBounds().contains(e.getPoint()) ?
                            new Color(0x2FCAA6) : new Color(0x8E9294);
                } else if (display == 'L') {
                    toggleClr = new Color(0xBE1B86);
                    buttonColor = new Color(0x6B3456);
                    hoverColor = hoverRec.getBounds().contains(e.getPoint()) ?
                            new Color(0x8E9294) : new Color(0x2FCAA6);
                } else if (display == 'R') {
                    buttonColor = Color.WHITE;
                } else {
                    adminDisplay.mouseReleased(e);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //mouse position on screen
        try {
            mouseX = getMousePosition() == null ? -1 : getMousePosition().x;
            mouseY = getMousePosition() == null ? -1 : getMousePosition().y;
        } catch (Exception ignored) {
        }

        if (display == 'S' || display == 'L' || display == 'R') {
            if (display == 'S') {
                g2.drawImage(signUpBackgroundImg, 0, 0, null);
                g2.setColor(new Color(0x12A3D2));
                signUpDisplay(g2);
            } else if (display == 'L') {
                g2.drawImage(loginBackgroundImg, 0, 0, null);
                g2.setColor(new Color(0xBE1B86));
                loginDisplay(g2);
            } else {
                g2.drawImage(bookingBackground, 0, 0, null);
                g2.setColor(Color.WHITE);
                reservationDisplay(g2);
            }

            font = new Font("Book Antiqua", Font.BOLD | Font.ITALIC, 20);
            g2.setFont(font);


            if (x_pos < g2.getFontMetrics(font.deriveFont(25f)).stringWidth(" Available Trains:") + 5
                    || x_pos > getBounds().width - 860 - train.getWidth(null))
                animate = -animate;
            x_pos += animate;

            g2.drawImage(train, x_pos, 90 - train.getHeight(null) + 2, null);
            int num = train.getWidth(null) + 10;
            for (int availableTrain : availableTrainNumbers) {
                g2.drawString(String.valueOf(availableTrain), x_pos + num, 90);
                num += getFontMetrics(font).stringWidth(String.valueOf(availableTrain)) + 30;
            }

            g2.setFont(font.deriveFont(25f));
            g2.drawString(" Available Trains:", 0, 88);

            if (timeDepartureBool && display == 'R')
                dateTimeInterface.drawTimeInterface(g2, departureTime.getBounds().x + departureTime.getBounds().width / 2
                        - dateTimeInterface.w / 2, (int) (departureTime.y - dateTimeInterface.h) - 10, hour, min);

            if (dateDepartureBool && display == 'R')
                dateTimeInterface.drawDateInterface(g2, departureDate.getBounds().x + departureDate.getBounds().width / 2
                                - dateTimeInterface.w / 2, (int) (departureDate.y - dateTimeInterface.h) - 10,
                        year, month, day);

        }
        if (display == 'T') {
            adminDisplay.drawInterface(g2, new Point(mouseX, mouseY));
        }
    }

    private void reservationDisplay(Graphics2D g2) {
        //draw logo and logo text
        String text = "Reservation.com";
        font = new Font("Bell MT", Font.BOLD, 20);
        int xPos = 40;
        int yPos = 20;
        g2.drawImage(logo, xPos, yPos, null);
        g2.setFont(font);
        g2.setColor(Color.WHITE);
        yPos += logo.getHeight(null) / 2;
        FontMetrics fm = g2.getFontMetrics(font);
        xPos += 8 + logo.getWidth(null);
        yPos += fm.getAscent() / 2 + 1;
        g2.drawString(text, xPos, yPos);

        //create the sign-out button
        xPos = getBounds().width - 160;
        int h = 30, w = 60;
        yPos = 20 + logo.getHeight(null) / 2 - h / 2;
        loginBtn = new RoundRectangle2D.Float(xPos, yPos, w + 10, h, 10, 10);
        g2.setStroke(new BasicStroke(4));
        g2.fill(loginBtn);
        g2.setColor(loginBtn.contains(mouseX, mouseY) ? new Color(0x35464B) : new Color(0xFF9900));
        g2.draw(loginBtn);
        //draw text
        font = font.deriveFont(Font.PLAIN, 15f);
        g2.setFont(font);
        g2.setColor(new Color(0x003EBB));
        fm = g2.getFontMetrics(font);
        text = "Sign out";
        xPos += (w + 10) / 2 - fm.stringWidth(text) / 2;
        yPos += h / 2 + fm.getAscent() / 2 - 1;
        g2.drawString(text, xPos, yPos);

        //create the booked button
        text = "Cancel Reservation";
        w = fm.stringWidth(text) + 12;
        xPos = loginBtn.getBounds().x - 100 - w;
        yPos = loginBtn.getBounds().y;
        h = loginBtn.getBounds().height;
        booked = new RoundRectangle2D.Float(xPos, yPos, w, h, 10, 10);
        g2.setColor(Color.WHITE);
        g2.fill(booked);
        g2.setColor(booked.contains(mouseX, mouseY) ? new Color(0x35464B) : new Color(0xFF9900));
        g2.draw(booked);
        //draw text
        g2.setColor(new Color(0x003EBB));
        xPos += w / 2 - fm.stringWidth(text) / 2;
        yPos += h / 2 + fm.getAscent() / 2 - 1;
        g2.drawString(text, xPos, yPos);

        if (emailField != null) {
            //create email box
            font = font.deriveFont(Font.BOLD, 15f);
            g2.setFont(font);
            Color foreground = new Color(0x35464B);
            w = 400;
            h = getFontMetrics(font).getHeight() + 2;
            xPos = getWidth() / 2 - w / 2;
            yPos = 220;
            email = textEditor(g2, xPos, yPos, w, "Email", true, emailClr);
            emailField.setBounds((int) (email.getBounds().x + email.getArcWidth() / 2),
                    email.getBounds().height + email.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    email.getBounds().width - (int) email.arcwidth, h);
            emailField.setBackground(foreground);
            emailField.setText(database.getEmail());
            emailField.setEditable(false);
            emailField.requestFocus(false);
            emailField.setFocusable(false);
            emailField.setVisible(true);

            //create first name box
            w = 150;
            yPos -= (20 + email.getBounds().height);
            firstName = textEditor(g2, xPos, yPos, w, "First name", true, firstNameClr);
            w = firstName.getBounds().width - (int) firstName.arcwidth;
            firstNameField.setBounds((int) (firstName.getBounds().x + firstName.getArcWidth() / 2),
                    firstName.getBounds().height + firstName.getBounds().y - getFontMetrics(font).getHeight() - 8, w, h);
            firstNameField.setBackground(foreground);
            firstNameField.setText(database.getFirstName());
            firstNameField.setEditable(false);
            firstNameField.requestFocus(false);
            firstNameField.setFocusable(false);
            firstNameField.setVisible(true);

            //create last name box
            w = 150;
            xPos = email.getBounds().x + email.getBounds().width - w;
            lastName = textEditor(g2, xPos, yPos, w, "Last name", true, lastNameClr);
            w = lastName.getBounds().width - (int) lastName.arcwidth;
            lastNameField.setBounds((int) (lastName.getBounds().x + lastName.getArcWidth() / 2),
                    lastName.getBounds().height + lastName.getBounds().y - getFontMetrics(font).getHeight() - 8, w, h);
            lastNameField.setBackground(foreground);
            lastNameField.setText(database.getLastName());
            lastNameField.setEditable(false);
            lastNameField.requestFocus(false);
            lastNameField.setFocusable(false);
            lastNameField.setVisible(true);

            //create phone number box
            xPos = email.getBounds().x;
            yPos = email.getBounds().y + email.getBounds().height + 20;
            w = email.getBounds().width;
            password = textEditor(g2, xPos, yPos, w, "Phone number", passwordBool, passwordClr);
            passwordField.setBounds((int) (password.getBounds().x + password.getArcWidth() / 2),
                    password.getBounds().height + password.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    password.getBounds().width - (int) password.arcwidth, h);
            passwordField.setBackground(foreground);

            //create train number box
            w = firstName.getBounds().width;
            xPos = getWidth() / 2 - w / 2;
            yPos = password.getBounds().y + password.getBounds().height + 20;
            confirmPassword = textEditor(g2, xPos, yPos, w, "Train number", confirmPasswordBool, confirmPasswordClr);
            confirmPasswordField.setBounds((int) (confirmPassword.getBounds().x + confirmPassword.getArcWidth() / 2),
                    confirmPassword.getBounds().height + confirmPassword.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    confirmPassword.getBounds().width - (int) confirmPassword.arcwidth, h);
            confirmPasswordField.setBackground(foreground);

            yPos = confirmPassword.getBounds().y + confirmPassword.getBounds().height + 20;
            xPos = firstName.getBounds().x;
            w = password.getBounds().width - 120;
            trainName = "Not available";
            for (int i = 0; i < availableTrainNumbers.length; i++) {
                if (!String.valueOf(confirmPasswordField.getPassword()).isBlank() &&
                        String.valueOf(confirmPasswordField.getPassword()).matches(String.valueOf(availableTrainNumbers[i]))) {
                    trainName = availableTrainNames[i];
                    break;
                }
            }
            Rectangle2D shape = textEditor(g2, xPos, yPos, w, "Train name", true, Color.WHITE).getBounds();
            g2.setFont(font.deriveFont(Font.PLAIN, 20f));
            fm = g2.getFontMetrics();
            xPos = (int) (shape.getBounds().getCenterX() - fm.stringWidth(trainName) / 2);
            w = (int) (shape.getBounds().getMaxY() - 8);
            g2.setColor(Color.WHITE);
            g2.drawString(trainName, xPos, w);


            w = 100;
            xPos = lastName.getBounds().x + lastName.getBounds().width - w;
            trainClass = textEditor(g2, xPos, yPos, w, "Class", true, Color.WHITE);
            g2.setFont(font.deriveFont(Font.PLAIN, 20f));
            fm = g2.getFontMetrics();
            xPos = (int) (trainClass.getBounds().getCenterX() - fm.stringWidth("A") / 2);
            yPos = (int) (trainClass.getBounds().getMaxY() - 8);
            g2.setColor(Color.WHITE);
            g2.drawString(String.valueOf(userClass), xPos, yPos);

            xPos = firstName.getBounds().x;
            yPos = trainClass.getBounds().y + trainClass.getBounds().height + 20;
            w = password.getBounds().width - 160;
            text = "Departure date";
            departureDate = textEditor(g2, xPos, yPos, w, text, true, Color.WHITE);
            //insert day to depart text
            font = new Font("Bell MT", Font.PLAIN, 20);
            g2.setFont(font);
            g2.setColor(Color.WHITE);
            fm = g2.getFontMetrics(font);
            xPos += w / 2 - fm.stringWidth(dateToDepart) / 2;
            yPos += departureDate.getBounds().height - 8;
            if (!dateDepartureBool && !day.getText().isBlank() && !month.getText().isBlank() && !year.getText().isBlank()) {
                int ny = Integer.parseInt(year.getText());
                int nm = Integer.parseInt(month.getText());
                int nd = Integer.parseInt(day.getText());
                try {
                    dateToDepart = nd + "\\" + LocalDate.of(ny, nm, nd).getMonth() + "\\" +
                            String.valueOf(LocalDate.now().getYear()).substring(0, 2) + year.getText();
                } catch (Exception e) {
                    dateToDepart = nd + "\\" + LocalDate.of(00, 1, 12).getMonth() + "\\" +
                            String.valueOf(LocalDate.now().getYear()).substring(0, 2) + year.getText();
                }
            } else
                dateToDepart = LocalDate.now().getDayOfMonth() + "\\" + LocalDate.now().getMonth().toString() + "\\" + LocalDate.now().getYear();
            g2.drawString(dateToDepart, xPos, yPos);

            text = "Departure time";
            w = 140;
            yPos = trainClass.getBounds().y + trainClass.getBounds().height + 20;
            xPos = lastName.getBounds().x + lastName.getBounds().width - w;
            departureTime = textEditor(g2, xPos, yPos, w, text, true, Color.WHITE);
            //draw time selected and insert time to depart text
            font = new Font("Bell MT", Font.PLAIN, 20);
            g2.setFont(font);
            g2.setColor(Color.WHITE);
            fm = g2.getFontMetrics(font);
            xPos += w / 2 - fm.stringWidth(timeToDepart) / 2;
            yPos += departureTime.getBounds().height - 8;
            if (!timeDepartureBool && !hour.getText().isBlank() && !min.getText().isBlank())
                timeToDepart = hour.getText() + ":" + min.getText();
            else timeToDepart = LocalTime.now().getHour() + ":" + LocalTime.now().getMinute();
            g2.drawString(timeToDepart, xPos, yPos);

            xPos = firstName.getBounds().x;
            yPos = departureTime.getBounds().y + departureTime.getBounds().height + 20;
            w = email.getBounds().width / 2 - 10;
            username = textEditor(g2, xPos, yPos, w, "From", usernameBool, usernameClr);
            usernameField.setBounds((int) (username.getBounds().x + username.getArcWidth() / 2),
                    username.getBounds().height + username.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    username.getBounds().width - (int) username.arcwidth, h);
            usernameField.setBackground(foreground);

            xPos = lastName.getBounds().x + lastName.getBounds().width - w;
            toDestination = textEditor(g2, xPos, yPos, w, "To", toDestinationBool, toDestinationClr);
            toDestinationField.setBounds((int) (toDestination.getBounds().x + toDestination.getArcWidth() / 2),
                    toDestination.getBounds().height + toDestination.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    toDestination.getBounds().width - (int) toDestination.arcwidth, h);
            toDestinationField.setBackground(foreground);

            h = 40;
            w = 90;
            xPos = email.getBounds().x + email.getBounds().width / 2 - w / 2;
            yPos = toDestination.getBounds().y + toDestination.getBounds().height + 20;
            submitBtn = new RoundRectangle2D.Float(xPos, yPos, w + 10, h, 10, 10);
            g2.setStroke(new BasicStroke(4));
            g2.setColor(Color.WHITE);
            g2.fill(submitBtn);
            g2.setColor(submitBtn.contains(mouseX, mouseY) ? new Color(0x35464B) : new Color(0xFF9900));
            g2.draw(submitBtn);
            //draw text
            font = font.deriveFont(Font.PLAIN, 22f);
            g2.setFont(font);
            g2.setColor(new Color(0x003EBB));
            fm = g2.getFontMetrics(font);
            text = "Book";
            xPos += (w + 10) / 2 - fm.stringWidth(text) / 2;
            yPos += h / 2 + fm.getAscent() / 2 - 1;
            g2.drawString(text, xPos, yPos);

        } else initializeReservationComponents();
    }

    private void loginDisplay(Graphics2D g2) {
        String text;
        FontMetrics fm;
        int xPos = getToolkit().getScreenSize().width / 2 - 300;
        int yPos = getToolkit().getScreenSize().height / 2 - 250;
        int h = 500, w = 600;

        RoundRectangle2D.Float con = new RoundRectangle2D.Float(xPos, yPos, w, h, 150, 150);
        g2.setColor(new Color(0xB3000000, true));
        g2.fill(con);
        g2.setStroke(new BasicStroke(4));
        Color clr = new Color(0xBE1B86);
        g2.setPaint(new GradientPaint(mouseX, mouseY, clr, w, h, new Color(0x000000)));
        g2.draw(con);

        //draw the logo sign
        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(0x12A3D2));
        w = 20;
        Ellipse2D.Float circle = new Ellipse2D.Float(con.x + 35, con.y + 35, w, w);
        g2.fill(circle);

        font = new Font("Bell MT", Font.BOLD, 20);
        g2.setFont(font);
        fm = g2.getFontMetrics();
        xPos = (int) (circle.x + circle.width + 10);
        yPos = (circle.getBounds().y + circle.getBounds().height / 2);
        text = "Reservation.com";
        g2.drawString(text, xPos, yPos + fm.getHeight() / 3);

        if (emailField != null) {
            yPos = 60;
            email = textEditor(g2, (int) (con.getBounds().x + con.getArcWidth() / 2),
                    (int) (con.getBounds().y + con.getArcHeight() / 2) + yPos,
                    (int) (con.getBounds().width - con.getArcWidth()),
                    "Email\\Username", emailBool, emailClr);
            emailField.setBounds((int) (email.getBounds().x + email.getArcWidth() / 2),
                    email.getBounds().height + email.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    email.getBounds().width - (int) email.arcwidth, getFontMetrics(font).getHeight() + 2);
            emailField.setBackground(new Color(0x35464B));

            yPos += 100;
            password = textEditor(g2, (int) (con.getBounds().x + con.getArcWidth() / 2),
                    (int) (con.getBounds().y + con.getArcHeight() / 2) + yPos,
                    (int) (con.getBounds().width - con.getArcWidth()),
                    "Password", passwordBool, passwordClr);
            w = password.getBounds().width - (int) (password.arcwidth + password.arcwidth / 2) -
                    toggleInvisible.getWidth(null) - 5;
            passwordField.setBounds(
                    password.getBounds().x + (int) (password.getArcWidth() / 2),
                    (password.getBounds().height + password.getBounds().y) - getFontMetrics(font).getHeight() - 8, w,
                    getFontMetrics(font).getHeight() + 2);
            passwordField.setBackground(new Color(0x35464B));
            xPos = password.getBounds().x + password.getBounds().width -
                    (int) password.arcwidth - toggleInvisible.getWidth(null) / 2;
            yPos = (int) password.getBounds().getCenterY() - toggleInvisible.getHeight(null) / 2;
            w = toggleInvisible.getWidth(null);
            h = toggleInvisible.getHeight(null);
            toggleRec = new Rectangle2D.Float(xPos, yPos, w, h);
            hoverOverImg(g2, toggleRec, xPos, yPos, toggleInvisible);
            g2.drawImage(isToggled ? toggleVisible : toggleInvisible, xPos, yPos, null);

            xPos = password.getBounds().x + password.getBounds().width / 2 - 75;
            yPos = //!emailField.getText().isBlank() && !passwordField.getText().isBlank() ?
                    password.getBounds().height + password.getBounds().y + 40;// : -44;
            w = 150;
            h = 40;
            loginBtn = new RoundRectangle2D.Float(xPos, yPos, w, h, 20, 20);
            g2.setColor(buttonColor.brighter());
            g2.fill(loginBtn);
            g2.setColor(Color.black);
            g2.draw(loginBtn);

            text = "Login";
            font = new Font("Book Antiqua", Font.BOLD | Font.ITALIC, 20);
            g2.setFont(font);
            g2.setColor(new Color(0xFF090606, true));
            fm = g2.getFontMetrics(font);
            xPos = (int) (loginBtn.getBounds().getCenterX() - fm.stringWidth(text) / 2);
            yPos += fm.getHeight() + 3;
            g2.drawString(text, xPos, yPos);

            xPos = password.getBounds().width + password.getBounds().x - signUP.getIcon().getIconWidth() + 20;
            yPos = password.getBounds().height + password.getBounds().y + 120;
            signUP.setBounds(xPos, yPos, signUP.getIcon().getIconWidth() - 5,
                    signUP.getIcon().getIconHeight() - 10);
            hoverOverLabel(g2, signUP);

            font = font.deriveFont(Font.BOLD | Font.ITALIC);
            g2.setFont(font);
            fm = g2.getFontMetrics(font);
            text = "Not A Member?";
            xPos = signUP.getBounds().x - fm.stringWidth(text) - 10;
            yPos = (int) (signUP.getBounds().getCenterY() + fm.getHeight() / 3) - 2;
            g2.setColor(Color.WHITE);
            g2.drawString(text, xPos, yPos);

            //draw rememberMe check box
            w = 20;
            h = 6;
            xPos = passwordField.getBounds().x;
            yPos = (int) (signUP.getBounds().getCenterY() - (w / 2f));
            rememberMe = new RoundRectangle2D.Float(xPos, yPos, w, w, h, h);
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(3));
            g2.draw(rememberMe);

            w = 14;
            circle = new Ellipse2D.Float(xPos + rememberMe.getBounds().width / 2f - w / 2f,
                    yPos + rememberMe.getBounds().width / 2f - w / 2f, w, w);
            if (shouldRemember) {
                g2.setColor(rememberMeClr);
                g2.fill(circle);
            }
            g2.setColor(new Color(0xBE1B86));
            g2.draw(circle);

            text = "Remember me";
            font = font.deriveFont(Font.PLAIN);
            g2.setFont(font);
            g2.setColor(shouldRemember ? Color.black : rememberMeClr);
            fm = g2.getFontMetrics(font);
            xPos = (int) (rememberMe.getMaxX() + 5);
            yPos = (int) (rememberMe.getBounds().getCenterY() + fm.getHeight() / 3);
            g2.drawString(text, xPos, yPos);
            rememberMeBound = new Rectangle2D.Float(xPos, rememberMe.getBounds().y, fm.stringWidth(text), fm.getHeight() - 5);
        } else initializeLoginComponents();
    }

    private void signUpDisplay(Graphics2D g2) {
        //create the size of container
        int xPos = 100, yPos = 40;
        int w = getToolkit().getScreenSize().width - (xPos * 2);
        int h = getToolkit().getScreenSize().height - (yPos * 2);
        RoundRectangle2D.Float con = new RoundRectangle2D.Float(xPos, yPos,
                w, h, (getBounds().height - (yPos * 2)) / 6f, (getBounds().height - (yPos * 2)) / 6f);
        g2.setColor(new Color(0xB3112931, true));
//        g2.fill(con);
        g2.setStroke(new BasicStroke(3));
//        g2.setPaint(new GradientPaint(mouseX, mouseY, new Color(0x12A3D2).brighter(),
//                (float) con.getWidth() / 2f, (float) con.getHeight() / 2f,
//                new Color(0xFF000000, true), true));
//        g2.draw(con);

        //draw the logo sign
        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(0x12A3D2));
        w = 25;
        Ellipse2D.Float circle = new Ellipse2D.Float(con.x + 30, 10, w, w);
        g2.fill(circle);

        Font font = new Font("Bell MT", Font.BOLD, 20);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        xPos = (int) (circle.x + circle.width + 10);
        yPos = (circle.getBounds().y + circle.getBounds().height / 2);
        String text = "Reservation.com";
        g2.drawString(text, xPos, yPos + fm.getHeight() / 3);


        font = new Font("Book Antiqua", Font.PLAIN, 40);
        g2.setFont(font);
        fm = g2.getFontMetrics(font);
        text = "Create new account";
        yPos += 90 + fm.getHeight();
        g2.setColor(Color.WHITE);
        g2.drawString(text, xPos, yPos);

        g2.setColor(new Color(0x12A3D2));
        xPos += fm.stringWidth(text) + 5;
        yPos -= 6;
        w = 8;
        circle = new Ellipse2D.Float(xPos, yPos, w, w);
        g2.fill(circle);
        xPos = (int) (con.x + 65);

        font = font.deriveFont(Font.PLAIN, 25f);
        g2.setFont(font);
        fm = g2.getFontMetrics(font);
        g2.setColor(Color.black);
        text = "Already A Member?";
        yPos += 36 + fm.getHeight();
        g2.drawString(text, xPos, yPos);

        xPos += fm.stringWidth(text) + 4;
        yPos -= fm.getHeight() - 12;

        if (emailField != null) {
            w = logIn.getIcon().getIconWidth() - 20;
            h = logIn.getIcon().getIconHeight() - 10;
            logIn.setBounds(xPos, yPos, w, h);
            hoverOverLabel(g2, logIn);

            xPos = (int) (con.x + 67);
            yPos += 25 + logIn.getHeight();
            font = font.deriveFont(Font.BOLD, 15f);
            firstName = textEditor(g2, xPos, yPos, 150, "First name", firstNameBool, firstNameClr);
            w = firstName.getBounds().width - (int) firstName.arcwidth;
            h = getFontMetrics(font).getHeight() + 2;
            firstNameField.setBounds((int) (firstName.getBounds().x + firstName.getArcWidth() / 2),
                    firstName.getBounds().height + firstName.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    w, h);

            xPos = circle.getBounds().x + circle.getBounds().width - 154;
            lastName = textEditor(g2, xPos, yPos, 150, "Last name", lastNameBool, lastNameClr);
            lastNameField.setBounds((int) (lastName.getBounds().x + lastName.getArcWidth() / 2),
                    lastName.getBounds().height + lastName.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    lastName.getBounds().width - (int) lastName.arcwidth, h);

            xPos = (int) (con.x + 67);
            yPos = (int) (25 + firstName.getBounds().getMaxY());
            email = textEditor(g2, xPos, yPos, (int) (circle.getBounds().getMaxX() - xPos),
                    "Email", emailBool, emailClr);
            emailField.setBounds((int) (email.getBounds().x + email.getArcWidth() / 2),
                    email.getBounds().height + email.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    email.getBounds().width - (int) email.arcwidth, h);

            yPos += 75;
            password = textEditor(g2, xPos, yPos, (int) (circle.getBounds().getMaxX() - xPos), "Password",
                    passwordBool, passwordClr);
            w = password.getBounds().width - (int) (password.arcwidth + password.arcwidth / 2) -
                    toggleInvisible.getWidth(null) - 5;
            passwordField.setBounds(
                    password.getBounds().x + (int) (password.getArcWidth() / 2),
                    password.getBounds().height + password.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    w, h);
            int z = password.getBounds().x + password.getBounds().width -
                    (int) password.arcwidth - toggleInvisible.getWidth(null) / 2;
            int s = (int) password.getBounds().getCenterY() - toggleInvisible.getHeight(null) / 2;
            toggleRec = new Rectangle2D.Float(z, s, toggleInvisible.getWidth(null), toggleInvisible.getHeight(null));
            hoverOverImg(g2, toggleRec, z, s, toggleInvisible);
            g2.drawImage(isToggled ? toggleVisible : toggleInvisible, z, s, null);

            yPos += 75;
            confirmPassword = textEditor(g2, xPos, yPos, (int) (circle.getBounds().getMaxX() - xPos),
                    "Confirm password", confirmPasswordBool, confirmPasswordClr);
            confirmPasswordField.setBounds((int) (confirmPassword.getBounds().x + confirmPassword.getArcWidth() / 2),
                    confirmPassword.getBounds().height + confirmPassword.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    w, h);
            s = (int) confirmPassword.getBounds().getCenterY() - toggleInvisible.getHeight(null) / 2;
            rememberMeBound = new Rectangle2D.Float(z, s, toggleInvisible.getWidth(null), toggleInvisible.getHeight(null));
            hoverOverImg(g2, rememberMeBound, z, s, toggleInvisible);
            g2.drawImage(isToggled ? toggleVisible : toggleInvisible, z, s, null);

            yPos += 75;
            username = textEditor(g2, xPos, yPos, (int) (circle.getBounds().getMaxX() - xPos),
                    "Username", usernameBool, usernameClr);
            usernameField.setBounds((int) (username.getBounds().x + username.getArcWidth() / 2),
                    username.getBounds().height + username.getBounds().y - getFontMetrics(font).getHeight() - 8,
                    username.getBounds().width - (int) username.arcwidth, getFontMetrics(font).getHeight());

            g2.setColor(buttonColor);
            yPos += 70;
            submitBtn = new RoundRectangle2D.Float((float) (username.getBounds().getCenterX() - 150 / 2f), yPos,
                    150, 40, 20, 20);
            g2.fill(submitBtn);
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(0xFF8C8C8C, true));
            g2.draw(submitBtn);

            g2.setColor(new Color(0x79D4F3).brighter());
            text = "Create Account";
            font = new Font("Book Antiqua", Font.BOLD | Font.ITALIC, 20);
            g2.setFont(font);
            fm = g2.getFontMetrics(font);
            xPos = (int) (submitBtn.getBounds().getCenterX() - fm.stringWidth(text) / 2);
            yPos += fm.getHeight() + 3;
            g2.drawString(text, xPos, yPos);
        } else initializeSignupComponents();
    }

    private void loginNullified() {
        remove(signUP);
        signUP = null;
        remove(passwordField);
        passwordField = null;
        remove(emailField);
        emailField = null;
        emailBool = false;
        passwordBool = false;
        emailClr = passwordClr = Color.BLACK;
        email = password = null;
    }

    private void signupNullified() {
        remove(logIn);
        logIn = null;
        remove(firstNameField);
        firstNameField = null;
        remove(lastNameField);
        lastNameField = null;
        remove(passwordField);
        passwordField = null;
        remove(emailField);
        emailField = null;
        remove(confirmPasswordField);
        confirmPasswordField = null;
        remove(usernameField);
        usernameField = null;
        firstNameBool = false;
        lastNameBool = false;
        emailBool = false;
        passwordBool = false;
        confirmPasswordBool = false;
        usernameBool = false;
        firstName = lastName = email = password = confirmPassword = username = null;
        emailClr = passwordClr = new Color(0x063747);
    }

    private void registerNullified() {
        remove(emailField);
        emailField = null;
        remove(firstNameField);
        firstNameField = null;
        remove(lastNameField);
        lastNameField = null;
        remove(passwordField);
        passwordField = null;
        remove(confirmPasswordField);
        confirmPasswordField = null;
        remove(usernameField);
        usernameField = null;
        remove(toDestinationField);
        toDestinationField = null;
        remove(hour);
        hour = null;
        remove(min);
        min = null;
        remove(year);
        year = null;
        remove(month);
        month = null;
        remove(day);
        day = null;
        toDestination = null;
        firstNameBool = false;
        lastNameBool = false;
        emailBool = false;
        passwordBool = false;
        confirmPasswordBool = false;
        usernameBool = false;
        firstName = lastName = email = password = confirmPassword = username = null;
    }

    private RoundRectangle2D.Float textEditor(Graphics2D g2, int x, int y, int w, String text, boolean anim, Color c) {
        int xp, yp;
        font = new Font("Bell MT", Font.PLAIN, 18);
        RoundRectangle2D.Float shape = new RoundRectangle2D.Float(x, y, w, 50, 50 / 2f, 50 / 2f);
        g2.setColor(new Color(0x35464B));
        g2.fill(shape);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(4));
        g2.draw(shape);

        if (anim) {
            font = font.deriveFont(Font.PLAIN, 15f);
            g2.setFont(font);
            xp = (int) shape.getX() + 10;
            yp = (int) shape.getY() + 18;
            g2.setColor(new Color(0x112931));
        } else {
            font = font.deriveFont(Font.PLAIN, 18f);
            g2.setColor(Color.WHITE);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics(font);
            xp = (int) shape.getCenterX() - fm.stringWidth(text) / 2;
            yp = (int) shape.getCenterY() + fm.getHeight() / 3;
        }
        g2.drawString(text, xp, yp);
        return shape;
    }

    private void hoverOverImg(Graphics2D g, Rectangle2D.Float rec, int x, int y, Image img) {
        g.setColor(toggleClr);
        if (rec.contains(mouseX, mouseY)) {
            int padding = 5;
            RoundRectangle2D.Float r1 = new RoundRectangle2D.Float(x - padding, y - padding - 2,
                    img.getWidth(null) + padding * 2,
                    img.getHeight(null) + padding * 2 + 4, 12, 12);
            g.fill(r1);
            g.setColor(new Color(0xFF737373, true));
            g.draw(r1);
        }
    }

    private void hoverOverLabel(Graphics2D g, JLabel l) {
        try {
            if (l.getBounds().contains(getMousePosition())) {
                hoverRec = new RoundRectangle2D.Float(l.getX() - 2, l.getY() - 2,
                        4 + l.getWidth(), l.getHeight() + 4, 8, 8);
                g.setColor(hoverColor);
                g.fill(hoverRec);
            }
        } catch (Exception ignored) {
        }
    }

    private void initializeLoginComponents() {
        buttonColor = new Color(0x6B3456);
        emailClr = passwordClr = Color.BLACK;

        emailField = new JTextField(10);
        emailField.getDocument().addDocumentListener(documentListener(emailField, emailClr));
        add(emailField);
        emailField.setVisible(false);

        passwordField = new JPasswordField(10);
        passwordField.getDocument().addDocumentListener(documentListener(passwordField, passwordClr));
        add(passwordField);
        passwordField.setVisible(false);

        signUP = new JLabel();
        signUP.setDoubleBuffered(true);
        signUP.setIcon(new ImageIcon(signupImg));
        add(signUP);
    }

    private void initializeReservationComponents() {
        buttonColor = toDestinationClr = firstNameClr = lastNameClr = emailClr = passwordClr
                = confirmPasswordClr = usernameClr = Color.WHITE;
        hoverColor = new Color(0x12A3D2);

        firstNameField = new JTextField(10);
        firstNameField.getDocument().addDocumentListener(documentListener(firstNameField, firstNameClr));
        add(firstNameField);
        firstNameField.setVisible(false);

        lastNameField = new JTextField(10);
        lastNameField.getDocument().addDocumentListener(documentListener(lastNameField, lastNameClr));
        add(lastNameField);
        lastNameField.setVisible(false);

        emailField = new JTextField(10);
        emailField.getDocument().addDocumentListener(documentListener(emailField, emailClr));
        add(emailField);
        emailField.setVisible(false);

        passwordField = new JPasswordField(10);
        passwordField.getDocument().addDocumentListener(documentListener(passwordField, passwordClr));
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) e.consume();
            }
        });
        add(passwordField);
        passwordField.setVisible(false);

        confirmPasswordField = new JPasswordField(10);
        confirmPasswordField.getDocument().addDocumentListener(documentListener(confirmPasswordField, confirmPasswordClr));
        confirmPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) e.consume();
            }
        });
        add(confirmPasswordField);
        confirmPasswordField.setVisible(false);

        usernameField = new JTextField(10);
        usernameField.getDocument().addDocumentListener(documentListener(usernameField, usernameClr));
        add(usernameField);
        usernameField.setVisible(false);

        toDestinationField = new JTextField(10);
        toDestinationField.getDocument().addDocumentListener(documentListener(toDestinationField, usernameClr));
        add(toDestinationField);
        toDestinationField.setVisible(false);

        hour = new JTextField(10);
        {
            hour.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c)) e.consume();
                    if (hour.getText().length() > 1) e.consume();
                }
            });
        }
        add(hour);
        hour.setVisible(false);

        min = new JTextField(10);
        {
            min.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c)) e.consume();
                    if (min.getText().length() > 1) e.consume();
                    try {
                        if (min.getText().isEmpty() && Integer.parseInt(String.valueOf(c)) >= 6) e.consume();
                    } catch (Exception ignored) {
                    }

                }
            });
        }
        add(min);
        min.setVisible(false);

        year = new JTextField(10);
        {
            year.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c)) e.consume();
                    if (year.getText().length() > 1) e.consume();
                }
            });
        }
        add(year);
        year.setVisible(false);

        month = new JTextField(10);
        {
            month.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c)) e.consume();
                    if (month.getText().length() > 1) e.consume();
                }
            });
        }
        add(month);
        month.setVisible(false);

        day = new JTextField(10);
        {
            day.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c)) e.consume();
                    if (day.getText().length() > 1) e.consume();
                }
            });
        }
        add(day);
        day.setVisible(false);
    }

    private void initializeSignupComponents() {
        buttonColor = new Color(0x055470);
        firstNameClr = lastNameClr = emailClr = passwordClr = confirmPasswordClr = usernameClr = new Color(0x063747);
        logIn = new JLabel();
        logIn.setDoubleBuffered(true);
        logIn.setIcon(new ImageIcon(loginImg));
        add(logIn);

        firstNameField = new JTextField(10);
        firstNameField.getDocument().addDocumentListener(documentListener(firstNameField, firstNameClr));
        add(firstNameField);
        firstNameField.setVisible(false);

        lastNameField = new JTextField(10);
        lastNameField.getDocument().addDocumentListener(documentListener(lastNameField, lastNameClr));
        add(lastNameField);
        lastNameField.setVisible(false);

        emailField = new JTextField(10);
        emailField.getDocument().addDocumentListener(documentListener(emailField, emailClr));
        add(emailField);
        emailField.setVisible(false);

        passwordField = new JPasswordField(10);
        passwordField.getDocument().addDocumentListener(documentListener(passwordField, passwordClr));
        add(passwordField);
        passwordField.setVisible(false);

        confirmPasswordField = new JPasswordField(10);
        confirmPasswordField.getDocument().addDocumentListener(documentListener(confirmPasswordField,
                confirmPasswordClr));
        add(confirmPasswordField);
        confirmPasswordField.setVisible(false);

        usernameField = new JTextField(10);
        usernameField.getDocument().addDocumentListener(documentListener(usernameField, usernameClr));
        add(usernameField);
        usernameField.setVisible(false);
    }

    public DocumentListener documentListener(JTextField f, Color clr) {
        font = font.deriveFont(Font.PLAIN, 20f);
        if (f instanceof JPasswordField) {
            ((JPasswordField) f).setEchoChar((char) 0);
        }
        f.setForeground(Color.WHITE);
        f.setFont(font);
        f.setBackground(clr);
        f.setDoubleBuffered(true);
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setCaretColor(Color.black);
        f.setBorder(new MatteBorder(0, 3, 0, 3, hoverColor));
        f.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                f.setFocusable(true);
                f.setRequestFocusEnabled(true);
                f.requestFocusInWindow();
                f.setHorizontalAlignment(JTextField.CENTER);
            }
        });
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                f.setHorizontalAlignment(JTextField.CENTER);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                f.setHorizontalAlignment(JTextField.CENTER);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                f.setHorizontalAlignment(JTextField.CENTER);
            }
        };
    }


    private String PNRGenerator() {
        Random rand = new Random();
        String PNR = (char) rand.nextInt(97, 122) + "" +
                (char) rand.nextInt(65, 90) + (char) rand.nextInt(65, 90) +
                Math.abs(rand.nextInt()) + (char) rand.nextInt(97, 122) +
                (char) rand.nextInt(65, 90) + (char) rand.nextInt(65, 90) +
                Math.abs(rand.nextInt()) + (char) rand.nextInt(97, 122);

        JTextField panel = getLabel();
        panel.setText(PNR);
        panel.setEditable(false);

        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(getParent(), "Your PNR number:");
        dialog.setMinimumSize(new Dimension(panel.getWidth(), 90));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.setVisible(true);
        return panel.getText();
    }

    private JTextField getLabel() {
        JTextField panel = new JTextField();
        panel.setFont(new Font("Callibre", Font.BOLD, 25));
        panel.setForeground(Color.black);
        panel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0xFF9900)));
        return panel;
    }
}