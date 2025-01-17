import java.util.*;

public class KnapsackSolver {
    private static final Random random = new Random();
    private static final double MUTATION_RATE = 0.01;
    private Item[] items;
    private List<int[]> population;
    private int maxWeight;
    private static final int MAX_POPULATION = 500;

    public KnapsackSolver(Item[] items, int maxWeight) {
        this.maxWeight = maxWeight;
        this.items = items;
        initializePopulation();
    }

    private void initializePopulation() {
        this.population = new ArrayList<>();
        while (population.size() < MAX_POPULATION) {
            int[] individual = new int[items.length];
            for (int i = 0; i < items.length; i++) {
                individual[i] = random.nextInt(2);
            }
            population.add(individual);
        }
    }

    private void repairIndividual(int[] individual) {
        int totalWeight = 0;
        for (int i = 0; i < individual.length; i++) {
            if (individual[i] == 1) {
                totalWeight += items[i].weight;
            }
        }

        while (totalWeight > maxWeight) {
            int index = random.nextInt(individual.length);
            if (individual[index] == 1) {
                individual[index] = 0;
                totalWeight -= items[index].weight;
            }
        }
    }

    private int[] tournament(int[] candidate1, int[] candidate2) {
        if (fitness(candidate1) > fitness(candidate2)) {
            return candidate1;
        }
        return candidate2;
    }

    public void solve() {
        for (int i = 0; i < 500; i++) {
            int counter = 100;
            population.sort((individual1, individual2) -> Integer.compare(fitness(individual2), fitness(individual1)));

            if (i % 10 == 0) {
                System.out.println("Generation " + i + ": Best Fitness = " + fitness(population.getFirst()));
            }
            List<int[]> newPopulation = new ArrayList<>(population.subList(0, 100));

            while (counter < MAX_POPULATION) {
                int[] parent1 = tournament(population.get(random.nextInt(MAX_POPULATION)), population.get(random.nextInt(MAX_POPULATION)));
                int[] parent2 = tournament(population.get(random.nextInt(MAX_POPULATION)), population.get(random.nextInt(MAX_POPULATION)));
                crossover(parent1, parent2, newPopulation);
                counter += 2;
            }
            population = newPopulation;
        }
        population.sort((individual1, individual2) -> Integer.compare(fitness(individual2), fitness(individual1)));

        System.out.println("Generation " + 500 + "(last): Best Fitness = " + fitness(population.getFirst()));
    }

    public int fitness(int[] state) {
        int totalWeight = 0;
        int totalValue = 0;

        for (int i = 0; i < state.length; i++) {
            if (state[i] == 1) {
                totalWeight += items[i].weight;
                totalValue += items[i].value;
            }
        }
        if (totalWeight > maxWeight) {
            return 0;
        }
        return totalValue;
    }

    private void crossover(int[] parent1, int[] parent2, List<int[]> newPopulation) {
        int itemLength = items.length;
        int[] child1 = new int[itemLength];
        int[] child2 = new int[itemLength];
        int crossoverPoint = random.nextInt(itemLength);
        for (int i = 0; i < crossoverPoint; i++) {
            child1[i] = parent1[i];
            child2[i] = parent2[i];
        }
        for (int i = crossoverPoint; i < itemLength; i++) {
            child1[i] = parent2[i];
            child2[i] = parent1[i];
        }
        mutate(child1);
        newPopulation.add(child1);
        mutate(child2);
        newPopulation.add(child2);
    }

    private void mutate(int[] state) {

        for (int i = 0; i < items.length; i++) {
            if (random.nextDouble() < MUTATION_RATE) {
                state[i] = 1 - state[i];
            }
        }
        repairIndividual(state);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int m = scanner.nextInt();
        int n = scanner.nextInt();
        Item[] items1 = new Item[n];
        for (int i = 0; i < n; i++) {
            int weight = scanner.nextInt();
            int value = scanner.nextInt();
            items1[i] = new Item(weight, value);
        }
        KnapsackSolver knapsackSolver = new KnapsackSolver(items1, m);
        knapsackSolver.solve();
    }
}
