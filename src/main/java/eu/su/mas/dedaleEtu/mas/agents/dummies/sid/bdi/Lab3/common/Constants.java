package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common;

import com.google.gson.reflect.TypeToken;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import org.apache.jena.rdf.model.Statement;

import java.lang.reflect.Type;
import java.util.List;

public class Constants {
    public static String I_AM_REGISTERED = "IAmRegistered";
    public static String OBSERVATIONS_PROTOCOL = "OBSERVATIONS06";
    public static String MOVEMENT_PROTOCOL = "MOVEMENT06";
    public static String SHOUT_ONTOLOGY_PROTOCOL_OUT = "06shoutOntologyOut";
    public static String SHOUT_ONTOLOGY_PROTOCOL_IN = "06shoutOntologyIn";
    public static String DATA_PROTOCOL = "DATA06";

    public static String ONTOLOGY = "ontology";

    public static String AGENT_STATE = "agentState";
    public static String COMPUTED_POSITION = "computedPosition";
    public static String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/usuari/ontologies/P2_ontologia";
    public static String QUERY_SITUATED_AGENT =
            "PREFIX NAMESPACE: <" + ONTOLOGY_NAMESPACE + "#> " +
                    "SELECT ?Agent where {" +
                    " ?Agent a NAMESPACE:Agent ." +
                    "}";

    public static String QUERY_SITUATED_AGENT_POSITION(String situatedName) { //TODO: esto está hardcodeado con explorer, actualizarlo con AID
        return "PREFIX NAMESPACE: <" + ONTOLOGY_NAMESPACE + "#> " +
                "SELECT ?Position_id where {" +
                " BIND(<" + ONTOLOGY_NAMESPACE + "#" + situatedName + "> as ?Agent) ." +
                " ?Agent NAMESPACE:is_in ?Position ." +
                " ?Position NAMESPACE:position_id ?Position_id ." +
                "}";
    }

    public static String QUERY_ADJACENT_CELLS(String locationId) {
        return "PREFIX NAMESPACE: <" + ONTOLOGY_NAMESPACE + "#> " +
                "SELECT ?adjacentLocationId " +
                "where {" +
                "?cell a NAMESPACE:Node ." +
                "?cell NAMESPACE:position_id " + locationId + " ." +
                "?cell NAMESPACE:is_adjacent_to ?adjacentCell ." +
                "?adjacentCell NAMESPACE:position_id ?adjacentLocationId ." +
                "}";
    }

    public static Type observationsType() {
        return new TypeToken<List<Couple<gsLocation, List<Couple<Observation, Integer>>>>>() {
        }.getType();
    }

    public static Type statementsType() {
        return new TypeToken<List<Statement>>() {
        }.getType();
    }



}
