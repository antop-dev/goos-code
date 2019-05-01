package org.antop.auctionsniper.end2end;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.LogManager;

public class AuctionLogDriver {
    public static String LOG_FILE_NAME = "auction-sniper.log";
    private final Path logFile = Paths.get(LOG_FILE_NAME);

    public void clearLog() throws IOException {
        LogManager.getLogManager().reset();
        Files.deleteIfExists(logFile);
    }

    public void hasEntry(Matcher<String> matcher) throws IOException {
        String s = new String(Files.readAllBytes(logFile));
        MatcherAssert.assertThat(s, matcher);
    }
}
