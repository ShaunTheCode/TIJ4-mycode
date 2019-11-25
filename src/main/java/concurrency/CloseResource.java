package concurrency;

import java.io.EOFException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.mindview.util.Print.print;

public class CloseResource {
    public static void main(String[] args) throws Exception {
        ExecutorService exec= Executors.newCachedThreadPool();
        ServerSocket server=new ServerSocket(8080);
        InputStream inputStream=new Socket("localhost",8080).getInputStream();
        exec.execute(new IOBlocker(inputStream));
        exec.execute(new IOBlocker(System.in));
        TimeUnit.MILLISECONDS.sleep(100);
        print("Shutting down all threads");
        exec.shutdownNow();
        TimeUnit.SECONDS.sleep(1);
        print("Closing "+inputStream.getClass().getName());
        inputStream.close();
        TimeUnit.SECONDS.sleep(1);
        print("Closing "+System.in.getClass().getName());
        System.in.close();
    }
}
