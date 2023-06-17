package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans;

import bdi4jade.annotation.Parameter.Direction;
import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.plan.Plan;
import bdi4jade.plan.Plan.EndState;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BDIAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BdiStates;
import jade.lang.acl.ACLMessage;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.AGENT_STATE;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.COMPUTED_POSITION;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.MOVEMENT_PROTOCOL;

public class RequestMovementPlanBody extends BeliefGoalPlanBody {
    @Override
    protected void execute() {
        String computedPosition = (String) getCapability().getBeliefBase().getBelief(COMPUTED_POSITION).getValue();
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(((BDIAgent) this.myAgent).situatedData.getSituatedAgent());
        request.setProtocol(MOVEMENT_PROTOCOL);
        request.setSender(this.myAgent.getAID());
        request.setContent(computedPosition);
        this.myAgent.send(request);

        Belief agentState = new TransientBelief(AGENT_STATE, BdiStates.MOVEMENT_REQUEST_SENT);
        getCapability().getBeliefBase().addOrUpdateBelief(agentState);
        setEndState(EndState.SUCCESSFUL);
        //TODO: poner tiemout
    }
}
