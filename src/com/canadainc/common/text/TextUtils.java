package com.canadainc.common.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils
{
	public static final Map<String, Pattern> PATTERNS = new HashMap<String, Pattern>();

	public static ArrayList<String> extractQuotedStringValues(String input)
	{
		return extractQuotedValues(input, "QString, \"", "\")");
	}
	
	
	public static ArrayList<String> extractQuotedValues(String input, String prefix, String suffix)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		int start = 0;
		
		while (start >= 0)
		{
			start = input.indexOf(prefix, start);
			int end = input.indexOf(suffix, start);
	
			if (start >= 0 && end >= 0)
			{
				String term = input.substring( start+prefix.length(), end );
				result.add(term);
			}
			
			start = end;
			start = input.indexOf(prefix, start);
		}
		
		return result;
	}

	public static int findClosingBracket(String text, int openPos)
	{
		int closePos = openPos;
		int counter = 1;
		while (counter > 0) {
			char c = text.charAt(++closePos);
			if (c == '(') {
				counter++;
			}
			else if (c == ')') {
				counter--;
			}
		}
		return closePos;
	}


	public static List<String> getValues(String key, String text, boolean sanitize)
	{
		if ( !PATTERNS.containsKey(key) )
		{
			String toCompile = sanitize ? key.replaceAll("(?=[]\\[+&|!(){}^\"~*?:\\\\-])", "\\\\") + " [^\n]+" : key;
			PATTERNS.put( key, Pattern.compile(toCompile) );
		}
	
		Matcher matcher = PATTERNS.get(key).matcher(text);
		List<String> results = new ArrayList<String>();
	
		while ( matcher.find() ) {
			results.add( text.substring( matcher.start()+key.length(), matcher.end() ).trim() );
		}
	
		return results;
	}


	public static List<String> getEqualValue(String key, String text)
	{
		if ( !PATTERNS.containsKey(key) ) {
			PATTERNS.put( key, Pattern.compile( key+"=[^\n]+") );
		}
		
		Matcher matcher = PATTERNS.get(key).matcher(text);
		List<String> results = new ArrayList<String>();
	
		while ( matcher.find() )
		{
			String match = text.substring( matcher.start()+key.length(), matcher.end() ).trim();
			String[] tokens = match.split("=");
			
			results.add( tokens[1] );
		}
	
		return results;
	}


	public static List<String> getValues(String key, String text) {
		return getValues(key, text, true);
	}


	public static String removeQuotes(String input) {
		return input.substring(1, input.length()-1);
	}

}
