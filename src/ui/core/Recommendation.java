package ui.core;

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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ui.utils.Utils;

public class Recommendation implements Comparable<Recommendation> {
	
	public static final int CONDITION_NOTHING = 0;
	public static final int CONDITION_PEOPLE_NAME = 1;
	public static final int CONDITION_PEOPLE_NUMBER = 2;
	public static final int CONDITION_CONFIDENCE_RATING = 3;
	
	private String label;
	private int condition;
	private String id = "";
	private String htmlFile = "";
	private int goodness = 0;
	private String conditionString;
	private String conditionShortString;
	private float rating;
	
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
		int difference = r.goodness - this.goodness;
		if (difference == 0)
			return this.id.compareTo(r.id);
		else return difference;
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
		case CONDITION_NOTHING:
			conditionShortString = "none";
			break;
		case CONDITION_PEOPLE_NAME:
			conditionShortString = "peoplename";
			break;
		case CONDITION_PEOPLE_NUMBER:
			conditionShortString = "peoplenumber";
			break;
		case CONDITION_CONFIDENCE_RATING:
			conditionShortString = "confidence";
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
		int conditionTotal = (Utils.conditions[0] + Utils.conditions[1] + Utils.conditions[2] + Utils.conditions[3]);
		
		if (conditionTotal % 4 == 0){
			Random generator = new Random();
			int randomSeed = generator.nextInt(100) * generator.nextInt(100);
			int randomInt = generator.nextInt(randomSeed + 1);
			
			int condition = randomInt % 4;
			
			this.setCondition(condition);
			Utils.conditions[condition]++;
		}
		
		else {
			int condition;
			Random generator = new Random();
			int randomInt = generator.nextInt(100);
			randomInt = randomInt % 4;
			
			if(Utils.conditions[randomInt] < Utils.conditions[(randomInt + 1) % 4] || 
					Utils.conditions[randomInt] < Utils.conditions[(randomInt + 2) % 4] ||
					Utils.conditions[randomInt] < Utils.conditions[(randomInt + 3) % 4]){
			    condition = randomInt;
			}
			else if(Utils.conditions[(randomInt + 1) % 4] < Utils.conditions[(randomInt + 2) % 4] || 
					Utils.conditions[(randomInt + 1) % 4] < Utils.conditions[(randomInt + 3) % 4] || 
					Utils.conditions[(randomInt + 1) % 4] < Utils.conditions[randomInt]){
			    condition = (randomInt + 1) % 4;
			}
			else if(Utils.conditions[(randomInt + 2) % 4] < Utils.conditions[(randomInt + 3) % 4] || 
					Utils.conditions[(randomInt + 2) % 4] < Utils.conditions[(randomInt + 1) % 4] || 
					Utils.conditions[(randomInt + 2) % 4] < Utils.conditions[randomInt]){
			    condition = (randomInt + 2) % 4;
			}
			else{
			    condition = (randomInt + 3) % 4;
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
		if (this.condition == CONDITION_NOTHING){
			this.conditionString = "";
		}
		else if (this.condition == CONDITION_PEOPLE_NAME){
			ArrayList<String> friends = readFriendsFile(); 
			int size = friends.size();
			Random random = new Random();
			int randomSeed = random.nextInt(100);
			int index = random.nextInt(randomSeed + 1) % size;
			
			this.conditionString = friends.get(index) + " uses this command.";
		}
		else if (this.condition == CONDITION_PEOPLE_NUMBER){
			Random random = new Random();
			int randomSeed = random.nextInt(100);
			int index = random.nextInt(randomSeed + 1) % 30;
			int number = index + 13;
			
			this.conditionString = number + " people use this command.";
		}
		else if (this.condition == CONDITION_CONFIDENCE_RATING){
			this.conditionString = "";
			float var = (new Random().nextInt() % 4);
			var = Math.abs(var);
			var = (float) (var / 2 + 3.5);
			this.rating = var;
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
	}

	public float getRating() {
		return rating;
	};
	
	public void setRating(float rating) {
		this.rating = rating;
	}
}

