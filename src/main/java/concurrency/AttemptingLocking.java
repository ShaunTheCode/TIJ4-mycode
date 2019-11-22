package concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class AttemptingLocking {
    private ReentrantLock lock = new ReentrantLock();

    public void untimed() {
        boolean capture = lock.tryLock();
        try {
            System.out.println("tryLock(): " + capture);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if (capture) {
                lock.unlock();
            }
        }
    }

    public void timed() {
        boolean captured = false;
        try {
            captured = lock.tryLock(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            System.out.println("tryLock(2, TimeUnit.SECONDS): " + captured);
        } finally {
            if (captured) {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        final AttemptingLocking attemptingLocking=new AttemptingLocking();
        attemptingLocking.untimed();
        attemptingLocking.timed();
        new Thread(){
            {setDaemon(true);}

            @Override
            public void run() {
                attemptingLocking.lock.lock();
                System.out.println("acquired");
            }
        }.start();
        //Thread.yield();
        //添加语句，让后台线程可以执行
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        attemptingLocking.untimed();
        attemptingLocking.timed();

    }
}
