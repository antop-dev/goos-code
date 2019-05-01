package org.antop.auctionsniper.unit;

import org.antop.auctionsniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;

import static org.mockito.Mockito.*;

public class SniperLauncherTest {
    private final Auction auction = Mockito.mock(Auction.class);
    private final AuctionHouse auctionHouse = Mockito.mock(AuctionHouse.class);
    private final SniperCollector collector = Mockito.mock(SniperCollector.class);
    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, collector);

    /**
     * 다른 모든 것이 준비된 후에 경매에 참여하는지 검증한다.
     */
    @Test
    void addNewSniperToCollectorAndThenJoinsAuction() {
        // Stub: auctionHouse.auctionFor(String)를 실행하면 auction mock을 리턴하게 한다.
        when(auctionHouse.auctionFor(any(Item.class))).thenReturn(auction);
        // action
        final Item item = new Item("item 111", 12345);
        launcher.joinAuction(item);

        InOrder inOrder = Mockito.inOrder(auction, collector);
        // order verify #1
        // Auction.addAuctionEventListener(sniper:AuctionSniper) 실행 시
        // sniper 인스턴스의 snapshot.itemId가 일치하는 지 검증
        inOrder.verify(auction).addAuctionEventListener(MockitoHamcrest.argThat(sniperForItem(item)));
        // order verify #2
        // SniperCollector.addSniper(sniper:AuctionSniper) 실행 시
        // sniper 인스턴스의 snapshot.itemId가 일치하는 지 검증
        inOrder.verify(collector).addSniper(MockitoHamcrest.argThat(sniperForItem(item)));
        // order verify #3
        // Auction.join() 메서드를 실행 했는지 검증
        inOrder.verify(auction).join();
        // 이 후에 더 이상 다른 액션이 일어나지 않았는지 검증
        verifyZeroInteractions(auction, collector);
        // TODO: auction.join()이 수행 되기 전에 위 두개의 메서드 콜이 순서 상관 없이 일어 났는지 검증
    }

    protected Matcher<AuctionSniper> sniperForItem(Item item) {
        return new FeatureMatcher<AuctionSniper, String>(Matchers.equalTo(item.getIdentifier()), "sniper with item id", "itemId") {
            @Override
            protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().getItemId();
            }
        };
    }

}
