package com.canadainc.common.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUtils
{
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
