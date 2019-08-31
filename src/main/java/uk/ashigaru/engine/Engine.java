package uk.ashigaru.engine;

import org.lwjgl.opengl.GL11;

import uk.ashigaru.engine.loop.GameLoopRequester;
import uk.ashigaru.engine.window.Display;
import uk.ashigaru.engine.window.Viewport;

public class Engine {

	private Display display;
	private GameLoopRequester glRequester;
	
	private Object child;
	
	public void setChild(Object child) {
		this.child = child;
	}
	
	public Object getChild() {
		return this.child;
	}

	public static void clearGLBuffer() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public Display getDisplay() {
		return display;
	}
	
	public long getDisplayID() {
		return display.getWindowID();
	}
	
	public GameLoopRequester getGameLoopRequester() {
		return glRequester;
	}
	
	public Engine() {
		this.display = new Display();
		this.glRequester = new GameLoopRequester();
	}
	
	public void createWindow(int width, int height, String title, boolean vsync, boolean fullscreen) {
		display.create(width, height, title, vsync, fullscreen);
	}
	
	private double fps;
	
	public void createGameLoop() {
		double t = 0.0;
		final double dt = 0.01;
			
		double currentTime = System.nanoTime() / 1000000000.0;
		double accumulator = 0.0;
		
		double ct = System.nanoTime() / 1000000000.0;
		
		int count = 0;
		while(display.isDisplayActive()) {
			double newTime = System.nanoTime() / 1000000000.0;
			double frameTime = newTime - currentTime;
			currentTime = newTime;
				
			accumulator += frameTime;
			
			while(accumulator >= dt) {
				glRequester.update(t, dt);
				//input poll
				accumulator -= dt;
				t += 1;
			}
			glRequester.draw();
			display.update();
			
			count += 1;
			double nt = System.nanoTime() / 1000000000.0;
			if(nt - ct >= 1.0) {
				fps = count;
				count = 0;
				ct = nt;
			}
		}
		glRequester.exit();
		display.kill();
		System.exit(0);
	}
	
	public double getFPS() {
		return fps;
	}
	
	public static boolean isDev() {
		String e = System.getProperty("eclipse");
		return e == null ? false : e.equalsIgnoreCase("true");
	}

	public int getWidth() {
		return display.getWidth();
	}
	
	public int getHeight() {
		return display.getHeight();
	}
	
	public float getAR() {
		return (float) display.getWidth() / (float) display.getHeight();
	}
}
