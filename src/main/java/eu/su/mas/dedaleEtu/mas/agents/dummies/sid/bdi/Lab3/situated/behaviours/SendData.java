package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import com.google.gson.Gson;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendData extends OneShotBehaviour {
    ACLMessage msg;
    public SendData(ACLMessage msg) {
        super();
        this.msg = msg;
    }
    @Override
    public void action() {
        msg.setPerformative(ACLMessage.INFORM);
        msg.setContent(new Gson().toJson(((SituatedAgent)myAgent).data));
        this.myAgent.send(msg);
    }
}
