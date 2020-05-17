package sample;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;


public class CreatePageController {

    //Registering Controllers
    @FXML
    TextField nameInput, numberInput, emailInput, initialAmountInput;
    @FXML
    Button createAccountButton;

    //For JDBC Connectivity
    public static Connection connection = null;
    public static PreparedStatement preparedStatement = null;
    public static ResultSet resultSet = null;

    //Layout
    Stage stage = new Stage();
    Scene scene;

    //Alert
    Alert a = new Alert(Alert.AlertType.NONE);

    private int newAccountNumber, newAccountPin;
    private String creationMsg;

    public void createAccountButtonClicked(ActionEvent event){
        String name = nameInput.getText().trim();
        String number = numberInput.getText().trim();
        String email = emailInput.getText().trim();
        int initialAmount = Integer.parseInt(String.valueOf(initialAmountInput.getText().trim()));

        //Connecting to Database
        try
        {
            //Registering Driver Class
            Class.forName("com.mysql.jdbc.Driver");
            connection = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/DATABASE_NAME","root","YOUR_PASSWORD_HERE");
            System.out.println("Connection with database established!");
        }
        catch(Exception exception)
        {
            System.out.println("Connection with database NOT established.");
            JOptionPane.showMessageDialog(null, exception);
        }

        //Executing Query
        try{
            //New Account number is last account number + 1
            String query1 = "select max(accountNumber) from accountholder;";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query1);
            rs.next();
            newAccountNumber = Integer.parseInt(String.valueOf(rs.getString("max(accountNumber)"))) + 1;


            //New Account's ping will be a random 4 digit number.
            Random random = new Random();
            newAccountPin = Integer.parseInt(String.format("%04d", random.nextInt(10000000) % 10000));

            //SQL Query
            String sql = "insert into accountholder values("+
                    String.valueOf(newAccountNumber)+","+
                    String.valueOf(newAccountPin)+","+"\""+
                    name+"\""+","+number+","+"\""+email+"\""+","+initialAmount+")";

            //Executing query to insert new row into table
            if(!(st.executeUpdate(sql)==1)){
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("Account Creation Unsuccessful.");
                a.show();
            }else{

                //Giving user his new account's information.
                a.setAlertType(Alert.AlertType.CONFIRMATION);
                creationMsg = "New Account Creation Successful, details are: \n"+
                        "Name: "+name+"\nMo. Number: "+number+"\nE-mail: "+email+"\n"+
                        "ACCOUNT NUMBER: "+newAccountNumber+"\nACCOUNT PIN: "+newAccountPin+
                        "\nINITIAL AMOUNT: "+initialAmount+"\n\nClick OK to continue.";
                a.setContentText(creationMsg);
                a.showAndWait();
                Node node = (Node)event.getSource();
                stage = (Stage) node.getScene().getWindow();
                stage.close();

                //As soon as the new account is created, the user is sent to Account Page
                scene = new Scene(FXMLLoader.load(getClass().getResource("AccountPage.fxml")));
                AccountPageController.initAccount(newAccountNumber);
                stage.setScene(scene);
                stage.show();
            }
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
    }

}
