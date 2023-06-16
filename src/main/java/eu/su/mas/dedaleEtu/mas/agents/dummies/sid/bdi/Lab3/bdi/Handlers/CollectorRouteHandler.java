package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.Handlers;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.agent.BDIAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.situated.agent.SituatedAgent;
import jade.core.AID;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.impl.OntModelImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;

import java.time.Instant;
import java.util.List;
import java.util.Stack;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.common.Constants.ONTOLOGY_NAMESPACE;

public class CollectorRouteHandler implements RouteHandler {

    public Stack<Location> route;

    public boolean allExplored;

    public CollectorRouteHandler() {

        route = new Stack<>();
        allExplored = false;
    }

    @Override
    public boolean updateStack(Model model, String fase, String situatedAgentName) {


        if(!allExplored) // nodos nuevos
            fase3(model, situatedAgentName);
        else           //Nodos mas antiguos
            fase4(model, situatedAgentName);

        return route.isEmpty();
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
    public String computeNextPosition(Model model, AID situatedAgent, int gold, int diamond) {
        //FASE 1    -> busca tanker
        //FASE 2    -> busca tesoro
        //FASE 3 +  -> busca no visitados / busca mas tiempo sin ver

        if(!allExplored)isMapExplored(model);

        // TODO Mirar el % de oro
        if(gold > 20 || diamond > 20) // tanker
            fase1(model, situatedAgent.getLocalName());
        else    // recurso
            fase2(model, situatedAgent.getLocalName());
        if(route.isEmpty()) updateStack(model, "",situatedAgent.getLocalName());

        return route.peek().getLocationId();
    }

    private void fase1(Model model, String situatedAgentName){
        //TODO que busque tanker

        route = OntologyManager.shortestPathToTarget(model, situatedAgentName, (current) -> !model.getProperty(
                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + current),
                model.getProperty(ONTOLOGY_NAMESPACE + "#visited")).getBoolean()
        );

    }

    private void fase2(Model model, String situatedAgentName){
        //TODO que busque tesoro de su tipo

       /*

        route = OntologyManager.shortestPathToTarget(model, situatedAgentName, (current) ->
                model.getProperty(ONTOLOGY_NAMESPACE + "#Location_" + current + "-" + "Content_" + "DIAMOND"),
                model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.getResource(ONTOLOGY_NAMESPACE + "DIAMOND").getBoolean()
        );

        */

    }
    private void fase3(Model model, String situatedAgentName){
        route = OntologyManager.shortestPathToTarget(model, situatedAgentName, (current) -> !model.getProperty(
                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + current),
                model.getProperty(ONTOLOGY_NAMESPACE + "#visited")).getBoolean()
        );

    }
    private void fase4(Model model, String situatedAgentName){
        List<Individual> indivs = ((OntModelImpl) model).listIndividuals().toList();
        Long maxLastUpdated = Instant.now().toEpochMilli();
        int positinId = 0;

        Property lastUpdated = model.getProperty(ONTOLOGY_NAMESPACE + "#LastUpdated");
        Property locationId = model.getProperty(ONTOLOGY_NAMESPACE + "#position_id");
        for(Individual ind : indivs){
            Statement aux = ind.getProperty(lastUpdated);
            if(aux != null && ind.hasOntClass(ONTOLOGY_NAMESPACE + "#Node"))
                if(aux.getLong() < maxLastUpdated){
                    maxLastUpdated = aux.getLong();
                    try {
                        positinId = ind.getProperty(locationId).getInt();
                    }
                    catch (Exception e){
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



    public void isMapExplored(Model model){
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
