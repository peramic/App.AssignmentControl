package havis.custom.harting.assignmentcontrol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import havis.custom.harting.assignmentcontrol.model.Assignment;
import havis.custom.harting.assignmentcontrol.model.Location;
import havis.custom.harting.assignmentcontrol.model.Tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class TestSuite {
	@Test
	public void testObjectMapper() throws JsonProcessingException {
		Assignment assignment = new Assignment();
		Location location = new Location(1, "Test");
		Tag tag = new Tag("123123", "24234", new Date());
		
		assignment.setLocation(location);
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(tag);
		
		assignment.setTags(tags);
		
		String writeValueAsString = new ObjectMapper().writeValueAsString(assignment);

		System.out.println(writeValueAsString);
	}
}
