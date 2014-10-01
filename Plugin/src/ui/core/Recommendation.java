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

    private String conditionString;

    private String conditionShortString;

    private float rating;
    
    private Random random = new Random();

    private List<String> friendsList;

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
        final int prime = 31;
        int result = 1;
        return prime * result + ((id == null) ? 0 : id.hashCode());
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

    public String getConditionString() {
        return this.conditionString;
    }

    public void addCondition() throws URISyntaxException, IOException {
        if (conditionString != null)
            return; // condition already added
        computeCondition();
        if (this.condition == CONDITION_NOTHING) {
            this.conditionString = "";
        }
        else if (this.condition == CONDITION_PEOPLE_NAME) {
            List<String> friends = getFriendsFile();
            int size = friends.size();
            
            int randomSeed = random.nextInt(100);
            int index = random.nextInt(randomSeed + 1) % size;

            this.conditionString = friends.get(index) + " uses this command.";
        }
        else if (this.condition == CONDITION_PEOPLE_NUMBER) {
            int randomSeed = random.nextInt(100);
            int index = random.nextInt(randomSeed + 1) % 30;
            int number = index + 13;

            this.conditionString = number + " people use this command.";
        }
        else if (this.condition == CONDITION_CONFIDENCE_RATING) {
            this.conditionString = "";
            float var = (random.nextInt() % 4);
            var = Math.abs(var);
            var = (float) (var / 2 + 3.5);
            this.rating = var;
        }
    }

    private List<String> getFriendsFile() throws URISyntaxException, IOException {
        // reads in FriendsFile from disk or returns the parsed list.
        if (this.friendsList == null) {
            File friendsFile = Utils.getFriendsFile();

            friendsList = new ArrayList<String>();
            try (BufferedReader br = new BufferedReader(new FileReader(friendsFile));)
            {
                String idStart = String.format("%03d:", Utils.getParticipantID());
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(idStart)) {
                        friendsList.add(line.substring(4)); // the line starts with 123:
                    }     
                }
            } catch (IOException e) {
                UsageDataCaptureActivator.logException("Problem with friends file", e);
            }
        }
        return friendsList;
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
