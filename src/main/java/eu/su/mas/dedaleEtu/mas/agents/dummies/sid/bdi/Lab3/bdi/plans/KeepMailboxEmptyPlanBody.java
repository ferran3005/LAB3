package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Lab3.bdi.plans;

import bdi4jade.annotation.Parameter;
import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.AbstractPlanBody;
import jade.lang.acl.ACLMessage;

public class KeepMailboxEmptyPlanBody extends AbstractPlanBody {
    private ACLMessage msgReceived;

    @Override
    public void action() {
        System.out.println("Emptying mailbox: " + this.msgReceived);
        setEndState(Plan.EndState.SUCCESSFUL);
    }

    @Parameter(direction = Parameter.Direction.IN)
    public void setMessage(ACLMessage msgReceived) {
        this.msgReceived = msgReceived;
    }

    @Override
    public int onEnd() {
        System.out.println("Mailbox emptied: " + this.msgReceived);
        return 0;
    }
}