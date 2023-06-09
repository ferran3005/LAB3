package eu.su.mas.dedaleEtu.sid.grupo06.bdi.plans;

import bdi4jade.belief.Belief;
import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import eu.su.mas.dedale.env.EntityType;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.Handlers.CollectorRouteHandler;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.Handlers.ExplorerRouteHandler;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.Handlers.OntologyManager;
import eu.su.mas.dedaleEtu.sid.grupo06.BDIAgent06;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.apache.jena.rdf.model.Model;

import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.ONTOLOGY;

public class FindSituatedPlanBody extends BeliefGoalPlanBody {
    @Override
    protected void execute() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();

        final Object[] args = myAgent.getArguments();
        String situatedRegName = "SituatedAgent06";
        if(args.length > 0)
            situatedRegName = (String) args[0];

        templateSd.setName(situatedRegName);
        template.addServices(templateSd);
        DFAgentDescription[] results;
        try {
            results = DFService.search(this.myAgent, template);
            if (results.length > 0) {
                DFAgentDescription dfd = results[0];
                AID provider = dfd.getName();
                ((BDIAgent06)myAgent).situatedAgent = provider;

                ServiceDescription sd = (ServiceDescription) dfd.getAllServices().next();
                System.out.println("Found situated! " + provider.getName());
                String type = sd.getType();
                if(type.equals(EntityType.AGENT_EXPLORER.getName()) || type.equals(EntityType.AGENT_TANKER.getName())) {
                    ((BDIAgent06) this.myAgent).routeHandler = new ExplorerRouteHandler();
                }
                else if(type.equals(EntityType.AGENT_COLLECTOR.getName())) {
                    ((BDIAgent06) this.myAgent).routeHandler = new CollectorRouteHandler();
                }
                updateOntology(provider.getLocalName(), type);
            }
            // if results.length == 0, no endState is set,
            // so the plan body will run again (if the goal still holds)
        } catch (FIPAException e) {
            setEndState(Plan.EndState.FAILED);
            e.printStackTrace();
        }
    }

    private void updateOntology(String situatedAgentName, String type) {
        BDIAgent06 agent = (BDIAgent06) this.myAgent;
        Belief b = agent.getCapability().getBeliefBase().getBelief(ONTOLOGY);
        Model model = (Model) b.getValue();
        OntologyManager.addAgent(situatedAgentName, type, model);
    }
}
