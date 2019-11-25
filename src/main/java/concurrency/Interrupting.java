package concurrency;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static net.mindview.util.Print.print;

class SleepBlocker implements Runnable {

    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            print("InterruptedException");
        }
        print("Exiting SleepBlocker.run()");

    }
}

class IOBlocker implements Runnable {
    private InputStream in;

    public IOBlocker(InputStream is) {
        in = is;
    }

    @Override
    public void run() {
        try {
            print("Waiting for read():");
            in.read();
        } catch (IOException e) {
            if (Thread.currentThread().isInterrupted()) {
                print("Interrupted from blocked I/O");
            } else {
                throw new RuntimeException(e);
            }
        }
        print("Exiting IOBlocker.run()");
    }
}

class SynchronizedBlocker implements Runnable {
    public synchronized void f() {
        System.out.println("SynchronizedBlocker.f(): " + this);
        while (true) {
            Thread.yield();
        }
    }

    public SynchronizedBlocker() {
        new Thread() {
            @Override
            public void run() {
                f();
            }
        }.start();
    }

    @Override
    public void run() {
        print("Trying to call f()");
        f();
        print("Exiting SynchronizedBlocker.run()");
    }
}

public class Interrupting {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    static void test(Runnable r) throws InterruptedException {
        Future<?> f = exec.submit(r);
        TimeUnit.MILLISECONDS.sleep(100);
        print("Interrupting " + r.getClass().getName());
        f.cancel(true);
        print("Interrupt send to " + r.getClass().getName());
    }

    public static void main(String[] args) throws InterruptedException {
        test(new SleepBlocker());
        test(new IOBlocker(System.in));
        test(new SynchronizedBlocker());

        TimeUnit.SECONDS.sleep(10);
        print("Aborting with System.exit(0)");
        System.exit(0);
    }
}
