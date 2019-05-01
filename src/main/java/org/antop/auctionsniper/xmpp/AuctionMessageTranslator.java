package org.antop.auctionsniper.xmpp;

import org.antop.auctionsniper.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

import static org.antop.auctionsniper.AuctionEventListener.PriceSource;

public class AuctionMessageTranslator implements MessageListener {
    public static final String CLOSE_COMMAND_FORMAT = "SOLVersion: 1.1; Event: CLOSE;";
    public static final String PRICE_COMMAND_FORMAT = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;";

    private final String sniperId;
    private final AuctionEventListener listener;
    private final XMPPFailureReporter failureReporter;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener, XMPPFailureReporter failureReporter) {
        this.sniperId = sniperId;
        this.listener = listener;
        this.failureReporter = failureReporter;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        String messageBody = message.getBody();
        try {
            AuctionEvent event = AuctionEvent.from(messageBody);
            switch (event.type()) {
                case "CLOSE":
                    listener.auctionClosed();
                    break;
                case "PRICE":
                    listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            failureReporter.cannotTranslateMessage(sniperId, messageBody, e);
            listener.auctionFailed();
        }

    }

    static class AuctionEvent {
        private final Map<String, String> fields = new HashMap<>();

        String type() throws MissingValueException {
            return get("Event");
        }

        int currentPrice() throws MissingValueException {
            return getInt("CurrentPrice");
        }

        int increment() throws MissingValueException {
            return getInt("Increment");
        }

        PriceSource isFrom(String sniperId) throws MissingValueException {
            return sniperId.equals(bidder()) ? PriceSource.FROM_SNIPER : PriceSource.FROM_OTHER_BIDDER;
        }

        private String bidder() throws MissingValueException {
            return get("Bidder");
        }

        private String get(String fieldName) throws MissingValueException {
            String value = fields.get(fieldName);
            if (value == null) {
                throw new MissingValueException(fieldName);
            }
            return value;
        }

        private int getInt(String fieldName) throws MissingValueException {
            return Integer.parseInt(get(fieldName));
        }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            for (String field : fieldsIn(messageBody)) {
                event.addField(field);
            }
            return event;
        }

        private static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }

    }

    private static class MissingValueException extends Exception {
        public MissingValueException(String fieldName) {
            super("Missing value for " + fieldName);
        }
    }

}
