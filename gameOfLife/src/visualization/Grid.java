package visualization;


import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Grid {

    private Cell[][] cells;
    private int numberOfRows;
    private int numberOfColumns;

    public Grid(int n , int m)
    {
        resize(n,m);
    }
    void initializeCells()
    {
        cells = new Cell[numberOfRows][numberOfColumns];
        for(int i = 0; i < numberOfRows; i++)
            for(int j = 0; j < numberOfColumns; j++)
                cells[i][j] = new Cell();
    }

    public void updateCell(int i, int j) {
        cells[i][j].negateAlive();
    }

    public void nextGeneration() {
        goToNextState(calculateNextState());
    }

    private void goToNextState(boolean[][] nextState) {
        for (int rowIndex = 0; rowIndex < getNumberOfRows(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getNumberOfColumns(); columnIndex++) {
                getCell(rowIndex, columnIndex).setAlive(nextState[rowIndex][columnIndex]);
            }
        }
    }

    private boolean[][] calculateNextState() {
        boolean[][] nextState = new boolean[getNumberOfRows()][getNumberOfColumns()];

        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfColumns(); j++) {
                Cell cell = getCell(i, j);
                int numberOfAliveNeighbours = countAliveNeighbours(i, j);
                boolean isAliveInNextState =
                        ((cell.isAlive() && numberOfAliveNeighbours == 2) || numberOfAliveNeighbours == 3);
                nextState[i][j] = isAliveInNextState;
            }
        }

        return nextState;
    }
    private int countAliveNeighbours(int rowIndex, int columnIndex) {
        return (int) getNeighbours(rowIndex, columnIndex)
                .stream()
                .filter(Cell::isAlive)
                .count();
    }
    private List<Cell> getNeighbours(int rowIndex, int columnIndex) {
        int north = rowIndex - 1;
        int east = columnIndex + 1;
        int south = rowIndex + 1;
        int west = columnIndex - 1;

        return Arrays.asList(
                getCell(north, west),
                getCell(north, columnIndex),
                getCell(north, east),
                getCell(rowIndex, east),
                getCell(south, east),
                getCell(south, columnIndex),
                getCell(south, west),
                getCell(rowIndex, west)
        );
    }
    public Cell getCell(int rowIndex, int columnIndex) {
        return cells[getPeriodicRow(rowIndex)][getPeriodicColumn(columnIndex)];
    }
    private int getPeriodicRow(int rowIndex) {
        return (rowIndex + getNumberOfRows()) % getNumberOfRows();
    }

    private int getPeriodicColumn(int columnIndex) {
        return (columnIndex + getNumberOfColumns()) % getNumberOfColumns();
    }
    public int getNumberOfRows() {
        return numberOfRows;
    }


    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public void makeEveryCellDead() {
        for(int i = 0; i < numberOfRows; i++)
        {
            for(int j = 0; j < numberOfColumns; j++)
            {
                getCell(i,j).setAlive(false);
            }
        }
    }

    public void makeGlider() {
        int iCenter = numberOfRows / 2;
        int jCenter = numberOfColumns / 2;
        getCell(iCenter,jCenter).setAlive(true);
        getCell(iCenter,jCenter-1).setAlive(true);
        getCell(iCenter-1,jCenter).setAlive(true);
        getCell(iCenter-1,jCenter+1).setAlive(true);
        getCell(iCenter+1,jCenter+1).setAlive(true);
    }

    public void makeOscillator() {
        int iCenter = numberOfRows / 2;
        int jCenter = numberOfColumns / 2;
        getCell(iCenter,jCenter).setAlive(true);
        getCell(iCenter+1,jCenter).setAlive(true);
        getCell(iCenter-1,jCenter).setAlive(true);

    }

    public void makeBeehive() {
        int iCenter = numberOfRows / 2;
        int jCenter = numberOfColumns / 2;
        getCell(iCenter,jCenter-1).setAlive(true);
        getCell(iCenter-1,jCenter).setAlive(true);
        getCell(iCenter+1,jCenter).setAlive(true);
        getCell(iCenter+1,jCenter+1).setAlive(true);
        getCell(iCenter,jCenter+2).setAlive(true);
        getCell(iCenter-1,jCenter+1).setAlive(true);

    }

    public void makeRandom() {
        Random random = new Random();
        for(int i = 0; i < numberOfRows; i++)
            for(int j = 0; j < numberOfColumns; j++)
                getCell(i,j).setAlive(random.nextBoolean());
        }

    public void resize(int n, int m) {
        numberOfRows = n;
        numberOfColumns = m;
        initializeCells();
    }
}

