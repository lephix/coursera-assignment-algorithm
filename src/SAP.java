import java.util.Arrays;



public class SAP {

	private Digraph G;
	
	// constructor takes a digraph (not necessarily a DAG)
	public SAP(Digraph G) {
		this.G = G;
	}

	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) throws IndexOutOfBoundsException {
		return length(Arrays.asList(v), Arrays.asList(w));
	}

	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(int v, int w) throws IndexOutOfBoundsException {
		return ancestor(Arrays.asList(v), Arrays.asList(w));
	}

	// length of shortest ancestral path between any vertex in v and any vertex
	// in w; -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w) throws IndexOutOfBoundsException {
		checkInput(v);
		checkInput(w);
		
		BreadthFirstDirectedPaths vPath = new BreadthFirstDirectedPaths(G, v);
		BreadthFirstDirectedPaths wPath = new BreadthFirstDirectedPaths(G, w);
		int shortest = -1;
		for (int i=0; i<G.V(); i++) {
			int dist= vPath.distTo(i) + wPath.distTo(i);
			
			if (shortest < 0 && dist > 0 && dist < G.E()) {
				shortest = dist;
			} else if (dist < shortest && dist > 0) {
				shortest = dist;
			}
		}
		return shortest;
	}

	// a common ancestor that participates in shortest ancestral path; -1 if no
	// such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) throws IndexOutOfBoundsException {
		checkInput(v);
		checkInput(w);

		BreadthFirstDirectedPaths vPath = new BreadthFirstDirectedPaths(G, v);
		BreadthFirstDirectedPaths wPath = new BreadthFirstDirectedPaths(G, w);
		int shortest = -1;
		int ancestor = -1;
		for (int i=0; i<G.V(); i++) {
			int dist= vPath.distTo(i) + wPath.distTo(i);
			
			if (shortest < 0 && dist > 0 && dist < G.E()) {
				shortest = dist;
				ancestor = i;
			} else if (dist < shortest && dist > 0) {
				shortest = dist;
				ancestor = i;
			}
		}
		return ancestor;
	}

	private void checkInput(Iterable<Integer> ints) throws IndexOutOfBoundsException {
		for (Integer v : ints) {
			if (v < 0 || v >= G.V()) {
				throw new IndexOutOfBoundsException();
			}
		}
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