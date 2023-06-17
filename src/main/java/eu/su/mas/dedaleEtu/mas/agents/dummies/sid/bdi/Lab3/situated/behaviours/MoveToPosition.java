package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import bdi4jade.annotation.Parameter;
import com.google.gson.Gson;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.EntityType;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.MovementData;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.States;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import javax.swing.text.Position;
import java.util.List;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.MOVEMENT_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;

public class MoveToPosition extends  OneShotBehaviour{
    ACLMessage msg;

    public MoveToPosition (ACLMessage msg) {
        super();
        this.msg = msg;
    }
    @Override
    public void action() {

        if(((AbstractDedaleAgent) this.myAgent).moveTo(new gsLocation(msg.getContent()))){
            if(((SituatedAgent) this.myAgent).data.getAgentType().equals(EntityType.AGENT_COLLECTOR.getName())) {
                tryToCollect();
            }
            successMove();
        }
        else failMove();
    }


    public void successMove(){

        List<Couple<Observation, Integer>> freeSpace = ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace();
        int diamondCap = 0;
        int goldCap = 0;
        for(Couple<Observation, Integer> f : freeSpace){
            if(f.getLeft().equals(Observation.DIAMOND))diamondCap = f.getRight();
            else goldCap = f.getRight();
        }

        MovementData movData =  new  MovementData(diamondCap, goldCap, ((AbstractDedaleAgent) this.myAgent)
                .getMyTreasureType());

        msg.setPerformative(ACLMessage.INFORM);
        msg.setProtocol(MOVEMENT_PROTOCOL);
        msg.setContent(new Gson().toJson(movData));
        myAgent.send(msg);
        ((SituatedAgent)this.myAgent).currentState = States.NO_UPDATES_SENT;
    }

    public void tryToCollect(){
        List<Couple<Observation, Integer>> observations = ((AbstractDedaleAgent) this.myAgent).observe().get(0).getRight();
        if(observations.stream().anyMatch(obs -> isMyTreasureType(obs.getLeft()))){
            Observation type = ((AbstractDedaleAgent) this.myAgent).getMyTreasureType();
            boolean unlock = ((AbstractDedaleAgent) this.myAgent).openLock(type);
            if(unlock) ((AbstractDedaleAgent) this.myAgent).pick();
        }
    }

    public Boolean isMyTreasureType(Observation observation){
        Observation type = ((AbstractDedaleAgent) this.myAgent).getMyTreasureType();
        if(observation.equals(type)) return true;
        else {
            if(type.equals(Observation.ANY_TREASURE)) {
                return (observation.equals(Observation.GOLD) || observation.equals(Observation.DIAMOND));
            }
            return false;
        }
    }

    public void failMove(){
        msg.setPerformative(ACLMessage.FAILURE);
        msg.setProtocol(MOVEMENT_PROTOCOL);
        myAgent.send(msg);
        ((SituatedAgent)this.myAgent).currentState = States.OBSERVATIONS_SENT;
    }
}
