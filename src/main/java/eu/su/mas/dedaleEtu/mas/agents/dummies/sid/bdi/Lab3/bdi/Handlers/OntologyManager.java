package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.Handlers;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import org.apache.commons.compress.utils.Lists;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.impl.OntModelImpl;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.StatementImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Function;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY_NAMESPACE;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.QUERY_ADJACENT_CELLS;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.QUERY_SITUATED_AGENT_POSITION;


public class OntologyManager {

    public static String getSituatedPosition(Model model, String situatedName) {

        Query query = QueryFactory.create(QUERY_SITUATED_AGENT_POSITION(situatedName));
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        List<QuerySolution> results = Lists.newArrayList(qe.execSelect());
        qe.close();

        if (!results.isEmpty()) {
            QuerySolution solution = results.get(0);
            return String.valueOf(solution.get("Position_id").asLiteral().getInt());
        }
        return null;
    }

    public static Boolean goldChecker(String currentPosition, Model model) {
        //check if Location with location_id currentPosition has gold in model
        return model.contains(
                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + currentPosition),
                model.getProperty(ONTOLOGY_NAMESPACE + "#hasObservation"),
                model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + currentPosition + "-Content_Gold")
        );
    }

    public static Stack<Location> shortestPathToTarget(Model model, String situatedName, Function<String, Boolean> checkFunction) {
        Queue<String> queue = new LinkedList<>();
        List<String> visited = new ArrayList<>();
        Map<String, String> pathMap = new HashMap<>();

        String currentPosition = getSituatedPosition(model, situatedName);

        queue.add(currentPosition);
        visited.add(currentPosition);

        while (!queue.isEmpty()) {
            String position = queue.poll();
            if (checkFunction.apply(position)) { //TODO: modificar para funcion de checkeo
                return buildPath(position, pathMap);
            }
            List<String> adjacentCells = getAdjacentCells(model, position);
            for (String adjacentCell : adjacentCells) {
                if (!visited.contains(adjacentCell)) {
                    queue.add(adjacentCell);
                    visited.add(adjacentCell);
                    pathMap.put(adjacentCell, position);
                }
            }
        }
        return new Stack<>();
    }

    private static Stack<Location> buildPath(String target, Map<String, String> pathMap) {
        Stack<Location> path = new Stack<>();
        String currentLocation = target;
        while (pathMap.containsKey(currentLocation)) {
            path.push(new gsLocation(currentLocation));
            currentLocation = pathMap.get(currentLocation);
        }
        return path;
    }

    public static List<String> getAdjacentCells(Model model, String currentPosition) {
        Query query = QueryFactory.create(QUERY_ADJACENT_CELLS(currentPosition));
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        List<QuerySolution> results2 = Lists.newArrayList(qe.execSelect());
        qe.close();
        return results2.stream()
                .map(querySolution -> String.valueOf(querySolution.get("adjacentLocationId").asLiteral().getInt()))
                .collect(java.util.stream.Collectors.toList());
    }

    public static void addAgent(String situatedAgentName, String type, Model model) {
        model.add(new StatementImpl(
                model.createResource(ONTOLOGY_NAMESPACE + "#" + situatedAgentName),
                model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.getResource(ONTOLOGY_NAMESPACE + "#" + type)));

        updateAgentLastSeen(situatedAgentName, model);
    }


    public static void addVisited(Model model, String locationID) {
        Statement visited = model.getProperty(
                model.createResource(ONTOLOGY_NAMESPACE + "#Location-" + locationID), //dominio
                model.getProperty(ONTOLOGY_NAMESPACE + "#visited")  //nombre propiedad
        );
        if(visited != null) visited.changeLiteralObject(true);
        else {
            model.add(new StatementImpl(
                    model.createResource(ONTOLOGY_NAMESPACE + "#Location-" + locationID), //dominio
                    model.getProperty(ONTOLOGY_NAMESPACE + "#visited"),   //nombre propiedad
                    model.createTypedLiteral(true))
            );
        }
    }

    public static void addCurrentPosition(String situatedAgentName, String locationId, Model model) {

        model.add(new StatementImpl(
                model.createResource(ONTOLOGY_NAMESPACE + "#Location-" + locationId),
                model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.getResource(ONTOLOGY_NAMESPACE + "#Node")));

        model.add(new StatementImpl(
                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + locationId),
                model.getProperty(ONTOLOGY_NAMESPACE + "#position_id"),
                model.createTypedLiteral(Integer.valueOf(locationId))));

        String currentPosition = getSituatedPosition(model, situatedAgentName);

        if (currentPosition != null) {
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


        updateAgentLastSeen(situatedAgentName, model);
        updateLocationLastSeen(locationId, model);
        addVisited(model, locationId);
    }

    private static void updateAgentLastSeen(String situatedAgentName, Model model) {
        Statement lastUpdated = model.getProperty(
                model.createResource(ONTOLOGY_NAMESPACE + "#" + situatedAgentName),
                model.getProperty(ONTOLOGY_NAMESPACE + "#LastUpdated")
        );

        if (lastUpdated != null) {
            lastUpdated.changeLiteralObject(Instant.now().toEpochMilli());
        } else {
            model.add(new StatementImpl(
                    model.createResource(ONTOLOGY_NAMESPACE + "#" + situatedAgentName),
                    model.getProperty(ONTOLOGY_NAMESPACE + "#LastUpdated"),
                    model.createTypedLiteral(Instant.now().toEpochMilli())));
        }
    }

    public static void addAdjacentPosition(String originNode, String adjacentNode, Model model) {
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

            model.add(new StatementImpl(
                            model.createResource(ONTOLOGY_NAMESPACE + "#Location-" + adjacentNode), //dominio
                            model.getProperty(ONTOLOGY_NAMESPACE + "#visited"),   //nombre propiedad
                            model.createTypedLiteral(false))
                    );

            model.add(
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + originNode),
                    model.getProperty(ONTOLOGY_NAMESPACE + "#is_adjacent_to"),
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + adjacentNode)
            );

            updateLocationLastSeen(adjacentNode, model);
        }
    }

    private static void updateLocationLastSeen(String adjacentNode, Model model) {
        Statement lastUpdated = model.getProperty(
                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + adjacentNode),
                model.getProperty(ONTOLOGY_NAMESPACE + "#LastUpdated")
        );

        if (lastUpdated != null) {
            lastUpdated.changeLiteralObject(Instant.now().toEpochMilli());
        } else {
            model.add(new StatementImpl(
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + adjacentNode),
                    model.getProperty(ONTOLOGY_NAMESPACE + "#LastUpdated"),
                    model.createTypedLiteral(Instant.now().toEpochMilli())));
        }
    }


    public static void cleanObservations(String locationId, Model model) {
        List<Statement> matches = model.listStatements(
                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + locationId),
                model.getProperty(ONTOLOGY_NAMESPACE + "#hasObservation"),
                (RDFNode) null
        ).toList();
        if (!matches.isEmpty()) {
            matches.stream().map(Statement::getObject)
                    .forEach(s -> ((OntModelImpl) model).getIndividual(s.asResource().getURI()).remove()
                    );
            matches.forEach(model::remove);
        }
    }

    public static void addObservation(String locationId, Observation observation, Integer value, Model model) {
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
            String aux;
            aux = "#" + observation.getName();

            model.add(new StatementImpl(
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + locationId + "-" + "Content_" + observation),
                    model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                    model.getResource(ONTOLOGY_NAMESPACE + aux)
            ));
            model.add(new StatementImpl(
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + locationId + "-" + "Content_" + observation),
                    model.getProperty(ONTOLOGY_NAMESPACE + "#value"),
                    model.createTypedLiteral(observation.getName().equals("None") ? 0 : value)
            ));
            model.add(new StatementImpl(
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + locationId),
                    model.getProperty(ONTOLOGY_NAMESPACE + "#hasObservation"),
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + locationId + "-" + "Content_" + observation)
            ));
            updateLocationLastSeen(locationId, model);
        }

    }


    public static void mergeOntology(Model oldModel, Model newModel) {
        ((OntModelImpl) newModel).listIndividuals().forEach(
                ind -> {
                    //add individuals with different URI from new model to old model
                    if (ind.getURI() != null) {
                        if (((OntModelImpl) oldModel).getIndividual(ind.getURI()) == null) {
                            oldModel.add(ind.listProperties());
                        } else {
                            Individual oldInd = ((OntModelImpl) oldModel).getIndividual(ind.getURI());
                            Statement newLastUpdated = ind.getProperty(
                                    ind.getModel().getProperty(ONTOLOGY_NAMESPACE + "#LastUpdated")
                            );

                            Statement oldLastUpdated = oldInd.getProperty(
                                    oldInd.getModel().getProperty(ONTOLOGY_NAMESPACE + "#LastUpdated")
                            );

                            if (newLastUpdated != null) {
                                Instant newModelLastUpdated = Instant.ofEpochMilli(newLastUpdated.getLiteral().getLong());
                                if (oldLastUpdated == null ||
                                        newModelLastUpdated.isAfter(Instant.ofEpochMilli(oldLastUpdated.getLiteral().getLong()))) {

                                    oldModel.remove(oldInd.listProperties());
                                    oldModel.add(ind.listProperties());
                                }
                            }
                        }
                    }
                }
        );
    }
}
