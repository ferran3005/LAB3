package eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent;

public enum BdiStates {
    UNKNOWN,
    DATA_REQUEST_SENT,
    INITIAL,
    UPDATE_REQUEST_SENT,
    UPDATE_REQUEST_AGREED,
    UPDATED,
    MOVEMENT_COMPUTED,
    MOVEMENT_REQUEST_SENT,
    MOVEMENT_REQUEST_AGREED,
    MOVEMENT_END,
    SHOUT_ONTOLOGY_REQUEST_SENT,
    SHOUT_ONTOLOGY_REQUEST_AGREED,
}