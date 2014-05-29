package com.braintreegateway;

import com.braintreegateway.util.NodeWrapper;
import java.util.ArrayList;
import java.util.List;

public class SEPABankAccount implements PaymentMethod {
    private String token;
    private boolean isDefault;
    private String maskedIban;
    private String bic;
    private String mandateReferenceNumber;
    private String accountHolderName;

    public enum MandateType {
        BUSINESS("business"),
        CONSUMER("consumer");
        private final String name;

        MandateType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public SEPABankAccount(NodeWrapper node) {
        this.token = node.findString("token");
        this.isDefault = node.findBoolean("default");
        this.maskedIban = node.findString("masked-iban");
        this.bic = node.findString("bic");
        this.mandateReferenceNumber = node.findString("mandate-reference-number");
        this.accountHolderName = node.findString("account-holder-name");
    }

    public String getToken() {
        return token;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getMaskedIban() {
        return maskedIban;
    }

    public String getBic() {
        return bic;
    }

    public String getMandateReferenceNumber() {
        return mandateReferenceNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }


}
