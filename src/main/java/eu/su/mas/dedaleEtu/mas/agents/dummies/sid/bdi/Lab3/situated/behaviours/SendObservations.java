package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import com.google.gson.Gson;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;

public class SendObservations extends OneShotBehaviour {
    ACLMessage msg;

    public SendObservations(ACLMessage msg) {
        super();
        this.msg = msg;
    }
    @Override
    public void action() {
        List<Couple<Location, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent)this.myAgent).observe();
        sendObservations(lobs);
    }
    public void sendObservations(List<Couple<Location, List<Couple<Observation, Integer>>>> observations) {
        msg.setPerformative(ACLMessage.AGREE);
        msg.setProtocol(OBSERVATIONS_PROTOCOL);
        msg.setContent(new Gson().toJson(observations));
        myAgent.send(msg);
    }
}
