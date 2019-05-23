package visualization;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class GrainGrowthController implements Initializable {

    @FXML private ComboBox<NucleationType> nucleationTypes;
    @FXML private Button startButton;
    @FXML private TextField nSize;
    @FXML private TextField mSize;
    @FXML private TextField homogeneousRows;
    @FXML private TextField homogeneousColumns;
    @FXML private Button stopButton;
    @FXML private TextField numberOfGrains;
    @FXML private ComboBox<BoundaryCondition> boundaryConditions;
    @FXML private GridPane visualizationGridPane;
    @FXML private ComboBox<NeighborhoodType> growthTypes;
    @FXML private Button resumeButton;
    @FXML private Text errorText;
    @FXML private TextField radiusNucleation;
    @FXML private TextField radiusNeighborhood;

    private GrainGrowth grainGrowth;

    private String[] grainColors;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        grainGrowth = new GrainGrowth(60, 40);
        boundaryConditions.setItems(FXCollections.observableArrayList(BoundaryCondition.values()));
        nucleationTypes.setItems(FXCollections.observableArrayList(NucleationType.values()));
        growthTypes.setItems(FXCollections.observableArrayList(NeighborhoodType.values()));
        stopButton.setOnAction(e -> grainGrowth.stopGame());
        resumeButton.setOnAction(e -> grainGrowth.playGame());
        startButton.setOnAction(e -> {
            errorText.setText("");
            int n, m, homogeneousRows =0 , homogeneousColumns =0, numOfGrains;
            double radiusNucleation = 0.0, radiusNeighborhood = 0.0;
            BoundaryCondition boundaryCondition = boundaryConditions.getValue();
            NucleationType nucleationType = nucleationTypes.getValue();
            NeighborhoodType neighborhoodType = growthTypes.getValue();
            try {
                n = Integer.parseInt(nSize.getText());
                m = Integer.parseInt(mSize.getText());
                if (nucleationType.equals(NucleationType.Homogeneous)) {
                    homogeneousRows = Integer.parseInt(this.homogeneousRows.getText());
                    homogeneousColumns = Integer.parseInt(this.homogeneousColumns.getText());
                    numOfGrains = homogeneousColumns*homogeneousRows;
                }else if(nucleationType.equals(NucleationType.WithRadius)){
                    numOfGrains = Integer.parseInt(numberOfGrains.getText());
                    radiusNucleation = Double.parseDouble(this.radiusNucleation.getText());

                }else
                    numOfGrains = Integer.parseInt(numberOfGrains.getText());
                if(neighborhoodType.equals(NeighborhoodType.RadiusWithCenterOfGravity))
                    radiusNeighborhood = Double.parseDouble(this.radiusNeighborhood.getText());
                if(validateInputs(boundaryCondition,nucleationType, neighborhoodType,
                        n,m,homogeneousColumns,homogeneousRows,numOfGrains,radiusNucleation, radiusNeighborhood)) {
                    makeColorsForGrains(numOfGrains);
                    grainGrowth.setInstance(numOfGrains, boundaryCondition,
                            nucleationType, neighborhoodType,
                            homogeneousRows, homogeneousColumns, radiusNucleation, radiusNeighborhood);
                    resizeVisualization(n, m, numOfGrains);
                    grainGrowth.doNucleationType();
                    grainGrowth.setPlayable();
                }
            }catch (NumberFormatException exception){
                printError("Wrong input data");
            };


        });
    }

    private boolean validateInputs(BoundaryCondition boundaryCondition, NucleationType nucleationType,
                                   NeighborhoodType neighborhoodType, int n, int m, int homogeneousColumns,
                                   int homogeneousRows, int numOfGrains, double radiusNucleation, double radiusNeighborhood) {
        if(boundaryCondition == null || nucleationType == null || neighborhoodType == null){
            printError("select every option");
            return false;
        }else if(n <= 0 || m <= 0){
            printError("wrong grid size");
            return false;
        }else if(homogeneousColumns > m || homogeneousRows > n || homogeneousColumns*homogeneousRows > n*m){
            printError("wrong homogeneous options");
            return false;
        }else if( numOfGrains<=0 || numOfGrains > n*m){
            printError("wrong number of grains");
            return false;
        }//else if( radius <= 1)
        return true;
    }

    private void printError(String error) {
        errorText.setText(error);
    }

    private void makeColorsForGrains(int numberOfGrains) {
        Random random = new Random();
        Set<String> colorsSet = new LinkedHashSet<>();
        colorsSet.add("rgb(255,255,255)");
        for(int i = 1; i <= numberOfGrains; i++){
            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);
            String value = "rgb("+red+","+green+","+blue+")";
            if(!colorsSet.contains(value))
                colorsSet.add(value);
            else
                i--;
        }
        grainColors =  colorsSet.stream().toArray(String[]::new);

    }

    public void resizeVisualization(int n, int m, int numOfGrains) {
        grainGrowth.resizeGrid(n,m);
        Grid grid = grainGrowth.getGrid();
        cleanVisualizationPane();
        int num = numOfGrains;
        double width = visualizationGridPane.getWidth()/(double)m;
        double height = visualizationGridPane.getHeight()/(double)n;

        for (int i = 0; i < m; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setHgrow(Priority.ALWAYS);
            colConst.setPercentWidth(width);
            visualizationGridPane.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < n; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setVgrow(Priority.ALWAYS);
            rowConst.setPercentHeight(height);
            visualizationGridPane.getRowConstraints().add(rowConst);
        }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                StackPane square = new StackPane();
                makeBorderOnSquare(square);
                final int ii = i;
                final int jj = j;
                square.setOnMouseClicked((e)-> {
                    if(grid.setColorIdOnClick(ii,jj))
                        grid.updateCell(ii,jj);
                });
                grid.getCell(i, j).aliveProperty().addListener((e) ->{
                        updateColor(square, grid.getCell(ii,jj).getGrainNumber());
                        });
                visualizationGridPane.add(square, j, i);
            }

    }

    private void cleanVisualizationPane() {

        visualizationGridPane.getChildren().clear();
        while (visualizationGridPane.getRowConstraints().size() > 0) {
            visualizationGridPane.getRowConstraints().remove(0);
        }
        while (visualizationGridPane.getColumnConstraints().size() > 0) {
            visualizationGridPane.getColumnConstraints().remove(0);
        }


    }

        private void updateColor(StackPane square, int numberOfGrain) {
            String colorRGB = grainColors[numberOfGrain];
            square.setStyle("-fx-background-color: "+colorRGB);

        }
        private void makeBorderOnSquare(StackPane square){
            square.setStyle("-fx-background-color: white;");
        }

    }

