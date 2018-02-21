package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import java.io.*;
import java.util.*;



public class Main extends Application {

    private TextField _text1;
    private TextField _text2;
    private Button _btn1;
    private Button _btn2;
    private Button _btn3;
    protected File train;
    protected File test;


    public static void main(String[] args) {
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Spam Master 3000");

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        GridPane left = new GridPane();
        left.setPadding(new Insets(10, 10, 10 ,10));

        Label btnLabel = new Label();
        _btn1 = new Button("Open Training Set");
        _btn1.setPadding(new Insets(10));
        left.add(_btn1, 0, 0);

        Label trainSet = new Label();
        _text1 = new TextField();
        left.add(_text1, 1, 0);
        left.add(trainSet, 1, 3);
        _text1.setMinWidth(300);
        _text1.setPromptText("Select the Training data path");

        _btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setInitialDirectory(new File("."));
                File file = directoryChooser.showDialog(primaryStage);
                System.out.println("Success");
                if (file == null) {
                    trainSet.setText("No Directory Selected");
                } else {
                    _text1.setText(file.getPath());
                    trainSet.setText("");
                }
                train = file;
            }
        });

        Label btnLabel2 = new Label();
        _btn2 = new Button("Open Test Set");
        _btn2.setPadding(new Insets(10));
        left.add(_btn2, 0, 1);

        Label testSet = new Label();
        _text2 = new TextField();
        left.add(_text2, 1, 1);
        left.add(testSet, 1, 3);
        _text2.setMinWidth(300);
        _text2.setPromptText("Select the Test data path");

        _btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setInitialDirectory(new File("."));
                File file = directoryChooser.showDialog(primaryStage);
                System.out.println("Success2");
                if (file == null) {
                    trainSet.setText("");
                    testSet.setText("No Directory Selected");
                } else {
                    _text2.setText(file.getPath());
                    testSet.setText("");
                }
                test = file;
            }

        });

        Label btnLabel3 = new Label();
        _btn3 = new Button("Submit");
        _btn3.setPadding(new Insets(10));
        layout.setBottom(_btn3);
        layout.setAlignment(_btn3, Pos.BOTTOM_RIGHT);



        layout.setLeft(left);
        Scene scene = new Scene(layout, 500, 200);
        primaryStage.setScene(scene);
        primaryStage.show();



    }

}
