package com.braintreegateway.util;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.exceptions.AuthenticationException;

public class HttpTest {

    private BraintreeGateway gateway;

    @Before
    public void createGateway() {
        this.gateway = new BraintreeGateway(Environment.DEVELOPMENT, "integration_merchant_id", "integration_public_key", "integration_private_key");
    }

    @Test
    public void smokeTestGet() {
        NodeWrapper node = new Http(gateway.getAuthorizationHeader(), gateway.baseMerchantURL(), "1.0.0").get("/customers/131866");
        Assert.assertNotNull(node.findString("first-name"));
    }

    @Test
    public void smokeTestPostWithRequest() {
        CustomerRequest request = new CustomerRequest().firstName("Dan").lastName("Manges").company("Braintree");
        NodeWrapper node = new Http(gateway.getAuthorizationHeader(), gateway.baseMerchantURL(), "1.0.0").post("/customers", request);
        Assert.assertEquals("Dan", node.findString("first-name"));
    }

    @Test
    public void smokeTestPut() {
        CustomerRequest request = new CustomerRequest().firstName("NewName");
        NodeWrapper node = new Http(gateway.getAuthorizationHeader(), gateway.baseMerchantURL(), "1.0.0").put("/customers/131866", request);
        Assert.assertEquals("NewName", node.findString("first-name"));
    }

    @Test
    public void smokeTestDelete() {
        NodeWrapper node = new Http(gateway.getAuthorizationHeader(), gateway.baseMerchantURL(), "1.0.0").post("/customers", new CustomerRequest());
        new Http(gateway.getAuthorizationHeader(), gateway.baseMerchantURL(), "1.0.0").delete("/customers/" + node.findString("id"));
    }
    
    @Test(expected = AuthenticationException.class)
    public void authenticationException() {
        new Http("bad auth", gateway.baseMerchantURL(), "1.0.0").get("/");
    }
    
    @Test
    public void sslCertificateSuccessful() {
        BraintreeGateway testGateway = new BraintreeGateway(Environment.DEVELOPMENT, "test_merchant_id", "test_public_key", "test_private_key");
        Http http = new Http(testGateway.getAuthorizationHeader(), "https://qa-master.braintreegateway.com/merchants/test_merchant_id", "1.0.0");
        http.get("/customers");
    }

    @Test
    public void sslBadCertificate() throws Exception {
        startSSLServer();
        try {
            Http http = new Http(gateway.getAuthorizationHeader(), "https://localhost:9443", "1.0.0");
            http.get("/");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Cert"));
        } finally {
            stopSSLServer();
        }
    }

    private void startSSLServer() throws Exception {
        String fileName = StringUtils.getFullPathOfFile("script/httpsd.rb");
        new ProcessBuilder(fileName, "/tmp/httpsd.pid").start().waitFor();
    }

    private void stopSSLServer() throws IOException {
        String pid = StringUtils.inputStreamToString(new FileInputStream("/tmp/httpsd.pid"));
        new ProcessBuilder("kill", "-9", pid).start();
    }
}
