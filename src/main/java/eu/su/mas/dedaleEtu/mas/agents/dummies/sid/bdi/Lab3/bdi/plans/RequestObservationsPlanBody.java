package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans;

import bdi4jade.annotation.Parameter.Direction;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BDIAgent;
import jade.lang.acl.ACLMessage;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;

public class RequestObservationsPlanBody extends BeliefGoalPlanBody {

    @Override
    protected void execute() {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(((BDIAgent) this.myAgent).situatedAgent);
        request.setProtocol(OBSERVATIONS_PROTOCOL);
        request.setSender(this.myAgent.getAID());
        this.myAgent.send(request);
        ((BDIAgent) this.myAgent).log.add(new Couple<>(request, Direction.OUT));
    }
}
