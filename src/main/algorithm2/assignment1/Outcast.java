
public class Outcast {

	WordNet wordNet;
	// constructor takes a WordNet object
	public Outcast(WordNet wordnet) {
		this.wordNet = wordnet;
	}

	// given an array of WordNet nouns, return an outcast
	public String outcast(String[] nouns) {
		int longestDist = 0;
		String outcastString = null;
		for (String nounA : nouns) {
			int dist = 0;
			for (String nounB : nouns) {
				if (!nounA.equals(nounB)) {
					dist += wordNet.distance(nounA, nounB);
				}
			}
			if (dist > longestDist) {
				longestDist = dist;
				outcastString = nounA;
			}
		}
		
		return outcastString;
	}

	// for unit testing of this class (such as the one below)
	public static void main(String[] args) {
	    WordNet wordnet = new WordNet(args[0], args[1]);
	    Outcast outcast = new Outcast(wordnet);
	    for (int t = 2; t < args.length; t++) {
	        In in = new In(args[t]);
	        String[] nouns = in.readAllStrings();
	        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	    }
	}
}
