package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans;

import bdi4jade.annotation.Parameter;
import bdi4jade.annotation.Parameter.Direction;
import bdi4jade.belief.Belief;
import bdi4jade.belief.BeliefBase;
import bdi4jade.belief.TransientBelief;
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
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.SituatedData;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals.ComputeNextPositionGoal;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals.ComputeRandomMovementGoal;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals.SendMovementRequestGoal;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals.SendUpdateRequestGoal;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals.ShoutOntologyGoal;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.MovementData;
import jade.lang.acl.ACLMessage;
import net.sourceforge.plantuml.graph.Move;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.impl.OntModelImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Random;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.AGENT_STATE;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.COMPUTED_POSITION;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.DATA_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.MOVEMENT_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_IN;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_OUT;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.observationsType;

public class KeepMailboxEmptyPlanBody extends AbstractPlanBody {  //TODO: MUCHO de lo que hay aquí se puede extraer em handlers (i.e. sparql queries, message handling, goals, etc.)
    private ACLMessage message;

    @Override
    public void action() {
        if(message.getSender().getName().equals(((BDIAgent) getCapability().getMyAgent()).situatedAgent.getName())) {
            setEndState(Plan.EndState.SUCCESSFUL);
            BeliefBase beliefBase = getCapability().getBeliefBase();
            BdiStates agentState = (BdiStates) beliefBase.getBelief(AGENT_STATE).getValue();
            if ((agentState.equals(BdiStates.UPDATE_REQUEST_SENT) || agentState.equals(BdiStates.UPDATE_REQUEST_AGREED))
                    && message.getProtocol().equals(OBSERVATIONS_PROTOCOL)) {
                handleObservationResponses(message);
            } else if ((agentState.equals(BdiStates.MOVEMENT_REQUEST_SENT) || agentState.equals(BdiStates.MOVEMENT_REQUEST_AGREED))
                    && message.getProtocol().equals(MOVEMENT_PROTOCOL)) {
                handleMovementResponses(message);
            }else if ((agentState.equals(BdiStates.SHOUT_ONTOLOGY_REQUEST_SENT) || agentState.equals(BdiStates.SHOUT_ONTOLOGY_REQUEST_AGREED))
                    && message.getProtocol().equals(SHOUT_ONTOLOGY_PROTOCOL_OUT)) {
                handleShoutOntologyResponses(message);
            } else if(message.getProtocol().equals(SHOUT_ONTOLOGY_PROTOCOL_IN)) {
                mergeOntologies(message.getContent());
            } else if((agentState.equals(BdiStates.DATA_REQUEST_SENT) && message.getProtocol().equals(DATA_PROTOCOL))) {
                handleDataResponses(message);
            }
            setEndState(Plan.EndState.SUCCESSFUL);
            ((BDIAgent) getCapability().getMyAgent()).log.add(new Couple<>(message, Direction.IN));
        }
    }

    private void handleDataResponses(ACLMessage message) {
        ((BDIAgent) getCapability().getMyAgent()).situatedData = new Gson().fromJson(message.getContent(), SituatedData.class);
        ((BDIAgent) getCapability().getMyAgent()).situatedData.setSituatedAgent(((BDIAgent) getCapability().getMyAgent()).situatedAgent);
        Belief agentState = new TransientBelief(AGENT_STATE, BdiStates.INITIAL);
        getCapability().getBeliefBase().addOrUpdateBelief(agentState);

        addRequestUpdateGoal();
    }

    private void mergeOntologies(String content) {
        Model model = (Model) getCapability().getBeliefBase().getBelief(ONTOLOGY).getValue();
        Model model2 =  new OntModelImpl(OntModelSpec.OWL_MEM);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
        RDFDataMgr.read(model2, inputStream, RDFFormat.JSONLD_COMPACT_PRETTY.getLang());
        OntologyManager.mergeOntology(model, model2);
    }


    @Parameter(direction = Parameter.Direction.IN)
    public void setMessage(ACLMessage msgReceived) {
        message = msgReceived;
    }

    @Override
    public int onEnd() {
        return 0;
    }

    private void updateOntologyWithObservations(String json) {

        String situatedAgentName = ((BDIAgent) getCapability().getMyAgent()).situatedData.getSituatedAgent().getLocalName();
        Model model = (Model) getCapability().getBeliefBase().getBelief(ONTOLOGY).getValue();

        List<Couple<Location, List<Couple<Observation, Integer>>>> observations =
                new Gson().fromJson(json, observationsType());

        String originLocationId = observations.get(0).getLeft().getLocationId();
        OntologyManager.addCurrentPosition(
                situatedAgentName,
                originLocationId,
                model);

        Boolean actualWind = observations.get(0).getRight().stream().anyMatch(o -> o.getLeft() == Observation.WIND);
        for (Couple<Location, List<Couple<Observation, Integer>>> obs : observations) {
            Boolean nextWind = obs.getRight().stream().anyMatch(o -> o.getLeft() == Observation.WIND);
            if(!actualWind  || !nextWind) {
                OntologyManager.addAdjacentPosition(
                        originLocationId,
                        obs.getLeft().getLocationId(),
                        model); //añadimos la posición adyacente

                for (Couple<Observation, Integer> observation : obs.getRight()) {


                    OntologyManager.addObservation(
                            obs.getLeft().getLocationId(),
                            observation.getLeft(),
                            (observation.getRight() != null) ? observation.getRight() : 0,
                            model);
                }
            }
        }
    }

    private void handleShoutOntologyResponses(ACLMessage message) {
        if (message.getPerformative() == ACLMessage.AGREE) {
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.SHOUT_ONTOLOGY_REQUEST_AGREED);
        } else if (message.getPerformative() == ACLMessage.INFORM) {
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.INITIAL);
            addRequestUpdateGoal();
        }
    }

    private void handleMovementResponses(ACLMessage message) {
        if (message.getPerformative() == ACLMessage.AGREE) {
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.MOVEMENT_REQUEST_AGREED);
            //Todo: reset timeout
        } else if (message.getPerformative() == ACLMessage.REFUSE) { //todo: si nos rechazan, significa que el agente situado decide descartar la ruta por X motivo (peligro de muerte)
            ((BDIAgent) getCapability().getMyAgent()).routeHandler.discardTop(); //TODO: creo que esto ya no nos vale
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.INITIAL);
            addRequestUpdateGoal();
        } else if (message.getPerformative() == ACLMessage.FAILURE) { //todo: si falla, significa que hay algún agente en medio y hay que hacer retry
            int randomNumber = new Random().nextInt(100);
            if (randomNumber < 50) { //TODO 50% SI FALLA
                getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.MOVEMENT_COMPUTED);
                addRequestMovementGoal();
            } else {
                getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.UPDATED);
                addComputeRandomPositionGoal();
            }

        } else if (message.getPerformative() == ACLMessage.INFORM) {
            Model model = (Model) getCapability().getBeliefBase().getBelief(ONTOLOGY).getValue();
            String currentPosition = (String) getCapability().getBeliefBase().getBelief(COMPUTED_POSITION).getValue();

            String dataGson = message.getContent();
            MovementData mov =  new Gson().fromJson(dataGson, MovementData.class);

            ((BDIAgent) getCapability().getMyAgent()).situatedData.setBackPackCapacityDiamond(mov.backpackFreeSpaceDiamate);
            ((BDIAgent) getCapability().getMyAgent()).situatedData.setBackPackCapacityGold(mov.backpackFreeSpaceOro);

            String situatedAgentName = ((BDIAgent) getCapability().getMyAgent()).situatedData.getSituatedAgent().getLocalName();
            OntologyManager.addCurrentPosition(situatedAgentName, currentPosition, model);
            ((BDIAgent) getCapability().getMyAgent()).routeHandler.updateAfterMovement();


            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.MOVEMENT_END);
            addRequestShoutOntologyGoal();
        }
    }

    private void handleObservationResponses(ACLMessage message) {
        if (message.getPerformative() == ACLMessage.AGREE) {
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.UPDATE_REQUEST_AGREED);
        } else if (message.getPerformative() == ACLMessage.INFORM) {
            updateOntologyWithObservations(message.getContent());
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.UPDATED);
            addComputeNextPositionGoal();
        }
    }

    void addRequestShoutOntologyGoal() {
        Goal sendShoutOntologyRequestGoal = new ShoutOntologyGoal(AGENT_STATE + "ShoutOntologyReq");
        getCapability().getMyAgent().addGoal(sendShoutOntologyRequestGoal);
        GoalTemplate sendShoutOntologyRequestGoalTemplate = matchesGoal(sendShoutOntologyRequestGoal);
        Plan shoutOntologyPlan = shoutOntologyPlan(sendShoutOntologyRequestGoalTemplate);
        getCapability().getPlanLibrary().addPlan(shoutOntologyPlan);
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

    void addComputeRandomPositionGoal() {
        Goal computeRandomPositionGoal = new ComputeRandomMovementGoal(AGENT_STATE + "ComputeRandom");
        getCapability().getMyAgent().addGoal(computeRandomPositionGoal);
        GoalTemplate computeRandomPositionGoalTemplate = matchesGoal(computeRandomPositionGoal);
        Plan computeRandomMovementPlan = computeRandomMovementPlan(computeRandomPositionGoalTemplate);
        getCapability().getPlanLibrary().addPlan(computeRandomMovementPlan);
    }

    private Plan computeRandomMovementPlan(GoalTemplate computeRandomPositionGoalTemplate) {
        return new DefaultPlan(computeRandomPositionGoalTemplate, ComputeRandomMovementPlanBody.class) {
            @Override
            public boolean isContextApplicable(Goal goal) {
                return getCapability().getBeliefBase().getBelief(AGENT_STATE).getValue().equals(BdiStates.UPDATED);
            }
        };
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

    private Plan shoutOntologyPlan(GoalTemplate sendShoutOntologyRequestGoalTemplate) {
        return new DefaultPlan(sendShoutOntologyRequestGoalTemplate, ShoutOntologyPlanBody.class) {
            @Override
            public boolean isContextApplicable(Goal goal) {
                return getCapability().getBeliefBase().getBelief(AGENT_STATE).getValue().equals(BdiStates.MOVEMENT_END);
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
