package org.antop.auctionsniper.unit.ui.cell;

import org.antop.auctionsniper.ui.cell.DecimalCellRenderer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.text.NumberFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DecimalCellRendererTest {

    private final JTable table = Mockito.mock(JTable.class);

    private final DecimalCellRenderer renderer = new DecimalCellRenderer();

    @Test
    void changeNumberToCurrencyAndDisplay() {
        Number value = 12345;
        renderer.getTableCellRendererComponent(table, value, false, false, 0, 0);

        assertEquals(NumberFormat.getInstance().format(value), renderer.getText());
    }

    @Test
    void mustBeRightAligned() {
        assertEquals(SwingConstants.RIGHT, renderer.getHorizontalAlignment());
        assertNotEquals(SwingConstants.LEFT, renderer.getHorizontalAlignment());
    }

    @Test
    void showWithoutConversion() {
        String value = "this is not number";
        renderer.getTableCellRendererComponent(table, value, false, false, 0, 0);
        assertEquals(value, renderer.getText());
    }

    @Test
    void whenYouEnterZero() {
        renderer.getTableCellRendererComponent(table, 0, false, false, 0, 0);
        assertEquals("0", renderer.getText());
    }

    @Test
    void whenYouEnterANegativeNumber() {
        renderer.getTableCellRendererComponent(table, -2000, false, false, 0, 0);
        assertEquals("-2,000", renderer.getText());

        renderer.getTableCellRendererComponent(table, -0, false, false, 0, 0);
        assertEquals("0", renderer.getText());
    }

    @Test
    void whenYouEnterNull() {
        renderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
        assertEquals("", renderer.getText());
    }

}
