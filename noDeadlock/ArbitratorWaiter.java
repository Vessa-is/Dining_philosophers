package noDeadlock;

public class ArbitratorWaiter {
    private static final int N = 5;
    private static final Object[] forks = new Object[N];
    private static final Object waiter = new Object();

    static class Philosopher extends Thread {
        private final int id;

        public Philosopher(int id) {
            this.id = id;
        }

        public void run() {
            while (true) {
                think();
                synchronized (waiter) {
                    synchronized (forks[id]) {
                        synchronized (forks[(id + 1) % N]) {
                            eat();
                        }
                    }
                }
            }
        }

        private void think() {
            System.out.println("Filozofi " + id + " po mendon...");
            try {
                Thread.sleep((int)(Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void eat() {
            System.out.println("Filozofi " + id + " po ha me pirunjt " + id + " dhe " + ((id + 1) % N));
            try {
                Thread.sleep((int)(Math.random() * 2000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Filozofi " + id + " ka përfunduar ushqimin.");
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < N; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < N; i++) {
            new Philosopher(i).start();
        }
    }
}

