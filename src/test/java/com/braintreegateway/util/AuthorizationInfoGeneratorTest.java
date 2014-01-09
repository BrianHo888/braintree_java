package com.braintreegateway.util;

import org.junit.Test;
import java.util.regex.*;

import com.braintreegateway.AuthorizationInfoGenerator;
import com.braintreegateway.AuthorizationFingerprintOptions;
import com.braintreegateway.testhelpers.TestHelper;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AuthorizationInfoGeneratorTest {

  private String _getAuthInfo(AuthorizationFingerprintOptions options) {
      return AuthorizationInfoGenerator.generate(
          "my_merchant_id",
          "my_public_key",
          "private_key",
          "http://client.api.url",
          "http://auth.url",
          options
      );
  }

  @Test
  public void containsEssentialData() {
    String authInfo = _getAuthInfo(null);
    String fingerprint = TestHelper.extractParamFromJson("fingerprint", authInfo);
    String[] fingerprintParts = fingerprint.split("\\|");
    String signature = fingerprintParts[0];
    String data = fingerprintParts[1];

    assertTrue(signature.length() > 1);
    assertTrue(data.contains("public_key=my_public_key"));
    assertTrue(data.contains("created_at="));
    assertEquals(TestHelper.extractParamFromJson("client_api_url", authInfo), "http://client.api.url");
    assertEquals(TestHelper.extractParamFromJson("auth_url", authInfo), "http://auth.url");
  }

  @Test
  public void isNotUrlEncoded() {
    String authInfo = _getAuthInfo(null);
    String fingerprint = TestHelper.extractParamFromJson("fingerprint", authInfo);
    String[] fingerprintParts = fingerprint.split("\\|");
    String data = fingerprintParts[1];

    assertFalse(data.contains("%3A1"));
    assertTrue(data.contains("my_public_key"));
    assertTrue(data.contains("created_at"));
  }

  @Test
  public void canIncludeCustomerId() {
    AuthorizationFingerprintOptions options = new AuthorizationFingerprintOptions().customerId("a-customer-id");
    String authInfo = _getAuthInfo(options);
    String fingerprint = TestHelper.extractParamFromJson("fingerprint", authInfo);
    String[] fingerprintParts = fingerprint.split("\\|");
    String data = fingerprintParts[1];

    assertTrue(data.contains("a-customer-id"));
  }

  @Test
  public void containsOptions() {
    AuthorizationFingerprintOptions options = new AuthorizationFingerprintOptions().
      customerId("a-customer-id").
      makeDefault(true).
      verifyCard(true).
      failOnDuplicatePaymentMethod(true);

    String authInfo = _getAuthInfo(options);
    String fingerprint = TestHelper.extractParamFromJson("fingerprint", authInfo);
    String[] fingerprintParts = fingerprint.split("\\|");
    String data = fingerprintParts[1];

    assertTrue(data.contains("credit_card[options][make_default]=true"));
    assertTrue(data.contains("credit_card[options][verify_card]=true"));
    assertTrue(data.contains("credit_card[options][fail_on_duplicate_payment_method]=true"));
  }

  @Test
  public void requiresCustomerIdForOptions() {
    Pattern expectedPattern = Pattern.compile("verifyCard");
    try {
      AuthorizationFingerprintOptions options = new AuthorizationFingerprintOptions().verifyCard(true);
      AuthorizationInfoGenerator.generate("test", "test", "test", "test", "test", options);
      fail("Expected IllegalArgumentException when credit card options are provided with no customer ID");
    } catch (IllegalArgumentException e) {
      assertTrue(expectedPattern.matcher(e.getMessage()).find());
    }

    expectedPattern = Pattern.compile("makeDefault");
    try {
      AuthorizationFingerprintOptions options = new AuthorizationFingerprintOptions().makeDefault(true);
      AuthorizationInfoGenerator.generate("test", "test", "test", "test", "test", options);
      fail("Expected IllegalArgumentException when credit card options are provided with no customer ID");
    } catch (IllegalArgumentException e) {
      assertTrue(expectedPattern.matcher(e.getMessage()).find());
    }

    expectedPattern = Pattern.compile("failOnDuplicatePaymentMethod");
    try {
      AuthorizationFingerprintOptions options = new AuthorizationFingerprintOptions().failOnDuplicatePaymentMethod(true);
      AuthorizationInfoGenerator.generate("test", "test", "test", "test", "test", options);
      fail("Expected IllegalArgumentException when credit card options are provided with no customer ID");
    } catch (IllegalArgumentException e) {
      assertTrue(expectedPattern.matcher(e.getMessage()).find());
    }
  }
}
