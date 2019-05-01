package org.antop.auctionsniper.unit.xmpp;

import org.antop.auctionsniper.Item;
import org.antop.auctionsniper.xmpp.XMPPAuction;
import org.antop.auctionsniper.xmpp.XMPPFailureReporter;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class XMPPAuctionTest {

    private final Chat chat = mock(Chat.class);
    private final ChatManager chatManager = mock(ChatManager.class);
    private final XMPPConnection connection = mock(XMPPConnection.class);
    private final Item item = new Item("item-9999", 5000);
    private final XMPPFailureReporter failureReporter = mock(XMPPFailureReporter.class);

    private XMPPAuction auction;

    @BeforeEach
    void setUp() {
        when(connection.getChatManager()).thenReturn(chatManager);
        when(chatManager.createChat(anyString(), any())).thenReturn(chat);

        auction = new XMPPAuction(connection, item.getIdentifier(), failureReporter);
    }

    @Test
    void bid() throws Exception {
        int bid = 1200;
        // auction
        auction.bid(bid);
        // verify
        verify(chat).sendMessage(String.format(XMPPAuction.BID_COMMAND_FORMAT, bid));
    }

    @Test
    void join() throws Exception {
        auction.join();
        verify(chat).sendMessage(XMPPAuction.JOIN_COMMAND_FORMAT);
    }

    @Test
    void errorWhenSendMessage() throws Exception {
        // chat.sendMessage(message:String) 실핼 하면 XMPPException 발생
        doThrow(XMPPException.class).when(chat).sendMessage(anyString());

        // TODO catch (XMPPException e) 체크
    }
}
