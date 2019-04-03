package ingsw.TestServer.Utility;

import ingsw.Server.Utility.CountUpAndDownLatch;
import org.junit.Assert;
import org.junit.Test;

public class CountUpAndDownLatchTest {

    CountUpAndDownLatch latch = new CountUpAndDownLatch(0);

    @Test
    public void testIncrement() {
        latch.countUp();
        Assert.assertEquals(1, latch.getCount());
    }

    @Test
    public void testDecrement() {
        latch.countUp();
        latch.countUp();
        latch.countDownTillZero();
        Assert.assertEquals(1, latch.getCount());
        latch.countDownTillZero();
        latch.countDownTillZero();
        Assert.assertEquals(0, latch.getCount());
    }

    @Test
    public void testWait() {
        Thread wait = new Thread(() -> {
            try {
                latch.waitUntil(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        wait.start();

        Thread increment = new Thread(() -> {
            while(latch.getCount() < 2)
                latch.countUp();
        });
        increment.start();

        try {
            wait.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(2, latch.getCount());
    }

    @Test
    public void testCountDownOrWait() {
        Thread wait = new Thread(() -> {
            try {
                latch.countDownOrWaitIfZero();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        wait.start();

        Thread increment = new Thread(() -> {
            while(latch.getCount() < 2)
                latch.countUp();
        });
        increment.start();

        try {
            wait.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(1, latch.getCount());
    }
}
