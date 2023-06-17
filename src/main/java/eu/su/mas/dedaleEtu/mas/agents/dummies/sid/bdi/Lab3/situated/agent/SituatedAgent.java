package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.EntityCharacteristics;
import eu.su.mas.dedale.env.mapElements.LockElement;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.SituatedData;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours.FindBDI;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours.Listen;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours.RegisterDF;
import jade.core.AID;
import jade.core.behaviours.Behaviour;

import java.util.ArrayList;
import java.util.List;

public class SituatedAgent extends AbstractDedaleAgent {

    public List<String> allAgents = new ArrayList<>();
    public List<String> tankers = new ArrayList<>();
    public AID bdiAgent;
    public Boolean checkNearbyAgents = true;
    public String agentType;
    public States currentState = States.NO_UPDATES_SENT;
    public SituatedData data;
    @Override
    protected void setup() {
        super.setup();

        List<Behaviour> lb = new ArrayList<>();
        final Object[] args = getArguments();
        EntityCharacteristics entity = (EntityCharacteristics) args[0];
        String name = (String) args[2];
        String bdiName = (String) args[3];

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

        lb.add(new RegisterDF(this, name));
        lb.add(new FindBDI(bdiName));
        lb.add(new Listen());
        addBehaviour(new startMyBehaviours(this, lb));
    }
}
