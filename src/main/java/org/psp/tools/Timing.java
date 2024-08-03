package org.psp.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class Timing {
    final static Logger LOG = LoggerFactory.getLogger(Timing.class.getName());
    private long start;
    private long end;

    public Timing() {
        setStart(System.currentTimeMillis());
    }

    private void setStart(long start) {
        LOG.debug("start = {}", start);
        this.start = start;
    }

    public void setEnd() {
        this.end = System.currentTimeMillis();
    }

    public String getBetween() {
        return between(start, end);
    }

    private String between(long start, long end) {
        LOG.debug(String.valueOf(start), end);

        String stringBetween = end - start + " ms";
        LOG.debug(stringBetween);

        return stringBetween;
    }
}
