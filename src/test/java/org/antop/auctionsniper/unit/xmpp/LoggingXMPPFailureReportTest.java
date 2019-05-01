package org.antop.auctionsniper.unit.xmpp;

import org.antop.auctionsniper.xmpp.LoggingXMPPFailureReporter;
import org.antop.auctionsniper.xmpp.XMPPFailureReporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggingXMPPFailureReportTest {

    private final Logger logger = Mockito.mock(Logger.class);
    private final XMPPFailureReporter reporter = new LoggingXMPPFailureReporter(logger);

    @AfterAll
    public static void resetLogging() {
        LogManager.getLogManager().reset();;
    }

    @Test
    void writesMessageTranslationFailureToLog() {
        reporter.cannotTranslateMessage("auction id", "bad message", new Exception("it's bad"));

        Mockito.verify(logger).severe(
                "<auction id> "
                        + "Could not translate message \"bad message\" " +
                        "because \"java.lang.Exception: it's bad\"");
    }
}
