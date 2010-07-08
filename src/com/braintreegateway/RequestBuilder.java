package com.braintreegateway;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.braintreegateway.util.QueryString;
import com.braintreegateway.util.StringUtils;

public class RequestBuilder {

    private String parent;
    private Map<String, String> topLevelElements;
    private Map<String, Object> elements;

    public RequestBuilder(String parent) {
        this.parent = parent;
        this.topLevelElements = new HashMap<String, String>();
        this.elements = new HashMap<String, Object>();
    }
    
    public RequestBuilder addTopLevelElement(String name, String value) {
        topLevelElements.put(name, value);
        return this;
    }

    public RequestBuilder addElement(String name, Object value) {
        elements.put(name, value);
        return this;
    }
    
    public String toQueryString() {
        QueryString queryString = new QueryString();
        for (Map.Entry<String, String> entry : topLevelElements.entrySet()) {
            queryString.append(parentBracketChildString(parent, StringUtils.underscore(entry.getKey())), entry.getValue());
        }
        for (Map.Entry<String, Object> entry : elements.entrySet()) {
            queryString.append(parentBracketChildString(parent, StringUtils.underscore(entry.getKey())), entry.getValue());
        }
        return queryString.toString();
    }
    
    public String toXML() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("<%s>", parent));
        for (Map.Entry<String, Object> entry : elements.entrySet()) {
            builder.append(buildXMLElement(entry.getKey(), entry.getValue()));
        }
        builder.append(String.format("</%s>", parent));
        return builder.toString();
    }
    
    
    protected String buildXMLElement(Object element) {
        return buildXMLElement("", element);
    }

    protected String buildXMLElement(String name, Map<String, String> map) {
        if (map == null)
            return "";
        String xml = "";
        xml += String.format("<%s>", name);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            xml += buildXMLElement(entry.getKey(), entry.getValue());
        }
        xml += String.format("</%s>", name);
        return xml;
    }

    protected String buildXMLElement(String name, Object element) {
        if (element == null) {
            return "";
        } else if (element instanceof Request) {
            return ((Request) element).toXML();
        } else if (element instanceof Calendar) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return String.format("<%s type=\"datetime\">%s</%s>", name, dateFormat.format(((Calendar) element).getTime()), name);
        } else {
            return String.format("<%s>%s</%s>", xmlEscape(name), element == null ? "" : xmlEscape(element.toString()), xmlEscape(name));
        }
    }

    protected Object buildQueryStringElement(String name, String value) {
        if (value != null) {
            try {
                return String.format("%s=%s&", URLEncoder.encode(name, "UTF-8"), URLEncoder.encode(value, "UTF-8"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return "";
        }
    }

    protected String parentBracketChildString(String parent, String child) {
        return String.format("%s[%s]", parent, child);
    }

    protected String wrapInXMLTag(String tagName, String xml) {
        return String.format("<%s>%s</%s>", tagName, xml, tagName);
    }

    protected String wrapInXMLTag(String tagName, String xml, String type) {
        return String.format("<%s type=\"%s\">%s</%s>", tagName, type, xml, tagName);
    }

    protected String xmlEscape(String input) {
        return input.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");
    }
}