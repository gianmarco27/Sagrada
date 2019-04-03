package ingsw.Server.Utility;

import java.util.logging.Logger;

/**
 * This Class implements a Timer
 */
public class Timer {
    private static final Logger log = Logger.getLogger( Timer.class.getName() );

    private boolean timeReached;
    private boolean running;
    private WaitTimer backgroundWait;
    public Timer() {
        timeReached = false;
        running = false;
    }

    private class WaitTimer implements Runnable {
        private final Logger log = Logger.getLogger( WaitTimer.class.getName() );

        private Timer t;
        private int sec;
        public WaitTimer(Timer t, int seconds) {
            this.t = t;
            this.sec = seconds*1000;
        }

        public void run() {
            synchronized (t) {
                try {
                    t.wait(sec);
                } catch (InterruptedException e) {
                    log.warning("The thread that was waiting for the timer to expire had an exception : " + e.toString());
                    Thread.currentThread().interrupt();
                }
                if (t.running) {
                    t.timeReached = true;
                    t.running = false;
                    t.notifyAll();
                } else {
                    return;
                }
            }
        }
    }


    public synchronized void startTimer(int seconds) {
        timeReached = false;
        running = true;
        backgroundWait = new WaitTimer(this,seconds);
        new Thread(backgroundWait).start();
    }

    public synchronized void stopTimer() {
        running = false;
        notifyAll();
    }



    public synchronized boolean timerFinished() throws InterruptedException {
            wait();
            return timeReached;
    }

}