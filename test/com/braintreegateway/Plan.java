package com.braintreegateway;

import java.math.BigDecimal;

public class Plan {
    private int billingFrequency;
    private String description;
    private String id;
    private BigDecimal price;
    private boolean trialPeriod;
    private int trialDuration;
    private Subscription.DurationUnit trialDurationUnit;
    
    public static final Plan PLAN_WITHOUT_TRIAL = new Plan("Plan for integration tests -- without a trial", "integration_trialless_plan", new BigDecimal("12.34"), 1, false);
    
    public static final Plan PLAN_WITH_TRIAL = new Plan("Plan for integration tests -- with a trial", "integration_trial_plan", new BigDecimal("43.21"), 1, true, 2, Subscription.DurationUnit.DAY);
    
    public Plan(String description, String id, BigDecimal price, int billingFrequency, boolean trialPeriod, int trialDuration, Subscription.DurationUnit trialDurationUnit) {
        this.billingFrequency = billingFrequency;
        this.description = description;
        this.id = id;
        this.price = price;
        this.trialPeriod = trialPeriod;
        this.trialDuration = trialDuration;
        this.trialDurationUnit = trialDurationUnit;
    }
    
    public Plan(String description, String id, BigDecimal price, int billingFrequency, boolean trialPeriod) {
        this(description, id, price, 1, trialPeriod, 0, null);
    }

    public Integer getBillingFrequency() {
        return billingFrequency;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Boolean isTrialPeriod() {
        return trialPeriod;
    }

    public Integer getTrialDuration() {
        return trialDuration;
    }

    public Subscription.DurationUnit getTrialDurationUnit() {
        return trialDurationUnit;
    }
}
