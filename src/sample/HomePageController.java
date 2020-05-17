package sample;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class HomePageController {

    //Registering controllers
    @FXML
    TextField accountInput;
    @FXML
    PasswordField pinInput;
    @FXML
    Button submitButton;
    @FXML
    Button createButton;
    @FXML
    MenuItem aboutItem;

    //For JDBC Connectivity
    public static Connection connection = null;
    public static PreparedStatement preparedStatement = null;
    public static ResultSet resultSet = null;

    //Layout
    Stage stage = new Stage();
    Scene scene;

    //Alert
    Alert a = new Alert(Alert.AlertType.NONE);

    //About window
    public void aboutClicked(){
        AlertBox.display("About","This is a JavaFX project created by Arun Kumar.");
    }

    public void submitClicked(ActionEvent event){
        String accountNumber = accountInput.getText();
        String accountPin = pinInput.getText();

        if(accountNumber.equals("") || accountPin.equals("")){
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Please enter Account number and Pin.");
            a.show();
        }
        else{
                //Connecting to Database
                try
                {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/dbase","root","imghostrider1");
                    System.out.println("Connection with database established!");
                }
                catch(Exception exception)
                {
                    System.out.println("Connection with database NOT established.");
                    JOptionPane.showMessageDialog(null, exception);
                }

                //Executing Query
                try{
                    String sql = "select * from accountholder where accountNumber = ? and accountPin = ?;";
                    preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
                    preparedStatement.setString(1, accountNumber);
                    preparedStatement.setString(2, accountPin);
                    resultSet = preparedStatement.executeQuery();
                    if(!resultSet.next()){
                        a.setAlertType(Alert.AlertType.ERROR);
                        a.setContentText("Please enter correct username or password.");
                        a.show();
                    }else{
                        Node node = (Node)event.getSource();
                        stage = (Stage) node.getScene().getWindow();
                        stage.close();
                        scene = new Scene(FXMLLoader.load(getClass().getResource("AccountPage.fxml")));
                        AccountPageController.initAccount(Integer.parseInt(accountNumber));

                        stage.setScene(scene);
                        stage.show();
                    }
                }
                catch(Exception exception){
                    exception.printStackTrace();
                }
        }
    }

    //When user tries to create a new account
    public void createButtonClicked(ActionEvent event){
        try{
            Node node = (Node)event.getSource();
            stage = (Stage) node.getScene().getWindow();
            stage.close();
            scene = new Scene(FXMLLoader.load(getClass().getResource("CreatePage.fxml")));
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
    }
}
