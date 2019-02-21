package app;

import java.time.ZoneId;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GUI {
	
	//configuration UI
	@SuppressWarnings("unchecked")
	public static void configurationStage(Stage stage, Configuration conf) {
		
		//create new window
		Stage configure = new Stage();
        configure.initModality(Modality.APPLICATION_MODAL);
        configure.setTitle("Konfiguracja");
        configure.setWidth(300);
        configure.setHeight(550);
        
        //grid containing all elements
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Scene configureScene = new Scene(grid); 
        
        //create list and fill it with training names from actual configuration
        ObservableList<String> trainingNames = FXCollections.observableArrayList();
        trainingNames.addAll(conf.namesArray());
        if(trainingNames.size() < 20) {
        	for(int i = trainingNames.size(); i < 20; i++) {
        		trainingNames.add("");
        	}
        }
        
        //create editable table
        TableView<String> trainingNamesTable = new TableView<String>();
        trainingNamesTable.setEditable(true);
        
        //create column showing button number connected with training name (from 0 to 19)
        TableColumn<String, Integer> buttonNrCol = 
        		new TableColumn<String, Integer>("Numer przycisku");
        buttonNrCol.setCellFactory(col -> {
        return new TableCell<String, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty); 
                try {
                	setText(this.getTableRow().getIndex() + "");
                } catch(NullPointerException e) {}
            }
        };
        });
        
        //create column showing training names
        TableColumn<String, String> nameCol = 
        		new TableColumn<String, String>("Nazwa treningu");
        nameCol.setCellValueFactory(
        		new Callback<CellDataFeatures<String, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(CellDataFeatures<String, String> p) {
                return new SimpleStringProperty(p.getValue());
            }
         });
        
        //make this column editable
        nameCol.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        nameCol.setOnEditCommit(
                (CellEditEvent<String, String> t) -> {
                    trainingNames.set(t.getTablePosition().getRow(), t.getNewValue());
                }
        );
        
        //fill table with appropriate data
        trainingNamesTable.setItems(trainingNames);
        
        //add columns to table
        trainingNamesTable.getColumns().addAll(buttonNrCol, nameCol);
        
        //create button to save changes
        final Button applyButton = new Button("Zapisz i wyjd�");
        applyButton.setOnAction(e -> {
        	conf.clear();
        	for(int i = 0; i < 20; i++) {
            	if(!nameCol.getCellData(i).isEmpty()) {
            		conf.addName(nameCol.getCellData(i));
            	}
        	}
        	Parser.saveConfiguration(conf);
        	configure.close();
        	stage.close();
        });
        
        //create button to leave configuration mode
        final Button cancelButton = new Button("Anuluj");
        cancelButton.setOnAction(e -> {
        	configure.close();
        });
        
        //add buttons and table to grid
        grid.add(applyButton, 0, 0);
        grid.add(cancelButton, 1, 0);
        grid.add(trainingNamesTable, 0, 1, 2, 1);
        
        //add scene to stage and show it
        configure.setScene(configureScene);
        configure.show();

	}
	
	//UI for generating reports
	public static void reportStage(Configuration conf) {
		
		//create new window
		Stage report = new Stage();
    	report.initModality(Modality.APPLICATION_MODAL);
    	report.setTitle("Raport");
    	report.setWidth(350);
    	report.setHeight(200);
        
    	//grid containing all elements
        GridPane reportGrid = new GridPane();
        reportGrid.setAlignment(Pos.TOP_CENTER);
        reportGrid.setHgap(10);
        reportGrid.setVgap(10);
        reportGrid.setPadding(new Insets(20, 20, 20, 20));

        Scene reportScene = new Scene(reportGrid); 
        
        //label indicating where to put "from" date
        Label from = new Label("Od kiedy:");
        reportGrid.add(from, 0, 0);
        
        //label indicating where to put "to" date
        Label to = new Label("Do kiedy:");
        reportGrid.add(to, 0, 1);
        
        //datepicker to enter "from" date
        final DatePicker dateFrom = new DatePicker(
        		new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        reportGrid.add(dateFrom, 1, 0);
        
        //datepicker to enter "to" date
        final DatePicker dateTo = new DatePicker(
        		new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        reportGrid.add(dateTo, 1, 1);
        
        //label indicating where to put training name
        Label name = new Label("Nazwa treningu");
        reportGrid.add(name, 0, 2);
        
        //field for choosing training name
        ChoiceBox<String> cb = new ChoiceBox<String>(
        		FXCollections.observableArrayList(
        				conf.namesArray())
        );
        reportGrid.add(cb, 1, 2);
        
        //button generating and saving to filesystem pdf report
        Button generate = new Button("Generuj raport");
        generate.setOnAction(e -> {
        	PDF.generateReport(cb.getValue(), dateFrom.getValue(), dateTo.getValue());
        });
        
        //just for centering button
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().add(generate);
        reportGrid.add(hb, 0, 3, 2, 1);
        
        //add scene to stage and show
        report.setScene(reportScene);
        report.show();
	}
	
	//window showing when saved trainings
	public static void saved() {
		
		//create new window
		Stage savedStage = new Stage();
		savedStage.initModality(Modality.APPLICATION_MODAL);
		savedStage.setTitle("Zapisano!");
		savedStage.setWidth(150);
		savedStage.setHeight(150);
		       
		//grid containing all elements
		GridPane savedGrid = new GridPane();
		savedGrid.setAlignment(Pos.CENTER);
		savedGrid.setHgap(10);
		savedGrid.setVgap(10);
		savedGrid.setPadding(new Insets(20, 20, 20, 20));
		
		Scene savedScene = new Scene(savedGrid); 
		
		//label saying that trainings where saved
        Label saved = new Label("Zapisano!");
        saved.setFont(new Font("Arial", 18));
        savedGrid.add(saved, 0, 0);
        
        //button closing window
        Button close = new Button("Ok!");
        close.setOnAction(e -> {
        	savedStage.close();
        });
        
        //just for centering button
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().add(close);
        savedGrid.add(hb, 0, 1);
        
        //add scene to stage and show
        savedStage.setScene(savedScene);
        savedStage.show();
	}
	
	//window showing when there is no configuration.json file found on disk (usually first use)
	public static void confFileNotFoundException() {
			
		//create new window
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("B��d!");
		stage.setWidth(350);
		stage.setHeight(250);
		       
		//grid containing all elements
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 20, 20, 20));
		
		Scene scene = new Scene(grid); 
		
		//text saying what is the problem
	    Text text = new Text("Nie znaleziono pliku \"configuration.json\". Je�eli jest to pierwsze u�ycie "
	    		+ "programu, zignoruj t� informacj�. W przeciwnym wypadku - sprawd�, czy "
	    		+ "nazwy trening�w na pewno s� prawid�owe i popraw je w razie potrzeby!");
	    text.setFont(new Font("Arial", 15));
	    text.wrappingWidthProperty().set(300);
	    text.setTextAlignment(TextAlignment.JUSTIFY);
	    grid.add(text, 0, 0);
	       
	    //button closing window
	    Button close = new Button("Ok!");
	    close.setOnAction(e -> {
	      	stage.close();
	    });
	        
	    //just for centering button
	    HBox hb = new HBox(10);
	    hb.setAlignment(Pos.CENTER);
	    hb.getChildren().add(close);
	    grid.add(hb, 0, 1);
	        
	    //add scene to stage and show
	    stage.setScene(scene);
	    stage.show();
	    
	}
		
}
