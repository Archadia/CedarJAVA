package uk.ashigaru.engine.render.model.lower;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import uk.ashigaru.engine.util.Resource;

public class Texture {

	private int width, height;
	private int textureID;
		
	private int iconSize = 1;
	
	public Texture() {}
	
	public Texture(Resource texture) {
		this.load(texture);
	}
	
	public Texture(Resource texture, int iconSize) {
		this.load(texture);
		this.setIconSize(iconSize);
	}

	public Vector2f getTextureOffset(int index) {
		if(iconSize == 0) return new Vector2f(0,0);
		return new Vector2f(index % iconSize, (int) index / iconSize);
	}
	
	public int getIconSize() {
		return iconSize;
	}

	public void setIconSize(int iconSize) {
		this.iconSize = iconSize;
	}

	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	public void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	private void load(Resource texture) {
		int[] pixels = null;
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(texture.getPath()));
			this.width = image.getWidth();
			this.height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, this.width, this.height, pixels, 0, width);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		int[] data = new int[width * height];
		for(int i = 0; i < width * height; i++) {
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);
			
			data[i] = a << 24 | b << 16 | g << 8 | r;
		}
		
		int result = GL11.glGenTextures();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, result);	
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data).flip();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		this.textureID = result;
	}
	
	public void delete() {
		GL11.glDeleteTextures(textureID);
	}
		
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getTextureID() { return textureID; }
}