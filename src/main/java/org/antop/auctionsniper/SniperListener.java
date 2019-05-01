package org.antop.auctionsniper;

import org.antop.auctionsniper.ui.SniperSnapshot;

import java.util.EventListener;

public interface SniperListener extends EventListener {

    void sniperStateChanged(SniperSnapshot snapshot);

}
