/*
    March 5th, 2018

    Taabish Jeshani - 100621665
<<<<<<< HEAD
    Aleem Alibhai -
=======
    Aleem Alibhai - 
>>>>>>> 84fd9b8dce761a4105b39704d34d8f4fb21980d5
 */

package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.DirectoryChooser;

import java.io.*;



public class Main extends Application {

    private TextField _text1;
    private TextField _text2;
    private Button _btn1;
    private Button _btn2;
    private Button _btn3;
    protected File train;
    protected File test;
    public ObservableList<TestFile> data = FXCollections.observableArrayList();
    private String[] precisionAndAccuracy = new String[2];


    public static void main(String[] args) {
        Application.launch(args);
    }


    @Override
        public void start(Stage primaryStage) throws Exception{
            primaryStage.setTitle("Test/Train Set Directory Selector");

            // Creating the Directory stage layout
            BorderPane layout = new BorderPane();
            layout.setPadding(new Insets(10));
            GridPane left = new GridPane();
            left.setPadding(new Insets(10, 10, 10 ,10));

            // Training set button
            _btn1 = new Button("Open Training Set");
            _btn1.setPadding(new Insets(10, 10, 10, 10));
            left.add(_btn1, 0, 0);

            // Text field for Training set file path
            Label trainSet = new Label();
            _text1 = new TextField();
            left.add(_text1, 1, 0);
            left.add(trainSet, 1, 3);
            _text1.setMinWidth(400);
            _text1.setPromptText("Select the Training data path");

            // Test set button
            _btn2 = new Button("Open Test Set");
            _btn2.setPadding(new Insets(10, 23, 10, 22));
            left.add(_btn2, 0, 1);

            // Text field for Test set file path
            Label testSet = new Label();
            _text2 = new TextField();
            left.add(_text2, 1, 1);
            left.add(testSet, 1, 3);
            _text2.setMinWidth(300);
            _text2.setPromptText("Select the Test data path");

            // Submitting directory buttons
            _btn3 = new Button("Submit");
            _btn3.setPadding(new Insets(10));
            layout.setBottom(_btn3);
            layout.setAlignment(_btn3, Pos.BOTTOM_RIGHT);

            // Using to DirectoryChooser to select Training set path
            _btn1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setInitialDirectory(new File("."));
                    File file = directoryChooser.showDialog(primaryStage);
                    if (file == null) {
                        testSet.setText("");
                        trainSet.setText("Training Set directory not selected");
                    } else {
                        _text1.setText(file.getPath());
                        trainSet.setText("");
                        testSet.setText("");
                    }
                    train = file;
                }
            });

        // Using to DirectoryChooser to select Test set path
        _btn2.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setInitialDirectory(new File("."));
                    File file = directoryChooser.showDialog(primaryStage);
                    if (file == null) {
                        trainSet.setText("");
                        testSet.setText("Test Set directory not selected");
                    } else {
                        _text2.setText(file.getPath());
                        trainSet.setText("");
                        testSet.setText("");
                    }
                    test = file;
                }

            });

            // Displaying the First Scene
            left.setHgap(10);
            left.setVgap(10);
            layout.setLeft(left);
            Scene scene = new Scene(layout, 600, 200);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Submit buttons calls on the second stage
        _btn3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
                try {
                    SpamFilter hamSpam = new SpamFilter(train, test);
                    hamSpam.train();
                    data = hamSpam.test();
                    precisionAndAccuracy = hamSpam.getPrecisionAccuracy();
                } catch (IOException e){
                    System.out.println("Wrong Directory Selected");
                }
                new SecondStage();
            }
        });
    }

    public class SecondStage extends Stage {
        private TableView<TestFile> mail;
        private TextField _text3;
        private TextField _text4;

        // Processes the table of data
        SecondStage() {
            this.setTitle("Spam Master 3000");
            BorderPane layout2 = new BorderPane();

            // Initializing the three columns
            TableColumn<TestFile, String> fileCol = new TableColumn<>("File");
            fileCol.setPrefWidth(400);
            fileCol.setCellValueFactory(new PropertyValueFactory<>("FileName"));

            TableColumn<TestFile, String> classCol = new TableColumn<>("Actual Class");
            classCol.setPrefWidth(200);
            classCol.setCellValueFactory(new PropertyValueFactory<>("ActualClass"));

            TableColumn<TestFile, Double> probCol = new TableColumn<>("Spam Probability");
            probCol.setPrefWidth(300);
            probCol.setCellValueFactory(new PropertyValueFactory<>("SpamProbRounded"));

            // Adding the 3 columns to a TableView
            this.mail = new TableView<>();
            this.mail.getColumns().add(fileCol);
            this.mail.getColumns().add(classCol);
            this.mail.getColumns().add(probCol);

            // Creating a GridPane for Accuracy and Precision
            GridPane bottom = new GridPane();
            bottom.setPadding(new Insets(10));
            bottom.setHgap(10);
            bottom.setVgap(10);

            // Accuracy TextField
            Label accuracy = new Label("Accuracy: ");
            _text3 = new TextField();
            bottom.add(accuracy, 0, 0);
            bottom.add(_text3, 1, 0);
            _text3.setText(precisionAndAccuracy[1]);

            // Precision TextField
            Label precision = new Label("Precision: ");
            _text4 = new TextField();
            bottom.add(precision, 0, 1);
            bottom.add(_text4, 1, 1);
            _text4.setText(precisionAndAccuracy[0]);

            layout2.setCenter(mail);
            layout2.setBottom(bottom);

            // Displaying the second Scene
            Scene scene = new Scene(layout2, 900, 500);
            this.setScene(scene);
            this.show();
            this.mail.setItems(data);
        }

    }

}
