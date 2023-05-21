package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import weka.core.stemmers.Stemmer;

import java.util.List;
import java.util.stream.Collectors;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.MOVEMENT_PROTOCOL;

public class CanIMove extends OneShotBehaviour {


    ACLMessage msg;
    String content;

    public CanIMove(ACLMessage msg, String content){
        super();
        this.msg = msg;
        this.content = content;
    }

    @Override
    public void action() {

        if(canIMove()){
            agreePetition();
            msg.setContent(content);
            this.myAgent.addBehaviour(new MoveToPosition(msg));
        }
        else refusePetition();
    }

    public Boolean canIMove(){
        List<Couple<Location, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent)this.myAgent).observe();
        Location pos = new gsLocation(content);

        return contains(pos, lobs.stream().map(Couple::getLeft).collect(Collectors.toList()));
    }

    public void refusePetition(){
        msg.setPerformative(ACLMessage.REFUSE);
        msg.setProtocol(MOVEMENT_PROTOCOL);
        myAgent.send(msg);
        //((SituatedAgent)this.myAgent).currentState = States.OBSERVATIONS_SENT;
    }

    public void agreePetition(){
        msg.setPerformative(ACLMessage.AGREE);
        msg.setProtocol(MOVEMENT_PROTOCOL);
        myAgent.send(msg);
    }

    private Boolean contains(Location loc, List<Location> locations) {
        for (Location vis : locations) {
            if (loc.getLocationId().equals(vis.getLocationId())) return true;
        }
        return false;
    }
}