package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import bdi4jade.annotation.Parameter;
import com.google.gson.Gson;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
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
            succesMove();
        }
        else failMove();
    }


    public void succesMove(){
        String position = ((AbstractDedaleAgent) this.myAgent).getCurrentPosition().toString();
        msg.setPerformative(ACLMessage.INFORM);
        msg.setProtocol(MOVEMENT_PROTOCOL);
        msg.setContent(position + ";" + msg.getContent());
        myAgent.send(msg);
        ((SituatedAgent)this.myAgent).currentState = States.NO_UPDATES_SENT;
    }

    public void failMove(){
        msg.setPerformative(ACLMessage.FAILURE);
        msg.setProtocol(MOVEMENT_PROTOCOL);
        myAgent.send(msg);
    }
}
