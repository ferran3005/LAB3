package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class RegisterDF extends OneShotBehaviour {
    @Override
    public void action() {
        Agent agent = this.myAgent;
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName((String) myAgent.getArguments()[2]);
        sd.setType(((SituatedAgent) myAgent).data.agentType.getName());
        dfd.addServices(sd);
        try {
            DFService.register(this.myAgent, dfd);
            System.out.println("Situated agent registered!");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
