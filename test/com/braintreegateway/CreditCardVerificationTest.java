package com.braintreegateway;

import com.braintreegateway.util.NodeWrapperFactory;
import org.junit.Assert;

import org.junit.Test;

import com.braintreegateway.util.NodeWrapper;


public class CreditCardVerificationTest {
    @Test
    public void constructFromResponse() {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append("<api-error-response>");
        builder.append("  <verification>");
        builder.append("    <avs-error-response-code nil=\"true\"></avs-error-response-code>");
        builder.append("    <avs-postal-code-response-code>I</avs-postal-code-response-code>");
        builder.append("    <status>processor_declined</status>");
        builder.append("    <processor-response-code>2000</processor-response-code>");
        builder.append("    <avs-street-address-response-code>I</avs-street-address-response-code>");
        builder.append("    <processor-response-text>Do Not Honor</processor-response-text>");
        builder.append("    <cvv-response-code>M</cvv-response-code>");
        builder.append("  </verification>");
        builder.append("  <errors>");
        builder.append("    <errors type=\"array\"/>");
        builder.append("  </errors>");
        builder.append("</api-error-response>");

        NodeWrapper verificationNode = (NodeWrapperFactory.instance.create(builder.toString())).findFirst("verification");
        CreditCardVerification verification = new CreditCardVerification(verificationNode);
        Assert.assertEquals(null, verification.getAvsErrorResponseCode());
        Assert.assertEquals("I", verification.getAvsPostalCodeResponseCode());
        Assert.assertEquals(CreditCardVerification.Status.PROCESSOR_DECLINED, verification.getStatus());
        Assert.assertEquals("2000", verification.getProcessorResponseCode());
        Assert.assertEquals("I", verification.getAvsStreetAddressResponseCode());
        Assert.assertEquals("Do Not Honor", verification.getProcessorResponseText());
        Assert.assertEquals("M", verification.getCvvResponseCode());
    }
}
