package com.braintreegateway;

public class CustomerPager implements Pager<Customer> {

    private CustomerGateway gateway;

    public CustomerPager(CustomerGateway gateway) {
        this.gateway = gateway;
    }

    public PagedCollection<Customer> getPage(int page) {
        return gateway.all(page);
    }

}
