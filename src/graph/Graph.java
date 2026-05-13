package graph;

import java.util.List;

public interface Graph<V> {

    int getSize();

    List<V> getVertices();

    V getVertex(int index);

    int getIndex(V vertex);

    List<Integer> getNeighbors(int index);

    int getDegree(int vertexIndex);

    void printEdges();

    void clear();

    boolean addVertex(V vertex);

    boolean addEdge(int u, int v);

    boolean addEdge(Edge edge);

    boolean remove(V vertex);

    boolean remove(int u, int v);

    UnweightedGraph<V>.SearchTree dfs(int startIndex);

    UnweightedGraph<V>.SearchTree bfs(int startIndex);
}
