package concurrency;

import java.util.concurrent.TimeUnit;

import static net.mindview.util.Print.printnb;

class Daemon implements Runnable{
    private Thread[] threads=new Thread[10];

    @Override
    public void run() {
        for (int i = 0; i <threads.length ; i++) {
            threads[i]=new Thread(new DaemonSpawn());
            threads[i].start();
            printnb("DaemonSpawn "+i+" started, ");
        }
        for (int i = 0; i <threads.length ; i++) {
            printnb("threads["+i+"].isDaemon() = "+threads[i].isDaemon()+", ");
        }
        while (true){
            Thread.yield();
        }

    }
}

class DaemonSpawn implements Runnable{
    @Override
    public void run() {
        while (true){
            Thread.yield();
        }
    }
}

public class Daemons {
    public static void main(String[] args) throws Exception{
        Thread t=new Thread(new Daemon());
        t.setDaemon(true);
        t.start();
        printnb("t.isDaemon() = "+t.isDaemon()+", ");
        TimeUnit.SECONDS.sleep(1);
    }
}
