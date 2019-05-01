package org.antop.auctionsniper.ui;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SniperSnapshot {

    private final String itemId;
    private final int lastPrice;
    private final int lastBid;
    private final SniperState state;

    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    }

    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.state = state;
    }

    public String getItemId() {
        return itemId;
    }

    public int getLastPrice() {
        return lastPrice;
    }

    public int getLastBid() {
        return lastBid;
    }

    public SniperState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SniperSnapshot that = (SniperSnapshot) o;

        return new EqualsBuilder()
                .append(itemId, that.itemId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(itemId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("itemId", itemId)
                .append("lastPrice", lastPrice)
                .append("lastBid", lastBid)
                .append("state", state)
                .toString();
    }

    public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
        return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
    }

    public SniperSnapshot winning(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, newLastPrice, SniperState.WINNING);
    }

    public SniperSnapshot closed() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
    }

    public SniperSnapshot failed() {
        // 나는 좀 더 명확한 구분을 위해 뭔가 실패시 [-1, -1]을 사용했다.
        // 이유는 처음 경매에 참여를 하게 되면 시작이 [0, 0]으로 시작하기 때문에
        // [0, 0, Failed], [0, 0, Joining] 두개의 상태가 햇갈렸다.
        return new SniperSnapshot(itemId, -1, -1, SniperState.FAILED);
    }

    public SniperSnapshot losing(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.LOSING);
    }

    public boolean isForSameItemAs(SniperSnapshot snapshot) {
        return itemId.equals(snapshot.itemId);
    }

    public boolean isState(SniperState state) {
        return this.state == state;
    }

}
