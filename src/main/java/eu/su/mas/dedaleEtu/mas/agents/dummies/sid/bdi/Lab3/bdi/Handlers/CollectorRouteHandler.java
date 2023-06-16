package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.Handlers;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import jade.core.AID;
import org.apache.jena.rdf.model.Model;

import java.util.Stack;

public class CollectorRouteHandler implements RouteHandler {

    public Stack<Location> route;


    public CollectorRouteHandler() {
        route = new Stack<>();
    }

    @Override
    public boolean updateStack(Model model, String fase, String situatedAgentName) {

        return false;
    }

    @Override
    public void updateAfterMovement() {

        route.pop();
    }

    @Override
    public void discardTop() {

        route.pop();
    }

    @Override
    public String computeNextPosition(Model model, AID situatedAgent) {
        //TODO Saber espacio
        if(route.isEmpty()){
            updateStack(model, "fase", situatedAgent.getLocalName());
        }

        return route.peek().getLocationId();
    }



}
