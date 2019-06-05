package visualization;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Cell {

    private int id;
    private final BooleanProperty alive = new SimpleBooleanProperty(false);
    private int grainNumber = 0;
    private double centerOfGravityX;
    private double centerOfGravityY;
    private double energy = 0;
    private boolean changed = false;
    private double density;
    private boolean crystalized = false;

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

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getEnergy(){
        return this.energy;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public double getDensity() {
        return density;
    }

    public void addDensity(double density) {
        this.density+=density;
    }
    public void setDensity(double density){
        this.density = density;
    }
    public boolean isCrystalized() {
        return crystalized;
    }

    public void setCrystalized(boolean crystalized) {
        this.crystalized = crystalized;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

