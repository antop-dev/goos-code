package org.antop.auctionsniper.unit.xmpp;

import org.antop.auctionsniper.AuctionEventListener;
import org.antop.auctionsniper.end2end.ApplicationRunner;
import org.antop.auctionsniper.xmpp.AuctionMessageTranslator;
import org.antop.auctionsniper.xmpp.XMPPAuction;
import org.antop.auctionsniper.xmpp.XMPPFailureReporter;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.antop.auctionsniper.AuctionEventListener.PriceSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AuctionMessageTranslatorTest {
    public static final Chat UNUSED_CHAT = null;

    private final AuctionEventListener listener = Mockito.mock(AuctionEventListener.class);
    private final XMPPFailureReporter failureReporter = Mockito.mock(XMPPFailureReporter.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(ApplicationRunner.SNIPER_ID, listener, failureReporter);

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {
        Message message = new Message();
        message.setBody(AuctionMessageTranslator.CLOSE_COMMAND_FORMAT);

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).auctionClosed();
    }

    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        Message message = new Message();

        message.setBody(String.format(AuctionMessageTranslator.PRICE_COMMAND_FORMAT, 192, 7, "Someone else"));
        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).currentPrice(192, 7, PriceSource.FROM_OTHER_BIDDER);
    }

    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        Message message = new Message();

        message.setBody(String.format(AuctionMessageTranslator.PRICE_COMMAND_FORMAT, 234, 5, ApplicationRunner.SNIPER_ID));
        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).currentPrice(234, 5, PriceSource.FROM_SNIPER);
    }

    @Test
    void notifiesAuctionFailedWhenBadMessageReceived() {
        String badMessage = "a bad message";
        Message message = message(badMessage);
        // action
        translator.processMessage(UNUSED_CHAT, message);
        // verify #1
        verify(listener).auctionFailed();
        // verify #2
        verify(failureReporter).cannotTranslateMessage(eq(ApplicationRunner.SNIPER_ID), eq(badMessage), any(Exception.class));
    }

    private Message message(String body) {
        Message message = new Message();
        message.setBody(body);
        return message;
    }


    @Test
    void notifiesAuctionFailedWhenEventTypeMissing() {
        // SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;
        // Event 빠짐
        Message message = message("SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + ApplicationRunner.SNIPER_ID + ";");
        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).auctionFailed();
    }

    @Test
    void notifiesAuctionFailedWhenCurrentPriceMissing() {
        // SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;
        // CurrentPrice 빠짐
        Message message = new Message("SOLVersion: 1.1; Event: PRICE; Increment: 5; Bidder: " + ApplicationRunner.SNIPER_ID + ";");
        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).auctionFailed();
    }

}
