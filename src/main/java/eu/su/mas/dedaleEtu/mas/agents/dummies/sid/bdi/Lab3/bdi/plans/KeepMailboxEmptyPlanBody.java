package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans;

import bdi4jade.annotation.Parameter;
import bdi4jade.annotation.Parameter.Direction;
import bdi4jade.belief.BeliefBase;
import bdi4jade.goal.Goal;
import bdi4jade.goal.GoalTemplate;
import bdi4jade.plan.DefaultPlan;
import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.AbstractPlanBody;
import com.google.gson.Gson;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.Handlers.OntologyManager;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BDIAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BdiStates;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals.ComputeNextPositionGoal;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals.SendMovementRequestGoal;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals.SendUpdateRequestGoal;
import jade.lang.acl.ACLMessage;
import org.apache.jena.rdf.model.Model;

import java.util.List;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.AGENT_STATE;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.COMPUTED_POSITION;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.MOVEMENT_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.observationsType;

public class KeepMailboxEmptyPlanBody extends AbstractPlanBody {  //TODO: MUCHO de lo que hay aquí se puede extraer em handlers (i.e. sparql queries, message handling, goals, etc.)
    private ACLMessage message;
    @Override
    public void action() {
        setEndState(Plan.EndState.SUCCESSFUL);
        BeliefBase beliefBase = getCapability().getBeliefBase();
        BdiStates agentState = (BdiStates) beliefBase.getBelief(AGENT_STATE).getValue();
        if ((agentState.equals(BdiStates.UPDATE_REQUEST_SENT) || agentState.equals(BdiStates.UPDATE_REQUEST_AGREED))
                && message.getProtocol().equals(OBSERVATIONS_PROTOCOL)) {
            handleObservationResponses(message);
        }
        else if ((agentState.equals(BdiStates.MOVEMENT_REQUEST_SENT) || agentState.equals(BdiStates.MOVEMENT_REQUEST_AGREED))
                && message.getProtocol().equals(MOVEMENT_PROTOCOL)) {
            handleMovementResponses(message);
        }
        setEndState(Plan.EndState.SUCCESSFUL);
        ((BDIAgent)getCapability().getMyAgent()).log.add(new Couple<>(message, Direction.IN));
    }

    @Parameter(direction = Parameter.Direction.IN)
    public void setMessage(ACLMessage msgReceived) throws InterruptedException {
        message = msgReceived;
    }

    @Override
    public int onEnd() {
        return 0;
    }
    private void updateOntologyWithObservations(String json) {

        String situatedAgentName = ((BDIAgent)getCapability().getMyAgent()).situatedAgent.getLocalName();
        Model model = (Model) getCapability().getBeliefBase().getBelief(ONTOLOGY).getValue();

        List<Couple<Location, List<Couple<Observation, Integer>>>> observations =
                new Gson().fromJson(json, observationsType());
        //TODO llamar a Ontologymanager para actualizar las hojas
        String originLocationId = observations.get(0).getLeft().getLocationId();
        ((BDIAgent)getCapability().getMyAgent()).ontologyManager.addCurrentPosition(
                situatedAgentName,
                originLocationId,
                model);

        for(Couple<Location, List<Couple<Observation, Integer>>> obs: observations) {
            ((BDIAgent)getCapability().getMyAgent()).ontologyManager.addAdjacentPosition(
                    originLocationId,
                    obs.getLeft().getLocationId(),
                    model); //añadimos la posición adyacente

            for(Couple<Observation, Integer> observation: obs.getRight()) {
                ((BDIAgent)getCapability().getMyAgent()).ontologyManager.addObservation(
                        obs.getLeft().getLocationId(),
                        observation.getLeft(),
                        (observation.getRight() != null) ? observation.getRight() : 0,
                        model);
            }
        }
    }

    private void handleMovementResponses(ACLMessage message) {
        if(message.getPerformative() == ACLMessage.AGREE) {
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.MOVEMENT_REQUEST_AGREED);
            //Todo: reset timeout
        }
        else if(message.getPerformative() == ACLMessage.REFUSE) { //todo: si nos rechazan, significa que el agente situado decide descartar la ruta por X motivo (peligro de muerte)
            ((BDIAgent)getCapability().getMyAgent()).dfsHandler.discardTop();
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.INITIAL);
            addRequestUpdateGoal();
        }
        else if(message.getPerformative() == ACLMessage.FAILURE) { //todo: si falla, significa que hay algún agente en medio y hay que hacer retry
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.MOVEMENT_COMPUTED);
            addRequestMovementGoal();
        }
        else if(message.getPerformative() == ACLMessage.INFORM) {
            Model model = (Model) getCapability().getBeliefBase().getBelief(ONTOLOGY).getValue();
            String previousLocation = ((BDIAgent)getCapability().getMyAgent()).ontologyManager.getSituatedPosition(model);
            String currentPosition = (String) getCapability().getBeliefBase().getBelief(COMPUTED_POSITION).getValue();

            // public void addCurrentPosition(String situatedAgentName, String locationId, Model model)
            String situatedAgentName = ((BDIAgent)getCapability().getMyAgent()).situatedAgent.getLocalName();
            ((BDIAgent)getCapability().getMyAgent()).ontologyManager.addCurrentPosition(situatedAgentName, currentPosition, model);
           // ((BDIAgent)getCapability().getMyAgent()).ontologyManager.addCurrentPosition(situatedAgentName, originLocationId, model);
            ((BDIAgent) getCapability().getMyAgent()).dfsHandler.updateAfterMovement(previousLocation, currentPosition);
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.INITIAL);
            addRequestUpdateGoal();
        }
    }

    private void handleObservationResponses(ACLMessage message) {
        if(message.getPerformative() == ACLMessage.AGREE) {
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.UPDATE_REQUEST_AGREED);
            //Todo: reset timeout
        }
        else if(message.getPerformative() == ACLMessage.INFORM) {
            updateOntologyWithObservations(message.getContent());
            boolean notEmptyStack = ((BDIAgent)getCapability().getMyAgent()).dfsHandler.updateStack(message.getContent());
            if(notEmptyStack) {
                getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.UPDATED);
                addComputeNextPositionGoal();
            }
        }
    }

    void addRequestUpdateGoal() {
        Goal sendUpdateRequestGoal = new SendUpdateRequestGoal(AGENT_STATE + "UpdateReq");
        getCapability().getMyAgent().addGoal(sendUpdateRequestGoal);
        GoalTemplate sendUpdateRequestGoalTemplate = matchesGoal(sendUpdateRequestGoal);
        Plan requestObservationPlan = requestObservationsPlan(sendUpdateRequestGoalTemplate);
        getCapability().getPlanLibrary().addPlan(requestObservationPlan);
    }
    void addComputeNextPositionGoal() {
        Goal computeNextPositionGoal = new ComputeNextPositionGoal(AGENT_STATE + "Compute");
        getCapability().getMyAgent().addGoal(computeNextPositionGoal);
        GoalTemplate computeNextPositionGoalTemplate = matchesGoal(computeNextPositionGoal);
        Plan computeNextMovementPlan = computeNextMovementPlan(computeNextPositionGoalTemplate);
        getCapability().getPlanLibrary().addPlan(computeNextMovementPlan);
    }
    void addRequestMovementGoal() {
        Goal sendMovementRequestGoal = new SendMovementRequestGoal(AGENT_STATE + "MoveReq");
        getCapability().getMyAgent().addGoal(sendMovementRequestGoal);
        GoalTemplate sendMovementRequestGoalTemplate = matchesGoal(sendMovementRequestGoal);
        Plan requestMovementPlan = requestMovementPlan(sendMovementRequestGoalTemplate);
        getCapability().getPlanLibrary().addPlan(requestMovementPlan);
    }

    private GoalTemplate matchesGoal(Goal goalToMatch) {
        return new GoalTemplate() {
            @Override
            public boolean match(Goal goal) {
                return goal == goalToMatch;
            }
        };
    }
    private Plan requestObservationsPlan(GoalTemplate sendUpdateRequestGoalTemplate) {
        return new DefaultPlan(sendUpdateRequestGoalTemplate, RequestObservationsPlanBody.class) {
            @Override
            public boolean isContextApplicable(Goal goal) {
                return getCapability().getBeliefBase().getBelief(AGENT_STATE).getValue().equals(BdiStates.INITIAL);
            }
        };
    }
    private Plan computeNextMovementPlan(GoalTemplate computeNextMovementGoalTemplate) {
        return new DefaultPlan(computeNextMovementGoalTemplate, ComputeNextPositionPlanBody.class) {
            @Override
            public boolean isContextApplicable(Goal goal) {
                return getCapability().getBeliefBase().getBelief(AGENT_STATE).getValue().equals(BdiStates.UPDATED);
            }
        };
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
