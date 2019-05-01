package org.antop.auctionsniper.xmpp;

import org.antop.auctionsniper.Auction;
import org.antop.auctionsniper.AuctionHouse;
import org.antop.auctionsniper.Item;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class XMPPAuctionHouse implements AuctionHouse {

    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    public static final String LOGGER_NAME = "auction-sniper";
    public static final String LOG_FILE_NAME = "auction-sniper.log";

    private final XMPPConnection connection;
    private final XMPPFailureReporter failureReporter;

    private XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
        this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
    }

    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = connection(hostname, username, password);
        return new XMPPAuctionHouse(connection);
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, auctionId(item, connection), failureReporter);
    }

    private String auctionId(Item item, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, item.getIdentifier(), connection.getServiceName());
    }

    public void disconnect() {
        connection.disconnect();
    }

    private Logger makeLogger() {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFileHandler());
        return logger;
    }

    private Handler simpleFileHandler() {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_NAME);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (IOException e) {
            throw new XMPPAuctionException("Could not create logger FileHandler " + LOG_FILE_NAME, e);
        }
    }
}
