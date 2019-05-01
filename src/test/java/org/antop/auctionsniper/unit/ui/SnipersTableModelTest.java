package org.antop.auctionsniper.unit.ui;

import org.antop.auctionsniper.Auction;
import org.antop.auctionsniper.AuctionSniper;
import org.antop.auctionsniper.Item;
import org.antop.auctionsniper.ui.Column;
import org.antop.auctionsniper.ui.SniperSnapshot;
import org.antop.auctionsniper.ui.SnipersTableModel;
import org.antop.auctionsniper.util.Defect;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.antop.auctionsniper.ui.SniperState;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SnipersTableModelTest {
    private final TableModelListener listener = mock(TableModelListener.class);
    private final Auction auction = Mockito.mock(Auction.class);

    private final AuctionSniper sniper1 = new AuctionSniper(new Item("item 0", 1234), auction);
    private final AuctionSniper sniper2 = new AuctionSniper(new Item("item 1", 5678), auction);
    private final SnipersTableModel model = new SnipersTableModel();

    @BeforeEach
    void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    void hasEnoughColumns() {
        assertThat(model.getColumnCount(), CoreMatchers.equalTo(Column.values().length));
    }

    @Test
    void setsSniperValuesInColumns() {
        model.sniperAdded(sniper1);
        // verify #1
        assertRowMatchesSnapshot(0, sniper1.getSnapshot());
        // verify #2
        verify(listener).tableChanged(refEq(new TableModelEvent(model, 0, 0, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT)));
        // update to bidding
        SniperSnapshot bidding = sniper1.getSnapshot().bidding(555, 666);
        model.sniperStateChanged(bidding);
        // verify #3
        assertRowMatchesSnapshot(0, bidding);
        // verify #4
        verify(listener).tableChanged(refEq(new TableModelEvent(model, 0, 0, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE)));
    }

    @Test
    void notifiesListenersWhenAddingASniper() {
        assertEquals(0, model.getRowCount());

        model.sniperAdded(sniper1);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, sniper1.getSnapshot());
    }

    @Test
    void notifiesListenersWhenAddingMultiSniper() {
        assertEquals(0, model.getRowCount());

        model.sniperAdded(sniper1);
        model.sniperAdded(sniper2);

        assertEquals(2, model.getRowCount());
        assertRowMatchesSnapshot(0, sniper1.getSnapshot());
        assertRowMatchesSnapshot(1, sniper2.getSnapshot());
    }

    @Test
    void holdsSnipersInAdditionOrder() {
        model.sniperAdded(sniper1);
        model.sniperAdded(sniper2);

        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    @Test
    public void updatesCorrectRow() {
        model.sniperAdded(sniper1);
        model.sniperAdded(sniper2);

        // change
        SniperSnapshot snapshot = sniper2.getSnapshot();
        snapshot = snapshot.bidding(100, 105);
        model.sniperStateChanged(snapshot);
        // verify #1
        assertEquals(100, cellValue(1, Column.LAST_PRICE));
        assertEquals(105, cellValue(1, Column.LAST_BID));
        // verify #2
        ArgumentCaptor<TableModelEvent> argument = ArgumentCaptor.forClass(TableModelEvent.class);
        verify(listener, times(3)).tableChanged(argument.capture()); // times: INSERT → INSERT → UPDATE
        assertEquals(TableModelEvent.UPDATE, argument.getValue().getType());
    }

    @Test
    public void throwsDefectIfNoExistingSniperForAnUpdate() {
        assertThrows(Defect.class, () -> model.sniperStateChanged(new SniperSnapshot("item 1", 123, 234, SniperState.WINNING)));
    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertEquals(snapshot.getItemId(), cellValue(row, Column.ITEM_IDENTIFIER));
        assertEquals(snapshot.getLastPrice(), cellValue(row, Column.LAST_PRICE));
        assertEquals(snapshot.getLastBid(), cellValue(row, Column.LAST_BID));
        assertEquals(SnipersTableModel.textFor(snapshot.getState()), cellValue(row, Column.SNIPER_STATE));
    }

    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }

}