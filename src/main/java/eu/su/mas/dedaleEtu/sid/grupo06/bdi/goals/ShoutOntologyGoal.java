package eu.su.mas.dedaleEtu.sid.grupo06.bdi.goals;

import bdi4jade.belief.BeliefBase;
import bdi4jade.goal.AbstractBeliefGoal;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent.BdiStates;

import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.AGENT_STATE;

public class ShoutOntologyGoal<K> extends AbstractBeliefGoal<K> {

    public ShoutOntologyGoal(K beliefName) {
        super(beliefName);
    }
    @Override
    public boolean isAchieved(BeliefBase beliefBase) {
        return beliefBase.getBelief(AGENT_STATE).getValue().equals(BdiStates.SHOUT_ONTOLOGY_REQUEST_SENT);
    }
}
