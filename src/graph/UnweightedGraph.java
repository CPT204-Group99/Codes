package graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UnweightedGraph<V> implements Graph<V> {

    protected List<V> vertices = new ArrayList<>();
    protected List<List<Edge>> neighbors = new ArrayList<>();

    public UnweightedGraph() {
    }

    public UnweightedGraph(List<V> vertices, List<Edge> edges) {
        for (V vertex : vertices) {
            addVertex(vertex);
        }
        createAdjacencyLists(edges);
    }

    private void createAdjacencyLists(List<Edge> edges) {
        for (Edge edge : edges) {
            addEdge(edge);
        }
    }

    @Override
    public int getSize() {
        return vertices.size();
    }

    @Override
    public List<V> getVertices() {
        return vertices;
    }

    @Override
    public V getVertex(int index) {
        return vertices.get(index);
    }

    @Override
    public int getIndex(V vertex) {
        return vertices.indexOf(vertex);
    }

    @Override
    public List<Integer> getNeighbors(int index) {
        List<Integer> result = new ArrayList<>();
        for (Edge edge : neighbors.get(index)) {
            result.add(edge.v);
        }
        return result;
    }

    @Override
    public int getDegree(int vertexIndex) {
        return neighbors.get(vertexIndex).size();
    }

    @Override
    public void printEdges() {
        for (int u = 0; u < neighbors.size(); u++) {
            System.out.print(getVertex(u) + " (" + u + "): ");
            for (Edge edge : neighbors.get(u)) {
                System.out.print("(" + getVertex(edge.u) + ", " + getVertex(edge.v) + ") ");
            }
            System.out.println();
        }
    }

    @Override
    public void clear() {
        vertices.clear();
        neighbors.clear();
    }

    @Override
    public boolean addVertex(V vertex) {
        if (vertices.contains(vertex)) {
            return false;
        }
        vertices.add(vertex);
        neighbors.add(new ArrayList<>());
        return true;
    }

    @Override
    public boolean addEdge(Edge edge) {
        if (edge.u < 0 || edge.u >= getSize()) {
            throw new IllegalArgumentException("No such index: " + edge.u);
        }
        if (edge.v < 0 || edge.v >= getSize()) {
            throw new IllegalArgumentException("No such index: " + edge.v);
        }
        if (neighbors.get(edge.u).contains(edge)) {
            return false;
        }
        neighbors.get(edge.u).add(edge);
        return true;
    }

    @Override
    public boolean addEdge(int u, int v) {
        return addEdge(new Edge(u, v));
    }

    @Override
    public SearchTree dfs(int startIndex) {
        List<Integer> searchOrder = new ArrayList<>();
        int[] parent = new int[vertices.size()];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = -1;
        }
        boolean[] visited = new boolean[vertices.size()];
        dfs(startIndex, parent, searchOrder, visited);
        return new SearchTree(startIndex, parent, searchOrder);
    }

    private void dfs(int current, int[] parent, List<Integer> searchOrder, boolean[] visited) {
        searchOrder.add(current);
        visited[current] = true;

        for (Edge edge : neighbors.get(current)) {
            if (!visited[edge.v]) {
                parent[edge.v] = current;
                dfs(edge.v, parent, searchOrder, visited);
            }
        }
    }

    @Override
    public SearchTree bfs(int startIndex) {
        List<Integer> searchOrder = new ArrayList<>();
        int[] parent = new int[vertices.size()];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = -1;
        }

        LinkedList<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[vertices.size()];
        queue.offer(startIndex);
        visited[startIndex] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            searchOrder.add(current);
            for (Edge edge : neighbors.get(current)) {
                if (!visited[edge.v]) {
                    queue.offer(edge.v);
                    parent[edge.v] = current;
                    visited[edge.v] = true;
                }
            }
        }

        return new SearchTree(startIndex, parent, searchOrder);
    }

    @Override
    public boolean remove(V vertex) {
        return false;
    }

    @Override
    public boolean remove(int u, int v) {
        return false;
    }

    public class SearchTree {

        private final int root;
        private final int[] parent;
        private final List<Integer> searchOrder;

        public SearchTree(int root, int[] parent, List<Integer> searchOrder) {
            this.root = root;
            this.parent = parent;
            this.searchOrder = searchOrder;
        }

        public int getRoot() {
            return root;
        }

        public int getParent(int vertexIndex) {
            return parent[vertexIndex];
        }

        public List<Integer> getSearchOrder() {
            return searchOrder;
        }

        public int getNumberOfVerticesFound() {
            return searchOrder.size();
        }

        public List<V> getPath(int index) {
            ArrayList<V> path = new ArrayList<>();
            do {
                path.add(vertices.get(index));
                index = parent[index];
            } while (index != -1);
            return path;
        }

        public void printPath(int index) {
            List<V> path = getPath(index);
            System.out.print("A path from " + vertices.get(root) + " to " + vertices.get(index) + ": ");
            for (int i = path.size() - 1; i >= 0; i--) {
                System.out.print(path.get(i) + " ");
            }
        }
    }
}
