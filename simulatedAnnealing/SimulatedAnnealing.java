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
        double t = TI;
        
        Ranking xNow = new Ranking(weights);
        xNow.setRanking(xNow.constructInitialSolution());
        xNow.setKemenyScore(xNow.calculateFullCost());

        List<Integer> xNowRanking = new ArrayList(xNow.getRanking());
        Collections.copy(xNowRanking, xNow.getRanking());
        Ranking best = new Ranking(xNowRanking, weights, xNow.getKemenyScore());
        
        for (int i=0; i < num_iterations; i++) {
            for (int k = 0; k < TL; k++) {
                Random random = new Random();
                int val1 = random.nextInt(xNow.getRanking().size() - 1);
                int val2 = val1 + 1;

                List<Integer> neighbourOfXNow = xNow.generateNeighbouringSolution(xNow.getRanking(), val1, val2);
                Ranking xPrime = new Ranking(neighbourOfXNow, weights);
                int costXNow = xNow.getKemenyScore();
                int costXPrime = xPrime.getCost(xPrime.getRanking(), xNow.getRanking(), costXNow, val1, val2); // new cost
                xPrime.setKemenyScore(costXPrime);

                int deltaC = (costXPrime - costXNow);

                if (deltaC <= 0) {
                    xNow.setRanking(xPrime.getRanking());
                    xNow.setKemenyScore(xPrime.getKemenyScore());
                } else {
                    double q = Math.random();

                    if (q < Math.exp(-(deltaC)/t)) {
                        xNow.setRanking(xPrime.getRanking());
                        xNow.setKemenyScore(xPrime.getKemenyScore());
                    }
                }
                
                if (xNow.getKemenyScore() < best.getKemenyScore()) {
                    i = 0;
                    best.setKemenyScore(xNow.getKemenyScore());
                    best.setRanking(xNow.getRanking());
                }
            }
            t = setNewTemperature(t);
            
            if (i % m == 0 && i > 0) {
                /* Task 2, Part 4 */
                System.out.println("Best cost: " + best.getKemenyScore());
                System.out.println("Current cost: " + xNow.getKemenyScore());
            }
        }

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
        // int m = getUserInput(); // Task 2, Part 2
        int m = 100; // remove 
        Tournament iceDance1998 = new Tournament();
        int [][] weights = iceDance1998.convertTournamentData(filename); // Task 2, Part 3
        // iceDance1998.printMatrix(weights); // un-comment to print matrix of weights
        
        // int Ti, int Tl, int num_iterations, double cr_coefficient, int[][] weights
        SimulatedAnnealing s1 = new SimulatedAnnealing(50, 10, 25, 0.85, weights);
        
        // From http://stackoverflow.com/questions/5204051/how-to-calculate-the-running-time-of-my-program
        long startTime = System.nanoTime();
        // SimulatedAnnealing runs
        Ranking solution = s1.runSA(m);
        // simulatedAnnealing ends
        long runtime = System.nanoTime() - startTime;
        
        iceDance1998.getRealNames(solution.getRanking()); // Task 2, Part 5
        System.out.println("Kemeny Score for best Ranking: " + solution.getKemenyScore()); // Task 2, Part 5
        System.out.println("Runtime: " + runtime/1000000 + " milliseconds"); // Task 2, Part 5
    }
}
