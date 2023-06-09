package eu.su.mas.dedaleEtu.sid.grupo06.situated.behaviours;

import eu.su.mas.dedaleEtu.sid.grupo06.BDIAgent06;
import eu.su.mas.dedaleEtu.sid.grupo06.SituatedAgent06;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class FindBDI extends Behaviour {

    String bdiName;

    public FindBDI(String bdiName) {
        super();
        if(bdiName == null){
            this.bdiName = "BDISituatedAgent06";
        }
        else this.bdiName = bdiName;
    }
    @Override
    public void action() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();
        templateSd.setName(bdiName);
        templateSd.setType("bdi");
        template.addServices(templateSd);
        DFAgentDescription[] results;

        try {
            results = DFService.search(this.myAgent, template);
            if (results.length > 0) {
                DFAgentDescription dfd = results[0];
                AID provider = dfd.getName();
                System.out.println("Found bdi! " + provider.getName());
                ((SituatedAgent06) this.myAgent).bdiAgent = provider;
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean done() {
        return ((SituatedAgent06) this.myAgent).bdiAgent != null;
    }
}
