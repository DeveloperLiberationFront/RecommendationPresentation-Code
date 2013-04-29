package fatbastard.ui.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fatbastard.ui.utils.Utils;

public class Recommendation implements Comparable<Recommendation> {

	private String label;
	private int condition;
	private String id = "";
	private String htmlFile = "";
	private int goodness = 0;
	private String conditionString;
	private String conditionShortString;
	
	@Override
	public boolean equals(Object obj) {
		return this.id.equals(((Recommendation)obj).id);
	}
	
	@Override
	public int hashCode() {
		int len = id.length();
		int labelLen = label.length();
		return len * labelLen;
	}

	@Override
	public int compareTo(Recommendation r) {
		if (this.equals(r)) return 0;
		else {
			int difference = this.goodness - r.goodness;
			return difference == 0 ? 1 : difference; 
		}
	}
	
	public Recommendation(String id) throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
		this.id = id;
		String recommendationFolder = getRecommendationsFolder(id);
		String xmlFile = recommendationFolder + File.separator + "reco.xml";
		this.htmlFile = recommendationFolder + File.separator + "reco.html";
		
		readXmlFile(xmlFile);
	}

	private void readXmlFile(String xmlFile) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		DefaultHandler handler = new DefaultHandler() {
			boolean goodnessStart = false;
			boolean nameStart = false;
			
			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {
				 
				if (qName.equalsIgnoreCase("goodness")) {
					goodnessStart = true;
				}
				
				if (qName.equalsIgnoreCase("displayname")) {
					nameStart = true;
				}
		 
				super.startElement(uri, localName, qName, attributes);
			}
			
			@Override
			public void endElement(String uri, String localName, String qName)
					throws SAXException {
				super.endElement(uri, localName, qName);
			}
			
			@Override
			public void characters(char[] ch, int start, int length)
					throws SAXException {
				
				if (goodnessStart){
					goodness = Integer.parseInt(new String(ch, start, length));
					goodnessStart = false;
				}
				
				if (nameStart){
					label = new String(ch, start, length);
					nameStart = false;
				}
				
				super.characters(ch, start, length);
			}
		};
		
		saxParser.parse(xmlFile, handler);
	}

	private String getRecommendationsFolder(String id)
			throws URISyntaxException, IOException {
		return Utils.getResourceFolder() + File.separator + "recommendations" + File.separator + id;
	}
	
	public void setGoodness(int goodness) {
		this.goodness = goodness;
	}
	
	public int getGoodness() {
		return goodness;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getCondition() {
		return condition;
	}
	public void setCondition(int condition) {
		this.condition = condition;
		switch (condition){
		case Utils.CONDITION_NOTHING:
			conditionShortString = "none";
			break;
		case Utils.CONDITION_PEOPLE_NAME:
			conditionShortString = "peoplename";
			break;
		case Utils.CONDITION_PEOPLE_NUMBER:
			conditionShortString = "peoplenumber";
			break;
		default:
			conditionShortString = "";
			break;
		}
	}
	public String getId() {
		return id;
	}
	public String getHtmlFile() {
		return htmlFile;
	}	

	public void setHtmlFile(String htmlFile) {
		this.htmlFile = htmlFile;
	}

	private void computeCondition(){
		int conditionTotal = (Utils.conditions[0] + Utils.conditions[1] + Utils.conditions[2]);
		
		if (conditionTotal % 3 == 0){
			Random generator = new Random();
			int randomSeed = generator.nextInt(100) * generator.nextInt(100);
			int randomInt = generator.nextInt(randomSeed + 1);
			
			int condition = randomInt % 3;
			
			this.setCondition(condition);
			Utils.conditions[condition]++;
		}
		
		else {
			int condition;
			Random generator = new Random();
			int randomInt = generator.nextInt(100);
			randomInt = randomInt % 3;
			
			if(Utils.conditions[randomInt] < Utils.conditions[(randomInt + 1) % 3] && Utils.conditions[randomInt] < Utils.conditions[(randomInt + 2) % 3]){
			    condition = randomInt;
			}
			else if(Utils.conditions[(randomInt + 1) % 3] < Utils.conditions[(randomInt + 2) % 3] && Utils.conditions[(randomInt + 1) % 3] < Utils.conditions[randomInt]){
			    condition = (randomInt + 1) % 3;
			}
			else{
			    condition = (randomInt + 2) % 3;
			}
			this.setCondition(condition);
			Utils.conditions[condition]++;
		}
	}

	public String getConditionString() {
		return this.conditionString;
	}

	
	public void addCondition() throws URISyntaxException, IOException{
		if (conditionString != null) return; //condition already added
		computeCondition();
		if (this.condition == Utils.CONDITION_NOTHING){
			this.conditionString = "";
		}
		else if (this.condition == Utils.CONDITION_PEOPLE_NAME){
			ArrayList<String> friends = readFriendsFile(); 
			int size = friends.size();
			Random random = new Random();
			int randomSeed = random.nextInt(100);
			int index = random.nextInt(randomSeed + 1) % size;
			
			this.conditionString = friends.get(index) + " uses this command.";
		}
		else if (this.condition == Utils.CONDITION_PEOPLE_NUMBER){
			Random random = new Random();
			int randomSeed = random.nextInt(100);
			int index = random.nextInt(randomSeed + 1) % 30;
			int number = index + 13;
			
			this.conditionString = number + " people use this command.";
		}
	}

	private ArrayList<String> readFriendsFile() throws URISyntaxException, IOException {
		String friendsFile = Utils.getResourceFolder() + File.separator + "friends.txt";

	    BufferedReader br = new BufferedReader(new FileReader(friendsFile));
	    ArrayList<String> friends = new ArrayList<String>();
	    String line = null;
	    while ((line = br.readLine()) != null){
	    	if (!line.equalsIgnoreCase(""))
	    		friends.add(line);
	    }
	    br.close();
		return friends;
	}
	
	public String getConditionShortString() {
		return conditionShortString;
	};
}

