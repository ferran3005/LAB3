package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.States;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;

public class Listen extends Behaviour {


    public void listen() {
        ACLMessage msg = this.myAgent.receive();
        States currentState = ((SituatedAgent)this.myAgent).currentState;
        if (msg != null) {
            if(currentState.equals(States.NO_UPDATES_SENT) && msg.getProtocol().equals(OBSERVATIONS_PROTOCOL)) {
                this.myAgent.addBehaviour(new SendObservations(msg.createReply()));
            }
            //TODO: Define state machine and controls
        }
        else {
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
