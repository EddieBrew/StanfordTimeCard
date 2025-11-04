/*     */ package com.StanfordTimeCard.StanfordTimeCard;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.io.IOException;

/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PicturePanel
/*     */   extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = -4647310868286988256L;
/*     */   private BufferedImage img;
/*     */   private BufferedImage scaled;
/*  47 */   private final String BACKGROUND_PIC = "mesep_alt.jpg";
/*     */ 
/*     */ 
/*     */   
/*     */   public PicturePanel() {
/*     */     try {
/*  53 */       setBackground(ImageIO.read(new File("mesep_alt.jpg")));
/*  54 */     } catch (IOException e1) {
/*     */       
/*  56 */       JOptionPane.showMessageDialog(null, "Page Load Fault: Can not find background image");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/*  64 */     JFrame frame = new JFrame();
/*     */     
/*  66 */     frame.setTitle("Home Improvement Tasks Completed");
/*  67 */     frame.getContentPane().setLayout(new BorderLayout());
/*  68 */     frame.getContentPane().add(new PicturePanel());
/*  69 */     frame.pack();
/*  70 */     frame.setVisible(true);
/*  71 */     frame.setResizable(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dimension getPreferredSize() {
/*  80 */     return (this.img == null) ? super.getPreferredSize() : new Dimension(this.img.getWidth(), this.img.getHeight());
/*     */   }
/*     */   
/*     */   public void setBackground(BufferedImage value) {
/*  84 */     if (value != this.img) {
/*  85 */       this.img = value;
/*  86 */       repaint();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void invalidate() {
/*  92 */     super.invalidate();
/*  93 */     if (getWidth() > this.img.getWidth() || getHeight() > this.img.getHeight()) {
/*  94 */       this.scaled = getScaledInstanceToFill(this.img, getSize());
/*     */     } else {
/*  96 */       this.scaled = this.img;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void paintComponent(Graphics g) {
/* 102 */     super.paintComponent(g);
/* 103 */     if (this.scaled != null) {
/* 104 */       int x = (getWidth() - this.scaled.getWidth()) / 2;
/* 105 */       int y = (getHeight() - this.scaled.getHeight()) / 2;
/* 106 */       g.drawImage(this.scaled, x, y, this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static BufferedImage getScaledInstanceToFill(BufferedImage img, Dimension size) {
/* 112 */     double scaleFactor = getScaleFactorToFill(img, size);
/*     */     
/* 114 */     return getScaledInstance(img, scaleFactor);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static double getScaleFactorToFill(BufferedImage img, Dimension size) {
/* 120 */     double dScale = 1.0D;
/*     */     
/* 122 */     if (img != null) {
/*     */       
/* 124 */       int imageWidth = img.getWidth();
/* 125 */       int imageHeight = img.getHeight();
/*     */       
/* 127 */       double dScaleWidth = getScaleFactor(imageWidth, size.width);
/* 128 */       double dScaleHeight = getScaleFactor(imageHeight, size.height);
/*     */       
/* 130 */       dScale = Math.max(dScaleHeight, dScaleWidth);
/*     */     } 
/*     */ 
/*     */     
/* 134 */     return dScale;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static double getScaleFactor(int iMasterSize, int iTargetSize) {
/* 140 */     double dScale = iTargetSize / iMasterSize;
/*     */     
/* 142 */     return dScale;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor) {
/* 147 */     return getScaledInstance(img, dScaleFactor, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor, Object hint, boolean bHighQuality) {
/* 155 */     BufferedImage imgScale = img;
/*     */     
/* 157 */     int iImageWidth = (int)Math.round(img.getWidth() * dScaleFactor);
/* 158 */     int iImageHeight = (int)Math.round(img.getHeight() * dScaleFactor);
/* 159 */     if (dScaleFactor <= 1.0D) {
/* 160 */       imgScale = getScaledDownInstance(img, iImageWidth, iImageHeight, hint, bHighQuality);
/*     */     } else {
/* 162 */       imgScale = getScaledUpInstance(img, iImageWidth, iImageHeight, hint, bHighQuality);
/*     */     } 
/*     */     
/* 165 */     return imgScale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static BufferedImage getScaledDownInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
/* 174 */     int type = (img.getTransparency() == 1) ? 
/* 175 */       1 : 2;
/*     */     
/* 177 */     BufferedImage ret = img;
/* 178 */     if (targetHeight > 0 || targetWidth > 0) {
/*     */       int w; int h;
/* 180 */       if (higherQuality) {
/*     */ 
/*     */ 
/*     */         
/* 184 */         w = img.getWidth();
/* 185 */         h = img.getHeight();
/*     */       }
/*     */       else {
/*     */         
/* 189 */         w = targetWidth;
/* 190 */         h = targetHeight;
/*     */       } 
/*     */       
/*     */       do {
/* 194 */         if (higherQuality && w > targetWidth) {
/* 195 */           w /= 2;
/* 196 */           if (w < targetWidth) {
/* 197 */             w = targetWidth;
/*     */           }
/*     */         } 
/*     */         
/* 201 */         if (higherQuality && h > targetHeight) {
/* 202 */           h /= 2;
/* 203 */           if (h < targetHeight) {
/* 204 */             h = targetHeight;
/*     */           }
/*     */         } 
/*     */         
/* 208 */         BufferedImage tmp = new BufferedImage(Math.max(w, 1), Math.max(h, 1), type);
/* 209 */         Graphics2D g2 = tmp.createGraphics();
/* 210 */         g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
/* 211 */         g2.drawImage(ret, 0, 0, w, h, null);
/* 212 */         g2.dispose();
/*     */         
/* 214 */         ret = tmp;
/* 215 */       } while (w != targetWidth || h != targetHeight);
/*     */     } else {
/* 217 */       ret = new BufferedImage(1, 1, type);
/*     */     } 
/* 219 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static BufferedImage getScaledUpInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
/* 228 */     int w, h, type = 2;
/*     */     
/* 230 */     BufferedImage ret = img;
/*     */     
/* 232 */     if (higherQuality) {
/*     */ 
/*     */ 
/*     */       
/* 236 */       w = img.getWidth();
/* 237 */       h = img.getHeight();
/*     */     }
/*     */     else {
/*     */       
/* 241 */       w = targetWidth;
/* 242 */       h = targetHeight;
/*     */     } 
/*     */     
/*     */     while (true) {
/* 246 */       if (higherQuality && w < targetWidth) {
/* 247 */         w *= 2;
/* 248 */         if (w > targetWidth) {
/* 249 */           w = targetWidth;
/*     */         }
/*     */       } 
/*     */       
/* 253 */       if (higherQuality && h < targetHeight) {
/* 254 */         h *= 2;
/* 255 */         if (h > targetHeight) {
/* 256 */           h = targetHeight;
/*     */         }
/*     */       } 
/*     */       
/* 260 */       BufferedImage tmp = new BufferedImage(w, h, type);
/* 261 */       Graphics2D g2 = tmp.createGraphics();
/* 262 */       g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
/* 263 */       g2.drawImage(ret, 0, 0, w, h, null);
/* 264 */       g2.dispose();
/*     */       
/* 266 */       ret = tmp;
/* 267 */       tmp = null;
/*     */       
/* 269 */       if (w == targetWidth && h == targetHeight)
/* 270 */         return ret; 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\rober\OneDrive\Documents\MyApplications\StanfordTimeCard\stanfordTimecard.jar!\com\stanfordtimecard\maven\timecard\PicturePanel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */