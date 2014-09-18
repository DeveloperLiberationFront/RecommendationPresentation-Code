package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import ui.core.Task;
import ui.core.TaskReader;

public class TaskReaderTest {

	TaskReader tr;
	
	@Before
	public void initialize() throws ParserConfigurationException, SAXException{
		tr = new TaskReader();
	}
	
	@Test
	public void testGetTaskList() throws SAXException, IOException, URISyntaxException{
		ArrayList<Task> taskList = tr.getTaskList();
		
		assertEquals("passed", 7, taskList.size());	
		
	}
	
}
