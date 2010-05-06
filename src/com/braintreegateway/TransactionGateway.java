package com.braintreegateway;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.braintreegateway.Transaction.Type;
import com.braintreegateway.util.Http;
import com.braintreegateway.util.NodeWrapper;
import com.braintreegateway.util.TrUtil;

/**
 * Provides methods to interact with {@link Transaction Transactions}.
 * E.g. sales, credits, refunds, searches, etc.
 * This class does not need to be instantiated directly.
 * Instead, use {@link BraintreeGateway#transaction()} to get an instance of this class:
 * 
 * <pre>
 * BraintreeGateway gateway = new BraintreeGateway(...);
 * gateway.transaction().create(...)
 * </pre>
 * 
 * For more detailed information on {@link Transaction Transactions}, see <a href="http://www.braintreepaymentsolutions.com/gateway/transaction-api" target="_blank">http://www.braintreepaymentsolutions.com/gateway/transaction-api</a>
 */
public class TransactionGateway {

    private Http http;
    private Configuration configuration;

    public TransactionGateway(Http http, Configuration configuration) {
        this.http = http;
        this.configuration = configuration;
    }

    /**
     * Confirms the transparent redirect request and creates a {@link Transaction} based on the parameters
     * submitted with the transparent redirect.
     * @param queryString the queryString of the transparent redirect.
     * @return a {@link Result}.
     */
    public Result<Transaction> confirmTransparentRedirect(String queryString) {
        TransparentRedirectRequest trRequest = new TransparentRedirectRequest(configuration, queryString);
        NodeWrapper node = http.post("/transactions/all/confirm_transparent_redirect_request", trRequest);
        return new Result<Transaction>(node, Transaction.class);
    }

    /**
     * Creates a credit {@link Transaction}.
     * @param request the request.
     * @return a {@link Result}
     */
    public Result<Transaction> credit(TransactionRequest request) {
        NodeWrapper response = http.post("/transactions", request.type(Type.CREDIT));
        return new Result<Transaction>(response, Transaction.class);
    }

    /**
     * Creates transparent redirect data for a credit.
     * @param trData the request.
     * @param redirectURL the redirect URL.
     * @return a String representing the trData.
     */
    public String creditTrData(TransactionRequest trData, String redirectURL) {
        return new TrUtil(configuration).buildTrData(trData.type(Type.CREDIT), redirectURL);
    }
    
    /**
     * Finds a {@link Transaction} by id.
     * @param id the id of the {@link Transaction}.
     * @return the {@link Transaction} or raises a {@link com.braintreegateway.exceptions.NotFoundException}.
     */
    public Transaction find(String id) {
        return new Transaction(http.get("/transactions/" + id));
    }

    /**
     * Refunds all or part of a previous sale {@link Transaction}.
     * @param id the id of the (sale) {@link Transaction} to refund.
     * @return a {@link Result}.
     */
    public Result<Transaction> refund(String id) {
        NodeWrapper response = http.post("/transactions/" + id + "/refund");
        return new Result<Transaction>(response, Transaction.class);
    }
    
    public Result<Transaction> refund(String id, BigDecimal amount) {
        TransactionRequest request = new TransactionRequest().amount(amount);
        NodeWrapper response = http.post("/transactions/" + id + "/refund", request);
        return new Result<Transaction>(response, Transaction.class);
    }

    /**
     * Creates a sale {@link Transaction}.
     * @param request the request.
     * @return a {@link Result}.
     */
    public Result<Transaction> sale(TransactionRequest request) {
        NodeWrapper response = http.post("/transactions", request.type(Type.SALE));
        return new Result<Transaction>(response, Transaction.class);
    }

    /**
     * Creates transparent redirect data for a sale.
     * @param trData the request.
     * @param redirectURL the redirect URL.
     * @return a String representing the trData.
     */
    public String saleTrData(TransactionRequest trData, String redirectURL) {
        return new TrUtil(configuration).buildTrData(trData.type(Type.SALE), redirectURL);
    }

    /**
     * Finds all Transactions that match the query and returns a {@link ResourceCollection} for paging through them starting with t
     * Analogous to "basic search" in the control panel.
     * See: <a href="http://www.braintreepaymentsolutions.com/gateway/transaction-api#searching" target="_blank">http://www.braintr
     * @return a {@link ResourceCollection}.
     */
    public ResourceCollection<Transaction> search(TransactionSearchRequest query) {
        NodeWrapper node = http.post("/transactions/advanced_search_ids", query);
        return new ResourceCollection<Transaction>(new AdvancedTransactionPager(this, query), node);
    }

    List<Transaction> fetchTransactions(TransactionSearchRequest query, List<String> ids) {
        query.ids().in(ids);
        NodeWrapper response = http.post("/transactions/advanced_search", query);

        List<Transaction> items = new ArrayList<Transaction>();
        for (NodeWrapper node : response.findAll("transaction")) {
            items.add(new Transaction(node));
        }
        
        return items;
    }

    /**
     * Submits the transaction with the given id for settlement.
     * @param id of the transaction to submit for settlement.
     * @return a {@link Result}.
     */
    public Result<Transaction> submitForSettlement(String id) {
        return submitForSettlement(id, null);
    }

    /**
     * Submits the transaction with the given id to be settled for the given amount which must be less than or equal to the authorization amount.
     * @param id of the transaction to submit for settlement.
     * @param amount to settle. must be less than or equal to the authorization amount.
     * @return {@link Result}.
     */
    public Result<Transaction> submitForSettlement(String id, BigDecimal amount) {
        TransactionRequest request = new TransactionRequest().amount(amount);
        NodeWrapper response = http.put("/transactions/" + id + "/submit_for_settlement", request);
        return new Result<Transaction>(response, Transaction.class);
    }

    /**
     * Returns the transparent redirect URL for creating a {@link Transaction}.
     * @return a URL as a String.
     */
    public String transparentRedirectURLForCreate() {
        return configuration.baseMerchantURL + "/transactions/all/create_via_transparent_redirect_request";
    }

    /**
     * Voids the transaction with the given id.
     * @param id of the transaction to void.
     * @return {@link Result}.
     */
    public Result<Transaction> voidTransaction(String id) {
        NodeWrapper response = http.put("/transactions/" + id + "/void");
        return new Result<Transaction>(response, Transaction.class);
    }
}
