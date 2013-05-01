package com.braintreegateway;

import com.braintreegateway.util.NodeWrapper;

import java.util.Map;

public class Result<T> {

    private CreditCardVerification creditCardVerification;
    private Transaction transaction;
    private Subscription subscription;
    private ValidationErrors errors;
    private Map<String, String> parameters;
    private String message;
    private T target;

    @SuppressWarnings("unchecked")
    public static <T> T newInstanceFromNode(Class<T> klass, NodeWrapper node) {
        if (klass == CreditCard.class) {
            return (T) new CreditCard(node);
        } else if (klass == Address.class) {
            return (T) new Address(node);
        } else if (klass == Customer.class) {
            return (T) new Customer(node);
        } else if (klass == Subscription.class) {
            return (T) new Subscription(node);
        } else if (klass == Transaction.class) {
            return (T) new Transaction(node);
        } else if (klass == SettlementBatchSummary.class){
            return (T) new SettlementBatchSummary(node);
        }
        throw new IllegalArgumentException("Unknown klass: " + klass);
    }

    public Result() {
    }

    public Result(NodeWrapper node, Class<T> klass) {
        if (node.isSuccess()) {
            this.target = newInstanceFromNode(klass, node);
        } else {
            this.errors = new ValidationErrors(node);

            NodeWrapper verificationNode = node.findFirst("verification");
            if (verificationNode != null) {
                this.creditCardVerification = new CreditCardVerification(verificationNode);
            }

            NodeWrapper transactionNode = node.findFirst("transaction");
            if (transactionNode != null) {
                this.transaction = new Transaction(transactionNode);
            }
            NodeWrapper subscriptionNode = node.findFirst("subscription");
            if (subscriptionNode != null) {
                this.subscription = new Subscription(subscriptionNode);
            }
            this.parameters = node.findFirst("params").getFormParameters();
            this.message = node.findString("message");
        }
    }

    public CreditCardVerification getCreditCardVerification() {
        return creditCardVerification;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public ValidationErrors getErrors() {
        return errors;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public T getTarget() {
        return target;
    }

    public boolean isSuccess() {
        return errors == null;
    }

    public String getMessage() {
        return message;
    }
}
