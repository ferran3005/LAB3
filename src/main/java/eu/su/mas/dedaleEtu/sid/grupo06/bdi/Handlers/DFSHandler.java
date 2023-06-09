package eu.su.mas.dedaleEtu.sid.grupo06.bdi.Handlers;

import com.google.gson.Gson;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static eu.su.mas.dedaleEtu.sid.grupo06.common.Constants.observationsType;

public class DFSHandler {

    public DFSHandler() {
        visited = new ArrayList<>();
        stack = new Stack<>();
        path = new Stack<>();
    }

    public ArrayList<Location> visited;
    public Stack<Location> stack;
    public Stack<Location> path;

    public boolean updateStack(String ObservationsJson) {

        List<Couple<Location, List<Couple<Observation, Integer>>>> observations =
                new Gson().fromJson(ObservationsJson, observationsType());
        if(!contains(observations.get(0).getLeft(), visited))  {
            visited.add(observations.get(0).getLeft());
        }
        boolean currentWind = observations.get(0).getRight().stream().map(Couple::getLeft)
                .collect(Collectors.toList())
                .contains(Observation.WIND);

        for (Couple<Location, List<Couple<Observation, Integer>>> obs : observations) {
            boolean obsWind = obs.getRight().stream().map(Couple::getLeft)
                    .collect(Collectors.toList())
                    .contains(Observation.WIND);

            if (!contains(obs.getLeft(), visited) && (!contains(obs.getLeft(), stack)) && !(currentWind && obsWind)) stack.push(obs.getLeft());
        }
        System.out.println("Stack: " + stack);
        return !stack.isEmpty();
    }

    public void updateAfterMovement(String oldLocationId, String newLocationId) {
        Location newLocation = new gsLocation(newLocationId);
        Location oldLocation = new gsLocation(oldLocationId);
        if(contains(newLocation, visited))  {
            path.pop();
        }
        else {
            Location pathSegment = stack.pop();
            visited.add(pathSegment);
            path.push(oldLocation);
        }
    }

    public void discardTop() {
        stack.pop();
    }

    public String computeNextPosition(List<String> adjacentNodes) {
        if (adjacentNodes.contains(stack.peek().getLocationId())) {
            return stack.peek().getLocationId();
        }
        return path.peek().getLocationId();
    }

    private Boolean contains(Location loc, List<Location> locations) {
        for (Location vis : locations) {
            if (loc.getLocationId().equals(vis.getLocationId())) return true;
        }
        return false;
    }
}
