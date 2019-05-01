package org.antop.auctionsniper.unit.ui;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.antop.auctionsniper.Item;
import org.antop.auctionsniper.SniperPortfolio;
import org.antop.auctionsniper.end2end.AuctionSniperDriver;
import org.antop.auctionsniper.ui.MainWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {

    @BeforeAll
    public static void init() {
        System.setProperty("com.objogate.wl.keyboard", "GB");
    }

    private final SniperPortfolio portfolio = new SniperPortfolio();
    private final MainWindow mainWindow = new MainWindow(portfolio);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(5000);

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<>(equalTo(new Item("an item-id", 789)), "join request");
        mainWindow.addUserRequestListener(itemProbe::setReceivedValue);

        driver.startBiddingFor("an item-id", 789);
        driver.check(itemProbe);
    }

    @AfterEach
    public void stopApplication() {
        mainWindow.setVisible(false);
        mainWindow.dispose();
    }
}
