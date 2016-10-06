import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// iterator for IList interface
class IListIterator<T> implements Iterator<T> {
    // contains the list
    IList<T> items;

    // initializes the IList iterator
    IListIterator(IList<T> items) {
        this.items = items;
    }

    // checks if there is a next
    public boolean hasNext() {
        return this.items.isCons();
    }

    // returns the next item
    public T next() {
        Cons<T> itemsAsCons = this.items.asCons();
        T answer = itemsAsCons.first;
        this.items = itemsAsCons.rest;
        return answer;
    }

    // not implemented
    public void remove() {
        throw new UnsupportedOperationException("Don't do this!");
    }
}

// to represent a list
interface IList<T> extends Iterable<T> {
    // checks if the list is a cons
    boolean isCons();

    // returns the list if it is not empty
    Cons<T> asCons();

    // finds the length of the list
    int length();

    // removes an item from the list
    IList<T> remove(int index);

    // helps to remove an item from the list
    IList<T> removeHelp(int index, int counter);
}

// to represent an empty list
class Empty<T> implements IList<T> {
    // returns the iterator for the empty list
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }

    // returns false because the list is empty
    public boolean isCons() {
        return false;
    }

    // throws exception because no list to return
    public Cons<T> asCons() {
        throw new RuntimeException("No values in empty");
    }

    // returns 0 because list has no length
    public int length() {
        return 0;
    }

    // returns this list because there is nothing to remove
    public IList<T> remove(int index) {
        return new Empty<T>();
    }

    // returns this list because it cannot help remove anything
    public IList<T> removeHelp(int index, int counter) {
        return new Empty<T>();
    }
}

// to represent a non-empty list
class Cons<T> implements IList<T> {
    // first item in list
    T first;
    // rest of the list
    IList<T> rest;

    // initializes values in cons
    Cons(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }

    // returns the cons iterator
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }

    // returns true because this is a cons
    public boolean isCons() {
        return true;
    }

    // returns this because it is a cons
    public Cons<T> asCons() {
        return this;
    }

    // returns the length of the list
    public int length() {
        return 1 + this.rest.length();
    }

    // removes an item from the list
    public IList<T> remove(int index) {
        return this.removeHelp(index, 0);
    }

    // helps to remove an item from the list
    public IList<T> removeHelp(int index, int counter) {
        if (index == counter) {
            return this.rest;
        } 
        
        else {
            return new Cons<T>(this.first, 
                    this.rest.removeHelp(index, counter + 1));
        }
    }

}

// to represent a player in the game
class Player {
    // coordinates for player location
    int x;
    int y;
    // if the player has the snorkel
    boolean hasSnorkel;
    // score of the player
    int score = 0;

    // initializes the player values
    Player(int x, int y, boolean hasSnorkel) {
        this.x = x;
        this.y = y;
        this.hasSnorkel = hasSnorkel;
    }

    // draws the player
    WorldImage drawPlayer() {
        WorldImage player = new FromFileImage("pilot-icon.png");

        return player;
    }

    // EFFECT: changes the hasSnorkel field to the given value
    void giveSnorkel(boolean pickedUpSnorkel) {
        this.hasSnorkel = pickedUpSnorkel;
    }

    // returns if the player has the snorkel
    boolean hasSnorkel() {
        return this.hasSnorkel;
    }

    // EFFECT: changes the x or y field depending on the direction
    void move(String direction, Cell current) {
        if (this.hasSnorkel()) {
            if (direction.equals("left") && (current.x != 0)) {
                this.x -= 1;
                score += 1;

            } 
            
            else if (direction.equals("right") 
                    && (current.x != ForbiddenIslandWorld.ISLAND_SIZE)) {
                this.x += 1;
                score += 1;
            } 
            
            else if (direction.equals("up") && (current.y != 0)) {
                this.y -= 1;
                score += 1;

            } 
            
            else if (direction.equals("down") 
                    && (current.y != ForbiddenIslandWorld.ISLAND_SIZE)) {
                this.y += 1;
                score += 1;

            }
        } 
        
        else {

            if (direction.equals("left") && !current.left.isFlooded()) {

                this.x -= 1;
                score += 1;

            } 
            
            else if (direction.equals("right") 
                    && !current.right.isFlooded()) {
                this.x += 1;
                score += 1;

            } 
            
            else if (direction.equals("up") && !current.top.isFlooded()) {
                this.y -= 1;
                score += 1;

            } 
            
            else if (direction.equals("down") && !current.bottom.isFlooded()) {
                this.y += 1;
                score += 1;

            }
        }
    }

    // returns the score
    int score() {
        return this.score;
    }

    // returns the x value
    int getX() {
        return this.x;
    }

    // returns the y value
    int getY() {
        return this.y;
    }
}

// to represent a target to pick up
class Target {
    // coordinates of the target
    int x;
    int y;

    // initializes the target fields
    Target(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // draws the target
    WorldImage drawTarget() {
        WorldImage target = new FromFileImage("gear.png");
        return target;
    }

    // returns false because this is not the final target
    boolean finalTarget() {
        return false;
    }

    // returns the x value
    int getX() {
        return this.x;
    }

    // returns the y value
    int getY() {
        return this.y;
    }
}

// to represent a helicopter target
class HelicopterTarget extends Target {
    // initializes the helicopter target
    HelicopterTarget(int x, int y) {
        super(x, y);
    }

    // draws the helicopter target
    WorldImage drawTarget() {
        WorldImage finalTarget = new FromFileImage("helicopter.png");

        return finalTarget;
    }

    // returns true because this is the final target
    boolean finalTarget() {
        return true;
    }
}

// Represents a single square of the game area
class Cell {
    // represents absolute height of this cell, in feet
    double height;
    // In logical coordinates, with the origin at the top-left corner of the
    // screen
    int x;
    int y;
    // the four adjacent cells to this one
    Cell left;
    Cell top;
    Cell right; 
    Cell bottom;
    // reports whether this cell is flooded or not
    boolean isFlooded;
    // size of the cells
    static final int CELL_SIZE = 10;

    // initializes the cell fields
    Cell(double height, int x, int y) {
        this.height = height;
        this.x = x;
        this.y = y;
        this.isFlooded = false;
    }

    // EFFECT: sets the neighbors of the cell
    void initNeighbors(Cell left, Cell right, Cell top, Cell bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    // EFFECT: sets the neighbor on top of the cell
    void setTop(Cell top) {
        this.top = top;
    }

    // EFFECT: sets the neighbor below the cell
    void setBottom(Cell bottom) {
        this.bottom = bottom;
    }

    // EFFECT: sets the neighbor to the left of the cell
    void setLeft(Cell left) {
        this.left = left;
    }

    // EFFECT: sets the neighbor to the right of the cell
    void setRight(Cell right) {
        this.right = right;
    }

    // returns if the cell is flooded
    boolean isFlooded() {
        return this.isFlooded;
    }

    // returns if any of the neighbors are flooded
    boolean anyNeighborsFlooded() {
        return (this.left.isFlooded() 
                || this.right.isFlooded() || this.top.isFlooded() 
                || this.bottom.isFlooded);
    }

    // draws the cell
    WorldImage makeCell(int waterHeight) {
        int color = 0;
        Color cellColor = new Color(0, 0, 0);

        if (!this.isFlooded() && this.height >= waterHeight) {
            color = (int) (255 * (this.height - waterHeight) 
                    / ForbiddenIslandWorld.ISLAND_SIZE);
            cellColor = new Color(color, 255 - ((255 - color) / 2), color);
        } 
        
        else if (!this.isFlooded() && this.height < waterHeight) {
            if (Math.sqrt(waterHeight) * (waterHeight - height) > 127) {
                color = 127;
            } 
            
            else {
                color = (int) (Math.sqrt(waterHeight) * (waterHeight - height));
            }
            cellColor = new Color(color * 2, 128 - color, 0);
        } 
        
        else {
            if (128 - Math.sqrt(waterHeight) * (waterHeight - height) < 0) {
                color = 0;
            } 
            
            else {
                color = (int) (128 - Math.sqrt(waterHeight) 
                        * (waterHeight - height));
            }

            cellColor = new Color(0, 0, color);
        }

        WorldImage cell = new RectangleImage(Cell.CELL_SIZE, 
                Cell.CELL_SIZE, "solid", cellColor);

        return cell;
    }

    // EFFECT: floods the cell if the water is high enough
    void floodCell(int waterHeight) {
        if ((waterHeight > this.height)
                && (this.left.isFlooded() 
                        || this.right.isFlooded || this.top.isFlooded() 
                        || this.bottom.isFlooded())
                && !this.isFlooded()) {
            this.isFlooded = true;
        }
    }

    // returns true because the cell is not an ocean cell
    boolean notOcean() {
        return true;
    }

    // returns the x value
    int getX() {
        return this.x;
    }

    // returns the y value
    int getY() {
        return this.y;
    }
}

// to represent an ocean cell
class OceanCell extends Cell {
    // initializes the ocean cell fields
    OceanCell(double height, int x, int y) {
        super(height, x, y);
        this.isFlooded = true;
    }

    // draws the ocean cell
    WorldImage makeCell(int waterHeight) {
        WorldImage cell = new RectangleImage(Cell.CELL_SIZE, 
                Cell.CELL_SIZE, "solid", new Color(0, 0, 128));

        return cell;
    }

    // returns false because this is an ocean cell
    boolean notOcean() {
        return false;
    }
}

// to represent a snorkel
class Snorkel {
    // coordinates of the snorkel
    int x;
    int y;

    // initializes the snorkel fields
    Snorkel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // draws the snorkel
    WorldImage drawSnorkel() {
        WorldImage snorkel = new FromFileImage("snorkel.png");
        return snorkel;
    }

    // returns the x value
    int getX() {
        return this.x;
    }

    // returns the y value
    int getY() {
        return this.y;
    }
}

// represents the world
class ForbiddenIslandWorld extends World {
    // All the cells of the game, including the ocean
    IList<Cell> board = new Empty<Cell>();
    // the current height of the ocean
    int waterHeight = 0;
    // Defines an int constant
    static final int ISLAND_SIZE = 64;
    // Defines an int constant to replace ISLAND_SIZE for testing purposes
    static final int ISLAND_SIZE_TEST = 3;
    // All of the cell heights
    ArrayList<ArrayList<Double>> cellHeights = new ArrayList<ArrayList<Double>>();
    // All of the cells
    ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();
    // terrain to make
    String terrainType;
    // counter to raise water level
    int waterCounter = 0;
    // list of the pieces of targets to pick up
    IList<Target> pieces = new Empty<Target>();
    // the first player in the game
    Player player;
    // the second player in the game
    Player player2;
    // the snorkel
    Snorkel snorkel;
    // the timer left for the snorkel
    int snorkelTimer = 0;
    // the current score
    int score = 0;
    // the size to show the score
    int scoreSize = 40;
    // the number of players in the game
    int numPlayers = 1;
    // the cell where the helicopter is located
    Cell helicopterCell;
    // coordinates to randomly place items
    int xForItems;
    int yForItems;
    // list of the cells on the coast
    IList<Cell> coast = new Empty<Cell>();

    // initializes the island world fields
    ForbiddenIslandWorld() {
        this("m", 1);
    }

    // testing Constructor
    ForbiddenIslandWorld(String terrainType) {
        this.terrainType = terrainType;

        if (this.terrainType.equals("m")) {
            this.initRegMountain();
        }

        else if (this.terrainType.equals("r")) {
            this.initRandomHeightMountain();
        } 
        
        else if (this.terrainType.equals("t")) {
            this.initRandomTerrain();
        }
        // else, random terrain - TO BE implemented
        this.initCells();
        // initNeighbors();
        this.initBoard();
    }

    // initializes the island world fields
    ForbiddenIslandWorld(String terrainType, int numPlayers) {
        this.terrainType = terrainType;

        if (this.terrainType.equals("m")) {
            initRegMountain();
        } 
        
        else if (this.terrainType.equals("r")) {
            initRandomHeightMountain();
        } 
        
        else if (this.terrainType.equals("t")) {
            initRandomTerrain();
        } 
        
        else {
            throw new RuntimeException("Terrain type not valid!");
        }

        this.score = 0;
        this.setScoreSize();
        this.initCells();
        this.initNeighbors();
        this.initBoard();
        this.makePieces();
        this.setHelicopterCell();
        this.randomLandLocation();
        this.snorkel = new Snorkel(this.xForItems, this.yForItems);
        this.randomLandLocation();
        this.player = new Player(this.xForItems, this.yForItems, false);
        this.numPlayers = numPlayers;
        if (this.numPlayers == 2) {
            this.randomLandLocation();
            this.player2 = new Player(this.xForItems, this.yForItems, false);
        }
    }

    // EFFECT: initializes the cell heights for the regular mountain
    void initRegMountain() {
        this.cellHeights = new ArrayList<ArrayList<Double>>();
        double middle = (1.0 * ForbiddenIslandWorld.ISLAND_SIZE) / 2;

        for (int i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i += 1) {
            ArrayList<Double> tempList = new ArrayList<Double>();

            for (int j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j += 1) {
                double height = (1.0 * ForbiddenIslandWorld.ISLAND_SIZE)
                        - (Math.abs(middle - (1.0 * i)) * 
                                2 + Math.abs(middle - (j * 1.0)) * 2);
                if (height <= 0) {
                    height = 0.0;
                }

                tempList.add(height);
            }

            this.cellHeights.add(tempList);
        }
    }

    // EFFECT: initializes the cell heights for the random height mountain
    void initRandomHeightMountain() {
        this.cellHeights = new ArrayList<ArrayList<Double>>();
        double middle = (1.0 * ForbiddenIslandWorld.ISLAND_SIZE) / 2;

        for (int i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i += 1) {
            ArrayList<Double> tempList = new ArrayList<Double>();

            for (int j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j += 1) {
                double height = (1.0 * ForbiddenIslandWorld.ISLAND_SIZE)
                        - (Math.abs(middle - (1.0 * i)) 
                                + Math.abs(middle - (j * 1.0)) + 1.0);

                if (height >= ForbiddenIslandWorld.ISLAND_SIZE / 2) {
                    height = Math.random() * ForbiddenIslandWorld.ISLAND_SIZE;
                } 
                
                else {
                    height = 0.0;
                }

                tempList.add(height);
            }

            this.cellHeights.add(tempList);
        }
    }

    // EFFECT: initializes the cell heights for the random terrain mountain
    void initRandomTerrain() {
        this.cellHeights = new ArrayList<ArrayList<Double>>();
        int middle = ForbiddenIslandWorld.ISLAND_SIZE / 2;
        // Initializes cellHeights to contain ISLAND_SIZE+1 rows of
        // ISLAND_SIZE+1
        // columns of zeros
        for (int i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i += 1) {
            ArrayList<Double> tempList = new ArrayList<Double>();
            for (int j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j += 1) {
                tempList.add(0.0);
            }
            this.cellHeights.add(tempList);
        }
        // set values of the four corners of cellHeights to zero
        // (so corners are under water)
        this.cellHeights.get(0).set(0, 0.0);
        this.cellHeights.get(0).set(ForbiddenIslandWorld.ISLAND_SIZE, 0.0);
        this.cellHeights.get(ForbiddenIslandWorld.ISLAND_SIZE).set(0, 0.0);
        this.cellHeights.get(ForbiddenIslandWorld.ISLAND_SIZE).set(
                ForbiddenIslandWorld.ISLAND_SIZE, 0.0);
        // sets middle to max height
        // this.cellHeights.get(middle).set(middle,
        // ForbiddenIslandWorld.ISLAND_SIZE * 1.0);
        this.cellHeights.get(middle).set(middle, Math.random() 
                * ForbiddenIslandWorld.ISLAND_SIZE + 1);
        // initializes the middles of the four edges to height 1 so they're just
        // above the water
        this.cellHeights.get(middle).set(0, 1.0);
        this.cellHeights.get(middle).set(ForbiddenIslandWorld.ISLAND_SIZE, 1.0);
        this.cellHeights.get(0).set(middle, 1.0);
        this.cellHeights.get(ForbiddenIslandWorld.ISLAND_SIZE).set(middle, 1.0);
        // data points initialized
        // proceeding to Signals
        // can't use rushDivision b/c center is known for first subdivisions
        // top left
        subdivisions(cellHeights, 0, 0, middle, middle);
        // bottom right
        subdivisions(cellHeights, middle, middle, 
                ForbiddenIslandWorld.ISLAND_SIZE, 
                ForbiddenIslandWorld.ISLAND_SIZE);
        // bottom left
        subdivisions(cellHeights, 0, middle, middle, 
                ForbiddenIslandWorld.ISLAND_SIZE);
        // top right
        subdivisions(cellHeights, middle, 0, 
                ForbiddenIslandWorld.ISLAND_SIZE, middle);

        for (int i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i += 1) {
            for (int j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j += 1) {
                if (this.cellHeights.get(i).get(j) 
                        > ForbiddenIslandWorld.ISLAND_SIZE) {
                    this.cellHeights.get(i).set(j, 
                            ForbiddenIslandWorld.ISLAND_SIZE * 1.0);
                }
            }
        }
    }

    // aids in recursive random island generation
    // LLR
    // Sprawling on the fringes of the city
    // In geometric order
    // An insulated border
    // In between the bright lights
    // And the far unlit unknown
    // EFFECT: alters parent to have new values
    void subdivisions(ArrayList<ArrayList<Double>> parent, int lx, 
            int ly, int hx, int hy) {
        int length = hx - lx;

        // figuring out middle positions
        // figuring out midPointY
        int midPointY;
        if ((ly + hy) % 2 == 0) {
            midPointY = (ly + hy) / 2;

        }

        else {
            midPointY = (ly + hy) / 2 + 1;
        }
        // System.out.println("midPointY: " + midPointY);
        // figuring out midPointX
        int midPointX;
        if ((lx + hx) % 2 == 0) {
            midPointX = (lx + hx) / 2;
        }

        else {
            midPointX = (lx + hx) / 2 + 1;
        }
        // System.out.println("midPointX: " + midPointX);

        // corner heights
        double tl = parent.get(ly).get(lx);
        double bl = parent.get(hy).get(lx);
        double tr = parent.get(ly).get(hx);
        double br = parent.get(hy).get(hx);
        // side indexes
        ArrayList<Double> valueAssignment = new ArrayList<Double>();
        double l = (Math.random() * length - length / 2) + (tl + bl) / 2;
        valueAssignment.add(l);
        double r = (Math.random() * length - length / 2) + (tr + br) / 2;
        valueAssignment.add(r);
        double t = (Math.random() * length - length / 2) + (tl + tr) / 2;
        valueAssignment.add(t);
        double b = (Math.random() * length - length / 2) + (bl + br) / 2;
        valueAssignment.add(b);
        double middle = (Math.random() * length - length / 2) 
                + (tl + tr + bl + br) / 4;
        valueAssignment.add(middle);
        for (int i = 0; i < valueAssignment.size(); i += 1) {
            if (valueAssignment.get(i) <= 4) {
                valueAssignment.set(i, 0.0);
            }
        }
        // assigning found values
        // left side
        if (parent.get(midPointY).get(lx) == 0.0) {
            parent.get(midPointY).set(lx, valueAssignment.get(0));
        }
        // right side
        if (parent.get(midPointY).get(hx) == 0.0) {
            parent.get(midPointY).set(hx, valueAssignment.get(1));
        }
        // top
        if (parent.get(ly).get(midPointX) == 0.0) {
            parent.get(ly).set(midPointX, valueAssignment.get(2));
        }
        // bottom
        if (parent.get(hy).get(midPointX) == 0.0) {
            parent.get(hy).set(midPointX, valueAssignment.get(3));
        }
        // middle
        if (parent.get(midPointY).get(midPointX) == 0.0) {
            parent.get(midPointY).set(midPointX, valueAssignment.get(4));
        }
        rushDivision(parent, lx, ly, hx, hy, midPointX, midPointY);

    }

    // EFFECT: runs subdivisions individually on all four quadrants of given
    // parent to given
    // limits
    void rushDivision(ArrayList<ArrayList<Double>> parent, 
            int lx, int ly, int hx, int hy, int midPointX,
            int midPointY) {
        // check to see if there are any interior squares to evaluate
        if (midPointX - 1 != lx && midPointY - 1 != ly) {

            // top left corner
            subdivisions(parent, lx, ly, midPointX, midPointY);
            // bottom right corner
            subdivisions(parent, midPointX, midPointY, hx, hy);
            // bottom left corner
            subdivisions(parent, lx, midPointY, midPointX, hy);
            // top right corner
            subdivisions(parent, midPointX, ly, hx, midPointY);
        }
    }

    // EFFECT: initializes the arrayList of cells with the arrayList of heights
    void initCells() {
        this.cells = new ArrayList<ArrayList<Cell>>();
        for (int i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i += 1) {
            ArrayList<Cell> tempList = new ArrayList<Cell>();

            for (int j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j += 1) {
                if (this.cellHeights.get(i).get(j) == 0.0) {
                    tempList.add(new OceanCell(this.cellHeights.get(i).get(j), j, i));
                } 
                
                else {
                    tempList.add(new Cell(this.cellHeights.get(i).get(j), j, i));
                }
            }

            this.cells.add(tempList);
        }
    }

    // EFFECT: initializes the neighbors in the arrayList of cells
    void initNeighbors() {
        for (int i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i += 1) {
            for (int j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j += 1) {
                Cell temp = cells.get(i).get(j);

                temp.initNeighbors(temp, temp, temp, temp);

                if (i != 0) {
                    cells.get(i).get(j).setTop(cells.get(i - 1).get(j));
                }

                if (i != ForbiddenIslandWorld.ISLAND_SIZE) {
                    cells.get(i).get(j).setBottom(cells.get(i + 1).get(j));
                }

                if (j != 0) {
                    cells.get(i).get(j).setLeft(cells.get(i).get(j - 1));
                }

                if (j != ForbiddenIslandWorld.ISLAND_SIZE) {
                    cells.get(i).get(j).setRight(cells.get(i).get(j + 1));
                }
            }
        }
    }

    // EFFECT: initializes the list of cells for the board
    void initBoard() {
        this.board = new Empty<Cell>();
        for (int i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i += 1) {
            for (int j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j += 1) {
                this.board = new Cons<Cell>(cells.get(i).get(j), board);
            }
        }
    }

    // EFFECT: initializes x and y for items fields to random values on land
    void randomLandLocation() {
        Random random = new Random();
        boolean notLand = true;

        while (notLand) {
            this.xForItems = random.nextInt(ForbiddenIslandWorld.ISLAND_SIZE);
            this.yForItems = random.nextInt(ForbiddenIslandWorld.ISLAND_SIZE);

            for (Cell item : this.board) {
                if ((item.getX() == this.xForItems) && (item.getY() 
                        == this.yForItems) && item.notOcean()) {
                    notLand = false;
                }
            }
        }
    }

    // EFFECT: floods the cells on the coast
    void floodCells() {
        for (Cell item : this.coast) {
            item.floodCell(waterHeight);
        }
    }

    // EFFECT: initializes the list of pieces on the island
    void makePieces() {
        this.pieces = new Empty<Target>();
        for (int i = 0; i <= Math.sqrt(ForbiddenIslandWorld.ISLAND_SIZE); i += 1) {
            this.randomLandLocation();
            this.pieces = new Cons<Target>(new Target(this.xForItems, 
                    this.yForItems), this.pieces);
        }

        this.addHelicopter();
    }

    void addHelicopter() {
        int x = 0;
        int y = 0;
        double max = 0.0;

        for (Cell item : this.board) {
            if (item.height > max) {
                max = item.height;
                x = item.getX();
                y = item.getY();
            }
        }

        this.pieces = new Cons<Target>(new HelicopterTarget(x, y), this.pieces);
    }

    // returns the list of pieces with an item removed if the player is on it
    IList<Target> removePiece() {
        IList<Target> tempPieces = this.pieces;
        int counter = 0;

        for (Target item : tempPieces) {
            if ((((this.player.getX() == item.getX()) 
                    && (this.player.getY() == item.getY())) 
                    || ((this.numPlayers == 2)
                    && (this.player2.getX() == item.getX()) 
                    && (this.player2.getY() == item.getY())))
                    && !(item.finalTarget())) {

                tempPieces = tempPieces.remove(counter);

            } 
            
            else if ((this.player.getX() == item.getX()) 
                    && (this.player.getY() == item.getY())
                    && (this.numPlayers == 2) 
                    && (this.player2.getX() == item.getX())
                    && (this.player2.getY() == item.getY()) 
                    && (tempPieces.length() == 1)) {
                tempPieces = tempPieces.remove(counter);
            } 
            
            else if ((this.player.getX() == item.getX()) 
                    && (this.player.getY() == item.getY())
                    && (tempPieces.length() == 1) 
                    && (this.numPlayers == 1)) {
                tempPieces = tempPieces.remove(counter);
            }
            counter += 1;
        }

        return tempPieces;
    }

    // draws the cells of the island
    WorldScene drawCells() {
        WorldScene scene = this.getEmptyScene();

        for (Cell item : this.board) {
            scene.placeImageXY(item.makeCell(waterHeight), item.getX() 
                    * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                    item.getY() * Cell.CELL_SIZE + Cell.CELL_SIZE / 2);
        }

        return scene;
    }

    // EFFECT: sets the size of the score text
    void setScoreSize() {
        if (ForbiddenIslandWorld.ISLAND_SIZE < 30) {
            this.scoreSize = 20;
        } 
        
        else {
            this.scoreSize = 40;
        }
    }

    // returns if the snorkel is present on the island
    boolean snorkelPresent() {
        return this.snorkelTimer == 0;
    }

    // EFFECT: gives the player the snorkel if it is on the snorkel
    void givePlayerSnorkel() {
        if ((this.player.getX() == this.snorkel.getX()) 
                && (this.player.getY() == this.snorkel.getY())
                && this.snorkelPresent()) {
            this.player.giveSnorkel(true);
            this.snorkelTimer = 100;
        } 
        
        else if ((this.numPlayers == 2) 
                && (this.player2.getX() == this.snorkel.getX())
                && (this.player2.getY() == this.snorkel.getY()) 
                && this.snorkelPresent()) {
            this.player2.giveSnorkel(true);
            this.snorkelTimer = 100;
        }
    }

    // EFFECT: moves the players depending on the direction
    void movePlayers(String direction) {
        Cell current = null;
        for (Cell item : board) {
            if ((item.getX() == player.getX()) && (item.getY() == player.getY())) {
                current = item;
            }
        }

        this.player.move(direction, current);

        if (this.numPlayers == 2) {
            for (Cell item : board) {
                if ((item.getX() == player2.getX()) 
                        && (item.getY() == player2.getY())) {
                    current = item;
                }
            }

            if (direction.equals("w")) {
                this.player2.move("up", current);
            } 
            
            else if (direction.equals("a")) {
                this.player2.move("left", current);
            } 
            
            else if (direction.equals("s")) {
                this.player2.move("down", current);
            } 
            
            else if (direction.equals("d")) {
                this.player2.move("right", current);
            }

        }
    }

    // EFFECT: initializes the list of the coast
    void makeCoast() {
        this.coast = new Empty<Cell>();

        for (Cell item : this.board) {
            if (item.anyNeighborsFlooded()) {
                this.coast = new Cons<Cell>(item, this.coast);
            }
        }
    }

    // EFFECT: initializes the cell where the helicopter is
    void setHelicopterCell() {
        for (Cell item : this.board) {
            for (Target target : this.pieces) {
                if (target.finalTarget() 
                        && (target.getX() == item.getX()) 
                        && (target.getY() == item.getY())) {
                    this.helicopterCell = item;
                }
            }
        }
    }

    // checks if the helicopter cell is flooded
    boolean helicopterFlooded() {
        return this.helicopterCell.isFlooded();
    }

    // EFFECT: updates the island depending on the key press
    public void onKeyEvent(String key) {
        if (key.equals("m")) {
            this.terrainType = "m";
            this.waterHeight = 0;
            this.waterCounter = 0;
            this.score = 0;
            this.snorkelTimer = 0;
            this.initRegMountain();
            this.initCells();
            this.initNeighbors();
            this.initBoard();
            this.makePieces();
            this.setHelicopterCell();
            this.randomLandLocation();
            this.snorkel = new Snorkel(this.xForItems, this.yForItems);
            this.randomLandLocation();
            this.player = new Player(this.xForItems, this.yForItems, false);
            if (this.numPlayers == 2) {
                this.randomLandLocation();
                this.player2 = new Player(this.xForItems, this.yForItems, false);
            }
        } 
        
        else if (key.equals("r")) {
            this.terrainType = "r";
            this.waterHeight = 0;
            this.waterCounter = 0;
            this.score = 0;
            this.snorkelTimer = 0;
            this.initRandomHeightMountain();
            this.initCells();
            this.initNeighbors();
            this.initBoard();
            this.makePieces();
            this.setHelicopterCell();
            this.randomLandLocation();
            this.snorkel = new Snorkel(this.xForItems, this.yForItems);
            this.randomLandLocation();
            this.player = new Player(this.xForItems, this.yForItems, false);
            if (this.numPlayers == 2) {
                this.randomLandLocation();
                this.player2 = new Player(this.xForItems, this.yForItems, false);
            }
        } 
        
        else if (key.equals("t")) {
            this.terrainType = "t";
            this.waterHeight = 0;
            this.waterCounter = 0;
            this.score = 0;
            this.snorkelTimer = 0;
            this.initRandomTerrain();
            this.initCells();
            this.initNeighbors();
            this.initBoard();
            this.makePieces();
            this.setHelicopterCell();
            this.randomLandLocation();
            this.snorkel = new Snorkel(this.xForItems, this.yForItems);
            this.randomLandLocation();
            this.player = new Player(this.xForItems, this.yForItems, false);
            if (this.numPlayers == 2) {
                this.randomLandLocation();
                this.player2 = new Player(this.xForItems, this.yForItems, false);
            }
        } 
        
        else if (key.equals("2")) {
            this.numPlayers = 2;
            this.randomLandLocation();
            this.player2 = new Player(this.xForItems, this.yForItems, false);
        } 
        
        else if (key.equals("1")) {
            this.numPlayers = 1;
        }

        this.movePlayers(key);

    }

    // EFFECT: updates the island every tick
    public void onTick() {
        if (this.waterCounter == 10) {
            this.waterCounter = 0;
            this.waterHeight += 1;
        }

        if (!this.snorkelPresent()) {
            if (this.snorkelTimer == 1) {
                this.snorkelTimer = -1;
                this.player.giveSnorkel(false);
                if (this.numPlayers == 2) {
                    this.player2.giveSnorkel(false);
                }
            } 
            
            else {
                this.snorkelTimer -= 1;
            }
        }

        if (this.snorkelPresent()) {
            this.givePlayerSnorkel();
        }

        if (this.numPlayers == 2) {
            this.score = this.player.score() + this.player2.score();
        } 
        
        else {
            this.score = this.player.score();
        }

        this.makeCoast();
        this.pieces = this.removePiece();
        this.floodCells();

        this.waterCounter += 1;

        if (this.pieces.length() == 0) {
            this.worldEnds();
        }

    }

    // draws the world
    public WorldScene makeScene() {

        WorldScene world = this.getEmptyScene();
        world = this.drawCells();

        for (Target item : this.pieces) {
            world.placeImageXY(item.drawTarget(), item.getX() 
                    * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                    item.getY() * Cell.CELL_SIZE + Cell.CELL_SIZE / 2);
        }

        if (this.snorkelPresent()) {
            world.placeImageXY(this.snorkel.drawSnorkel(), snorkel.getX() 
                    * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                    snorkel.getY() * Cell.CELL_SIZE + Cell.CELL_SIZE / 2);
        }

        world.placeImageXY(player.drawPlayer(), this.player.getX() 
                * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                this.player.getY() * Cell.CELL_SIZE + Cell.CELL_SIZE / 2);

        if (this.numPlayers == 2) {
            world.placeImageXY(player2.drawPlayer(), this.player2.getX() 
                    * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                    this.player2.getY() * Cell.CELL_SIZE + Cell.CELL_SIZE / 2);
        }

        world.placeImageXY(new TextImage("Steps: "         
                + Integer.toString(score), this.scoreSize, new Color(255, 255, 255)),
                (int) (ForbiddenIslandWorld.ISLAND_SIZE 
                        - Math.sqrt(ForbiddenIslandWorld.ISLAND_SIZE) - 2)
                        * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                Cell.CELL_SIZE * 2);

        if (this.numPlayers == 2) {
            if (this.player.hasSnorkel() || this.player2.hasSnorkel()) {
                world.placeImageXY(
                        new TextImage("Oxygen Left: "                 
                                + Integer.toString(this.snorkelTimer), 
                                this.scoreSize,
                                new Color(255, 255, 255)),
                        (ForbiddenIslandWorld.ISLAND_SIZE / 4) 
                        * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                        Cell.CELL_SIZE * 2);
            }
        } 
        
        else {
            if (this.player.hasSnorkel()) {
                world.placeImageXY(
                        new TextImage("Oxygen Left: "                
                                + Integer.toString(this.snorkelTimer), 
                                this.scoreSize,
                                new Color(255, 255, 255)),
                        (ForbiddenIslandWorld.ISLAND_SIZE / 4) 
                        * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                        Cell.CELL_SIZE * 2);
            }
        }

        return world;
    }

    // draws the scene for the end of the game
    WorldScene gameOver(boolean won) {
        WorldScene end = this.getEmptyScene();

        if (won) {
            end.placeImageXY(new TextImage("You Won!", 48, Color.RED),
                    (ForbiddenIslandWorld.ISLAND_SIZE / 2) 
                    * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                    (ForbiddenIslandWorld.ISLAND_SIZE / 2) 
                    * Cell.CELL_SIZE + Cell.CELL_SIZE / 2);
            end.placeImageXY(new TextImage("Score: " + this.score, 48, Color.RED),
                    (ForbiddenIslandWorld.ISLAND_SIZE / 2) 
                    * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                    (ForbiddenIslandWorld.ISLAND_SIZE / 2) 
                    * Cell.CELL_SIZE + Cell.CELL_SIZE * 4);
        } 
        
        else {
            end.placeImageXY(new TextImage("Game Over!", 48, Color.RED),
                    (ForbiddenIslandWorld.ISLAND_SIZE / 2) 
                    * Cell.CELL_SIZE + Cell.CELL_SIZE / 2,
                    (ForbiddenIslandWorld.ISLAND_SIZE / 2) 
                    * Cell.CELL_SIZE + Cell.CELL_SIZE / 2);
        }

        return end;
    }

    // returns the last world state
    public WorldEnd worldEnds() {
        boolean lost = false;
        for (Cell item : this.board) {
            if ((this.numPlayers == 2) 
                    && (((this.player.getX() == item.getX()) 
                            && (this.player.getY() == item.getY())
                    && item.isFlooded() && !this.player.hasSnorkel())
                    || ((this.player2.getX() == item.getX()) 
                            && (this.player2.getY() == item.getY()) 
                            && item.isFlooded()
                            && !this.player2.hasSnorkel()))) {
                lost = true;
            } 
            
            else if ((this.player.getX() == item.getX()) 
                    && (this.player.getY() == item.getY()) && item.isFlooded()
                    && !this.player.hasSnorkel() && this.numPlayers == 1) {
                lost = true;
            } 
            
            else if (this.helicopterFlooded()) {
                lost = true;
            }
        }

        if (this.pieces.length() == 0) {
            return new WorldEnd(true, this.gameOver(true));
        } 
        
        else if (lost) {
            return new WorldEnd(true, this.gameOver(false));
        } 
        
        else {
            return new WorldEnd(false, this.makeScene());
        }
    }
}

// tests based on Island proper require an ISLAND_SIZE of 3
class ExampleForbiddenIslandWorld {
    IList<Integer> sample;
    IList<Integer> sampleEmpty;
    Iterator<Integer> sampleIterator;
    Iterator<Integer> sampleEmptyIterator;
    Cell testC1;
    Cell testC2O;
    ForbiddenIslandWorld reg;
    ForbiddenIslandWorld ran;
    ForbiddenIslandWorld reg2;
    ForbiddenIslandWorld rant;
    ArrayList<ArrayList<Double>> sampleHeights;
    ArrayList<ArrayList<Cell>> sampleCells;
    IList<Cell> sampleBoard;
    Player player1;
    Player player2;
    Target target1;
    Target target2;
    Target heliTarget;
    Snorkel snorkel1;
    Snorkel snorkel2;
    ArrayList<ArrayList<Double>> heights;

    /*
    void testGame(Tester t) {
        ForbiddenIslandWorld f = new ForbiddenIslandWorld();
        
        f.bigBang((ForbiddenIslandWorld.ISLAND_SIZE + 1)  * Cell.CELL_SIZE,
                (ForbiddenIslandWorld.ISLAND_SIZE + 1) * Cell.CELL_SIZE, .2);
        
    }
  */
    
    /*void init() {
        sample = new Cons<Integer>(new Integer(1),
                new Cons<Integer>(new Integer(2), 
                        new Cons<Integer>(new Integer(3), new Empty<Integer>())));
        sampleEmpty = new Empty<Integer>();
        sampleIterator = sample.iterator();
        sampleEmptyIterator = sampleEmpty.iterator();
        testC1 = new Cell(12, 1, 2);
        testC2O = new OceanCell(3, 1, 3);
        // using tester constructor to avoid having to set up cell neighbors
        reg = new ForbiddenIslandWorld("m");
        reg2 = new ForbiddenIslandWorld("m");
        ran = new ForbiddenIslandWorld("r");
        rant = new ForbiddenIslandWorld("t");
        sampleHeights = new ArrayList<ArrayList<Double>>();
        sampleCells = new ArrayList<ArrayList<Cell>>();
        sampleBoard = new Empty<Cell>();
        player1 = new Player(1, 1, false);
        player2 = new Player(2, 2, true);
        target1 = new Target(1, 1);
        target2 = new Target(2, 2);
        heliTarget = new HelicopterTarget(1, 2);
        snorkel1 = new Snorkel(1, 1);
        snorkel2 = new Snorkel(2, 2);
        // heights for an Island of ISLAND_SIZE == 3 and regular mountain format
        for (int i = 0; i <= 3; i += 1) {
            ArrayList<Double> tempList = new ArrayList<Double>();
            for (int j = 0; j <= 3; j += 1) {
                if (i == 0 || i == 3) {
                    tempList.add(0.0);
                }

                else if (i == 1 || i == 2) {
                    if (j == 0 || j == 3) {
                        tempList.add(0.0);
                    }

                    else {
                        tempList.add(1.0);
                    }
                }
            }
            sampleHeights.add(tempList);
        }
        // cell types for testing against Reg and Ran island creations
        // basically just used to make a mountain surrounded by water on all
        // sides
        for (int i = 0; i <= 3; i += 1) {
            ArrayList<Cell> tempList = new ArrayList<Cell>();

            for (int j = 0; j <= 3; j += 1) {
                if (i == 0 || i == 3) {
                    // to add symmetry, uses SampleHeights for height
                    tempList.add(new OceanCell(this.sampleHeights.get(i).get(j), 
                    j, i));
                }

                else if (i == 1 || i == 2) {
                    if (j == 0 || j == 3) {
                        tempList.add(new OceanCell(this.sampleHeights.get(i).get(j)
                        , j, i));
                    }

                    else {
                        tempList.add(new Cell(this.sampleHeights.get(i).get(j), 
                        j, i));
                    }
                }
            }

            this.sampleCells.add(tempList);
        }
        // sets up sample board
        for (int i = 0; i <= 3; i += 1) {
            for (int j = 0; j <= 3; j += 1) {
                sampleBoard = new Cons<Cell>(sampleCells.get(i).get(j), 
                sampleBoard);
            }
        }
        //tests added for subdivisions
        heights = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> temp = new ArrayList<Double>();
        for(int i=0; i <= 4; i+=1) {
            temp.add(0.0);
        }
        heights.add(temp);
        ArrayList<Double> temp2 = new ArrayList<Double>();
        for(int i=0; i <= 4; i+=1) {
            temp2.add(0.0);
        }
        heights.add(temp2);
        ArrayList<Double> temp3 = new ArrayList<Double>();
        for(int i=0; i <= 4; i+=1) {
            temp3.add(0.0);
        }
        heights.add(temp3);
        ArrayList<Double> temp4 = new ArrayList<Double>();
        for(int i=0; i <= 4; i+=1) {
            temp4.add(0.0);
        }
        heights.add(temp4);

    }

        //to just set heights to all non-zero values
    void initDifHeights() {
        heights = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> temp = new ArrayList<Double>();
        for(int i=0; i <= 4; i+=1) {
            temp.add(5.0);
        }
        heights.add(temp);
        ArrayList<Double> temp2 = new ArrayList<Double>();
        for(int i=0; i <= 4; i+=1) {
            temp2.add(5.0);
        }
        heights.add(temp2);
        ArrayList<Double> temp3 = new ArrayList<Double>();
        for(int i=0; i <= 4; i+=1) {
            temp3.add(5.0);
        }
        heights.add(temp3);
        ArrayList<Double> temp4 = new ArrayList<Double>();
        for(int i=0; i <= 4; i+=1) {
            temp4.add(5.0);
        }
        heights.add(temp4);
        
    }

    void testIterators(Tester t) {
        init();
        // sample = 3 2 1
        t.checkExpect(sampleEmptyIterator.hasNext(), false);
        t.checkExpect(sampleIterator.hasNext(), true);
        t.checkException(new RuntimeException("No values in empty"), 
        sampleEmptyIterator, "next");
        sampleIterator.next();
        // sample = 2 1
        t.checkExpect(sampleIterator.hasNext(), true);
        sampleIterator.next();
        // sample = 1
        sampleIterator.next();
        // sample = empty
        t.checkExpect(sampleIterator.hasNext(), false);
        t.checkException(new RuntimeException("No values in empty"), 
        sampleIterator, "next");
        t.checkException(new UnsupportedOperationException("Don't do this!"), 
                sampleIterator, "remove");
        t.checkException(new UnsupportedOperationException("Don't do this!"), 
                sampleEmptyIterator, "remove");

    }

    void testBasicIListFunctionality(Tester t) {
        init();
        t.checkExpect(sample.isCons(), true);
        t.checkExpect(sampleEmpty.isCons(), false);
        t.checkExpect(sample.asCons(), sample);
        t.checkException(new RuntimeException("No values in empty"), 
        sampleEmpty, "asCons");
        t.checkExpect(sampleEmpty.length(), 0);
        t.checkExpect(sample.length(), 3);
        t.checkExpect(sample.remove(0).length(), 2);
        t.checkExpect(sample.removeHelp(0, 0).length(), 2);
        t.checkExpect(sampleEmpty.remove(0).length(), 0);
        t.checkExpect(sampleEmpty.removeHelp(0, 0).length(), 0);
    }

    void testCells(Tester t) {
        init();
        // testing void set methods
        // using initNeighbors to test all set Methods since it uses all four
        // in series
        // initNeighbors assigns (left, right, top, bottom)
        testC1.initNeighbors(testC1, testC1, testC2O, testC1);
        t.checkExpect(testC1.bottom, testC1);
        t.checkExpect(testC1.left, testC1);
        t.checkExpect(testC1.right, testC1);
        t.checkExpect(testC1.top, testC2O);
        // ocean cell creation
        t.checkExpect(testC2O.makeCell(1), new RectangleImage(10, 10, "solid", 
                new Color(0, 0, 128)));
        t.checkExpect(testC1.isFlooded(), false);
        t.checkExpect(testC2O.isFlooded(), true);
        t.checkExpect(testC1.anyNeighborsFlooded(), true);
        t.checkExpect(testC1.notOcean(), true);
        t.checkExpect(testC2O.notOcean(), false);
        testC1.floodCell(1);
        t.checkExpect(testC1.isFlooded(), false);
        testC1.floodCell(100);
        t.checkExpect(testC1.isFlooded(), true);
        t.checkExpect(testC1.getX(), 1);  
        t.checkExpect(testC1.getY(), 2);  
        t.checkExpect(testC2O.getX(), 1);  
        t.checkExpect(testC2O.getY(), 3);  
    }

    void testWorldHeights(Tester t) {
        init();
        // checks regular mountain heights
        t.checkExpect(reg.cellHeights, sampleHeights);
        // checks mountain heights
        // AKA in a ISLAND_SIZE of 3, if the 2x2 mountainHeights are surrounded
        // by
        // heights below water level (0)
        t.checkExpect(ran.cellHeights.get(0), sampleHeights.get(0));
        t.checkExpect(ran.cellHeights.get(3), sampleHeights.get(3));
        t.checkExpect(ran.cellHeights.get(1).get(0), sampleHeights.get(1).get(0));
        t.checkExpect(ran.cellHeights.get(2).get(0), sampleHeights.get(2).get(0));
        t.checkExpect(ran.cellHeights.get(1).get(3), sampleHeights.get(1).get(3));
        t.checkExpect(ran.cellHeights.get(2).get(3), sampleHeights.get(2).get(3));
    }

    void testInitCells(Tester t) {
        init();
        // checks to see if cells have been properly initialized with the right
        // types based on height. I.E. 0 means underwater and should be
        // initialized
        // as an OceanCell
        t.checkExpect(reg.cells.get(0), sampleCells.get(0));
        t.checkExpect(reg.cells.get(3), sampleCells.get(3));
        t.checkExpect(reg.cells.get(1), sampleCells.get(1));
        t.checkExpect(reg.cells.get(2), sampleCells.get(2));
        t.checkExpect(ran.cells.get(0), sampleCells.get(0));
        t.checkExpect(ran.cells.get(3), sampleCells.get(3));
        // can only compare ocean cells
        // since land is of varying height
        t.checkExpect(ran.cells.get(1).get(0), sampleCells.get(1).get(0));
        t.checkExpect(ran.cells.get(1).get(3), sampleCells.get(1).get(3));
        t.checkExpect(ran.cells.get(2).get(0), sampleCells.get(2).get(0));
        t.checkExpect(ran.cells.get(2).get(3), sampleCells.get(2).get(3));
    }
    
    void testRandomTerrain(Tester t) {
        //Since Random terrain generation is only doing its job when the terrain
        //is truly random, there is no easy way to test each individual cell for
        //equivalence. So we will test the range of values and that could possibly
        //be in a sample island and know we have an accurately random island.
        //To prove randomness we will test a random world against a regular
        //mountain and a random mountain to prove it's different.
        t.checkFail(reg, rant);
        t.checkFail(ran, rant);
        //while this doesn't show much, it is necessary to prove we didn't just
        //use random or regular mountain code and call it random design
        rant.initRandomTerrain();
        rant.initCells();
        rant.initBoard();
        t.checkExpect(rant.board.length(), 16);
        //this proves the board is a 4X4 grid when ISLAND_SIZE is 3    
    }

    void testInitNeighbors(Tester t) {
        init();
        // initializes neighbors
        reg.initNeighbors();
        // proves relations using position in Cells ArrayList<ArrayList> by
        // making
        // sure the neighbors are what you think they are
        t.checkExpect(reg.cells.get(0).get(0).top, reg.cells.get(0).get(0));
        t.checkExpect(reg.cells.get(1).get(1).top, reg.cells.get(0).get(1));
        t.checkExpect(reg.cells.get(1).get(1).bottom, reg.cells.get(2).get(1));
        t.checkExpect(reg.cells.get(1).get(1).left, reg.cells.get(1).get(0));
        t.checkExpect(reg.cells.get(1).get(1).right, reg.cells.get(1).get(2));
        //
        ran.initNeighbors();
        t.checkExpect(ran.cells.get(0).get(0).top, ran.cells.get(0).get(0));
        t.checkExpect(ran.cells.get(1).get(1).top, ran.cells.get(0).get(1));
        t.checkExpect(ran.cells.get(1).get(1).bottom, ran.cells.get(2).get(1));
        t.checkExpect(ran.cells.get(1).get(1).left, ran.cells.get(1).get(0));
        t.checkExpect(ran.cells.get(1).get(1).right, ran.cells.get(1).get(2));
    }

    void testInitBoard(Tester t) {
        init();
        // all cells should be the same in terms of height and shape for sample
        // and reg
        t.checkExpect(reg.board, sampleBoard);
        // since can't compare cell heights with random and sample,
        // we've proven cell type and ocean height so we'll now just prove size
        // of board
        t.checkExpect(ran.board.length(), sampleBoard.length());
        
    }
    
    void testCoastAndFlood(Tester t) {
        init();
        ran.initNeighbors();
        reg.initNeighbors();
        t.checkExpect(ran.board.length(), 16);
        t.checkExpect(reg.board.length(), 16);
        t.checkExpect(ran.coast.length(), 0);
        t.checkExpect(reg.coast.length(), 0);
        ran.makeCoast();
        reg.makeCoast();
        t.checkExpect(ran.coast.length(), 16);
        t.checkExpect(reg.coast.length(), 16);
        ran.waterHeight = 100000;
        reg.waterHeight = 100000;
        ran.floodCells();
        reg.floodCells();
        t.checkExpect(ran.coast.length(), 16);
        t.checkExpect(reg.coast.length(), 16); 
    }
    
    void testPieces(Tester t) {
        init();
        ran.initNeighbors();
        reg.initNeighbors();
        t.checkExpect(ran.pieces.length(), 0);
        t.checkExpect(reg.pieces.length(), 0);
        ran.makePieces();
        reg.makePieces();
        t.checkExpect(ran.pieces.length(), 3);
        t.checkExpect(reg.pieces.length(), 3);
        ran.addHelicopter();
        reg.addHelicopter();
        t.checkExpect(ran.pieces.length(), 4);
        t.checkExpect(reg.pieces.length(), 4);
        ran.player = player1;
        reg.player = player1;
        ran.removePiece();
        reg.removePiece();
        t.checkRange(ran.pieces.length(), 3, 5);
        t.checkRange(reg.pieces.length(), 3, 5);
        ran.setHelicopterCell();
        reg.setHelicopterCell();
        t.checkExpect(ran.helicopterFlooded(), false);
        t.checkExpect(reg.helicopterFlooded(), false);    
    }
    
    void testPlayers(Tester t){
        init();
        t.checkExpect(player1.hasSnorkel(), false);
        t.checkExpect(player2.hasSnorkel(), true);
        player1.giveSnorkel(true);
        player2.giveSnorkel(false);
        t.checkExpect(player1.hasSnorkel(), true);
        t.checkExpect(player2.hasSnorkel(), false);
        t.checkExpect(player1.score(), 0);
        t.checkExpect(player2.score(), 0);
        player1.move("up", new Cell(5, 1, 1));
        t.checkExpect(player1.getX(), 1);
        t.checkExpect(player1.getY(), 0);
        player1.move("right", new Cell(5, 1, 1));
        t.checkExpect(player1.getX(), 2);
        t.checkExpect(player1.getY(), 0);
        player1.move("down", new Cell(5, 1, 1));
        t.checkExpect(player1.getX(), 2);
        t.checkExpect(player1.getY(), 1);
        player1.move("left", new Cell(5, 1, 1));
        t.checkExpect(player1.getX(), 1);
        t.checkExpect(player1.getY(), 1);
        t.checkExpect(player2.getX(), 2);
        t.checkExpect(player2.getY(), 2);
        t.checkExpect(player1.score(), 4);
        t.checkExpect(player2.score(), 0);
    }
    
    void testMovePlayers(Tester t) {
        init();
        ran.initNeighbors();
        ran.numPlayers = 2;
        ran.player = player1;
        ran.player2 = player2;
        t.checkExpect(ran.player.getX(), 1);
        ran.movePlayers("right");
        t.checkExpect(ran.player.getX(), 2);
        t.checkExpect(ran.player2.getY(), 2);
        ran.movePlayers("w");
        t.checkExpect(ran.player2.getY(), 1);     
    }
    
    void testTargets(Tester t) {
        init();
        t.checkExpect(target1.finalTarget(), false);
        t.checkExpect(target2.finalTarget(), false);
        t.checkExpect(heliTarget.finalTarget(), true);
        t.checkExpect(target1.getX(), 1);
        t.checkExpect(target2.getX(), 2);
        t.checkExpect(heliTarget.getX(), 1);
        t.checkExpect(target1.getY(), 1);
        t.checkExpect(target2.getY(), 2);
        t.checkExpect(heliTarget.getY(), 2);
    }
    
    void testSnorkel(Tester t) {
        init();
        t.checkExpect(snorkel1.getX(), 1);
        t.checkExpect(snorkel1.getY(), 1);
        t.checkExpect(snorkel2.getX(), 2);
        t.checkExpect(snorkel2.getY(), 2);
    }
    
    void testRushDivision(Tester t) {
        init();
        //since rushDivision and Subdivisions employ generative recursion, 
         // proving one to work before the
        //other is slightly impossible. However there are certain 
         // cases we can try for rushDivision that will
        //yield non-subdivided results so we will test those here
        //then when those conditions are not met, we will 
         // simply prove in this set that the list heights
        //has mutated.
        //you also have to trust our ability to figure out midpoint
        //arguements go: arrayList<arrayList<Double>>, 
         // low x, low y, high x, high y, midX, midY
        //first we will butcher reg to have the heights we need for these tests
        
        reg.cellHeights=heights;
        reg2.cellHeights=heights;
        //this gives us a smaller 
        // 2D ArrayList to worry about and since it's the only thing affected
        //by these methods...
        //as of right now reg and reg2 are basically perfect clones
        reg.rushDivision(reg.cellHeights, 3, 3, 5, 5, 4, 4);
        //should still be...
        //t.checkExpect(reg, reg2);
        // ^ commented out because checkExpect is calling Fail on the 
         // instance of WorldEnd being
        //different for reg and reg2 but everything else is the same, 
         // check it out
        reg.rushDivision(reg.cellHeights, 0, 0, 3, 3, 2, 2);
        //but now subdivisions has run meaning they shouldn't be
        t.checkFail(reg, reg2);       
    }

     void testRushAndSubs(Tester t) {
        //here we will try our best to show that Subdivisions 
         // and rushDivision work as advertised.
        //tricky due to random nudges present in subDivisions 
         // and generative recursion mentioned earlier
        init();
        reg.cellHeights=heights;
        reg2.cellHeights=heights;
        //beginning tests...
        reg.subdivisions(reg.cellHeights, 1, 1, 2, 2);
        //so...really all we can do is check the range of the 
         // new heights since each affected height
        //will be altered by a random nudge
        //all heights given this subDivisions call should be within 
         // this range and then trip
        //the baseCase in rushDivision
        for(int i=0; i< heights.size(); i+=1) {
            for(int j=0; j< heights.get(0).size(); j+=1) {
                t.checkNumRange(reg.cellHeights.get(i).get(j), 0, 1.5);
            }
        }
        init();
        reg.cellHeights=heights;
        reg2.cellHeights=heights;
        
        reg.subdivisions(reg.cellHeights, 0, 0, 3, 3);
        
        for(int i=0; i< heights.size(); i+=1) {
            for(int j=0; j< heights.get(0).size(); j+=1) {
                t.checkNumRange(reg.cellHeights.get(i).get(j), 0, 4);
            }
        }
        //now we've check for slightly bigger values if the method 
         // still holds its own
        //finally we'll set all values in heights to not 0.0 so 
         // no values are actually assigned, the final
        //test of subdivisions
        initDifHeights();
        reg.cellHeights=heights;
        reg2.cellHeights=heights;
        //this should not cause cellHeights to change
        reg.subdivisions(reg.cellHeights,0,0,2,2);
        //t.checkExpect(reg, reg2);
        // ^ worldEnd instances different causing test fail but again, 
         // everything else is the same
    }
     
     void testSetScoreSize(Tester t) {
         init();
         //This method is pretty cut and dry, hosting only an 
          // if/else combo that sets the size of score
         reg.setScoreSize();
         //done when ISLAND_SIZE is 3 so...
         t.checkExpect(reg.scoreSize, 20);
         t.checkFail(reg.scoreSize, 40);
     }
     
     void testSnorkelPresent(Tester t) {
         init();
         //same thing with setScoreSize, it's just a single if/else
         t.checkExpect(reg.snorkelPresent(), true);
         //can't really test whether the snorkel has left 
          // the island without playing the game
         t.checkFail(reg.snorkelPresent(), false);
         reg.snorkelTimer = -1;
         //this trips the else in snorkelPresent
         t.checkExpect(reg.snorkelPresent(), false);
     }
     
     void testGivePlayerSnorkel(Tester t) {
         init();
         //accessing fields of fields so we can test this method for true
         reg.player = new Player(1, 1, false);
         reg.snorkel = new Snorkel(1, 1);
         reg.snorkelTimer = 0;
         //running method
         reg.givePlayerSnorkel();
         t.checkExpect(reg.snorkelTimer, 100);
         t.checkExpect(reg.player.hasSnorkel, true);
         
         reg.player = new Player(1, 2, false);
         reg.snorkel = new Snorkel(1, 1);
         reg.givePlayerSnorkel();
         t.checkFail(reg.player.hasSnorkel, true);
         //the player two else works the same way as player one, 
         // no code differences except he/she is assigned
         //the snorkel
     }
    
    void testRandomLandLocation(Tester t) {
        init();
        ran.initNeighbors();
        ran.xForItems = 0;
        ran.yForItems = 0;
        ran.randomLandLocation();
        t.checkRange(ran.xForItems, 0, 4);
        t.checkRange(ran.yForItems, 0, 4); 
        ran.randomLandLocation();
        t.checkRange(ran.xForItems, 0, 4);
        t.checkRange(ran.yForItems, 0, 4); 
    }*/
}