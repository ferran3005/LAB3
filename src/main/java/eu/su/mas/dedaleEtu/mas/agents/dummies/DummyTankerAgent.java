package eu.su.mas.dedaleEtu.mas.agents.dummies;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.EntityCharacteristics;
import eu.su.mas.dedale.env.EntityType;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.mapElements.LockElement;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent.SituatedData;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Dummy Tanker agent. It does nothing more than printing what it observes every 10s and receiving the treasures from other agents.
 * <br/>
 * Note that this last behaviour is hidden, every tanker agent automatically possess it.
 *
 * @author hc
 */
public class DummyTankerAgent extends AbstractDedaleAgent {
    /**
     *
     */
    private static final long serialVersionUID = -1784844593772918359L;

    SituatedData data;
    /**
     * This method is automatically called when "agent".start() is executed.
     * Consider that Agent is launched for the first time.
     * 1) set the agent attributes
     * 2) add the behaviours
     */
    protected void setup() {
        super.setup();

        List<Behaviour> lb = new ArrayList<>();
        final Object[] args = getArguments();
        EntityCharacteristics entity = (EntityCharacteristics) args[0];
        String name = (String) args[2];

        entity.getExpertise();
        int lock = 0;
        int strength= 0;
        for (Couple<LockElement.LockType, Integer> a : entity.getExpertise()) {

            if (a.getLeft().getName().equals("LockPicking"))
                lock = a.getRight();
            else
                strength = a.getRight();

        }

        data = new SituatedData(

                lock,
                strength,
                entity.getDiamondCapacity(),
                entity.getGoldCapacity(),
                entity.getGoldCapacity(),
                entity.getDiamondCapacity(),
                entity.getMyTreasureType(),
                entity.getMyEntityType().getName(),
                null
        );

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(name);
        sd.setType((data.agentType));
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            System.out.println("Situated agent registered!");
        } catch (FIPAException e) {
            e.printStackTrace();
        }


        lb.add(new RandomTankerBehaviour(this));

        addBehaviour(new startMyBehaviours(this, lb));

        System.out.println("the  agent " + this.getLocalName() + " is started");
    }

    /**
     * This method is automatically called after doDelete()
     */
    protected void takeDown() {
    }
}

// BEHAVIOUR
class RandomTankerBehaviour extends TickerBehaviour {
    /**
     * When an agent choose to migrate all its components should be serializable
     */
    private static final long serialVersionUID = 9088209402507795289L;

    public RandomTankerBehaviour(final AbstractDedaleAgent myAgent) {
        super(myAgent, 1000);
    }

    @Override
    public void onTick() {
        //Example to retrieve the current position
        Location myPosition = ((AbstractDedaleAgent) this.myAgent).getCurrentPosition();

        if (!Objects.equals(myPosition.getLocationId(), "")) {
            //List of observable from the agent's current position
            List<Couple<Location, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent) this.myAgent).observe();//myPosition
            String message = this.myAgent.getLocalName() + " -- list of observables: " + lobs;
            System.out.println(message);

            ACLMessage shoutMessage = new ACLMessage(ACLMessage.INFORM);
            shoutMessage.setSender(myAgent.getAID());
            shoutMessage.setContent(message);

            getAllAgentNames().forEach( aid -> {if(!aid.getName().equals(this.myAgent.getAID().getName())) shoutMessage.addReceiver(aid);});
            ((AbstractDedaleAgent) this.myAgent).sendMessage(shoutMessage);

            Random r = new Random();
            int moveId = 1 + r.nextInt(lobs.size() - 1);
            //removing the current position from the list of target, not necessary as to stay is an action but allow quicker random move

            //The move action (if any) should be the last action of your behaviour
            ((AbstractDedaleAgent) this.myAgent).moveTo(lobs.get(moveId).getLeft());
        }
    }

    private List<AID> getAllAgentNames() {
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

            return Stream.concat(Stream.concat(resultsExplo.stream(), resultsCollect.stream()) , resultsTank.stream()).collect(Collectors.toList());
        } catch (FIPAException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
