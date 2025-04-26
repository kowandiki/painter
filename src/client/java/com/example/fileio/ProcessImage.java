package com.example.fileio;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.example.color.Color;
import com.example.color.Colors;

public class ProcessImage 
{
	
	/**
	 * Take an image and path for instruction output
	 * convert every pixel into a dye, and store that in a csv file
	 * for images larger than 1 tile (32x32px), number each file 1-n.csv
	 * throws IOException if image dimensions are invalid
	 */
	public static void generateInstructions(BufferedImage image, String path) throws IOException
	{
		if (image.getWidth() % 32 != 0 && image.getHeight() % 32 != 0)
			throw new IOException("invalid dimensions");
		
		// go over each pixel, and set the closest dye color to appropriate index in this array
//		Color[][] colors = new Color[image.getHeight()][image.getWidth()];
		Color[][] colors = new Color[image.getWidth()][image.getHeight()];
		
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
//					colors[y][x] = Colors.HexToColor(image.getRGB(x, y) & 0xFFFFFF);
				colors[x][y] = Colors.HexToColor(image.getRGB(x, y) & 0xFFFFFF);
			}
		}
		
		// write everything to file
		// first row, all dyes listed in order of frequency of occurence
		// all subsequent rows should be 32 entries long, with the dye name, with a value of 0-3 
		// for the lightness appended to the end of each entry
		
		int xtile = image.getWidth() / 32;
		int ytile = image.getHeight() / 32;
		
		int counter = 0;
		
		// make directory
		try
		{ 
			new File(path).mkdirs(); 
		}
		catch (Exception e)
		{
			throw new IOException("could not make directory");
		}
		
		
		for (int yt = 0; yt < ytile; yt++)
		{
			for (int xt = 0; xt < xtile; xt++)
			{
				Map<String, Integer> counts = new HashMap<String, Integer>();
				FileWriter writer = new FileWriter(path + "\\" + counter + ".csv");
				
				for (int y = 0; y < 32; y++)
				{
					String line = "";
					
					for (int x = 0; x < 31; x++)
					{
//						String c = colorToString(colors[y + 32 * yt][x + 32 * xt]);
						String c = colorToString(colors[x + 32 * xt][y + 32 * yt]);
						line += c + ",";
						
						if (counts.containsKey(c))
							counts.put(c, counts.get(c) + 1);
						else
							counts.put(c, 1);
					}
					
//					String c = colorToString(colors[y + 32 * yt][31 + 32 * xt]);
					String c = colorToString(colors[31 + 32 * xt][y + 32 * yt]);
					line += c + "\n";
					writer.write(line);
					
					if (counts.containsKey(c))
						counts.put(c, counts.get(c) + 1);
					else
						counts.put(c, 1);
				}
				writer.close();
				
				// create a new file, that has all the color info Nc.csv
				writer = new FileWriter(path + "\\" + counter + "c.csv");
				

		        // Sorting the list based on values
				Map<String,Integer> sorted = sortByValue(counts, false);
				
				for (Map.Entry<String, Integer> entry : sorted.entrySet())
				{
					writer.write(entry.getKey() + "," + entry.getValue() + "\n");
				}

				writer.close();
				
				counter++;
			}
		}
	}
	private static String colorToString(Color c)
	{
		return c.name + (c.lightness + 2);
	}
	
	// code stolen from https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap, final boolean order)
    {
        List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }
	
	public static BufferedImage resizeImage(BufferedImage image, int width, int height, boolean crop)
	{
		if (!crop)
		{
			return (BufferedImage) image.getScaledInstance(width, height,  Image.SCALE_DEFAULT);
		}
		
		BufferedImage newImage = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
		
		Graphics g = newImage.createGraphics();
		
		int x1 = (image.getWidth() - width) / 2;
		int x2 = image.getWidth() - x1;
		
		int y1 = (image.getHeight() - height) / 2;
		int y2 = image.getHeight() - y1;
		
		g.drawImage(image, 0, 0, width, height, x1, y1, x2, y2, null);
		
		g.dispose();
		
		return newImage;
	}
}