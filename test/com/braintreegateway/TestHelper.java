package com.braintreegateway;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import junit.framework.Assert;

import com.braintreegateway.Transaction.Status;
import com.braintreegateway.exceptions.UnexpectedException;
import com.braintreegateway.util.Crypto;

public class TestHelper {

    public static void assertDatesEqual(Calendar first, Calendar second) {
        if (first == null && second != null) {
            throw new AssertionError("dates are not equal. first is null, second is not");
        } else if (first != null && second == null) {
            throw new AssertionError("dates are not equal. second is null, first is not");
        }
        boolean yearsNotEqual = first.get(Calendar.YEAR) != second.get(Calendar.YEAR);
        boolean monthsNotEqual = first.get(Calendar.MONTH) != second.get(Calendar.MONTH);
        boolean daysNotEqual = first.get(Calendar.DAY_OF_MONTH) != second.get(Calendar.DAY_OF_MONTH);
        if (yearsNotEqual || monthsNotEqual || daysNotEqual) {
            StringBuffer buffer = new StringBuffer("dates are not equal. ");
            if (yearsNotEqual) {
                buffer.append("years (" + first.get(Calendar.YEAR) + ", " + second.get(Calendar.YEAR) + ") not equal.");
            }
            if (monthsNotEqual) {
                buffer.append("months (" + first.get(Calendar.MONTH) + ", " + second.get(Calendar.MONTH) + ") not equal.");
            }
            if (daysNotEqual) {
                buffer.append("days (" + first.get(Calendar.DAY_OF_MONTH) + ", " + second.get(Calendar.DAY_OF_MONTH) + ") not equal.");
            }
            throw new AssertionError(buffer.toString());
        }
    }

    public static void assertIncludes(String expected, String all) {
        Assert.assertTrue("Expected:\n" + all + "\nto include:\n" + expected, all.indexOf(expected) >= 0);
    }

    public static void assertValidTrData(Configuration configuration, String trData) {
        String[] dataSections = trData.split("\\|");
        String trHash = dataSections[0];
        String trContent = dataSections[1];
        Assert.assertEquals(trHash, new Crypto().hmacHash(configuration.privateKey, trContent));
    }

    public static boolean includesSubscription(PagedCollection<Subscription> collection, Subscription item) {
        for (Subscription subscription : collection) {
            if (subscription.getId().equals(item.getId())) {
                return true;
            }
        }

        return false;
    }

    public static boolean includesStatus(PagedCollection<Transaction> collection, Status status) {
        for (Transaction transaction : collection) {
            if (transaction.getStatus().equals(status)) {
                return true;
            }
        }

        return false;
    }

    public static String simulateFormPostForTR(BraintreeGateway gateway, Request trParams, Request request, String postUrl) {
        String response = "";
        try {
            String trData = gateway.trData(trParams, "http://example.com");
            String postData = "tr_data=" + URLEncoder.encode(trData, "UTF-8") + "&";
            postData += request.toQueryString();

            URL url = new URL(postUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Accept", "application/xml");
            connection.addRequestProperty("User-Agent", "Braintree Java");
            connection.addRequestProperty("X-ApiVersion", "1");
            connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.getOutputStream().write(postData.getBytes("UTF-8"));
            connection.getOutputStream().close();
            if (connection.getResponseCode() == 422) {
                connection.getErrorStream();
            } else {
                connection.getInputStream();
            }
            response = connection.getURL().getQuery();
        } catch (IOException e) {
            throw new UnexpectedException(e.getMessage());
        }

        return response;
    }
}
