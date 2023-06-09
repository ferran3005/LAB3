package eu.su.mas.dedaleEtu.sid.grupo06.situated.behaviours;

import eu.su.mas.dedaleEtu.sid.grupo06.SituatedAgent06;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class RegisterDF extends OneShotBehaviour {

    String name;

    public RegisterDF(Agent a, String name) {
        super(a);
        if(name == null){
            this.name = "SituatedAgent06";
        }
        else this.name = name;
    }
    @Override
    public void action() {
        Agent agent = this.myAgent;
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(name);
        sd.setType(((SituatedAgent06) myAgent).data.agentType);
        dfd.addServices(sd);
        try {
            DFService.register(this.myAgent, dfd);
            System.out.println("Situated agent registered!");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
