package org.ocpsoft.urlbuilder;

import junit.framework.Assert;

import org.junit.Test;

public class AddressBuilderTest
{

   @Test
   public void testBuildEverything()
   {
      Assert.assertEquals("http://example.com:8080/search/table?q=query+string#foo",

               Address.begin()
                        .protocol("http")
                        .host("example.com")
                        .port(8080)
                        .path("/{s}/{t}")
                        .set("s", "search")
                        .set("t", "table")
                        .query("q", "query string")
                        .anchor("foo")
                        .toString());
   }

   @Test
   public void testBuildQuery()
   {
      Assert.assertEquals("?q=200",
               Address.begin()
                        .query("q", 200).toString());
   }

   @Test
   public void testBuildQueryMultipleNames()
   {
      Assert.assertEquals("?q=query&e=string",
               Address.begin()
                        .query("q", "query").query("e", "string").toString());
   }

   @Test
   public void testBuildQueryMultipleValues()
   {
      Assert.assertEquals("?q=10&q=20",
               Address.begin()
                        .query("q", 10, 20).toString());
   }

   @Test
   public void testBuildPathSimple()
   {
      Assert.assertEquals("/store/23",
               Address.begin()
                        .path("/store/23").toString());
   }

   @Test
   public void testBuildPathWithOneParameter()
   {
      Assert.assertEquals("/store/23",
               Address.begin()
                        .path("/store/{item}").set("item", 23).toString());
   }

   @Test
   public void testBuildPathWithParameters()
   {
      Assert.assertEquals("/store/23/buy",
               Address.begin()
                        .path("/store/{item}/{action}").set("item", 23).set("action", "buy").toString());
   }

   @Test
   public void testBuildHostAndPath()
   {
      Assert.assertEquals("ocpsoft.org/store/23/buy",
               Address.begin()
                        .host("ocpsoft.org")
                        .path("/store/{item}/{action}").set("item", 23).set("action", "buy").toString());
   }

   @Test
   public void testProtocolAndPort()
   {
      Assert.assertEquals("file://:80",
               Address.begin()
                        .protocol("file")
                        .port(80).toString());
   }

   @Test
   public void testParameterEncoding()
   {
      Assert.assertEquals("http://localhost/a%20b?q=a+b",
               Address.begin()
                        .protocol("http")
                        .host("localhost")
                        .path("/{p}")
                        .set("p", "a b")
                        .query("q", "a b")
                        .toString());
   }

   @Test
   public void testParametersWithoutEncoding()
   {
      Assert.assertEquals("http://localhost/a%20b?q=a+b",
               Address.begin()
                        .protocol("http")
                        .host("localhost")
                        .path("/{p}")
                        .setEncoded("p", "a%20b")
                        .queryEncoded("q", "a+b")
                        .toString());
   }

}
