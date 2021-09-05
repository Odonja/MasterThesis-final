package org.anhu.statistics;

import java.text.DecimalFormat;

public class StopWatch {

    private long start;
    private long end;
    private long timeout;
    private boolean timeOutSet;

    public StopWatch() {
        this.start = 0;
        this.end = 0;
        timeOutSet = false;
    }

    public void start() {
        start = System.nanoTime();
    }

    public void startWithTimeout(long maxRuntime) {
        start = System.nanoTime();
        timeout = start + maxRuntime;
        timeOutSet = true;
      //  System.out.println("start with timeout. start: " + start + ", timeout: " + timeout);
    }

    public void stop() {
        end = System.nanoTime();
    }

    public boolean timerExpired(){
        final long currentTime = System.nanoTime();
      //  System.out.println("timer exprired? " + timeOutSet + "+" + (currentTime > timeout));
        return timeOutSet && currentTime > timeout;
    }

   public long elapsed() {
        return (end - start);
    }

    public String toString() {
        long enlapsed = elapsed();
        double nanosecondsInASecond = 1000000000.0;
        int hours = (int) (enlapsed / (60*60*nanosecondsInASecond));
        int minutes = (int) ((enlapsed / (60*nanosecondsInASecond)) % 60);
        DecimalFormat df = new DecimalFormat("#.####");
        double seconds = ((double) enlapsed / nanosecondsInASecond) % 60;
        if(hours > 0) {
            return hours + " Hours " + minutes + " Minutes " + df.format(seconds) + " Seconds";
        }else if(minutes > 0){
            return minutes + " Minutes " + df.format(seconds) + " Seconds";
        }else{
            return df.format(seconds) + " Seconds";
        }

    }

}