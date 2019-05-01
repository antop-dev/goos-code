package org.antop.auctionsniper.ui;

import org.antop.auctionsniper.Item;
import org.antop.auctionsniper.SniperPortfolio;
import org.antop.auctionsniper.UserRequestListener;
import org.antop.auctionsniper.ui.cell.DecimalCellRenderer;
import org.antop.auctionsniper.util.Announcer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class MainWindow extends JFrame {
    public static final String APPLICATION_TITLE = "Auction Sniper";

    public static final String MAIN_WINDOW_NAME = "auction-sniper-main";
    public static final String NEW_ITEM_ID_NAME = "new-item-id-field";
    public static final String JOIN_BUTTON_NAME = "join-button";
    public static final String SNIPERS_TABLE_NAME = "snipers-table";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "new-item-stop-price";

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    public MainWindow(SniperPortfolio portfolio) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(portfolio), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel();

        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(10);
        itemIdField.setName(NEW_ITEM_ID_NAME);

        final JFormattedTextField stopPriceField = new JFormattedTextField(NumberFormat.getInstance());
        stopPriceField.setColumns(7);
        stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);

        final JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
            }

            private String itemId() {
                return itemIdField.getText();
            }

            private int stopPrice() {
                return ((Number) stopPriceField.getValue()).intValue();
            }
        });

        controls.add(new JLabel("Item:"));
        controls.add(itemIdField);
        controls.add(new JLabel("Stop price:"));
        controls.add(stopPriceField);
        controls.add(joinAuctionButton);

        return controls;
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(SniperPortfolio portfolio) {
        SnipersTableModel model = new SnipersTableModel();
        portfolio.addPortfolioListener(model);

        final JTable snipersTable = new JTable(model);
        snipersTable.setName(SNIPERS_TABLE_NAME);

        // format number
        DecimalCellRenderer decimalCellRenderer = new DecimalCellRenderer();
        snipersTable.getColumnModel().getColumn(Column.LAST_PRICE.ordinal()).setCellRenderer(decimalCellRenderer);
        snipersTable.getColumnModel().getColumn(Column.LAST_BID.ordinal()).setCellRenderer(decimalCellRenderer);

        return snipersTable;
    }

    public void addUserRequestListener(UserRequestListener listener) {
        userRequests.addListener(listener);
    }

}