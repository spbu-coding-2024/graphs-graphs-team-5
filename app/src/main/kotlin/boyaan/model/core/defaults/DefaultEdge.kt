package boyaan.model.core.defaults

import boyaan.model.core.base.Edge
import boyaan.model.core.base.Vertex

public class DefaultEdge<
        V : Vertex<*>,
        D : Any?,
        >(
    firstVertex: V,
    secondVertex: V,
    data: D,
) : Edge<V, D>(firstVertex, secondVertex, data)
