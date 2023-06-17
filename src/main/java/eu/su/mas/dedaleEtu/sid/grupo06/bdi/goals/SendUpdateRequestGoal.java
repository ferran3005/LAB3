package eu.su.mas.dedaleEtu.sid.grupo06.bdi.goals;

import bdi4jade.belief.BeliefBase;
import bdi4jade.goal.AbstractBeliefGoal;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent.BdiStates;

import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.AGENT_STATE;

public class SendUpdateRequestGoal<K> extends AbstractBeliefGoal<K> {

    public SendUpdateRequestGoal(K beliefName) {
        super(beliefName);
    }
    @Override
    public boolean isAchieved(BeliefBase beliefBase) {
        return beliefBase.getBelief(AGENT_STATE).getValue().equals(BdiStates.UPDATE_REQUEST_SENT);
    }
}
