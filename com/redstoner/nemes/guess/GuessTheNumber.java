package com.redstoner.nemes.guess;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 * LogalGamer challenged me okay!?
 * 
 * #Bugs included
 * 
 * @author nemes
 *
 */

public class GuessTheNumber {

	private static volatile boolean isRunning = true;
	private static Texture bg, alonzo, large, small, correct, guess;
	private static volatile int currentDialog = 0;
	private static int number;
	
	public static void main(String[] args) {
		Random rand = new Random();
		Canvas canvas = new Canvas();
		JFrame frame = new JFrame();
		JTextField num = new JTextField();
		BoxLayout layout = new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS);
		WindowListener listener = new WindowListener() {
			
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {
				isRunning = false;
			}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		};
		
		ActionListener textEvent = new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (num.getText().equals("")) {
					currentDialog = 1;
					number = rand.nextInt(100);
					return;
				}
				int i = 0;
				if (num.getText().equals("logalis1337")) {
					currentDialog = 5;
				}
				if (num.getText().equals("exit")) {
					isRunning = false;
					return;
				}
				try{
					i = Integer.parseInt(num.getText());
				} catch(NumberFormatException e) {
					num.setText("");
					return;
				}
				if (i == number) {
					currentDialog = 2;
				}else if (i > number) {
					currentDialog = 3;
				}else if (i < number) {
					currentDialog = 4;
				}
			}
		};
		
		try {
			frame.getContentPane().setLayout(layout);
			frame.setPreferredSize(new Dimension(800, 450));
			frame.setTitle("Guess the Number");
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			canvas.setMinimumSize(new Dimension(800, 430));
			num.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			num.addActionListener(textEvent);
			frame.getContentPane().add(canvas);
			frame.getContentPane().add(num);
			frame.addWindowListener(listener);
			Display.setDisplayMode(new DisplayMode(800, 430));
			Display.setParent(canvas);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			Display.create();
			initOpenGL();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		bg = loadTexture("background");
		alonzo = loadTexture("alonzo");
		large = loadTexture("large");
		small = loadTexture("small");
		correct = loadTexture("correct");
		guess = loadTexture("guess");
		long NEXT_SECOND = System.currentTimeMillis();
		int fps = 0;
		number = rand.nextInt(100);
		currentDialog = 1;
		
		while (isRunning) {
			if (Display.wasResized()) {
				reinitOpenGL();
			}
			render();
			fps++;
			if (NEXT_SECOND < System.currentTimeMillis()) {
				NEXT_SECOND += 1000;
				frame.setTitle("Guess the Number | FPS: " + fps);
				fps = 0;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Display.destroy();
		frame.dispose();
		System.gc();
		System.exit(0);
	}
	
	public static void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, bg.id);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 0);
		addPoint(0, 0);
		GL11.glTexCoord2d(0, 1);
		addPoint(0, 430);
		GL11.glTexCoord2d(1, 1);
		addPoint(800, 430);
		GL11.glTexCoord2d(1, 0);
		addPoint(800, 0);
		GL11.glEnd();
		
		int alonzoX = 300;
		int alonzoY = 130;
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, alonzo.id);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 0);
		addPoint(alonzoX, alonzoY);
		GL11.glTexCoord2d(0, 1);
		addPoint(alonzoX, alonzoY + 200);
		GL11.glTexCoord2d(1, 1);
		addPoint(alonzoX + 150, alonzoY + 200);
		GL11.glTexCoord2d(1, 0);
		addPoint(alonzoX + 150, alonzoY);
		GL11.glEnd();
		
		if (currentDialog > 0) {
			alonzoX += 115;
			alonzoY += -10;
			
			switch (currentDialog) {
			case 1: GL11.glBindTexture(GL11.GL_TEXTURE_2D, guess.id); break;
			case 2: GL11.glBindTexture(GL11.GL_TEXTURE_2D, correct.id); break;
			case 3: GL11.glBindTexture(GL11.GL_TEXTURE_2D, large.id); break;
			case 4: GL11.glBindTexture(GL11.GL_TEXTURE_2D, small.id); break;
			}
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2d(0, 0);
			addPoint(alonzoX, alonzoY);
			GL11.glTexCoord2d(0, 1);
			addPoint(alonzoX, alonzoY + 80);
			GL11.glTexCoord2d(1, 1);
			addPoint(alonzoX + 190, alonzoY + 80);
			GL11.glTexCoord2d(1, 0);
			addPoint(alonzoX + 190, alonzoY);
			GL11.glEnd();
		}
		
		Display.update();
	}
	
	public static void initOpenGL() {
		 GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		 GL11.glMatrixMode(GL11.GL_PROJECTION);
		 GL11.glLoadIdentity();
		 GL11.glOrtho(0.0f, Display.getWidth(), Display.getHeight(), 0.0f, -1.0f, 1.0f);
		 GL11.glMatrixMode(GL11.GL_MODELVIEW);
		 GL11.glEnable(GL11.GL_TEXTURE_2D);
		 GL11.glEnable(GL11.GL_BLEND);
		 GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		 GL11.glEnable(GL11.GL_ALPHA_TEST);
		 GL11.glEnable(GL11.GL_CULL_FACE);
		 GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void reinitOpenGL() {
		 GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		 GL11.glMatrixMode(GL11.GL_PROJECTION);
		 GL11.glLoadIdentity();
		 GL11.glOrtho(0.0f, Display.getWidth(), Display.getHeight(), 0.0f, -1.0f, 1.0f);
		 GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	private static void addPoint(double x, double y) {
		GL11.glVertex2d((x / 800.0d) * Display.getWidth(), (y / 430.0f) * Display.getHeight());
	}
	
	public static Texture loadTexture(String fileName) {
		int[] pixels = null;
		int w = 0;
		int h = 0;
		
		try {			
			BufferedImage image = ImageIO.read(ClassLoader.getSystemResourceAsStream("res/" + fileName + ".png"));
			w = image.getWidth();
			h = image.getHeight();
			pixels = new int[w * h];
			image.getRGB(0, 0, w, h, pixels, 0, w);
		}catch(IOException e) {
			e.printStackTrace();
			System.err.println("Could not find texture! (" + fileName + ".png)");
			System.exit(1);
		}
		int[] data = new int[w * h];
		for (int i = 0; i < w * h; i++) {
			int alpha = (pixels[i] & 0xFF000000) >> 24;
			int red = (pixels[i] & 0xFF0000) >> 16;
			int green = (pixels[i] & 0xFF00) >> 8;
			int blue = (pixels[i] & 0xFF);
			
			data[i] = alpha << 24 | blue << 16 | green << 8 | red;
		}
		IntBuffer res = ByteBuffer.allocateDirect(data.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		res.put(data).flip();
		
		int id = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, res);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		return new Texture(id, w, h);
	}
}

class Texture {
	
	public int id, w, h;
	
	public Texture(int ID, int width, int height) {
		id = ID;
		w = width;
		h = height;
	}
}
