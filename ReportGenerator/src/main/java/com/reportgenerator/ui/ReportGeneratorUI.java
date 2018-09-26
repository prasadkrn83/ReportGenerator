package com.reportgenerator.ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ReportGeneratorUI extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Reports Generator");
		primaryStage.setMaximized(true);

		Group root = new Group();
		Scene scene = new Scene(root, Color.WHITE);
		TabPane tabPane = new TabPane();
		BorderPane borderPane = new BorderPane();
		
		intializeTabs(tabPane);
		
		
		tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
		    @Override
		    public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
		        // do something...
		    	System.out.println(newValue);
		    }
		});
		
		
		// bind to take available space
		borderPane.prefHeightProperty().bind(scene.heightProperty());
		borderPane.prefWidthProperty().bind(scene.widthProperty());

		borderPane.setCenter(tabPane);
		root.getChildren().add(borderPane);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void intializeTabs(TabPane tabPane) {
		
		Tab tab1 = new Tab();
		tab1.setText("Generate Report");
		HBox hbox1= new HBox();
		tab1.setClosable(false);
		hbox1.getChildren().add(new Label("Generating report..."));
		hbox1.setAlignment(Pos.CENTER);
		tab1.setContent(hbox1);
		tabPane.getTabs().add(tab1);
		
		Tab tab2 = new Tab();
		tab2.setText("Configure Reports");
		HBox hbox2 = new HBox();
		tab2.setClosable(false);
		hbox2.getChildren().add(new Label("Configure Reports..."));
		hbox2.setAlignment(Pos.CENTER);
		tab2.setContent(hbox2);
		tabPane.getTabs().add(tab2);
		
		Tab tab3 = new Tab();
		tab3.setText("Add Report");
		HBox hbox3 = new HBox();
		tab3.setClosable(false);
		hbox3.getChildren().add(new Label("Add report..."));
		hbox3.setAlignment(Pos.CENTER);
		tab3.setContent(hbox3);
		tabPane.getTabs().add(tab3);
		
	}
}