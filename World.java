package core;


import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class World {
    public class XYPair {
        int x;
        int y;
        String direction;

        public XYPair(int xx, int yy, String direction) {
            this.x = xx;
            this.y = yy;
            this.direction = direction;

        }
    }

    private TETile[][] ourWorld;
    private Random r;
    int width;
    int height;
    Avatar ourAvatar;
    List<Room> roomsSoFar;
    String currentTile;
    TERenderer ter;
    StringBuilder moveTracker;
    char lastChar;
    StringBuilder avatarName;

    public World() {
        width = 60;
        height = 40;
        ourWorld = new TETile[width][height];
        this.currentTile = null;
        roomsSoFar = new ArrayList<>();
        moveTracker = new StringBuilder();
        avatarName = new StringBuilder();
        for (int column = 0; column < height; column++) { //initializes the world to just be nothing
            for (int row = 0; row < width; row++) {
                ourWorld[row][column] = Tileset.NOTHING;
            }
        }
    }

    public void initializeRenderer() {
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(width * 16, height * 16);
        ter = new TERenderer();
        ter.initialize(width, height);
    }

    public void renderCurrentTile() {
        StdDraw.setPenColor(255, 255, 255);
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        currentTile = ourWorld[x][y].description();
        StdDraw.textLeft(0.2, 39.5, "Tile under Mouse: " + currentTile);
        StdDraw.textRight(59.8, 39.5, "Your Name: " + avatarName.toString());
        StdDraw.show();
    }

    public void updateMouse() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (x < 60 && y < 40 && !currentTile.equals(ourWorld[x][y].description())) {
            StdDraw.clear(Color.black);
            ter.renderFrame(ourWorld);
            currentTile = ourWorld[x][y].description();
            prepareAvatarTopRight();
            StdDraw.show();
        }
    }

    public void prepareAvatarTopRight() {
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.textLeft(0.2, 39.5, "Tile under Mouse: " + currentTile);
        StdDraw.textRight(59.8, 39.5, "Your Name: " + avatarName.toString());
    }

    public void updateBoard() {
        if (StdDraw.hasNextKeyTyped()) {
            char c = StdDraw.nextKeyTyped();
            if (c == 'a' || c == 'A') {
                ourAvatar.moveLeft();
                moveTracker.append('A');
                ter.renderFrame(ourWorld);
                prepareAvatarTopRight();
                StdDraw.show();
                updateMouse();
            }
            if (c == 'd' || c == 'D') {
                ourAvatar.moveRight();
                ter.renderFrame(ourWorld);
                moveTracker.append('D');
                prepareAvatarTopRight();
                StdDraw.show();
                updateMouse();
            }
            if (c == 'w' || c == 'W') {
                ourAvatar.moveUp();
                ter.renderFrame(ourWorld);
                moveTracker.append('W');
                prepareAvatarTopRight();
                StdDraw.show();
                updateMouse();
            }
            if (c == 's' || c == 'S') {
                ourAvatar.moveDown();
                ter.renderFrame(ourWorld);
                moveTracker.append('S');
                prepareAvatarTopRight();
                StdDraw.show();
                updateMouse();
            }
            if (lastChar == ':') {
                if (c == 'q' || c == 'Q') {
                    FileUtils.writeFile("LastSavedMain.txt", moveTracker.toString());
                    quit();
                }
            }
            lastChar = c;
        }
    }

    public void setUpDrawingStart() {
        initializeRenderer();
        renderCurrentTile();
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.setPenRadius(300);
        StdDraw.text(30.0, 30.0, "CS61B: The Game");
        StdDraw.text(30.0, 20.0, "New Game (N)");
        StdDraw.text(30.0, 18.0, "Load Game (L)");
        StdDraw.text(30.0, 16.0, "Replay Most Recent Save (R)");
        StdDraw.text(30.0, 14.0, "Quit (Q)");
        StdDraw.text(30.0, 12.0, "Choose Avatar Name (A)");
    }
    public void runGame() {
        setUpDrawingStart();
        StdDraw.show();
        StringBuilder seedBuilder = new StringBuilder();
        String currentSeedNumber = seedBuilder.toString();
        String currentAvatarName = avatarName.toString();
        char lastCharacter;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'N' || c == 'n') {
                    lastCharacter = 'N';
                    StdDraw.clear(Color.black);
                    StdDraw.setPenColor(255, 255, 255);
                    StdDraw.text(30.0, 25.0, "Enter Seed Number: " + currentSeedNumber);
                    StdDraw.text(30.0, 24.0, "Press 'S' to generate world (Max Seed Number: 9,223,372,036,854,775,807");
                    StdDraw.show();
                    break;
                }
                if (c == 'L' || c == 'l') {
                    lastCharacter = 'L';
                    if (FileUtils.fileExists("LastSavedMain.txt")) {
                        StdDraw.clear(Color.black);
                        String lastSaved = FileUtils.readFile("LastSavedMain.txt");
                        long seed2 = getSeed(lastSaved);
                        createWorld(seed2);
                        makeChangesWithFile(lastSaved);
                        ter.renderFrame(ourWorld);
                        prepareAvatarTopRight();
                        StdDraw.show();
                        break;
                    } else {
                        quit();
                    }
                }
                if (c == 'Q' || c == 'q') {
                    quit();
                }
                if (c == 'A' || c == 'a') {
                    lastCharacter = 'A';
                    StdDraw.clear(Color.black);
                    StdDraw.text(30.0, 25.0, "Choose Avatar Name: " + currentAvatarName);
                    StdDraw.text(30.0, 24.0, "Press '*' to return to Main Menu");
                    StdDraw.show();
                    break;
                }
                if (c == 'R' || c == 'r') {
                    doTheReplay();
                }
            }
        }
        if (lastCharacter == 'N') {
            ifLastIsN(seedBuilder, currentSeedNumber);
        }
        if (lastCharacter == 'A') {
            ifLastIsA(currentAvatarName);
        }
        if (lastCharacter == 'L') {
            ifLastIsL();
        }
    }

    public void doTheReplay() {
        String readFile = FileUtils.readFile("LastSavedMain.txt");
        long seed = getSeed(readFile);
        createWorld(seed);
        ter.renderFrame(ourWorld);
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.textLeft(0.2, 39.5, "Replay In Progress");
        StdDraw.show();
        List<Character> moves = new ArrayList<>();
        for (char c : readFile.toCharArray()) {
            moves.add(c);
        }
        while (!moves.isEmpty()) {
            if (moves.get(0) == 'A') {
                ourAvatar.moveLeft();
                StdDraw.clear(Color.black);
                ter.renderFrame(ourWorld);
                StdDraw.setPenColor(255, 255, 255);
                StdDraw.textLeft(0.2, 39.5, "Replay In Progress");
                StdDraw.show();
                StdDraw.pause(800);
            }
            if (moves.get(0) == 'W') {
                ourAvatar.moveUp();
                StdDraw.clear(Color.black);
                ter.renderFrame(ourWorld);
                StdDraw.setPenColor(255, 255, 255);
                StdDraw.textLeft(0.2, 39.5, "Replay In Progress");
                StdDraw.show();
                StdDraw.pause(800);
            }
            if (moves.get(0) == 'S') {
                ourAvatar.moveDown();
                StdDraw.clear(Color.black);
                ter.renderFrame(ourWorld);
                StdDraw.setPenColor(255, 255, 255);
                StdDraw.textLeft(0.2, 39.5, "Replay In Progress");
                StdDraw.show();
                StdDraw.pause(800);
            }
            if (moves.get(0) == 'D') {
                ourAvatar.moveRight();
                StdDraw.clear(Color.black);
                ter.renderFrame(ourWorld);
                StdDraw.setPenColor(255, 255, 255);
                StdDraw.textLeft(0.2, 39.5, "Replay In Progress");
                StdDraw.show();
                StdDraw.pause(800);
            }
            moves.remove(0);
        }
        ter.renderFrame(ourWorld);
        prepareAvatarTopRight();
        StdDraw.show();
        ifLastIsL();
    }
    public void ifLastIsL() {
        moveTracker.delete(0, moveTracker.length());
        String temp = FileUtils.readFile("LastSavedMain.txt");
        moveTracker.append(temp);
        char lastOne = 'p';
        while (true) {
            prepareAvatarTopRight();
            updateBoard();
            updateMouse();
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (lastOne == ':') {
                    if (c == 'q' || c == 'Q') {
                        FileUtils.writeFile("LastSavedMain.txt", moveTracker.toString());
                        quit();
                    }
                }
                lastOne = c;
            }
        }
    }

    public void ifLastIsA(String currentAvatarName) {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                if (ch == '*') {
                    runGame();
                }
                avatarName.append(ch);
                currentAvatarName = avatarName.toString();
                StdDraw.setPenColor(Color.black);
                StdDraw.filledRectangle(30.0, 25.0, 500, 0.5);
                StdDraw.setPenColor(255, 255, 255);
                StdDraw.text(30.0, 25.0, "Choose Avatar Name: " + currentAvatarName);
                StdDraw.show();
            }
        }
    }
    public void ifLastIsN(StringBuilder seedBuilder, String currentSeedNumber) {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char d = StdDraw.nextKeyTyped();
                if (Character.isDigit(d)) {
                    seedBuilder.append(d);
                    currentSeedNumber = seedBuilder.toString();
                    StdDraw.setPenColor(Color.black);
                    StdDraw.filledRectangle(30.0, 25.0, 500, 0.5);
                    StdDraw.setPenColor(255, 255, 255);
                    StdDraw.text(30.0, 25.0, "Enter Seed Number: " + currentSeedNumber);
                    StdDraw.show();
                } else if (d == 'S' || d == 's') {
                    StdDraw.clear(Color.black);
                    long seed = Long.parseLong(seedBuilder.toString());
                    createWorld(seed);
                    ter.renderFrame(ourWorld);
                    prepareAvatarTopRight();
                    StdDraw.show();
                    while (true) {
                        updateBoard();
                        updateMouse();
                    }
                }
            }
        }
    }

    public void makeChangesWithFile(String input) {
        char[] fileArray = input.toCharArray();
        List<Character> fileList = new ArrayList<>();
        for (char c : fileArray) {
            fileList.add(c);
        }
        while (!fileList.isEmpty()) {
            if (fileList.get(0) == 'A') {
                ourAvatar.moveLeft();
            }
            if (fileList.get(0) == 'D') {
                ourAvatar.moveRight();
            }
            if (fileList.get(0) == 'S') {
                ourAvatar.moveDown();
            }
            if (fileList.get(0) == 'W') {
                ourAvatar.moveUp();
            }
            fileList.remove(0);
        }
    }

    public long getSeed(String input) {
        StringBuilder seed = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                seed.append(c);
            }
        }
        String stringSeed = seed.toString();
        return Long.parseLong(stringSeed);
    }

    public void placeAvatar() {
        Room room = roomsSoFar.get(r.nextInt(0, roomsSoFar.size()));
        int x = room.getColumn() + 1;
        int y = room.getRow() + 1;
        ourAvatar = new Avatar(ourWorld, x, y, this);
        ourWorld[x][y] = Tileset.AVATAR;
    }


    public void createWorld(long seed) {
        moveTracker.append(seed);
        r = new Random(seed);
        Room room = placeRandomRoom();
        roomsSoFar.add(room);
        for (int i = 0; i < r.nextInt(550, 600); i++) {
            room = placeHallwayAndRoom(room, 10);
            roomsSoFar.add(room);
            if (roomsSoFar.size() > r.nextInt(150, 200)) {
                break;
            }
        }
        getRidofBorderTiles();
        addWalls();
        placeDoor();
        placeAvatar();
    }

    public Room createRandomRoom(XYPair xy, int w, int h) {
        if (xy.direction.equals("East")) {
            Room room = new Room(xy.x + 1, xy.y - (h / 2), w, h);
            return room;
        } else if (xy.direction.equals("South")) {
            Room room = new Room(xy.x - (w / 2), xy.y - h, w, h);
            return room;
        } else if (xy.direction.equals("North")) {
            Room room = new Room(xy.x - (w / 2), xy.y + 1, w, h);
            return room;
        } else {
            Room room = new Room(xy.x - w, xy.y - (h / 2), w, h);
            return room;
        }
    }

    public Room placeHallwayAndRoom(Room room, int recursionDepth) {
        if (recursionDepth == 0) {
            return room;
        }
        XYPair firstxy = chooseRandomWall(room);
        int firstLength = r.nextInt(3, 7);
        int secondLength = r.nextInt(3, 7);
        XYPair secondxy = getSecondXY(firstxy, firstLength);
        XYPair xyOfRoom = getXYForNextRoom(secondxy, secondLength);
        int widthOfRoom = r.nextInt(5, 10);
        int heightOfRoom = r.nextInt(4, 10);
        Room room2 = createRandomRoom(xyOfRoom, widthOfRoom, heightOfRoom);
        if (canPlaceRoomAndHallway(firstxy, secondxy, firstLength, secondLength, room2)) {
            drawHallway(firstxy, firstLength);
            drawHallway(secondxy, secondLength);
            drawRoom(room2);
            return room2;
        } else {
            if (roomsSoFar.size() > 1) {
                int random = r.nextInt(0, roomsSoFar.size());
                return placeHallwayAndRoom(roomsSoFar.get(random), recursionDepth - 1);
            }
            return placeHallwayAndRoom(room, recursionDepth - 1);
        }
    }

    public XYPair getSecondXY(XYPair firstxy, int firstLength) {
        if (firstxy.direction.equals("North")) {
            int randomNum = r.nextInt(0, 2);
            if (randomNum == 0) {
                XYPair secondxy = new XYPair(firstxy.x + 1, firstxy.y + firstLength - 1, "East");
                return secondxy;
            } else {
                XYPair secondxy = new XYPair(firstxy.x - 1, firstxy.y + firstLength - 1, "West");
                return secondxy;
            }
        }
        if (firstxy.direction.equals("South")) {
            int randomNum = r.nextInt(0, 2);
            if (randomNum == 0) {
                XYPair secondxy = new XYPair(firstxy.x + 1, firstxy.y - firstLength + 1, "East");
                return secondxy;
            } else {
                XYPair secondxy = new XYPair(firstxy.x - 1, firstxy.y - firstLength + 1, "West");
                return secondxy;
            }
        }
        if (firstxy.direction.equals("East")) {
            int randomNum = r.nextInt(0, 2);
            if (randomNum == 0) {
                XYPair secondxy = new XYPair(firstxy.x + firstLength - 1, firstxy.y + 1, "North");
                return secondxy;
            } else {
                XYPair secondxy = new XYPair(firstxy.x + firstLength - 1, firstxy.y - 1, "South");
                return secondxy;
            }
        }
        if (firstxy.direction.equals("West")) {
            int randomNum = r.nextInt(0, 2);
            if (randomNum == 0) {
                XYPair secondxy = new XYPair(firstxy.x - firstLength + 1, firstxy.y + 1, "North");
                return secondxy;
            } else {
                XYPair secondxy = new XYPair(firstxy.x - firstLength + 1, firstxy.y - 1, "South");
                return secondxy;
            }
        }
        return new XYPair(0, 0, "LOL");
    }

    public boolean canPlaceRoomAndHallway(XYPair firstxy, XYPair secondxy, int first, int second, Room room) {
        boolean canPlaceFirst = canPlaceHallway(firstxy, first);
        boolean canPlaceSecond = canPlaceHallway(secondxy, second);
        boolean canPlaceR = canPlaceRoom(room);
        return canPlaceFirst && canPlaceR && canPlaceSecond;

    }

    public boolean canPlaceHallway(XYPair xy, int length) {
        boolean canPlace = true;
        int x = xy.x;
        int y = xy.y;
        String direction = xy.direction;
        if (direction.equals("South")) {
            for (int col = x; col < x + 3 - 2; col++) {
                for (int row = y; row >= y - length; row--) {
                    if (!withinBoard(col, row) || !(ourWorld[col][row] == Tileset.NOTHING)) {
                        canPlace = false;
                    }
                }
            }
        } else if (direction.equals("North")) {
            for (int col = x - 1; col < x + 3 - 2; col++) {
                for (int row = y + 1; row < y + length; row++) {
                    if (!withinBoard(col, row) || !(ourWorld[col][row] == Tileset.NOTHING)) {
                        canPlace = false;
                    }
                }
            }
        } else if (direction.equals("West")) {
            for (int col = x - 1; col >= x - length; col--) {
                for (int row = y - 1; row <= y + 3 - 2; row++) {
                    if (!withinBoard(col, row) || !(ourWorld[col][row] == Tileset.NOTHING)) {
                        canPlace = false;
                    }
                }
            }
        } else {
            for (int col = x + 1; col <= x + length; col++) {
                for (int row = y - 1; row <= y + 3 - 2; row++) {
                    if (!withinBoard(col, row) || !(ourWorld[col][row] == Tileset.NOTHING)) {
                        canPlace = false;
                    }
                }
            }
        }
        return canPlace;
    }

    public XYPair getXYForNextRoom(XYPair wallCoordinates, int length) {
        int startCol = wallCoordinates.x;
        int startRow = wallCoordinates.y;
        if (wallCoordinates.direction.equals("East")) {
            return createEastHallway(startCol, startRow, length, 3);
        } else if (wallCoordinates.direction.equals("West")) {
            return createWestHallway(startCol, startRow, length, 3);
        } else if (wallCoordinates.direction.equals("North")) {
            return createNorthHallway(startCol, startRow, 3, length);
        } else {
            return createSouthHallway(startCol, startRow, 3, length);
        }
    }

    public XYPair createEastHallway(int x, int y, int w, int h) {
        return new XYPair(x + w, y, "East");
    }

    public void drawEastHallway(int x, int y, int w, int h) {
        for (int c = x + 1; c <= x + w; c++) {
            for (int row = y - 1; row <= y + h - 2; row++) {
                ourWorld[c][row] = Tileset.HEART;
            }
        }
    }

    public XYPair createNorthHallway(int x, int y, int w, int h) {
        return new XYPair(x, y + h, "North");
    }

    public void drawNorthHallway(int x, int y, int w, int h) {
        for (int c = x - 1; c <= x + w - 2; c++) {
            for (int row = y + 1; row <= y + h; row++) {
                ourWorld[c][row] = Tileset.HEART;
            }
        }
    }

    public XYPair createSouthHallway(int x, int y, int w, int h) {
        return new XYPair(x, y - h, "South");
    }

    public void drawSouthHallway(int x, int y, int w, int h) {
        for (int c = x - 1; c <= x + w - 2; c++) {
            for (int row = y - 1; row >= y - h; row--) {
                ourWorld[c][row] = Tileset.HEART;
            }
        }
    }

    public XYPair createWestHallway(int x, int y, int w, int h) {
        return new XYPair(x - w, y, "West");
    }

    public void drawWestHallway(int x, int y, int w, int h) {
        for (int c = x - 1; c >= x - w; c--) {
            for (int row = y - 1; row <= y + h - 2; row++) {
                ourWorld[c][row] = Tileset.HEART;
            }
        }
    }

    public void drawHallway(XYPair wallCoordinates, int length) {
        int startCol = wallCoordinates.x;
        int startRow = wallCoordinates.y;
        if (wallCoordinates.direction.equals("East")) {
            drawEastHallway(startCol, startRow, length, 3);
        } else if (wallCoordinates.direction.equals("West")) {
            drawWestHallway(startCol, startRow, length, 3);
        } else if (wallCoordinates.direction.equals("North")) {
            drawNorthHallway(startCol, startRow, 3, length);
        } else {
            drawSouthHallway(startCol, startRow, 3, length);
        }
    }

    public boolean withinBoard(int col, int row) {
        return col > 1 && row > 1 && col < width - 1 && row < height - 1;
    }


    public String whichDirection(Room room, int col, int row) {
        if (col == room.getColumn()) {
            return "West";
        } else if (col == room.getColumn() + room.getWidth() - 1) {
            return "East";
        } else if (row == room.getRow() + room.getHeight() - 1) {
            return "North";
        } else {
            return "South";
        }
    }

    public XYPair chooseRandomWall(Room room) {
        List<XYPair> xy = new ArrayList<>();
        for (int col = room.getColumn(); col < room.getColumn() + room.getWidth(); col++) {
            for (int row = room.getRow(); row < room.getRow() + room.getHeight(); row++) {
                if (notACorner(room, col, row) && onTheEdge(room, col, row)) {
                    String direction = whichDirection(room, col, row);
                    xy.add(new XYPair(col, row, direction));
                }
            }
        }
        int random = r.nextInt(0, xy.size());
        return xy.get(random);
    }


    public boolean onTheEdge(Room room, int col, int row) {
        if (col == room.getColumn()) {
            return true;
        }
        if (col == room.getColumn() + room.getWidth() - 1) {
            return true;
        }
        if (row == room.getRow() + room.getHeight() - 1) {
            return true;
        }
        if (row == room.getRow()) {
            return true;
        }

        return false;
    }

    public boolean notACorner(Room room, int c, int row) {
        boolean topLeftCorner = (c == room.getColumn()) && (row == room.getRow() + room.getHeight() - 1);
        boolean bottomLeftCorner = (c == room.getColumn()) && (row == room.getRow());
        boolean topRightCorner = (c == room.getColumn() + room.getWidth() - 1)
                && (row == room.getRow() + room.getHeight() - 1);
        boolean bottomRightCorner = (c == room.getColumn() + room.getWidth() - 1) && (row == room.getRow());
        return !topLeftCorner && !bottomLeftCorner && !topRightCorner && !bottomRightCorner;
    }

    public Room placeRandomRoom() {
        int x = r.nextInt(width - 20);
        int y = r.nextInt(height - 12);
        int w = r.nextInt(6, 10); //width of the random room
        int h = r.nextInt(6, 10); //height of the random room
        Room room = new Room(x, y, w, h);
        placeRoom(x, y, w, h);
        return room;
    }

    public boolean canPlaceRoom(Room room) {
        boolean canPlace = true;
        for (int col = room.getColumn(); col < room.getColumn() + room.getWidth(); col++) {
            for (int row = room.getRow(); row < room.getRow() + room.getHeight(); row++) {
                if (!withinBoard(col, row) || !(ourWorld[col][row] == Tileset.NOTHING)) {
                    canPlace = false;
                }
            }
        }
        return canPlace;
    }

    public void drawRoom(Room room) {
        for (int col = room.getColumn(); col < room.getColumn() + room.getWidth(); col++) {
            for (int row = room.getRow(); row < room.getRow() + room.getHeight(); row++) {
                ourWorld[col][row] = Tileset.HEART;
            }
        }
    }

    public void placeRoom(int x, int y, int w, int h) {
        for (int col = x; col < x + w; col++) {
            for (int row = y; row < y + h; row++) {
                ourWorld[col][row] = Tileset.HEART;
            }
        }
    }

    public void getRidofBorderTiles() {
        int bottomEdge = 0;
        int topEdge = height - 1;
        int leftEdge = 1;
        int rightEdge = width - 1;

        //LEFT GRID BORDER
        for (int row = 0; row < height; row++) {
            if (ourWorld[leftEdge][row] == Tileset.HEART) {
                ourWorld[leftEdge][row] = Tileset.NOTHING;
            }
        }
        //RIGHT GRID BORDER
        for (int row = 0; row < height; row++) {
            if (ourWorld[rightEdge][row] == Tileset.HEART) {
                ourWorld[rightEdge][row] = Tileset.NOTHING;
            }
        }

        //UPPER GRID BORDER
        for (int c = 0; c < width; c++) {
            if (ourWorld[c][topEdge] == Tileset.HEART) {
                ourWorld[c][topEdge] = Tileset.NOTHING;
            }
        }

        //LOWER GRID BORDER
        for (int c = 0; c < width; c++) {
            if (ourWorld[c][bottomEdge] == Tileset.HEART) {
                ourWorld[c][bottomEdge] = Tileset.NOTHING;
            }
        }

    }

    public void addWalls() {
        for (int col = 0; col < width - 1; col++) {
            for (int row = 0; row < height - 1; row++) {
                if (ourWorld[col][row] == Tileset.HEART) {
                    if (needToMakeWall(col, row)) {
                        ourWorld[col][row] = Tileset.BIGX;
                    }
                }
            }
        }
    }

    public boolean needToMakeWall(int col, int row) {
        if ((col - 1) > 0 && ourWorld[col - 1][row] == Tileset.NOTHING) {
            return true;
        } else if (ourWorld[col + 1][row] == Tileset.NOTHING) {
            return true;
        } else if (ourWorld[col][row + 1] == Tileset.NOTHING) {
            return true;
        } else if ((row - 1) > 0 && ourWorld[col][row - 1] == Tileset.NOTHING) {
            return true;
        } else if ((col - 1) > 0 && ourWorld[col - 1][row + 1] == Tileset.NOTHING) {
            return true;
        } else if (ourWorld[col + 1][row + 1] == Tileset.NOTHING) {
            return true;
        } else if ((row - 1) > 0 && ourWorld[col + 1][row - 1] == Tileset.NOTHING) {
            return true;
        } else if ((row - 1) > 0 && (col - 1) > 0 && ourWorld[col - 1][row - 1] == Tileset.NOTHING) {
            return true;
        } else if (col == 0 && ourWorld[col][row] == Tileset.HEART) {
            return true;
        } else if (row == 1 && ourWorld[col][row] == Tileset.HEART) {
            return true;
        }
        return false;
    }

    public void placeDoor() {
        for (Room room : roomsSoFar) {
            if (shouldPlaceWall(room.getColumn(), room.getRow() + 1)) {
                ourWorld[room.getColumn()][room.getRow() + 1] = Tileset.LOCKED_DOOR;
                return;
            }
        }
    }

    public boolean shouldPlaceWall(int col, int row) {
        boolean left = withinBoard(col - 1, row) && ourWorld[col - 1][row] == Tileset.NOTHING;
        boolean leftDown = withinBoard(col - 1, row - 1) && ourWorld[col - 1][row - 1] == Tileset.NOTHING;
        boolean leftUp = withinBoard(col - 1, row + 1) && ourWorld[col - 1][row + 1] == Tileset.NOTHING;
        boolean up = withinBoard(col, row + 1) && ourWorld[col][row + 1] == Tileset.BIGX;
        boolean down = withinBoard(col, row - 1) && ourWorld[col][row - 1] == Tileset.BIGX;
        boolean right = withinBoard(col + 1, row) && ourWorld[col + 1][row] == Tileset.HEART;
        return left && leftDown && leftUp && up && down && right;
    }

    public void quit() {
        System.exit(0);
    }
    public TETile[][] getTiles() { //just returns our actual world
        return ourWorld;
    }
}
