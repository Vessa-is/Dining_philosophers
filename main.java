import java.util.concurrent.Semaphore;

class Main {
    static Thread[] philosopher;
    static Semaphore[] fork;
    static int N = 5;

    static Thread philosopher(int i) {
        return new Thread(() -> {
            try {
                while(true) {
                    System.out.println(i + ": thinking");
                    Thread.sleep(100);
                    int left = i;
                    int right = (i + 1) % N;

                    // Pick up left fork first (no ordering)
                    fork[left].acquire();
                    System.out.println(i + ": picked up left fork " + left);

                    // Pick up right fork second (no ordering)
                    fork[right].acquire();
                    System.out.println(i + ": picked up right fork " + right);

                    System.out.println(i + ": eating");
                    Thread.sleep(100);

                    // Put down forks
                    fork[right].release();
                    fork[left].release();
                }
            } catch (InterruptedException e) {
                System.out.println("ERROR: Philosopher " + i + " interrupted: " + e.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        System.out.println(N + " philosophers get to work ...");
        philosopher = new Thread[N];
        fork = new Semaphore[N];
        for (int i = 0; i < N; i++) {
            fork[i] = new Semaphore(1);
            philosopher[i] = philosopher(i);
        }
        for (int i = 0; i < N; i++)
            philosopher[i].start();
    }
}
