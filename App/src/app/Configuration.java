package app;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuration {
	private List<String> names;	//list containing names of trainings
	
	//default
	public Configuration() {
		this.names = new LinkedList<String>(
			Arrays.asList(
				"Boks",
				"Muay Thai",
				"MMA pocz.",
				"MMA zaaw.",
				"Grappling",
				"BJJ zaaw.",
				"BJJ pocz.",
				"BJJ 8-12",
				"BJJ 4-7",
				"MDS",
				"Krav Maga"
			)
		);
	}
	
	//for reading from json
	@JsonCreator
	public Configuration(@JsonProperty("names") List<String> names) {
		this.names = names;
	}
	
	//get name specified by i
	public String getName(int i) {
		return this.names.get(i);
	}
	
	//get names list in form of array
	public String[] namesArray() {
		String[] temp = new String[names.size()];
		temp = names.toArray(temp);
		return temp;
	}
	
	//length of names list
	public int length() {
		return this.names.size();
	}
	
	//add name to list
	public void addName(String name) {
		this.names.add(name);
	}
	
	//delete all names from list
	public void clear() {
		this.names.clear();
	}
	
}
