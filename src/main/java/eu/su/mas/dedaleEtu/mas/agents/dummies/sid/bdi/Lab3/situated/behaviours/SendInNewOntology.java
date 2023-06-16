package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_IN;

public class SendInNewOntology extends OneShotBehaviour {
    ACLMessage msg;

    public SendInNewOntology(ACLMessage msg) {
        super();
        this.msg = msg;
    }
    @Override
    public void action() {
        ACLMessage infoMsg = new ACLMessage(ACLMessage.INFORM);
        infoMsg.setProtocol(SHOUT_ONTOLOGY_PROTOCOL_IN);
        infoMsg.addReceiver(((SituatedAgent)this.myAgent).bdiAgent);
        infoMsg.setContent(msg.getContent());
        this.myAgent.send(infoMsg);
    }
}
