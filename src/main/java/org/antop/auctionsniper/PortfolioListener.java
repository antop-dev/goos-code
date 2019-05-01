package org.antop.auctionsniper;

import java.util.EventListener;

public interface PortfolioListener extends EventListener {

    void sniperAdded(AuctionSniper sniper);

}
