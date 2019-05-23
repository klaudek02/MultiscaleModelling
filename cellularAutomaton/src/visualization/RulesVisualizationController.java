package visualization;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;



import java.net.URL;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.ResourceBundle;

public class RulesVisualizationController implements Initializable {

    @FXML
    private GridPane visualizationGridPane;

    @FXML
    private Spinner<Integer> sizeN;

    @FXML
    private Spinner<Integer> sizeM;

    @FXML
    private Button updateSizeButton;

    @FXML
    private ChoiceBox<Rules> rulesChoiceBox;

    private RulesCalculator rulesCalculator;
    private ObservableList<ObservableList<Integer>> arrayRules;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rulesCalculator = new RulesCalculator();
        sizeM.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500));
        sizeN.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500));
        rulesChoiceBox.setItems(FXCollections.observableArrayList(Rules.values()));
        rulesChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends Rules> observable, Rules oldValue, Rules newValue) -> {
                    if(newValue != null) arrayRules = rulesCalculator.calculate(newValue, arrayRules);
                    setColorsOnRectangles();
                   });
        updateSizeButton.setOnAction((e)->{initializeArray();updateVisualization();});

        System.out.println(visualizationGridPane.getHeight());
    }

    private void updateVisualization() {
        resizeVisualization();
        setColorsOnRectangles();
        rulesChoiceBox.setValue(null);
    }
    private void setColorsOnRectangles()
    {
        FilteredList<Node> rectangles = visualizationGridPane.getChildren().filtered(n -> n instanceof StackPane);
        for(int i = 0; i < arrayRules.size(); i++)
        {
            for(int j = 0; j < arrayRules.get(0).size();j++)
            {
                StackPane rectangle = (StackPane) rectangles.get(i*arrayRules.get(0).size() + j);
                if(arrayRules.get(i).get(j) == 1)
                    rectangle.setStyle("-fx-background-color: black; -fx-border-color: black;-fx-border-width: 0.1px");
                else
                    rectangle.setStyle("-fx-background-color: white; -fx-border-color: black;-fx-border-width: 0.1px");
            }
        }
    }
    public void resizeVisualization()
    {
        visualizationGridPane.getChildren().clear();
        while(visualizationGridPane.getRowConstraints().size() > 0){
            visualizationGridPane.getRowConstraints().remove(0);
        }
        while(visualizationGridPane.getColumnConstraints().size() > 0){
            visualizationGridPane.getColumnConstraints().remove(0);
        }
       //visualizationGridPane.resize(20,20);
     /*   double sizee = 0;
        double width =  100./ arrayRules.get(0).size();
        double height = 100./ arrayRules.size();
        sizee = width > height ? height : width;  // to change
        for (int i = 0; i < arrayRules.get(0).size(); i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setHgrow(Priority.ALWAYS);
            colConst.setPercentWidth(sizee);
            visualizationGridPane.getColumnConstraints().add(colConst);
        }
        for(int i = 0; i < arrayRules.size(); i++){
            RowConstraints rowConst = new RowConstraints();
            rowConst.setVgrow(Priority.ALWAYS);
            rowConst.setPercentHeight(sizee);
            visualizationGridPane.getRowConstraints().add(rowConst);
        }
*/
    double width = (visualizationGridPane.getWidth()/(double)sizeM.getValue());
    double height = visualizationGridPane.getHeight()/(double)sizeN.getValue();
    double size = (width>height?height:width)-0.2;
        for(int i = 0; i < arrayRules.size(); i++)
            for(int j = 0; j <arrayRules.get(0).size(); j++){
                StackPane square = new StackPane();
                square.setMinSize((int)size,(int)size);
                visualizationGridPane.add(square,j,i);
            }
    }

    private void initializeArray()
    {
        arrayRules = FXCollections.observableArrayList();
        for (int i = 0; i < sizeN.getValue(); i++) {
            ObservableList<Integer> row = FXCollections.observableArrayList();
            arrayRules.add(i, row);
            for (int j = 0; j < sizeM.getValue(); j++) {
                row.add(0);
            }
        }
        arrayRules.get(0).set(sizeM.getValue()/2,1);
    }
}