package game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import javax.imageio.ImageIO;

/** 
 * Loads and stores all images
 */
public enum ImageHash {
	IMG();
	
	private HashMap<String, BufferedImage> imageHash;
	/**
	 * Constructor that instantiates this ImageHash. Reads in all images in the folder ./img/ and puts them into a hash
	 * with their name as the key
	 */
	private ImageHash(){
		imageHash = new HashMap<String, BufferedImage>();
		try {
			readHash(new File(System.getProperty("user.dir") + "/img"));
		} catch (IOException e) {
			System.err.println("ImageHash error");
			e.printStackTrace();
		}
	}
	/**
	 * Reads in all images from dir and puts them into a hash with their name as the key
	 * @param dir The directory to read images from
	 * @throws IOException
	 */
	private void readHash(File dir) throws IOException{
		for (File f:dir.listFiles()){
			if (f.isDirectory()){
				readHash(f);
			} else {
				if (f.getName().endsWith(".png")){
					String name = f.getName().substring(0, f.getName().lastIndexOf(".")).toLowerCase(Locale.ENGLISH);
					BufferedImage i = ImageIO.read(f);
					imageHash.put(name, i);
				}
			}
		}
	}
	/**
	 * @param s The name of the image to load
	 * @return BufferedImage of the requested image, if it exists (else returns a null image)
	 */
	public BufferedImage getImage(String s){
		return imageHash.get(s);
	}
	/**
	 * Reconstructs the ImageHash
	 */
	public void rehash(){
		imageHash = new HashMap<String, BufferedImage>();
		try {
			readHash(new File(System.getProperty("user.dir") + "/img"));
		} catch (IOException e) {
			System.err.println("ImageHash error");
			e.printStackTrace();
		}
	}
	/**
	 * Gets all keyed Strings in the hash
	 */
	public Collection<String> getKeys(){
		return imageHash.keySet();
	}
}
