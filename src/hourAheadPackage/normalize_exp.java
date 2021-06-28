package hourAheadPackage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class normalize_exp 
{
	private static Connection con;

	
	public static void main(String args[])
	{
		con = DatabaseConnection.connectDB();
		
		double load_temp = 0;
		int day_of_week_temp = 0;
		int weekend_day_raw_temp = 0;
		int type_of_day_raw_temp = 0;
		int week_num_raw_temp = 0;
		int time_raw_temp = 0;
		int day_date_raw_temp = 0;
		int month_raw_temp = 0;
		int year_raw_temp = 0;
		int id_temp = 0;
		
		double kw_del_sum_min = 0;
		double kw_del_sum_max = 0;
		double day_of_week_raw_min = 0;
		double day_of_week_raw_max = 0;
		double weekend_day_raw_min = 0;
		double weekend_day_raw_max = 0;
		double type_of_day_raw_min = 0;
		double type_of_day_raw_max = 0;
		double week_num_raw_min = 0;
		double week_num_raw_max = 0;
		double time_raw_min = 0;
		double time_raw_max = 0;
		double day_date_raw_min = 0;
		double day_date_raw_max = 0;
		double month_raw_min = 0;
		double month_raw_max = 0;
		double year_raw_min = 0;
		double year_raw_max = 0;
		
		int ideal_first_id = 0;
		double ideal_next_val = 0;
		
		try
		{	
			//query used for the "x" variable in min-max normalization
			String query = String.format("SELECT kw_del_sum, day_of_week_raw, weekend_day_raw, type_of_day_raw, week_num_raw, time_raw, day_date_raw, month_raw, year_raw, rawid from raw ORDER BY rawid");
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(query);
			
			//kw_del_sum MIN and MAX
			String query_min_max_kw_del_sum = String.format("SELECT min(kw_del_sum), max(kw_del_sum) from raw");
			Statement s_min_max_kw_del_sum = con.createStatement();
			ResultSet rs_min_max_kw_del_sum = s_min_max_kw_del_sum.executeQuery(query_min_max_kw_del_sum);
			
			//day_of_week_raw MIN and MAX
			String query_min_max_day_of_week_raw = String.format("SELECT min(day_of_week_raw), max(day_of_week_raw) from raw");
			Statement s_min_max_day_of_week_raw = con.createStatement();
			ResultSet rs_min_max_day_of_week_raw = s_min_max_day_of_week_raw.executeQuery(query_min_max_day_of_week_raw);
			
			//weekend_day_raw MIN and MAX
			String query_min_max_weekend_day_raw = String.format("SELECT min(weekend_day_raw), max(weekend_day_raw) from raw");
			Statement s_min_max_weekend_day_raw = con.createStatement();
			ResultSet rs_min_max_weekend_day_raw = s_min_max_weekend_day_raw.executeQuery(query_min_max_weekend_day_raw);
			
			//type_of_day_raw MIN and MAX
			String query_min_max_type_of_day_raw = String.format("SELECT min(type_of_day_raw), max(type_of_day_raw) from raw");
			Statement s_min_max_type_of_day_raw = con.createStatement();
			ResultSet rs_min_max_type_of_day_raw = s_min_max_type_of_day_raw.executeQuery(query_min_max_type_of_day_raw);

			//week_num_raw MIN and MAX
			String query_min_max_week_num_raw = String.format("SELECT min(week_num_raw), max(week_num_raw) from raw");
			Statement s_min_max_week_num_raw = con.createStatement();
			ResultSet rs_min_max_week_num_raw = s_min_max_week_num_raw.executeQuery(query_min_max_week_num_raw);
			
			//time_raw MIN and MAX
			String query_min_max_time_raw = String.format("SELECT min(time_raw), max(time_raw) from raw");
			Statement s_min_max_time_raw = con.createStatement();
			ResultSet rs_min_max_time_raw = s_min_max_time_raw.executeQuery(query_min_max_time_raw);
			
			//day_date_raw MIN and MAX
			String query_min_max_day_date_raw = String.format("SELECT min(day_date_raw), max(day_date_raw) from raw");
			Statement s_min_max_day_date_raw = con.createStatement();
			ResultSet rs_min_max_day_date_raw = s_min_max_day_date_raw.executeQuery(query_min_max_day_date_raw);
			
			//month_raw MIN and MAX
			String query_min_max_month_raw = String.format("SELECT min(month_raw), max(month_raw) from raw");
			Statement s_min_max_month_raw = con.createStatement();
			ResultSet rs_min_max_month_raw = s_min_max_month_raw.executeQuery(query_min_max_month_raw);
			
			//year_raw MIN and MAX
			String query_min_max_year_raw = String.format("SELECT min(year_raw), max(year_raw) from raw");
			Statement s_min_max_year_raw = con.createStatement();
			ResultSet rs_min_max_year_raw = s_min_max_year_raw.executeQuery(query_min_max_year_raw);
			
			//get result of the min max of kw_del_sum
			while(rs_min_max_kw_del_sum.next())
			{
				kw_del_sum_min = rs_min_max_kw_del_sum.getDouble(1);
				kw_del_sum_max = rs_min_max_kw_del_sum.getDouble(2);
			}
			//get result of the min max of day_of_week_raw
			while(rs_min_max_day_of_week_raw.next())
			{
				day_of_week_raw_min = rs_min_max_day_of_week_raw.getInt(1);
				day_of_week_raw_max = rs_min_max_day_of_week_raw.getInt(2);
			}
			//get result of the min max of weekend_day_raw
			while(rs_min_max_weekend_day_raw.next())
			{
				weekend_day_raw_min = rs_min_max_weekend_day_raw.getInt(1);
				weekend_day_raw_max = rs_min_max_weekend_day_raw.getInt(2);
			}
			//get result of the min max of type_of_day_raw
			while(rs_min_max_type_of_day_raw.next())
			{
				type_of_day_raw_min = rs_min_max_type_of_day_raw.getInt(1);
				type_of_day_raw_max = rs_min_max_type_of_day_raw.getInt(2);
			}
			//get result of the min max of type_of_day_raw
			while(rs_min_max_week_num_raw.next())
			{
				week_num_raw_min = rs_min_max_week_num_raw.getInt(1);
				week_num_raw_max = rs_min_max_week_num_raw.getInt(2);
			}
			//get result of the min max of time_raw
			while(rs_min_max_time_raw.next())
			{
				time_raw_min = rs_min_max_time_raw.getInt(1);
				time_raw_max = rs_min_max_time_raw.getInt(2);
			}
			//get result of the min max of day_date_raw
			while(rs_min_max_day_date_raw.next())
			{
				day_date_raw_min = rs_min_max_day_date_raw.getInt(1);
				day_date_raw_max = rs_min_max_day_date_raw.getInt(2);
			}
			//get result of the min max of month_raw
			while(rs_min_max_month_raw.next())
			{
				month_raw_min = rs_min_max_month_raw.getInt(1);
				month_raw_max = rs_min_max_month_raw.getInt(2);
			}
			//get result of the min max of year_raw
			while(rs_min_max_year_raw.next())
			{
				year_raw_min = rs_min_max_year_raw.getInt(1);
				year_raw_max = rs_min_max_year_raw.getInt(2);
			}
			
			while(rs.next())
			{
				load_temp = rs.getDouble(1);
				day_of_week_temp = rs.getInt(2);
				weekend_day_raw_temp = rs.getInt(3);
				type_of_day_raw_temp = rs.getInt(4);
				week_num_raw_temp = rs.getInt(5);
				time_raw_temp = rs.getInt(6);
				day_date_raw_temp = rs.getInt(7);
				month_raw_temp = rs.getInt(8);
				year_raw_temp = rs.getInt(9);
				id_temp = rs.getInt(10);

				Statement ins = con.createStatement();
				String query_ins = String.format("INSERT INTO normalized(normalizedid, rawid, load_input, day_of_week, weekend_day, type_of_day, week_num, time, day_date, month, year) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", id_temp, id_temp, normalize(load_temp, kw_del_sum_min, kw_del_sum_max), normalize(day_of_week_temp, day_of_week_raw_min, day_of_week_raw_max), normalize(weekend_day_raw_temp, weekend_day_raw_min, weekend_day_raw_max), normalize(type_of_day_raw_temp, type_of_day_raw_min, type_of_day_raw_max), normalize(week_num_raw_temp, week_num_raw_min, week_num_raw_max), normalize(time_raw_temp, time_raw_min, time_raw_max), normalize(day_date_raw_temp, day_date_raw_min, day_date_raw_max), normalize(month_raw_temp, month_raw_min, month_raw_max), normalize(year_raw_temp, year_raw_min, year_raw_max));
				ins.executeUpdate(query_ins);
			}			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		mainFrame.setConsoleTextArea("Setting ideal values...\n");
		//setting ideal values
		try
		{
			String query_ideal = String.format("SELECT normalizedid from normalized WHERE ideal_value is null order by normalizedid");
			Statement s_ideal = con.createStatement();
			ResultSet rs_ideal = s_ideal.executeQuery(query_ideal);
			
			while(rs_ideal.next())
			{
				ideal_first_id = rs_ideal.getInt(1);
				
				int second_id = ideal_first_id+1;
				
				String query_ideal_update = String.format("SELECT load_input from normalized where normalizedid  = '%s'", second_id);
				Statement s_ideal_update = con.createStatement();
				ResultSet rs_ideal_update = s_ideal_update.executeQuery(query_ideal_update);
				
					while(rs_ideal_update.next())
					{
						ideal_next_val = rs_ideal_update.getDouble(1);
					}
					
				Statement st_ideal = con.createStatement();
				String query_deal1 = String.format("UPDATE normalized SET ideal_value = %s WHERE normalizedid = %s", ideal_next_val, ideal_first_id);
				//Executing the update
				st_ideal.executeUpdate(query_deal1);
			}
			
			mainFrame.setConsoleTextArea("Importation of data process complete...\n");
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static double normalize(double x, double minvar, double maxvar)
	{
		double final_ans;
		
		final_ans = (x-minvar)/(maxvar-minvar);
		
		return final_ans;
	}
}
