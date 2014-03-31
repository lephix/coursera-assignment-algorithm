import java.util.Arrays;


public class SAP {

	private Digraph G;
	
	// constructor takes a digraph (not necessarily a DAG)
	public SAP(Digraph G) {
		this.G = G;
	}

	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) throws IndexOutOfBoundsException {
		if (v < 0 || v >= G.V() || w < 0 || w >= G.V()) {
			throw new IndexOutOfBoundsException();
		}	
		
		BreadthFirstDirectedPaths breadthFirstDirectedPaths = new BreadthFirstDirectedPaths(G, Arrays.asList(0));
		int shortest = 0;
		for (int i=0; i<G.V(); i++) {
			int dist = breadthFirstDirectedPaths.distTo(i);
			System.out.println(i+":"+dist);
//			if ((shortest == 0 || dist < shortest) && dist != 0) {
//				shortest = dist;
//			}
		}
		return shortest;
	}

	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(int v, int w) throws IndexOutOfBoundsException {
		return 0;
	}

	// length of shortest ancestral path between any vertex in v and any vertex
	// in w; -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w) throws IndexOutOfBoundsException {
		return 0;
	}

	// a common ancestor that participates in shortest ancestral path; -1 if no
	// such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) throws IndexOutOfBoundsException {
		return 0;
	}

	// for unit testing of this class (such as the one below)
	public static void main(String[] args) {
	    In in = new In(args[0]);
	    Digraph G = new Digraph(in);
	    SAP sap = new SAP(G);
	    while (!StdIn.isEmpty()) {
	        int v = StdIn.readInt();
	        int w = StdIn.readInt();
	        int length   = sap.length(v, w);
	        int ancestor = sap.ancestor(v, w);
	        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
	    }
	}

}