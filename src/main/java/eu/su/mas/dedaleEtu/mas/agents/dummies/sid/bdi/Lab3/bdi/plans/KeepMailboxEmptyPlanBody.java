package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans;

import bdi4jade.annotation.Parameter;
import bdi4jade.annotation.Parameter.Direction;
import bdi4jade.belief.TransientBelief;
import bdi4jade.belief.BeliefBase;
import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.AbstractPlanBody;
import com.google.gson.Gson;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BDIAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BdiStates;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.ActionResult;
import jade.lang.acl.ACLMessage;
import org.apache.jena.rdf.model.Model;

import java.util.List;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.AGENT_STATE;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.MOVEMENT_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.observationsType;

public class KeepMailboxEmptyPlanBody extends AbstractPlanBody {

    @Override
    public void action() {
        setEndState(Plan.EndState.SUCCESSFUL);
    }

    @Parameter(direction = Parameter.Direction.IN)
    public void listen(ACLMessage msgReceived) {
        BeliefBase beliefBase = getCapability().getBeliefBase();
        if (beliefBase.getBelief(AGENT_STATE).getValue().equals(BdiStates.UPDATE_REQUEST_SENT)
                && msgReceived.getProtocol().equals(OBSERVATIONS_PROTOCOL)) {

            updateOntologyWithObservations(msgReceived.getContent());
            ((BDIAgent)this.myAgent).dfsHandler.updateStack(msgReceived.getContent());
            beliefBase.updateBelief(AGENT_STATE, BdiStates.UPDATED);
            //TODO: goal + plan de compute position
        }
        if (beliefBase.getBelief(AGENT_STATE).getValue().equals(BdiStates.MOVEMENT_REQUEST_SENT)
                && msgReceived.getProtocol().equals(MOVEMENT_PROTOCOL)) {
            handleMovementResponses(msgReceived);
        }
        ((BDIAgent)getCapability().getMyAgent()).log.add(new Couple<>(msgReceived, Direction.IN));
    }

    @Override
    public int onEnd() {
        return 0;
    }
    private void addComputePositionGoal() {

        //TODO añadir goal de calcular posición y como planes posibles los de movimiento
    }
    private void updateOntologyWithObservations(String json) {
        String situatedAgentName = ((BDIAgent)getCapability().getMyAgent()).situatedAgent.getLocalName();
        Model model = (Model) getCapability().getBeliefBase().getBelief(ONTOLOGY).getValue();

        List<Couple<Location, List<Couple<Observation, Integer>>>> observations =
                new Gson().fromJson(json, observationsType());
        String originLocationId = observations.get(0).getLeft().getLocationId();
        ((BDIAgent)getCapability().getMyAgent()).ontologyManager.addCurrentPosition(
                situatedAgentName,
                originLocationId,
                model);

        for(Couple<Location, List<Couple<Observation, Integer>>> obs: observations) {
            ((BDIAgent)getCapability().getMyAgent()).ontologyManager.addAdjacentPosition(
                    originLocationId,
                    obs.getLeft().getLocationId(),
                    model);
            for(Couple<Observation, Integer> observation: obs.getRight()) {
                ((BDIAgent)getCapability().getMyAgent()).ontologyManager.addObservation(
                        obs.getLeft().getLocationId(),
                        observation.getLeft(),
                        observation.getRight(),
                        model);
            }
        }
    }

    private void handleMovementResponses(ACLMessage message) {
        if(message.getPerformative() == ACLMessage.AGREE) {
            //Todo: reset timeout
        }
        else if(message.getPerformative() == ACLMessage.REFUSE) {
            ((BDIAgent)this.myAgent).dfsHandler.discardTop();
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.UPDATED);
            //todo: calcular siguiente posicion
        }
        else if(message.getPerformative() == ACLMessage.FAILURE) {
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.UPDATED);
            //todo: calcular siguiente posicion
        }
        else if(message.getPerformative() == ACLMessage.INFORM) {
            getCapability().getBeliefBase().updateBelief(AGENT_STATE, BdiStates.INITIAL);
            //TODO: nos llega couple <String, String> con oldLocation y newLocation
            //Todo: actualizar path + stack y pedir updates
        }
    }


}
