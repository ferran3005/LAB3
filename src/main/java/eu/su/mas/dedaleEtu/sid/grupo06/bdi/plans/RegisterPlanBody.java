package eu.su.mas.dedaleEtu.sid.grupo06.bdi.plans;

import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.I_AM_REGISTERED;

public class RegisterPlanBody extends BeliefGoalPlanBody {
    @Override
    public void execute() {
        Agent agent = this.myAgent;

        final Object[] args = myAgent.getArguments();
        String name = "BDISituatedAgent06";
        if(args.length > 0)
            name = (String) args[1];

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(name);
        sd.setType("bdi");
        dfd.addServices(sd);
        try {
            DFService.register(this.myAgent, dfd);
            getBeliefBase().updateBelief(I_AM_REGISTERED, true);
            // This is valid but redundant
            // (because the goal implementation will check the belief anyway):
            // setEndState(Plan.EndState.SUCCESSFUL);
        } catch (FIPAException e) {
            setEndState(Plan.EndState.FAILED);
            e.printStackTrace();
        }
    }
}
