package sample;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class AccountPageController {

    //Registering Controllers
    @FXML
    static Label welcomeUserName;
    @FXML
    TextField amountInput;
    @FXML
    Button depositButton, withdrawButton, showDetailsButton, exitButton;
    @FXML
    TextArea accountTextArea;

    //Account attributes
    private static int account_number;
    private static String atm_pin;
    private static String name;
    private static String number;
    private static String email;
    private static int balance;

    //For JDBC Connectivity
    public static Connection connection = null;
    public static PreparedStatement preparedStatement = null;
    public static ResultSet resultSet = null;

    Alert a = new Alert(Alert.AlertType.NONE);
    String detailsMsg ="";



    //Initializing all the user information
    public static void initAccount(int accNumber){
        //Connecting to Database
        try
        {
            //Registering Driver Class
            Class.forName("com.mysql.jdbc.Driver");
            connection = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/dbase","root","imghostrider1");
            System.out.println("Connection with database established!");
        }
        catch(Exception exception)
        {
            System.out.println("Connection with database NOT established.");
            JOptionPane.showMessageDialog(null, exception);
        }

        //Executing Query to extract current user information
        try{
            String sql = "select * from accountholder where accountNumber = ?;";
            preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(accNumber));
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                account_number = accNumber;
                atm_pin = resultSet.getString("accountPin");
                name = resultSet.getString("holderName");
                number = resultSet.getString("holderNumber");
                email = resultSet.getString("holderEmail");
                balance = Integer.parseInt(resultSet.getString("holderBalance"));
                System.out.println("Account Initialized.");
            }
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
    }

    //Checking if the input field is empty
    public boolean checkEmpty(String msg){
        if(amountInput.getText().equals("")){
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText(msg);
            a.show();
            return false;
        }
        else
            return true;
    }

    //For amount deposit
    public void depositButtonClicked() throws Exception {
        if(checkEmpty("Please enter some amount to deposit.")){
            int newAmount = balance + Integer.parseInt(amountInput.getText());
            String sql = "update accountholder set holderBalance = +"+newAmount+" where accountNumber = "+account_number+";";
            Statement st = connection.createStatement();
            st.executeUpdate(sql);
            a.setAlertType(Alert.AlertType.INFORMATION);
            a.setContentText(" Rs. "+amountInput.getText()+"/- deposited successfully.");
            a.showAndWait();
            detailsMsg += "\n\nDEPOSIT----------"+
                    "\nOLD BALANCE: "+balance+
                    "\nDEPOSITED AMOUNT: "+Integer.parseInt(amountInput.getText())+
                    "\nUPDATED BALANCE: "+newAmount+"\n\n"+
                    "---------END---------";
            accountTextArea.setText(detailsMsg);
        }
    }

    //For amount withdrawal
    public void withdrawButtonClicked() throws Exception {
        if(checkEmpty("Please enter some amount to wihdraw.")){
            if(canWithdraw(Integer.parseInt(amountInput.getText()))){
                int newAmount = balance - Integer.parseInt(amountInput.getText());
                String sql = "update accountholder set holderBalance = +"+newAmount+" where accountNumber = "+account_number+";";
                Statement st = connection.createStatement();
                st.executeUpdate(sql);
                a.setAlertType(Alert.AlertType.INFORMATION);
                a.setContentText(" Rs. "+amountInput.getText()+"/- Withdrawn successfully.");
                a.showAndWait();
                detailsMsg += "\n\nWITHDRAWAL----------"+
                        "\nOLD BALANCE: "+balance+
                        "\nWITHDRAWAL AMOUNT: "+Integer.parseInt(amountInput.getText())+
                        "\nUPDATED BALANCE: "+newAmount+"\n\n"+
                        "---------END---------";
                accountTextArea.setText(detailsMsg);
            }
            else{
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("Insufficient Funds!!");
                a.show();
            }
        }
    }

    //Check if user has sufficient balance for making a withdrawal
    private boolean canWithdraw(int amount){
        if(balance >= amount)
            return true;
        else
            return false;
    }

    //Show account details.
    public void showDetailsButtonClicked(){
        accountTextArea.clear();
        initAccount(account_number);
        detailsMsg = null;
        detailsMsg ="\nACCOUNT DETAILS: \n"+
                "\nAccount Number: "+account_number+
                "\nOwner Name: "+name+
                "\nContact Number: "+number+
                "\nE-mail: "+email+
                "\nCurrent Balance: Rs "+balance+" /-"+
                "\n\n---------END---------";
        accountTextArea.setText(detailsMsg);
    }

    //Exit the program
    public void exitButtonClicked(){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
