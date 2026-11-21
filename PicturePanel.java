package com.StanfordTimeCard.StanfordTimeCard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class PicturePanel extends JPanel
{
	private static final long serialVersionUID = -4647310868286988256L;
	private BufferedImage img;
	private BufferedImage scaled;
	private final String BACKGROUND_PIC = "mesep_alt.jpg";



	public PicturePanel() {
		try {
			setBackground(Color.RED);
			setBackground(ImageIO.read(new File("mesep_alt.jpg")));
		} catch (IOException e1) {

			JOptionPane.showMessageDialog(null, "Page Load Fault: Can not find background image");
		} 
	}

	public static void main(String[] args) {


		JFrame frame = new JFrame();

		frame.setTitle("Home Improvement Tasks Completed");
		//frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new PicturePanel());
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);


	}

	public Dimension getPreferredSize() {
		return (this.img == null) ? super.getPreferredSize() : new Dimension(this.img.getWidth(), this.img.getHeight());
	}

	public void setBackground(BufferedImage value) {
		if (value != this.img) {
			this.img = value;
			repaint();
		} 
	}


	public void invalidate() {
		super.invalidate();
		if (getWidth() > this.img.getWidth() || getHeight() > this.img.getHeight()) {
			this.scaled = getScaledInstanceToFill(this.img, getSize());
		} else {
			this.scaled = this.img;
		} 
	}


	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.scaled != null) {
			int x = (getWidth() - this.scaled.getWidth()) / 2;
			int y = (getHeight() - this.scaled.getHeight()) / 2;
			g.drawImage(this.scaled, x, y, this);
		} 
	}


	public static BufferedImage getScaledInstanceToFill(BufferedImage img, Dimension size) {
		double scaleFactor = getScaleFactorToFill(img, size);

		return getScaledInstance(img, scaleFactor);
	}

	public static double getScaleFactorToFill(BufferedImage img, Dimension size) {
		double dScale = 1.0D;

		if (img != null) {

			int imageWidth = img.getWidth();
			int imageHeight = img.getHeight();

			double dScaleWidth = getScaleFactor(imageWidth, size.width);
			double dScaleHeight = getScaleFactor(imageHeight, size.height);

			dScale = Math.max(dScaleHeight, dScaleWidth);
		} 


		return dScale;
	}



	public static double getScaleFactor(int iMasterSize, int iTargetSize) {
		double dScale = iTargetSize / iMasterSize;

		return dScale;
	}


	public static BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor) {
		return getScaledInstance(img, dScaleFactor, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
	}





	protected static BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor, Object hint, boolean bHighQuality) {
		BufferedImage imgScale = img;

		int iImageWidth = (int)Math.round(img.getWidth() * dScaleFactor);
		int iImageHeight = (int)Math.round(img.getHeight() * dScaleFactor);
		if (dScaleFactor <= 1.0D) {
			imgScale = getScaledDownInstance(img, iImageWidth, iImageHeight, hint, bHighQuality);
		} else {
			imgScale = getScaledUpInstance(img, iImageWidth, iImageHeight, hint, bHighQuality);
		} 

		return imgScale;
	}


	protected static BufferedImage getScaledDownInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
		int type = (img.getTransparency() == 1) ? 
				1 : 2;

		BufferedImage ret = img;
		if (targetHeight > 0 || targetWidth > 0) {
			int w; int h;
			if (higherQuality) {     
				w = img.getWidth();
				h = img.getHeight();
			}
			else {

				w = targetWidth;
				h = targetHeight;
			} 

			do {
				if (higherQuality && w > targetWidth) {
					w /= 2;
					if (w < targetWidth) {
						w = targetWidth;
					}
				} 

				if (higherQuality && h > targetHeight) {
					h /= 2;
					if (h < targetHeight) {
						h = targetHeight;
					}
				} 

				BufferedImage tmp = new BufferedImage(Math.max(w, 1), Math.max(h, 1), type);
				Graphics2D g2 = tmp.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
				g2.drawImage(ret, 0, 0, w, h, null);
				g2.dispose();

				ret = tmp;
			} while (w != targetWidth || h != targetHeight);
		} else {
			ret = new BufferedImage(1, 1, type);
		} 
		return ret;
	}


	protected static BufferedImage getScaledUpInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
		int w, h, type = 2;

		BufferedImage ret = img;

		if (higherQuality) {
			w = img.getWidth();
			h = img.getHeight();
		}
		else {

			w = targetWidth;
			h = targetHeight;
		} 

		while (true) {
			if (higherQuality && w < targetWidth) {
				w *= 2;
				if (w > targetWidth) {
					w = targetWidth;
				}
			} 

			if (higherQuality && h < targetHeight) {
				h *= 2;
				if (h > targetHeight) {
					h = targetHeight;
				}
			} 

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
			tmp = null;

			if (w == targetWidth && h == targetHeight)
				return ret; 
		} 
	}
}


