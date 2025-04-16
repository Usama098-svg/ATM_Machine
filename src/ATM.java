
//This Project Design by UA Tech from Gujranwala,Pakistan....
//Contact us: uahmed098@gmail.com


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

class ATMMachine{
    Connection connection;
    Scanner sc;
    String enteredPIN;

    public ATMMachine(Connection connection, Scanner sc){
        this.connection = connection;
        this.sc = sc;
    }

    public void checkPIN(){
        int tryPIN = 0;
       try {
        String query = "SELECT * FROM atm WHERE atm_pin = ?";
        while (tryPIN < 3) {
            System.out.print("Enter your PIN: ");
            enteredPIN = sc.nextLine();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, enteredPIN);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                System.out.println("Access Granted!");
                mainMenu(enteredPIN); // Call your main menu method
                return;
            } else {
                tryPIN++;
                if (tryPIN == 3) {
                    System.out.println("You have entered the wrong PIN 3 times. Access Denied!");
                    break;
                }
                System.out.println("Invalid PIN! Please try again...");
            }
        }
       } catch (SQLException e) {
        System.out.println(e.getMessage());
       }
    }

    public void mainMenu(String enteredPIN){
        System.out.println("");
        System.out.println("----------WELCOME UA-TEACH ATM MACHINE----------");
        System.out.println("");
        System.out.println("1] Check Balance");
        System.out.println("2] Withdraw Money");
        System.out.println("3] Deposit Money");
        System.out.println("4] Exit");
        System.out.println("");
        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();
        switch (choice) {
            case 1 -> checkBalance();
            case 2 -> withdrawMoney();
            case 3 -> depositMoney();
            case 4 -> {
                return;
            }
            default -> System.out.println("Invlaid choice. Plz enter valid choice...");
        }

    }

    public void checkBalance(){
       try {
        String check_balance_query = "SELECT * FROM atm WHERE atm_pin = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(check_balance_query);
        preparedStatement.setString(1, enteredPIN);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            float balance = rs.getFloat("balance");
            System.out.print("Balance : "+balance);
            mainMenu(enteredPIN);
        } else {
            System.out.println("Balance not display.");
        }
        
       } catch (SQLException e) {
            System.out.println(e.getMessage());
       }
    }

    public void withdrawMoney(){
        try {
            System.out.print("Enter amount to withdraw: ");
            float withdraw_amount = sc.nextFloat();
            String query1 = "SELECT * FROM atm WHERE atm_pin = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setString(1, enteredPIN);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                float currnet_balance = rs.getFloat("balance");
                if (withdraw_amount > currnet_balance) {
                    System.out.println("Insufficient Balance");
                } else {
                    String query = "UPDATE atm SET balance = balance - ? WHERE atm_pin = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(query);
                    preparedStatement1.setFloat(1, withdraw_amount);
                    preparedStatement1.setString(2, enteredPIN);
                    int rowEffected = preparedStatement1.executeUpdate();
                    if (rowEffected > 0) {
                        System.out.println("Money withdraw successfully.");
                    } else {
                        System.out.println("Money withdraw failed.");
                    }
                }
            } else {
                System.out.println("Not account to given atm pin.");
            }
            mainMenu(enteredPIN);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void depositMoney(){
        try {
            System.out.print("Enter amount to deposit: ");
            float deposit_amount = sc.nextFloat();
           
                String query = "UPDATE atm SET balance = balance + ? WHERE atm_pin = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(query);
                preparedStatement1.setFloat(1, deposit_amount);
                preparedStatement1.setString(2, enteredPIN);
                int rowEffected = preparedStatement1.executeUpdate();
                if (rowEffected > 0) {
                    System.out.println("Money deposited successfully.");
                } else {
                    System.out.println("Money deposit failed.");
                }
           
            mainMenu(enteredPIN);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

public class ATM {
    private static final String url = "jdbc:mysql://localhost:3306/atm_machine";
    private static final String username = "root";
    private static final String password = "Usama@123456";
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully.");
            Scanner sc = new Scanner(System.in);
            ATMMachine obj = new ATMMachine(connection, sc);
            obj.checkPIN();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
    }
}
