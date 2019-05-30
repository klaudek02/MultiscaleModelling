package visualization;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.security.Key;
import java.sql.Time;

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
    public GrainGrowth(int n, int m) {
        grid = new Grid(n, m);
        buildGrid();
    }
    private void setTimeline() {
        EventHandler<ActionEvent> eventHandler = event-> next();
        KeyFrame keyFrame = new KeyFrame(new Duration(1000), eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
    }
    private void setTimelineMonteCarlo(int kt, int iterations){
        EventHandler<ActionEvent> eventHandler = event-> nextMonteCarlo(kt);
        KeyFrame keyFrame = new KeyFrame(new Duration(1000), eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(iterations);
        timeline.play();
    }

    private void nextMonteCarlo(int kt) {
        grid.nextMonteCarlo();
    }

    public void resizeGrid(int n, int m) {
        grid.resize(n,m);
    }
    private void next() {
        if(!finished)
            finished = grid.nextGeneration();

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
       if(playable)timeline.stop();
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
                grid.setGrainsWithRadius(numberOfGrains);
                break;
        }
    }
    public void setPlayable() {
        this.playable = true;
    }

    public void monteCarlo(NeighborhoodType neighborhoodType, int kt, int iterations) {
        if(finished) {
            grid.setNeighborhoodType(neighborhoodType);
            setTimelineMonteCarlo(kt, iterations);
        }
    }
}
