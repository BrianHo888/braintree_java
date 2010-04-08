package com.braintreegateway.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Request;
import com.braintreegateway.util.QueryString;

public class QueryStringTest {
    @Test
    public void append() {
        String actual = new QueryString().
            append("foo", "f").
            append("bar", "b").
            toString();
        Assert.assertEquals("foo=f&bar=b", actual);
    }

    @Test
    public void appendEmptyStringOrNulls() {
        String actual = new QueryString().
            append("foo", "f").
            append("", "b").
            append("bar", "").
            append("boom", null).
            append("", "c").
            toString();

        Assert.assertEquals("foo=f&bar=", actual);
    }

    @Test
    public void appendOtherObjectsWithCanBeConvertedToStrings() {
        String actual = new QueryString().
            append("foo", 10).
            append("bar", new BigDecimal("20.00")).
            toString();

        Assert.assertEquals("foo=10&bar=20.00", actual);
    }

    @Test
    public void appendWithRequest() {
        Request request = new CreditCardRequest().cvv("123").cardholderName("Drew");
        String actual = new QueryString().
            append("[credit_card]", request).
            toString();

        Assert.assertEquals("%5Bcredit_card%5D%5Bcardholder_name%5D=Drew&%5Bcredit_card%5D%5Bcvv%5D=123", actual);
    }

    @Test
    public void appendWithMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "john");
        map.put("age", "15");
        String actual = new QueryString().append("transaction[custom_fields]", map).toString();
        Assert.assertEquals("transaction%5Bcustom_fields%5D%5Bage%5D=15&transaction%5Bcustom_fields%5D%5Bname%5D=john", actual);
    }

    @Test
    public void appendWithNestedRequest() {
        Request request = new CreditCardRequest().
            cvv("123").
            cardholderName("Drew").
            billingAddress().
                company("Braintree").
                done().
            options().
                makeDefault(true).
                verifyCard(true).
                done();

        String actual = new QueryString().append("[credit_card]", request).toString();
        Assert.assertEquals("%5Bcredit_card%5D%5Bbilling_address%5D%5Bcompany%5D=Braintree&%5Bcredit_card%5D%5Bcardholder_name%5D=Drew&%5Bcredit_card%5D%5Bcvv%5D=123&%5Bcredit_card%5D%5Boptions%5D%5Bmake_default%5D=true&%5Bcredit_card%5D%5Boptions%5D%5Bverify_card%5D=true", actual);
    }
}
