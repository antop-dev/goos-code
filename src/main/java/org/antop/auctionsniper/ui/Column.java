package org.antop.auctionsniper.ui;

public enum Column {
    ITEM_IDENTIFIER("Item") {
        @Override
        public String valueIn(SniperSnapshot snapshot) {
            return snapshot.getItemId();
        }
    },
    LAST_PRICE("Last Price") {
        @Override
        public Integer valueIn(SniperSnapshot snapshot) {
            return snapshot.getLastPrice();
        }
    },
    LAST_BID("Last Bid") {
        @Override
        public Integer valueIn(SniperSnapshot snapshot) {
            return snapshot.getLastBid();
        }
    },
    SNIPER_STATE("State") {
        @Override
        public String valueIn(SniperSnapshot snapshot) {
            return SnipersTableModel.textFor(snapshot.getState());
        }
    };

    private final String name;

    Column(String name) {
        this.name = name;
    }

    public abstract Object valueIn(SniperSnapshot snapshot);

    public static Column at(int offset) {
        return values()[offset];
    }

    public String getName() {
        return name;
    }
}
