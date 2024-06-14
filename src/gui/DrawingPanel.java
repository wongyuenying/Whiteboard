/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
 */

package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import remote.IDrawing;

/**
 * This class is for handling all the drawing actions in the drawing area
 */

public class DrawingPanel extends JPanel implements MouseMotionListener, MouseListener {

	private static final int CANVAS_WIDTH = 750;
	private static final int CANVAS_HEIGHT = 600;
	enum drawingMode {
		FREEHAND, LINE, CIRCLE, OVAL, RECTANGLE, TEXT, ERASER
	};
	private drawingMode currentMode = drawingMode.FREEHAND;
	private JTextField inputField;
	private IDrawing idrawing;
	private BufferedImage image;
	private Graphics2D g2;
	private Path2D.Double freePath;
	private Point currentPoint;
	private Point previousPoint;
	private double width;
	private double height;
	private int startX, startY;
	private double endX, endY;
	private Shape drawing;
	private Color currentColor = Color.BLACK;
	private int brushSize = 3;
	private int textSize = 15;
	private File currentFile = null;

	public DrawingPanel(IDrawing idrawing) throws IOException {
		this.idrawing = idrawing;
		initializeCanvas();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	// Setter for drawing mode
	public void setCurrentMode(drawingMode currentMode) {
		this.currentMode = currentMode;
	}

	// Setter for colours
	public void setCurrentColor(Color color) {
		this.currentColor = color;
		g2.setColor(currentColor);
	}

	// Setting for brush sizes
	public void setBrushSize(int brushSize) {
		this.brushSize = brushSize;
		g2.setStroke(new BasicStroke(brushSize));
	}

	// Initialize the drawing canvas
	public void initializeCanvas() throws IOException {
		image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		//get the most updated canvas from the server
		byte[] initialCanvas = idrawing.getCanvas();
		if (initialCanvas != null) {
			image = ImageIO.read(new ByteArrayInputStream(initialCanvas));
		}
		g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setColor(currentColor);
		g2.setStroke(new BasicStroke(brushSize));
		drawing = null;
	}

	// Receive the updated canvas and repaint it
	public void updateCanvas(byte[] canvas) {
		try {
			image = ImageIO.read(new ByteArrayInputStream(canvas));
			g2 = image.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Send the updated canvas to the server
	private void sendCanvasUpdate() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			idrawing.updateCanvas(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// draw the whole canvas
		Graphics2D g2d = (Graphics2D) g;
		if (image != null) {
			g2d.drawImage(image, 0, 0, this);
		}

		// draw temporary shapes while dragging the mouse
		if (drawing != null && currentMode != drawingMode.ERASER) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(currentColor);
			g2.setStroke(new BasicStroke(brushSize));
			g2.draw(drawing);
		}
	}

	// Empty the canvas and start a new drawing
	public void clearBoard() {
		image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		repaint();
		sendCanvasUpdate();
	}

	// Open a drawing file
	public void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG (*.png)", "png");
		fileChooser.setFileFilter(filter);
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			currentFile = selectedFile;
			try {
				BufferedImage loadedImage = ImageIO.read(selectedFile);
				if (loadedImage != null) {
					
					//Fit the selected png file into the canvas size and draw it out
					image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2d = image.createGraphics();
					g2d.drawImage(loadedImage, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT, null);
					g2d.dispose();
					repaint();
					sendCanvasUpdate();
				} else {
					JOptionPane.showMessageDialog(this, "The file could not be opened.", "Error in opening",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error opening image: " + e.getMessage(), "Error in opening",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// Save the current file, if there is not one, ask to save as a new file
	public void saveFile() {
		if (currentFile != null) {
			try {
				ImageIO.write(image, "PNG", currentFile);
				JOptionPane.showMessageDialog(this, "File saved successfully.", "File Saved",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Save Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			saveAsFile();
		}
	}

	// Save the canvas as a png file to the user's operating system
	public void saveAsFile() {
		JFileChooser window = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		window.setDialogTitle("Choose a directory to save your file");
		window.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG (*.png)", "png");
		window.setFileFilter(filter);

		int userSelection = window.showSaveDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = window.getSelectedFile();

			String fileExtension = "png";
			String filePath = file.getAbsolutePath();

			if (!filePath.toLowerCase().endsWith(fileExtension)) {
				file = new File(filePath + "." + fileExtension);
			}

			try {
				ImageIO.write(image, fileExtension.toUpperCase(), file);
				currentFile = file;
				JOptionPane.showMessageDialog(this, "File was saved successfully.", "File Saved",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// Method for users to create a text input field to enter text
	private void createTextField(int x, int y) {
		if (inputField != null) {
			remove(inputField);
		}
		inputField = new JTextField("Type here", 10);
		inputField.setBounds(x, y - 15, 200, 20);
		add(inputField);
		inputField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (inputField != null) {
					drawText(inputField.getText(), x, y);
					remove(inputField);
					inputField = null;
					repaint();
					sendCanvasUpdate();
				}
			}
		});
		inputField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (inputField != null && !inputField.getText().equals("Type here")) {
					drawText(inputField.getText(), x, y);
					remove(inputField);
					inputField = null;
					repaint();
					sendCanvasUpdate();
				}
			}
		});
		inputField.requestFocusInWindow();
	}

	// Draw the text onto the canvas
	private void drawText(String text, int x, int y) {
		g2.setFont(new Font("TimesRoman", Font.PLAIN, textSize));
		g2.setColor(currentColor);
		g2.drawString(text, x, y);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (currentMode == drawingMode.TEXT) {
			createTextField(e.getX(), e.getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		g2.setColor(currentColor);
		g2.setStroke(new BasicStroke(brushSize));
		startX = e.getX();
		startY = e.getY();
		previousPoint = currentPoint = e.getPoint(); // Initialize the starting point of the shapes

		// Initialize the shape based on the current mode
		switch (currentMode) {

		case FREEHAND:
			freePath = new Path2D.Double();
			freePath.moveTo(startX, startY);
			drawing = freePath;
			break;

		case LINE:
			drawing = new Line2D.Double();
			break;

		case ERASER:
		case CIRCLE:
		case OVAL:
			drawing = new Ellipse2D.Double();
			break;

		case RECTANGLE:
			drawing = new Rectangle2D.Double();
			break;

		case TEXT:
			break;
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		endX = e.getX();
		endY = e.getY();
		double x = Math.min(startX, endX);
		double y = Math.min(startY, endY);
		currentPoint = e.getPoint();
		width = Math.abs(startX - endX);
		height = Math.abs(startY - endY);
		double diameter = Math.max(width, height);
		switch (currentMode) {

		case FREEHAND:
			freePath.lineTo(endX, endY);
			g2.draw(freePath);
			break;

		case LINE:
			((Line2D) drawing).setLine(previousPoint, currentPoint);
			break;

		case CIRCLE:
			((Ellipse2D) drawing).setFrame(x, y, diameter, diameter);
			break;

		case OVAL:
			((Ellipse2D) drawing).setFrame(x, y, width, height);
			break;

		case RECTANGLE:
			((Rectangle2D) drawing).setFrame(x, y, width, height);
			break;

		case ERASER:
			g2.setColor(Color.WHITE);
			g2.fill(new Ellipse2D.Double(endX - brushSize, endY - brushSize, brushSize * 6, brushSize * 6));
			g2.draw(drawing);

			break;

		case TEXT:
			break;
		}

		repaint();

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (drawing != null) {
			g2.draw(drawing);
			
			// Only fill shapes with these drawing modes
			if (currentMode == drawingMode.CIRCLE || currentMode == drawingMode.OVAL
					|| currentMode == drawingMode.RECTANGLE) {
				g2.fill(drawing);
			}
			
			// Reset the drawing shape for the next draw operation
			drawing = null; 
			freePath = null;
		}
		sendCanvasUpdate();
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

}
