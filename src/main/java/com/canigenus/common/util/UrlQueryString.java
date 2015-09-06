package com.canigenus.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class UrlQueryString {
	private static final String DEFAULT_ENCODING = "UTF-8";

	  public static String buildQueryString(final LinkedHashMap<String, Object> map) {
	    try {
	      final Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
	      final StringBuilder sb = new StringBuilder(map.size() * 8);
	      while (it.hasNext()) {
	        final Map.Entry<String, Object> entry = it.next();
	        final String key = entry.getKey();
	        if (key != null) {
	          sb.append(URLEncoder.encode(key, DEFAULT_ENCODING));
	          sb.append("=");
	          final Object value = entry.getValue();
	          final String valueAsString = value != null ? URLEncoder.encode(value.toString(), DEFAULT_ENCODING) : "";
	          sb.append(valueAsString);
	          if (it.hasNext()) {
	            sb.append("&");
	          }
	        } else {
	          // Do what you want...for example:
	          assert false : String.format("Null key in query map: %s", map.entrySet());
	        }
	      }
	      return sb.toString();
	    } catch (final UnsupportedEncodingException e) {
	      throw new UnsupportedOperationException(e);
	    }
	  }

	  public static String buildQueryStringMulti1( Map<String, String[]> map)
	  {
		  try {
		      final StringBuilder sb = new StringBuilder(map.size() * 8);
		      for (final Iterator<Entry<String, String[]>> mapIterator = map.entrySet().iterator(); mapIterator.hasNext();) {
		        final Entry<String, String[]> entry = mapIterator.next();
		        final String key = entry.getKey();
		        if (key != null) {
		          final String keyEncoded = URLEncoder.encode(key, DEFAULT_ENCODING);
		          final String[] values = entry.getValue();
		          sb.append(keyEncoded);
		          sb.append("=");
		          if (values != null) {
		            for (int  listIt = 0; listIt<values.length;listIt++) {
		              final Object valueObject = values[listIt];
		              sb.append(valueObject != null ? URLEncoder.encode(valueObject.toString(), DEFAULT_ENCODING) : "");
		              if (listIt+1<values.length) {
		                sb.append("&");
		                sb.append(keyEncoded);
		                sb.append("=");
		              }
		            }
		          }
		          if (mapIterator.hasNext()) {
		            sb.append("&");
		          }
		        } else {
		          // Do what you want...for example:
		          assert false : String.format("Null key in query map: %s", map.entrySet());
		        }
		      }
		      return sb.toString();
		    } catch (final UnsupportedEncodingException e) {
		      throw new UnsupportedOperationException(e);
		    }
	  }
	  public static String buildQueryStringMulti(final LinkedHashMap<String, List<String>> map) {
	    try {
	      final StringBuilder sb = new StringBuilder(map.size() * 8);
	      for (final Iterator<Entry<String, List<String>>> mapIterator = map.entrySet().iterator(); mapIterator.hasNext();) {
	        final Entry<String, List<String>> entry = mapIterator.next();
	        final String key = entry.getKey();
	        if (key != null) {
	          final String keyEncoded = URLEncoder.encode(key, DEFAULT_ENCODING);
	          final List<String> values = entry.getValue();
	          sb.append(keyEncoded);
	          sb.append("=");
	          if (values != null) {
	            for (final Iterator<String> listIt = values.iterator(); listIt.hasNext();) {
	              final Object valueObject = listIt.next();
	              sb.append(valueObject != null ? URLEncoder.encode(valueObject.toString(), DEFAULT_ENCODING) : "");
	              if (listIt.hasNext()) {
	                sb.append("&");
	                sb.append(keyEncoded);
	                sb.append("=");
	              }
	            }
	          }
	          if (mapIterator.hasNext()) {
	            sb.append("&");
	          }
	        } else {
	          // Do what you want...for example:
	          assert false : String.format("Null key in query map: %s", map.entrySet());
	        }
	      }
	      return sb.toString();
	    } catch (final UnsupportedEncodingException e) {
	      throw new UnsupportedOperationException(e);
	    }
	  }

	  public static void main(final String[] args) {
	    {
	      final LinkedHashMap<String, Object> queryItems = new LinkedHashMap<String, Object>();
	      queryItems.put("brand", "C&A");
	      queryItems.put("count", null);
	      queryItems.put("misc", 42);
	      final String buildQueryString = buildQueryString(queryItems);
	      System.out.println(buildQueryString);
	    }
	    {
	      final LinkedHashMap<String, List<String>> queryItems = new LinkedHashMap<String, List<String>>();
	      queryItems.put("usernames", new ArrayList<String>(Arrays.asList(new String[] { "bob", "john" })));
	      queryItems.put("nullValue", null);
	      queryItems.put("misc", new ArrayList<String>(Arrays.asList(new String[] { "1", "2", "3" })));
	      final String buildQueryString = buildQueryStringMulti(queryItems);
	      System.out.println(buildQueryString);
	    }
	  }
}
