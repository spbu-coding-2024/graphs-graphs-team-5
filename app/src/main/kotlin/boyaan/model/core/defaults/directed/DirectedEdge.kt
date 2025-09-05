package boyaan.model.core.defaults.directed

import boyaan.model.core.base.Edge
import boyaan.model.core.base.Vertex

public class DirectedEdge<
        V : Vertex<*>,
        D : Any?
        >(
    sourceVertex: V,
    destinationVertex: V,
    data: D,
) : Edge<V, D>(sourceVertex, destinationVertex, data), Directed