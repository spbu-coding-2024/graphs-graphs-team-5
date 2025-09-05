package boyaan.model.core.base

import boyaan.model.core.base.Vertex

abstract class Edge<
        V : Vertex<*>,
        D : Any?,
        >(
    val firstVertex: V,
    val secondVertex: V,
    var data: D,
)