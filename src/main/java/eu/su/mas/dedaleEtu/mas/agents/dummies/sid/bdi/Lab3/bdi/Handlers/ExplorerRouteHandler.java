package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.Handlers;

import eu.su.mas.dedale.env.Location;
import org.apache.jena.rdf.model.Model;

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

    public boolean updateStack(Model model, String fase) {

        route = OntologyManager.shortestPathToTarget(model, "Lab", (current) -> {//TODO LAB = NOMBRE AGENTE
                    return  !model.getProperty(
                            model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + current),
                            model.getProperty(ONTOLOGY_NAMESPACE + "#visited")).getBoolean();
                }
        );

        return !route.isEmpty();
    }

    public void updateAfterMovement(String oldLocationId, String newLocationId) {

    }

    public void discardTop() {
        route.pop();
    }

    public String computeNextPosition(Model model) {

        if(!allExplored)
        if(route.isEmpty() && !allExplored) updateStack(model, "fase1");
        else if(route.isEmpty() && allExplored) updateStack(model, "fase2");

        return route.peek().getLocationId();

    }

    private void isMapExplored(){


    }






}
