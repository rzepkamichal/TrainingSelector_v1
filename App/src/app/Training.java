package app;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Training {
	
	private String name;		//name of training
	private Date date;			//date of training
	private int entrances;		//number of entrances

	//creating training with given name, default date and 0 entrances
	public Training(String name) {
		this.name = name;
		this.date = new Date();
		this.entrances = 0;
	}
	
	//creator for reading from json
	@JsonCreator
	public Training(@JsonProperty("name") String name, 
			@JsonProperty("date") Date date, @JsonProperty("entrances") int entrances) {
		this.name = name;
		this.date = date;
		this.entrances = entrances;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	//for displaying date in nice string
	public String dateToDisplay() {
		SimpleDateFormat ft = 
			      new SimpleDateFormat("E, d.MM.y");
		return ft.format(this.date);
	}
	
	public int getEntrances() {
		return this.entrances;
	}
	
	public void addEntrance() {
		this.entrances++;
	}
	
	public void setEntrances(int entrances) {
		this.entrances = entrances;
	}
	
	public void deleteEntrance() {
		if(this.entrances > 0)	//when entrances are 0, doesn't make sense to make them negative value
			this.entrances--;
	}
		
}
