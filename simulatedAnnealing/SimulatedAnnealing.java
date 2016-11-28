package simulatedAnnealing;

import simulatedAnnealing.Ranking;
import simulatedAnnealing.Tournament;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Scanner;

public class SimulatedAnnealing {
    private double TI = 100;
    private int TL = 5;
    private int num_iterations = 10000;
    private double cr_coefficient = 0.9;
    private int[][] weights;

    public SimulatedAnnealing(int Ti, int Tl, int num_iterations, double cr_coefficient, int[][] weights) {
        this.TI = Ti;
        this.TL = Tl;
        this.num_iterations = num_iterations;
        this.cr_coefficient = cr_coefficient;
        this.weights = weights;
    }

    public Ranking runSA(int m) {
        Ranking xNow = new Ranking(weights);
        xNow.setRanking(xNow.constructInitialSolution());
        double t = TI;

        int i = 0;
        xNow.setKemenyScore(xNow.calculateFullCost());
        // System.out.println("initialSolution: " + xNow.getRanking());
        // System.out.println("initialCost: " + xNow.getKemenyScore());

        List<Integer> xNowRanking = new ArrayList(xNow.getRanking());
        Collections.copy(xNowRanking, xNow.getRanking());
        Ranking best = new Ranking(xNowRanking, weights); 
        best.setKemenyScore(xNow.getKemenyScore());

        while (i < num_iterations) {
            // System.out.println("iteration: " + i);        
            if (i % m == 0 && i > 0) {
                // System.out.println("print out current solution");
                System.out.println("Best solution: " + best.getRanking() + " with cost: " + best.getKemenyScore());
            }

            for (int k = 0; k < TL; k++) {
                // System.out.println("oldSolution: " + xNow);
                // System.out.println("oldCost: " + xNow.getKemenyScore());
                
                // System.out.println("t: " + t);
                Random random = new Random();
                int val1 = random.nextInt(xNow.getRanking().size() - 1);
                int val2 = val1 + 1;

                List<Integer> neighbourOfXNow = xNow.generateNeighbouringSolution(xNow.getRanking(), val1, val2);
                Ranking xPrime = new Ranking(neighbourOfXNow, weights);
                // System.out.println("newSolution: " + xPrime);
                int costXNow = xNow.getKemenyScore();
                int costXPrime = xPrime.getCost(xPrime.getRanking(), xNow.getRanking(), costXNow, val1, val2); // new cost
                xPrime.setKemenyScore(costXPrime);

                // System.out.println("cost old: " + costXNow);
                // System.out.println("cost new: " + costXPrime);

                int deltaC = (costXPrime - costXNow);

                if (deltaC <= 0) {
                    xNow.setRanking(xPrime.getRanking());
                    xNow.setKemenyScore(xPrime.getKemenyScore());
                } else {
                    double q = Math.random();

                    if (q < Math.exp(deltaC / t)) {
                        xNow.setRanking(xPrime.getRanking());
                        xNow.setKemenyScore(xPrime.getKemenyScore());
                    }
                }

                if (costXNow <= best.getKemenyScore()) {
                    best.setKemenyScore(costXNow);
                    
                    List<Integer> newBestRanking = new ArrayList(xNow.getRanking());
                    Collections.copy(newBestRanking, xNow.getRanking());
                    best.setRanking(newBestRanking);
                }
            }
            t = setNewTemperature(t);
            i++;
        }

        System.out.println("Best cost: " + best.getKemenyScore());
        return best;
    }

    private double setNewTemperature(double t) {
        return cr_coefficient * t;
    }
    
    private static int getUserInput() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter a value for m: ");
        int m = scan.nextInt();
        
        return m;
    }

    public static void main(String[] args) {
        String filename = args[0];
        // int m = getUserInput();
        Tournament iceDance1998 = new Tournament();
        int [][] weights = iceDance1998.convertTournamentData(filename);
        
        // int Ti, int Tl, int num_iterations, double cr_coefficient, int[][] weights
        SimulatedAnnealing s1 = new SimulatedAnnealing(10000, 750, 1000000, 0.70, weights);
        // s1.printMatrix(weights);
        Ranking solution = s1.runSA(50);
        // System.out.println("solution: " + solution.getRanking());
        iceDance1998.getRealNames(solution.getRanking());
        System.out.print("Kemeny Score for best Ranking: " + solution.getKemenyScore());
    }
}

// 1000, 500, 1000000, 0.65 = 287
// 1000, 500, 1000000, 0.70 = 272
// 10000, 500, 1000000, 0.70 = 248

