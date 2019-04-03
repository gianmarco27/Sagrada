package ingsw.TestServer.Utility;

import ingsw.Server.Utility.RegistrationLatch;
import org.junit.Assert;
import org.junit.Test;

public class RegistrationLatchTest {
    RegistrationLatch latch = new RegistrationLatch(9999);

    @Test
    public void testWaitSingle() {
        Thread addPlayers = new Thread(() -> {
            for (int i = 0; i < 1; i++) {
                latch.addSinglePlayer();
            }
        });
        addPlayers.start();

        try {
            latch.waitUntil(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(1, latch.getCount());
    }
    @Test
    public void testWaitMulti() {
        Thread addPlayers = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                latch.addPlayer();
            }
            latch.removePlayer();
            latch.addPlayer();
            latch.addPlayer();
        });
        addPlayers.start();

        try {
            latch.waitForStarting();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(4, latch.getCount());
    }

    @Test
    public void timeoutStart() {
        RegistrationLatch shortLatch = new RegistrationLatch(1);
        shortLatch.addPlayer();
        shortLatch.addPlayer();
        try {
            shortLatch.waitForStarting();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(2, shortLatch.getCount());
    }
}

