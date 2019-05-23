package visualization;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Cell {

    private final BooleanProperty alive = new SimpleBooleanProperty(false);

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
}
