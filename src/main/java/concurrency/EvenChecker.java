package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EvenChecker implements  Runnable{
    private IntGenerator intGenerator;
    private final int id;
    public EvenChecker(IntGenerator g,int d){
        intGenerator=g;
        id=d;
    }


    @Override
    public void run() {
        while (!intGenerator.iscanceled()){
            int val=intGenerator.next();
            if(val%2!=0){
                System.out.println(val+" not even!");
                intGenerator.cancel();
            }
        }

    }

    public static  void test(IntGenerator g,int d){
        System.out.println("Press Control-C to exit");
        ExecutorService exec= Executors.newCachedThreadPool();
        for (int i = 0; i < d; i++) {
            exec.execute(new EvenChecker(g,d));
        }
        exec.shutdown();
    }
    public static void test(IntGenerator g){
        test(g,10);
    }

}
