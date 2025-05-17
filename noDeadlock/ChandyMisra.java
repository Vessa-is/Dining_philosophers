package noDeadlock;

import java.util.HashMap;
import java.util.Map;

class Fork {
    private boolean isDirty = true;
    private Philosopher owner;

    public Fork(Philosopher owner) {
        this.owner = owner;
    }

    public synchronized void request(Philosopher requester) {
        if (owner != requester) {
            while (!isDirty) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            owner = requester;
            isDirty = false;
        }
    }

    public synchronized void release() {
        isDirty = true;
        notifyAll();
    }

    public void setOwner(Philosopher philosopher) {
        this.owner = philosopher;
    }

    public Philosopher getOwner() {
        return owner;
    }
}

class Philosopher extends Thread {
    public final int id;
    private Philosopher leftNeighbor;
    private Philosopher rightNeighbor;
    private final Map<Philosopher, Fork> forks = new HashMap<>();

    public Philosopher(int id) {
        this.id = id;
    }

    public void setNeighbors(Philosopher left, Philosopher right) {
        this.leftNeighbor = left;
        this.rightNeighbor = right;
    }

    public void addFork(Philosopher neighbor, Fork fork) {
        forks.put(neighbor, fork);
    }

    private void think() throws InterruptedException {
        System.out.println("Filozofi " + id + " është duke menduar.");
        Thread.sleep((int)(Math.random() * 1000) + 500);
    }

    private void eat() throws InterruptedException {
        System.out.println("Filozofi " + id + " është duke ngrënë.");
        Thread.sleep((int)(Math.random() * 1000) + 500);
    }

    private void requestFork(Philosopher neighbor) {
        Fork fork = forks.get(neighbor);
        if (fork == null) {
            System.err.println("Filozofi " + id + " nuk gjeti pirunin nga fqinj " + neighbor.id);
            return;
        }

        synchronized (fork) {
            if (fork.getOwner() != this) {
                fork.request(this);
                System.out.println("Filozofi " + id + " mori pirunin nga fqinj " + neighbor.id);
            }
        }
    }

    private void releaseFork(Philosopher neighbor) {
        Fork fork = forks.get(neighbor);
        if (fork != null) {
            synchronized (fork) {
                fork.release();
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();
                requestFork(leftNeighbor);
                requestFork(rightNeighbor);
                eat();
                releaseFork(leftNeighbor);
                releaseFork(rightNeighbor);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class ChandyMisra {
    public static void main(String[] args) {
        int N = 5;
        Philosopher[] philosophers = new Philosopher[N];
        Fork[] forks = new Fork[N];

        // Step 1: Create all philosophers
        for (int i = 0; i < N; i++) {
            philosophers[i] = new Philosopher(i);
        }

        // Step 2: Set neighbors correctly
        for (int i = 0; i < N; i++) {
            Philosopher left = philosophers[(i + N - 1) % N];
            Philosopher right = philosophers[(i + 1) % N];
            philosophers[i].setNeighbors(left, right);
        }

        // Step 3: Create forks and assign them to both philosophers
        for (int i = 0; i < N; i++) {
            Philosopher p1 = philosophers[i];
            Philosopher p2 = philosophers[(i + 1) % N];
            Philosopher owner = (p1.id < p2.id) ? p1 : p2;
            Fork fork = new Fork(owner);

            forks[i] = fork;

            p1.addFork(p2, fork);
            p2.addFork(p1, fork);
        }

        // Step 4: Start all philosopher threads
        for (Philosopher philosopher : philosophers) {
            philosopher.start();
        }
    }
}
