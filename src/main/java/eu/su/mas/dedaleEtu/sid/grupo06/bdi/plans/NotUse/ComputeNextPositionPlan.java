package eu.su.mas.dedaleEtu.sid.grupo06.bdi.plans.NotUse;

import bdi4jade.goal.Goal;
import bdi4jade.plan.DefaultPlan;
import bdi4jade.plan.planbody.PlanBody;

public class ComputeNextPositionPlan extends DefaultPlan {
    public ComputeNextPositionPlan(Class<? extends Goal> goalClass, Class<? extends PlanBody> planBodyClass) {
        super(goalClass, planBodyClass);
    }

    @Override
    public boolean isContextApplicable(Goal goal) {
        // TODO si el belief de movement no existe o es distinto de refused
        return false;
    }

}
