package eu.su.mas.dedaleEtu.sid.grupo06.bdi.plans;

import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import eu.su.mas.dedaleEtu.sid.grupo06.BDIAgent06;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent.BdiStates;
import jade.lang.acl.ACLMessage;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

import java.io.StringWriter;

import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.AGENT_STATE;
import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.ONTOLOGY;
import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_OUT;
import static org.apache.jena.riot.RDFFormat.JSONLD_COMPACT_PRETTY;

public class ShoutOntologyPlanBody extends BeliefGoalPlanBody {
    @Override
    protected void execute() {
        Model model = (Model) getCapability().getBeliefBase().getBelief(ONTOLOGY).getValue();

        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, model, JSONLD_COMPACT_PRETTY);
        String ontologyJson = writer.toString();

        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(((BDIAgent06) this.myAgent).situatedData.getSituatedAgent());
        request.setProtocol(SHOUT_ONTOLOGY_PROTOCOL_OUT);
        request.setSender(this.myAgent.getAID());
        request.setContent(ontologyJson);
        this.myAgent.send(request);

        Belief agentState = new TransientBelief(AGENT_STATE, BdiStates.SHOUT_ONTOLOGY_REQUEST_SENT);
        getCapability().getBeliefBase().addOrUpdateBelief(agentState);
        setEndState(Plan.EndState.SUCCESSFUL);
    }
}
