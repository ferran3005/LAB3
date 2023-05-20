package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.Ontology;

import bdi4jade.belief.Belief;
import bdi4jade.core.SingleCapabilityAgent;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.StatementImpl;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY_NAMESPACE;

public class OntologyManager {
    public void addExplorer(String situatedAgentName, Model model) {
        model.add(new StatementImpl(
                model.createResource(ONTOLOGY_NAMESPACE + "#" + situatedAgentName),
                model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.getResource(ONTOLOGY_NAMESPACE + "#Explorer")));
    }
}
