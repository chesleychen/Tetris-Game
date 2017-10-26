package application;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The author of GameBox class: Longling Wang
 * Student ID: W00001185983
 */


public class GameBox {
    public int[][] gameBox;
    private int[][] preGameBox;
    public int[] heights;
    public int[] widths;
    private boolean lock = false;
    private boolean moveDownFail = false;

    public GameBox(int height, int width) {
        gameBox = new int[width][height];
        preGameBox = new int[width][height];
        heights = new int[width];
        widths = new int[height];
        for (int i = 0; i<gameBox.length; i++) {
            for(int j = 0; j<gameBox[0].length; j++) {
                gameBox[i][j] = -1;
                preGameBox[i][j] = -1;
            }
        }
        for (int i = 0; i<heights.length; i++) {
            heights[i] = -1;
        }
        for (int i = 0; i<widths.length; i++) {
            widths[i] = 0;
        }
    }

    public void setMoveDownFail() {
        moveDownFail = true;
    }

    public int track(Block block, int x, int y) {
        // 0: place ok not bottom  1: ok and bottom  2: ok bottom and delete full row
        // -1:bad place need undo  -2:out of bound   -3: board is locked, cannot track()
        if (!lock) {
            try {
                for (int i = 0; i < 4; i++) {
                    if (gameBox[x + block.getPoint(i).getX()][y + block.getPoint(i).getY()] != -1) {
                        return -1;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                return -2;
            }
            boolean bottom = false;
            for (int i = 0; i < 4; i++) {
                if (y + block.getPoint(i).getY() == 0 || gameBox[x + block.getPoint(i).getX()][y + block.getPoint(i).getY() - 1] != -1) {
                    bottom = true;
                }
            }
            for (int i = 0; i < 4; i++) {
                gameBox[x + block.getPoint(i).getX()][y + block.getPoint(i).getY()] = block.getColor();
            }
            if(!bottom && !moveDownFail) {
                lock = true;
                return 0;
            } else {
                for (int i = 0; i < 4; i++) {
                    widths[y + block.getPoint(i).getY()]++;
                    for (int j = widths.length - 1; j >= 0; j--) {
                        if (gameBox[x + block.getPoint(i).getX()][j] != -1) {
                            heights[x + block.getPoint(i).getX()] = j;
                            break;
                        }
                    }
                }
                for (int i = 0; i < widths.length; i++) {
                    if (widths[i] == heights.length) {
                        lock = true;
                        return 2;
                    }
                }
                deepCopy(gameBox, preGameBox);
                return 1;
            }
        }
        else {
            System.out.println("User tried to call track() when board is locked");
            return -3;
        }
    }

    private void deepCopy(int[][] orignal, int[][] copy) {
        for (int i = 0; i < orignal.length; i++) {
            copy[i] = Arrays.copyOf(orignal[i], orignal[i].length);
        }
    }

    public ArrayList<Integer> rowToDelete() {
        // return the ArrayList of which rows to delete
        ArrayList<Integer> rowToDelete = new ArrayList<>();
        for (int i = 0; i < widths.length; i++) {
            if (widths[i] == heights.length) {
                rowToDelete.add(i);
            }
        }
        return rowToDelete;
    }

    public int deleteFullRow() {
        // return the number of rows already deleted
        if (lock) {
            int[] rowToSave = new int[widths.length];
            int j = 0;
            for (int i = 0; i < widths.length; i++) {
                if (widths[i] != heights.length) {
                    rowToSave[j] = i;
                    j++;
                }
            }

            for (int n = 0; n < j; n++) {
                for (int m = 0; m < gameBox.length; m++) {
                    gameBox[m][n] = gameBox[m][rowToSave[n]];
                }
                widths[n] = widths[rowToSave[n]];
            }

            for (int m = 0; m < heights.length; m++) {
                for (int n = j; n < widths.length; n++) {
                    gameBox[m][n] = -1;
                    widths[n] = 0;
                }
            }
            int numOfRow = widths.length - j;
            for (int m = 0; m < heights.length; m++) {
                for (int n = widths.length - 1; n >= -1; n--) {
                    if (n == -1 || gameBox[m][n] != -1) {
                        heights[m] = n;
                        break;
                    }
                }
            }
            deepCopy(gameBox, preGameBox);
            lock = false;
            return numOfRow;
        } else {
            System.out.println("User tried to call deleteFullRow() when board is unlocked");
            return -3;
        }
    }

    public int fastenDrop(Block block, int x, int y) {
        // return the new y coordinate of that block after fast drop
        int[] edge = block.getEdge();
        boolean moveDown = true;
        while (moveDown) {
            for (int i = 0; i < edge.length; i++) {
                if (y == 0 || gameBox[x + i][y + edge[i] - 1] != -1) {
                    moveDown = false;
                }
            }
            if (moveDown) {
                y--;
            }
        }
        return y;
    }

    public void erase() {
        // remove the falling block before give this block a new position
        if (lock) {
            deepCopy(preGameBox, gameBox);
            lock = false;
        } else {
            System.out.println("User tried to call erase() when board is unlocked");
        }
    }
}










