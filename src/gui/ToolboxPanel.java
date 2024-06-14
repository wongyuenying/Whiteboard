/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
 */

package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * This class is for initializing the tool box area of the main application
 */

public class ToolboxPanel extends JPanel {

	private DrawingPanel drawingPanel;
	private JTextField textInputField;
	private JButton selectedColorButton, selectedBrushButton, selectedModeButton;
	private Border defaultBorder = BorderFactory.createEmptyBorder(4, 4, 4, 4);
    private Border selectedColorBorder = BorderFactory.createLineBorder(Color.WHITE, 3);
    private Border selectedBrushBorder = BorderFactory.createLineBorder(Color.BLACK, 2);


	public ToolboxPanel(DrawingPanel drawingPanel) {
		this.drawingPanel = drawingPanel;
		initializeModePanel();
		initializeColorPanel();
		initializeBrushPanel();
		textInputField = new JTextField(10);
		textInputField.setColumns(13);
	}

	// initialize mode buttons to the tool box
	private void initializeModePanel() {
		addModeButton("Freehand", DrawingPanel.drawingMode.FREEHAND);
		addModeButton("Line", DrawingPanel.drawingMode.LINE);
		addModeButton("Circle", DrawingPanel.drawingMode.CIRCLE);
		addModeButton("Oval", DrawingPanel.drawingMode.OVAL);
		addModeButton("Rectangle", DrawingPanel.drawingMode.RECTANGLE);
		addModeButton("Text", DrawingPanel.drawingMode.TEXT);
		addModeButton("Eraser", DrawingPanel.drawingMode.ERASER);
	}

	// initialize colours to the tool box
	private void initializeColorPanel() {
		addColorButton(Color.BLACK);
		addColorButton(new Color(40, 25, 14));
		addColorButton(Color.DARK_GRAY);
		addColorButton(Color.GRAY);
		addColorButton(Color.LIGHT_GRAY);
		addColorButton(new Color(81, 60, 44));
		addColorButton(Color.RED);
		addColorButton(Color.MAGENTA);
		addColorButton(Color.PINK);
		addColorButton(Color.ORANGE);
		addColorButton(Color.YELLOW);
		addColorButton(Color.GREEN);
		addColorButton(new Color(21, 176, 151));
		addColorButton(new Color(58, 124, 65));
		addColorButton(new Color(22, 66, 91));
		addColorButton(Color.BLUE);
		addColorButton(new Color(91, 42, 134));
		addColorButton(new Color(54, 5, 104));
	}

	// initialize brush size to the tool box
	private void initializeBrushPanel() {
		addBrushButton(3);
		addBrushButton(6);
		addBrushButton(9);
	}

	// create mode buttons and set it to the drawing mode when they are pressed
	private void addModeButton(String label, DrawingPanel.drawingMode mode) {
		JButton button = new JButton(label);
		button.setBorderPainted(false); 
		button.setOpaque(true);

		button.addActionListener(e -> {
			if (selectedModeButton != null) {
				selectedModeButton.setBackground(null);
            }
			selectedModeButton = button; 
			selectedModeButton.setBackground(Color.LIGHT_GRAY);
			drawingPanel.setCurrentMode(mode);
		});	
		if(mode == DrawingPanel.drawingMode.FREEHAND) {
			selectedModeButton = button; 
			selectedModeButton.setBackground(Color.LIGHT_GRAY);
			drawingPanel.setCurrentMode(mode);
		}
		add(button);
	}

	// create color buttons and set it to the drawing color when they are pressed
	private void addColorButton(Color color) {
		JButton button = new JButton();
		button.setPreferredSize(new Dimension(25, 25));
		button.setBackground(color);
		button.setOpaque(true);
		button.setBorder(defaultBorder); 

		button.addActionListener(e -> {
			if (selectedColorButton != null) {
                selectedColorButton.setBorder(defaultBorder); 
            }
			selectedColorButton = button; 
			selectedColorButton.setBorder(selectedColorBorder);
			drawingPanel.setCurrentColor(color);
			});
		if(color == Color.BLACK) {
			selectedColorButton = button; 
            selectedColorButton.setBorder(selectedColorBorder);
		}
		add(button);
	}

	// create bursh buttons and set it to the brush size when they are pressed
	private void addBrushButton(int brushSize) {
		JButton button = new JButton();
		button.setIcon(createIcon(brushSize, Color.BLACK));
		button.setPreferredSize(new Dimension(20, 20));
		button.setBorder(defaultBorder); 
		button.addActionListener(e -> {
			if (selectedBrushButton != null) {
                selectedBrushButton.setBorder(defaultBorder); 
            }
			selectedBrushButton = button; 
			selectedBrushButton.setBorder(selectedBrushBorder);
			drawingPanel.setBrushSize(brushSize);
		});
		
		if(brushSize == 3) {
			selectedBrushButton = button; 
			selectedBrushButton.setBorder(selectedBrushBorder);
		}
		add(button);
	}
	
	// create the bursh icon for the brushes
	private ImageIcon createIcon(int diameter, Color color) {
	    int imageSize = diameter + 4; 
	    BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = image.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2.setColor(color);
	    g2.fillOval(1, 1, diameter, diameter);  
	    g2.dispose();
	    return new ImageIcon(image);
	}

}
