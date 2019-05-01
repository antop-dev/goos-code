package org.antop.auctionsniper.unit.xmpp;

import org.antop.auctionsniper.Auction;
import org.antop.auctionsniper.AuctionEventListener;
import org.antop.auctionsniper.Item;
import org.antop.auctionsniper.end2end.ApplicationRunner;
import org.antop.auctionsniper.end2end.FakeAuctionServer;
import org.antop.auctionsniper.xmpp.XMPPAuctionHouse;
import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class XMPPAuctionHouseTest {
    private final FakeAuctionServer server = new FakeAuctionServer("item-54321");
    private XMPPAuctionHouse auctionHouse;

    @BeforeEach
    public void openConnection() throws XMPPException {
        auctionHouse = XMPPAuctionHouse.connect(FakeAuctionServer.XMPP_HOSTNAME, ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD);
    }

    @AfterEach
    public void closeConnection() {
        auctionHouse.disconnect();
    }

    @BeforeEach
    public void startAuction() throws XMPPException {
        server.startSellingItem();
    }

    @AfterEach
    public void stopAuction() {
        server.stop();
    }

    @Test
    void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);
        // mock
        AuctionEventListener auctionClosedListener = Mockito.mock(AuctionEventListener.class);
        Mockito.doAnswer(invocation -> {
            auctionWasClosed.countDown();
            return null;
        }).when(auctionClosedListener).auctionClosed();

        Auction auction = auctionHouse.auctionFor(new Item(server.getItemId(), 12345));
        auction.addAuctionEventListener(auctionClosedListener);
        auction.join();

        server.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        server.announceClosed();

        assertTrue(auctionWasClosed.await(20, TimeUnit.SECONDS));
    }

}
