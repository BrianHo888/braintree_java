package com.braintreegateway.util;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

import com.braintreegateway.util.NodeWrapper;

public class NodeWrapperTest {

    @Test
    public void findString() {
        String xml = "<toplevel><foo>bar</foo></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        Assert.assertEquals("bar", node.findString("foo"));
    }

    @Test
    public void findStringWithNoMatchingElements() {
        String xml = "<toplevel><foo>bar</foo></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        Assert.assertEquals(null, node.findString("blah"));
    }

    @Test
    public void findStringWithBustedXPathExpression() {
        String xml = "<toplevel><foo>bar</foo></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        Assert.assertEquals(null, node.findString("$busted"));
    }

    @Test
    public void findDate() {
        String xml = "<toplevel><created-at type=\"date\">2010-02-16</created-at></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        Calendar expected = new GregorianCalendar(2010, 1, 16);
        expected.setTimeZone(TimeZone.getTimeZone("UTC"));
        Assert.assertEquals(expected, node.findDate("created-at"));
    }
    
    @Test
    public void findDateWithNoMatchingElement() {
        String xml = "<toplevel><foo>bar</foo></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        Assert.assertEquals(null, node.findDate("created-at"));
    }

    @Test
    public void findDateTime() {
        String xml = "<toplevel><created-at type=\"datetime\">2010-02-16T16:32:07Z</created-at></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        Calendar expected = Calendar.getInstance();
        expected.setTimeZone(TimeZone.getTimeZone("UTC"));
        expected.set(2010, 1, 16, 16, 32, 7);
        expected.set(Calendar.MILLISECOND, 0);
        Assert.assertEquals(expected, node.findDateTime("created-at"));
    }

    @Test
    public void findDateTimeWithNoMatchingElement() {
        String xml = "<toplevel><foo>bar</foo></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        Assert.assertEquals(null, node.findDateTime("created-at"));
    }

    @Test (expected=RuntimeException.class)
    public void findDateTimeWithMalformedXPath() {
        String xml = "<toplevel><created-at type=\"datetime\">2010-02-16T16:32:07Z</created-at></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        node.findDateTime("$##@busted");
    }

    @Test
    public void findBigDecimal() {
        String xml = "<toplevel><amount>12.59</amount></toplevel>";
        NodeWrapper response = new NodeWrapper(xml);
        Assert.assertEquals(new BigDecimal("12.59"), response.findBigDecimal("amount"));
    }

    @Test
    public void findBigDecimalWithNoMatchingElement() {
        String xml = "<toplevel><amount>12.59</amount></toplevel>";
        NodeWrapper response = new NodeWrapper(xml);
        Assert.assertEquals(null, response.findBigDecimal("price"));
    }

    @Test
    public void findBigDecimalWithMalformedXPath() {
        String xml = "<toplevel><amount>12.59</amount></toplevel>";
        NodeWrapper response = new NodeWrapper(xml);
        Assert.assertEquals(null, response.findBigDecimal("$##@busted"));
    }

    @Test
    public void findInteger() {
        String xml = "<toplevel><foo>4</foo></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        Assert.assertEquals(new Integer(4), node.findInteger("foo"));
    }

    @Test
    public void findIntegerWithNoMatchingElements() {
        String xml = "<toplevel><foo>4</foo></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        Assert.assertEquals(null, node.findInteger("blah"));
    }

    @Test
    public void findIntegerWithBustedXPathExpression() {
        String xml = "<toplevel><foo>4</foo></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        Assert.assertEquals(null, node.findInteger("$busted"));
    }

    @Test
    public void findAll() {
        String xml = "<toplevel><foo><bar><greeting>hi</greeting></bar><bar><greeting>hello</greeting></bar></foo></toplevel>";
        NodeWrapper node = new NodeWrapper(xml);
        List<NodeWrapper> nodes = node.findAll("foo/bar");
        Assert.assertEquals(2, nodes.size());
        Assert.assertEquals("hi", nodes.get(0).findString("greeting"));
        Assert.assertEquals("hello", nodes.get(1).findString("greeting"));
    }

    @Test
    public void findAllWithNoMatchingElement() {
        String xml = "<toplevel></toplevel>";
        Assert.assertTrue(new NodeWrapper(xml).findAll("foo/bar").isEmpty());
    }

    @Test
    public void findAllWithMalformedXPath() {
        String xml = "<toplevel></toplevel>";
        Assert.assertTrue(new NodeWrapper(xml).findAll("foo/bar").isEmpty());
    }

    @Test
    public void findFirst() {
        String xml = "<toplevel><foo><bar><greeting>hi</greeting></bar><bar><greeting>hello</greeting></bar></foo></toplevel>";
        NodeWrapper node = new NodeWrapper(xml).findFirst("foo/bar");
        Assert.assertEquals("hi", node.findString("greeting"));
    }

    @Test
    public void findFirstWithNoMatchingElement() {
        String xml = "<toplevel></toplevel>";
        Assert.assertNull(new NodeWrapper(xml).findFirst("foo/bar"));
    }

    @Test
    public void findFirstWithMalformedXPath() {
        String xml = "<toplevel></toplevel>";
        Assert.assertNull(new NodeWrapper(xml).findFirst("$#busted"));
    }

    public void getElementName() {
        String xml = "<toplevel></toplevel>";
        Assert.assertEquals("toplevel", new NodeWrapper(xml).getElementName());
    }

    public void getElementNameForNestedResponse() {
        String xml = "<toplevel><foo>bar</foo></toplevel>";
        Assert.assertEquals("foo", new NodeWrapper(xml).findFirst("foo").getElementName());
    }
}
