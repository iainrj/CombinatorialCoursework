import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.String;
import java.util.Random;
import java.util.Collections;
import java.util.Arrays;
import java.util.Scanner; 

public class SimulatedAnnealing {
    private double TI = 100;
    private int TL = 5;
    private int num_iterations = 25;
    private double cr_coefficient = 0.999;
    private int[][] weights;

    public SimulatedAnnealing(int Ti, int Tl, int num_iterations, double cr_coefficient, int[][] weights) {
        this.TI = Ti;
        this.TL = Tl;
        this.num_iterations = num_iterations;
        this.cr_coefficient = cr_coefficient;
        this.weights = weights;
    }

    public List runSA(int m) {
        List<Integer> xNow = constructInitialSolution();
        double t = TI;

        int i = 0;
        int costXNow = getInitialCost(xNow);
        System.out.println("initialSolution: " + xNow);
        System.out.println("initialCost: " + costXNow);

        List best = xNow;
        int bestCost = costXNow;

        while (i < num_iterations) {
            // System.out.println("iteration: " + i);        
            if (i % m == 0 && i > 0) {
                // System.out.println("print out current solution");
                System.out.println("Best: " + best);
            }

            for (int k = 0; k < TL; k++) {
                System.out.println("oldSolution: " + xNow);
                System.out.println("oldCost: " + costXNow);
                // System.out.println("t: " + t);
                Random random = new Random();
                int val1 = random.nextInt(xNow.size() - 1);
                int val2 = val1 + 1;

                List xPrime = generateNeighbouringSolution(xNow, val1, val2);
                System.out.println("newSolution: " + xPrime);
                int costXPrime = getCost(xPrime, xNow, costXNow, val1, val2); // new cost

                System.out.println("cost old: " + costXNow);
                System.out.println("cost new: " + costXPrime);

                int deltaC = (costXPrime - costXNow);

                System.out.println("deltaC: " + deltaC);

                if (deltaC <= 0) {
                    System.out.println("Move to this solution");
                    xNow = xPrime;
                    costXNow = costXPrime;
                } else {
                    double q = Math.random();

                    if (q < Math.exp(deltaC / t)) {
                        System.out.println("Also move to this solution");
                        xNow = xPrime;
                        costXNow = costXPrime;
                    }
                }

                if (costXNow <= bestCost) {
                    bestCost = costXNow;
                    best = xNow;
                }
            }
            t = setNewTemperature(t);
            i++;
        }

        // return xNow;
        System.out.println(bestCost);
        return best;
    }

    private double setNewTemperature(double t) {
        return cr_coefficient * t;
    }

    private int getInitialCost(List<Integer> ranking) {
        int kemenyScore = 0;
        
        for (int i=0; i < ranking.size(); i++) {
            kemenyScore += getOffsetCost(i, ranking);
        }
        
        return kemenyScore;
    } 
    
    private int getCost(List<Integer> ranking, List<Integer> oldRanking, int oldCost, int val1, int val2) {
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

    private List generateNeighbouringSolution(List<Integer> currentSolution, int val1, int val2) {
        // pick an index from currentSolution and it's neighbour right and swap their positions
        List<Integer> neighbour = new ArrayList<Integer>(currentSolution);
        Collections.copy(neighbour, currentSolution); 
        Collections.swap(neighbour, val1, val2);
        return neighbour;
    }

    private List constructInitialSolution() {
        List initialSolution = new ArrayList();

        for (int i = 0; i < 24; i++) {
            initialSolution.add(i);
        }

        return initialSolution;
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
    
    private static int getUserInput() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter a value for m: ");
        int m = scan.nextInt();
        
        return m;
    }
    
    private static int[][] convertTournamentData(String filename) {
        int[][] weights = new int [24][24];
        List teams = new ArrayList();
        
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

    public static void main(String[] args) {
        String filename = args[0];
        int m = getUserInput();
        int [][] weights = convertTournamentData(filename);
        
        SimulatedAnnealing s1 = new SimulatedAnnealing(100, 5, 25, 0.999, weights);
        s1.printMatrix(weights);
        List solution = s1.runSA(10);
        System.out.println("solution: " + solution);
    }
}
