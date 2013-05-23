package com.braintreegateway;

import com.braintreegateway.Transaction.Type;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a fluent interface to build up requests around {@link Transaction Transactions}.
 */
public class TransactionRequest extends Request {
    private BigDecimal amount;
    private TransactionAddressRequest billingAddressRequest;
    private TransactionCreditCardRequest creditCardRequest;
    private TransactionServiceFeeRequest serviceFeeRequest;
    private String channel;
    private String customerId;
    private String deviceSessionId;
    private CustomerRequest customerRequest;
    private Map<String, String> customFields;
    private String merchantAccountId;
    private String orderId;
    private String paymentMethodToken;
    private String purchaseOrderNumber;
    private Boolean recurring;
    private String shippingAddressId;
    private TransactionDescriptorRequest descriptorRequest;
    private TransactionAddressRequest shippingAddressRequest;
    private TransactionOptionsRequest transactionOptionsRequest;
    private BigDecimal taxAmount;
    private Boolean taxExempt;
    private Type type;
    private String venmoSdkPaymentMethodCode;

    public TransactionRequest() {
        this.customFields = new HashMap<String, String>();
    }

    public TransactionRequest amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TransactionAddressRequest billingAddress() {
        billingAddressRequest = new TransactionAddressRequest(this, "billing");
        return billingAddressRequest;
    }

    public TransactionRequest channel(String channel) {
        this.channel = channel;
        return this;
    }

    public TransactionCreditCardRequest creditCard() {
        creditCardRequest = new TransactionCreditCardRequest(this);
        return creditCardRequest;
    }
    
    public TransactionServiceFeeRequest serviceFee() {
    	serviceFeeRequest = new TransactionServiceFeeRequest(this);
    	return serviceFeeRequest;
    }

    public CustomerRequest customer() {
        customerRequest = new CustomerRequest(this);
        return customerRequest;
    }

    public TransactionRequest customerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public TransactionRequest customField(String apiName, String value) {
        customFields.put(apiName, value);
        return this;
    }

    public TransactionRequest deviceSessionId(String deviceSessionId) {
        this.deviceSessionId = deviceSessionId;
        return this;
    }

    public TransactionDescriptorRequest descriptor() {
        descriptorRequest = new TransactionDescriptorRequest(this);
        return descriptorRequest;
    }

    @Override
    public String getKind() {
        return TransparentRedirectGateway.CREATE_TRANSACTION;
    }

    public TransactionRequest merchantAccountId(String merchantAccountId) {
        this.merchantAccountId = merchantAccountId;
        return this;
    }

    public TransactionOptionsRequest options() {
        transactionOptionsRequest = new TransactionOptionsRequest(this);
        return transactionOptionsRequest;
    }

    public TransactionRequest orderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public TransactionRequest paymentMethodToken(String paymentMethodToken) {
        this.paymentMethodToken = paymentMethodToken;
        return this;
    }

    public TransactionRequest purchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
        return this;
    }

    public TransactionRequest recurring(Boolean recurring) {
        this.recurring = recurring;
        return this;
    }

    public TransactionAddressRequest shippingAddress() {
        shippingAddressRequest = new TransactionAddressRequest(this, "shipping");
        return shippingAddressRequest;
    }

    public TransactionRequest shippingAddressId(String shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
        return this;
    }

    public TransactionRequest taxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
        return this;
    }

    public TransactionRequest taxExempt(Boolean taxExempt) {
        this.taxExempt = taxExempt;
        return this;
    }

    public TransactionRequest venmoSdkPaymentMethodCode(String venmoSdkPaymentMethodCode) {
      this.venmoSdkPaymentMethodCode = venmoSdkPaymentMethodCode;
      return this;
    }

    @Override
    public String toQueryString() {
        return toQueryString("transaction");
    }

    @Override
    public String toQueryString(String root) {
        return buildRequest(root).toQueryString();
    }

    @Override
    public String toXML() {
        return buildRequest("transaction").toXML();
    }

    public TransactionRequest type(Type type) {
        this.type = type;
        return this;
    }

    protected RequestBuilder buildRequest(String root) {
        RequestBuilder builder = new RequestBuilder(root).
            addElement("amount", amount).
            addElement("channel", channel).
            addElement("customerId", customerId).
            addElement("merchantAccountId", merchantAccountId).
            addElement("orderId", orderId).
            addElement("paymentMethodToken", paymentMethodToken).
            addElement("purchaseOrderNumber", purchaseOrderNumber).
            addElement("taxAmount", taxAmount).
            addElement("taxExempt", taxExempt).
            addElement("shippingAddressId", shippingAddressId).
            addElement("creditCard", creditCardRequest).
            addElement("customer", customerRequest).
            addElement("descriptor", descriptorRequest).
            addElement("billing", billingAddressRequest).
            addElement("shipping", shippingAddressRequest).
            addElement("options", transactionOptionsRequest).
            addElement("recurring", recurring).
            addElement("deviceSessionId", deviceSessionId).
            addElement("venmoSdkPaymentMethodCode", venmoSdkPaymentMethodCode).
            addElement("serviceFee", serviceFeeRequest);

        if (!customFields.isEmpty()) {
            builder.addElement("customFields", customFields);
        }
        if (type != null) {
            builder.addElement("type", type.toString().toLowerCase());
        }

        return builder;
    }
}
