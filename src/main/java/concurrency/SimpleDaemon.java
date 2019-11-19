package concurrency;

import java.util.concurrent.TimeUnit;

import static net.mindview.util.Print.print;


public class SimpleDaemon implements Runnable {
    @Override
    public void run() {
        try {
            while(true){
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println(Thread.currentThread()+" "+this);
            }
        } catch (InterruptedException e) {
            System.out.println("sleep() intertupted");
        }

    }

    public static void main(String[] args) throws Exception{
        for (int i = 0; i < 10; i++) {
            Thread thread=new Thread(new SimpleDaemon());
            thread.setDaemon(true);
            thread.start();

        }
        print("All daemons started");
        TimeUnit.MILLISECONDS.sleep(175);

    }
}
