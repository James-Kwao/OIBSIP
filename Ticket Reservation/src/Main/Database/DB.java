package Main.Database;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;

public class DB {
    private static int id = -1;
    private final String delimiter;
    private final String rootPath;
    private PrintWriter fileWriter;
    private BufferedReader fileReader;
    private String firstName, lastName, email, password, username;
    private String[] data;
    private File file, currentUser, userDetails, bookings;
    private boolean topUser;


    public DB(String dataBase_name) {
        rootPath = "E:\\." + dataBase_name;
        delimiter = "@d#b%";
        firstName = lastName = email = password = username = "Null";
        try {
            file = new File(rootPath);
            if (!file.exists()) {
                //create the root as a directory
                file.mkdir();
                //make the directory invisible
                makeFileInvisible(file);
                //create superuser account
                String child = "admin@gmail.com" + delimiter + "admin" + delimiter + ".rdb";
                file = new File(rootPath, child);
                String newUser = "admin" + delimiter + "admin" + delimiter +
                        "admin@gmail.com" + delimiter + "admin123" + delimiter + "admin";
                file.createNewFile();
                fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true);
                fileWriter.println(newUser);
                fileWriter.close();
                file = new File(rootPath);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void makeFileInvisible(File f) throws IOException {
        Path p = Files.setAttribute(f.toPath(), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        f = p.toFile();
    }

    public int insert(String firstName, String lastName, String email, String password, String username) {
        id++;
        String child = email + delimiter + username + delimiter + ".rdb";

        File u = new File(rootPath, child);
        try {
            if (!u.exists()) {
                u.mkdir();
                userDetails = new File(u.getCanonicalFile().toString(), "user.detis");
                bookings = new File(u.getCanonicalFile().toString(), "Bookings.bks");
                userDetails.createNewFile();
                bookings.createNewFile();
                makeFileInvisible(bookings);
                makeFileInvisible(userDetails);
                String newUser = firstName + delimiter + lastName + delimiter + email + delimiter + password + delimiter + username;
                fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(userDetails, true)), true);
                fileWriter.println(newUser);
                fileWriter.close();
                currentUser = u;
                this.firstName = firstName;
                this.lastName = lastName;
                this.email = email;
                this.password = password;
                this.username = username;
                return 0;
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    private boolean checkForExist(String details, int where) {
        try {
            File[] files = file.listFiles();
            if (files != null)
                for (File f : files) {
                    data = f.getName().split(delimiter);
                    if (data[0].equals(details) || data[1].equals(details)) {
                        if (f.isDirectory())
                            for (File nf : Objects.requireNonNull(f.listFiles())) {
                                if (nf.getName().equals("user.detis")) {
                                    userDetails = nf;
                                    fileReader = new BufferedReader(new FileReader(nf));
                                    String userInfo = fileReader.readLine();
                                    data = userInfo.split(delimiter);
                                    if (data[where].contains(details)) {
                                        currentUser = nf;
                                        firstName = data[0];
                                        lastName = data[1];
                                        email = data[2];
                                        password = data[3];
                                        username = data[4];
                                        fileReader.close();
                                        return true;
                                    }
                                    fileReader.close();
                                    break;
                                } else bookings = nf;
                            }
                        else {
                            fileReader = new BufferedReader(new FileReader(f));
                            String userInfo = fileReader.readLine();
                            data = userInfo.split(delimiter);
                            if (data[where].contains(details)) {
                                currentUser = f;
                                firstName = data[0];
                                lastName = data[1];
                                email = data[2];
                                password = data[3];
                                username = data[4];
                                topUser = true;
                                fileReader.close();
                                return true;
                            }
                            fileReader.close();
                        }
                    }
                }
        } catch (Exception ignored) {
        }
        return false;
    }

    public void writeIntoUser(String info) {
        try {
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(bookings, true)), true);
            fileWriter.println(info);
            fileWriter.close();
        } catch (Exception ignored) {
        }
    }

    public void removeData(Container parent) {
        JTextField panel = new JTextField();
        panel.setFont(new Font("Callibre", Font.BOLD, 25));
        panel.setForeground(Color.black);
        panel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0xFF9900)));
        panel.requestFocusInWindow();
        panel.setFocusable(true);

        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(parent, "Enter PNR number:");
        dialog.setMinimumSize(new Dimension(panel.getWidth(), 90));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        if (selectedValue != null && selectedValue.equals(JOptionPane.OK_OPTION)) {
            try {
                selectedValue = null;
                StringBuilder updatedInfo = new StringBuilder();
                fileReader = new BufferedReader(new FileReader(bookings.getCanonicalFile()));
                String read = fileReader.readLine();
                while (read != null) {
                    if (read.contains(delimiter) && read.split(delimiter)[8].equals(panel.getText())) {
                        JTextArea textArea = displayPNRInfo(read);
                        optionPane = new JOptionPane(textArea, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
                        dialog = optionPane.createDialog(parent, "Delete PNR?");
                        dialog.setMinimumSize(new Dimension(textArea.getWidth(), textArea.getHeight()));
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.setResizable(false);
                        dialog.setBackground(Color.black);
                        dialog.setVisible(true);
                        selectedValue = optionPane.getValue();
                        read = fileReader.readLine();
                    }
                    if (read != null)
                        updatedInfo.append(read).append("\n");
                    read = fileReader.readLine();
                }
                fileReader.close();

                if (selectedValue != null && selectedValue.equals(JOptionPane.OK_OPTION)) {
                    File temp = new File("tmp");
                    fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(temp, true)), true);
                    fileWriter.println(updatedInfo);
                    fileWriter.close();
                    System.out.println(bookings.delete());
                    System.out.println(temp.renameTo(bookings));
                } else if (selectedValue == null) {
                    optionPane = new JOptionPane(new JLabel("PNR does not exist"), JOptionPane.ERROR_MESSAGE,
                            JOptionPane.OK_CANCEL_OPTION);
                    dialog = optionPane.createDialog(parent, "Error");
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.setResizable(false);
                    dialog.setVisible(true);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public String getDelimiter() {
        return delimiter;
    }

    public boolean isEmail(String person) {
        return checkForExist(person, 2);
    }

    public boolean isPassword(String pass) {
        return pass.equals(password);
    }

    public boolean isUsername(String person) {
        return checkForExist(person, 4);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        try {
            String newUser = firstName + delimiter + lastName + delimiter + email + delimiter + password + delimiter + username;
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(userDetails, false)), false);
            fileWriter.println(newUser);
            fileWriter.close();
        } catch (Exception ignored) {
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        try {
            String newUser = firstName + delimiter + lastName + delimiter + email + delimiter + password + delimiter + username;
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(userDetails, false)), false);
            fileWriter.println(newUser);
            fileWriter.close();
        } catch (Exception ignored) {
        }
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        try {
            String newUser = firstName + delimiter + lastName + delimiter + email + delimiter + password + delimiter + username;
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(userDetails, false)), false);
            fileWriter.println(newUser);
            fileWriter.close();
        } catch (Exception ignored) {
        }
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        try {
            String newUser = firstName + delimiter + lastName + delimiter + email + delimiter + password + delimiter + username;
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(userDetails, false)), false);
            fileWriter.println(newUser);
            fileWriter.close();
        } catch (Exception ignored) {
        }
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        try {
            String newUser = firstName + delimiter + lastName + delimiter + email + delimiter + password + delimiter + username;
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(userDetails, false)), false);
            fileWriter.println(newUser);
            fileWriter.close();
        } catch (Exception ignored) {
        }
        this.username = username;
    }

    public File getCurrentUser() {
        return currentUser;
    }

    public int totalUsers() {
        return id;
    }

    public boolean isTopUser() {
        return topUser;
    }

    private JTextArea displayPNRInfo(String s) {
        JTextArea panel = new JTextArea();
        panel.setFont(new Font("Book Antiqua", Font.PLAIN, 20));
        panel.setBackground(Color.black);
        panel.setForeground(Color.WHITE);
        panel.setEditable(false);
        panel.setFocusable(false);
        panel.requestFocus(false);
        panel.setBorder(new MatteBorder(2, 2, 2, 2, new Color(0xFF9900)));
        String[] bookingInfo = s.split(delimiter);
        panel.setText("First Name: " + getFirstName() + "\n" + "Last Name: " + getLastName() + "\n" +
                "Email: " + getEmail() + "\n" + "Phone number: " + bookingInfo[0] + "\n" +
                "Train number: " + bookingInfo[1] + "\n" + "Train name: " + bookingInfo[2] + "\n" +
                "Class: " + bookingInfo[3] + "\n" + "Date to depart: " + bookingInfo[4] + "\n" +
                "Time to depart: " + bookingInfo[5] + "\n" + "From: " + bookingInfo[6] + "\n" + "Destination: " + bookingInfo[7]);
        return panel;
    }
}
