package org.antop.auctionsniper;

import java.util.EventListener;

public interface AuctionEventListener extends EventListener {

    void auctionFailed();

	void auctionClosed();

	void currentPrice(int price, int increment, PriceSource priceSource);

	enum PriceSource {
		FROM_SNIPER, FROM_OTHER_BIDDER
	}
}
