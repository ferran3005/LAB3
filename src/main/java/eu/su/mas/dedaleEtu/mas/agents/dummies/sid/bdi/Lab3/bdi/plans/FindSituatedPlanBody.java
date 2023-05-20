package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans;

import bdi4jade.belief.Belief;
import bdi4jade.core.SingleCapabilityAgent;
import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BDIAgent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.StatementImpl;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY_NAMESPACE;

public class FindSituatedPlanBody extends BeliefGoalPlanBody {
    @Override
    protected void execute() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();
        templateSd.setType("dedale");
        template.addServices(templateSd);
        DFAgentDescription[] results;
        try {
            results = DFService.search(this.myAgent, template);
            if (results.length > 0) {
                DFAgentDescription dfd = results[0];
                AID provider = dfd.getName();
                System.out.println("Found situated! " + provider.getName());
                ((BDIAgent) this.myAgent).situatedAgent = provider;
                updateOntology(provider.getLocalName());
            }
            // if results.length == 0, no endState is set,
            // so the plan body will run again (if the goal still holds)
        } catch (FIPAException e) {
            setEndState(Plan.EndState.FAILED);
            e.printStackTrace();
        }
    }

    private void updateOntology(String situatedAgentName) {
        SingleCapabilityAgent agent = (SingleCapabilityAgent) this.myAgent;
        Belief b = agent.getCapability().getBeliefBase().getBelief(ONTOLOGY);
        Model model = (Model) b.getValue();
        ((BDIAgent) this.myAgent).ontologyManager.addExplorer(situatedAgentName, model);
    }
}
