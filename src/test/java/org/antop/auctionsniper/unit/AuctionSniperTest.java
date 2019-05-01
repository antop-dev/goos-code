package org.antop.auctionsniper.unit;

import org.antop.auctionsniper.Auction;
import org.antop.auctionsniper.AuctionSniper;
import org.antop.auctionsniper.Item;
import org.antop.auctionsniper.SniperListener;
import org.antop.auctionsniper.ui.SniperSnapshot;
import org.antop.auctionsniper.ui.SniperState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.antop.auctionsniper.AuctionEventListener.PriceSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuctionSniperTest {

    private final String ITEM_ID = "test-item";
    private final Auction auction = Mockito.mock(Auction.class);
    private final SniperListener sniperListener = Mockito.mock(SniperListener.class);
    private final Item item = new Item(ITEM_ID, 789);
    private final AuctionSniper sniper = new AuctionSniper(item, auction);
    private final ArgumentCaptor<SniperSnapshot> argument = ArgumentCaptor.forClass(SniperSnapshot.class);

    @BeforeEach
    void attachSniperListener() {
        sniper.addSniperListener(sniperListener);
    }

    @Test
    void reportsLostWhenAuctionClosesImmediately() {
        sniper.auctionClosed();
        verify(sniperListener).sniperStateChanged(argument.capture());
        assertEquals(SniperState.LOST, argument.getValue().getState());
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 500;
        final int increment = 25;
        final int bid = price + increment;
        // action #1
        sniper.currentPrice(price, increment, PriceSource.FROM_OTHER_BIDDER);

        verify(auction).bid(price + increment);
        verify(sniperListener).sniperStateChanged(refEq(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING)));
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(135, 45, PriceSource.FROM_SNIPER);

        verify(sniperListener, times(2)).sniperStateChanged(argument.capture());
        assertEquals(SniperState.WINNING, argument.getValue().getState());
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        // 다른 입찰자가 입찰
        sniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
        verify(sniperListener).sniperStateChanged(refEq(new SniperSnapshot(ITEM_ID, 123, 123 + 45, SniperState.BIDDING)));
        // 경매 종료
        sniper.auctionClosed();
        // verify
        verify(sniperListener, atLeastOnce()).sniperStateChanged(argument.capture());
        assertEquals(SniperState.LOST, argument.getValue().getState());
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        // 스나이퍼가 입찰
        sniper.currentPrice(123, 45, PriceSource.FROM_SNIPER);
        // verify: [낙찰중] 상태 확인
        verify(sniperListener).sniperStateChanged(argument.capture());
        assertEquals(SniperState.WINNING, argument.getValue().getState());
        // 경매 종료
        sniper.auctionClosed();
        // 낙찰
        verify(sniperListener, atLeastOnce()).sniperStateChanged(argument.capture());
        assertEquals(SniperState.WON, argument.getValue().getState());
    }

    @Test
    void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        allowingSniperBidding();

        sniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(2345, 25, PriceSource.FROM_OTHER_BIDDER);
        // verify: Losing
        verify(sniperListener, times(3)).sniperStateChanged(argument.capture());
        assertEquals(SniperState.LOSING, argument.getValue().getState());
    }

    /**
     * 처음 금액이 상한가보다 클 경우 입찰을 하지 않고 Losing 상태가 된다.
     */
    @Test
    void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
        sniper.currentPrice(890, 89, PriceSource.FROM_OTHER_BIDDER);
        // verify #1: does not bid
        verify(auction, never()).bid(anyInt());
        // verify #2: reports losing
        verify(sniperListener).sniperStateChanged(argument.capture());
        assertEquals(SniperState.LOSING, argument.getValue().getState());
    }

    /**
     * Losing 상태에서 경매가 종료되면 Lost 상태가 된다.
     */
    @Test
    void reportsLostIfAuctionClosesWhenLosing() {
        sniper.currentPrice(890, 89, PriceSource.FROM_OTHER_BIDDER);
        sniper.auctionClosed();

        // verify #1
        // times 1 = LOSING
        // times 2 = LOST
        verify(sniperListener, times(2)).sniperStateChanged(argument.capture());
        // verify #2: reports lost
        assertEquals(SniperState.LOST, argument.getValue().getState());
    }

    /**
     * 지정 가격에 도달하여 Losing 상태가 될 때까지 계속 입찰한다.
     */
    @Test
    void continuesToBeLosingOnceStopPriceHasBeenReached() {
        allowingSniperBidding();
        // 다른 입찰자들이 행한 입찰
        sniper.currentPrice(400, 40, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(490, 49, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(600, 60, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(730, 70, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(890, 89, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(1000, 100, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(1300, 130, PriceSource.FROM_OTHER_BIDDER);
        // verify #1: 스나이퍼는 3번 입찰을 했다
        // 440  → 539 → 660 → 다음은 803 입찰을 해야하지만 할 수 없다 (지정가격:789)
        verify(auction, times(3)).bid(anyInt());
        // verify #2: reports losing
        verify(sniperListener, atLeastOnce()).sniperStateChanged(argument.capture());
        assertEquals(SniperState.LOSING, argument.getValue().getState());
    }

    /**
     * Winning 상태에서 지정 가격보다 높은 입찰이 나왔을 때 Losing 상태가 되고 입찰하지 않는다.
     */
    @Test
    void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        allowingSniperBidding();

        // action #1: 다른 입찰자가 500원에 입찰
        sniper.currentPrice(500, 50, PriceSource.FROM_OTHER_BIDDER);
        // → 스나이퍼가 550원에 입찰함

        // action #2: 다른 입찰자가 680원에 입찰
        sniper.currentPrice(680, 68, PriceSource.FROM_OTHER_BIDDER);
        // → 스나이퍼가 748원에 입찰함

        // verify #1: reports winning
        verify(sniperListener, times(4)).sniperStateChanged(argument.capture());
        assertEquals(SniperState.WINNING, argument.getValue().getState());

        // action #3: 다른 입찰자들이 입찰 진행
        sniper.currentPrice(850, 85, PriceSource.FROM_OTHER_BIDDER);
        // → 스나이퍼는 입찰 안함

        // action #4: 다른 입찰자들이 입찰 진행
        sniper.currentPrice(950, 95, PriceSource.FROM_OTHER_BIDDER);
        // → 스나이퍼는 입찰 안함

        // verify #2: 스나이퍼가 입찰을 두번 했다.
        verify(auction, times(2)).bid(anyInt());
        // verify #1: reports losing
        verify(sniperListener, times(6)).sniperStateChanged(argument.capture());
        assertEquals(SniperState.LOSING, argument.getValue().getState());
    }

    /**
     * auction.bid(bid:int) 하면 입찰을 수행 한 것 처럼 만든다.
     */
    private void allowingSniperBidding() {
        // auction.bid(bid:int) → sniper.currentPrice()
        doAnswer(invocation -> {
            int price = invocation.getArgument(0);
            // 다음 상한가는 서버에서 만들어지는 값이다.
            // 임의로 10%로 한다.
            int bid = (int) (price * 0.1);
            sniper.currentPrice(price, bid, PriceSource.FROM_SNIPER);
            return null;
        }).when(auction).bid(anyInt());
    }

    @Test
    void reportsFailedIfAuctionFailsWhenBidding() {
        sniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
        sniper.auctionFailed();

        verify(sniperListener).sniperStateChanged(refEq(new SniperSnapshot(ITEM_ID, -1, -1, SniperState.FAILED)));
    }
}
