package visualization;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GrainGrowth {

    private final Grid grid;
    private Timeline timeline;
    private NucleationType nucleationType;
    private int numberOfGrains;
    private int homogeneousRows;
    private int homogeneousColumns;
    private boolean playable;
    private int radius;
    private boolean finished = false;
    private Text errorText;
    private boolean monteCarloRun;
    private boolean drxBool;

    public GrainGrowth(int n, int m, Text errorText) {
        grid = new Grid(n, m);
        this.errorText = errorText;
        buildGrid();
    }
    public Cell[][] getCells(){
        return grid.getCells();
    }
    private void setTimeline() {
        EventHandler<ActionEvent> eventHandler = event-> next();
        KeyFrame keyFrame = new KeyFrame(new Duration(1000), eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
    }
    private void setTimelineMonteCarlo(double kt, int iterations){
        EventHandler<ActionEvent> eventHandler = event-> {nextMonteCarlo(kt);};
        KeyFrame keyFrame = new KeyFrame(new Duration(1000), eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(iterations);
        timeline.play();
    }
    private void setTimelineDRX(int iterations) {
        EventHandler<ActionEvent> eventHandler = event->nextDRX();
        KeyFrame keyFrame = new KeyFrame(new Duration(1000), eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(iterations);
        timeline.play();
    }

    private void nextDRX() {
        grid.nextDRX();
    }

    private void nextMonteCarlo(double kt) {
        grid.nextMonteCarlo(kt);
        monteCarloRun = true;
    }

    public void resizeGrid(int n, int m) {
        grid.resize(n,m);
    }
    private void next() {
        if(!finished)
            finished = grid.nextGeneration();
        else{
            timeline.stop();
            System.out.println("end");
        }

    }
    private void buildGrid()
    {
        grid.initializeCells();
    }
    public Grid getGrid()
    {
        return grid;
    }
    public void playGame()
    {
        if(playable)timeline.play();
    }
    public void stopGame() {
        if(drxBool){
            System.out.println("tru");
            timeline.stop();
            setTimelineLAST();
        }
       if(playable)timeline.stop();
    }

    private void setTimelineLAST() {
        EventHandler<ActionEvent> eventHandler = event->grid.transitionRule();
        KeyFrame keyFrame = new KeyFrame(new Duration(1000), eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void setInstance(int numberOfGrains, BoundaryCondition boundaryCondition,
                            NucleationType nucleationType, NeighborhoodType neighborhoodType,
                            int homogeneousRows, int homogeneousColumns, double radiusNucleation, double radiusNeighborhood) {
        stopGame();
        setTimeline();
        finished = false;
        this.numberOfGrains = numberOfGrains;
        this.nucleationType = nucleationType;
        this.homogeneousRows = homogeneousRows;
        this.homogeneousColumns = homogeneousColumns;
        grid.setNumberOfGrains(numberOfGrains);
        grid.setRadiusNucleation(radiusNucleation);
        grid.setRadiusNeighborhood(radiusNeighborhood);
        grid.setBoundaryCondition(boundaryCondition);
        grid.setNeighborhoodType(neighborhoodType);
        grid.setNucleationType(nucleationType);
    }
    public void doNucleationType() {
        switch (nucleationType)
        {
            case Custom:
                grid.setGrainsCustom(numberOfGrains);
                break;
            case Homogeneous:
                grid.setGrainsHomogeneous(homogeneousRows,homogeneousColumns);
                break;
            case Random:
                grid.setGrainsRandom(numberOfGrains);
                break;
            case WithRadius:
                grid.setGrainsWithRadius(numberOfGrains, errorText);
                break;
        }
    }
    public void setPlayable() {
        this.playable = true;
    }

    public void monteCarlo(NeighborhoodType neighborhoodType, double kt, int iterations) {
        if(finished) {
            System.out.println("started MonteCarlo");
            grid.setNeighborhoodType(neighborhoodType);
            setTimelineMonteCarlo(kt, iterations);
        }
    }

    public boolean isPlayable() {
        return playable;
    }

    public void drx(int iterations) {
        if(monteCarloRun) {
            drxBool = true;
            timeline.stop();
            grid.setNeighborhoodType(NeighborhoodType.VonNeumann);
            grid.clearDislocation();
            System.out.println("DRX started");
            grid.calculateDislocation(iterations);
            setTimelineDRX(iterations);
        }
    }
}
