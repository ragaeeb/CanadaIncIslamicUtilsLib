package com.canadainc.common.io;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

public class DBUtils
{
	private static final String SUFFIX_NOT_NULL = " NOT NULL";
	private static final String SUFFIX_PRIMARY_KEY = " PRIMARY KEY";


	public static void createTable(Connection c, String table, Collection<String> columns) throws SQLException {
		execStatement(c, "CREATE TABLE IF NOT EXISTS "+table+" ("+StringUtil.join(columns, ",")+")");
	}

	
	public static final void execStatement(Connection c, String query) throws SQLException
	{
		PreparedStatement ps = c.prepareStatement(query);
		ps.execute();
		ps.close();
	}
	

	public static List<String> isolateColumnNames(List<String> columns, String... toRemove)
	{
		//TODO: Bring all the manual usages to use this one

		for (int i = columns.size()-1; i >= 0; i--) {
			columns.set( i, columns.get(i).split(" ")[0] );
		}
		
		columns.removeAll( Arrays.asList(toRemove) );
		return columns;
	}


	public static void cleanUp(String db)
	{
		new File(db).delete();
		new File(db+"-journal").delete();
	}


	public static void setNullInt(int i, int value, PreparedStatement ps) throws SQLException
	{
		if (value == 0) {
			ps.setNull(i, Types.INTEGER);
		} else {
			ps.setInt(i, value);
		}
	}


	/**
	 * Creates a list of columns for a table that cannot be NULL.
	 * @param primary A primary key. If this is <code>null</code> then a primary key is not created.
	 * @param columns All additional mandatory columns for the table.
	 * @return
	 */
	public static List<String> createNotNullColumns(String primary, String... columns)
	{
		ArrayList<String> result = new ArrayList<String>();

		if (primary != null) {
			result.add(primary+SUFFIX_PRIMARY_KEY);
		}

		for (String column: columns) {
			result.add(column+SUFFIX_NOT_NULL);
		}

		return result;
	}


	/**
	 * Appends a list of columns to <code>result</code> that are allowed to be <code>null</code>.
	 * @param result The list of columns to append the results to.
	 * @param columns All additional optional columns.
	 * @return A reference to <code>result</code>.
	 */
	public static List<String> createNullColumns(List<String> result, String... columns)
	{
		for (String column: columns) {
			result.add(column);
		}

		return result;
	}
	
	
	public static List<String> filterNullable(List<String> columns)
	{
		columns = columns.stream().filter(p -> p.endsWith(SUFFIX_NOT_NULL)).collect(Collectors.toList());
		
		for (int i = 0; i < columns.size(); i++) {
			columns.set(i, columns.get(i).split(" ")[0]);
		}
		
		return columns;
	}
	
	
	public static PreparedStatement createInsert(Connection c, String table, List<String> columns) throws SQLException {
		return c.prepareStatement("INSERT INTO "+table+" ("+StringUtils.join(columns,",")+")"+" VALUES "+DBUtils.generatePlaceHolders(columns));
	}
	
	
	public static void attach(Connection c, String file, String name) throws SQLException {
		execStatement(c, "ATTACH DATABASE '"+file+"' AS "+name);
	}


	public static String generatePlaceHolders(Collection<String> columns)
	{
		Collection<String> placeHolders = new ArrayList<String>();
		// TODO: http://stackoverflow.com/questions/1235179/simple-way-to-repeat-a-string-in-java

		for (String s: columns) {
			placeHolders.add("?");
		}

		return "("+StringUtil.join(placeHolders, ",")+")";
	}


	public static void reset(Connection c, String[] tables) throws SQLException
	{
		PreparedStatement ps;
		
		for (String table: tables)
		{
			ps = c.prepareStatement("DELETE FROM "+table);
			ps.executeUpdate();
		}
		c.commit();
		
		c.setAutoCommit(true);
		ps = c.prepareStatement("VACUUM");
		ps.execute();
		c.setAutoCommit(false);
	}
}