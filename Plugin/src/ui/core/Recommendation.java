package ui.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.epp.usagedata.internal.gathering.UsageDataCaptureActivator;
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

    private File htmlFile = null;

    private int goodness = 0;

    private String userFacingString;

    private String conditionShortString;

    private float rating;
    
    private Random random = new Random();

    private List<String> friendsList;

    private List<String> strangersList;
    
    private boolean wasLastOneStranger = random.nextBoolean();

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Recommendation other = (Recommendation) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public int compareTo(Recommendation r) {
        int difference = r.goodness - this.goodness;
        if (difference == 0)
            return this.id.compareTo(r.id);
        return difference;
    }

    public Recommendation(String id) throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
        this.id = id;
        RecommendationBundle reco = Utils.getRecommendationsBundle(id);
        
        File xmlFile = reco.xmlFile; //recommendationFolder + File.separator + "reco.xml";
        this.htmlFile = reco.htmlFile; // recommendationFolder + File.separator + "reco.html";

        readXmlFile(xmlFile);
    }

    private void readXmlFile(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DefaultHandler handler = new DefaultHandler() {
            boolean goodnessStart = false;

            boolean nameStart = false;

            @Override
            public void startElement(String uri, String localName,
                    String qName, Attributes attributes) throws SAXException {

                if ("goodness".equalsIgnoreCase(qName)) {
                    goodnessStart = true;
                }

                if ("displayname".equalsIgnoreCase(qName)) {
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

                if (goodnessStart) {
                    goodness = Integer.parseInt(new String(ch, start, length));
                    goodnessStart = false;
                }

                if (nameStart) {
                    label = new String(ch, start, length);
                    nameStart = false;
                }

                super.characters(ch, start, length);
            }
        };

        saxParser.parse(xmlFile, handler);
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
        switch (condition) {
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
        return htmlFile.getAbsolutePath();
    }

    private void computeCondition() {
        this.setCondition(Utils.getParticipantID() % 4);
    }

    public String getUserFacingString() {
        return this.userFacingString;
    }

    public void addCondition() throws URISyntaxException, IOException {
        if (userFacingString != null)
            return; // condition already added
        computeCondition();
        if (this.condition == CONDITION_NOTHING) {
            this.userFacingString = "";
        }
        else if (this.condition == CONDITION_PEOPLE_NAME) {
            handleFriendOrStrangerCondition();
        }
        else if (this.condition == CONDITION_PEOPLE_NUMBER) {
            random.setSeed(id.hashCode());
            int randomSeed = random.nextInt(100);
            int index = random.nextInt(randomSeed + 1) % 30;
            int number = index + 13;

            this.userFacingString = number + " people use this command.";
        }
        else if (this.condition == CONDITION_CONFIDENCE_RATING) {
            this.userFacingString = "";
            random.setSeed(id.hashCode());
            float var = (random.nextInt() % 4);
            var = Math.abs(var);
            var = (float) (var / 2 + 3.5);
            this.rating = var;
        }
    }

    private void handleFriendOrStrangerCondition() throws URISyntaxException, IOException {
        //We alternate between using a stranger and using a known person given a random starting point.
        //This should balance the creation (especially guaranteeing that one of the first two recos will 
        //be someone known.
        
        if (wasLastOneStranger) {
            List<String> friends = getFriendsFile();
            int size = friends.size();
            
            int index = random.nextInt(size);

            this.userFacingString = friends.get(index) + " uses this command.";
            this.conditionShortString = conditionShortString+ ":"+friends.get(index);
        } else {
            List<String> strangers = getStrangersFile();
            int size = strangers.size();
            
            int index = random.nextInt(size);
            
            this.userFacingString = strangers.get(index) + " uses this command.";
            this.conditionShortString = conditionShortString+ "_stranger:"+strangers.get(index);
        }
        
        wasLastOneStranger = !wasLastOneStranger;
        
        
    }

    private List<String> getStrangersFile() {
        if (this.strangersList == null) {
            File strangersFile = Utils.getStrangersFile();
            //dividing by 100 to get the section number
            strangersList = readFriendsOrStrangersFile(strangersFile, Utils.getParticipantID()/100);
        }
        return this.strangersList;
    }

    private List<String> getFriendsFile() throws URISyntaxException, IOException {
        // reads in FriendsFile from disk or returns the parsed list.
        if (this.friendsList == null) {
            File friendsFile = Utils.getFriendsFile();
            friendsList = readFriendsOrStrangersFile(friendsFile, Utils.getParticipantID());
        }
        return friendsList;
    }

    private List<String> readFriendsOrStrangersFile(File file, int rowNum) {
        List<String> list = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(file));)
        {
            
            String idStart = String.format("%03d:", rowNum);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(idStart)) {
                    list.add(line.substring(4).trim()); // the line starts with 123:
                }     
            }
        } catch (IOException e) {
            UsageDataCaptureActivator.logException("Problem with friends or stranger file" + file, e);
        }
        return list;
    }

    public String getConditionShortString() {
        return conditionShortString;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
