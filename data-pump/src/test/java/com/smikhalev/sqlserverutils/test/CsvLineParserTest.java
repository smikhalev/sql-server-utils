package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.importdata.importer.CsvLineParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CsvLineParserTest {

    private CsvLineParser parser = new CsvLineParser();

    @Test
    public void oneSymbolTest() {
        String inputLine = "a";
        List<String> values = parser.parse(inputLine);
        Assert.assertEquals(values.size(), 1);
        Assert.assertEquals(values.get(0), "a");
    }

    @Test
    public void simpleTest() {
        String inputLine = "abc";
        List<String> values = parser.parse(inputLine);
        Assert.assertEquals(values.size(), 1);
        Assert.assertEquals(values.get(0), "abc");
    }

    @Test
    public void severalValuesTest() {
        String inputLine = "abc,1,sdfj3";
        List<String> values = parser.parse(inputLine);
        Assert.assertEquals(values.size(), 3);
        Assert.assertEquals(values.get(0), "abc");
        Assert.assertEquals(values.get(1), "1");
        Assert.assertEquals(values.get(2), "sdfj3");
    }

    @Test
    public void stringValuesTest() {
        String inputLine = "\"abc\",\"1\",\"sdfj3\"";
        List<String> values = parser.parse(inputLine);
        Assert.assertEquals(values.size(), 3);
        Assert.assertEquals(values.get(0), "abc");
        Assert.assertEquals(values.get(1), "1");
        Assert.assertEquals(values.get(2), "sdfj3");
    }

    @Test
    public void stringValuesWithQuoteTest() {
        String inputLine = "\"abc\",\"1\"\"23\",\"sdfj3\"";
        List<String> values = parser.parse(inputLine);
        Assert.assertEquals(values.size(), 3);
        Assert.assertEquals(values.get(0), "abc");
        Assert.assertEquals(values.get(1), "1\"23");
        Assert.assertEquals(values.get(2), "sdfj3");
    }
}
