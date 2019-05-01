package org.antop.auctionsniper;

import org.antop.auctionsniper.util.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SniperPortfolio implements SniperCollector {

    private final Announcer<PortfolioListener> announcer = Announcer.to(PortfolioListener.class);
    // notToBeGCd
    private final List<AuctionSniper> snipers = new ArrayList<>();

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        announcer.announce().sniperAdded(sniper);
    }

    public void addPortfolioListener(PortfolioListener listener) {
        announcer.addListener(listener);
    }
}
