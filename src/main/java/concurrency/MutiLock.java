package concurrency;
import static net.mindview.util.Print.print;

public class MutiLock {

    public synchronized void f1(int count) {
        if (--count > 0) {
            print("f1() calling f2() with count " + count);
            f2(count);
        }

    }

    public synchronized void f2(int count) {
        if (--count > 0) {
            print("f2() calling f1() with count " + count);
            f1(count);
        }


    }

    public static void main(String[] args) {
        final MutiLock mutiLock = new MutiLock();
        new Thread() {
            @Override
            public void run() {
                mutiLock.f1(10);
            }
        }.start();
    }
}
