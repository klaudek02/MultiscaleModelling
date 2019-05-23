package visualization;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class GameOfLife {

    private final Grid grid;
    private Timeline timeline;
    private States state;

    public GameOfLife(int n, int m) {
        grid = new Grid(n, m);
        buildGrid();
        setTimeline();
    }

    private void setTimeline() {
        EventHandler<ActionEvent> eventHandler = event-> next();
        KeyFrame keyFrame = new KeyFrame(new Duration(1000), eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);

    }
    public void resizeGrid(int n, int m)
    {
        grid.resize(n,m);

    }
    private void next() {
        grid.nextGeneration();
    }

    public void updateState(States newState) {
        this.state = newState;
        changeBoard();
    }

    private void changeBoard() {
        cleanBoard();
        switch(state)
        {
            case Glider:
                makeGliderOnGrid();
                break;
            case Random:
                makeRandomOnGrid();
                break;
            case Beehive:
                makeBeehiveOnGrid();
                break;
            case Oscillator:
                makeOscillatorOnGrid();
                break;
        }
    }

    private void makeGliderOnGrid() {
        grid.makeGlider();
    }

    private void makeRandomOnGrid() {
        grid.makeRandom();
    }

    private void makeBeehiveOnGrid() {
        grid.makeBeehive();
    }

    private void makeOscillatorOnGrid() {
        grid.makeOscillator();
    }

    private void cleanBoard() {
        stopGame();
        grid.makeEveryCellDead();
    }

    private void buildGrid()
    {
        grid.initializeCells();
    }
    public Grid getGrid()
    {
        return grid;
    }
    public void updateCell(int i , int j){grid.updateCell(i, j);}
    public void playGame()
    {
        timeline.play();
    }

    public void stopGame() {
        timeline.stop();
    }
}
