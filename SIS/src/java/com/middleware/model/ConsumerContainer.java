package com.middleware.model;


import java.util.ArrayList;
import java.util.List;
import com.middleware.model.Consumer;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ConsumerContainer {

	List<Consumer> consumers;

	public List<Consumer> getConsumers() {
		return consumers;
	}

	public void setConsumers(List<Consumer> consumers) {
		this.consumers = consumers;
	}

	public ConsumerContainer(List<Consumer> consumers) {
		super();
		this.consumers = consumers;
	}

	public ConsumerContainer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ConsumerContainer(Consumer[] consumers) {
		this.consumers = new ArrayList<Consumer>();
		for (Consumer i: consumers) {
			this.consumers.add(i);
		}
	}
	
	
}
