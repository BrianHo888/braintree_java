package com.braintreegateway;

import java.math.BigDecimal;

public class SandboxValues {
    public enum CreditCardNumber {
        VISA("4111111111111111");

        public String number;

        private CreditCardNumber(String number) {
            this.number = number;
        }
    }

    public enum TransactionAmount {
        AUTHORIZE("1000.00"), DECLINE("2000.00");

        public BigDecimal amount;

        private TransactionAmount(String amount) {
            this.amount = new BigDecimal(amount);
        }
    }
}
