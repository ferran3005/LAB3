package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.Ontology;

import eu.su.mas.dedale.env.Observation;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
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

    public void addCurrentPosition(String situatedAgentName, String locationId, Model model) {

        model.add(new StatementImpl(
                model.createResource(ONTOLOGY_NAMESPACE + "#Location-" + locationId),
                model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.getResource(ONTOLOGY_NAMESPACE + "#Node")));

        model.add(new StatementImpl(
                model.getResource(ONTOLOGY_NAMESPACE + "#" + situatedAgentName),
                model.getProperty(ONTOLOGY_NAMESPACE + "#is_in"),
                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + locationId)
        ));
    }

    public void addAdjacentPosition(String originNode, String adjacentNode, Model model) {
        Individual adjacentInd = ((OntModel) model).getIndividual(ONTOLOGY_NAMESPACE + "#Location-" + adjacentNode);
        if (adjacentInd == null) {
            model.add(new StatementImpl(
                    model.createResource(ONTOLOGY_NAMESPACE + "#Location-" + adjacentNode),
                    model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                    model.getResource(ONTOLOGY_NAMESPACE + "#Node")));

            model.add(
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + originNode),
                    model.getProperty(ONTOLOGY_NAMESPACE + "#is_adjacent_to"),
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + adjacentNode)
            );
        }
    }


    //TODO: deberiamos cambiar el nombre de los recursos en la ontología
    //TODO:para hacer match con el enum
    public void addObservation(String locationId, Observation observation, Integer value, Model model) {
        if (isObservationSupported(observation)) {
            Individual observationInd = ((OntModel) model).getIndividual(
                    ONTOLOGY_NAMESPACE + "#" +
                            "Location_" + locationId + "-" +
                            "Content_" + observation);
            if (observationInd == null) {
                model.add(new StatementImpl(
                        model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + locationId + "-" + "Content_" + observation),
                        model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                        model.getResource(ONTOLOGY_NAMESPACE + "#Observation")
                ));
                if (observation != Observation.WIND) {
                    if (observation == Observation.GOLD) {
                        model.add(new StatementImpl(
                                model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + locationId + "-" + "Content_" + observation),
                                model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                                model.getResource(ONTOLOGY_NAMESPACE + "#Gold")
                        ));
                    }
                    else if (observation == Observation.DIAMOND) {
                        model.add(new StatementImpl(
                                model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + locationId + "-" + "Content_" + observation),
                                model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                                model.getResource(ONTOLOGY_NAMESPACE + "#Diamond")
                        ));
                    }
                    model.add(new StatementImpl(
                            model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + locationId + "-" + "Content_" + observation),
                            model.getProperty(ONTOLOGY_NAMESPACE + "#value"),
                            model.createTypedLiteral(value)
                    ));
                }
                else {
                    model.add(new StatementImpl(
                            model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + locationId + "-" + "Content_" + observation),
                            model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                            model.getResource(ONTOLOGY_NAMESPACE + "#Wind")
                    ));
                }
                model.add(new StatementImpl(
                        model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + locationId),
                        model.getProperty(ONTOLOGY_NAMESPACE + "#hasObservation"),
                        model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + locationId + "-" + "Content_" + observation)
                ));
            }
        }
    }

    private Boolean isObservationSupported(Observation obs) {
        return obs == Observation.GOLD || obs == Observation.DIAMOND || obs == Observation.WIND;
    }
}
