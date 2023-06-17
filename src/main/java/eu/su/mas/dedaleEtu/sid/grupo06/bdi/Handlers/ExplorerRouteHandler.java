package eu.su.mas.dedaleEtu.sid.grupo06.bdi.Handlers;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent.SituatedData;
import jade.core.AID;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.impl.OntModelImpl;
import org.apache.jena.rdf.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.ONTOLOGY_NAMESPACE;

public class ExplorerRouteHandler implements RouteHandler {
    public Stack<Location> route;
    public boolean allExplored;

    public ExplorerRouteHandler() {
        route = new Stack<>();
        allExplored = false;
    }

    public boolean updateStack(Model model, String fase, String situatedAgentName) {

        if (fase.equals("fase1"))
            fase1(model, situatedAgentName);
        else //Fase 2
            fase2(model, situatedAgentName);

        return !route.isEmpty();
    }

    public void updateAfterMovement() {
        route.pop();
    }

    public void discardTop() {
        route.pop();
    }

    public String computeRandomPath(Model model, AID situatedAgent, SituatedData situatedData) {
        route.clear();
        route = OntologyManager.shortestPathToTarget(model, situatedAgent.getLocalName(), (current) -> {
                    int randomNumber = new Random().nextInt(100);
                    return randomNumber < 30;
                }
        );
        return route.empty() ? computeRandomPath(model, situatedAgent, situatedData) : route.peek().getLocationId();
    }

    public String computeNextPosition(Model model, AID situatedAgent, SituatedData situatedData) {

        if (!allExplored) isMapExplored(model);
        if (route.isEmpty()) {
            String fase = !allExplored ? "fase1" : "fase2";
            boolean stackFilled = updateStack(model, fase, situatedAgent.getLocalName());
            if(!stackFilled) return computeRandomPath(model, situatedAgent, situatedData);
        }

        return route.peek().getLocationId();
    }

    public void isMapExplored(Model model) {
        allExplored = true; //Empezamos de esta premisa, y si hay algun nodo no visitado se pone a false

        ((OntModelImpl) model).listIndividuals().forEach(
                ind -> {
                    if (ind.getURI() != null) {
                        Statement visited = model.getProperty(model.getResource(ind.getURI()),
                                model.getProperty(ONTOLOGY_NAMESPACE + "#visited"));
                        if (visited != null && !visited.getBoolean()) allExplored = false;
                    }
                }
        );
    }

    private void fase1(Model model, String situatedAgentName) {
        route = OntologyManager.shortestPathToTarget(model, situatedAgentName, (current) -> !model.getProperty(
                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + current),
                model.getProperty(ONTOLOGY_NAMESPACE + "#visited")).getBoolean()
        );
    }

    private void fase2(Model model, String situatedAgentName) {
        List<Individual> indivs = ((OntModelImpl) model).listIndividuals().toList();
        Long maxLastUpdated = Instant.now().toEpochMilli();
        int positinId = 0;

        Property lastUpdated = model.getProperty(ONTOLOGY_NAMESPACE + "#LastUpdated");
        Property locationId = model.getProperty(ONTOLOGY_NAMESPACE + "#position_id");
        for (Individual ind : indivs) {
            Statement aux = ind.getProperty(lastUpdated);
            if (aux != null && ind.hasOntClass(ONTOLOGY_NAMESPACE + "#Node"))
                if (aux.getLong() < maxLastUpdated) {
                    maxLastUpdated = aux.getLong();
                    try {
                        positinId = ind.getProperty(locationId).getInt();
                    } catch (Exception e) {
                        System.out.print(e);
                    }

                }
        }
        int find = positinId;
        route = OntologyManager.shortestPathToTarget(model, situatedAgentName, (current) -> {

                    return (find == model.getProperty(
                            model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + current),
                            model.getProperty(ONTOLOGY_NAMESPACE + "#position_id")).getInt());
                }
        );
    }



}
