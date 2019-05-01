package org.antop.auctionsniper;

public interface Auction {

	void bid(int amount);

	void join();

    void addAuctionEventListener(AuctionEventListener auctionSniper);

}
