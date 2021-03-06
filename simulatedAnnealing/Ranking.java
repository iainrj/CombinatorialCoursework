package simulatedAnnealing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.Arrays;

public class Ranking {
    private List<Integer> ranking;
    private int kemenyScore;
    private static int[][] weights;
  
    public Ranking(int[][] givenWeights) {
        kemenyScore = 0;
        weights = givenWeights;
    }
  
    public Ranking(List<Integer> givenRanking, int[][] givenWeights) {
        ranking = givenRanking;
        weights = givenWeights;
        kemenyScore = 0;
    }
  
    public Ranking(List<Integer> givenRanking, int[][] givenWeights, int givenKemenyScore) {
        ranking = givenRanking;
        weights = givenWeights;
        kemenyScore = givenKemenyScore;
    }
  
    public void setKemenyScore(int score) {
        kemenyScore = score;
    }
    
    public int getKemenyScore() {
        return kemenyScore;
    }
    
    public void setRanking(List<Integer> givenRanking) {
        ranking = givenRanking;
    }
    
    public List<Integer> getRanking() {
        return ranking;
    }
    
    public int calculateFullCost() {
        /* Calculate the kemenyScore of the current ranking, by doing a full traversal of the list */      
        for (int i=0; i < ranking.size(); i++) {
          kemenyScore += getOffsetCost(i, ranking);
        }
            
        return kemenyScore;
    }
      
    public int getCost(List<Integer> currentRanking, List<Integer> oldRanking, int oldCost, int val1, int val2) {
        /* Calculate the kemenyScore of a given ranking, using the oldRanking's cost and the swapped indexes */
        int kemenyScore = 0;
        
        int removals = getRemovalsScore(val1, val2, oldRanking);
        int additions = getAdditionsScore(val1, val2, currentRanking);
        kemenyScore = (oldCost  - removals ) + additions;

        return kemenyScore;
    }

    private int getRemovalsScore(int val1, int val2, List<Integer> oldRanking) {
        /* getRemovalsScore and getAdditionsScore are identical but are split out for ease of debugging */
        int removalsScore = 0;
        int valueAtVal1 = oldRanking.get(val1);
        int valueAtVal2 = oldRanking.get(val2);
        
        removalsScore += getOffsetCost(val1, oldRanking);
        removalsScore += getOffsetCost(val2, oldRanking);

        return removalsScore;
    }

    private int getAdditionsScore(int val1, int val2, List<Integer> currentRanking) {
        /* getRemovalsScore and getAdditionsScore are identical but are split out for ease of debugging */
        int additionsScore = 0;
        int valueAtVal1 = currentRanking.get(val1);
        int valueAtVal2 = currentRanking.get(val2);
        
        additionsScore += getOffsetCost(val1, currentRanking);
        additionsScore += getOffsetCost(val2, currentRanking);
        
        return additionsScore;
    }

    private int getOffsetCost(int offset, List<Integer> givenRanking) {
        /* gets the cost from the offset index to the end of the Ranking */
        int score = 0;

        for (int j = offset; j < givenRanking.size(); j++){
            int atJ = givenRanking.get(j);
            int atOffset = givenRanking.get(offset);
            
            score += weights[atJ-1][atOffset-1];
        }

        return score;
    }

    public List<Integer> generateNeighbouringSolution(List<Integer> currentSolution, int val1, int val2) {
        /* pick an index from currentSolution and it's neighbour immediately to its right and swap their positions */
        List<Integer> neighbourRanking = new ArrayList<Integer>(currentSolution);
        Collections.copy(neighbourRanking, currentSolution); 
        Collections.swap(neighbourRanking, val1, val2);
        
        return neighbourRanking;
    }

    public List<Integer> constructInitialSolution() {
        /* construct [1,2,3,4...24] as initial solution */
        List<Integer> localRanking = new ArrayList();

        for (int i = 1; i < 25; i++) {
            localRanking.add(i);
        }

        return localRanking;
    }
}
