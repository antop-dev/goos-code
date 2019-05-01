package org.antop.auctionsniper.unit.ui;

import org.antop.auctionsniper.ui.Column;
import org.antop.auctionsniper.ui.SniperSnapshot;
import org.junit.jupiter.api.Test;

import org.antop.auctionsniper.ui.SniperState;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnTest {

    @Test
    void retrievesValuesFromASniperSnapshot() {
        SniperSnapshot snapshot = new SniperSnapshot("item", 123, 34, SniperState.BIDDING);
        assertEquals("item", Column.ITEM_IDENTIFIER.valueIn(snapshot));
        assertEquals(123, Column.LAST_PRICE.valueIn(snapshot));
        assertEquals(34, Column.LAST_BID.valueIn(snapshot));
        assertEquals("Bidding", Column.SNIPER_STATE.valueIn(snapshot));
    }

}
