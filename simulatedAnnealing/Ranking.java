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
  
  public int calculateFullCost() {        
    for (int i=0; i < ranking.size(); i++) {
      kemenyScore += getOffsetCost(i, ranking);
    }
        
    return kemenyScore;
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
    
  public int getCost(List<Integer> ranking, List<Integer> oldRanking, int oldCost, int val1, int val2) {
    int kemenyScore = 0;
    
    int removals = getRemovalsScore(val1, val2, oldRanking);
    int additions = getAdditionsScore(val1, val2, ranking);
    kemenyScore = (oldCost  - removals ) + additions;

    return kemenyScore;
  }

  private int getRemovalsScore(int val1, int val2, List<Integer> oldRanking) {
    int removalsScore = 0;
    int valueAtVal1 = oldRanking.get(val1);
    int valueAtVal2 = oldRanking.get(val2);
    
    removalsScore += getOffsetCost(val1, oldRanking);
    removalsScore += getOffsetCost(val2, oldRanking);

    return removalsScore;
  }

  private int getAdditionsScore(int val1, int val2, List<Integer> ranking) {
    int additionsScore = 0;
    int valueAtVal1 = ranking.get(val1);
    int valueAtVal2 = ranking.get(val2);
    
    additionsScore += getOffsetCost(val1, ranking);
    additionsScore += getOffsetCost(val2, ranking);
    
    return additionsScore;
  }

  private int getOffsetCost(int offset, List<Integer> ranking) {
    // gets the cost from ranking[offset] to the end of the List
    int score = 0;
    for (int j = offset + 1; j < ranking.size(); j++){
        score += weights[ranking.get(j)][ranking.get(offset)];
    }

    return score;
  }

  public List<Integer> generateNeighbouringSolution(List<Integer> currentSolution, int val1, int val2) {
    // pick an index from currentSolution and it's neighbour right and swap their positions
    List<Integer> neighbourRanking = new ArrayList<Integer>(currentSolution);
    Collections.copy(neighbourRanking, currentSolution); 
    Collections.swap(neighbourRanking, val1, val2);
    
    return neighbourRanking;
  }

  public List<Integer> constructInitialSolution() {
    List<Integer> localRanking = new ArrayList();
    
    for (int i = 0; i < 24; i++) {
        localRanking.add(i);
    }
    
    return localRanking;
  }

  private void printMatrix(int[][] matrix) {
    char[] alphabet = "  A B C D E F G H I J K L M N O P Q R S T U V W X".toCharArray();
    char[] alphabet2 = "ABCDEFGHIJKLMNOPQRSTUVWX".toCharArray();

    System.out.println(alphabet);
    for (int i = 0; i < matrix.length; i++) {
        System.out.print(alphabet2[i] + " ");
        for (int j = 0; j < matrix[i].length; j++) {
            if (matrix[i][j] == 0){
                System.out.print("  ");
            } else {
                System.out.print(matrix[i][j] + " ");
            }
        }
        System.out.println();
    }
  }
}
