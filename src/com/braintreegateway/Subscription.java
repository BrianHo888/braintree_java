package com.braintreegateway;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.braintreegateway.util.EnumUtils;
import com.braintreegateway.util.NodeWrapper;

public class Subscription {

    private Calendar billingPeriodEndDate;
    private Calendar billingPeriodStartDate;
    private Integer failureCount;
    private Calendar firstBillingDate;
    private Boolean hasTrialPeriod;
    private String id;
    private Calendar nextBillingDate;
    private String paymentMethodToken;
    private String planId;
    private BigDecimal price;
    private Status status;
    private List<Transaction> transactions;
    private Integer trialDuration;
    private DurationUnit trialDurationUnit;

    public enum DurationUnit {
        DAY, MONTH, UNRECOGNIZED
    }

    public enum Status {
        ACTIVE, CANCELED, PAST_DUE, UNRECOGNIZED
    }

    public Subscription(NodeWrapper node) {
        billingPeriodEndDate = node.findDate("billing-period-end-date");
        billingPeriodStartDate = node.findDate("billing-period-start-date");
        failureCount = node.findInteger("failure-count");
        firstBillingDate = node.findDate("first-billing-date");
        id = node.findString("id");
        nextBillingDate = node.findDate("next-billing-date");
        paymentMethodToken = node.findString("payment-method-token");
        planId = node.findString("plan-id");
        price = node.findBigDecimal("price");
        status = EnumUtils.findByName(Status.class, node.findString("status"));
        hasTrialPeriod = node.findBoolean("trial-period");
        trialDuration = node.findInteger("trial-duration");
        trialDurationUnit = EnumUtils.findByName(DurationUnit.class, node.findString("trial-duration-unit"));
        transactions = new ArrayList<Transaction>();
        for (NodeWrapper transactionResponse : node.findAll("transactions/transaction")) {
            transactions.add(new Transaction(transactionResponse));
        }
    }

    public Calendar getBillingPeriodEndDate() {
        return billingPeriodEndDate;
    }

    public Calendar getBillingPeriodStartDate() {
        return billingPeriodStartDate;
    }

    public String getId() {
        return id;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public Calendar getFirstBillingDate() {
        return firstBillingDate;
    }

    public Calendar getNextBillingDate() {
        return nextBillingDate;
    }

    public String getPaymentMethodToken() {
        return paymentMethodToken;
    }

    public String getPlanId() {
        return planId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Status getStatus() {
        return status;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Integer getTrialDuration() {
        return trialDuration;
    }

    public DurationUnit getTrialDurationUnit() {
        return trialDurationUnit;
    }

    public Boolean hasTrialPeriod() {
        return hasTrialPeriod;
    }

}
