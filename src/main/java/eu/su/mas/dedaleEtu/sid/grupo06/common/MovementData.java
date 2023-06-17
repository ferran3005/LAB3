package eu.su.mas.dedaleEtu.sid.grupo06.common;

import eu.su.mas.dedale.env.Observation;

public class MovementData {

    public MovementData (int backpackFreeSpaceDiamate, int backpackFreeSpaceOro, Observation treasureType){
        this.backpackFreeSpaceDiamate = backpackFreeSpaceDiamate;
        this.backpackFreeSpaceOro = backpackFreeSpaceOro;
        this.treasureType = treasureType;
    }
    public int backpackFreeSpaceDiamate; //Espacio libre en la mochila
    public int backpackFreeSpaceOro; //Espacio libre en la mochila

    public Observation treasureType; //Tipo de tesoro que se esta buscando
}
