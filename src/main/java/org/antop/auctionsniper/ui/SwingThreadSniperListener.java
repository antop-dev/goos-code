package org.antop.auctionsniper.ui;

import org.antop.auctionsniper.SniperListener;

public class SwingThreadSniperListener implements SniperListener {
    private final SnipersTableModel snipers;

    public SwingThreadSniperListener(SnipersTableModel snipers) {
        this.snipers = snipers;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot snapshot) {
        snipers.sniperStateChanged(snapshot);
    }
}
