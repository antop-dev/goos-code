package org.antop.auctionsniper.end2end;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.*;
import com.objogate.wl.swing.gesture.GesturePerformer;
import org.antop.auctionsniper.ui.MainWindow;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import java.text.NumberFormat;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;

@SuppressWarnings("unchecked")
public class AuctionSniperDriver extends JFrameDriver {

    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), showingOnScreen()),
                new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        JTableDriver table = new JTableDriver(this, named(MainWindow.SNIPERS_TABLE_NAME));
        table.hasRow(matching(
                withLabelText(itemId),
                withLabelText(NumberFormat.getInstance().format(lastPrice)),
                withLabelText(NumberFormat.getInstance().format(lastBid)),
                withLabelText(statusText)
        ));
    }

    public void hasColumnTitle() {
        JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
        headers.hasHeaders(matching(
                withLabelText("Item"),
                withLabelText("Last Price"),
                withLabelText("Last Bid"),
                withLabelText("State")
        ));
    }

    public void startBiddingFor(String itemId) {
        startBiddingFor(itemId, Integer.MAX_VALUE);
    }

    public void startBiddingFor(String itemId, int stopPrice) {
        // Ubuntu Desktop에서 테스트 결과 driver.replaceAllText()를 사용하면 맨 처음 한글자가 입력이 안되는 버그(?)가 있다.
        // Travis CI + Xvfb 환경에서도 같은 현상이 나타난다.
        // itemIdField().replaceAllText(itemId);
        enterText(textField(MainWindow.NEW_ITEM_ID_NAME), itemId);
        enterText(textField(MainWindow.NEW_ITEM_STOP_PRICE_NAME), String.valueOf(stopPrice));
        bidButton().click();
    }

    private void enterText(JTextFieldDriver driver, String text) {
        driver.component().component().setText(text);
    }

    private JTextFieldDriver textField(String name) {
        JTextFieldDriver textField = new JTextFieldDriver(this, JTextField.class, named(name));
        textField.focusWithMouse();
        return textField;
    }

    private JButtonDriver bidButton() {
        return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
    }

}