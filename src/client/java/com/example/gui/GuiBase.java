package com.example.gui;

import com.example.actions.Queue;
import com.example.fileio.ReadFromFile;
import com.example.instructions.InstructionManager;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
//import io.github.cottonmc.cotton.gui.widget.WToggleButton;


public class GuiBase extends LightweightGuiDescription {
	
	public static WButton process = null;
	
	public static WTextField filePath = null;
	public static WButton loadFile = null;

	public static WButton panelPlus = null;
	public static WButton panelMinus = null;
	public static WButton loadInstructions = null;
	
	public static int panelNum = 0;
	private static int totalPanels = 0;
	
	public static WGridPanel scrollContents = null;
	public static WScrollPanel scrollPanel = null;
	
	public static String progress = "";
	
	public static String backgroundDye = "";
	
	public static WGridPanel root = null;
	
	public GuiBase()
	{
		if (root != null)
		{
			reRender();	
//			resetAll();
			return;
		}
		resetAll();
	}
	
	private void reRender()
	{
		// everything should stay the same
//		this.root = new WGridPanel();
		setRootPanel(root);
//	    root.setSize(420, 240);
	}
	
	private void reset(boolean selected)
	{
		root = new WGridPanel();
		setRootPanel(root);
		root.setSize(420, 240);
		
		filePath = new WTextField();
		filePath.setMaxLength(512);
		filePath.setText("");
	    root.add(filePath, 1, 1, 20, 1);
		
//		createScrollPanel(root, scroll);
		
		progress = "";
		WDynamicLabel progressLabel = new WDynamicLabel(() -> I18n.translate("%s", progress), -1);
		progressLabel.setColor(0, 0);
		root.add(progressLabel, 11, 1, 5, 2);

	    WLabel disclaimer2 = new WLabel(Text.literal("Load will load instructions, if none exist it will create them"));
	    root.add(disclaimer2, 1,5, 3,3);
	    
	    WLabel panelDisclaimer = new WLabel(Text.literal("the load button for the panel number will"));
	    panelDisclaimer.setVerticalAlignment(VerticalAlignment.BOTTOM);
	    WLabel panelDisclaimer2 = new WLabel(Text.literal("select that panels instructions to paint"));
	    root.add(panelDisclaimer, 6,6);
	    root.add(panelDisclaimer2, 6,7);
	    
	    WDynamicLabel backDye = new WDynamicLabel(() -> I18n.translate("background color: %s", backgroundDye), -1);
	    backDye.setColor(0, 0);
		root.add(backDye, 6, 8, 5, 2);
	    
	    WLabel processingDisclaimer = new WLabel(Text.literal("All image processing must be done beforehand, will convert image as is"));
	    processingDisclaimer.setVerticalAlignment(VerticalAlignment.BOTTOM);
	    root.add(processingDisclaimer, 1, 2);
	    
	    
	    createSelectedPanel(root, selected);
	    
	    // delay (in ticks) between actions
	    WButton increaseTicks = new WButton();
	    increaseTicks.setLabel(Text.literal("+"));
	    increaseTicks.setOnClick(() ->{
			if (Queue.delay < 30)
				Queue.delay++;
		});
	    WButton decreaseTicks = new WButton();
	    decreaseTicks.setLabel(Text.literal("-"));
	    decreaseTicks.setOnClick(() ->{
			if (Queue.delay > 5)
				Queue.delay--;
		});
	    
	    root.add(increaseTicks, 1, 9, 1, 1);
		root.add(decreaseTicks, 2, 9, 1, 1);

		WDynamicLabel DelayLabel = new WDynamicLabel(() -> I18n.translate("tick delay: %d", Queue.delay), -1);
		DelayLabel.setColor(0, 0);
		
	    root.add(DelayLabel, 3, 9, 5, 2);
		
	    root.validate(this); 
		
	}
	
	// sets everything to the state when first gui is first opened
	private void resetAll()
	{
		reset(true);
	}
	
	private void createSelectedPanel(WGridPanel root, boolean reset)
	{
		if (reset)
		{
			panelNum = 0;
			panelPlus = new WButton();
			panelPlus.setLabel(Text.literal("+"));
			panelPlus.setOnClick(() -> {
				if (panelNum < totalPanels)
					panelNum++;
				else
					panelNum = 0;
			});
			
			panelMinus = new WButton();
			panelMinus.setLabel(Text.literal("-"));
			panelMinus.setOnClick(() ->{
				if (panelNum > 0)
					panelNum--;
				else
					panelNum = totalPanels;
			});
			
			loadInstructions = new WButton();
			loadInstructions.setLabel(Text.literal("Load"));
			loadInstructions.setOnClick(() ->{
				try
				{
					// setup the instruction manager
					// need to make it throw errors, but thats an issue for later
					Queue.instMan = new InstructionManager(filePath.getText(), panelNum);
					totalPanels = ReadFromFile.getNumberOfPanels(filePath.getText());
//					ReadFromFile.getOrCreateInstructions(filePath.getText(), panelNum);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			});
		}
		
		WDynamicLabel label = new WDynamicLabel(() -> I18n.translate("panel #: %d", panelNum), -1);
		label.setColor(0, 0);
		
	    root.add(label, 1, 6, 5, 2);
	    
		root.add(panelPlus, 1, 7, 1, 1);
		root.add(panelMinus, 2, 7, 1, 1);
		root.add(loadInstructions, 3, 7, 2, 1);
	}
}