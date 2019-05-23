package visualization;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GameOfLifeController implements Initializable {

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private ChoiceBox<States> stateChoiceBox;

    @FXML
    private TextField nSizeField;

    @FXML
    private TextField mSizeField;

    @FXML
    private Button updateButton;

    @FXML
    private GridPane visualizationPane;

    @FXML
    private AnchorPane wrapperOfVisualization;

    private GameOfLife gameOfLife;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gameOfLife = new GameOfLife(60, 40);
        stateChoiceBox.setItems(FXCollections.observableArrayList(States.values()));
        stateChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((ObservableValue<? extends States> observable, States oldValue, States newValue) -> {
                    if (newValue != null) {
                        gameOfLife.updateState(newValue);
                    }
                });
        resizeVisualization(60, 40);
        startButton.setOnAction(e -> gameOfLife.playGame());
        stopButton.setOnAction(e -> gameOfLife.stopGame());
        updateButton.setOnAction(e -> {
            int n = Integer.parseInt(nSizeField.getText());
            int m = Integer.parseInt(mSizeField.getText());
            if (n > 5 && m > 5)
                resizeVisualization(n, m);
        });
    }


    public void resizeVisualization(int n, int m) {
        gameOfLife.resizeGrid(n, m);
        Grid grid = gameOfLife.getGrid();
        cleanVisualizationPane();
  /*      double width = 100. / m;
        double height = 100. / n;
        for (int i = 0; i < m; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setHgrow(Priority.ALWAYS);
            colConst.setPercentWidth(width);
            visualizationPane.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < n; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setVgrow(Priority.ALWAYS);
            rowConst.setPercentHeight(height);
            visualizationPane.getRowConstraints().add(rowConst);
        }
*/
        double width = visualizationPane.getWidth()/m;
        double height = visualizationPane.getHeight()/n;

        int size = (int)(width > height ? height:width);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                StackPane square = new StackPane();
                updateColor(square, false);
                square.setMinSize(size,size);
                final int ii = i;
                final int jj = j;
                square.setOnMouseClicked(e -> {
                    gameOfLife.updateCell(ii, jj);

                });
                grid.getCell(i, j).aliveProperty().addListener((e) -> {
                    updateColor(square, grid.getCell(ii, jj).isAlive());
                });

                visualizationPane.add(square, j, i);
            }

    }

    private void cleanVisualizationPane() {

        visualizationPane.getChildren().clear();
        while (visualizationPane.getRowConstraints().size() > 0) {
            visualizationPane.getRowConstraints().remove(0);
        }
        while (visualizationPane.getColumnConstraints().size() > 0) {
            visualizationPane.getColumnConstraints().remove(0);
        }

       
    }

    private void updateColor(StackPane square, boolean alive) {
        if (alive) {
            square.setStyle("-fx-background-color: black; -fx-border-color: black");
        } else {
            square.setStyle("-fx-background-color: white; -fx-border-color: black");
        }

    }
}
