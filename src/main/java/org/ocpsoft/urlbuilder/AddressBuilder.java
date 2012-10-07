package org.ocpsoft.urlbuilder;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ocpsoft.urlbuilder.util.CaptureType;
import org.ocpsoft.urlbuilder.util.CapturingGroup;
import org.ocpsoft.urlbuilder.util.ParseTools;

public class AddressBuilder implements Address
{
   private volatile CharSequence protocol;
   private volatile CharSequence host;
   private volatile Integer port;
   private volatile CharSequence path;
   private Map<CharSequence, List<Object>> parameters = new LinkedHashMap<CharSequence, List<Object>>();
   private Map<CharSequence, List<Object>> queries = new LinkedHashMap<CharSequence, List<Object>>();

   private AddressBuilder()
   {}

   public static AddressBuilder begin()
   {
      return new AddressBuilder();
   }

   public Address build()
   {
      return this;
   }

   public AddressBuilder protocol(String protocol)
   {
      this.protocol = protocol;
      return this;
   }

   public AddressBuilder host(CharSequence host)
   {
      this.host = host;
      return this;
   }

   public AddressBuilder port(int port)
   {
      this.port = port;
      return this;
   }

   public AddressBuilder path(CharSequence path)
   {
      this.path = path;
      return this;
   }

   public AddressBuilder query(CharSequence name, Object... values)
   {
      this.queries.put(name.toString(), Arrays.asList(values));
      return this;
   }

   public AddressBuilder set(CharSequence name, Object... values)
   {
      this.parameters.put(name.toString(), Arrays.asList(values));
      return this;
   }

   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      if (isSet(protocol))
         result.append(parameterize(protocol)).append("://");

      if (isSet(host))
         result.append(parameterize(host));

      if (isSet(port))
      {
         result.append(":").append(port);
      }

      if (isSet(path))
      {
         if (path.charAt(0) != '/')
            result.append('/');
         result.append(parameterize(path));
      }

      if (!queries.isEmpty())
      {
         result.append('?');
         boolean first = true;
         for (CharSequence name : queries.keySet()) {
            List<Object> values = queries.get(name);
            for (Object value : values) {

               if (!first)
                  result.append('&');
               else
                  first = false;

               result.append(name).append('=').append(value);
            }
         }
      }

      return result.toString();
   }

   private CharSequence parameterize(CharSequence sequence)
   {
      StringBuilder result = new StringBuilder();
      int cursor = 0;
      int lastEnd = 0;
      while (cursor < sequence.length())
      {
         switch (sequence.charAt(cursor))
         {
         case '{':
            result.append(sequence.subSequence(lastEnd, cursor));

            int startPos = cursor;
            CapturingGroup group = ParseTools.balancedCapture(sequence, startPos, sequence.length() - 1,
                     CaptureType.BRACE);
            cursor = group.getEnd();
            lastEnd = group.getEnd() + 1;

            String name = group.getCaptured().toString();

            List<Object> value = parameters.get(name);
            if (value == null || value.isEmpty())
               throw new IllegalStateException("No parameter [" + name + "] was set in the pattern [" + sequence
                        + "]. Call address.set(\"" + name + "\", value); or remove the parameter from the pattern.");

            result.append(value.get(0));

            break;

         default:
            break;
         }

         cursor++;
      }

      if (cursor >= lastEnd)
         result.append(sequence.subSequence(lastEnd, cursor));
      return result;
   }

   private boolean isSet(Integer port)
   {
      return port != null;
   }

   private boolean isSet(CharSequence value)
   {
      return value != null && value.length() > 0;
   }
}