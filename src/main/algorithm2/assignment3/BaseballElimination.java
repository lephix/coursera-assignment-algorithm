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
	
	private int againstVerticsCount = 0;
	private List<Integer> teamOrder;
	
	private int maxWin = 0;
	private FordFulkerson fordFulkerson;
	private String lastTeam = "";

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
		
		// this variable store count of the first part graph of all team against vertices.
		againstVerticsCount = ((teamsCount - 1) * (teamsCount - 2) / 2);
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
		int teamVerticsCount = teamsCount - 1;
		
		// start vertex number is g.V()-2, end vertex number is g.V()-1
		// The last 2 means start vertex and end vertex
		FlowNetwork flowNetwork = new FlowNetwork(againstVerticsCount + teamVerticsCount + 2); 
		int startVertex = flowNetwork.V() - 2;
		int sinkVertex = flowNetwork.V() - 1;
		
		int againstVertexNumber = 0;
		int maxCapacity = 0;
		this.teamOrder = new ArrayList<>();
		for (int i=0; i<teamsCount - 1; i++) {
			for (int j=0; j<teamsCount - 1; j++) {
				if (i >= j || i == teams.indexOf(team) || j == teams.indexOf(team)) continue;
				if (!teamOrder.contains(i)) teamOrder.add(i);
				if (!teamOrder.contains(j)) teamOrder.add(j);
				
				FlowEdge edgeFromSource = new FlowEdge(startVertex, againstVertexNumber, against.get(i+"-"+j));
				FlowEdge edgeToI = new FlowEdge(againstVertexNumber, againstVerticsCount+teamOrder.indexOf(i), Double.POSITIVE_INFINITY);
				FlowEdge edgeToJ = new FlowEdge(againstVertexNumber, againstVerticsCount+teamOrder.indexOf(j), Double.POSITIVE_INFINITY);
				
				flowNetwork.addEdge(edgeFromSource);
				flowNetwork.addEdge(edgeToI);
				flowNetwork.addEdge(edgeToJ);
				
				maxCapacity += against.get(i+"-"+j);
				againstVertexNumber++;
			}
		}
		
		for (int i=0; i<teamOrder.size(); i++) {
			int otherTeamCapacity = wins(team)+remaining(team)-wins.get(teamOrder.get(i));
			if (otherTeamCapacity < 0) otherTeamCapacity = 0;
			FlowEdge edge = new FlowEdge(againstVerticsCount+i, sinkVertex, otherTeamCapacity);
			flowNetwork.addEdge(edge);
		}
		
		FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, startVertex, sinkVertex);
		lastTeam = team;
		this.fordFulkerson = fordFulkerson;
		
		if (maxCapacity > fordFulkerson.value()) {
			return true;
		}
		
		return false;
	}

	public Iterable<String> certificateOfElimination(String team)
			throws IllegalArgumentException {
		if (!lastTeam.equals(team)) {
			isEliminated(team);
		}
		
		List<String> returnList = new ArrayList<>();
		
		for (String otherTeam : teams()) {
			if (otherTeam.equals(team)) continue;
			int otherTeamPos = againstVerticsCount + teamOrder.indexOf(teams.indexOf(otherTeam));
			if (this.fordFulkerson.inCut(otherTeamPos)) {
				returnList.add(otherTeam);
			}
		}
		return returnList;
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
