package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans;

import bdi4jade.annotation.Parameter;
import bdi4jade.annotation.Parameter.Direction;
import bdi4jade.belief.TransientBelief;
import bdi4jade.belief.BeliefBase;
import bdi4jade.belief.TransientPredicate;
import bdi4jade.core.SingleCapabilityAgent;
import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.AbstractPlanBody;
import com.google.gson.Gson;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BDIAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.ActionResult;
import jade.lang.acl.ACLMessage;
import org.apache.jena.rdf.model.Model;

import java.util.List;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.IS_INFO_UPDATED;
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
    public void setMessage(ACLMessage msgReceived) {
        BeliefBase beliefBase = getCapability().getBeliefBase();
        if (!(boolean) beliefBase.getBelief(IS_INFO_UPDATED).getValue()
                && msgReceived.getProtocol().equals(OBSERVATIONS_PROTOCOL)) {
            updateOntologyWithObservations(msgReceived.getContent());
            //TODO goalMove();
            beliefBase.updateBelief(IS_INFO_UPDATED, true);
        }
        if ((boolean) beliefBase.getBelief(IS_INFO_UPDATED).getValue()
                && msgReceived.getProtocol().equals(MOVEMENT_PROTOCOL)) {
            if(msgReceived.getContent().equals(ActionResult.SUCCESS.toString())) {
                beliefBase.updateBelief(IS_INFO_UPDATED, false);//TODO: faltan cosas
            }
            beliefBase.addOrUpdateBelief(new TransientBelief("MOVEMENT", msgReceived.getContent()));
        }
        //TODO estados de movimiento
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
            //todo: añadir adyacencia y observaciones
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


}
