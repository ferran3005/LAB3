package eu.su.mas.dedaleEtu.sid.grupo06.situated.behaviours;

import eu.su.mas.dedale.env.EntityType;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.sid.grupo06.SituatedAgent06;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.SHOUT_ONTOLOGY_PROTOCOL_OUT;

public class ShoutOntology extends OneShotBehaviour {

    ACLMessage msg;
    String modelJson;

    public ShoutOntology(ACLMessage msg, String modelJson) {
        super();
        this.msg = msg;
        this.modelJson = modelJson;
    }
    @Override
    public void action() {
        sendAgree();
        shoutOntology();
    }

    private void sendAgree() {
        ACLMessage agreeMsg = new ACLMessage(ACLMessage.AGREE);
        agreeMsg.setProtocol(SHOUT_ONTOLOGY_PROTOCOL_OUT);
        agreeMsg.addReceiver(((SituatedAgent06)this.myAgent).bdiAgent);
        this.myAgent.send(agreeMsg);
    }

    private void shoutOntology() {
        ACLMessage shoutMessage = new ACLMessage(ACLMessage.INFORM);
        shoutMessage.setSender(myAgent.getAID());
        shoutMessage.setProtocol(SHOUT_ONTOLOGY_PROTOCOL_OUT);
        shoutMessage.setContent(modelJson);

        if(((SituatedAgent06)myAgent).allAgents.isEmpty()) {
            updateFromDF();
        }
        ((SituatedAgent06)myAgent).allAgents.forEach(aid -> {if(!aid.getName().equals(this.myAgent.getAID().getName())) shoutMessage.addReceiver(aid);});
        ((AbstractDedaleAgent) this.myAgent).sendMessage(shoutMessage);

        msg.setPerformative(ACLMessage.INFORM);
        msg.setProtocol(SHOUT_ONTOLOGY_PROTOCOL_OUT);
        this.myAgent.send(msg);
    }


    private void updateFromDF() {
        DFAgentDescription templateExplo = new DFAgentDescription();
        DFAgentDescription templateCollect = new DFAgentDescription();
        DFAgentDescription templateTank = new DFAgentDescription();

        // Create ServiceDescription objects for each service type
        ServiceDescription serviceExplo = new ServiceDescription();
        serviceExplo.setType(EntityType.AGENT_EXPLORER.getName());
        ServiceDescription serviceCollect = new ServiceDescription();
        serviceCollect.setType(EntityType.AGENT_COLLECTOR.getName());
        ServiceDescription serviceTanker = new ServiceDescription();
        serviceTanker.setType(EntityType.AGENT_TANKER.getName());

        templateExplo.addServices(serviceExplo);
        templateCollect.addServices(serviceCollect);
        templateTank.addServices(serviceTanker);

        try { //TODO: LO ODIO LO ODIO LO ODIO
            List<AID> resultsExplo = Arrays.stream(DFService.search(this.myAgent, templateExplo)).map(DFAgentDescription::getName).collect(Collectors.toList());
            List<AID> resultsCollect = Arrays.stream(DFService.search(this.myAgent, templateCollect)).map(DFAgentDescription::getName).collect(Collectors.toList());
            List<AID> resultsTank = Arrays.stream(DFService.search(this.myAgent, templateTank)).map(DFAgentDescription::getName).collect(Collectors.toList());

            ((SituatedAgent06) myAgent).tankers = resultsTank;
            ((SituatedAgent06) myAgent).allAgents = Stream.concat(Stream.concat(resultsExplo.stream(), resultsCollect.stream()), resultsTank.stream()).collect(Collectors.toList());
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

}
