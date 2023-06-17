package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.EntityType;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.States;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.DATA_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.MOVEMENT_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.OBSERVATIONS_PROTOCOL;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_OUT;

public class Listen extends Behaviour {


    public void listen() {
        ACLMessage msg = this.myAgent.receive();
        States currentState = ((SituatedAgent) this.myAgent).currentState;
        if (msg != null) {
            String Sender = msg.getSender().getName();
            String BDIAgent = ((SituatedAgent) this.myAgent).bdiAgent.getName();
            if (Sender.equals(BDIAgent)) {
                if (currentState.equals(States.NO_UPDATES_SENT) && msg.getProtocol().equals(OBSERVATIONS_PROTOCOL)) {
                    this.myAgent.addBehaviour(new SendObservations(msg.createReply()));
                } else if (currentState.equals(States.OBSERVATIONS_SENT) && msg.getProtocol().equals(MOVEMENT_PROTOCOL)) {
                    this.myAgent.addBehaviour(new CanIMove(msg.createReply(), msg.getContent()));
                } else if (msg.getProtocol().equals(SHOUT_ONTOLOGY_PROTOCOL_OUT)) {
                    this.myAgent.addBehaviour(new ShoutOntology(msg.createReply(), msg.getContent()));
                } else if (msg.getProtocol().equals(DATA_PROTOCOL)) {
                    this.myAgent.addBehaviour(new SendData(msg.createReply()));
                } else {
                    block();
                }
            } else {

                if (((SituatedAgent) this.myAgent).data.getAgentType().equals(EntityType.AGENT_COLLECTOR.getName())) {

                    String sender = msg.getSender().getLocalName();
                    boolean isKnown = ((SituatedAgent) myAgent).allAgents.stream().map(AID::getLocalName).collect(Collectors.toList()).contains(sender);
                    if (!isKnown) {
                        updateFromDF();
                    }

                    int diamondCap = 0;
                    int goldCap = 0;
                    int diamondMaxCap = ((SituatedAgent) myAgent).data.maxCapDiam;
                    int goldMaxCap = ((SituatedAgent) myAgent).data.maxCapGold;
                    for (Couple<Observation, Integer> f : ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace()) {
                        if (f.getLeft().equals(Observation.DIAMOND)) diamondCap = f.getRight();
                        else goldCap = f.getRight();
                    }

                    if (diamondCap < diamondMaxCap || goldCap < goldMaxCap) {
                        boolean isTanker = ((SituatedAgent) myAgent).tankers.stream().map(AID::getLocalName).collect(Collectors.toList()).contains(sender);
                        if (isTanker) {
                            tryToDeposit(msg.getSender().getLocalName());
                        }
                    }

                }
                if (((SituatedAgent) myAgent).checkNearbyAgents) {
                    if (Objects.equals(msg.getProtocol(), SHOUT_ONTOLOGY_PROTOCOL_OUT)) {
                        ((SituatedAgent) myAgent).checkNearbyAgents = false;
                        this.myAgent.addBehaviour(new SendInNewOntology(msg));
                        myAgent.addBehaviour(new WaitBeforeListeningForOntologies(Instant.now().toEpochMilli()));
                    }
                }
            }
        } else {
            block();
        }

    }

    private void updateFromDF() {
        DFAgentDescription templateExplo = new DFAgentDescription();
        DFAgentDescription templateCollect = new DFAgentDescription();
        DFAgentDescription templateTank = new DFAgentDescription();

        // Create ServiceDescription objects for each service type
        ServiceDescription serviceExplo = new ServiceDescription();
        serviceExplo.setType(EntityType.AGENT_EXPLORER.getName());
        ServiceDescription serviceCollect = new ServiceDescription();
        serviceCollect.setType(EntityType.AGENT_COLLECTOR.getName());
        ServiceDescription serviceTanker = new ServiceDescription();
        serviceTanker.setType(EntityType.AGENT_TANKER.getName());

        templateExplo.addServices(serviceExplo);
        templateCollect.addServices(serviceCollect);
        templateTank.addServices(serviceTanker);

        try { //TODO: LO ODIO LO ODIO LO ODIO
            List<AID> resultsExplo = Arrays.stream(DFService.search(this.myAgent, templateExplo)).map(DFAgentDescription::getName).collect(Collectors.toList());
            List<AID> resultsCollect = Arrays.stream(DFService.search(this.myAgent, templateCollect)).map(DFAgentDescription::getName).collect(Collectors.toList());
            List<AID> resultsTank = Arrays.stream(DFService.search(this.myAgent, templateTank)).map(DFAgentDescription::getName).collect(Collectors.toList());

            ((SituatedAgent) myAgent).tankers = resultsTank;
            ((SituatedAgent) myAgent).allAgents = Stream.concat(Stream.concat(resultsExplo.stream(), resultsCollect.stream()), resultsTank.stream()).collect(Collectors.toList());
        } catch (FIPAException e) {
            e.printStackTrace();
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

    public void tryToDeposit(String sender) {
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
