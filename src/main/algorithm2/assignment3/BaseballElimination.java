import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseballElimination {

	private int teamsCount;
	private ArrayList<String> teams = new ArrayList<>();
	private HashMap<Integer, Integer> wins = new HashMap<>();
	private HashMap<Integer, Integer> loses = new HashMap<>();
	private HashMap<Integer, Integer> remains = new HashMap<>();
	private HashMap<String, Integer> against = new HashMap<>();
	
	private int maxWin = 0;

	public BaseballElimination(String filename) {
		In in = new In(filename);
		teamsCount = in.readInt();

		int teamNo = 0;
		while (teamNo < teamsCount) {
			String teamName = in.readString();
			if (teamName == null || teamName.equals("")) {
				break;
			}
			teams.add(teamName);
			
			int win = in.readInt();
			maxWin = win > maxWin? win : maxWin;
			
			wins.put(teamNo, win);
			loses.put(teamNo, in.readInt());
			remains.put(teamNo, in.readInt());
			for (int i = 0; i < teamsCount; i++) {
				against.put(teamNo+"-"+i, in.readInt());
			}
			teamNo++;
		}
	}

	public int numberOfTeams() {
		return teamsCount;
	}

	public Iterable<String> teams() {
		return teams;
	}

	public int wins(String team) throws IllegalArgumentException {
		return wins.get(teams.indexOf(team));
	}

	public int losses(String team) throws IllegalArgumentException {
		return loses.get(teams.indexOf(team));
	}

	public int remaining(String team) throws IllegalArgumentException {
		return remains.get(teams.indexOf(team));
	}

	public int against(String team1, String team2)
			throws IllegalArgumentException {
		return against.get(teams.indexOf(team1)+"-"+teams.indexOf(team2));
	}

	public boolean isEliminated(String team) throws IllegalArgumentException {
		if (wins(team) + remaining(team) < maxWin) {
			return false;
		}
		
		int againstVerticsCount = ((teamsCount - 1) * (teamsCount - 2) / 2);
		int teamVerticsCount = teamsCount - 1;
		
		// start vertex number is g.V()-2, end vertex number is g.V()-1
		// The last 2 means start vertex and end vertex
		FlowNetwork flowNetwork = new FlowNetwork(againstVerticsCount + teamVerticsCount + 2); 
		
		int againstVertexNumber = 0;
		List<Integer> teamOrder = new ArrayList<>();
		for (int i=0; i<teamsCount - 1; i++) {
			for (int j=0; j<teamsCount - 1; j++) {
				if (i == j || i == teams.indexOf(team) || j == teams.indexOf(team)) continue;
				if (!teamOrder.contains(i)) teamOrder.add(i);
				if (!teamOrder.contains(j)) teamOrder.add(j);
				
				FlowEdge edgeFromSource = new FlowEdge(flowNetwork.V()-2, againstVertexNumber, against.get(i+"-"+j));
				FlowEdge edgeToI = new FlowEdge(againstVertexNumber, againstVerticsCount+teamOrder.indexOf(i), Double.POSITIVE_INFINITY);
				FlowEdge edgeToJ = new FlowEdge(againstVertexNumber, againstVerticsCount+teamOrder.indexOf(j), Double.POSITIVE_INFINITY);
				
				flowNetwork.addEdge(edgeFromSource);
				flowNetwork.addEdge(edgeToI);
				flowNetwork.addEdge(edgeToJ);
				
				againstVertexNumber++;
			}
		}
		
		for (int i=0; i<teamOrder.size(); i++) {
			FlowEdge edge = new FlowEdge(againstVerticsCount+i, flowNetwork.V()-1, 
					wins(team)+remaining(team)-wins.get(teamOrder.get(i)));
		}
		
		return false;
	}

	public Iterable<String> certificateOfElimination(String team)
			throws IllegalArgumentException {
		// TODO call before isEliminated
		return null;
	}

	public static void main(String[] args) {
		BaseballElimination division = new BaseballElimination(args[0]);
		for (String team : division.teams()) {
			if (division.isEliminated(team)) {
				StdOut.print(team + " is eliminated by the subset R = { ");
				for (String t : division.certificateOfElimination(team))
					StdOut.print(t + " ");
				StdOut.println("}");
			} else {
				StdOut.println(team + " is not eliminated");
			}
		}
	}
}
