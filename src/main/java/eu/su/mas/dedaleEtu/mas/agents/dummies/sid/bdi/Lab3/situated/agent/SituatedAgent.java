package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours.FindBDI;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours.Listen;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours.RegisterDF;
import jade.core.AID;
import jade.core.behaviours.Behaviour;

import java.util.ArrayList;
import java.util.List;

public class SituatedAgent extends AbstractDedaleAgent {
    public AID bdiAgent;
    public String agentType;
    public States currentState = States.NO_UPDATES_SENT;
    @Override
    protected void setup() {
        super.setup();
        List<Behaviour> lb = new ArrayList<>();
        final Object[] args = getArguments();
        agentType = (String) args[2];

        lb.add(new RegisterDF());
        lb.add(new FindBDI());
        lb.add(new Listen());
        addBehaviour(new startMyBehaviours(this, lb));
    }
}
