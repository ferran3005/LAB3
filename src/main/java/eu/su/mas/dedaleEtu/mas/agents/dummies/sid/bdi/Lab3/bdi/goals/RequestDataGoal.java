package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.goals;

import bdi4jade.belief.BeliefBase;
import bdi4jade.goal.AbstractBeliefGoal;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BdiStates;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.AGENT_STATE;

public class RequestDataGoal<K> extends AbstractBeliefGoal<K> {
    public RequestDataGoal(K beliefName) {
        super(beliefName);
    }

    @Override
    public boolean isAchieved(BeliefBase beliefBase) {
        return beliefBase.getBelief(AGENT_STATE).getValue().equals(BdiStates.DATA_REQUEST_SENT);
    }
}
