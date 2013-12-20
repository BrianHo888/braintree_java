package com.braintreegateway.util;

import org.junit.Test;
import java.util.regex.*;

import com.braintreegateway.AuthorizationFingerprintGenerator;
import com.braintreegateway.AuthorizationFingerprintOptions;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class AuthorizationFingerprintGeneratorTest {

  @Test
  public void containsEssentialData() {
    String fingerprint = AuthorizationFingerprintGenerator.generate(
        "my_merchant_id",
        "my_public_key",
        "private_key",
        "http://localhost:3000/merchants/my_merchant_id/client_api",
        "http://auth.venmo.dev:4567",
        null
    );
    String[] fingerprintParts = fingerprint.split("\\|");
    String signature = fingerprintParts[0];
    String data = fingerprintParts[1];

    assertTrue(signature.length() > 1);
    assertTrue(data.contains("merchant_id=my_merchant_id"));
    assertTrue(data.contains("public_key=my_public_key"));
    assertTrue(data.contains("created_at="));
    assertTrue(data.contains("client_api_url=http://localhost:3000/merchants/my_merchant_id/client_api"));
    assertTrue(data.contains("auth_url=http://auth.venmo.dev:4567"));
  }

  @Test
  public void isNotUrlEncoded() {
    String fingerprint = AuthorizationFingerprintGenerator.generate(
        "my_merchant_id",
        "my_public_key",
        "private_key",
        "http://localhost:3000/merchants/my_merchant_id",
        "http://auth.venmo.dev:4567",
        null
    );
    String[] fingerprintParts = fingerprint.split("\\|");
    String data = fingerprintParts[1];

    assertFalse(data.contains("%3A1"));
    assertTrue(data.contains("my_merchant_id"));
    assertTrue(data.contains("my_public_key"));
    assertTrue(data.contains("created_at"));
    assertTrue(data.contains("http://localhost:3000/merchants/my_merchant_id"));
  }

  @Test
  public void canIncludeCustomerId() {
    String fingerprint = AuthorizationFingerprintGenerator.generate(
        "merchant_id",
        "public_key",
        "private_key",
        "http://localhost:3000/merchants/my_merchant_id",
        "http://auth.venmo.dev:4567",
        new AuthorizationFingerprintOptions().customerId("a-customer-id")
    );
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

    String fingerprint = AuthorizationFingerprintGenerator.generate(
        "needs encoding",
        "public_key",
        "private_key",
        "http://localhost:3000/merchants/my_merchant_id",
        "http://auth.venmo.dev:4567",
        options
    );
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
      AuthorizationFingerprintGenerator.generate("test", "test", "test", "test", "test", options);
      fail("Expected IllegalArgumentException when credit card options are provided with no customer ID");
    } catch (IllegalArgumentException e) {
      assertTrue(expectedPattern.matcher(e.getMessage()).find());
    }

    expectedPattern = Pattern.compile("makeDefault");
    try {
      AuthorizationFingerprintOptions options = new AuthorizationFingerprintOptions().makeDefault(true);
      AuthorizationFingerprintGenerator.generate("test", "test", "test", "test", "test", options);
      fail("Expected IllegalArgumentException when credit card options are provided with no customer ID");
    } catch (IllegalArgumentException e) {
      assertTrue(expectedPattern.matcher(e.getMessage()).find());
    }

    expectedPattern = Pattern.compile("failOnDuplicatePaymentMethod");
    try {
      AuthorizationFingerprintOptions options = new AuthorizationFingerprintOptions().failOnDuplicatePaymentMethod(true);
      AuthorizationFingerprintGenerator.generate("test", "test", "test", "test", "test", options);
      fail("Expected IllegalArgumentException when credit card options are provided with no customer ID");
    } catch (IllegalArgumentException e) {
      assertTrue(expectedPattern.matcher(e.getMessage()).find());
    }
  }
}
