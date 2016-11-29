package simulatedAnnealing;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;

public class Tournament {
  private int[][] weights;
  private List teams;
  
  public Tournament() {
    teams = new ArrayList();
    weights = new int [24][24];
  }
  
  public List getTeams() {
    return teams;
  }
  
  public void getRealNames(List<Integer> ranking) {
    // for a given ranking, print the names of the participants from the teams array
    String leftAlignFormat = "| %-3d | %-28s |%n";
    
    System.out.format("+-----------------+------------------+%n");
    System.out.format("| Pos.| Team Members                 |%n");
    System.out.format("+-----------------+------------------+%n");
    
    for (int i = 0; i < ranking.size(); i++) {
      System.out.format(leftAlignFormat, i+1, teams.get(ranking.get(i)));
    }
    
    System.out.format("+-----------------+------------------+%n"); 
  }
  
  public int[][] convertTournamentData(String filename) {
    try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line = br.readLine();
      int count = 0;

      while (line != null) {
          if (count > 0 && count < 25) {
                String[] parts = line.split(",");
                int index = Integer.parseInt(parts[0]) - 1; // identifying number becomes index in array - 1. Rest of algorithm will use 0-23
                String players = parts[1]; // name of players becomes value in array
                teams.add(index, players);
            } else if (count > 25) {
                String[] parts = line.split(",");
                int weight = Integer.parseInt(parts[0]); // weight between edge1 and edge2
                int edge1 = Integer.parseInt(parts[1]) - 1; // lines start at 1, matrix starts at 0 so subtract 1
                int edge2 = Integer.parseInt(parts[2]) - 1;

                weights[edge1][edge2] = weight;
            }
            line = br.readLine();
            count++;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return weights;
  }
}