package boyaan.model.core.defaults.weighted

import boyaan.model.core.base.Edge
import boyaan.model.core.base.Vertex

public class WeightedEdge<
        V : Vertex<*>,
        D : Any?,
        >(
    sourceVertex: V,
    destinationVertex: V,
    data: D,
    override var weight: Number = 0,
) : Edge<V, D>(sourceVertex, destinationVertex, data),
    Weighted