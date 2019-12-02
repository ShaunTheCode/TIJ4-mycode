package concurrency;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static net.mindview.util.Print.print;

public class GreenhouseScheduler {
    private volatile boolean light=false;
    private volatile boolean water=false;
    private String thermostat="Day";
    public synchronized String getThermostat(){
        return thermostat;
    }
    public synchronized void setThermostat(String t){
        thermostat=t;
    }
    ScheduledThreadPoolExecutor scheduler=new ScheduledThreadPoolExecutor(10);
    public void schedule(Runnable event,long delay){
        scheduler.schedule(event,delay, TimeUnit.MILLISECONDS);
    }
    public void repeat(Runnable event,long initialDelay,long period){
        scheduler.scheduleAtFixedRate(event,initialDelay,period,TimeUnit.MILLISECONDS);
    }
    class LightOn implements Runnable{
        @Override
        public void run() {
            System.out.println("Turning on lights");
            light=true;
        }
    }
    class LightOff implements Runnable{
        @Override
        public void run() {
            System.out.println("Turning off lights");
            light=false;
        }
    }
    class WaterOn implements Runnable{
        @Override
        public void run() {
            System.out.println("Turning greenhouse water on");
            light=true;
        }
    }
    class WaterOff implements Runnable{
        @Override
        public void run() {
            System.out.println("Turning greenhouse water off");
            light=false;
        }
    }

    class ThermostatNight implements Runnable{
        @Override
        public void run() {
            System.out.println("Thermostat to night setting");
            setThermostat("night");
        }
    }

    class ThermostatDay implements Runnable{
        @Override
        public void run() {
            System.out.println("Thermostat to day setting");
            setThermostat("day");
        }
    }
    class Bell implements Runnable{
        @Override
        public void run() {
            System.out.println("Bing!");
        }
    }
    class Terminate implements Runnable{
        @Override
        public void run() {
            System.out.println("Terminating");
            scheduler.shutdownNow();
            new Thread(){
                @Override
                public void run() {
                    for(DataPoint d:data){
                        System.out.println(d);
                    }
                }
            }.start();
        }
    }
    static class DataPoint{
        final Calendar time;
        final float temperature;
        final float humidity;
        public DataPoint(Calendar d,float temp,float hum){
            time=d;
            temperature=temp;
            humidity=hum;
        }

        @Override
        public String toString() {
            return time.getTime()+String.format(" temperature: %1$.1f humidity:  %2$.2f",temperature,humidity);
        }

    }
    private Calendar lastTime=Calendar.getInstance();
    {
        lastTime.set(Calendar.MINUTE,30);
        lastTime.set(Calendar.SECOND,00);
    }
    private float lastTemp=65.0f;
    private int temDirection=+1;
    private float lastHumidity=50.0f;
    private int humidityDirection=+1;
    private Random rand=new Random(47);
    List<DataPoint> data= Collections.synchronizedList(new ArrayList<>());
    class CollectData implements Runnable{
        @Override
        public void run() {
            print("Collecting data");
            synchronized (GreenhouseScheduler.this){
                lastTime.set(Calendar.MINUTE,lastTime.get(Calendar.MINUTE)+30);
                if(rand.nextInt(5)%4==0){
                    temDirection=-temDirection;
                }
                lastTemp=lastTemp+temDirection*(1.0f+rand.nextFloat());
                if(rand.nextInt(5)%4==0){
                    humidityDirection=-humidityDirection;
                }
                lastHumidity=lastHumidity+humidityDirection*rand.nextFloat();
                data.add(new DataPoint((Calendar)lastTime.clone(),lastTemp,lastHumidity));
            }
        }
    }

    public static void main(String[] args) {
        GreenhouseScheduler gs=new GreenhouseScheduler();
        gs.schedule(gs.new Terminate(),5000);
        gs.repeat(gs.new Bell(),0,1000);
        gs.repeat(gs.new ThermostatNight(),0,2000);
        gs.repeat(gs.new LightOn(),0,200);
        gs.repeat(gs.new LightOff(),0,400);
        gs.repeat(gs.new WaterOn(),0,600);
        gs.repeat(gs.new WaterOff(),0,800);
        gs.schedule(gs.new ThermostatDay(),5000);
        gs.schedule(gs.new CollectData(),5000);
    }



}
