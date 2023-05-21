package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common;

import com.google.gson.reflect.TypeToken;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;

import java.lang.reflect.Type;
import java.util.List;

public class Constants {
    public static String I_AM_REGISTERED = "IAmRegistered";
    public static String ALL_MAP_EXPLORED = "AllMapExplored";
    public static String IS_INFO_UPDATED = "IsInfoUpdated";
    public static String OBSERVATIONS_PROTOCOL = "OBSERVATIONS";
    //public static String MOVEMENT_PROTOCOL = "MOVEMENT";
    public static String MOVEMENT_PROTOCOL = "REQUEST";
    public static String ONTOLOGY = "ontology";
    public static String AGENT_STATE = "agentState";

    public static String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/usuari/ontologies/P2_ontologia";
    public static String QUERY_SITUATED_AGENT =
                    "PREFIX NAMESPACE: <" + ONTOLOGY_NAMESPACE + "#> " +
                    "SELECT ?Agent where {" +
                    " ?Agent a NAMESPACE:Explorer ."+
                    "}";

    public static String QUERY_SITUATED_AGENT_POSITION =
            "PREFIX NAMESPACE: <" + ONTOLOGY_NAMESPACE + "#> " +
            "SELECT ?Position where {" +
            " ?Agent a NAMESPACE:Explorer ."+
            " ?Agent NAMESPACE:hasPosition ?Position ."+
            "}";

    public static Type observationsType() {
        return new TypeToken<List<Couple<gsLocation, List<Couple<Observation, Integer>>>>>(){}.getType();

    }

}
