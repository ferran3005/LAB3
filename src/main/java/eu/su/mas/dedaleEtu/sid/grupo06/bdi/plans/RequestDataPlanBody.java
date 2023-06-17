package eu.su.mas.dedaleEtu.sid.grupo06.bdi.plans;

import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import eu.su.mas.dedaleEtu.sid.grupo06.BDIAgent06;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent.BdiStates;
import jade.lang.acl.ACLMessage;

import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.AGENT_STATE;
import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.DATA_PROTOCOL;

public class RequestDataPlanBody extends BeliefGoalPlanBody {
    @Override
    protected void execute() {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(((BDIAgent06) this.myAgent).situatedAgent);
        request.setProtocol(DATA_PROTOCOL);
        request.setSender(this.myAgent.getAID());
        this.myAgent.send(request);

        Belief agentState = new TransientBelief(AGENT_STATE, BdiStates.DATA_REQUEST_SENT);
        getCapability().getBeliefBase().addOrUpdateBelief(agentState);
        setEndState(Plan.EndState.SUCCESSFUL);
    }
}
