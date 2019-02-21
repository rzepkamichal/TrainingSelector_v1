package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class AppReceiver extends ArduinoDataReceiver{

    private ObservableList<Training> trainings;
    private TableView<Training> table;

    AppReceiver(){
        this.trainings = FXCollections.observableArrayList();
        this.table = new TableView<Training>();
    }

    public void addNewTrainingToList(Training training){
        this.trainings.add(training);
    }

    public ObservableList<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(ObservableList<Training> trainings) {
        this.trainings = trainings;
    }

    public TableView<Training> getTable() {
        return table;
    }

    public void setTable(TableView<Training> table) {
        this.table = table;
    }

    @Override
    public void parseData(int value) {
        if(value < trainings.size()) {
                        //adding entrance to training specified by index from input stream
                        trainings.get(value).addEntrance();
                        table.refresh();
                    }
    }
}
