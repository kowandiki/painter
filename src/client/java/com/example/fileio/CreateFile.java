package com.example.fileio;

import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.ArrayList;

public class CreateFile {

	public static void toFile(String fileName, ArrayList<String> args, String delimiter) 
	{
		String path = "";
		try {
			File myObj = new File(path + fileName);
			System.out.println(myObj.getAbsolutePath());
			if (myObj.createNewFile()) 
			{
//				System.out.println("File created: " + myObj.getName());
			} else 
			{
//				System.out.println("File already exists.");
			}
		
		} catch (IOException e) 
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
			return;
		}
		// file definitely exists now, so write everything to it
		
		try {
			FileWriter writer = new FileWriter(path + fileName);
			for (int i = 0; i < args.size(); i++)
			{
				writer.write(args.get(i) + delimiter);
			}
			writer.close();
			System.out.println("added content to file: " + path + fileName);
		}
		catch (IOException e)
		{
			return;
		}
	}
	
	public static void toFile(String fileName, ArrayList<String> args)
	{
		CreateFile.toFile(fileName, args, "\n");
	}
}