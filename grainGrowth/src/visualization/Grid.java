package visualization;


import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Grid {

    private Cell[][] cells;
    private int numberOfRows;
    private int numberOfColumns;
    private BoundaryCondition boundaryCondition;
    private NeighborhoodType neighborhoodType;


    private NucleationType nucleationType;
    int counterGrain = 1;
    int numberOfGrains = 0;
    private boolean[][] oldState;
    private Random generator;
    private double radiusNucleation;
    private double radiusNeighborhood;
    private boolean noChanges = true;

    public Grid(int n, int m) {
        generator = new Random();
        resize(n, m);
    }

    void initializeCells() {
        cells = new Cell[numberOfRows][numberOfColumns];
        oldState = new boolean[numberOfRows][numberOfColumns];
        int id = 1;
        for (int i = 0; i < numberOfRows; i++)
            for (int j = 0; j < numberOfColumns; j++) {
                cells[i][j] = new Cell(generator.nextDouble() + i, generator.nextDouble() + j);
                cells[i][j].setId(id++);
            }
    }

    public void updateCell(int i, int j) {
        cells[i][j].negateAlive();
    }

    public boolean nextGeneration() {
        noChanges = true;
        goToNextState(calculateNextState());
        return noChanges;
    }

    private void goToNextState(boolean[][] nextState) {
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfColumns(); j++) {
                if (oldState[i][j] != nextState[i][j] && nextState[i][j] == true) {
                    noChanges = false;
                    getCell(i, j).setAlive(true);
                }
            }
        }
        oldState = nextState;
    }

    private boolean[][] calculateNextState() {
        boolean[][] nextState = new boolean[getNumberOfRows()][getNumberOfColumns()];

        IntStream.range(0, getNumberOfRows()).parallel().forEach(i -> {
            IntStream.range(0, getNumberOfColumns()).parallel().forEach(j -> {
                Cell cell = getCell(i, j);
                if (!cell.isAlive()) {
                    int nextGrainType = checkNextGrainType(i, j);
                    if (nextGrainType != -1) {
                        cell.setGrainNumber(nextGrainType);
                        nextState[i][j] = true;
                    } else
                        nextState[i][j] = false;
                } else
                    nextState[i][j] = true;
            });
        });
        return nextState;
    }

    private int checkNextGrainType(int rowIndex, int columnIndex) {
        List<Cell> collect = getNeighbours(rowIndex, columnIndex).stream()
                .filter(Cell::isAlive).collect(Collectors.toList());
        if (collect.size() > 0) {
            Map<Integer, Integer> map = new HashMap<>();
            for (Cell cell : collect) {
                int grainNumber = cell.getGrainNumber();
                if (map.containsKey(grainNumber)) {
                    map.put(grainNumber, map.get(grainNumber) + 1);
                } else {
                    map.put(cell.getGrainNumber(), 1);
                }
            }
            OptionalInt max = map.values().stream().mapToInt(v -> v).max();
            Optional<Integer> integerStream = map.entrySet().stream().filter(entry -> max.getAsInt() == entry.getValue()).map(Map.Entry::getKey).findFirst();
            return integerStream.get();
        } else return -1;

    }

    private List<Cell> getNeighbours(int rowIndex, int columnIndex) {
        int north = rowIndex - 1;
        int east = columnIndex + 1;
        int south = rowIndex + 1;
        int west = columnIndex - 1;
        switch (neighborhoodType) {
            case VonNeumann: {
                return Arrays.asList(
                        getCell(north, columnIndex),
                        getCell(south, columnIndex),
                        getCell(rowIndex, east),
                        getCell(rowIndex, west)
                );
            }
            case Moore: {
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
            case Pentagonal_Random: {
                int pentagonalRandom = generator.nextInt(4);

                switch (pentagonalRandom) {
                    case 0:
                        return pentagonalLeft(north, east, south, west, columnIndex, rowIndex);
                    case 1:
                        return pentagonalRight(north, east, south, west, columnIndex, rowIndex);
                    case 2:
                        return pentagonalUp(north, east, south, west, columnIndex, rowIndex);
                    case 3:
                        return pentagonalDown(north, east, south, west, columnIndex, rowIndex);
                }
            }
            case Hexagonal_Left: {
                return hexagonalLeft(north, east, south, west, columnIndex, rowIndex);
            }
            case Hexagonal_Right: {
                return hexagonalRight(north, east, south, west, columnIndex, rowIndex);
            }
            case Hexagonal_Random: {
                int hexagonalRandom = generator.nextInt(2);

                switch (hexagonalRandom) {
                    case 0:
                        return hexagonalLeft(north, east, south, west, columnIndex, rowIndex);
                    case 1:
                        return hexagonalRight(north, east, south, west, columnIndex, rowIndex);
                }
            }
            case RadiusWithCenterOfGravity: {
                return radiusWithCenterOfGravity(columnIndex, rowIndex);
            }
            default:
                return Arrays.asList();

        }


    }

    public void nextMonteCarlo() {
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfColumns(); j++) {
                Cell cell = getCell(i, j);
                List<Cell> neighbours = getNeighbours(i, j);
                List<Cell> neighboursFiltered = neighbours.stream().filter(c -> c.getGrainNumber() != cell.getGrainNumber()).collect(Collectors.toList());
                List<Integer> neighboursNumbers = neighboursFiltered.stream().filter(c -> c.getGrainNumber() != 0).map(c -> c.getGrainNumber()).collect(Collectors.toList());
                if (neighboursNumbers.size() > 1) {
                    long energyBefore = neighboursFiltered.size();
                    int newNumber = neighboursNumbers.get(generator.nextInt(neighboursNumbers.size()));
                    long energyAfter = neighbours.stream().filter(c -> newNumber != c.getGrainNumber()).count();
                    boolean changed = cell.isChanged();
                    cell.setEnergy(energyBefore - energyAfter);
                    if (cell.getEnergy() >= 0) {
                        cell.setChanged(true);
                        cell.setGrainNumber(newNumber);
                        cell.negateAlive();
                    }

                }else{
                    cell.setChanged(false);
                    cell.negateAlive();
                }
            }
        }
     //   for (int i = 0; i < getNumberOfRows(); i++)
     //       for (int j = 0; j < getNumberOfColumns(); j++) {
     //           getCell(i, j).negateAlive();
      //      }
    }

    private List<Cell> radiusWithCenterOfGravity(int columnIndex, int rowIndex) {
        List<Cell> neighbours = new LinkedList<>();
        Cell cell;
        for (double x = rowIndex - radiusNeighborhood; x <= rowIndex + radiusNeighborhood; x++) {
            for (double y = columnIndex - radiusNeighborhood; y <= columnIndex + radiusNeighborhood; y++) {

                cell = getCell((int) x, (int) y);
                if (cell.isAlive()) {
                    double lineLength = calculateLineLengthCG(rowIndex, columnIndex, (int) x, (int) y);
                    if (lineLength <= radiusNeighborhood) {
                        neighbours.add(cell);
                    }
                }

            }
        }
        return neighbours;
    }

    private double calculateLineLengthCG(int x1, int y1, int x2, int y2) {
        int row = (x2 + getNumberOfRows()) % getNumberOfRows();
        int column = (y2 + getNumberOfColumns()) % getNumberOfColumns();
        Cell cell2 = getCell(row, column);
        Cell cell = getCell(x1, y1);
        double length;
        double xCenter = cell2.getCenterOfGravityX();
        double yCenter = cell2.getCenterOfGravityY();
        if (column != y2) {
            yCenter = yCenter - (int) yCenter + y2;
        }
        if (row != x2) {
            xCenter = xCenter - (int) xCenter + x2;
        }
        length = Math.sqrt(Math.abs((Math.pow(cell.getCenterOfGravityX() - xCenter, 2) +
                Math.pow(cell.getCenterOfGravityY() - yCenter, 2))));
        return length;
    }

    private List<Cell> hexagonalRight(int north, int east, int south, int west, int columnIndex, int rowIndex) {
        return Arrays.asList(
                getCell(north, columnIndex),
                getCell(north, east),
                getCell(rowIndex, east),
                getCell(south, columnIndex),
                getCell(south, west),
                getCell(rowIndex, west)
        );
    }

    private List<Cell> hexagonalLeft(int north, int east, int south, int west, int columnIndex, int rowIndex) {
        return Arrays.asList(
                getCell(north, west),
                getCell(north, columnIndex),
                getCell(rowIndex, east),
                getCell(south, east),
                getCell(south, columnIndex),
                getCell(rowIndex, west)
        );


    }

    private List<Cell> pentagonalDown(int north, int east, int south, int west, int columnIndex, int rowIndex) {
        return Arrays.asList(
                getCell(rowIndex, east),
                getCell(south, east),
                getCell(south, columnIndex),
                getCell(south, west),
                getCell(rowIndex, west)
        );
    }

    private List<Cell> pentagonalUp(int north, int east, int south, int west, int columnIndex, int rowIndex) {
        return Arrays.asList(
                getCell(north, west),
                getCell(north, columnIndex),
                getCell(north, east),
                getCell(rowIndex, east),
                getCell(rowIndex, west)
        );
    }

    private List<Cell> pentagonalRight(int north, int east, int south, int west, int columnIndex, int rowIndex) {
        return Arrays.asList(
                getCell(north, west),
                getCell(north, columnIndex),
                getCell(south, columnIndex),
                getCell(south, west),
                getCell(rowIndex, west)
        );
    }

    private List<Cell> pentagonalLeft(int north, int east, int south, int west, int columnIndex, int rowIndex) {
        return Arrays.asList(
                getCell(north, columnIndex),
                getCell(north, east),
                getCell(rowIndex, east),
                getCell(south, east),
                getCell(south, columnIndex)
        );
    }

    public void resize(int n, int m) {
        numberOfRows = n;
        numberOfColumns = m;
        initializeCells();
    }

    public void setGrainsRandom(int numberOfGrains) {
        for (int i = 1; i <= numberOfGrains; i++) {
            int row = generator.nextInt(numberOfRows);
            int column = generator.nextInt(numberOfColumns);
            if (!getCell(row, column).isAlive()) {
                cells[row][column].setGrainNumber(i);
                cells[row][column].negateAlive();
            } else i--;
        }
    }

    public void setGrainsCustom(int numberOfGrains) {
        this.numberOfGrains = numberOfGrains;
        counterGrain = 1;
    }

    public void setGrainsHomogeneous(int homogeneousRows, int homogeneousColumns) {
        if (numberOfRows >= homogeneousRows && numberOfColumns >= homogeneousColumns
                && numberOfColumns * numberOfRows >= homogeneousColumns * homogeneousRows) {
            int rowStep = (int) (numberOfRows / (double) homogeneousRows);
            int columnStep = (int) (numberOfColumns / (double) homogeneousColumns);
            int counter = 1;
            for (int i = 0; i < homogeneousRows; i++) {
                for (int j = 0; j < homogeneousColumns; j++) {
                    cells[rowStep * i + rowStep / 2][columnStep * j + columnStep / 2].setGrainNumber(counter++);
                    cells[rowStep * i + rowStep / 2][columnStep * j + columnStep / 2].negateAlive();
                }
            }

        }

    }

    public void setGrainsWithRadius(int numberOfGrains, Text errorText) {
        int failedCounter = 0;
        int number = 0;
        boolean valid = true;
        for (int i = 1; i <= numberOfGrains; i++) {
            int x1 = generator.nextInt(numberOfRows);
            int y1 = generator.nextInt(numberOfColumns);
            if (!cells[x1][y1].isAlive()) {
                for (double x = x1 - radiusNucleation; x <= x1 + radiusNucleation; x++) {
                    for (double y = y1 - radiusNucleation; y <= y1 + radiusNucleation; y++) {
                        if (x >= 0 && x < numberOfRows && y >= 0 && y <
                                numberOfColumns && Math.sqrt(Math.abs(Math.pow((x - x1) * (x - x1), 2) +
                                Math.pow((y - y1) * (y - y1), 2))) <= radiusNucleation) {
                            if (getCell((int) x, (int) y).isAlive()) {
                                valid = false;
                                break;
                            }
                        }
                    }
                }
                if (!valid) {
                    i--;
                    failedCounter++;
                } else {
                    number++;
                    failedCounter = 0;
                    cells[x1][y1].setGrainNumber(i);
                    cells[x1][y1].negateAlive();
                }
            } else {
                i--;
                failedCounter++;
            }

            if (failedCounter == 10000) break;
        }
        if(numberOfGrains != number)
            errorText.setText("Wygenerowano: " + number + " zarodkow");
    }

    public boolean setColorIdOnClick(int ii, int jj) {
        if (nucleationType.equals(NucleationType.Custom)) {
            if (cells[ii][jj].getGrainNumber() != 0) {
                cells[ii][jj].setGrainNumber(0);
                counterGrain--;
                return true;
            }
            if (counterGrain <= numberOfGrains) {
                cells[ii][jj].setGrainNumber(counterGrain++);
                return true;
            }
        }
        return false;
    }

    public void setBoundaryCondition(BoundaryCondition boundaryCondition) {
        this.boundaryCondition = boundaryCondition;
    }

    public void setNeighborhoodType(NeighborhoodType neighborhoodType) {
        this.neighborhoodType = neighborhoodType;
    }

    public Cell getCell(int rowIndex, int columnIndex) {
        switch (boundaryCondition) {
            case Absorbing:
                if (rowIndex < 0 || rowIndex >= numberOfRows || columnIndex < 0 || columnIndex >= numberOfColumns)
                    return new Cell(0, 0);
                else
                    return cells[rowIndex][columnIndex];
            case Periodic:
                return cells[getPeriodicRow(rowIndex)][getPeriodicColumn(columnIndex)];
            default:
                return null;

        }

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

    public void setRadiusNucleation(double radiusNucleation) {
        this.radiusNucleation = radiusNucleation;
    }

    public void setRadiusNeighborhood(double radiusNeighborhood) {
        this.radiusNeighborhood = radiusNeighborhood;
    }

    public void setNumberOfGrains(int numberOfGrains) {
        this.numberOfGrains = numberOfGrains;
    }

    public void setNucleationType(NucleationType nucleationType) {
        this.nucleationType = nucleationType;
    }
    public Cell[][] getCells(){
        return cells;
    }
}

