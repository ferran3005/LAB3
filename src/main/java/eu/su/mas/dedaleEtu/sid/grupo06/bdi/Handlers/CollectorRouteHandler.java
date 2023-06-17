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
    public String computeNextPosition(Model model, AID situatedAgent, SituatedData situatedData) {

        if(route.isEmpty()){
            if(!allExplored)
                isMapExplored(model);
            double gold = -1;
            double diamond = -1;
            double threshold = 0.8;

            if(situatedData.getMaxCapGold() > 0)
                gold = situatedData.getBackPackCapacityGold() / situatedData.getMaxCapGold();
            if(situatedData.getMaxCapDiam() > 0)
                diamond = situatedData.getBackPackCapacityDiamond() / situatedData.getMaxCapDiam();

            if(gold == -1) //Diamante
                if(diamond < threshold)
                    faseTanker(model, situatedAgent.getLocalName());
                else
                    faseRecurso(model, situatedAgent.getLocalName(), "Diamond", null);
            else if(diamond == -1) // Oro
                if(gold < threshold)
                    faseTanker(model, situatedAgent.getLocalName());
                else
                    faseRecurso(model, situatedAgent.getLocalName(), "Gold", null);
            else{   //Los 2
                if(gold < threshold && diamond < threshold)
                    faseTanker(model, situatedAgent.getLocalName());
                else if(gold < threshold)
                    faseRecurso(model, situatedAgent.getLocalName(), "Diamond", null);
                else if(diamond < threshold)
                    faseRecurso(model, situatedAgent.getLocalName(), "Gold", null);
                else
                    faseRecurso(model, situatedAgent.getLocalName(), "Gold", "Diamond");
            }

            if(route.isEmpty()) updateStack(model, "",situatedAgent.getLocalName());
            if(route.isEmpty()) computeRandomPath(model, situatedAgent, situatedData);
        }

        return route.peek().getLocationId();
    }

    private void faseTanker(Model model, String situatedAgentName){

        route = OntologyManager.shortestPathToTarget(model, situatedAgentName, (current) ->
                {
                    StmtIterator it =  model.listStatements(
                            null,
                            model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                            model.getResource(ONTOLOGY_NAMESPACE + "#Tanker")
                    );

                    while (it.hasNext()){
                        Statement st = it.next();
                        Resource res = st.getSubject();
                        if(!model.listStatements(
                                res.asResource(),
                                model.getProperty(ONTOLOGY_NAMESPACE + "#is_in"),
                                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + current)).toList().isEmpty()) {
                            return true;
                        }
                    }

                    return false;
                }
        );
    }

    private void faseRecurso(Model model, String situatedAgentName, String recurso1, String recurso2){

        if(recurso2 != null){
            route = OntologyManager.shortestPathToTarget(model, situatedAgentName, (current) ->
                    {
                        StmtIterator it =  model.listStatements(
                                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + current),
                                model.getProperty(ONTOLOGY_NAMESPACE + "#hasObservation"),
                                (RDFNode) null);
                        while (it.hasNext()){
                            Statement st = it.next();
                            Resource res = st.getObject().asResource();
                            Boolean isGold, isDiamond;
                            isGold = res.getURI().contains("Gold");
                            isDiamond = res.getURI().contains("Diamond");
                            return isGold || isDiamond;
                        }
                        return false;
                    }
            );
        } else {
            route = OntologyManager.shortestPathToTarget(model, situatedAgentName, (current) ->
                    {
                        StmtIterator it =  model.listStatements(
                                model.getResource(ONTOLOGY_NAMESPACE + "#Location-" + current),
                                model.getProperty(ONTOLOGY_NAMESPACE + "#hasObservation"),
                                model.getResource(ONTOLOGY_NAMESPACE + "#Location_" + current + "-" + "Content_" + recurso1));
                        return it.hasNext();
                    }
            );
        }

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

    @Override
    public String computeRandomPath(Model model, AID situatedAgent, SituatedData situatedData) {
        route.clear();
        route = OntologyManager.shortestPathToTarget(model, situatedAgent.getLocalName(), (current) -> {
                    int randomNumber = new Random().nextInt(100);
                    return randomNumber < 30;
                }
        );
        return route.empty() ? computeRandomPath(model, situatedAgent, situatedData) : route.peek().getLocationId();
    }
}
