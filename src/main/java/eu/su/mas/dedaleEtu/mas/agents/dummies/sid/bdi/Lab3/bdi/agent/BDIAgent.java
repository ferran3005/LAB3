package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent;

import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.belief.TransientPredicate;
import bdi4jade.core.GoalUpdateSet;
import bdi4jade.core.SingleCapabilityAgent;
import bdi4jade.event.GoalEvent;
import bdi4jade.event.GoalListener;
import bdi4jade.goal.*;
import bdi4jade.plan.DefaultPlan;
import bdi4jade.plan.Plan;
import bdi4jade.reasoning.*;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans.ComputeNextPositionPlanBody;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans.FindSituatedPlanBody;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans.KeepMailboxEmptyPlanBody;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans.RegisterPlanBody;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals.SPARQLGoal;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans.RequestMovementPlanBody;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans.RequestObservationsPlanBody;
import jade.core.AID;
import jade.lang.acl.MessageTemplate;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.*;

public class BDIAgent extends SingleCapabilityAgent {
    public ArrayList<Location> visited;
    public Stack<Location> stack;
    public Stack<Location> path;

    public AID situatedAgent;

    public BDIAgent() {
        // Create initial beliefs
        Belief iAmRegistered = new TransientPredicate(I_AM_REGISTERED, false);
        Belief ontology = new TransientBelief(ONTOLOGY, loadOntology());
        Belief allMapExplored = new TransientPredicate(ALL_MAP_EXPLORED, false);
        Belief isInfoUpdated = new TransientPredicate(IS_INFO_UPDATED, false);

        // Add initial desires
        Goal registerGoal = new PredicateGoal(I_AM_REGISTERED, true);
        Goal findSituatedGoal = new SPARQLGoal(ONTOLOGY, QUERY_SITUATED_AGENT);
        Goal allMapExploredGoal = new PredicateGoal(ALL_MAP_EXPLORED, true);

        addGoal(registerGoal);
        addGoal(findSituatedGoal);
        addGoal(allMapExploredGoal);

        // Declare goal templates
        GoalTemplate registerGoalTemplate = matchesGoal(registerGoal);
        GoalTemplate findSituatedTemplate = matchesGoal(findSituatedGoal);
        GoalTemplate allMapExploredTemplate = matchesGoal(allMapExploredGoal);

        // Assign plan bodies to goals
        Plan registerPlan = new DefaultPlan(registerGoalTemplate, RegisterPlanBody.class);
        Plan findSituatedPlan = new DefaultPlan(findSituatedTemplate, FindSituatedPlanBody.class);
        Plan keepMailboxEmptyPlan = new DefaultPlan(MessageTemplate.MatchAll(), KeepMailboxEmptyPlanBody.class);//TODO: Esto se puede fragmentar como
        //TODO -->
//        Plan ObservationProtocolPlan = new DefaultPlan(MessageTemplate.MatchProtocol(OBSERVATIONS_PROTOCOL), ObservationProtocolMessagePlan.class);
//        Plan MovedProtocolPlan = new DefaultPlan(MessageTemplate.MatchProtocol(MOVED_PROTOCOL), MovedProtocolMessagePlan.class);
        //TODO <--

        Plan requestObservationPlan = requestObservationsPlan(allMapExploredTemplate); //Only sends a message requesting information
        Plan computeNextPositionPlan = computeNextPositionPlan(allMapExploredTemplate); //Does the actual DFS and whole calculation to decide on next position
        Plan RequestMovementPlan = requestMovementPlan(allMapExploredTemplate); //Only sends a message requesting movement


        // Init plan library
        getCapability().getPlanLibrary().addPlan(registerPlan);
        getCapability().getPlanLibrary().addPlan(findSituatedPlan);
        getCapability().getPlanLibrary().addPlan(keepMailboxEmptyPlan);

        getCapability().getPlanLibrary().addPlan(requestObservationPlan);
        getCapability().getPlanLibrary().addPlan(computeNextPositionPlan);
        getCapability().getPlanLibrary().addPlan(RequestMovementPlan);

        // Init belief base
        getCapability().getBeliefBase().addBelief(iAmRegistered);
        getCapability().getBeliefBase().addBelief(ontology);
        getCapability().getBeliefBase().addBelief(allMapExplored);
        getCapability().getBeliefBase().addBelief(isInfoUpdated);

        // Add a goal listener to track events
        enableGoalMonitoring();

        // Override BDI cycle meta-functions, if needed
        overrideBeliefRevisionStrategy();
        overrideOptionGenerationFunction();
        overrideDeliberationFunction();
        overridePlanSelectionStrategy();
    }

    private void overrideBeliefRevisionStrategy() {
        this.getCapability().setBeliefRevisionStrategy(new DefaultBeliefRevisionStrategy() {
            @Override
            public void reviewBeliefs() {
                // This method should check belief base consistency,
                // make new inferences, etc.
                // The default implementation does nothing
            }
        });
    }

    private void overrideOptionGenerationFunction() {
        this.getCapability().setOptionGenerationFunction(new DefaultOptionGenerationFunction() {
            @Override
            public void generateGoals(GoalUpdateSet agentGoalUpdateSet) {
                // A GoalUpdateSet contains the goal status for the agent:
                // - Current goals (.getCurrentGoals)
                // - Generated goals, existing but not adopted yet (.getGeneratedGoals)
                // - Dropped goals, discarded forever (.getDroppedGoals)
                // This method should update these three sets (current,
                // generated, dropped).
                // The default implementation does nothing
            }
        });
    }

    private void overrideDeliberationFunction() {
        this.getCapability().setDeliberationFunction(new DefaultDeliberationFunction() {
            @Override
            public Set<Goal> filter(Set<GoalUpdateSet.GoalDescription> agentGoals) {
                // This method should choose which of the current goal
                // of the agent should become intentions in this iteration
                // of the BDI cycle.
                // The default implementation chooses all goals with no
                // actual filtering.
                return super.filter(agentGoals);
            }
        });
    }

    private void overridePlanSelectionStrategy() {
        this.getCapability().setPlanSelectionStrategy(new DefaultPlanSelectionStrategy() {
            @Override
            public Plan selectPlan(Goal goal, Set<Plan> capabilityPlans) {
                // This method should return a plan from a list of
                // valid (ordered) plans for fulfilling a particular goal.
                // The default implementation just chooses
                // the first plan of the list.
                return super.selectPlan(goal, capabilityPlans);
            }
        });
    }

    private void enableGoalMonitoring() {
        this.addGoalListener(new GoalListener() {
            @Override
            public void goalPerformed(GoalEvent goalEvent) {
                if(goalEvent.getStatus() == GoalStatus.ACHIEVED) {
                    System.out.println("BDI: " + goalEvent.getGoal() + " " +
                            "fulfilled!");
                }
            }
        });
    }

    private GoalTemplate matchesGoal(Goal goalToMatch) {
        return new GoalTemplate() {
            @Override
            public boolean match(Goal goal) {
                return goal == goalToMatch;
            }
        };
    }

    private Model loadOntology() {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        OntDocumentManager dm = model.getDocumentManager();
        URL fileAsResource = getClass().getClassLoader().getResource("example.owl");
        dm.addAltEntry("example", fileAsResource.toString());
        model.read("example");
        return model;
    }

    private Plan requestObservationsPlan(GoalTemplate allMapExploredTemplate) {
        return new DefaultPlan(allMapExploredTemplate, RequestObservationsPlanBody.class) {
            @Override
            public boolean isContextApplicable(Goal goal) {
                boolean isInfoUpdated = (boolean) getCapability().getBeliefBase().getBelief(IS_INFO_UPDATED).getValue();
                return !isInfoUpdated;
            }
        };
    }
    private Plan requestMovementPlan(GoalTemplate allMapExploredTemplate) {
        return new DefaultPlan(allMapExploredTemplate, RequestMovementPlanBody.class) {
            @Override
            public boolean isContextApplicable(Goal goal) {
                // TODO
                return false;
            }
        };
    }
    private Plan computeNextPositionPlan(GoalTemplate allMapExploredTemplate) {
        return new DefaultPlan(allMapExploredTemplate, ComputeNextPositionPlanBody.class) {
            @Override
            public boolean isContextApplicable(Goal goal) {
                // TODO
                return false;
            }
        };
    }
}
