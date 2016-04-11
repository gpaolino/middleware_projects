package com.middleware.model;


import java.util.ArrayList;
import java.util.List;
import com.middleware.model.Image;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ImageContainer {

	List<Image> images;

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public ImageContainer(List<Image> images) {
		super();
		this.images = images;
	}

	public ImageContainer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ImageContainer(Image[] images) {
		this.images = new ArrayList<Image>();
		for (Image i: images) {
			this.images.add(i);
		}
	}
	
	
}
