import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SeamCarver {
	Picture picture = null;
	
	// Constructor
	public SeamCarver(Picture picture) {
		this.picture = picture;
	}

	// current picture
	public Picture picture() {
		return this.picture;
	}

	// width of current picture
	public int width() {
		return this.picture.width();
	}

	// height of current picture{
	public int height() {
		return this.picture.height();
	}
	
	// energy of pixel at column x and row y in current picture
	public double energy(int x, int y) throws IndexOutOfBoundsException{
		if (x == 0 || y == 0 || x == width() -1 || y == height() - 1) {
			return 255*255 + 255*255 + 255*255;
		} else if (x < 0 || y < 0 || x >= width() || y >= height()) {
			throw new IndexOutOfBoundsException();
		} else {
			return energyX(x, y) + energyY(x, y); 
		}
	}

	private int energyX(int x, int y) {
		Color colorLeft = this.picture.get(x - 1, y);
		Color colorRight = this.picture.get(x + 1, y);
		int red = colorLeft.getRed() - colorRight.getRed();
		int green = colorLeft.getGreen() - colorRight.getGreen();
		int blue = colorLeft.getBlue() - colorRight.getBlue();
		return red*red + green*green + blue*blue;
	}
	
	private int energyY(int x, int y) {
		Color colorUp = this.picture.get(x, y - 1);
		Color colorDown = this.picture.get(x, y + 1);
		int red = colorUp.getRed() - colorDown.getRed();
		int green = colorUp.getGreen() - colorDown.getGreen();
		int blue = colorUp.getBlue() - colorDown.getBlue();
		return red*red + green*green + blue*blue;
	}
	
	// sequence of indices for horizontal seam in current picture
	public int[] findHorizontalSeam() {
		transpose(true);
		
		int[] shortestPath = findVerticalSeam();
		
		for (int i=0; i<shortestPath.length; i++) {
			shortestPath[i] = width() - 1 - shortestPath[i];
		}
		
		transpose(false);
		return shortestPath;
	}

	// sequence of indices for vertical seam in current picture
	public int[] findVerticalSeam() {
		double[][] energyArray = new double[width()][height()];
		
		// initial the enegryArray
		for (int x=0; x < width(); x++) {
			for (int y=0; y < height(); y++) {
				energyArray[x][y] = energy(x, y);
//				System.out.printf("%9.0f", energyArray[x][y]);
			}
//			System.out.println();
		}
		
		int[] shortestPath = null;
		double shortestWeight = Double.MAX_VALUE;
		for (int x=0; x < width(); x++) {
			Stack<int[]> topologicalOrder = new Stack<>();
			// get the topological order
			topologicalOrder = getTopologicalOrder(x, 0, topologicalOrder); 
			Collections.reverse(topologicalOrder);
			
			// find the shortest path
			int[] path = new int[height()];
			path[0] = x;
			double weight = getWeight(topologicalOrder, energyArray, path);
			if (weight < shortestWeight) {
				shortestWeight = weight;
				shortestPath = path;
			}
		}
		
		return shortestPath;
	}
	
	private Stack<int[]> getTopologicalOrder(int x, int y, Stack<int[]> order) {
		for (int[] pos : adj(x, y)) {
			if (!contains(order, pos[0], pos[1])) {
				getTopologicalOrder(pos[0], pos[1], order);
			}
		}
		order.push(new int[]{x, y});
		return order;
	}
	
	private boolean contains(Stack<int[]> order, int x, int y) {
		for (int[] pos : order) {
			if (pos[0] == x && pos[1] == y) {
				return true;
			}
		}
		return false;
	}
	
	// get the adjacent position. Return is an array with 2 elements which the
	// first element is x pos and the other one is y pos.
	private List<int[]> adj(int x, int y) {
		List<int[]> posList = new ArrayList<>();
		if (y < height() - 1) {
			if (x > 0) {
				posList.add(new int[] { x - 1, y + 1 });
			}
			posList.add(new int[] {x, y+1});
			if (x < width() - 1) {
				posList.add(new int[] { x + 1, y + 1 });
			}
		}
		
		return posList;
	}
	
	/**
	 * 
	 * @param topologicalOrder
	 * @param energyArray
	 * @param path
	 *            This path will be changed during visiting the topological
	 *            order. The result is the shortest path
	 * @return
	 */
	private double getWeight(List<int[]> topologicalOrder, double[][] energyArray, int[] path) {
		Map<String, Double> distTo = new HashMap<>();
		Map<String, String> edgeTo = new HashMap<>();
		
		distTo.put(topologicalOrder.get(0)[0]+","+topologicalOrder.get(0)[1], 0.0);
		for (int[] pos : topologicalOrder) {
			for (int[] adjpos : adj(pos[0], pos[1])) {
				if (distTo.get(adjpos[0]+","+adjpos[1]) == null || 
						distTo.get(adjpos[0]+","+adjpos[1]) > distTo.get(pos[0]+","+pos[1]) + energyArray[adjpos[0]][adjpos[1]]) {
					distTo.put(adjpos[0]+","+adjpos[1], distTo.get(pos[0]+","+pos[1]) + energyArray[adjpos[0]][adjpos[1]]);
					edgeTo.put(adjpos[0]+","+adjpos[1], pos[0]+","+pos[1]);
				}
			}
		}
		
		double lightestWeight = Double.MAX_VALUE;
		String shortestPathEnd = "";
		for (String key : distTo.keySet()) {
			if (key.endsWith("," + (height() - 1)) &&
					lightestWeight > distTo.get(key)) {
				lightestWeight = distTo.get(key);
				shortestPathEnd = key;
			}
		}
		
		
		String source = shortestPathEnd;
		for (int i=height() - 1; i>0; i--) {
			path[i] = Integer.parseInt(source.split(",")[0]);
			source = edgeTo.get(source);
		}
		
		return lightestWeight;
	}
	
	/**
	 * 
	 * @param t
	 */
	private void transpose(boolean t) {
		Picture picture = new Picture(height(), width());
		if (t) {
			for (int x=0; x<width(); x++) {
				for (int y=0; y<height(); y++) {
					picture.set(height()-1-y, x, this.picture.get(x, y));
				}
			}
		} else {
			for (int x=0; x<width(); x++) {
				for (int y=0; y<height(); y++) {
					picture.set(y, width()-1-x, this.picture.get(x, y));
				}
			}
		}
		
		this.picture = picture;
	}
	
	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] a) {
		transpose(true);
		removeVerticalSeam(a);
		transpose(false);
	}

	// remove vertical seam from current picture
	public void removeVerticalSeam(int[] a) {
		if (width() <= 1) {
			throw new IllegalArgumentException();
		}
		if (a.length != height()) {
			throw new IllegalArgumentException();
		}
		for (int i=0; i<a.length - 1; i++) {
			if (Math.abs(a[i+1] - a[i]) > 1 ) {
				throw new IllegalArgumentException();
			}
		}
		
		Picture picture = new Picture(width(), height() - 1);
		for (int x=0; x<picture.width(); x++) {
			for (int y=0; y<picture.height(); y++) {
				if (a[y] != x) {
					picture.set(x, y, this.picture.get(x, y));
				}
			}
		}
		
		this.picture = picture;
		
	}
	
	public static void main(String[] args) throws IllegalArgumentException{
		if (args.length < 1 || args[0] == "") {
			throw new IllegalArgumentException();
		}
		Picture picture = new Picture(args[0]);
		SeamCarver seamCarver = new SeamCarver(picture);
		
		for (int y=0; y<seamCarver.height(); y++) {
			for (int x=0; x<seamCarver.width(); x++) {
				System.out.printf("%9.0f", seamCarver.energy(x, y));
			}
			System.out.println();
		}
		
		seamCarver.transpose(true);
		System.out.println();
		
		for (int y=0; y<seamCarver.height(); y++) {
			for (int x=0; x<seamCarver.width(); x++) {
				System.out.printf("%9.0f", seamCarver.energy(x, y));
			}
			System.out.println();
		}
		
		seamCarver.transpose(false);
		System.out.println();
		
		for (int y=0; y<seamCarver.height(); y++) {
			for (int x=0; x<seamCarver.width(); x++) {
				System.out.printf("%9.0f", seamCarver.energy(x, y));
			}
			System.out.println();
		}
		
	}
}
