package com.example.fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.example.FabricPainter;
import com.example.color.Color;
import com.example.color.Colors;
import com.example.instructions.Point;

public class ReadFromFile {
	
	
	/**
	 * Give a path to an image or directory, will generate the instructions if needed.
	 * 
	 * Returns the instructions for the given fileNumber. may return null if something goes wrong.
	 * 
	 * throws IOException if invalid file, or InvalidFileNumberException if invalid fileNumber
	 */
	public static ReturnBody getOrCreateInstructions(String path, int fileNumber) throws InvalidFileNumberException, IOException
	{
		
		if (fileNumber < 0)
			throw new InvalidFileNumberException("invalid number: " + fileNumber + " is too small");
		
		// check path is a directory, or an image
		
		try
		{
			File file = new File(path);
			
			if (file.isDirectory())
			{
				try
				{
					System.out.println("dir");
					return getBodyFromFile(path, fileNumber);
				}
				catch (FileNotFoundException e)
				{
					// didn't give an image, directory doesn't contain instructions
					// so give up I guess
					System.out.println("image not found");
					return null;
				}
			}
			
			System.out.println("passed trycatch");
			
			// file is a file, not a directory
			// check if there is a directory, and if so, see if it contains instructions
			String fileName = file.getName();
			int index = fileName.lastIndexOf('.');
			fileName = fileName.substring(0, index == -1 ? fileName.length() - 1 : index);
			
//			String dirPath = path.replaceFirst(file.getName(), fileName);
//			String dirPath = path + "_InstructionSet";
			String dirPath = getDirPath(path);
//			System.out.println(dirPath + " - " + path + " - " + file.getName());
			try
			{
				return getBodyFromFile(dirPath, fileNumber);
				
			}
			catch (FileNotFoundException e)
			{
				System.out.println("failed try");
				// if there isn't, or contains no instructions, create directory/instructions
				// instructions dont exist, so make them
				
				new File(dirPath).mkdir();
				
				try
				{
					ProcessImage.generateInstructions(ImageIO.read(new File(path)), dirPath);
					try
					{
						return getBodyFromFile(dirPath, fileNumber);
					}
					catch (FileNotFoundException notFoundErr)
					{
						// fileNumber is too big
						throw new InvalidFileNumberException("file number:" + fileNumber + " is either too large or too small");
					}
					
				}
				catch (IOException ioErr)
				{
					// not an image/can't be decoded
					System.out.println("io error");
					return null;
				}
			}
			
		}
		catch(IOException e)
		{
			throw new IOException(e);
		}
	}
	
	// read instructions from the file given, generate an array of everything
	// also generate a hashmap for each color and their corresponding points
	// return both
	public static ReturnBody getBodyFromFile(String DirectoryPath, int fileNumber) throws IOException, FileNotFoundException
	{
		FabricPainter.LOGGER.info("getting body from file from this path: " + DirectoryPath);
		FabricPainter.LOGGER.info("^ with fileNumber of: " + fileNumber);
		
		ReturnBody out = new ReturnBody();
		try
		{
			File file = new File(DirectoryPath + File.separator + fileNumber + ".csv");
			
			Scanner reader = new Scanner(file);
			int y = 0;
			
			
			
			while (reader.hasNextLine())
			{
				String data = reader.nextLine();
				
				// ignore any whitespace
				if (data.isBlank())
					continue;
				
				String[] tokens = data.split(",");
				
				if (tokens.length != 32)
				{
					System.out.println("INVALID LENGTH: " + tokens.length);
					reader.close();
					throw new IOException("invalid row length");
				}
				
				Color[] colors = stringArrToColorArr(tokens);
				
				for (int i = 0; i < 32; i++)
				{
//					out.colors[y][i] = colors[i];
					out.colors[i][y] = colors[i];
					
					// see if color is in the map, if not, create a new entry
					if (!out.colorMap.containsKey(colors[i]))
					{
						out.colorMap.put(colors[i], new ArrayList<Point>());
					}
					
					// append color to arraylist of points
//					out.colorMap.get(colors[i]).add(new Point(y,i));
					out.colorMap.get(colors[i]).add(new Point(i,y));

				}
				y++;
			}
			
			reader.close();
		}
		catch(FileNotFoundException e)
		{
			throw new FileNotFoundException("File does not exist");
		}
		catch(IOException e)
		{
			throw e;
		}
//		catch(Exception e)
//		{
//			throw new IOException("unable to read file :(");
//		}
		
		return out;
	}
	
	private static Color[] stringArrToColorArr(String[] tokens) throws IOException
	{
		Color[] colors = new Color[tokens.length];
		for (int i = 0; i < tokens.length; i++)
		{
			colors[i] = Colors.fromString(tokens[i]);
			
			if (colors[i] == null)
				throw new IOException("failed to convert string to color");
		}
		return colors;
	}
	
	private static String getDirPath(String path)
	{
		return path + "_InstructionSet";
	}
	
	/**
	 * give it the path to an image or directory, will return the number of panels. instructions must already exist.
	 * Method is not immune to bad input, so don't intentionally feed it garbage and expect good results
	 * @param path
	 * @return the number of panels, 0 if there are no panels/instructions
	 */
	public static int getNumberOfPanels(String path)
	{
		// see if path is a directory,
		// if it is, check for files named n.csv, count the highest number that shows up there?
		
		// if its not a directory, getDirPath(), repeat previous step if directory exists
		// if directory does not exist, return 0
		
		FabricPainter.LOGGER.info("get panels: " + path);
		
		File file = new File(path);
		int maxNum = 0;
		if (file.isDirectory())
		{
			for (final File entry : file.listFiles())
			{
				if (entry.isDirectory())
					continue;
				if (!entry.getName().endsWith(".csv"))
					continue;
				// strip extension, try converting name to a number. if it succeeds, update maxNum if needed
				String fileName = entry.getName();
				int index = fileName.lastIndexOf('.');
				fileName = fileName.substring(0, index == -1 ? fileName.length() - 1 : index);
				
				try
				{
					int num = Integer.parseInt(fileName);
					if (num > maxNum)
						maxNum = num;
				}
				catch (Exception e)
				{
					
				}
			}
			FabricPainter.LOGGER.info("maxNum called, in dir, result: " + maxNum);
			System.out.println(maxNum);
			return maxNum;
		}
		
		String dirPath = getDirPath(path);
		
		file = new File(dirPath);
		if (!file.exists() || !file.isDirectory())
		{
			System.out.println("file dne - " + dirPath);
			return 0;
		}

		// directory exists, check for panels
		// copy pasting code
		
		for (final File entry : file.listFiles())
		{
			if (entry.isDirectory())
				continue;
			if (!entry.getName().endsWith(".csv"))
				continue;
			// strip extension, try converting name to a number. if it succeeds, update maxNum if needed
			String fileName = entry.getName();
			int index = fileName.lastIndexOf('.');
			fileName = fileName.substring(0, index == -1 ? fileName.length() - 1 : index);
			
			try
			{
				int num = Integer.parseInt(fileName);
				
				if (num > maxNum)
					maxNum = num;
			}
			catch (Exception e) {}
		}
		
		FabricPainter.LOGGER.info("maxNum not sure how you reach this: " + maxNum);
		
		return maxNum;
	}
}