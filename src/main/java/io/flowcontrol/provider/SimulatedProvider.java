package io.flowcontrol.provider;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A fake payment provider. 
 * It takes roughly {@code meanLatency} to answer, 
 * fails some requests, and declines some charges.
 */
public class SimulatedProvider {

    // How spread out the latencies are. Bigger means a longer tail of slow requests
    private static final double LATENCY_SPREAD = 0.35;

    private final String id;
    private final Duration meanLatency;
    private final double failureRate;
    private final double declineRate; 

    public SimulatedProvider(String id, Duration meanLatency, double failureRate, double declineRate) {
        this.id = id;
        this.meanLatency = meanLatency;
        this.failureRate = failureRate;
        this.declineRate = declineRate;
    }

    // Pretends to charge a card. Returns a ChargeResult with the outcome and how long it took
    public ChargeResult charge(long amountCents) throws InterruptedException {

        long start = System.nanoTime();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // 1. Pretend to do the work
        Thread.sleep(randomLatency(random));

        // 2. Roll the dice for what happened.
        Duration took = Duration.ofNanos(System.nanoTime() - start);
        double roll = random.nextDouble(); // uniform between 0.0 and 1.0

        // 3. Return the result
        if (roll < failureRate) {
            return new ChargeResult(ChargeOutcome.FAILED, null, took);
        }

        // 4. If it didn't fail, it either declined or approved
        String reference = id + "_" + UUID.randomUUID();
        if (roll < failureRate + declineRate) {
            return new ChargeResult(ChargeOutcome.DECLINED, reference, took);
        }
        return new ChargeResult(ChargeOutcome.APPROVED, reference, took);
    }

    // Returns a random latency based on a log-normal distribution with the given mean and spread
    private Duration randomLatency(ThreadLocalRandom random) {
        double gaussian = random.nextGaussian(); // bell curve centred on 0
        double factor = Math.exp(LATENCY_SPREAD * gaussian - LATENCY_SPREAD * LATENCY_SPREAD / 2);
        
        return Duration.ofNanos((long) (meanLatency.toNanos() * factor));
    }

    // GETTERS

    public String getId() {
        return this.id;
    }
}
