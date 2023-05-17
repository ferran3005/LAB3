package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situatedBehaviours;

import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.SituatedAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class FindBDI extends Behaviour {
    @Override
    public void action() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();
        templateSd.setType("bdi");
        template.addServices(templateSd);
        DFAgentDescription[] results;

        try {
            results = DFService.search(this.myAgent, template);
            if (results.length > 0) {
                DFAgentDescription dfd = results[0];
                AID provider = dfd.getName();
                System.out.println("Found bdi! " + provider.getName());
                ((SituatedAgent) this.myAgent).bdiAgent = provider;
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean done() {
        return ((SituatedAgent) this.myAgent).bdiAgent != null;
    }
}
