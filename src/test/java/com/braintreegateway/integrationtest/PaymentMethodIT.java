package com.braintreegateway.integrationtest;

import com.braintreegateway.*;
import com.braintreegateway.testhelpers.TestHelper;
import com.braintreegateway.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PaymentMethodIT {

    private BraintreeGateway gateway;

    @Before
    public void createGateway() {
        this.gateway = new BraintreeGateway(Environment.DEVELOPMENT, "integration_merchant_id", "integration_public_key", "integration_private_key");
    }

    @Test
    public void createPayPalAccountWithNonce() {
        Result<Customer> customerResult = gateway.customer().create(new CustomerRequest());
        assertTrue(customerResult.isSuccess());
        Customer customer = customerResult.getTarget();

        String nonce = TestHelper.generateFuturePaymentPayPalNonce(gateway);
        PaymentMethodRequest request = new PaymentMethodRequest().
            customerId(customer.getId()).
            paymentMethodNonce(nonce);

        Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);

        assertTrue(result.isSuccess());
        PaymentMethod paymentMethod = result.getTarget();
        assertNotNull(paymentMethod.getToken());
        assertNotNull(paymentMethod.getImageUrl());

        PayPalAccount paypalAccount = (PayPalAccount) paymentMethod;
        assertNotNull(paypalAccount.getEmail());
        assertNotNull(paypalAccount.getImageUrl());
    }

    @Test
    public void createPaymentMethodAndMakeDefault() {
        Result<Customer> customerResult = gateway.customer().create(new CustomerRequest());
        assertTrue(customerResult.isSuccess());
        Customer customer = customerResult.getTarget();

        CreditCardRequest creditCardRequest = new CreditCardRequest().
            customerId(customer.getId()).
            cardholderName("John Doe").
            cvv("123").
            number("5105105105105100").
            expirationDate("05/12");
        Result<CreditCard> creditCardResult = gateway.creditCard().create(creditCardRequest);
        assertTrue(creditCardResult.isSuccess());

        String nonce = TestHelper.generateFuturePaymentPayPalNonce(gateway);
        PaymentMethodRequest request = new PaymentMethodRequest().
            customerId(customer.getId()).
            paymentMethodNonce(nonce).
            options().
              makeDefault(true).
              done();

        Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);

        assertTrue(result.isSuccess());
        PaymentMethod paymentMethod = result.getTarget();
        assertTrue(paymentMethod.isDefault());
    }

    @Test
    public void createPaymentMethodWithToken() {
        Result<Customer> customerResult = gateway.customer().create(new CustomerRequest());
        assertTrue(customerResult.isSuccess());
        Customer customer = customerResult.getTarget();

        String nonce = TestHelper.generateFuturePaymentPayPalNonce(gateway);
        int r = (int)(Math.random() * 100000);
        String paymentMethodToken = "token-" + r;
        PaymentMethodRequest request = new PaymentMethodRequest().
            customerId(customer.getId()).
            paymentMethodNonce(nonce).
            token(paymentMethodToken);

        Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);

        assertTrue(result.isSuccess());
        PaymentMethod paymentMethod = result.getTarget();
        assertEquals(paymentMethodToken, paymentMethod.getToken());
    }

    @Test
    public void createPayPalAccountWithOneTimeNonceFails() {
        Result<Customer> customerResult = gateway.customer().create(new CustomerRequest());
        assertTrue(customerResult.isSuccess());
        Customer customer = customerResult.getTarget();

        String nonce = TestHelper.generateOneTimePayPalNonce(gateway);
        PaymentMethodRequest request = new PaymentMethodRequest().
            customerId(customer.getId()).
            paymentMethodNonce(nonce);

        Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);

        assertFalse(result.isSuccess());
        assertEquals(
                ValidationErrorCode.PAYPAL_ACCOUNT_CANNOT_VAULT_ONE_TIME_USE_PAYPAL_ACCOUNT,
                result.getErrors().forObject("paypalAccount").onField("base").get(0).getCode()
                );
    }

    @Test
    public void createSEPABankAccountWithNonce() {
        Result<Customer> customerResult = gateway.customer().create(new CustomerRequest());
        assertTrue(customerResult.isSuccess());
        Customer customer = customerResult.getTarget();

        String nonce = TestHelper.generateSEPABankAccountNonce(gateway, customer);
        PaymentMethodRequest request = new PaymentMethodRequest().
                customerId(customer.getId()).
                paymentMethodNonce(nonce);

        Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);
        assertTrue(result.isSuccess());
    }

    @Test
    public void createCreditCardWithNonce() {
        String nonce = TestHelper.generateUnlockedNonce(gateway, null, SandboxValues.CreditCardNumber.VISA.number);
        Result<Customer> customerResult = gateway.customer().create(new CustomerRequest());
        assertTrue(customerResult.isSuccess());
        Customer customer = customerResult.getTarget();

        PaymentMethodRequest request = new PaymentMethodRequest().
            customerId(customer.getId()).
            paymentMethodNonce(nonce);

        Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);

        assertTrue(result.isSuccess());
        PaymentMethod paymentMethod = result.getTarget();
        assertNotNull(paymentMethod.getToken());

        CreditCard creditCard = (CreditCard) paymentMethod;
        assertEquals("1111", creditCard.getLast4());
    }

    @Test
    public void deletePayPalAccount() {
        Result<Customer> customerResult = gateway.customer().create(new CustomerRequest());
        assertTrue(customerResult.isSuccess());
        Customer customer = customerResult.getTarget();

        String nonce = TestHelper.generateFuturePaymentPayPalNonce(gateway);
        PaymentMethodRequest request = new PaymentMethodRequest().
            customerId(customer.getId()).
            paymentMethodNonce(nonce);

        Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);

        assertTrue(result.isSuccess());
        PaymentMethod paymentMethod = result.getTarget();

        Result<? extends PaymentMethod> deleteResult = gateway.paymentMethod().delete(paymentMethod.getToken());
        assertTrue(deleteResult.isSuccess());
    }

    @Test
    public void deleteCreditCard() {
        Result<Customer> customerResult = gateway.customer().create(new CustomerRequest());
        assertTrue(customerResult.isSuccess());
        Customer customer = customerResult.getTarget();

        String nonce = TestHelper.generateUnlockedNonce(gateway, null, SandboxValues.CreditCardNumber.VISA.number);
        PaymentMethodRequest request = new PaymentMethodRequest().
            customerId(customer.getId()).
            paymentMethodNonce(nonce);

        Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);

        assertTrue(result.isSuccess());
        PaymentMethod paymentMethod = result.getTarget();

        Result<? extends PaymentMethod> deleteResult = gateway.paymentMethod().delete(paymentMethod.getToken());
        assertTrue(deleteResult.isSuccess());
    }

    @Test
    public void deleteMissingRaisesNotFoundError() {
        try {
            gateway.paymentMethod().delete("missing");
            fail("Should throw NotFoundException");
        } catch (NotFoundException e) {
        }
    }

    @Test
    public void findPayPalAccount() {
        Result<Customer> customerResult = gateway.customer().create(new CustomerRequest());
        assertTrue(customerResult.isSuccess());
        Customer customer = customerResult.getTarget();

        String nonce = TestHelper.generateFuturePaymentPayPalNonce(gateway);
        PaymentMethodRequest request = new PaymentMethodRequest().
            customerId(customer.getId()).
            paymentMethodNonce(nonce);

        Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);

        assertTrue(result.isSuccess());
        PaymentMethod paymentMethod = result.getTarget();

        PaymentMethod found = gateway.paymentMethod().find(paymentMethod.getToken());
        assertNotNull(found);
        assertTrue(found instanceof PayPalAccount);
    }

    @Test
    public void findCreditCard() {
        Result<Customer> customerResult = gateway.customer().create(new CustomerRequest());
        assertTrue(customerResult.isSuccess());
        Customer customer = customerResult.getTarget();

        String nonce = TestHelper.generateUnlockedNonce(gateway, null, SandboxValues.CreditCardNumber.VISA.number);
        PaymentMethodRequest request = new PaymentMethodRequest().
            customerId(customer.getId()).
            paymentMethodNonce(nonce);

        Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);

        assertTrue(result.isSuccess());
        PaymentMethod paymentMethod = result.getTarget();

        PaymentMethod found = gateway.paymentMethod().find(paymentMethod.getToken());
        assertNotNull(found);
        assertTrue(found instanceof CreditCard);
    }

    @Test
    public void findBlankRaisesNotFoundError() {
        try {
            gateway.paymentMethod().find("");
            fail("Should throw NotFoundException");
        } catch (NotFoundException e) {
        }
    }

    @Test
    public void findMissingRaisesNotFoundError() {
        try {
            gateway.paymentMethod().find("missing");
            fail("Should throw NotFoundException");
        } catch (NotFoundException e) {
        }
    }
}
