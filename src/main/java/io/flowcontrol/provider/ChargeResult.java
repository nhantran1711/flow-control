package io.flowcontrol.provider;

import java.time.Duration;
import java.util.Objects;

/**
 * The provider's answer to one charge.
 *
 * @param outcome   what happened
 * @param reference the provider's ID for the charge, or null if nothing was charged
 * @param latency   how long the provider took to answer
 */
public record ChargeResult(ChargeOutcome outcome, String reference, Duration latency) {

    // Compact constructor: runs before the fields are set, so bad results can never exist.
    public ChargeResult {
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(latency, "latency");

        // A failed charge has no reference, because nothing was charged.
        if (outcome == ChargeOutcome.FAILED && reference != null) {
            throw new IllegalArgumentException("a failed charge has no reference");
        }
    }
}
