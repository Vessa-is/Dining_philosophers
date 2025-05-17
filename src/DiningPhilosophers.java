import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
    private static final int NUM_PHILOSOPHERS = 5;
    private static final ReentrantLock[] forks = new ReentrantLock[NUM_PHILOSOPHERS];

    static {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ReentrantLock();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            final int philosopherId = i;
            new Thread(() -> philosopher(philosopherId)).start();
        }
    }

    private static void philosopher(int id) {
        int leftFork = id;
        int rightFork = (id + 1) % NUM_PHILOSOPHERS;

        int firstFork = Math.min(leftFork, rightFork);
        int secondFork = Math.max(leftFork, rightFork);

        while (true) {
            think(id);

            forks[firstFork].lock();
            forks[secondFork].lock();

            try {
                eat(id);
            } finally {
                forks[secondFork].unlock();
                forks[firstFork].unlock();
            }
        }
    }

    private static void think(int id) {
        System.out.println("Filozofi " + id + " po mendon...");
        sleep(1000);
    }

    private static void eat(int id) {
        System.out.println("Filozofi " + id + " po ha 🍝...");
        sleep(1000);
    }

    private static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
