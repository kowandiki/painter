package com.example.instructions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.example.color.Color;
import com.example.fileio.ReadFromFile;
import com.example.fileio.ReturnBody;
import com.example.gui.GuiBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;

public class InstructionManager {
	
	// current color thats being worked with
	private Color currentColor;
	
	// color currently held by the sponge
	private Color sponge;
	
	// location of each color on grid
	private HashMap<Color, ArrayList<Point>> colors;
	
	private Color[][] colorGrid;
	
//	private ArrayList<Color> prevColors = new ArrayList<Color>();
	private Set<Color> prevColors;// = new HashSet<Color>(colors.size()+1);
	
	public HashSet<String> missingColors = new HashSet<String>();
	
	private int stepOnColor;
	
	public InstructionManager(String directoryPath, int fileNumber)
	{
		// load instructions from file
		ReturnBody r = null;
		try
		{
			// swapping back to this because I don't want this to make files anymore
			r = ReadFromFile.getBodyFromFile(directoryPath, fileNumber);
			
			// gonna try swapping to this for added redundancy
//			r = ReadFromFile.getOrCreateInstructions(directoryPath, fileNumber);
			
		}
		catch(Exception e)
		{
			this.currentColor = null;
			this.sponge = null;
			this.colors = null;
			this.colorGrid = null;
			System.out.println("something went wrong when reading file");
			
			return;
		}
		finally
		{
			if (r == null)
				return;
		}
		
		// populate the colors hashmap
		
		this.colorGrid = r.colors;
		this.colors = r.colorMap;
		this.prevColors = new HashSet<Color>(colors.size()+1);
		// set currentColor
		this.currentColor = getNextMostCommonColor(null);
		GuiBase.backgroundDye = this.currentColor.toString();
		
		// ignore the most common color for the time being, assume player manually set the background
		this.currentColor = getNextMostCommonColor(this.currentColor);
		
		this.stepOnColor = 0;
	}
	
	public void nullSponge()
	{
		this.sponge = null;
	}
	/*
	 * returns an instruction block that tells the client what to do next.
	 * Needs to keep track of everything, and update sponge as needed.
	 * Needs to also only do things with stuff player has in their inventory
	 * decided to use a list because trying to make this work for instructions more complicated than pressing one button
	 * (i.e. placing a dye, darkening it twice and then picking it up with the sponge would've taken a lot more effort)
	 */
	public ArrayList<InstructionBlock> getNextStep(PlayerInventory inv)
	{
		if (this.currentColor == null)
			return null;
		
		// make sure the current color does not need to be changed
		if (stepOnColor >= colors.get(this.currentColor).size())
		{
			this.currentColor = getNextMostCommonColor(this.currentColor);
			this.stepOnColor = 0;
			
			// no new colors to paint, so return null
			if (this.currentColor == null)
			{
				return null;
			}
			
			System.out.println("NEW COLOR: " + this.currentColor.name);
		}
		
		// should handle cases where the canvas is only 1 color and prevent a crash
		// along with other stuff probably
		if (this.currentColor == null)
		{
			return null;
		}

		// make sure the player has the dye in their inventory
		while (!inv.contains(this.currentColor.item.getDefaultStack()))
		{
			System.out.println("player does not have " + this.currentColor.item.getName().getString());
			
			missingColors.add(this.currentColor.item.getName().getString());
			System.out.println(missingColors.contains(this.currentColor.item.getName().getString()));
			
			this.currentColor = getNextMostCommonColor(this.currentColor);
			
			if (this.currentColor == null)
				return null;
			
			this.stepOnColor = 0;
		}
		
		if (this.currentColor == null)
			return null;
		
		// get next grid square to paint on
		Point point = colors.get(this.currentColor).get(stepOnColor);
		
		// create arraylist for instructions
		ArrayList<InstructionBlock> instructions = new ArrayList<InstructionBlock>();
		
		// assume background is set, so don't bother with that rn
		
		// check that the sponge matches the current color
		
		// if it doesn't:
			// check that the current color occurs more than 2 times
			// if it does:
				// set the sponge color
			// if it doesn't, move on
		
		// if it does: move on
		
		if (this.sponge == null || !this.sponge.equals(this.currentColor))
		{
			
			if (this.colors.get(this.currentColor).size() > 2)
			{
				// instructions for setting the sponge
				// left click = set the sponge color
				
				// set orientation
				// already done
				
				// place dye (left click)
					// move needed to active hand
					// set left click field
				instructions.add(new InstructionBlock(this.currentColor.item, point, InstructionBlock.LEFT_CLICK));
				// double up because having issues with the dye not being placed
				instructions.add(new InstructionBlock(this.currentColor.item, point, InstructionBlock.LEFT_CLICK));
				
				// darken/lighten (left click)
				switch(this.currentColor.lightness)
				{
				case -2:
					instructions.add(new InstructionBlock(Items.COAL, point, InstructionBlock.LEFT_CLICK));
				case -1:
					instructions.add(new InstructionBlock(Items.COAL, point, InstructionBlock.LEFT_CLICK));
					break;
				case 1:
					instructions.add(new InstructionBlock(Items.FEATHER, point, InstructionBlock.LEFT_CLICK));
				}
				
				// left click with sponge
				instructions.add(new InstructionBlock(Items.SPONGE, point, InstructionBlock.LEFT_CLICK));
				instructions.add(new InstructionBlock(Items.SPONGE, point, InstructionBlock.LEFT_CLICK));
				
				// update sponge color
				this.sponge = this.currentColor;
			}
			else
			{
				// instructions for just manually painting some stuff
				
				// set orientation
				// already done
				
				// place dye (left click)
				instructions.add(new InstructionBlock(this.currentColor.item, point, InstructionBlock.LEFT_CLICK));
				
				// darken/lighten (left click)
				switch(this.currentColor.lightness)
				{
				case -2:
					instructions.add(new InstructionBlock(Items.COAL, point, InstructionBlock.LEFT_CLICK));
				case -1:
					instructions.add(new InstructionBlock(Items.COAL, point, InstructionBlock.LEFT_CLICK));
					break;
				case 1:
					instructions.add(new InstructionBlock(Items.FEATHER, point, InstructionBlock.LEFT_CLICK));
				}
				
			}
		}
		else
		{
			// sponge is already the correct color
			// so right click on correct grid square
			
			// set orientation
			// already done
			
			// right click with sponge
			instructions.add(new InstructionBlock(Items.SPONGE, point, InstructionBlock.RIGHT_CLICK));
		}
		
		
		this.stepOnColor++;
		return instructions;
	}
	
	/*
	 * Gets the next most common color, color given must already be present
	 * returns the original parameter if its the least common
	 * if passed null, returns the most common color
	 */
	private Color getNextMostCommonColor(Color c)
	{
		int nextLargest = 0;
		int origSize;
		Color nextMaxColor = null;
		
//		if (c != null)
//			System.out.println("+Set contains " + c.name + ": " + this.prevColors.contains(c) + " quantity: " + this.colors.get(c).size());
		
		this.prevColors.add(c);
		
//		if (c != null)
//			System.out.println("-Set contains " + c.name + ": " + this.prevColors.contains(c) + " size: " + this.prevColors.size());
//		
		
		if (c == null)
		{
			origSize = Integer.MAX_VALUE;
		}
		else
		{
//			origSize = 0;
			origSize = this.colors.get(c).size();
		}
		
		for (Entry<Color, ArrayList<Point>> e : this.colors.entrySet())
		{
			// skip if more common
			if (e.getValue().size() > origSize)
				continue;
				
			// skip if original color
			if (e.getKey().equals(c))
				continue;
			
			// skip if already done
			if (this.prevColors.contains(e.getKey()))
				continue;
			
			// same size, can't be bothered to do a not terrible fix
			if (e.getValue().size() == origSize && !this.prevColors.contains(e.getKey()) && !c.equals(e.getKey()))
			{
				nextMaxColor = e.getKey();
				break;
			}
			
			// smaller than current, bigger than everything else so far
			if (e.getValue().size() > nextLargest && !e.getValue().equals(c))
			{
				nextLargest = e.getValue().size();
				nextMaxColor = e.getKey();
				continue;
			}
			
			
		}
		
		if (nextMaxColor == c)
			return null;
		
		return nextMaxColor;
	}
	
	public Color getColorAtPoint(Point p)
	{
		return colorGrid[p.x][p.y];
	}
	
	public void resetToStartOfInstructions()
	{
		this.prevColors = new HashSet<Color>(colors.size()+1);
		// set currentColor
		this.currentColor = getNextMostCommonColor(null);
		
		// ignore the most common color for the time being, assume player manually set the background
		this.currentColor = getNextMostCommonColor(this.currentColor);
		
		this.stepOnColor = 0;
		this.sponge = null;

	}
	public boolean isColorGridNull() { return this.colorGrid == null; }
}