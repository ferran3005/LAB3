package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans;

import bdi4jade.annotation.Parameter;
import bdi4jade.annotation.Parameter.Direction;
import bdi4jade.belief.TransientBelief;
import bdi4jade.belief.BeliefBase;
import bdi4jade.belief.TransientPredicate;
import bdi4jade.core.SingleCapabilityAgent;
import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.AbstractPlanBody;
import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BDIAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.ActionResult;
import jade.lang.acl.ACLMessage;
import org.omg.PortableInterceptor.SUCCESSFUL;

import java.util.List;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.IS_INFO_UPDATED;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.MOVEMENT_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;

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
            msgReceived.getContent();
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
        ((BDIAgent) this.myAgent).log.add(new Couple<>(msgReceived, Direction.IN));
    }

    @Override
    public int onEnd() {
        return 0;
    }
    private void addComputePositionGoal() {
        //TODO añadir goal de calcular posición y como planes posibles los de movimiento
    }
}
