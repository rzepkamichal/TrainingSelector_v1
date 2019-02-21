package app;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class Parser {
	
	//reading configuration names list from json, returns configuration object
	public static Configuration readConfiguration() throws IOException {
		Configuration conf = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		try {
			conf = mapper.readValue(new File("configuration.json"), Configuration.class);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			if(e instanceof FileNotFoundException);
				throw e;
		}
		return conf;
	}
	
	//saving conf object to json
	public static void saveConfiguration(Configuration conf) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		try {
			mapper.writeValue(new File("configuration.json"), conf);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//reading training list from json, returns list with trainings
	public static List<Training> readTrainings() {
		List<Training> trainings = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		try {
			trainings = 
					mapper.readValue(new File("trainings.json"), new TypeReference<List<Training>>(){});
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return trainings;
	}
	
	//just save trainings list to json
	public static void saveTrainingsBasic(List<Training> trainings) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		try {
			mapper.writeValue(new File("trainings.json"), trainings);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//save trainings list to json but first check if wanted-to-save training exists in database
	//if so - overwrite it
	public static void saveTrainings(List<Training> trainings) {
		List<Training> trainings_ = Parser.readTrainings();
		if(trainings_ == null) {
			saveTrainingsBasic(trainings);
		} else {
			trainings.forEach(training -> {
				
				boolean rightDate = true;
				boolean replaced = false;
				
				for(int i = trainings_.size() - 1; i >= 0 && rightDate; i--) {
					
					//check if the date of current training equals to date of
					//training in database
					rightDate = trainings_.get(i).getDate()
							.equals(training.getDate());
					
					//check if the name of current training matches the name of training 
					//saved in database
					boolean rightName = Objects.equals(training.getName(),
							trainings_.get(i).getName());
					
					//if all previous true replace previous training with new training
					if(rightName && rightDate) {
						trainings_.set(i, training);
						replaced = true;
					}
					
				}
				
				//if training doesn't exist in database - add it
				if(!replaced)
					trainings_.add(training);
				
			});
			
			saveTrainingsBasic(trainings_);
			
		}
	}
	
}
