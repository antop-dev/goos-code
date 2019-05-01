package org.antop.auctionsniper.xmpp;

import org.antop.auctionsniper.Auction;
import org.antop.auctionsniper.AuctionEventListener;
import org.antop.auctionsniper.util.Announcer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private final Chat chat;
    private final XMPPFailureReporter failureReporter;

    public XMPPAuction(XMPPConnection connection, String itemId, XMPPFailureReporter failureReporter) {
        this.failureReporter = failureReporter;
        AuctionMessageTranslator translator = translatorFor(connection);
        chat = connection.getChatManager().createChat(itemId, translator);
        addAuctionEventListener(chatDisconnectorFor(translator));
    }

    private AuctionEventListener chatDisconnectorFor(AuctionMessageTranslator translator) {
        return new AuctionEventListener() {
            @Override
            public void auctionFailed() {
                chat.removeMessageListener(translator);
            }

            @Override
            public void auctionClosed() {
                // do nothing
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {
                // do nothing
            }
        };
    }

    private AuctionMessageTranslator translatorFor(XMPPConnection connection) {
        return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce(), failureReporter);
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(BID_COMMAND_FORMAT, amount));
    }

    @Override
    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        auctionEventListeners.addListener(listener);
    }

    private void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
