package concurrency;

import java.util.List;
import java.util.concurrent.*;

import net.mindview.util.BasicGenerator;
import net.mindview.util.Generator;

class ExchangerProduce<T> implements Runnable {
    private Generator<T> generator;
    private Exchanger<List<T>> exchanger;
    private List<T> holder;

    public ExchangerProduce(Generator<T> generator, Exchanger<List<T>> exchanger, List<T> holder) {
        this.generator = generator;
        this.exchanger = exchanger;
        this.holder = holder;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                for (int i = 0; i < ExchangerDemo.size; i++) {
                    holder.add(generator.next());
                }
                System.out.println("Before ExchangerProduce.holder exchange()");
                holder = exchanger.exchange(holder);
                System.out.println("After ExchangerProduce.holder exchange()");

            }
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }
}

class ExchangerConsumer<T> implements Runnable {
    private Exchanger<List<T>> exchanger;
    private List<T> holder;
    private volatile T value;

    public ExchangerConsumer(Exchanger<List<T>> exchanger, List<T> holder) {
        this.exchanger = exchanger;
        this.holder = holder;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("Before ExchangerConsumer.holder exchange()");
                holder = exchanger.exchange(holder);
                System.out.println("After ExchangerConsumer.holder exchange()");

                for (T x : holder) {
                    value = x;
                    holder.remove(x);
                }
            }
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        System.out.println("Final value: " + value);
    }
}

public class ExchangerDemo {
    static int size = 10;
    static int delay = 5;

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            size = new Integer(args[0]);
        }
        if (args.length > 1) {
            delay = new Integer(args[1]);
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Exchanger<List<Fat>> xc = new Exchanger<>();
        List<Fat> producerList = new CopyOnWriteArrayList<>(),
                cosumerList = new CopyOnWriteArrayList<>();
        exec.execute(new ExchangerProduce<Fat>(BasicGenerator.create(Fat.class), xc, producerList));
        exec.execute(new ExchangerConsumer<Fat>(xc, cosumerList));
        TimeUnit.SECONDS.sleep(delay);
        exec.shutdownNow();
    }
}
