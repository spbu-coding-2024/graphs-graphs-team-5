package boyaan.model.core.defaults.directed

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex

public class DirectedGraph<
        V : Vertex<*>,
        E : DirectedEdge<V, *>
        > : Graph<V, E>() {

}