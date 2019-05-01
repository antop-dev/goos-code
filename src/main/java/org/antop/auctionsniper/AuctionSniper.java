package org.antop.auctionsniper;

import org.antop.auctionsniper.ui.SniperSnapshot;
import org.antop.auctionsniper.ui.SniperState;
import org.antop.auctionsniper.util.Announcer;

public class AuctionSniper implements AuctionEventListener {

    private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
    private final Item item;
    private final Auction auction;
    private SniperSnapshot snapshot;

    public AuctionSniper(Item item, Auction auction) {
        this.item = item;
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(item.getIdentifier());
    }

    @Override
    public void auctionFailed() {
        snapshot = snapshot.failed();
        notifyChange();
    }

    @Override
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        if (priceSource == PriceSource.FROM_SNIPER) {
            snapshot = snapshot.winning(price);
        } else {
            int bid = price + increment;
            if (item.allowsBid(bid)) {
                snapshot = snapshot.bidding(price, bid);
            } else {
                snapshot = snapshot.losing(price);
            }
        }

        notifyChange();

        if (snapshot.isState(SniperState.BIDDING)) {
            auction.bid(price + increment);
        }
    }

    private void notifyChange() {
        listeners.announce().sniperStateChanged(snapshot);
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    public void addSniperListener(SniperListener listener) {
        listeners.addListener(listener);
    }
}
