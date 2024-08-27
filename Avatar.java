package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Avatar {
    private TETile[][] board;
    private TETile object;
    private int x;
    private int y;
    private World world;
    public Avatar(TETile[][] board, int startingX, int startingY, World world) {
        this.board = board;
        object = Tileset.AVATAR;
        x = startingX;
        y = startingY;
        this.world = world;
    }


    public void moveRight() {
        if (canMoveRight()) {
            world.getTiles()[x][y] = Tileset.HEART;
            x += 1;
            world.getTiles()[x][y] = object;
        }

    }

    public boolean canMoveRight() {
        return board[x + 1][y] != Tileset.BIGX && board[x + 1][y] != Tileset.LOCKED_DOOR;

    }
    public void moveLeft() {
        if (canMoveLeft()) {
            board[x][y] = Tileset.HEART;
            x -= 1;
            board[x][y] = object;
        }
    }

    public boolean canMoveLeft() {
        return board[x - 1][y] != Tileset.BIGX && board[x - 1][y] != Tileset.LOCKED_DOOR;
    }

    public void moveDown() {
        if (canMoveDown()) {
            board[x][y] = Tileset.HEART;
            y -= 1;
            board[x][y] = object;
        }
    }

    public boolean canMoveDown() {
        return board[x][y - 1] != Tileset.BIGX && board[x][y - 1] != Tileset.LOCKED_DOOR;

    }

    public void moveUp() {
        if (canMoveUp()) {
            board[x][y] = Tileset.HEART;
            y += 1;
            board[x][y] = object;
        }
    }

    public boolean canMoveUp() {
        return board[x][y + 1] != Tileset.BIGX && board[x][y + 1] != Tileset.LOCKED_DOOR;

    }
}
