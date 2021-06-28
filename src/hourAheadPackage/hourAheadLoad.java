package hourAheadPackage;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationGaussian;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.quick.QuickPropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.platformspecific.j2se.data.SQLNeuralDataSet;


public class hourAheadLoad
{	
	private static Connection con;
	
	//training
	public static String SQL = "";
	//testing
	//public static String SQL = "SELECT load_input, day_of_week, weekend_day, type_of_day, week_num, time, day_date, month, year, ideal_value FROM normalized WHERE (year,month,day_date,time) between (2012,12,1,1) and (2012,12,31, 96) ORDER BY ID";
	//validation
	//public static String SQL = "SELECT load_input, day_of_week, weekend_day, type_of_day, week_num, time, day_date, month, year, ideal_value FROM normalized WHERE (year,month,day_date,time) between (2014,9,30,93) and (2014,9,30, 95) ORDER BY ID";
	public final static int INPUT_SIZE = 9;
	public final static int IDEAL_SIZE = 1;
	public final static String SQL_DRIVER = "org.postgresql.Driver";
	public final static String SQL_URL = "jdbc:postgresql://localhost/ANNNew";
	public final static String SQL_UID = "postgres";
	public final static String SQL_PWD = "karl";
	
	public static double kw_del_sum_min = 0;
	public static double kw_del_sum_max = 0;
	public static double time_raw_min = 0;
	public static double time_raw_max = 0;
	
	public static void main(String args[])
	{	
		//change the sql for normal user to input regular dates.
		//train network. will add customizable params later.
		//train(trainingData());
		//evaluate network
		//evaluate(trainingData());
		Encog.getInstance().shutdown();
	}
	//evaluate results
	public static void evaluate(MLDataSet testSet, String trainedFile, int queryCount)
	{
		timeMinMax();	
		double[] fullMape = new double[queryCount];
		int i = 0;
		double totalCount = 0;
		
		BasicNetwork network = (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(trainedFile));	
		// test the neural network
		mainFrame.setResultTextArea("Neural Network Results:" + "\n");
		for(MLDataPair pair: testSet ) {
			final MLData output = network.compute(pair.getInput());
			mainFrame.setResultTextArea("Time = " + timeDenorm(pair.getInput().getData(5)) + " Predicted = " + output.getData(0) + "      " + " Actual = " + pair.getIdeal().getData(0) + "      " + " MAPE = " + mapeEach(pair.getIdeal().getData(0), output.getData(0)) + "\n");
			fullMape[i] = mapeEach(pair.getIdeal().getData(0), output.getData(0));
			i++;
		}
		
		for(int o = 0; o < queryCount; o++)
		{	
			totalCount += fullMape[o];			
		}
		mainFrame.setResultTextArea("\n" + "MAPE: " + (totalCount/queryCount));
	}
	//mape
	public static double mapeEach(double actual, double forecast)
	{
		double mape = 0;
		
		mape = Math.abs(((actual-forecast)/actual)*100);
		
		return mape;
	}
	//minmax load
	public static void loadMinMax()
	{
		con = DatabaseConnection.connectDB();
		
		try
		{
			//kw_del_sum MIN and MAX
			String query_min_max_kw_del_sum = String.format("SELECT min(kw_del_sum), max(kw_del_sum) from raw");
			Statement s_min_max_kw_del_sum = con.createStatement();
			ResultSet rs_min_max_kw_del_sum = s_min_max_kw_del_sum.executeQuery(query_min_max_kw_del_sum);
			
			//get result of the min max of kw_del_sum
			while(rs_min_max_kw_del_sum.next())
			{
				kw_del_sum_min = rs_min_max_kw_del_sum.getDouble(1);
				kw_del_sum_max = rs_min_max_kw_del_sum.getDouble(2);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//denormalize load
	public static double loadDenorm(double var)
	{
		double final_ans = ((var*(kw_del_sum_max-kw_del_sum_min)) + kw_del_sum_min);
		
		return final_ans;
	}
	//minmax time
	public static void timeMinMax()
	{
		con = DatabaseConnection.connectDB();

		try
		{
			//time_raw MIN and MAX
			String query_min_max_time_raw = String.format("SELECT min(time_raw), max(time_raw) from raw");
			Statement s_min_max_time_raw = con.createStatement();
			ResultSet rs_min_max_time_raw = s_min_max_time_raw.executeQuery(query_min_max_time_raw);
			
			//get result of the min max of time_raw
			while(rs_min_max_time_raw.next())
			{
				time_raw_min = rs_min_max_time_raw.getInt(1);
				time_raw_max = rs_min_max_time_raw.getInt(2);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	//denorm time
	public static int timeDenorm(double var)
	{
		double timeDenormalized = ((var*(time_raw_max-time_raw_min)) + time_raw_min);
		
		return (int) timeDenormalized;
	}
	//predict (no ideal values)
	public static void predict(String trainedFile)
	{
		con = DatabaseConnection.connectDB();
		
		int minutes = 15;
		String word = "First 15 minutes";
		double input1 = 0;
		double input2 = 0;
		double input3 = 0;
		double input4 = 0;
		double input5 = 0;
		double input6 = 0;
		double input7 = 0;
		double input8 = 0;
		double input9 = 0;
		loadMinMax();
		mainFrame.setResultTextArea("Neural Network Results:\n\n");
		
		try
		{
//			String query = String.format("SELECT * FROM (SELECT normalizedid, load_input, day_of_week, weekend_day, type_of_day, week_num, time, day_date, month, year FROM normalized ORDER BY normalizedid DESC LIMIT 1753) AS ordered ORDER BY normalizedid ASC");
			String query = String.format("SELECT * FROM (SELECT normalizedid, load_input, day_of_week, weekend_day, type_of_day, week_num, time, day_date, month, year FROM normalized ORDER BY normalizedid DESC LIMIT 4) AS ordered ORDER BY normalizedid ASC");
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(query);
			
			while(rs.next())
			{
				input1 = rs.getDouble(2);
				input2 = rs.getDouble(3);
				input3 = rs.getDouble(4);
				input4 = rs.getDouble(5);
				input5 = rs.getDouble(6);
				input6 = rs.getDouble(7);
				input7 = rs.getDouble(8);
				input8 = rs.getDouble(9);
				input9 = rs.getDouble(10);
				
				double inputs[][] = {{input1, input2, input3, input4, input5, input6, input7, input8, input9}};
				
				BasicNetwork network = (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(trainedFile));	
				// test the neural network
				for(int i = 0; i < 1; i++ ) {
			        MLData inputData = new BasicMLData(inputs[i]);
					final MLData output = network.compute(inputData);
					mainFrame.setResultTextArea(word + " Predicted Normalized = " + output.getData(0) + "\n");
				}
				
				for(int i = 0; i < 1; i++ ) {
			        MLData inputData = new BasicMLData(inputs[i]);
					final MLData output = network.compute(inputData);
					mainFrame.setResultTextArea(word + " Predicted Denormalized = " + loadDenorm(output.getData(0)) + "\n\n");
				}
				
				minutes += 15;
				if(minutes == 30)
				{
					word = "Second 15 minutes";
				}
				if(minutes == 45)
				{
					word = "Third 15 minutes";
				}
				if(minutes == 60)
				{
					word = "Fourth 15 minutes";
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		Encog.getInstance().shutdown();
	}
	
	//train resilientProp
	public static void trainResilient(MLDataSet trainingSet, int hidden, String activeFnc)
	{
		//basic neural network template. Inputs should'nt have activation functions
		//because it affects data coming from the previous layer and there is no previous layer before the input.
		BasicNetwork network = new BasicNetwork();
		//input layer with 2 neurons.
		//The 'true' parameter means that it should have a bias neuron. Bias neuron affects the next layer.
		network.addLayer(new BasicLayer(null , true, 9));
		//hidden layer with 3 neurons
		//choose if sigmoid or gaussian.
		if(activeFnc.equals("Sigmoid"))
		{
			network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hidden));
			//output layer with 1 neuron
			network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		}
		else if(activeFnc.equals("Gaussian"))
		{
			network.addLayer(new BasicLayer(new ActivationGaussian(), true, hidden));
			//output layer with 1 neuron
			network.addLayer(new BasicLayer(new ActivationGaussian(), false, 1));
		}
		network.getStructure().finalizeStructure() ;
		network.reset();
		
		String Dir = "C:\\Final Eclipse workspace\\Hour_Ahead\\trainedNetworks\\";
		String trainedFileName = "TA_" + popUp.chosenAlgo + "_AF_" + popUp.chosenActiveFnc + "_HN_" + popUp.hiddenNeurons;
		String extensionName = ".eg";
		String finalName = "";

		File toFile = new File(Dir+trainedFileName+extensionName);
		if (toFile.exists()) {
		    // rename string
		    finalName = Dir+trainedFileName+"-Copy"+extensionName;
		} else {
			finalName = Dir+trainedFileName+extensionName;
		}
		
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		int epoch = 1;
		 
		do {
			train.iteration();
			mainFrame.setResultTextArea("Epoch #" + epoch + " Error:" + train.getError() + "\n");
			epoch++;
			// train until error is less than 0.001 or epoch is 20000
		} while((train.getError() > 0.001) && epoch <= 20000); 
		mainFrame.setConsoleTextArea("Saving network" + "\n");
		EncogDirectoryPersistence.saveObject(new File(finalName), network);
		mainFrame.setConsoleTextArea("Saving Done" + "\n");
	}
	
	//train backProp
	public static void trainBackProp(MLDataSet trainingSet, double learningRate, double momentum, int hidden, String activeFnc)
	{
		//basic neural network template. Inputs should'nt have activation functions
		//because it affects data coming from the previous layer and there is no previous layer before the input.
		BasicNetwork network = new BasicNetwork();
		//input layer with 2 neurons.
		//The 'true' parameter means that it should have a bias neuron. Bias neuron affects the next layer.
		network.addLayer(new BasicLayer(null , true, 9));
		//hidden layer with 3 neurons
		//choose if sigmoid or gaussian.
		if(activeFnc.equals("Sigmoid"))
		{
			network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hidden));
			//output layer with 1 neuron
			network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		}
		else if(activeFnc.equals("Gaussian"))
		{
			network.addLayer(new BasicLayer(new ActivationGaussian(), true, hidden));
			//output layer with 1 neuron
			network.addLayer(new BasicLayer(new ActivationGaussian(), false, 1));
		}
		network.getStructure().finalizeStructure() ;
		network.reset();
		
		String Dir = "C:\\Final Eclipse workspace\\Hour_Ahead\\trainedNetworks\\";
		String trainedFileName = "TA_" + popUp.chosenAlgo + "_AF_" + popUp.chosenActiveFnc + "_HN_" + popUp.hiddenNeurons + "_LR_" + popUp.chosenLearningRate + "_M_" + popUp.chosenMomentum;
		String extensionName = ".eg";
		String finalName = "";

		File toFile = new File(Dir+trainedFileName+extensionName);
		if (toFile.exists()) {
		    // rename string
		    finalName = Dir+trainedFileName+"-Copy"+extensionName;
		} else {
			finalName = Dir+trainedFileName+extensionName;
		}
		
		final Backpropagation train = new Backpropagation(network, trainingSet, learningRate, momentum);
		
		int epoch = 1;
		 
		do {
			train.iteration();
			mainFrame.setResultTextArea("Epoch #" + epoch + " Error:" + train.getError() + "\n");
			epoch++;
			// train until error is less than 0.001 or epoch is 20000
		} while((train.getError() > 0.001) && epoch <= 20000); 
		mainFrame.setConsoleTextArea("Saving network" + "\n");
		EncogDirectoryPersistence.saveObject(new File(finalName), network);
		mainFrame.setConsoleTextArea("Saving Done" + "\n");
	}
	
	//train quickProp
	public static void trainQuickProp(MLDataSet trainingSet, double learningRate, int hidden, String activeFnc)
	{
		//basic neural network template. Inputs should'nt have activation functions
				//because it affects data coming from the previous layer and there is no previous layer before the input.
				BasicNetwork network = new BasicNetwork();
				//input layer with 2 neurons.
				//The 'true' parameter means that it should have a bias neuron. Bias neuron affects the next layer.
				network.addLayer(new BasicLayer(null , true, 9));
				//hidden layer with 3 neurons
				//choose if sigmoid or gaussian.
				if(activeFnc.equals("Sigmoid"))
				{
					network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hidden));
					//output layer with 1 neuron
					network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
				}
				else if(activeFnc.equals("Gaussian"))
				{
					network.addLayer(new BasicLayer(new ActivationGaussian(), true, hidden));
					//output layer with 1 neuron
					network.addLayer(new BasicLayer(new ActivationGaussian(), false, 1));
				}
				network.getStructure().finalizeStructure() ;
				network.reset();
				
				String Dir = "C:\\Final Eclipse workspace\\Hour_Ahead\\trainedNetworks\\";
				String trainedFileName = "TA_" + popUp.chosenAlgo + "_AF_" + popUp.chosenActiveFnc + "_HN_" + popUp.hiddenNeurons + "_LR_" + popUp.chosenLearningRate;
				String extensionName = ".eg";
				String finalName = "";

				File toFile = new File(Dir+trainedFileName+extensionName);
				if (toFile.exists()) {
				    // rename string
				    finalName = Dir+trainedFileName+"-Copy"+extensionName;
				} else {
					finalName = Dir+trainedFileName+extensionName;
				}
		
		final QuickPropagation train = new QuickPropagation(network, trainingSet, learningRate);
		final StopTrainingStrategy stop = new StopTrainingStrategy();
		train.addStrategy(stop);
		int epoch = 1;
		 
		do {
			train.iteration();
			mainFrame.setResultTextArea("Epoch #" + epoch + " Error:" + train.getError() + "\n");
			epoch++;
			// train until error is less than 0.001 or epoch is 20000
		} while((train.getError() > 0.001) && epoch <= 20000); 
		mainFrame.setConsoleTextArea("Saving network" + "\n");
		EncogDirectoryPersistence.saveObject(new File(finalName), network);
		mainFrame.setConsoleTextArea("Saving Done" + "\n");
	}
	
	//dataset that will be used for training and evaluation.
	public static MLDataSet trainingData()
	{
		MLDataSet trainingSet = new SQLNeuralDataSet(
				hourAheadLoad.SQL,
				hourAheadLoad.INPUT_SIZE,
				hourAheadLoad.IDEAL_SIZE,
				hourAheadLoad.SQL_DRIVER,
				hourAheadLoad.SQL_URL,
				hourAheadLoad.SQL_UID,
				hourAheadLoad.SQL_PWD);
		
		return trainingSet;
	}
	
	public static void querySelector(int yearFrom, int monthFrom, int dayFrom, int timeFrom, int yearTo, int monthTo, int dayTo, int timeTo)
	{
		SQL = "SELECT load_input, day_of_week, weekend_day, type_of_day, week_num, time, day_date, month, year, ideal_value FROM normalized WHERE (year,month,day_date,time) between (" + year_input(yearFrom) + "," + month_input(monthFrom) + "," + day_input(dayFrom) + "," + time_input(timeFrom) + ") and (" + year_input(yearTo) + "," + month_input(monthTo) + "," + day_input(dayTo) + "," + time_input(timeTo) + ") ORDER BY normalizedid";
	}
	public static int queryCount(int yearFrom, int monthFrom, int dayFrom, int timeFrom, int yearTo, int monthTo, int dayTo, int timeTo)
	{
		int finalCount = 0;
		
		con = DatabaseConnection.connectDB();

		try
		{
			String query = String.format("SELECT count(load_input) FROM normalized WHERE (year,month,day_date,time) between (" + year_input(yearFrom) + "," + month_input(monthFrom) + "," + day_input(dayFrom) + "," + time_input(timeFrom) + ") and (" + year_input(yearTo) + "," + month_input(monthTo) + "," + day_input(dayTo) + "," + time_input(timeTo) + ")");
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(query);
			
			while(rs.next())
			{
				finalCount = rs.getInt(1);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return finalCount;
	}
	
	//this function will return the normalized year that will be used in the sql query for training,testing,validation.
	public static String year_input(int xVar)
	{
		con = DatabaseConnection.connectDB();

		double final_year = 0;
		int year_raw_min = 0;
		int year_raw_max = 0;
		
		//get the min and max of year
		try
		{
			//year_raw MIN and MAX
			String query_min_max_year_raw = String.format("SELECT min(year_raw), max(year_raw) from raw");
			Statement s_min_max_year_raw = con.createStatement();
			ResultSet rs_min_max_year_raw = s_min_max_year_raw.executeQuery(query_min_max_year_raw);
			
			while(rs_min_max_year_raw.next())
			{
				year_raw_min = rs_min_max_year_raw.getInt(1);
				year_raw_max = rs_min_max_year_raw.getInt(2);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		final_year = normalize(xVar, year_raw_min, year_raw_max);
		
		return String.valueOf(final_year);
	}
	
	//this function will return the normalized month that will be used in the sql query for training,testing,validation.
	public static String month_input(int xVar)
	{
		con = DatabaseConnection.connectDB();

		double final_month= 0;
		int month_raw_min = 0;
		int month_raw_max = 0;
		
		//get the min and max of year
		try
		{
			//month_raw MIN and MAX
			String query_min_max_month_raw = String.format("SELECT min(month_raw), max(month_raw) from raw");
			Statement s_min_max_month_raw = con.createStatement();
			ResultSet rs_min_max_month_raw = s_min_max_month_raw.executeQuery(query_min_max_month_raw);
			
			//get result of the min max of month_raw
			while(rs_min_max_month_raw.next())
			{
				month_raw_min = rs_min_max_month_raw.getInt(1);
				month_raw_max = rs_min_max_month_raw.getInt(2);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		final_month = normalize(xVar, month_raw_min, month_raw_max);
		
		return String.valueOf(final_month);
	}
	
	//this function will return the normalized month that will be used in the sql query for training,testing,validation.
	public static String day_input(int xVar)
	{
		con = DatabaseConnection.connectDB();

		double final_day = 0;
		int day_date_raw_min = 0;
		int day_date_raw_max = 0;
		
		//get the min and max of year
		try
		{
			//day_date_raw MIN and MAX
			String query_min_max_day_date_raw = String.format("SELECT min(day_date_raw), max(day_date_raw) from raw");
			Statement s_min_max_day_date_raw = con.createStatement();
			ResultSet rs_min_max_day_date_raw = s_min_max_day_date_raw.executeQuery(query_min_max_day_date_raw);
			
			//get result of the min max of day_date_raw
			while(rs_min_max_day_date_raw.next())
			{
				day_date_raw_min = rs_min_max_day_date_raw.getInt(1);
				day_date_raw_max = rs_min_max_day_date_raw.getInt(2);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		final_day = normalize(xVar, day_date_raw_min, day_date_raw_max);
		
		return String.valueOf(final_day);
	}
	
	//this function will return the normalized month that will be used in the sql query for training,testing,validation.
	public static String time_input(int xVar)
	{
		con = DatabaseConnection.connectDB();

		double final_time= 0;
		int time_raw_min = 0;
		int time_raw_max = 0;
		
		//get the min and max of year
		try
		{
			//time_raw MIN and MAX
			String query_min_max_time_raw = String.format("SELECT min(time_raw), max(time_raw) from raw");
			Statement s_min_max_time_raw = con.createStatement();
			ResultSet rs_min_max_time_raw = s_min_max_time_raw.executeQuery(query_min_max_time_raw);
			
			//get result of the min max of time_raw
			while(rs_min_max_time_raw.next())
			{
				time_raw_min = rs_min_max_time_raw.getInt(1);
				time_raw_max = rs_min_max_time_raw.getInt(2);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		final_time = normalize(xVar, time_raw_min, time_raw_max);
		
		return String.valueOf(final_time);
	}
	
	public static double normalize(double x, double minvar, double maxvar)
	{
		double final_ans;
		
		final_ans = (x-minvar)/(maxvar-minvar);
		
		return final_ans;
	}
}
