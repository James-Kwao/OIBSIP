import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Scanner;

public class Display {
    private final DataBase db;
    private final User user;
    public Scanner getInputs;

    public Display() {
        String welcomeMsg = "Welcome to Java ATM\nPlease enter you ID: ";
        System.out.print(welcomeMsg);
        getInputs = new Scanner(System.in);
        String id = getInputs.nextLine();
        welcomeMsg = "Please enter your pin: ";
        System.out.print(welcomeMsg);
        int pin = getInputs.nextInt();
        user = new User(id, pin);
        db = new DataBase();
        db.getDetails(user.id, user.pin);

        generalInterface();
    }

    private void quit() {
        System.out.println("Thank you for using Java ATM");
        System.exit(0);
    }

    private void transfer() {
        float balance = Float.parseFloat(db.getHistory()
                .split("\n")[0].split(":")[1].trim());
        float tranferred;
        String name;
        getInputs = new Scanner(System.in);
        do {
            System.out.println("Total balance: " + balance);
            System.out.println("Enter amount to transfer, please" +
                    " note amount can't be more than " + balance);
            tranferred = getInputs.nextFloat();
        } while (tranferred > balance);
        getInputs = new Scanner(System.in);
        System.out.print("Transfer to: ");
        name = getInputs.nextLine();
        balance -= tranferred;
        db.insertHistory("Transaction type: debit(transfer):\n" +
                "Previous balance => " + (balance + tranferred) + "\nTo:" +
                name + " " + LocalDate.now().getDayOfMonth() + "-" +
                LocalDate.now().getMonthValue() + "-" +
                LocalDate.now().getYear() + " @ " +
                LocalTime.now().getHour() + ":" +
                LocalTime.now().getMinute() + " Amount: " +
                tranferred + " Balance => " + balance);
        db.changeBalance(balance);
        System.out.println("Transaction successful");
        System.out.println("Total balance: " + balance);
        backOrQuit();
    }

    private void deposit() {
        getInputs = new Scanner(System.in);
        System.out.println("Acc name: ");
        String s = getInputs.nextLine();
        System.out.println("Amount: ");
        float deposit = getInputs.nextFloat();

        float balance = Float.parseFloat(db.getHistory()
                .split("\n")[0].split(":")[1].trim());
        balance += deposit;
        db.insertHistory("Transaction type: credit(deposit): " +
                "Previous balance => " + (balance - deposit) + "\nFrom:" +
                s + " " + LocalDate.now().getDayOfMonth() + "-" +
                LocalDate.now().getMonthValue() + "-" +
                LocalDate.now().getYear() + " @ " +
                LocalTime.now().getHour() + ":" +
                LocalTime.now().getMinute() + " amount: " +
                deposit + " Balance => " + balance);
        db.changeBalance(balance);
        System.out.println("Transaction successful");
        System.out.println("Total balance: " + balance);
        backOrQuit();
    }

    private void withdraw() {
        float balance = Float.parseFloat(db.getHistory()
                .split("\n")[0].split(":")[1].trim());
        float withdraw;
        do {
            System.out.println("Total balance: " + balance);
            System.out.println("Enter amount to withdraw, please" +
                    " note amount can't be more than " + balance);
            withdraw = getInputs.nextFloat();
        } while (withdraw > balance);
        balance -= withdraw;
        db.insertHistory("Transaction type: debit(withdrawal): " +
                "Previous balance => " + (balance + withdraw) + "\n" +
                LocalDate.now().getDayOfMonth() + "-" +
                LocalDate.now().getMonthValue() + "-" +
                LocalDate.now().getYear() + " @ " +
                LocalTime.now().getHour() + ":" +
                LocalTime.now().getMinute() + " Amount: " +
                withdraw + " Balance => " + balance);
        db.changeBalance(balance);
        System.out.println("Transaction successful");
        System.out.println("Total balance: " + balance);
        backOrQuit();
    }

    private void history() {
        System.out.print(db.getHistory());
        backOrQuit();
    }

    private void backOrQuit() {
        int result;
        getInputs = new Scanner(System.in);
        do {
            System.out.println("""
                    Please enter an action to perform:
                    0. Back
                    1. Quit
                    """);
            result = getInputs.nextInt();
        } while (result < 0 || result > 1);
        if (result == 0) generalInterface();
        else quit();
    }

    public void generalInterface() {
        int face;
        do {
            System.out.println("Welcome " + user.id +
                    "!\nPlease enter an action to perform:\n" +
                    "1. Transactions History\n" +
                    "2. Withdraw\n" +
                    "3. Deposit\n" +
                    "4. Transfer\n" +
                    "5. Quit ");
            face = getInputs.nextInt();
        } while (face < 1 || face > 5);
        if (face == 1) history();
        if (face == 2) withdraw();
        if (face == 3) deposit();
        if (face == 4) transfer();
        if (face == 5) quit();
    }

    public record User(String id, int pin) {
    }

    private class DataBase {
        private File parent, currentUser;
        private PrintWriter writer;

        public DataBase() {
            try {
                parent = new File("E:\\.ATM_DB");
                if (!parent.exists()) {
                    //create the root as a directory
                    parent.mkdir();
                }
            } catch (Exception ignored) {

            }
        }


        public void getDetails(String id, int pin) {
            String delimeter = "@db";
            if (Objects.requireNonNull(parent.listFiles()).length > 0)
                for (File f : parent.listFiles()) {
                    String[] data = f.getName().split(delimeter);
                    if (data[0].matches(id) && data[1].matches(String.valueOf(pin)))
                        currentUser = f;
                }

            if (currentUser == null) {
                String newUser = id + delimeter + pin;
                try {
                    File nu = new File(parent, newUser);
                    if (!nu.exists())
                        if (nu.createNewFile()) {
                            writer = new PrintWriter(new FileWriter(nu, true), true);
                            writer.println("Total Balance: 10000.00");
                            writer.close();
                            currentUser = nu;
                        }
                } catch (Exception ignored) {
                }
            }
        }

        public void insertHistory(String details) {
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(currentUser, true)), true);
                writer.println(details);
                writer.close();
            } catch (Exception ignored) {
            }
        }

        public void changeBalance(float amount) {
            StringBuilder history = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(currentUser));
                String read = reader.readLine();
                read = reader.readLine();
                while (read != null) {
                    history.append(read).append("\n");
                    read = reader.readLine();
                }
                reader.close();

                File tmp = new File("temp");
                writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp, true)), true);
                writer.println("Total Balance: " + amount);
                writer.println(history);
                writer.close();
                if (currentUser.delete())
                    tmp.renameTo(currentUser);
            } catch (Exception ignored) {
            }
        }

        public String getHistory() {
            StringBuilder history = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(currentUser));
                String read = reader.readLine();
                while (read != null) {
                    history.append(read).append("\n");
                    read = reader.readLine();
                }
                reader.close();
            } catch (Exception ignored) {
            }
            return history.toString();
        }
    }
}
