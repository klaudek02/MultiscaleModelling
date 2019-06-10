package visualization;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

public class GrainGrowthController implements Initializable {

    @FXML
    public TextField iterationsMC;
    @FXML
    public TextField ktMC;
    @FXML
    private ComboBox<NucleationType> nucleationTypes;
    @FXML
    private Button startButton;
    @FXML
    private TextField nSize;
    @FXML
    private TextField mSize;
    @FXML
    private TextField homogeneousRows;
    @FXML
    private TextField homogeneousColumns;
    @FXML
    private Button stopButton;
    @FXML
    private TextField numberOfGrains;
    @FXML
    private ComboBox<BoundaryCondition> boundaryConditions;
    @FXML
    private GridPane visualizationGridPane;
    @FXML
    private ComboBox<NeighborhoodType> growthTypes;
    @FXML
    private Button resumeButton;
    @FXML
    private Text errorText;
    @FXML
    private TextField radiusNucleation;
    @FXML
    private TextField radiusNeighborhood;
    @FXML
    private Button monteCarlo;
    @FXML
    private Button drx;
    @FXML
    private ComboBox<NeighborhoodType> neighborhoodMC;
    @FXML
    private ComboBox<VisualizationType> switchVisualization;

    private GrainGrowth grainGrowth;
    private VisualizationType visualizationType = VisualizationType.Microstructure;
    private String[] grainColors;

    private StackPane[][] squares;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        grainGrowth = new GrainGrowth(60, 40, errorText);
        boundaryConditions.setItems(FXCollections.observableArrayList(BoundaryCondition.values()));
        nucleationTypes.setItems(FXCollections.observableArrayList(NucleationType.values()));
        growthTypes.setItems(FXCollections.observableArrayList(NeighborhoodType.values()));
        neighborhoodMC.setItems(FXCollections.observableArrayList(NeighborhoodType.Moore, NeighborhoodType.VonNeumann));
        switchVisualization.setItems(FXCollections.observableArrayList(VisualizationType.values()));
        stopButton.setOnAction(e -> grainGrowth.stopGame());
        switchVisualization.setOnAction(e->{
            System.out.println("now");
            if(grainGrowth.isPlayable()){
                grainGrowth.stopGame();
                visualizationType = switchVisualization.getValue();
                changeColorsOnBoard();
            }
        });
        monteCarlo.setOnAction(e -> {
            try {
                double kt = Double.parseDouble(ktMC.getText());
                int iterations = Integer.parseInt(iterationsMC.getText());
                NeighborhoodType neighborhoodType = neighborhoodMC.getValue();
                if (neighborhoodType != null && kt <= 6 && kt >= 0.1)
                    grainGrowth.monteCarlo(neighborhoodType, kt, iterations);
                else
                    printError("select neighborhood for MC");
            } catch (Exception exception) {
                printError("wrong input for MC");
            }
        });
        drx.setOnAction(e->{
            try{
                int iterations = Integer.parseInt(iterationsMC.getText());
                printError("");
                grainGrowth.drx(iterations);
            }catch (Exception exception){
                printError("wrong input for DRX");
            }
        });
        resumeButton.setOnAction(e -> grainGrowth.playGame());
        startButton.setOnAction(e -> {
            errorText.setText("");
            int n, m, homogeneousRows = 0, homogeneousColumns = 0, numOfGrains;
            double radiusNucleation = 2.0, radiusNeighborhood = 2.0;
            BoundaryCondition boundaryCondition = boundaryConditions.getValue();
            NucleationType nucleationType = nucleationTypes.getValue();
            NeighborhoodType neighborhoodType = growthTypes.getValue();
            try {
                n = Integer.parseInt(nSize.getText());
                m = Integer.parseInt(mSize.getText());
                squares = new StackPane[n][m];
                if (nucleationType.equals(NucleationType.Homogeneous)) {
                    homogeneousRows = Integer.parseInt(this.homogeneousRows.getText());
                    homogeneousColumns = Integer.parseInt(this.homogeneousColumns.getText());
                    numOfGrains = homogeneousColumns * homogeneousRows;
                } else if (nucleationType.equals(NucleationType.WithRadius)) {
                    numOfGrains = Integer.parseInt(numberOfGrains.getText());
                    radiusNucleation = Double.parseDouble(this.radiusNucleation.getText());

                } else
                    numOfGrains = Integer.parseInt(numberOfGrains.getText());
                if (neighborhoodType.equals(NeighborhoodType.RadiusWithCenterOfGravity))
                    radiusNeighborhood = Double.parseDouble(this.radiusNeighborhood.getText());
                if (validateInputs(boundaryCondition, nucleationType, neighborhoodType,
                        n, m, homogeneousColumns, homogeneousRows, numOfGrains, radiusNucleation, radiusNeighborhood)) {
                    makeColorsForGrains(numOfGrains);
                    grainGrowth.setInstance(numOfGrains, boundaryCondition,
                            nucleationType, neighborhoodType,
                            homogeneousRows, homogeneousColumns, radiusNucleation, radiusNeighborhood);
                    resizeVisualization(n, m, numOfGrains);
                    grainGrowth.doNucleationType();
                    grainGrowth.setPlayable();
                }
            } catch (NumberFormatException | NullPointerException exception) {
                printError("Wrong input data");
            }
            ;


        });
    }

    private void changeColorsOnBoard() {
        switch(visualizationType){
            case Microstructure:
                colorsMicrostructure();
                break;
            case Energy:
                colorsEnergy();
                break;
            case DensityOfDislocation:
                colorsDensity();
                break;
                
        }
    }

    private void colorsDensity() {
        Cell[][] cells = grainGrowth.getCells();
        for(int i = 0; i < cells.length; i++)
            for(int j = 0; j < cells[0].length;j++)
                updateColorDensity(squares[i][j],cells[i][j].isCrystalized());
    }



    private void colorsMicrostructure() {
        Cell[][] cells = grainGrowth.getCells();
        for(int i = 0; i < cells.length; i++){
            for(int j = 0; j < cells[0].length; j++){
                if(cells[i][j].isCrystalized()) {
                    updateColor(squares[i][j], "rgb(255,0,0)");
                }
                else updateColor(squares[i][j],grainColors[cells[i][j].getGrainNumber()]);
            }
        }
        
    }

    private void colorsEnergy() {
        Cell[][] cells = grainGrowth.getCells();
        for(int i = 0; i < cells.length; i++){
            for(int j = 0; j < cells[0].length; j++){
                updateColorEnergy(squares[i][j],cells[i][j].isChanged());
            }
        }
    }

    private boolean validateInputs(BoundaryCondition boundaryCondition, NucleationType nucleationType,
                                   NeighborhoodType neighborhoodType, int n, int m, int homogeneousColumns,
                                   int homogeneousRows, int numOfGrains, double radiusNucleation, double radiusNeighborhood) {
        if (boundaryCondition == null || nucleationType == null || neighborhoodType == null) {
            printError("select every option");
            return false;
        } else if (n <= 0 || m <= 0) {
            printError("wrong grid size");
            return false;
        } else if (homogeneousColumns > m || homogeneousRows > n || homogeneousColumns * homogeneousRows > n * m) {
            printError("wrong homogeneous options");
            return false;
        } else if (numOfGrains <= 0 || numOfGrains > n * m) {
            printError("wrong number of grains");
            return false;
        } else if (radiusNeighborhood <= 1 || radiusNucleation <= 1 || radiusNucleation >= n || radiusNucleation >=m
        || radiusNeighborhood >= n || radiusNucleation >=m) {
            printError("wrong radius");
            return false;
        }
        return true;
    }

    private void printError(String error) {
        errorText.setText(error);
    }

    private void makeColorsForGrains(int numberOfGrains) {
        Random random = new Random();
        Set<String> colorsSet = new LinkedHashSet<>();
        colorsSet.add("rgb(255,255,255)");
        for (int i = 1; i <= numberOfGrains; i++) {
            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);
            String value = "rgb(" + red + "," + green + "," + blue + ")";
            if (!colorsSet.contains(value))
                colorsSet.add(value);
            else
                i--;
        }
        grainColors = colorsSet.stream().toArray(String[]::new);

    }

    public void resizeVisualization(int n, int m, int numOfGrains) {
        grainGrowth.resizeGrid(n, m);
        Grid grid = grainGrowth.getGrid();
        cleanVisualizationPane();
        System.out.println(visualizationGridPane.getWidth());
        System.out.println(visualizationGridPane.getHeight());
        double width = visualizationGridPane.getWidth() / (double) m;
        double height = visualizationGridPane.getHeight() / (double) n;
        double size = (int)(width > height ? height : width);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                StackPane square = new StackPane();
                squares[i][j] = square;
                square.setMinSize(size, size);
                makeBorderOnSquare(square);
                final int ii = i;
                final int jj = j;
                square.setOnMouseClicked((e) -> {
                    if (grid.setColorIdOnClick(ii, jj))
                        grid.updateCell(ii, jj);
                });
                grid.getCell(i, j).aliveProperty().addListener((e) -> {
                    if(visualizationType.equals(VisualizationType.Microstructure) && grid.getCell(ii,jj).isCrystalized()) {
                        updateColor(square, "rgb(255,0,0)");
                    }
                    else if(visualizationType.equals(VisualizationType.Microstructure))
                        updateColor(square, grainColors[grid.getCell(ii, jj).getGrainNumber()]);
                    else if(visualizationType.equals(VisualizationType.DensityOfDislocation))
                        updateColorDensity(square, grid.getCell(ii,jj).isCrystalized());
                    else
                        updateColorEnergy(square, grid.getCell(ii,jj).isChanged());
                });
                visualizationGridPane.add(square, j, i);
            }

    }

    private void updateColorEnergy(StackPane square, boolean isChanged) {
        if(isChanged)
            square.setStyle("-fx-background-color: rgb(0,0,0)");
        else
            square.setStyle("-fx-background-color: rgb(255,255,255)");
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
    private void updateColorDensity(StackPane square, boolean crystalized) {
        if(crystalized)
            square.setStyle("-fx-background-color: rgb(255,0,0)");
        else
            square.setStyle("-fx-background-color: rgb(255,255,255)");
    }

    private void updateColor(StackPane square, String colorRGB) {
        square.setStyle("-fx-background-color: " + colorRGB);
    }

    private void makeBorderOnSquare(StackPane square) {
        square.setStyle("-fx-background-color: white;");
    }

}

