package com.unal;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graph {

    HashMap<String, ArrayList<Route>> nodes = new HashMap<>();

    public Graph(){}

    public void addEdge(String origin_node, String neighbour_node, Integer time, Integer route_id){
        if (!nodes.containsKey(origin_node)){
            nodes.put(origin_node, new ArrayList<>());
        }
        if (!nodes.containsKey(neighbour_node)){
            nodes.put(neighbour_node, new ArrayList<>());
        }

        nodes.get(origin_node).add(new Route(origin_node ,neighbour_node, time, route_id));

    }

    public List<Route> shortestPath(String origin_node, String destiny_node){
        HashMap<String, PathNode> stops = new HashMap<>();
        PriorityQueue<Route> routes = new PriorityQueue<>(Comparator.comparingInt(o -> o.time));
        ArrayList<Route> answer = new ArrayList<>();

        for (String node : nodes.keySet()){
            stops.put(node, new PathNode());
        }

        stops.get(origin_node).previous_node = null;
        stops.get(origin_node).time_from_origin = 0;
        stops.get(origin_node).status = true;
        routes.addAll(nodes.get(origin_node));



        while (!routes.isEmpty()){
            Route route = routes.poll();

            if (!stops.get(route.end_node).status) {
                Integer new_time = route.time + stops.get(route.start_node).time_from_origin;

                if (stops.get(route.end_node).time_from_origin != 0){

                    Integer old_time = stops.get(route.end_node).time_from_origin;

                    if (new_time < old_time){
                        stops.get(route.end_node).previous_node = route.start_node;
                        stops.get(route.end_node).time_from_origin = new_time;
                        stops.get(route.end_node).route_id.set(0, route.route_id);
                    } else if (new_time == old_time) {
                        stops.get(route.end_node).route_id.add(route.route_id);

                    }
                } else {
                    stops.get(route.end_node).previous_node = route.start_node;
                    stops.get(route.end_node).time_from_origin = new_time;
                    stops.get(route.end_node).route_id.add(route.route_id);
                }

                stops.get(route.end_node).status = true;
                routes.addAll(nodes.get(route.end_node));
            }

        }


        String prev = destiny_node;

        while (true){
            if (stops.get(prev).previous_node == null){
                break;
            } else {

                if (stops.get(prev).route_id.size() <= 1){
                    for (Route route : nodes.get(stops.get(prev).previous_node)){
                        if (stops.get(prev).route_id.getFirst() == route.route_id){
                            answer.add(route);

                        }
                    }
                    prev = stops.get(prev).previous_node;

                } else {
                    Integer match_id = 0;
                    for (Integer id : stops.get(stops.get(prev).previous_node).route_id){
                        if (stops.get(prev).route_id.contains(id)){
                            match_id = id;
                        }
                    }
                    if (match_id == 0){
                        for (Route route : nodes.get(stops.get(prev).previous_node)){
                            if (stops.get(prev).route_id.getFirst() == route.route_id){
                                answer.add(route);
                            }
                        }
                        prev = stops.get(prev).previous_node;
                    } else {
                        for (Route route : nodes.get(stops.get(prev).previous_node)) {
                            if (match_id == route.route_id) {
                                answer.add(route);
                                for (Route prev_route : nodes.get(stops.get(stops.get(prev).previous_node).previous_node)) {
                                    if (match_id == prev_route.route_id) {
                                        answer.add(prev_route);
                                    }
                                }

                            }
                        }

                        if (stops.get(prev).previous_node == null){
                            break;
                        }
                        prev = stops.get(stops.get(prev).previous_node).previous_node;
                    }
                }
            }
        }
        return answer.reversed();

    }

    public void readFile(File routes) {
        int nrutas = 0;
        nodes.clear();
        try{
            Scanner reader = new Scanner(routes);

            if (reader.hasNextLine()) {
                nrutas = Integer.parseInt(reader.nextLine());
            }
            for (int r = 0; r < nrutas; r++){
                String[] parts = reader.nextLine().split(" ");

                for (int i = 1; i<=parts.length-2; i+=2){
                    addEdge(parts[i], parts[i+2], Integer.parseInt(parts[i+1]), Integer.parseInt(parts[0]));
                }
            }

            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    public class Route{
        String start_node;
        String end_node;
        Integer time;
        Integer route_id;



        public Route(String start_node, String end_node, Integer time, Integer route_id){
            this.start_node = start_node;
            this.end_node = end_node;
            this.time = time;
            this.route_id = route_id;
        }


        public Integer getTime(){
            return time;
        }
        public Integer getRouteID(){
            return route_id;
        }

    }

    private class PathNode{
        String previous_node = null;
        Integer time_from_origin = 0;
        ArrayList<Integer> route_id = new ArrayList<>();
        boolean status = false;

        public PathNode(){}


    }
}