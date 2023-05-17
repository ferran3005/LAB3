package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situatedBehaviours.FindBDI;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situatedBehaviours.RegisterDF;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.ArrayList;
import java.util.List;

public class SituatedAgent extends AbstractDedaleAgent {
    public AID bdiAgent;
    @Override
    protected void setup() {
        super.setup();
        List<Behaviour> lb = new ArrayList<>();
        lb.add(new RegisterDF());
        lb.add(new FindBDI());
        addBehaviour(new startMyBehaviours(this, lb));
    }
}
