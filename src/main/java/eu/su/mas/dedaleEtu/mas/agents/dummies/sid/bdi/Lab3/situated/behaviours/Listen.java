package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.States;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.MOVEMENT_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_IN;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_OUT;

public class Listen extends Behaviour {


    public void listen() {
        ACLMessage msg = this.myAgent.receive();
        States currentState = ((SituatedAgent) this.myAgent).currentState;
        if (msg != null) {
            String Sender = msg.getSender().getName();
            String BDIAgent = ((SituatedAgent)this.myAgent).bdiAgent.getName();
            if(Sender.equals(BDIAgent)) {
                if (currentState.equals(States.NO_UPDATES_SENT) && msg.getProtocol().equals(OBSERVATIONS_PROTOCOL)) {
                    this.myAgent.addBehaviour(new SendObservations(msg.createReply()));
                } else if (currentState.equals(States.OBSERVATIONS_SENT) && msg.getProtocol().equals(MOVEMENT_PROTOCOL)) {
                    this.myAgent.addBehaviour(new CanIMove(msg.createReply(), msg.getContent()));
                } else if (msg.getProtocol().equals(SHOUT_ONTOLOGY_PROTOCOL_OUT)) {
                    this.myAgent.addBehaviour(new ShoutOntology(msg.createReply(), msg.getContent()));
                }
                else {
                    block();
                }
            }
            else {
                if(msg.getProtocol().equals(SHOUT_ONTOLOGY_PROTOCOL_OUT)) {
                    this.myAgent.addBehaviour(new SendInNewOntology(msg));
                }
                else {
                    block();
                }
                //TODO: mirar si el protocolo es ONTOLOGY_06
            }
        } else {
            block();
        }

    }

    @Override
    public void action() {
        listen();
    }

    @Override
    public boolean done() {
        return false;
    }
}
