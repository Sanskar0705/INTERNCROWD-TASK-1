package com.jdbc.surveysystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class onlineSurvey {
	private static Connection connection = null;
	private static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		onlineSurvey onlinesurvey = new onlineSurvey();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String dbURL = "jdbc:mysql://localhost:3306/surveyjdbc";
			String username ="root";
			String password = "rootp";
			connection = DriverManager.getConnection(dbURL,username,password);
			while(true)
			{
			System.out.println("\nWELCOME TO ONLINE SURVEY SYSTEM\n");
			System.out.println("1. CREATE SURVEY");
			System.out.println("2. TAKE SURVEY");
			System.out.println("3. DISPLAY ALL SURVEYS");
			System.out.println("4. DELETE A SURVEY");
			System.out.println("5. EXIT\n");
			System.out.print("Enter your Choice : ");
			int choice = Integer.parseInt(scanner.nextLine());
			
			switch(choice)
			{
			case 1:
				onlinesurvey.CreateSurvey();
				break;
			case 2:
				onlinesurvey.TakeSurvey();
				break;
			case 3:
				onlinesurvey.DisplaySurvey();
				break;
			case 4:
				onlinesurvey.DeleteSurvey();
				break;
			case 5:
				System.out.println("\n\nTHANK YOU FOR USING OUR SURVEY APPLICATION!");
				System.exit(0);
			default:
				break;
			}
			}
		}
		catch(Exception e){
			throw new RuntimeException("Something went wrong");
		}
	}
	
	public void CreateSurvey() throws SQLException
	{
		System.out.println("..........................................REGISTER............................................\n");
		String sql ="insert into logindb (surveyid, lusername, lpassword, surveyname) values (?,?,?,?)";
		
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		String randomId = "SID0";
		for(int i=0; i<7;i++)
		{
			randomId += randomCharacter();
		}
		preparedStatement.setString(1,randomId);
		System.out.print("ENTER USERNAME :      ");
		preparedStatement.setString(2,scanner.nextLine());
		System.out.print("\nCREATE PASSWORD :      ");
		preparedStatement.setString(3, scanner.nextLine());
		System.out.print("\nENTER YOUR SURVEY NAME :      ");
		String surveyName =scanner.nextLine();
		preparedStatement.setString(4,surveyName);
		System.out.println();
		int rows = preparedStatement.executeUpdate();
		
		if(rows > 0) {
			System.out.println("..........................................Registered for your Survey............................................\n");
		}
		
		String sql1 ="insert into surveyresult (surveyid, surveyname, surveytakencount) values (?,?,?)";
		
		PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
		preparedStatement1.setString(1,randomId);
		preparedStatement1.setString(2,surveyName);
		preparedStatement1.setInt(3,0);
		preparedStatement1.executeUpdate();
		
		CreateSurveyQuestions(randomId);
	}
	
	public void TakeSurvey() throws SQLException
	{
		String sName = "";
		String sID ="";
		System.out.print("\nTo Take Survey please entry your survey ID : ");
		sID=scanner.nextLine();
		
		String sql = "select surveyid, surveyname from logindb where surveyid = '"+sID+"'";		
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(sql);
		if(result.next()) {
			sName = result.getString("surveyname");
			System.out.println();
		}
		else {
			System.out.println("Survey does not exist");
		}
		TakeSurvey takesurvey = new TakeSurvey();
		takesurvey.show(sID,sName);
	}
	
	public char randomCharacter()
	{
		int rand = (int)(Math.random()*62);
		if(rand<=9) {
			int ascii = rand+48;
			return (char)(ascii);
	
		}
		else if(rand<=35)
		{
			int ascii = rand+55;
			return (char)(ascii);
		}
		else
		{
			int ascii = rand+61;
			return  (char)(ascii);
		}
	}
	
	public void CreateSurveyQuestions(String randomId) throws SQLException
	{
		String sql = "select surveyid, surveyname from logindb where surveyid = '"+ randomId+"'";		
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(sql);
		if(result.next()) {
			String sID = result.getString("surveyid");
			String sName = result.getString("surveyname");
			System.out.println("Your Survey ID : "+sID+"                          Survey Name : "+sName);
			System.out.println();
		}
		else {
			System.out.println("No Records Found....");
		}
		CreateSurvey createSurvey = new CreateSurvey();
		createSurvey.createYourSurvey(randomId);
	}
	
	public void DisplaySurvey() throws SQLException
	{
		String sql = "Select surveyid, surveyname from surveyresult";
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(sql);
		System.out.println("\n::::::::::::::::::::::::::::::::::::::::::::::::::");
		System.out.println("  SURVEYID                  SURVEYNAME" );
		System.out.println(" ----------                ------------");
		if(result.next()) {
			do
			{
				String surveyid = result.getString("surveyid");
				String surveyname = result.getString("surveyname");
				System.out.println(surveyid+"              "+surveyname);
			}while(result.next());
		}
		else {
			System.out.println("No Records Found....");
		}
		System.out.println("\n::::::::::::::::::::::::::::::::::::::::::::::::::");
	}
	
	public void DeleteSurvey() throws SQLException
	{
		String surveyid="";
		System.out.print("\nTo Delete a Survey ");
		System.out.print("Enter Survey ID : ");
		surveyid=scanner.nextLine();
		
		String sql1 = "delete from logindb where surveyid = (?)";
	    PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
	    preparedStatement1.setString(1,surveyid);
	    preparedStatement1.execute();
	    
	    String sql2 = "delete from surveyresult where surveyid = (?)";
	    PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
	    preparedStatement2.setString(1,surveyid);
	    preparedStatement2.execute();
	    
	    String sql3 = "DROP TABLE "+surveyid.toLowerCase();
		Statement statement = connection.createStatement();
        statement.executeUpdate(sql3);
        
	    System.out.println("\n      YOUR SURVEY "+surveyid+" IS DELETED");
	}
}
