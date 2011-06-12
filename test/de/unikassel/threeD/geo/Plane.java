package de.unikassel.threeD.geo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 2dim plane with 3d coordinates
 * <pre>
 *       1- - -2 - - 3
 *       |     |     |
 *       4 - - 5 - - 6
 *       |     |     |
 *       7 - - 8 - - 9
 * </pre>
 * 
 */
public class Plane extends LineGeom {

	public Point3D[][] pointMatrix;
	private int maxHeight;
	private int maxWidth;
	private int maxDepth;
	private int cols;
	private int rows;

	public Plane(int rows, int cols, int maxWidth, int maxHeight, int maxDepth) {
		pointMatrix = new Point3D[rows][cols];
		lineList = new ArrayList<Line>();
		points = new Geom3D();
		this.cols = cols;
		this.rows = rows;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.maxDepth = maxDepth;
		init();
	}

	
	public void init() {		
		lineList.clear();
		points.points.clear();
		Random rand = new Random();
		
		// points
		for (int r=0; r<rows; r++) {
			for (int c=0; c<cols; c++) {
//				pointMatrix[r][c] = new Point3D(rand.nextDouble()*maxWidth,
//						rand.nextDouble()*maxHeight, rand.nextDouble()*maxDepth);
				pointMatrix[r][c] = new Point3D(r*maxWidth/rows,
						c*maxHeight/cols, rand.nextDouble()*maxDepth);
				points.add(pointMatrix[r][c]);
			}
		}
		
		for (int r=0; r<rows; r++) {
			for (int c=0; c<cols; c++) {
				if (r>0) {
					Line up = new Line(pointMatrix[r][c], pointMatrix[r-1][c]);
					lineList.add(up);
				}
				if (c>0) {
					Line left = new Line(pointMatrix[r][c], pointMatrix[r][c-1]);
					lineList.add(left);
				}
			}
		}
	}

}
