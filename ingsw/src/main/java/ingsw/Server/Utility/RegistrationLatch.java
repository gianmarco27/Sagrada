package ingsw.Server.Utility;


import java.util.logging.Logger;

/**
 * Latch used to manage player reegistration, with reset if player number < 2 and instant end if 4 players are connected
 */
public class RegistrationLatch {
    private Timer timer;
    private int n;
    private boolean starting = false;
    private int waitTime;
    private Thread asyncWaitTimer;
    public RegistrationLatch(int timeoutSeconds) {
        this.waitTime = timeoutSeconds;
        this.n = 0;
        this.timer = new Timer();
        asyncWaitTimer = new Thread(new AsyncWaitTimer(this));
        asyncWaitTimer.start();
    }


    public synchronized void waitUntil(int target) throws InterruptedException {
        while(n != target) {
            wait();
        }
    }

    public synchronized void addSinglePlayer() {
        if(!starting){
            starting = true;
            n++;
            incrementChecks();
            notifyAll();
        }
    }

    private class AsyncWaitTimer implements Runnable {
        private final Logger log = Logger.getLogger( AsyncWaitTimer.class.getName() );

        private RegistrationLatch r;
        public AsyncWaitTimer(RegistrationLatch r) {
            this.r = r;
        }

        public void run() {
            synchronized (r.timer) {
                while(!r.starting){
                    try {
                        if(r.timer.timerFinished()){
                            synchronized (r){
                                r.starting = true;
                                r.notifyAll();
                                return;
                            }
                        }
                    } catch (InterruptedException e) {
                        log.warning("The thread that was waiting for the timer to expire had an exception : " + e.toString());
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public synchronized void removePlayer() {
        if(!starting){
            if(n>0){
                n--;
                decrementChecks();
                notifyAll();
            }
        }
    }


    public synchronized void addPlayer() {
        if(!starting){
            n++;
            incrementChecks();
            notifyAll();
        }
    }

    public synchronized void incrementChecks() {
        switch(n){
            case 2:
                timer.startTimer(waitTime);
                break;
            case 3:
                break;
            case 4:
                //Lobby full, starting
                starting = true;
                break;
            default:
                break;
        }
    }

    public synchronized void decrementChecks() {
        switch(n){
            case 0:
                break;
            case 1:
                timer.stopTimer();
                break;
            default:
                break;
        }
    }

    public synchronized void waitForStarting() throws InterruptedException {
        while(!starting){
            wait();
        }
    }

    public synchronized int getCount() {
        return n;
    }
}