package concurrency;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Blocker {
    public synchronized void waitingCall() {
        try {
            while (!Thread.interrupted()) {
                wait();
                System.out.print(Thread.currentThread() + " ");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized void prod() {
        notify();
    }

    synchronized void prodAll() {
        notifyAll();
    }
}

class Task implements Runnable {
    static Blocker blocker = new Blocker();

    @Override
    public void run() {
        blocker.waitingCall();
    }
}

class Task2 implements Runnable {
    static Blocker blocker = new Blocker();

    @Override
    public void run() {
        blocker.waitingCall();

    }
}

public class NotifyVsNotifyall {
    public static void main(String[] args) {
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            for (int i = 0; i < 5; i++) {
                executorService.execute(new Task());
            }
            executorService.execute(new Task2());
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                boolean prod = true;

                @Override
                public void run() {
                    if (prod) {
                        System.out.print("\nnotify() ");
                        Task.blocker.prod();
                        prod = false;
                    } else {
                        System.out.print("\nnotifyAll() ");
                        Task.blocker.prodAll();
                        prod = true;
                    }
                }
            }, 400, 400);
            TimeUnit.SECONDS.sleep(5);
            timer.cancel();
            System.out.println("\nTimer cancled");
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.print("Task2.blocker.prodAll() ");
            Task2.blocker.prodAll();
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println("\nShutting down ");
            executorService.shutdownNow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
