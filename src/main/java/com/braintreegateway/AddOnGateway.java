package com.braintreegateway;

import java.util.ArrayList;
import java.util.List;

import com.braintreegateway.util.Http;
import com.braintreegateway.util.NodeWrapper;

public class AddOnGateway {
    private Http http;

    public AddOnGateway(Http http) {
        this.http = http;
    }

    public List<AddOn> all() {
        NodeWrapper node = http.get("/add_ons");

        List<AddOn> addOns = new ArrayList<AddOn>();

        for (NodeWrapper addOnResponse : node.findAll("add-on")) {
            addOns.add(new AddOn(addOnResponse));
        }

        return addOns;
    }
}
