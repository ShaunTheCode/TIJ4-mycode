package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AutomicTest implements Runnable {
    private int i = 0;

    public int getValue() {
        return i;
    }

    private synchronized void evenIncrement() {
        i++;
        i++;
    }

    @Override
    public void run() {
        while (true) {
            evenIncrement();
        }
    }

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        AutomicTest automicTest = new AutomicTest();
        exec.execute(automicTest);
        while (true) {
            int val = automicTest.getValue();
            if (val % 2 != 0) {
                System.out.println(val);
                System.exit(0);
            }
        }
    }
}
