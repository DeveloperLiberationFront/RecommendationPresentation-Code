package ui.core;

import java.io.File;

public class RecommendationBundle {

    public final File xmlFile;
    public final File htmlFile;
    
    public RecommendationBundle(File xmlFile, File htmlFile) {
        this.xmlFile = xmlFile;
        this.htmlFile = htmlFile;
    }

}
