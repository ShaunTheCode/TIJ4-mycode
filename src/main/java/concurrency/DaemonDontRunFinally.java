package concurrency;

import java.util.concurrent.TimeUnit;

import static net.mindview.util.Print.print;

class ADaemon implements Runnable {
    @Override
    public void run() {
        try {
            print("starting ADaemon ");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            print("Exiting via InterruptedException");
        } finally {
            print("This should always run?");
        }
    }
}

public class DaemonDontRunFinally {
    public static void main(String[] args) {
        Thread t = new Thread(new ADaemon());
        //t.setDaemon(true);
        t.start();

    }
}
