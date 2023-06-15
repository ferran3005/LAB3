package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.Handlers;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BDIAgent;
import org.apache.jena.ontology.impl.OntModelImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;

import java.time.Instant;
import java.util.List;
import java.util.Stack;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY_NAMESPACE;

public class ExplorerRouteHandler implements RouteHandler{
    public Stack<Location> route;
    private boolean allExplored;
    public ExplorerRouteHandler() {
        route = new Stack<>();
        allExplored = false;
    }

    public boolean updateStack(Model model, String fase, String situatedAgentName) {
        if(fase.equals("fase1")) {
            route = OntologyManager.shortestPathToTarget(model, situatedAgentName, (current) -> !model.getProperty(
                    model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + current),
                    model.getProperty(ONTOLOGY_NAMESPACE + "#visited")).getBoolean()
            );
        }
        else{ //fase 2
            // TODO fase 2

            Instant maxLastUpdated = Instant.now();

            ((OntModelImpl) model).listIndividuals().forEach(
                    ind -> {
                        if (ind.getURI() != null) {
                            if(!model.getProperty(model.getResource(ind.getURI()),
                                    model.getProperty(ONTOLOGY_NAMESPACE + "#visited")).getBoolean()) allExplored = false;

                            Statement actual = ind.getProperty(
                                    ind.getModel().getProperty(ONTOLOGY_NAMESPACE + "#LastUpdated")
                            );
                            if(actual != null) {
                                Instant actualInstant = Instant.ofEpochMilli(actual.getLiteral().getLong());
                                if(actualInstant.isAfter(maxLastUpdated)){ // mirar
                                    //maxLastUpdated =
                                }
                            }
                        }
                    }
            );


            route = OntologyManager.shortestPathToTarget(model, situatedAgentName, (current) -> {
                        return !model.getProperty(
                                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + current),
                                model.getProperty(ONTOLOGY_NAMESPACE + "#visited")).getBoolean();
                    }
            );
        }
        return !route.isEmpty();
    }

    public void updateAfterMovement() {
        route.pop();
    }

    public void discardTop() {
        route.pop();
    }

    public String computeNextPosition(Model model, String situatedAgentName) {

        if(!allExplored)isMapExplored(model);
        if(route.isEmpty()){
            String fase = !allExplored ? "fase1" : "fase2";
            updateStack(model, fase, situatedAgentName);
        }

        return route.peek().getLocationId();
    }

    private void isMapExplored(Model model){
        allExplored = true; //Empezamos de esta premisa, y si hay algun nodo no visitado se pone a false

        ((OntModelImpl) model).listIndividuals().forEach(
                ind -> {
                    if (ind.getURI() != null) {
                        Statement visited = model.getProperty(model.getResource(ind.getURI()),
                                model.getProperty(ONTOLOGY_NAMESPACE + "#visited"));
                        if(visited != null && !visited.getBoolean()) allExplored = false;
                    }
                }
        );
    }



}
