package com.braintreegateway;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import com.braintreegateway.SandboxValues.CreditCardNumber;
import com.braintreegateway.SandboxValues.TransactionAmount;

public class TransparentRedirectTest {
    private BraintreeGateway gateway;
    
    @Before
    public void createGateway() {
        this.gateway = new BraintreeGateway(Environment.DEVELOPMENT, "integration_merchant_id", "integration_public_key", "integration_private_key");
    }
    
    @Test
    public void createTransactionFromTransparentRedirect() {
        TransactionRequest request = new TransactionRequest().
            amount(TransactionAmount.AUTHORIZE.amount).
            creditCard().
                number(CreditCardNumber.VISA.number).
                expirationDate("05/2009").
                done().
            options().
                storeInVault(true).
                done();

        TransactionRequest trParams = new TransactionRequest().
            type(Transaction.Type.SALE);
    
        String queryString = TestHelper.simulateFormPostForTR(gateway, trParams, request, gateway.transparentRedirect().url());
        Result<Transaction> result = gateway.transparentRedirect().confirmTransaction(queryString);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(CreditCardNumber.VISA.number.substring(0, 6), result.getTarget().getCreditCard().getBin());
        Assert.assertEquals(TransactionAmount.AUTHORIZE.amount, result.getTarget().getAmount());
    }
    
    @Test
    public void createTransactionFromTransparentRedirectSpecifyingMerchantAccountId() {
        TransactionRequest request = new TransactionRequest().
            amount(TransactionAmount.AUTHORIZE.amount).
            creditCard().
                number(CreditCardNumber.VISA.number).
                expirationDate("05/2009").
                done();

        TransactionRequest trParams = new TransactionRequest().
            type(Transaction.Type.SALE).
            merchantAccountId(MerchantAccount.NON_DEFAULT_MERCHANT_ACCOUNT_ID);
    
        String queryString = TestHelper.simulateFormPostForTR(gateway, trParams, request, gateway.transparentRedirect().url());
        Result<Transaction> result = gateway.transparentRedirect().confirmTransaction(queryString);
        
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(MerchantAccount.NON_DEFAULT_MERCHANT_ACCOUNT_ID, result.getTarget().getMerchantAccountId());
    }
    
    @Test
    public void createTransactionFromTransparentRedirectSpecifyingDescriptor() {
        TransactionRequest request = new TransactionRequest().
            amount(TransactionAmount.AUTHORIZE.amount).
            creditCard().
                number(CreditCardNumber.VISA.number).
                expirationDate("05/2009").
                done();

        TransactionRequest trParams = new TransactionRequest().
            type(Transaction.Type.SALE).
            descriptor().
                name("123*123456789012345678").
                phone("3334445555").
                done();
    
        String queryString = TestHelper.simulateFormPostForTR(gateway, trParams, request, gateway.transparentRedirect().url());
        Result<Transaction> result = gateway.transparentRedirect().confirmTransaction(queryString);
        
        Assert.assertTrue(result.isSuccess());
        Transaction transaction = result.getTarget();
        Assert.assertEquals("123*123456789012345678", transaction.getDescriptor().getName());
        Assert.assertEquals("3334445555", transaction.getDescriptor().getPhone());
    }
    
    @Test
    public void createCustomerFromTransparentRedirect() {
        CustomerRequest request = new CustomerRequest().firstName("John");
        CustomerRequest trParams = new CustomerRequest().lastName("Doe");
        String queryString = TestHelper.simulateFormPostForTR(gateway, trParams, request, gateway.transparentRedirect().url());
        
        Result<Customer> result = gateway.transparentRedirect().confirmCustomer(queryString);
        
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("John", result.getTarget().getFirstName());
        Assert.assertEquals("Doe", result.getTarget().getLastName());
    }
    
    @Test
    public void updateCustomerFromTransparentRedirect() {
        CustomerRequest request = new CustomerRequest().
            firstName("John").
            lastName("Doe");
        Customer customer = gateway.customer().create(request).getTarget();
        
        CustomerRequest updateRequest = new CustomerRequest().firstName("Jane");
        CustomerRequest trParams = new CustomerRequest().customerId(customer.getId()).lastName("Dough");
        String queryString = TestHelper.simulateFormPostForTR(gateway, trParams, updateRequest, gateway.transparentRedirect().url());
        
        Result<Customer> result = gateway.transparentRedirect().confirmCustomer(queryString);
        
        Assert.assertTrue(result.isSuccess());
        Customer updatedCustomer = gateway.customer().find(customer.getId());
        Assert.assertEquals("Jane", updatedCustomer.getFirstName());
        Assert.assertEquals("Dough", updatedCustomer.getLastName());
    }
    
    @Test
    public void createCreditCardFromTransparentRedirect() {
        Customer customer = gateway.customer().create(new CustomerRequest()).getTarget();
        CreditCardRequest request = new CreditCardRequest();
        CreditCardRequest trParams = new CreditCardRequest().
            customerId(customer.getId()).
            number("4111111111111111").
            expirationDate("10/10");
        String queryString = TestHelper.simulateFormPostForTR(gateway, trParams, request, gateway.transparentRedirect().url());
        
        Result<CreditCard> result = gateway.transparentRedirect().confirmCreditCard(queryString);
        
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("411111", result.getTarget().getBin());
        Assert.assertEquals("1111", result.getTarget().getLast4());
        Assert.assertEquals("10/2010", result.getTarget().getExpirationDate());
    }
    
    @Test
    public void updateCreditCardFromTransparentRedirect() {
        Customer customer = gateway.customer().create(new CustomerRequest()).getTarget();
        CreditCardRequest request = new CreditCardRequest().
            customerId(customer.getId()).
            number("5105105105105100").
            expirationDate("05/12");
        CreditCard card = gateway.creditCard().create(request).getTarget();
        
        CreditCardRequest updateRequest = new CreditCardRequest();
        CreditCardRequest trParams = new CreditCardRequest().
            paymentMethodToken(card.getToken()).
            number("4111111111111111").
            expirationDate("10/10");
        String queryString = TestHelper.simulateFormPostForTR(gateway, trParams, updateRequest, gateway.transparentRedirect().url());
        
        Result<CreditCard> result = gateway.transparentRedirect().confirmCreditCard(queryString);
        
        Assert.assertTrue(result.isSuccess());
        CreditCard updatedCreditCard = gateway.creditCard().find(card.getToken());
        Assert.assertEquals("411111", updatedCreditCard.getBin());
        Assert.assertEquals("1111", updatedCreditCard.getLast4());
        Assert.assertEquals("10/2010", updatedCreditCard.getExpirationDate());
    }
    
    @Test
    public void errorRaisedWhenConfirmingIncorrectResource() {
        CustomerRequest request = new CustomerRequest().firstName("John");
        CustomerRequest trParams = new CustomerRequest().lastName("Doe");
        String queryString = TestHelper.simulateFormPostForTR(gateway, trParams, request, gateway.transparentRedirect().url());
        
        try {
            gateway.transparentRedirect().confirmTransaction(queryString);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("You attemped to confirm a transaction, but received a customer.", e.getMessage());
        }
    }
    
    @Test
    public void errorNotRaisedWhenReceivingApiErrorResponse() {
        TransactionRequest invalidRequest = new TransactionRequest();
        TransactionRequest trParams = new TransactionRequest().type(Transaction.Type.SALE);
        String queryString = TestHelper.simulateFormPostForTR(gateway, trParams, invalidRequest, gateway.transparentRedirect().url());
        Result<Transaction> result = gateway.transparentRedirect().confirmTransaction(queryString);
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getErrors().deepSize() > 0);
    }
}
