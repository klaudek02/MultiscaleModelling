package visualization;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Grid {

    private Cell[][] cells;
    private int numberOfRows;
    private int numberOfColumns;
    private BoundaryCondition boundaryCondition;
    private NeighborhoodType neighborhoodType;
    int counterGrain = 1;
    int numberOfGrains;
    private boolean[][] oldState;
    private Random generator;
    private double radiusNucleation;
    private double radiusNeighborhood;

    public Grid(int n, int m) {
        generator = new Random();
        resize(n, m);
    }

    void initializeCells() {
        cells = new Cell[numberOfRows][numberOfColumns];
        oldState = new boolean[numberOfRows][numberOfColumns];
        for (int i = 0; i < numberOfRows; i++)
            for (int j = 0; j < numberOfColumns; j++) {
                cells[i][j] = new Cell(generator.nextDouble()+i, generator.nextDouble()+j);
            }
    }

    public void updateCell(int i, int j) {
        cells[i][j].negateAlive();
    }

    public void nextGeneration() {
        goToNextState(calculateNextState());
    }

    private void goToNextState(boolean[][] nextState) {
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfColumns(); j++) {
                if (oldState[i][j] != nextState[i][j] && nextState[i][j] == true) {
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
                int selectPentagonal = generator.nextInt(4);
                switch (selectPentagonal) {
                    case 1:
                        return pentagonalLeft(north, east, south, west, columnIndex, rowIndex);
                    case 2:
                        return pentagonalRight(north, east, south, west, columnIndex, rowIndex);
                    case 3:
                        return pentagonalUp(north, east, south, west, columnIndex, rowIndex);
                    case 4:
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
                switch (generator.nextInt(2)) {
                    case 1:
                        return hexagonalLeft(north, east, south, west, columnIndex, rowIndex);
                    case 2:
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

    private List<Cell> radiusWithCenterOfGravity(int columnIndex, int rowIndex) {
        List<Cell> neighbours = new LinkedList<>();
        Cell cell,cell2;
        for (double x = rowIndex - radiusNeighborhood; x <= rowIndex + radiusNeighborhood; x++) {
            for (double y = columnIndex - radiusNeighborhood; y <= columnIndex + radiusNeighborhood; y++) {
                if (x >= 0 && x < numberOfRows && y >= 0 && y < numberOfColumns) {
                    cell = getCell((int)x, (int)y);
                    cell2 = getCell(rowIndex,columnIndex);
                    if (cell.isAlive()) {
                        if (Math.sqrt(Math.abs((Math.pow(cell.getCenterOfGravityX() - cell2.getCenterOfGravityX(), 2) +
                                Math.pow(cell.getCenterOfGravityY() - cell2.getCenterOfGravityY(), 2)))) <= radiusNeighborhood) {
                            neighbours.add(cell);
                        }
                    }
                }
            }
        }
        return neighbours;
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
            int rowStep = (int) Math.ceil(numberOfRows / (double) homogeneousRows);
            int columnStep = (int) Math.ceil(numberOfColumns / (double) homogeneousColumns);
            int counter = 1;
            for (int i = 0; i < numberOfRows; i += rowStep) {
                for (int j = 0; j < numberOfColumns; j += columnStep) {
                    cells[rowStep / 2 + i][columnStep / 2 + j].setGrainNumber(counter++);
                    cells[rowStep / 2 + i][columnStep / 2 + j].negateAlive();
                }
            }

        }

    }

    public void setGrainsWithRadius(int numberOfGrains) {
        int failedCounter = 0;
        boolean valid = true;
        for (int i = 1; i <= numberOfGrains; i++) {
            int x1 = generator.nextInt(numberOfRows);
            int y1 = generator.nextInt(numberOfColumns);
            if (!cells[x1][y1].isAlive()) {
                for (double x = x1 - radiusNucleation; x <= x1 + radiusNucleation; x++) {
                    for (double y = y1 - radiusNucleation; y <= y1 + radiusNucleation; y++) {
                        if (x >= 0 && x < numberOfRows && y >= 0 && y <
                                numberOfColumns && Math.sqrt(Math.abs(Math.pow((x - x1) * (x - x1),2)+
                                Math.pow((y - y1) * (y - y1),2))) <= radiusNucleation) {
                            if (getCell((int)x,(int)y).isAlive()) {
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
    }

    public boolean setColorIdOnClick(int ii, int jj) {
        if (counterGrain <= numberOfGrains) {
            cells[ii][jj].setGrainNumber(counterGrain++);
            return true;
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
                    return new Cell(0,0);
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
}

