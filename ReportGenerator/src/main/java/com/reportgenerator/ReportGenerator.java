package com.reportgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;

public class ReportGenerator {

	public static void main(String[] args) throws IOException {

		Connection connection = null;
		try {

			long start = System.currentTimeMillis();

			// the sql server driver string
			Class.forName("net.sourceforge.jtds.jdbc.Driver");

			// the sql server url
			Properties prop = new Properties();
			String path = args[0];

			InputStream configinput = new FileInputStream(path + "/config.properties");
			prop.load(configinput);

			String url = prop.getProperty("url");
			System.out.println("URL=" + url);

			String fileName = args[1];
			String outputFolder = prop.getProperty("outputFolder");
			String cssFile = prop.getProperty(fileName+".css");
			String reportColumnCss=prop.getProperty(fileName+".cols");
			String evenrowcolor=prop.getProperty(fileName+".evenrowcolor");
			String[] cssSplitvalues= reportColumnCss.split(";");
			HashMap<String, String> cssValues = new HashMap<String,String>();
			for (int i = 0; i < cssSplitvalues.length; i++) {
				String value[] = cssSplitvalues[i].split("=");
				cssValues.put(value[0], value[1]);
			}

			InputStream sqlFile = new FileInputStream(path + fileName + ".sql");
			
			InputStream inCssFile = new FileInputStream(path+"/css/" + cssFile + ".css");

            BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFolder + fileName + ".csv"));


			connection = DriverManager.getConnection(url);

			
			Statement stmt = connection.createStatement(); 

			String sql = IOUtils.toString(sqlFile, StandardCharsets.UTF_8);
			System.out.println(sql);

			ResultSet rs = stmt.executeQuery(sql);
			// STEP 5: Extract data from result set
	        final CSVPrinter printer = CSVFormat.EXCEL.withHeader(rs).print(writer);

	        ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			StringBuffer htmlStr = new StringBuffer();
			htmlStr.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/"+cssFile+".css\"/>");
			htmlStr.append("<table class=\""+cssFile+"\">");
			htmlStr.append("<thead>");
			htmlStr.append("<tr>");
			for (int i = 1; i <= columnCount; i++) {
				String style="style='column-width:";
				String columnName=rsmd.getColumnName(i);
				if(cssValues.containsKey(columnName)) {
					style+=cssValues.get(columnName)+";'";
				}else {
					style+=cssValues.get("Default")+";'";

				}
				htmlStr.append("<th "+style+" >"+rsmd.getColumnName(i)+"</th>");
				
			}
			htmlStr.append("</tr>");
			htmlStr.append("</thead>");
			htmlStr.append("<tbody>");

			
			ArrayList<String> value = new ArrayList<String>();
			ArrayList<ArrayList> values = new ArrayList<ArrayList>();

			int counter=0;
			
			while (rs.next()) {
				if(counter%2==0) {
					htmlStr.append("<tr bgcolor=\""+evenrowcolor+"\">");
				}else {
					htmlStr.append("<tr>");
				}
				counter++;
				value = new ArrayList<String>();
				for (int i = 1; i <= columnCount; i++) {
						htmlStr.append("<td>"+rs.getString(i)+"</td>");

					
					value.add(rs.getString(i));
				}
				values.add(value);
				value = null;
				htmlStr.append("</tr>");

			}
			htmlStr.append("</tbody>");
			htmlStr.append("</table>");
			htmlStr.append("<br>");

			System.out.println("Started to write to file..");

			
			printer.printRecords(values);

			printer.flush();
			
			BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(outputFolder +"report.html")));

			//write contents of StringBuffer to a file
			bwr.write(htmlStr.toString());
			
			//flush the stream
			bwr.flush();
			
			//close the stream
			bwr.close();
			
			long end = System.currentTimeMillis();

			System.out.println("finished writing..");

			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.print("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(2);
		}

	}
}