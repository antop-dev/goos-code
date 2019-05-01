package org.antop.auctionsniper.unit;

import com.jparams.verifier.tostring.ToStringVerifier;
import org.antop.auctionsniper.ui.SniperSnapshot;
import org.antop.auctionsniper.ui.SniperState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SniperSnapshotTest {

    @Test
    void transitionsBetweenStates() {
        final String itemId = "item id";
        SniperSnapshot joining = SniperSnapshot.joining(itemId);

        assertEquals(new SniperSnapshot(itemId, 0, 0, SniperState.JOINING), joining);

        SniperSnapshot bidding = joining.bidding(123, 234);
        assertEquals(new SniperSnapshot(itemId, 123, 234, SniperState.BIDDING), bidding);

        assertEquals(new SniperSnapshot(itemId, 456, 234, SniperState.WINNING), bidding.winning(456));
        assertEquals(new SniperSnapshot(itemId, 123, 234, SniperState.LOST), bidding.closed());
        assertEquals(new SniperSnapshot(itemId, 678, 234, SniperState.WON), bidding.winning(678).closed());
    }

    @Test
    void comparesItemIdentities() {
        assertTrue(SniperSnapshot.joining("item 1").isForSameItemAs(SniperSnapshot.joining("item 1")));
        assertFalse(SniperSnapshot.joining("item 1").isForSameItemAs(SniperSnapshot.joining("item 2")));
    }

    @Test
    void haveToString() {
        ToStringVerifier.forClass(SniperSnapshot.class).verify();
    }

}
