package org.antop.auctionsniper.unit;

import org.antop.auctionsniper.util.Defect;
import org.junit.jupiter.api.Test;

import org.antop.auctionsniper.ui.SniperState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SniperStateTests {

    @Test
    void isWonWhenAuctionClosesWhileWinning() {
        assertEquals(SniperState.LOST, SniperState.JOINING.whenAuctionClosed());
        assertEquals(SniperState.LOST, SniperState.BIDDING.whenAuctionClosed());
        assertEquals(SniperState.WON, SniperState.WINNING.whenAuctionClosed());
    }

    @Test
    void defectIfAuctionClosesWhenWon() {
        assertThrows(Defect.class, SniperState.WON::whenAuctionClosed);
    }

    @Test()
    void defectIfAuctionClosesWhenLost() {
        assertThrows(Defect.class, SniperState.LOST::whenAuctionClosed);
    }

}
