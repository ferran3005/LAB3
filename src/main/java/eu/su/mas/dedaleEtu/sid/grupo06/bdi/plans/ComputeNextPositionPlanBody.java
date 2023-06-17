package eu.su.mas.dedaleEtu.sid.grupo06.bdi.plans;

import bdi4jade.belief.TransientBelief;
import bdi4jade.goal.Goal;
import bdi4jade.goal.GoalTemplate;
import bdi4jade.plan.DefaultPlan;
import bdi4jade.plan.Plan;
import bdi4jade.plan.Plan.EndState;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import eu.su.mas.dedaleEtu.sid.grupo06.BDIAgent06;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent.BdiStates;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent.SituatedData;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.goals.SendMovementRequestGoal;
import jade.core.AID;
import org.apache.jena.rdf.model.Model;


import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.AGENT_STATE;
import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.COMPUTED_POSITION;
import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.ONTOLOGY;

public class ComputeNextPositionPlanBody extends BeliefGoalPlanBody {
    @Override
    protected void execute() {

        Model model = (Model) getBeliefBase().getBelief(ONTOLOGY).getValue();
        String situatedName = ((BDIAgent06) this.myAgent).situatedData.getSituatedAgent().getLocalName();

        SituatedData data = ((BDIAgent06) this.myAgent).situatedData;
        AID situatedAgent = ((BDIAgent06) this.myAgent).situatedData.getSituatedAgent();
        String nextMove = ((BDIAgent06) this.myAgent).routeHandler.computeNextPosition(model, situatedAgent, data);
        getCapability().getBeliefBase().addOrUpdateBelief(new TransientBelief(COMPUTED_POSITION, nextMove));
        getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.MOVEMENT_COMPUTED);
        addRequestMovementGoal();
        setEndState(EndState.SUCCESSFUL);
    }

    private GoalTemplate matchesGoal(Goal goalToMatch) {
        return new GoalTemplate() {
            @Override
            public boolean match(Goal goal) {
                return goal == goalToMatch;
            }
        };
    }

    void addRequestMovementGoal() {
        Goal sendMovementRequestGoal = new SendMovementRequestGoal(AGENT_STATE);
        getCapability().getMyAgent().addGoal(sendMovementRequestGoal);
        GoalTemplate sendMovementRequestGoalTemplate = matchesGoal(sendMovementRequestGoal);
        Plan requestMovementPlan = requestMovementPlan(sendMovementRequestGoalTemplate);
        getCapability().getPlanLibrary().addPlan(requestMovementPlan);
    }

    private Plan requestMovementPlan(GoalTemplate sendMovementRequestGoalTemplate) {
        return new DefaultPlan(sendMovementRequestGoalTemplate, RequestMovementPlanBody.class) {
            @Override
            public boolean isContextApplicable(Goal goal) {
                return getCapability().getBeliefBase().getBelief(AGENT_STATE).getValue().equals(BdiStates.MOVEMENT_COMPUTED);
            }
        };
    }
}
