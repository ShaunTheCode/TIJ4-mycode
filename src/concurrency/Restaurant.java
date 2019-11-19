package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Meat {
    private final int orderNum;

    public Meat(int orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "Meat " + orderNum;
    }
}

class Chef implements Runnable {
    private Restaurant restaurant;
    private int count = 0;

    public Chef(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    while (restaurant.meat != null) {
                        wait();
                    }
                }
                if (++count == 10) {
                    System.out.println("Out of food,closing");
                    restaurant.executorService.shutdownNow();
                }
                System.out.println("Order up");
                synchronized (restaurant.waitPerson) {
                    restaurant.meat = new Meat(count);
                    restaurant.waitPerson.notifyAll();
                }
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("The chef interrupted");
        }


    }
}

class WaitPerson implements Runnable {
    private Restaurant restaurant;

    public WaitPerson(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    while (restaurant.meat == null) {
                        wait();
                    }
                }
                System.out.println("WaitPerson got " + restaurant.meat);
                synchronized (restaurant.chef) {
                    this.restaurant.meat = null;
                    restaurant.chef.notify();
                }
                TimeUnit.MILLISECONDS.sleep(100);

            }
        } catch (InterruptedException e) {
            System.out.println("The WaitPerson interrupted");
        }


    }
}

public class Restaurant {
    Meat meat;
    Chef chef = new Chef(this);
    WaitPerson waitPerson = new WaitPerson(this);
    ExecutorService executorService = Executors.newCachedThreadPool();

    public Restaurant() {
        executorService.execute(chef);
        executorService.execute(waitPerson);
    }

    public static void main(String[] args) {
        new Restaurant();

    }
}
