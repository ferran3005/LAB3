package eu.su.mas.dedaleEtu.sid.grupo06.bdi.Handlers;

import eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent.SituatedData;
import jade.core.AID;
import org.apache.jena.rdf.model.Model;

public interface RouteHandler {



    public boolean updateStack(Model model, String fase, String situatedAgent);


    public void updateAfterMovement();


    public void discardTop();



    public String computeNextPosition(Model model, AID situatedAgentName, SituatedData situatedData);

    public void isMapExplored(Model model);

    public String computeRandomPath(Model model, AID situatedAgent, SituatedData situatedData);
}
