package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans;

import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.goal.Goal;
import bdi4jade.goal.GoalTemplate;
import bdi4jade.plan.DefaultPlan;
import bdi4jade.plan.Plan;
import bdi4jade.plan.Plan.EndState;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BDIAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BdiStates;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals.SendMovementRequestGoal;
import org.apache.commons.compress.utils.Lists;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;

import java.util.List;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.AGENT_STATE;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.COMPUTED_POSITION;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY;

public class ComputeNextPositionPlanBody extends BeliefGoalPlanBody {
    @Override
    protected void execute() {

        Model model = (Model) getBeliefBase().getBelief(ONTOLOGY).getValue();
        String situatedName = ((BDIAgent) this.myAgent).situatedAgent.getLocalName();
        String currentPosition = ((BDIAgent) this.myAgent).ontologyManager.getSituatedPosition(model, situatedName);
        List<String>  adjacentNodes = ((BDIAgent) this.myAgent).ontologyManager.getAdjacentCells(model, currentPosition);

        String nextMove = ((BDIAgent) this.myAgent).dfsHandler.computeNextPosition(adjacentNodes);
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
