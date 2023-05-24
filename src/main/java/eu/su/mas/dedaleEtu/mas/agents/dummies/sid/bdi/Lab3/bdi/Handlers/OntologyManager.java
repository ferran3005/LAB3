package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.Handlers;

import eu.su.mas.dedale.env.Observation;
import org.apache.commons.compress.utils.Lists;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.StatementImpl;

import java.util.List;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY_NAMESPACE;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.QUERY_ADJACENT_CELLS;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.QUERY_SITUATED_AGENT_POSITION;

public class OntologyManager {

    public String getSituatedPosition(Model model) {
        Query query = QueryFactory.create(QUERY_SITUATED_AGENT_POSITION);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        List<QuerySolution> results = Lists.newArrayList(qe.execSelect());
        qe.close();

        if (!results.isEmpty()) {
            QuerySolution solution = results.get(0);
            return String.valueOf(solution.get("Position_id").asLiteral().getInt());
        }
        return null;
    }

    public List<String> getAdjacentCells(Model model, String currentPosition) {
        Query query = QueryFactory.create(QUERY_ADJACENT_CELLS(currentPosition));
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        List<QuerySolution> results2 = Lists.newArrayList(qe.execSelect());
        qe.close();
        return results2.stream()
                .map(querySolution -> String.valueOf(querySolution.get("adjacentLocationId").asLiteral().getInt()))
                .collect(java.util.stream.Collectors.toList());
    }
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
                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + locationId),
                model.getProperty(ONTOLOGY_NAMESPACE + "#position_id"),
                model.createTypedLiteral(Integer.valueOf(locationId))));

        String currentPosition = getSituatedPosition(model);

        if(currentPosition != null) {
            model.remove(new StatementImpl(
                    model.getResource(ONTOLOGY_NAMESPACE + "#" + situatedAgentName),
                    model.getProperty(ONTOLOGY_NAMESPACE + "#is_in"),
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + currentPosition)
            ));
        }

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

            model.add(new StatementImpl(
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + adjacentNode),
                    model.getProperty(ONTOLOGY_NAMESPACE + "#position_id"),
                    model.createTypedLiteral(Integer.valueOf(adjacentNode))));

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
