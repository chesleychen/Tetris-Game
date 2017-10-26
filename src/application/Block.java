/** Block Class
 * @Author: Zhengyu Chen	W1182849
 */


package application;

import java.util.ArrayList;

public class Block {

	private int width;
	private int height;
	public Block next;
	private Block temp;
	private Block curr;
	private int[] edge;
	private Point[] points;
	private int color;

	// Constructor
	public Block(Point[] points, int color) {
		this.points = points;
		setHeight();
		setWidth();
		setEdge();
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public Point[] getPoints() {
		return this.points;
	}

	public Point getPoint(int number) {
		if (number >= 4) {
			System.out.println("Error: invalid input!");
			return this.points[0];
		} else if (number == 0) {
			return this.points[0];
		} else if (number == 1) {
			return this.points[1];
		} else if (number == 2) {
			return this.points[2];
		} else {
			return points[3];
		}
	}

	// calculate a block's width
	public void setWidth() {
		int right = points[0].getX();
		int left = points[0].getX();
		for (int i = 0; i < points.length; i++) {
			if (left >= points[i].getX()) {
				left = points[i].getX();
			}
			if (right <= points[i].getX()) {
				right = points[i].getX();
			}
		}
		width = right - left + 1;
	}

	public int getWidth() {
		return width;
	}

	// calculate a block's height
	public void setHeight() {
		int top = points[0].getY();
		int bottom = points[0].getY();
		for (int i = 0; i < points.length; i++) {
			if (top <= points[i].getY()) {
				top = points[i].getY();
			}
			if (bottom >= points[i].getY()) {
				bottom = points[i].getY();
			}
		}
		height = top - bottom + 1;
	}

	public int getHeight() {
		return height;
	}

	// calculate lowest point
	public void setEdge() {
		edge = new int[getWidth()];
		for (int i = 0; i < getWidth(); i++) {
			edge[i] = 4;
			for (int j = 0; j < points.length; j++) {
				if (points[j].getX() == i && edge[i] >= points[j].getY()) {
					edge[i] = points[j].getY();
				}
			}
		}
	}

	public int[] getEdge() {
		return edge;
	}

	// Rotation
	public Block rotate() {

		Point[] temp = new Point[this.points.length];
		for (int i = 0; i < points.length; i++) {
			temp[i] = new Point(points[i].getX(), points[i].getY());
		}

		Block ret = new Block(temp, color);

		int h = ret.getHeight();

		for (Point i : ret.points) {
			int tem = i.x;
			i.x = h - 1 - i.y;
			i.y = tem;
		}

		ret.setHeight();
		ret.setWidth();
		ret.setEdge();

		return ret;

	}

	// compare two blocks is the same or not
	public static boolean compareBlocks(Block a, Block b) {
		ArrayList<Point> aPoints = new ArrayList<Point>();
		ArrayList<Point> bPoints = new ArrayList<Point>();
		boolean[] c = new boolean[4];
		boolean[] d = new boolean[4];

		for (int i = 0; i < a.getPoints().length; i++) {

			aPoints.add(a.getPoints()[i]);
			bPoints.add(b.getPoints()[i]);
		}

		for (int j = 0; j < aPoints.size(); j++) {
			for (int k = 0; k < bPoints.size(); k++) {
				if (a.points[j].getX() == b.points[k].getX() && a.points[j].getY() == b.points[k].getY()) {
					c[j] = true;
					d[k] = true;
				}
			}

		}
		for (int i = 0; i < 4; i++) {
			if (c[i] == false) {
				return false;
			}
		}
		return true;
	}

	// Construct an array with all blocks, each blocks has its own rotation
	// blocks
	public static Block[] allBlocks() {

		Block[] blocks = new Block[7];

		// Square
		Point[] points_square = new Point[4];
		Point p0_square = new Point(0, 0);
		points_square[0] = p0_square;
		Point p1_square = new Point(0, 1);
		points_square[1] = p1_square;
		Point p2_square = new Point(1, 0);
		points_square[2] = p2_square;
		Point p3_square = new Point(1, 1);
		points_square[3] = p3_square;
		Block Square = new Block(points_square, 0);
		blocks[0] = Square;

		Square.next = Square;

		// BlockL
		Point[] points_L = new Point[4];
		Point p0_L = new Point(0, 0);
		points_L[0] = p0_L;
		Point p1_L = new Point(0, 1);
		points_L[1] = p1_L;
		Point p2_L = new Point(0, 2);
		points_L[2] = p2_L;
		Point p3_L = new Point(1, 0);
		points_L[3] = p3_L;
		Block L = new Block(points_L, 1);
		blocks[1] = L;

		L.curr = L;
		L.temp = L.rotate();
		while (compareBlocks(L, L.temp) == false) {
			L.curr.next = L.temp;
			L.temp = L.temp.rotate();
			L.curr = L.curr.next;
		}
		L.curr.next = L;

		// BlockML
		Point[] points_ML = new Point[4];
		Point p0_ML = new Point(0, 0);
		points_ML[0] = p0_ML;
		Point p1_ML = new Point(1, 0);
		points_ML[1] = p1_ML;
		Point p2_ML = new Point(1, 1);
		points_ML[2] = p2_ML;
		Point p3_ML = new Point(1, 2);
		points_ML[3] = p3_ML;
		Block ML = new Block(points_ML, 2);
		blocks[2] = ML;

		ML.curr = ML;
		ML.temp = ML.rotate();
		while (compareBlocks(ML, ML.temp) == false) {
			ML.curr.next = ML.temp;
			ML.temp = ML.temp.rotate();
			ML.curr = ML.curr.next;
		}
		ML.curr.next = ML;

		// BlockS
		Point[] points_S = new Point[4];
		Point p0_S = new Point(0, 0);
		points_S[0] = p0_S;
		Point p1_S = new Point(1, 0);
		points_S[1] = p1_S;
		Point p2_S = new Point(1, 1);
		points_S[2] = p2_S;
		Point p3_S = new Point(2, 1);
		points_S[3] = p3_S;
		Block S = new Block(points_S, 3);
		blocks[3] = S;

		S.curr = S;
		S.temp = S.rotate();
		while (compareBlocks(S, S.temp) == false) {
			S.curr.next = S.temp;
			S.temp = S.temp.rotate();
			S.curr = S.curr.next;
		}
		S.curr.next = S;

		// BlockMS
		Point[] points_MS = new Point[4];
		Point p0_MS = new Point(0, 1);
		points_MS[0] = p0_MS;
		Point p1_MS = new Point(1, 0);
		points_MS[1] = p1_MS;
		Point p2_MS = new Point(1, 1);
		points_MS[2] = p2_MS;
		Point p3_MS = new Point(2, 0);
		points_MS[3] = p3_MS;
		Block MS = new Block(points_MS, 4);
		blocks[4] = MS;

		MS.curr = MS;
		MS.temp = MS.rotate();
		while (compareBlocks(MS, MS.temp) == false) {
			MS.curr.next = MS.temp;
			MS.temp = MS.temp.rotate();
			MS.curr = MS.curr.next;
		}
		MS.curr.next = MS;

		// BlockT
		Point[] points_T = new Point[4];
		Point p0_T = new Point(0, 0);
		points_T[0] = p0_T;
		Point p1_T = new Point(1, 0);
		points_T[1] = p1_T;
		Point p2_T = new Point(1, 1);
		points_T[2] = p2_T;
		Point p3_T = new Point(2, 0);
		points_T[3] = p3_T;
		Block T = new Block(points_T, 5);
		blocks[5] = T;

		T.curr = T;
		T.temp = T.rotate();
		while (compareBlocks(T, T.temp) == false) {
			T.curr.next = T.temp;
			T.temp = T.temp.rotate();
			T.curr = T.curr.next;
		}
		T.curr.next = T;

		// BlockI
		Point[] points_I = new Point[4];
		Point p0_I = new Point(0, 0);
		points_I[0] = p0_I;
		Point p1_I = new Point(0, 1);
		points_I[1] = p1_I;
		Point p2_I = new Point(0, 2);
		points_I[2] = p2_I;
		Point p3_I = new Point(0, 3);
		points_I[3] = p3_I;
		Block I = new Block(points_I, 6);
		blocks[6] = I;

		I.curr = I;
		I.temp = I.rotate();
		while (compareBlocks(I, I.temp) == false) {
			I.curr.next = I.temp;
			I.temp = I.temp.rotate();
			I.curr = I.curr.next;
		}
		I.curr.next = I;

		return blocks;
	}

}
