package paintApp;
import java.awt.event.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.border.Border;
@SuppressWarnings("Serial")
public class PaintApplication extends JFrame{
    int i = 0;
	JButton brushBut, lineBut, ellipseBut, rectBut, strokeBut, fillBut;
	JSlider transSlider;
	String arr[] = {"Pencil", "Line", "Ellipse", "Rectangle", "Stroke Color", "Fill Color"};
	JLabel transLabel;
	DecimalFormat dec = new DecimalFormat("#.##");
	Graphics2D graphicSettings;
	Border border1;
	
	int currentAction = 1;
	float transparentVal = 1.0f;
	Color strokeColor = Color.BLACK, fillColor = Color.BLACK;
	
public static void main(String[] args) {
	new PaintApplication();
}
	
public PaintApplication() {
	this.setSize(1200, 800);
	this.setTitle("MY PAINT");
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	border1 = BorderFactory.createTitledBorder("Your tools honey : ");
	JPanel buttonPanel = new JPanel();
	buttonPanel.setBorder(border1);
	Box theBox = Box.createHorizontalBox();
	
	brushBut = makeMeButtons("./src/brush.png", 1);
	lineBut = makeMeButtons("./scr/line.png", 2);
	ellipseBut = makeMeButtons("./src/ellipse.png", 3);
	rectBut = makeMeButtons("./src/rectangle.png", 4);
	strokeBut = makeMeColorButtons("./src/stroke.png", 5, true);
	fillBut = makeMeColorButtons("./src/fill.png", 6, false);
	
	transLabel = new JLabel("Transparency fraction : 1");
	transSlider = new JSlider(1, 99, 99);
	
	ListenerForSlider lforSlider = new ListenerForSlider();
	transSlider.addChangeListener(lforSlider);
	
	buttonPanel.add(transLabel);
	buttonPanel.add(transSlider);
	buttonPanel.add(brushBut);
	buttonPanel.add(lineBut);
	buttonPanel.add(ellipseBut);
	buttonPanel.add(rectBut);
	buttonPanel.add(strokeBut);
	buttonPanel.add(fillBut);
	
	theBox.add(buttonPanel);
	
	this.add(new DrawingBoard(), BorderLayout.CENTER);
	this.add(buttonPanel, BorderLayout.SOUTH);
	this.setVisible(true);
	
}
	
		
		public JButton makeMeButtons(String iconFile, final int actionNum) {
			JButton temp = new JButton(arr[actionNum-1]);
			//Icon butIcon = new ImageIcon(iconFile);
			//temp.setIcon(butIcon);
			
						
			temp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(e.getSource() == temp) {
						currentAction = actionNum;
					}
				}

				
			});
			return temp;
		}
	
	
	public JButton makeMeColorButtons(String iconFile , final int actionNum, final Boolean stroke) {
		
		JButton temp = new JButton(arr[actionNum-1]);
		//Icon butIcon = new ImageIcon(iconFile);
		//temp.setIcon(butIcon);
		
		temp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == temp) {
					if(stroke) {
						strokeColor = JColorChooser.showDialog(null, "select the stroke button", Color.BLACK);
					}
					else {
						fillColor = JColorChooser.showDialog(null, "select the fill button", Color.BLACK);
				}}
			}

			
			
		});
		
		
		
		return temp;
	}
	private class DrawingBoard extends JComponent{
		
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		ArrayList<Color> shapeFill = new ArrayList<Color>();
		ArrayList<Float> transPercent = new ArrayList<Float>();
		ArrayList<Color> shapeStroke = new ArrayList<Color>();
		Point drawStart, drawEnd;
		
		public DrawingBoard() {
			
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
				if(currentAction != 1) {
					drawStart = new Point(e.getX(), e.getY());
					drawEnd = drawStart;
					//repaint(); //there is no requirement to call this method right now..because abhi i haven't added the shapes
					//to the arraylists and thus, nothing will be drawn when repaint() call paint() in this case..
				  }
				}
				//ye tab k liye jab shapes bana ni pura.. u are in between and wo transparent sa shape aata h..
				public void mouseReleased(MouseEvent e) {
					if(currentAction != 1) {
						
						Shape aShape = null;
						
						if(currentAction == 2)
					 aShape = drawLine(drawStart.x, drawStart.y, e.getX(), e.getY());
					
						else if(currentAction == 3) aShape = drawEllipse(drawStart.x, drawStart.y, e.getX(), e.getY());
						else if(currentAction == 4)aShape = drawRectangle(drawStart.x, drawStart.y, e.getX(), e.getY());
						shapes.add(aShape);
					shapeFill.add(fillColor);
					shapeStroke.add(strokeColor);
					transPercent.add(transparentVal);
					drawStart = null;
					drawEnd = null;
					repaint();
				}
					}
			}); //end of addMouseListener..
			
			this.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					//graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
					if(currentAction == 1) {
						int x = e.getX();
						int y = e.getY();
						Shape aShape = null;
						strokeColor = fillColor;
						aShape = drawBrush(x, y, 5, 5);
						
						shapes.add(aShape);
						shapeFill.add(fillColor);
						shapeStroke.add(strokeColor);
						transPercent.add(transparentVal);
					}
					drawEnd = new Point(e.getX(), e.getY());
					repaint();
				}
			});
		}
		
		public void paint(Graphics g) {
			Graphics2D graphSettings = (Graphics2D)g;
			graphSettings.setPaint(Color.WHITE);
			graphSettings.fillRect(0, 0, getWidth(), getHeight());
			graphSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			graphSettings.setStroke(new BasicStroke(2));
			//Iterator<Shape> shapeCounter = shapes.iterator();
			Iterator<Float> transCounter = transPercent.iterator();
			Iterator<Color> strokeCounter = shapeStroke.iterator();
			Iterator<Color> fillCounter = shapeFill.iterator();
			
			//graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			
			for(Shape s: shapes) {
				//set composite is for transparency
				graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,  transCounter.next())) ;
				graphSettings.setPaint(strokeCounter.next());
				graphSettings.draw(s);
				graphSettings.setPaint(fillCounter.next());
				graphSettings.fill(s);
				
				
			}
			
			if(drawStart != null && drawEnd != null) {
				graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
				graphSettings.setPaint(Color.GRAY);
				Shape aShape = null;
				
				if(currentAction == 2)
				 aShape = drawLine(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
				
				else if(currentAction == 3)aShape = drawEllipse(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
				else if(currentAction == 4)aShape = drawRectangle(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
				graphSettings.draw(aShape);
				graphSettings.fill(aShape);
				//shapes.add(aShape);
				//shapeStroke.add(strokeColor);
				//shapeFill.add(fillColor);
				
				
			}
		}
	}
		 private Rectangle2D.Float drawRectangle(int x1, int y1, int x2, int y2){
			int x = Math.min(x1,  x2);
			int y = Math.min(y1,  y2);
			int width = Math.abs(x1-x2);
			int height = Math.abs(y1-y2);
			
			return new Rectangle2D.Float(x,  y,  width,  height);
		}

	
	private class ListenerForSlider implements ChangeListener{

		
		public void stateChanged(ChangeEvent e) {
			if(e.getSource() == transSlider) {
				
				transLabel.setText("Transparency fraction : " + dec.format(transSlider.getValue()*0.01));
				if(transSlider.getValue() %10 == 0)
					transLabel.setText(transLabel.getText() + "0");
			
			transparentVal = (float) (transSlider.getValue()*0.01);
			
			}
			
		}
		
	}
	
	private Line2D.Float drawLine(int x1, int y1, int x2, int y2){
		int x = Math.min(x1,  y1);
		int y = Math.min(y1,  y2);
		int height = Math.abs(y1-y2);
		int width = Math.abs(x1-x2);
		return new Line2D.Float(x1, y1, x2, y2);
		
	}
	
	private Ellipse2D.Float drawEllipse(int x1, int y1, int x2,int y2){
		int x = Math.min(x1, y1);
		int y = Math.min(y1, y2);
		int height = Math.abs(y1-y2);
		int width = Math.abs(x1-x2);
		return new Ellipse2D.Float(x,y, width, height);
	}
	
	private Ellipse2D.Float drawBrush(int a, int b, int c, int d){
		return new Ellipse2D.Float(a, b, c, d);
	}
}


















