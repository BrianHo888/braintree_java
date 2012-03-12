package com.braintreegateway;

import com.braintreegateway.Webhook;
import com.braintreegateway.util.EnumUtils;
import com.braintreegateway.util.NodeWrapper;

public class WebhookNotification {
	private Subscription subscription;
	private Webhook.Kind kind;
	
	public WebhookNotification(NodeWrapper node) {
		this.kind = EnumUtils.findByName(Webhook.Kind.class, node.findString("kind"));
		
		if (node.findFirst("subject/subscription") != null) {
			this.subscription = new Subscription(node.findFirst("subject/subscription"));
		}
	}

	public Webhook.Kind getKind() {
		return this.kind;
	}

	public Subscription getSubscription() {
		return this.subscription;
	}
}
