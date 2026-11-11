package com.StanfordTimeCard.StanfordTimeCard;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	  private Image backgroundImage;

	    public ImagePanel(String imagePath) {
	        try {
	            // Load the image file
	            backgroundImage = ImageIO.read(new File(imagePath));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g); // Clears the panel and honors background colors
	        
	        if (backgroundImage != null) {
	            // Dynamically draw the image to match the current panel dimensions
	            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
	        }
	    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
