package io.flowcontrol.provider;

/** What a payment provider says happened to a charge. */
public enum ChargeOutcome {

    /** The money was taken from the customer. */
    APPROVED,

    /** The bank refused, e.g. insufficient funds. Final: retrying will not help. */
    DECLINED,

    /** The provider hit an error and charged nothing. Safe to retry later. */
    FAILED
}
