package noDeadlock;
import java.util.concurrent.Semaphore;

public class SemaphoreMutex {
    private static final int NUM_PHILOSOPHERS = 5;

    private static final Semaphore maxDiners = new Semaphore(NUM_PHILOSOPHERS - 1);

    private static final Semaphore[] forks = new Semaphore[NUM_PHILOSOPHERS];

    public static void main(String[] args) {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new Semaphore(1);
        }

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            new Philosopher(i).start();
        }
    }

    static class Philosopher extends Thread {
        private final int id;
        private final int leftFork;
        private final int rightFork;

        Philosopher(int id) {
            this.id = id;
            this.leftFork = id;
            this.rightFork = (id + 1) % NUM_PHILOSOPHERS;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    think();

                    maxDiners.acquire();

                    forks[leftFork].acquire();
                    forks[rightFork].acquire();

                    eat();

                    forks[leftFork].release();
                    forks[rightFork].release();

                    maxDiners.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void think() throws InterruptedException {
            System.out.println("Filozofi " + id + " po mendon.");
            Thread.sleep((int)(Math.random() * 1000));
        }

        private void eat() throws InterruptedException {
            System.out.println("Filozofi " + id + " po ha.");
            Thread.sleep((int)(Math.random() * 1000));
        }
    }
}
