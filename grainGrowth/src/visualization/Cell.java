package visualization;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Cell {

    private final BooleanProperty alive = new SimpleBooleanProperty(false);
    private int grainNumber = 0;
    private double centerOfGravityX;
    private double centerOfGravityY;

    public Cell(double centerOfGravityX, double centerOfGravityY) {
        this.centerOfGravityX = centerOfGravityX;
        this.centerOfGravityY = centerOfGravityY;
    }

    public boolean isAlive() {
        return alive.get();
    }

    public BooleanProperty aliveProperty() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive.set(alive);
    }

    public void negateAlive(){
        setAlive(!isAlive());
    }
    public void setGrainNumber(int grainNumber){
        this.grainNumber = grainNumber;
    }
    public int getGrainNumber(){return grainNumber;}
    public void setCenterOfGravity(double x, double y){
        this.centerOfGravityX = x;
        this.centerOfGravityY = y;
    };
    public double getCenterOfGravityX() {
        return centerOfGravityX;
    }

    public double getCenterOfGravityY() {
        return centerOfGravityY;
    }
}
