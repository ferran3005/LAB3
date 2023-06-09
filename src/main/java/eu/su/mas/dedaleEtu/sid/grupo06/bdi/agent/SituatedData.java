package eu.su.mas.dedaleEtu.sid.grupo06.bdi.agent;

import eu.su.mas.dedale.env.EntityType;
import eu.su.mas.dedale.env.Observation;
import jade.core.AID;

import java.io.Serializable;

public class SituatedData implements Serializable {
    private static final long serialVersionUID = 1L;

    public SituatedData() {
        this.lockPicking = 0;
        this.strength = 0;
        this.backPackCapacityDiamond = 0;
        this.backPackCapacityGold = 0;
        this.maxCapGold = 0;
        this.maxCapDiam = 0;
        this.treasureType = null;
        this.agentType = null;
        this.situatedAgent = null;
    }

    public SituatedData(int lockPicking,
                        int strength,
                        int backPackCapacityDiamond,
                        int backPackCapacityGold,
                        int maxCapGold,
                        int maxCapDiam,
                        Observation treasureType,
                        String agentType,
                        AID situatedAgent) {
        this.lockPicking = lockPicking;
        this.strength = strength;
        this.backPackCapacityDiamond = backPackCapacityDiamond;
        this.backPackCapacityGold = backPackCapacityGold;
        this.maxCapGold = maxCapGold;
        this.maxCapDiam = maxCapDiam;
        this.treasureType = treasureType;
        this.agentType = agentType;
        this.situatedAgent = situatedAgent;
    }

    public int lockPicking;
    public int strength;
    public int backPackCapacityDiamond;
    public int backPackCapacityGold;
    public int maxCapGold;
    public int maxCapDiam;
    public Observation treasureType;
    public String agentType;
    public AID situatedAgent;

    public int getLockPicking() {
        return lockPicking;
    }

    public void setLockPicking(int lockPicking) {
        this.lockPicking = lockPicking;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getBackPackCapacityDiamond() {
        return backPackCapacityDiamond;
    }

    public void setBackPackCapacityDiamond(int backPackCapacityDiamond) {
        this.backPackCapacityDiamond = backPackCapacityDiamond;
    }

    public int getBackPackCapacityGold() {
        return backPackCapacityGold;
    }

    public void setBackPackCapacityGold(int backPackCapacityGold) {
        this.backPackCapacityGold = backPackCapacityGold;
    }

    public int getMaxCapGold() {
        return maxCapGold;
    }

    public void setMaxCapGold(int maxCapGold) {
        this.maxCapGold = maxCapGold;
    }

    public int getMaxCapDiam() {
        return maxCapDiam;
    }

    public void setMaxCapDiam(int maxCapDiam) {
        this.maxCapDiam = maxCapDiam;
    }

    public Observation getTreasureType() {
        return treasureType;
    }

    public void setTreasureType(Observation treasureType) {
        this.treasureType = treasureType;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public AID getSituatedAgent() {
        return situatedAgent;
    }

    public void setSituatedAgent(AID situatedAgent) {
        this.situatedAgent = situatedAgent;
    }
}
