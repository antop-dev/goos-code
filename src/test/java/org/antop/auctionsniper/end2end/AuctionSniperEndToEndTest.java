package org.antop.auctionsniper.end2end;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AuctionSniperEndToEndTest {

    @BeforeAll
    public static void init() {
        System.setProperty("com.objogate.wl.keyboard", "GB");
    }

    private final FakeAuctionServer auction1 = new FakeAuctionServer("item-54321");
    private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
    private final ApplicationRunner application = new ApplicationRunner();

    // https://www.baeldung.com/junit-before-beforeclass-beforeeach-beforeall
    // @After == @AfterEach
    // @AfterClass == @AfterAll
    @AfterEach
    public void stopAuction() {
        auction1.stop();
        auction2.stop();
    }

    @AfterEach
    public void stopApplication() {
        application.stop();
    }

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction1.startSellingItem();
        application.startBiddingIn(auction1);
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction1.announceClosed();
        application.hasShownSniperIsLostAuction(auction1, 0, 0);
    }

    @Test
    void sniperMakesAHigherBidButLoses() throws Exception {
        // 경매 오픈
        auction1.startSellingItem();
        // 스나이퍼가 경매에 참여
        application.startBiddingIn(auction1);
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(1000, 98, "other bidder");

        application.hasShownSniperIsBidding(auction1, 1000, 1098);
        auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction1.announceClosed();
        application.hasShownSniperIsLostAuction(auction1, 1000, 1098);
    }

    @Test
    void sniperWinsAnAuctionByBidingHigher() throws Exception {
        // [서버] 경매 시작
        auction1.startSellingItem();
        // [어플] 경매 차여
        application.startBiddingIn(auction1);
        // 스나이퍼가 옥션에 참여 했는가?
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        // 다른 입찰자가 1000원에 입찰을 했고 98원을 더 올려 입찰해야 한다고 알림
        // 현재 13장에서 스나이퍼는 바로 1000 + 98 입찰을 한다.
        auction1.reportPrice(1000, 98, "other bidder");
        // [어플] 상태가 Bidding 인지 확인
        application.hasShownSniperIsBidding(auction1, 1000, 1098);
        // [서버] 스나이퍼로부터 1098원을 입찰 받았는지 확인
        auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
        // [서버] 모두에게 1098원이 입찰가고 다음 입찰가는 +97원 해야하며 입찰자는 스나이퍼라고 알림
        auction1.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        // [어플] 상태가 낙찰 진행인지 확인
        application.hasShownSniperIsWinning(auction1, 1098);
        // [서버] 경매 종료
        auction1.announceClosed();
        // [어플] 낙찰 확인
        application.hasShownSniperIsWonAuction(auction1, 1098);
    }

    @Test
    void sniperBidsForMultipleTimes() throws Exception {
        auction1.startSellingItem();
        auction2.startSellingItem();

        application.startBiddingIn(auction1, auction2);
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(1000, 98, "other bidder");
        auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction2.reportPrice(500, 21, "other bidder");
        auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);

        application.hasShownSniperIsWinning(auction1, 1098);
        application.hasShownSniperIsWinning(auction2, 521);

        auction1.announceClosed();
        auction2.announceClosed();

        application.hasShownSniperIsWonAuction(auction1, 1098);
        application.hasShownSniperIsWonAuction(auction2, 521);
    }

    @Test
    void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception {
        auction1.startSellingItem();
        application.startBiddingWithStopPrice(auction1, 1100);
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction1.reportPrice(1000, 98, "other bidder"); // 1st bid other bidder

        application.hasShownSniperIsBidding(auction1, 1000, 1098); // 2nd bid by sniper

        auction1.reportPrice(1197, 10, "John"); // 3st bid by John
        application.hasShownSniperIsLosing(auction1, 1197, 1098);

        auction1.reportPrice(1207, 10, "Antop"); // 4st bid by Antop
        application.hasShownSniperIsLosing(auction1, 1207, 1098);

        auction1.announceClosed();
        application.hasShownSniperIsLostAuction(auction1, 1207, 1098);
    }

    @Test
    void sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() throws Exception {
        String brokenMessage = "a broken message";
        auction1.startSellingItem();
        auction2.startSellingItem();

        application.startBiddingIn(auction1, auction2);
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(500, 20, "other bidder");
        auction1.hasReceivedBid(520, ApplicationRunner.SNIPER_XMPP_ID);
        // 잘못된 메세지 전송
        auction1.sendInvalidMessageContaining(brokenMessage);
        // Failed 상태 확인
        application.hasShownSniperIsFailed(auction1);

        // 다른 입찰자가 해당 아이템을 입찰함.
        // ※ auction1 스나이퍼는 Failed 상태이기 때문에 더이상 진행을 하면 안된다.
        auction1.reportPrice(520, 21, "other bidder");
        // p249 뭔가 일어나지 않음을 테스트하기
        waitForAnotherAuctionEvent();
        application.reportsInvalidMessage(auction1, brokenMessage);
        // 여전히 auction1은 Failed 인가?
        application.hasShownSniperIsFailed(auction1);
    }

    private void waitForAnotherAuctionEvent() throws Exception {
        auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction2.reportPrice(600, 6, "other bidder");
        application.hasShownSniperIsBidding(auction2, 600, 606);
    }

}
