package boyaan.model.core.defaults.weighted

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex

public class WeightedGraph<
        V : Vertex<*>,
        E : WeightedEdge<V, *>
        > : Graph<V, E>() {

}