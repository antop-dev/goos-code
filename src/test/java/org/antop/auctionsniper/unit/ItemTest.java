package org.antop.auctionsniper.unit;

import com.jparams.verifier.tostring.ToStringVerifier;
import org.antop.auctionsniper.Item;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    private final Item item1 = new Item("item-123", 300);
    private final Item item2 = new Item("item-456", 700);

    @Test
    void createItem() {
        Item item = new Item("an item", 123);

        assertNotNull(item, "item is not null");
        assertEquals("an item", item.getIdentifier());
        assertEquals(123, item.getStopPrice());
        assertEquals(new HashCodeBuilder().append(item.getIdentifier()).toHashCode(), item.hashCode());
    }

    @Test
    void compareItem() {
        // 아이디만 같으면 같은 아이템이다.
        assertEquals(item1, new Item("item-123", 777));
        assertNotEquals(item1, item2);
    }

    @Test
    void haveToString() {
        ToStringVerifier.forClass(Item.class).verify();
    }

}
