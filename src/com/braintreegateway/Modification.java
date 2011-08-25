package com.braintreegateway;

import java.math.BigDecimal;

import com.braintreegateway.util.NodeWrapper;

public class Modification {
    private BigDecimal amount;
    private String description;
    private String id;
    private String kind;
    private Integer quantity;
    private String name;
    private Boolean neverExpires;
    private Integer numberOfBillingCycles;
    private String planId;

    public Modification(NodeWrapper node) {
        amount = node.findBigDecimal("amount");
        description = node.findString("description");
        id = node.findString("id");
        kind = node.findString("kind");
        quantity = node.findInteger("quantity");
        name = node.findString("name");
        neverExpires = node.findBoolean("never-expires");
        numberOfBillingCycles = node.findInteger("number-of-billing-cycles");
        planId = node.findString("plan-id");
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    public Integer getNumberOfBillingCycles() {
        return numberOfBillingCycles;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public boolean neverExpires() {
        return neverExpires;
    }

    public String getDescription() {
        return description;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public String getPlanId() {
        return planId;
    }
}
