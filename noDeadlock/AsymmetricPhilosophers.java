package noDeadlock;

import java.util.concurrent.locks.ReentrantLock;

public class AsymmetricPhilosophers {

    public static void main(String[] args) {
        int numPhilosophers = 5;
        ReentrantLock[] forks = new ReentrantLock[numPhilosophers];
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new ReentrantLock();
        }

        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        for (int i = 0; i < numPhilosophers; i++) {
            ReentrantLock leftFork = forks[i];
            ReentrantLock rightFork = forks[(i + 1) % numPhilosophers];
            philosophers[i] = new Philosopher(i, leftFork, rightFork);
            new Thread(philosophers[i]).start();
        }
    }

    static class Philosopher implements Runnable {
        private final int id;
        private final ReentrantLock leftFork;
        private final ReentrantLock rightFork;

        public Philosopher(int id, ReentrantLock leftFork, ReentrantLock rightFork) {
            this.id = id;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    think();
                    pickUpForks();
                    eat();
                    putDownForks();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void think() throws InterruptedException {
            System.out.println("Filozofi " + id + " po mendon.");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void pickUpForks() {
            // Marrja asimetrike e pirunave: ID çift marrin së pari majtas, ID tek djathtas
            if (id % 2 == 0) {
                leftFork.lock();
                rightFork.lock();
            } else {
                rightFork.lock();
                leftFork.lock();
            }
            System.out.println("Filozofi " + id + " mori pirunat.");
        }

        private void eat() throws InterruptedException {
            System.out.println("Filozofi " + id + " po ha.");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void putDownForks() {
            leftFork.unlock();
            rightFork.unlock();
            System.out.println("Filozofi " + id + " lëshoi pirunat.");
        }
    }
}