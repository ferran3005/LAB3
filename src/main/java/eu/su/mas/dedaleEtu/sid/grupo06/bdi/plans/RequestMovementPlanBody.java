package eu.su.mas.dedaleEtu.sid.grupo06.bdi.plans;

import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.plan.Plan.EndState;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import eu.su.mas.dedaleEtu.sid.grupo06.BDIAgent06;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent.BdiStates;
import jade.lang.acl.ACLMessage;

import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.AGENT_STATE;
import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.COMPUTED_POSITION;
import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.MOVEMENT_PROTOCOL;

public class RequestMovementPlanBody extends BeliefGoalPlanBody {
    @Override
    protected void execute() {
        String computedPosition = (String) getCapability().getBeliefBase().getBelief(COMPUTED_POSITION).getValue();
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(((BDIAgent06) this.myAgent).situatedData.getSituatedAgent());
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
