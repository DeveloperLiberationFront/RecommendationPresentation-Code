package data;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.PriorityQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DataParser {
    
    public static final String dateStringOfStudy = "2014-10-02";
    public static final String[] dirsToParse = 
            new String[]{
        "C:\\Users\\KevinLubick\\Downloads\\data\\2nd Section\\",
        "C:\\Users\\KevinLubick\\Downloads\\data\\3rd Section\\",
        "C:\\Users\\KevinLubick\\Downloads\\data\\4th Section\\"
        };
    private static SQLiteDatabaseLink db;
    
    private static SAXParser xmlReader;
    private static int participantId;
    
    static {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            xmlReader = factory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws DatabaseException {
        
        db = new SQLiteDatabaseLink();
        
        
        for (String dirToParse : dirsToParse) {
            File dir = new File(dirToParse);
            if (!(dir.exists() && dir.isDirectory())) {
                System.err.println("Problem with " + dir.getAbsolutePath());
                return;
            }
            // Find all zip files.
            File[] participantFiles = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File arg0, String arg1) {
                    return !arg1.startsWith("1") && arg1.endsWith("zip"); // 1 means broken data
                }
            });
            
            for (File participant : participantFiles) {
                try {
                    System.out.println("Beginning "+participant.getName());
                    parseParticipant(new ZipFile(participant));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
       
    }

    private static void parseParticipant(ZipFile zipFile) throws IOException, SAXException {
        getParticipantID(zipFile);
        
        String lastFolderName = findLastEntry(zipFile);
        
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            
            String folderName = entry.getName(); 
            if (folderName.endsWith(lastFolderName+"/clicks.xml")) {
                handleClicks(zipFile.getInputStream(entry));
            } else if (folderName.endsWith(lastFolderName+"/recos.xml")) {
                handleRecos(zipFile.getInputStream(entry));
            } else if (folderName.endsWith(lastFolderName+"/responses.xml")) {
                handleResponses(zipFile.getInputStream(entry));
            } else if (folderName.endsWith(lastFolderName+"/usage.xml")) {
                handleUsages(zipFile.getInputStream(entry));
            }
        }
    }
    private static void getParticipantID(ZipFile zipFile) {
        String participantString = zipFile.getName();
        participantString = participantString.substring(participantString.lastIndexOf('\\')+1).replace(".zip","");   
        participantId = Integer.parseInt(participantString);
    }

    private static String findLastEntry(ZipFile zipFile) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        
        PriorityQueue<String> entryNames = new PriorityQueue<>(20,new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);        //compare greater (newer) strings first
            }
        });
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            
            String folderName = entry.getName();
            int dateLoc = folderName.lastIndexOf("2014-10-02");
            if (dateLoc == -1) continue;
            folderName = folderName.substring(dateLoc, 
                            folderName.lastIndexOf('/'));
            
            entryNames.add(folderName);
        }
        
        String newestFolderName = entryNames.peek();
        return newestFolderName;
    }
    
    private static void handleResponses(InputStream inputStream) throws IOException {
        try {
            xmlReader.parse(inputStream, new ResponsesHandler());
        } catch (SAXException e) {
            System.err.println("Problem with responses on participant " + participantId);
            e.printStackTrace();
        }
        
    }

    private static void handleUsages(InputStream inputStream) throws SAXException, IOException {
        xmlReader.parse(inputStream, new UsagesHandler());
    }

    private static void handleRecos(InputStream inputStream) throws SAXException, IOException {
        xmlReader.parse(inputStream, new RecoHandler());
    }

    private static void handleClicks(InputStream inputStream) throws SAXException, IOException {
        xmlReader.parse(inputStream, new ClickHandler());
    }
    
    private static class ResponsesHandler extends DefaultHandler {
        int currentTaskNumber = 0;
        private String currentResponse;
    
        private boolean sawResponse;
    
        @Override
        public void startElement(String uri, String localName, String elementName, Attributes attributes) throws SAXException {
            if ("response".equalsIgnoreCase(elementName)) {
                sawResponse = true;
            }
            super.startElement(uri, localName, elementName, attributes);
        }
    
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String elementContents = new String(ch, start, length);
            if (sawResponse) {
                currentResponse = elementContents;
            }            
            super.characters(ch, start, length);
        }
    
        @Override
        public void endElement(String uri, String localName, String elementName) throws SAXException {
            if ("response".equalsIgnoreCase(elementName)) {
                db.sawResponse(participantId, currentTaskNumber, currentResponse);
                currentTaskNumber++;
            }
            super.endElement(uri, localName, elementName);
        }
    }
    
    private static class UsagesHandler extends DefaultHandler {
        int currentTaskNumber = -1;
    
        boolean sawNumber = false;
    
        private String currentCommandId;
    
        private boolean sawCommandId;
    
        @Override
        public void startElement(String uri, String localName, String elementName, Attributes attributes) throws SAXException {
            if ("number".equalsIgnoreCase(elementName)) {
                sawNumber = true;
            } else if ("id".equalsIgnoreCase(elementName)) {
                sawCommandId = true;
            } 
            super.startElement(uri, localName, elementName, attributes);
        }
    
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String elementContents = new String(ch, start, length);
    
            if (sawNumber) {
                currentTaskNumber = Integer.parseInt(elementContents);
            } else if (sawCommandId) {
                currentCommandId = elementContents;
            }             
            super.characters(ch, start, length);
        }
    
        @Override
        public void endElement(String uri, String localName, String elementName) throws SAXException {
            if ("number".equalsIgnoreCase(elementName)) {
                sawNumber = false;
            } else if ("id".equalsIgnoreCase(elementName)) {
                db.sawUsage(participantId, currentTaskNumber, currentCommandId);
                sawCommandId = false;
            }
            super.endElement(uri, localName, elementName);
        }
    }

    private static class RecoHandler extends DefaultHandler {
        int currentTaskNumber = -1;
    
        boolean sawNumber = false;
    
        private String currentCondition;
    
        private String currentCommandId;
    
        private boolean sawCondition;
    
        private boolean sawCommandId;
    
        @Override
        public void startElement(String uri, String localName, String elementName, Attributes attributes) throws SAXException {
            if ("number".equalsIgnoreCase(elementName)) {
                sawNumber = true;
            } else if ("commandid".equalsIgnoreCase(elementName)) {
                sawCommandId = true;
            } else if ("condition".equalsIgnoreCase(elementName)) {
                sawCondition = true;
            }
            super.startElement(uri, localName, elementName, attributes);
        }
    
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String elementContents = new String(ch, start, length);
    
            if (sawNumber) {
                currentTaskNumber = Integer.parseInt(elementContents);
            } else if (sawCommandId) {
                currentCommandId = elementContents;
            } else if (sawCondition) {
                currentCondition = elementContents;
            }
            
            super.characters(ch, start, length);
        }
    
        @Override
        public void endElement(String uri, String localName, String elementName) throws SAXException {
            if ("number".equalsIgnoreCase(elementName)) {
                sawNumber = false;
            } else if ("commandid".equalsIgnoreCase(elementName)) {
                sawCommandId = false;
            } else if ("condition".equalsIgnoreCase(elementName)) {
                sawCondition = false;
            } else if ("recommendation".equalsIgnoreCase(elementName)) {
                db.sawRecommendation(participantId, currentTaskNumber, currentCommandId, currentCondition);
            }
            super.endElement(uri, localName, elementName);
        }
    }

    private static class ClickHandler extends DefaultHandler {
        int currentTaskNumber = -1;
    
        boolean sawNumber = false;
    
        private String currentCondition;
    
        private String currentCommandId;
    
        private boolean sawCondition;
    
        private boolean sawCommandId;
    
        @Override
        public void startElement(String uri, String localName, String elementName, Attributes attributes) throws SAXException {
            if ("number".equalsIgnoreCase(elementName)) {
                sawNumber = true;
            } else if ("commandid".equalsIgnoreCase(elementName)) {
                sawCommandId = true;
            } else if ("condition".equalsIgnoreCase(elementName)) {
                sawCondition = true;
            }
            super.startElement(uri, localName, elementName, attributes);
        }
    
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String elementContents = new String(ch, start, length);
    
            if (sawNumber) {
                currentTaskNumber = Integer.parseInt(elementContents);
            } else if (sawCommandId) {
                currentCommandId = elementContents;
            } else if (sawCondition) {
                currentCondition = elementContents;
            }
            
            super.characters(ch, start, length);
        }
    
        @Override
        public void endElement(String uri, String localName, String elementName) throws SAXException {
            if ("number".equalsIgnoreCase(elementName)) {
                sawNumber = false;
            } else if ("commandid".equalsIgnoreCase(elementName)) {
                sawCommandId = false;
            } else if ("condition".equalsIgnoreCase(elementName)) {
                sawCondition = false;
            } else if ("recommendation".equalsIgnoreCase(elementName)) {
                db.sawClick(participantId, currentTaskNumber, currentCommandId, currentCondition);
            }
            super.endElement(uri, localName, elementName);
        }
    }
}
