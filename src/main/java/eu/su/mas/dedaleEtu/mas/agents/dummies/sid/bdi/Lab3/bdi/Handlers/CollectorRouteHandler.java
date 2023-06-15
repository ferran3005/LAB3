package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.Handlers;

import org.apache.jena.rdf.model.Model;

public class CollectorRouteHandler implements RouteHandler {
    @Override
    public boolean updateStack(Model model, String fase, String situatedAgentName) {
        return false;
    }

    @Override
    public void updateAfterMovement() {

    }

    @Override
    public void discardTop() {

    }

    @Override
    public String computeNextPosition(Model model, String situatedAgentName) {
        return null;
    }
}
