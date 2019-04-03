package ingsw.Server.Utility;

import java.util.concurrent.CountDownLatch;

/**
 * Implementation of a CountDown latch, used to keep track of players acks and choices
 */
public class CountUpAndDownLatch {
    private int n;

    public CountUpAndDownLatch(int count) {
        this.n = count;
    }

    public synchronized void countDownOrWaitIfZero() throws InterruptedException {
        while(n == 0) {
            wait();
        }
        n--;
        notifyAll();
    }

    public synchronized void countDownTillZero(){
        if(n>0){
            n--;
            notifyAll();
        }
    }


    public synchronized void waitUntil(int target) throws InterruptedException {
        while(n != target) {
            wait();
        }
    }

    public synchronized void countUp() { //should probably check for Integer.MAX_VALUE
            n++;
            notifyAll();
    }

    public synchronized int getCount() {
            return n;
    }
}