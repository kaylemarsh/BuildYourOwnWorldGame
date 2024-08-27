package core;

public class Room {

    private int column;
    private int row;
    private int width;
    private int height;

    public Room(int column, int row, int width, int height) {
        this.column = column;
        this.row = row;
        this.width = width;
        this.height = height;
    }

    public int getColumn() {
        return this.column;
    }
    public int getRow() {
        return this.row;
    }
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }
}
