package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common;

public class Constants {
    public static String I_AM_REGISTERED = "IAmRegistered";
    public static String ALL_MAP_EXPLORED = "AllMapExplored";
    public static String IS_INFO_UPDATED = "IsInfoUpdated";
    public static String OBSERVATIONS_PROTOCOL = "OBSERVATIONS";
    public static String MOVEMENT_PROTOCOL = "MOVEMENT";
    public static String ONTOLOGY = "ontology";

    public static String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/usuari/ontologies/P2_ontologia";
    public static String QUERY_SITUATED_AGENT =
                    "PREFIX NAMESPACE: <" + ONTOLOGY_NAMESPACE + "#> " +
                    "SELECT ?Agent where {" +
                    " ?Agent a NAMESPACE:Explorer ."+
                    "}";
}
