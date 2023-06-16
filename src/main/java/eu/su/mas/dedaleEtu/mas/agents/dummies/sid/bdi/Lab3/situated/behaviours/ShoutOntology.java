package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_OUT;

public class ShoutOntology extends OneShotBehaviour {

    ACLMessage msg;
    String modelJson;

    public ShoutOntology(ACLMessage msg, String modelJson) {
        super();
        this.msg = msg;
        this.modelJson = modelJson;
    }
    @Override
    public void action() {
        sendAgree();
        shoutOntology();
    }

    private void sendAgree() {
        ACLMessage agreeMsg = new ACLMessage(ACLMessage.AGREE);
        agreeMsg.setProtocol(SHOUT_ONTOLOGY_PROTOCOL_OUT);
        agreeMsg.addReceiver(((SituatedAgent)this.myAgent).bdiAgent);
        this.myAgent.send(agreeMsg);
    }

    private void shoutOntology() {
        msg.setPerformative(ACLMessage.INFORM);
        msg.setProtocol(SHOUT_ONTOLOGY_PROTOCOL_OUT);
        msg.setContent(modelJson);
        myAgent.send(msg);
    }
}
