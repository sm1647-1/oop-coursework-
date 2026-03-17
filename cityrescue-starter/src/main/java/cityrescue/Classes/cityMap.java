package cityrescue.Classes;

import cityrescue.exceptions.InvalidLocationException;

public class cityMap {
    private final int width;
    private final int height;
    private final boolean[][] blocked; // True = blocked

    public cityMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.blocked = new boolean[width][height];
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isBlocked(int x, int y) {
        return !isInBounds(x, y) || blocked[x][y];
    }

    public void addObstacle(int x, int y) throws InvalidLocationException {
        if (!isInBounds(x, y)) throw new InvalidLocationException("Out of bounds");
        blocked[x][y] = true;
    }

    public void removeObstacle(int x, int y) throws InvalidLocationException {
        if (!isInBounds(x, y)) throw new InvalidLocationException("Out of bounds");
        blocked[x][y] = false;
    }

    public int getWidth() {return width; }
    public int getHeight() {return height; }
}


