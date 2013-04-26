package com.braintreegateway;

import com.braintreegateway.util.Http;
import com.braintreegateway.util.NodeWrapper;

import java.util.ArrayList;
import java.util.List;

public class PlanGateway {
    private Http http;

    public PlanGateway(Http http) {
        this.http = http;
    }

    public List<Plan> all() {
        NodeWrapper node = http.get("/plans");

        List<Plan> plans = new ArrayList<Plan>();

        for (NodeWrapper planResponse : node.findAll("plan")) {
            plans.add(new Plan(planResponse));
        }

        return plans;
    }
}
