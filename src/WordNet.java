import java.util.HashMap;
import java.util.Map;

public class WordNet {

	Map<Integer, String> nounsSet = new HashMap<Integer, String>();
	
	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) throws IllegalArgumentException{
		// read synsets and store nouns in nounsSet
		In in = new In(synsets);
		int verticesNum = 0;
		try {
			while (in.hasNextLine()) { 
				String line = in.readLine();
				nounsSet.put(Integer.parseInt(line.split(",")[0]), line.split(",")[1]);
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
		
		//TODO check DAG and throw
	}
	
	// the set of nouns (no duplicates), returned as an Iterable
	public Iterable<String> nouns() {
		return nounsSet.values();
	}

	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		return nounsSet.containsValue(word);
	}

	// distance between nounA and nounB (defined below)
	public int distance(String nounA, String nounB) throws IllegalArgumentException {
		return 0;
	}

	// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	// in a shortest ancestral path (defined below)
	public String sap(String nounA, String nounB) throws IllegalArgumentException {
		return null;
	}

	// for unit testing of this class
	public static void main(String[] args) {
		WordNet wordNet = new WordNet("synsets.txt", "hypernyms.txt");
		System.out.println(wordNet.nounsSet.size());
		System.out.println(wordNet.isNoun("Anglicism Briticism Britishism"));
	}
}
