import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WordNet {

	List<String> nounsSet = new ArrayList<String>();
	SAP sap;
	
	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) throws IllegalArgumentException{
		// read synsets and store nouns in nounsSet
		In in = new In(synsets);
		int verticesNum = 0;
		try {
			while (in.hasNextLine()) { 
				String line = in.readLine();
				nounsSet.add(line.split(",")[1]);
				verticesNum++;
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Wrong format of the synsets file.");
		} finally {
			in.close();
		}
		
		// Initial a directed graph with vertices size.
		Digraph digraph = new Digraph(verticesNum);
		
		// read relations
		in = new In(hypernyms);
		String line = null;
		try {
			while ( (line = in.readLine()) != null) {
				String lineString[] = line.split(",");
				for (int i=1; i<lineString.length; i++) {
					digraph.addEdge(Integer.parseInt(lineString[0]), Integer.parseInt(lineString[i]));
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Wrong format of the hypernyms file.");
		} finally {
			in.close();
		}
		
		// Initial a sap instance
		this.sap = new SAP(digraph);
		
		// throw an exception if the directed graph is not a DAG
		DirectedCycle cycle = new DirectedCycle(digraph);
		if (cycle.hasCycle()) {
			throw new IllegalArgumentException("The Digraph is not a DAG.");
		}
	}
	
	// the set of nouns (no duplicates), returned as an Iterable
	public Iterable<String> nouns() {
		return nounsSet;
	}

	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		for (int i=0; i < nounsSet.size(); i++) {
			List<String> words = Arrays.asList(nounsSet.get(i).split(" "));
			if (words.contains(word)) {
				return true;
			}
		}
		return false;
	}

	// distance between nounA and nounB (defined below)
	public int distance(String nounA, String nounB) throws IllegalArgumentException {
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new IllegalArgumentException(nounA + " " + nounB);
		}
		
		List<Integer> nounAList, nounBList;
		List<Integer> list = new ArrayList<>();
		for (int i=0; i<nounsSet.size(); i++) {
			List<String> synsets = Arrays.asList(nounsSet.get(i).split(" "));
			if (synsets.contains(nounA)) {
				list.add(i);
			}
		}
		nounAList = new ArrayList<>(list);
		list.clear();
		for (int i=0; i<nounsSet.size(); i++) {
			List<String> synsets = Arrays.asList(nounsSet.get(i).split(" "));
			if (synsets.contains(nounB)) {
				list.add(i);
			}
		}
		nounBList = new ArrayList<>(list);
		list.clear();
		
		return sap.length(nounAList, nounBList);
	}

	// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	// in a shortest ancestral path (defined below)
	public String sap(String nounA, String nounB) throws IllegalArgumentException {
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new IllegalArgumentException();
		}
		
		List<Integer> nounAList, nounBList;
		List<Integer> list = new ArrayList<>();
		for (int i=0; i<nounsSet.size(); i++) {
			List<String> synsets = Arrays.asList(nounsSet.get(i).split(" "));
			if (synsets.contains(nounA)) {
				list.add(i);
			}
		}
		nounAList = new ArrayList<>(list);
		list.clear();
		for (int i=0; i<nounsSet.size(); i++) {
			List<String> synsets = Arrays.asList(nounsSet.get(i).split(" "));
			if (synsets.contains(nounB)) {
				list.add(i);
			}
		}
		nounBList = new ArrayList<>(list);
		list.clear();
		
		return nounsSet.get(sap.ancestor(nounAList, nounBList));
	}

	// for unit testing of this class
	public static void main(String[] args) {
		WordNet wordNet = new WordNet("synsets.txt", "hypernyms.txt");
		System.out.println(wordNet.nounsSet.size());
		System.out.println(wordNet.isNoun("Anglicism"));
	}
}
