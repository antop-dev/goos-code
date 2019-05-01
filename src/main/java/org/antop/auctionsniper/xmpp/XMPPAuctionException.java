package org.antop.auctionsniper.xmpp;

public class XMPPAuctionException extends RuntimeException {

    public XMPPAuctionException(String message, Exception e) {
        super(message, e);
    }

}
