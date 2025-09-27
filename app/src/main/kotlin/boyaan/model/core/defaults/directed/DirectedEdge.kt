package boyaan.model.core.defaults.directed

import boyaan.model.core.base.Edge
import boyaan.model.core.base.Vertex

public class DirectedEdge<V, E>(override val u: Vertex<V>, override val v: Vertex<V>, override var data: E) :
    Edge<V, E>, Directed