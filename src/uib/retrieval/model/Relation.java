/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.retrieval.model;

import java.util.*;
import uib.retrieval.model.EnumRelations.Relations;
/**
 *
 * @author Olav
 */
public class Relation  implements Comparable {
    private int source, target;
    private String type;
    private final static EnumRelations enumRelations = new EnumRelations();
    public final static HashMap<String, String> relationMapping;
    public final static HashMap<String, String> relationMappingInverse;
    private final static Map<uib.retrieval.model.EnumRelations.Relations, String> relation = enumRelations.getRelations();

    static {
        relationMapping = new HashMap<String, String>(27);
        for (Relations r : relation.keySet()){
            relationMapping.put(r.getRelation(), r.getInverse());
        }
        

        relationMappingInverse = new HashMap<String, String>(relationMapping.size());
        for (Iterator<String> iterator = relationMapping.keySet().iterator(); iterator.hasNext();) {
            String s = iterator.next();
            relationMappingInverse.put(relationMapping.get(s), s);
        }

    }


    /**
     * Creates a new Relation with given source, target and name
     *
     * @param source defines the node ID of the source
     * @param target defines the node ID of the target
     * @param type
     */
    public Relation(int source, int target, String type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }

    public String getType() {
        return type;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return (type + " " + source + " " + target);
    }

    public boolean isSourceOrTarget(int nodeID) {
        boolean result = false;
        if (source == nodeID || target == nodeID) result = true;
        return result;
    }

    public int compareTo(Object o) {
        String s = ((Relation) o).toString();
        return (toString().compareTo(s));
    }

    public Relation clone() {
        return new Relation(source, target, new String(type));
    }

    public boolean equals(Object o) {
        return o.toString().equals(toString());
    }

    /**
     * eliminates an inverse relation as there is an inverse
     * defined for each possible relation.
     */
    public void eliminateInverse() {
        if (relationMapping.containsKey(type)) {
            type = relationMapping.get(type);
            int tmp = source;
            source = target;
            target = tmp;
        }
    }

    /**
     * Inverts the current relation ..
     */
    public void invert() {
        if (relationMapping.containsKey(type)) {
            type = relationMapping.get(type);
            int tmp = source;
            source = target;
            target = tmp;
        } else {
            type = relationMappingInverse.get(type);
            int tmp = source;
            source = target;
            target = tmp;
        }
    }

    /**
     * Allows the fast inversion of the given relationType
     * @param relationType
     * @return the inverse MPEG-7 relationType or NULL if none found.
     */
    public static String invertRelationType(String relationType) {
        String result = null;
        if (relationMapping.containsKey(relationType)) {
            result = relationMapping.get(relationType);
        } else {
            result = relationMappingInverse.get(relationType);
        }
        return result;
    }
}
