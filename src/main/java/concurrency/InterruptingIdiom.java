package concurrency;

import java.util.concurrent.TimeUnit;

import static net.mindview.util.Print.print;

class NeedsCleanUp {
    private final int id;

    public NeedsCleanUp(int ident) {
        id = ident;
        print("NeedsCleanUp " + id);
    }

    public void cleanUp() {
        print("Clean up " + id);
    }
}

class Blocked3 implements Runnable {
    private double d = 0.0;

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                //point1
                NeedsCleanUp n1 = new NeedsCleanUp(1);
                try {
                    print("Sleeping");
                    TimeUnit.SECONDS.sleep(1);
                    //point2
                    //在point2之后调用interrupt().则首先循环结束，本地对象被销毁，最后循环在while顶部退出
                    //如果在point1和point2之间执行interrupt().那么在第一次调用sleep之前，由InterruptionException退出
                    NeedsCleanUp n2 = new NeedsCleanUp(2);
                    try {
                        print("Calculating");
                        for (int i = 0; i < 2500000; i++) {
                            d = d + (Math.PI + Math.E) / d;
                        }
                        print("Finished time-consuming operation");
                    } finally {
                        n2.cleanUp();
                    }
                } finally {
                    n1.cleanUp();
                }
            }
            print("Exiting via while() test");
        } catch (InterruptedException e) {
            print("Exiting via InterruptedException");

        }
    }
}

public class InterruptingIdiom {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            print("Useage: java InterruptingIdiom delay-in-mS");
            System.exit(1);
        }
        Thread t = new Thread(new Blocked3());
        t.start();
        TimeUnit.MILLISECONDS.sleep(new Integer(args[0]));
        t.interrupt();
    }
}
