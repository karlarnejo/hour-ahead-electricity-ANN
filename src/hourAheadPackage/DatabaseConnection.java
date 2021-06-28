package hourAheadPackage;

import java.sql.Connection;
import java.sql.DriverManager;
public class DatabaseConnection 
{
	private static String url = "jdbc:postgresql://localhost:5432/ANNNew";
	private static String driverName = "org.postgresql.Driver";
	private static String username = "postgres";
	private static String password = "karl";
	private static Connection con;
	
	public static Connection connectDB()
	{
		try{
			Class.forName(driverName);
			con = DriverManager.getConnection(url, username, password);
		
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return con;
	}
		
}
