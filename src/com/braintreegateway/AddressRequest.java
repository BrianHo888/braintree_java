package com.braintreegateway;

import com.braintreegateway.util.QueryString;

/**
 * Provides a fluent interface to build up requests around {@link Address Addresses}.
 */
public class AddressRequest extends Request {

    private String countryName;
    private String extendedAddress;
    private String firstName;
    private String lastName;
    private String locality;
    private String postalCode;
    private String region;
    private String streetAddress;
    private String company;
    protected String tagName;

    public AddressRequest() {
        this.tagName = "address";
    }
    
    public AddressRequest company(String company) {
        this.company = company;
        return this;
    }

    public AddressRequest countryName(String countryName) {
        this.countryName = countryName;
        return this;
    }

    public AddressRequest extendedAddress(String extendedAddress) {
        this.extendedAddress = extendedAddress;
        return this;
    }

    public AddressRequest firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public AddressRequest lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public AddressRequest locality(String locality) {
        this.locality = locality;
        return this;
    }

    protected String optionsXML() {
        return "";
    }

    public AddressRequest postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public AddressRequest region(String region) {
        this.region = region;
        return this;
    }

    public AddressRequest streetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
        return this;
    }

    public String toQueryString() {
        return toQueryString(this.tagName);
    }

    public String toQueryString(String root) {
        return new QueryString().
            append(parentBracketChildString(root, "first_name"), firstName).
            append(parentBracketChildString(root, "last_name"), lastName).
            append(parentBracketChildString(root, "company"), company).
            append(parentBracketChildString(root, "country_name"), countryName).
            append(parentBracketChildString(root, "extended_address"), extendedAddress).
            append(parentBracketChildString(root, "locality"), locality).
            append(parentBracketChildString(root, "postal_code"), postalCode).
            append(parentBracketChildString(root, "region"), region).
            append(parentBracketChildString(root, "street_address"), streetAddress).
            toString();
    }
    
    @Override
    public String toXML() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("<%s>", this.tagName));
        builder.append(buildXMLElement("firstName", firstName));
        builder.append(buildXMLElement("lastName", lastName));
        builder.append(buildXMLElement("company", company));
        builder.append(buildXMLElement("countryName", countryName));
        builder.append(buildXMLElement("extendedAddress", extendedAddress));
        builder.append(buildXMLElement("locality", locality));
        builder.append(buildXMLElement("postalCode", postalCode));
        builder.append(buildXMLElement("region", region));
        builder.append(buildXMLElement("streetAddress", streetAddress));
        builder.append(optionsXML());
        builder.append(String.format("</%s>", this.tagName));
        return builder.toString();
    }
}
