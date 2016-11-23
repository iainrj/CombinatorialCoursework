import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.String;
import java.util.Random;
import java.util.Collections;

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

    public List simulate () {
        List<Integer> xNow = constructInitialSolution();
        // System.out.println("initialSolution: " + xNow);
        double t = TI;

        int i = 0;
        while (i < num_iterations) {
//            System.out.println(i);
            for (int k = 0; k < TL; k++) {
//                System.out.println(T);
                Random random = new Random();
                int val1 = xNow.get(random.nextInt(xNow.size()));
                int val2 = xNow.get(random.nextInt(xNow.size()));

                List xPrime = generateNeighbouringSolution(xNow, val1, val2);
                int deltaC = getDeltaC(val1, val2, xPrime, xNow);

                if (deltaC <= 0) {
                    xNow = xPrime;
                } else {
                    double q = Math.random();

                    if (q < Math.exp(deltaC / t)) {
                        xNow = xPrime;
                    }
                }
            }
            t = setNewTemperature(t);
            i++;
        }

        return xNow;
    }

    private double setNewTemperature(double t) {
        return cr_coefficient * t;
    }

    private int getDeltaC(int val1, int val2, List xPrime, List xNow) {
        int costXNow = getCost(xNow, -1);
        int costXPrime = getCost(xPrime, costXNow);
        
        return (costXPrime - costXNow);
    }

    private int getCost(List<Integer> ranking, int oldCost) {
        int kemenyScore = 0;
        
        if (oldCost < 0) {
            // calculate cost of whole ranking
        } else {
            // calculate the difference between old and new cost
            // subract every score involving x and y (swapped values) at start_indexes
            // add back score involving x and y in new indexes
            for (int index : ranking) {

            }
        }
        

        return kemenyScore;
    }

    // private List getDisagreements(List<Integer> ranking, int[][] weights) {
    //     List disagreements = new ArrayList();

    //     for (Integer team : ranking) {
    //         for (int i = team + 1; team < ranking.size(); i++) {
    //             disagreements.add(team);
    //         }
    //     }

    //     return disagreements;
    // }

    private List generateNeighbouringSolution(List<Integer> currentSolution, int val1, int val2) {
        // pick two indexes from currentSolution and swap their positions
        return Collections.swap(currentSolution, val1, val2);
    }

    private List constructInitialSolution() {
        List initialSolution = new ArrayList();

        for (int i = 0; i < 24; i++) {
            initialSolution.add(i);
        }

        return initialSolution;
    }

    public static void main(String[] args) {
        String filename = args[0];
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

        SimulatedAnnealing s1 = new SimulatedAnnealing(100, 5, 25, 0.999, weights);
        List solution = s1.simulate();
        System.out.println("solution: " + solution);
    }
}