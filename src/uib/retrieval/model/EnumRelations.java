/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.retrieval.model;

import java.util.EnumMap;

/**
 *
 * @author Olav
 */
public class EnumRelations {
    
   

    public EnumRelations() {
    }

    public enum Relations {

        KEY("key", "keyFor"),
        ANNOTATES("annotates", "annotatedBy"),
        SHOWS("shows", "appearsIn"),
        REFERENCES("references", "referencedBy"),
        QUALITY("quality", "qualityOf"),
        SYMBOLIZES("symbolizes", "symbolizedBy"),
        LOCATION("location", "locationOf"),
        SOURCE("source", "sourceOf"),
        DESTINATION("destination", "destinationOf"),
        PATH("path", "pathOf"),
        TIME("time", "timeOf"),
        DEPICTS("depicts", "depictedBy"),
        REPRESENTS("represents", "representedBy"),
        CONTEXT("context", "contextFor"),
        INTERPRETS("interprets", "interpretedBy"),
        AGENT("agent", "agentOf"),
        PATIENT("patient", "patientOf"),
        EXPERIENCER("experiencer", "experiencerOf"),
        STIMULUS("stimulus", "stimulusOf"),
        CAUSER("causer", "causerOf"),
        GOAL("goal", "goalOf"),
        BENEFICIALRY("beneficiary", "beneficiaryOf"),
        THEME("theme", "themeOf"),
        RESULT("result", "resultOf"),
        INSTRUMENT("instrument", "instrumentOf"),
        ACCOMPANIER("accompanier", "accompanierOf"),
        SUMMARIZES("summarizes", "summarizedBy"),
        SPECIALIZES("specializes", "generalizes"),
        EXEMPLIFIES("exemplifies", "exemplifiedBy"),
        PART("part", "partOf"),
        rPROPERTY("property", "propertyOf"),
        USER("user", "userOf"),
        COMPONENT("component", "componentOf"),
        SUBSTANCE("substance", "substanceOf"),
        ENTAILS("entails", "entailedBy"),
        MANNER("manner", "mannerOf"),
        STATE("state", "stateOf"),
        INFLUENCES("influences", "dependsOn");
        
        private final String relation;
        private final String inverse;
        
        Relations(String relation, String inverse) {
            this.relation = relation;
            this.inverse = inverse;
        }
        
        public String getRelation(){
            return relation;
        }
        
        public String getInverse(){
            return inverse;
        }
    }
    
    public EnumMap getRelations(){
        EnumMap<Relations, String> getRelations = new EnumMap<Relations, String>(Relations.class );
        for(Relations r : Relations.values()){
            getRelations.put(r, r.getRelation());
            
        }
        return getRelations;
    }

    
}
