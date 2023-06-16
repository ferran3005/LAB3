package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.States;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.MOVEMENT_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_IN;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_OUT;

public class Listen extends Behaviour {


    public void listen() {
        ACLMessage msg = this.myAgent.receive();
        States currentState = ((SituatedAgent) this.myAgent).currentState;
        if (msg != null) {
            String Sender = msg.getSender().getName();
            String BDIAgent = ((SituatedAgent)this.myAgent).bdiAgent.getName();
            if(Sender.equals(BDIAgent)) {
                if (currentState.equals(States.NO_UPDATES_SENT) && msg.getProtocol().equals(OBSERVATIONS_PROTOCOL)) {
                    this.myAgent.addBehaviour(new SendObservations(msg.createReply()));
                } else if (currentState.equals(States.OBSERVATIONS_SENT) && msg.getProtocol().equals(MOVEMENT_PROTOCOL)) {
                    this.myAgent.addBehaviour(new CanIMove(msg.createReply(), msg.getContent()));
                } else if (msg.getProtocol().equals(SHOUT_ONTOLOGY_PROTOCOL_OUT)) {
                    this.myAgent.addBehaviour(new ShoutOntology(msg.createReply(), msg.getContent()));
                }
                else {
                    block();
                }
            }
            else {
                if(msg.getProtocol().equals(SHOUT_ONTOLOGY_PROTOCOL_OUT)) {
                    if(((SituatedAgent) this.myAgent).agentType == "collector")
                        tryToLeave(msg.getSender().getLocalName());
                    this.myAgent.addBehaviour(new SendInNewOntology(msg));
                }
                //TODO: mirar si el protocolo es ONTOLOGY_06
            }
        } else {
            block();
        }

    }

    @Override
    public void action() {
        listen();
    }

    @Override
    public boolean done() {
        return false;
    }

    public void tryToLeave(String sender){
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> task = new Callable<Object>() {
            public Object call() {
                return ((SituatedAgent) myAgent).emptyMyBackPack(sender);
            }
        };
        Future<Object> future = executor.submit(task);
        try {
            Object result = future.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            // handle the timeout
        }
    }
}
