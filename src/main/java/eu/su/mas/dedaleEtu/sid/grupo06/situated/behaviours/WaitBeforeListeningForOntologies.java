package eu.su.mas.dedaleEtu.sid.grupo06.situated.behaviours;

import eu.su.mas.dedaleEtu.sid.grupo06.SituatedAgent06;
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
        ((SituatedAgent06)myAgent).checkNearbyAgents = true;
        return 0;
    }
}
