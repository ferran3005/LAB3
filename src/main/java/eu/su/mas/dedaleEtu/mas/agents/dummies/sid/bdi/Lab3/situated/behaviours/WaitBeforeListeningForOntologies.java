package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import jade.core.behaviours.Behaviour;

import java.time.Instant;

public class WaitBeforeListeningForOntologies extends Behaviour {

    Long timeOfCreation;

    Boolean done;

    public WaitBeforeListeningForOntologies(Long timeOfCreation) {
        super();
        this.timeOfCreation = timeOfCreation;
    }
    @Override
    public void action() {
        done = Instant.now().isAfter(Instant.ofEpochMilli(timeOfCreation + 2000));
    }

    @Override
    public boolean done() {
        return done;
    }

    @Override
    public int onEnd() {
        ((SituatedAgent)myAgent).checkNearbyAgents = true;
        return 0;
    }
}
