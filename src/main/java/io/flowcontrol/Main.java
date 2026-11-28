package io.flowcontrol;

import io.flowcontrol.provider.ChargeResult;
import io.flowcontrol.provider.SimulatedProvider;
import java.time.Duration;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        
        // Create a simulated provider with a mean latency of 80ms, a 0.1% failure rate, and a 2% decline rate
        SimulatedProvider providerA = new SimulatedProvider("provider-a", Duration.ofMillis(80), 0.001, 0.02);
        
        // Run 10 charges and print the results
        int charges = 10;
        Duration total = Duration.ZERO;

        for (int i = 1; i <= charges; i++) {
            ChargeResult result = providerA.charge(1_000); // $10.00, in cents
            total = total.plus(result.latency());

            // Print the outcome and latency of each charge
            System.out.printf("Charge %2d: %-8s %3d ms%n", i, result.outcome(), result.latency().toMillis());
        }

        System.out.println("Average latency: " + total.dividedBy(charges).toMillis() + " ms" + " (" + providerA.getId() + ")");
    }
}
