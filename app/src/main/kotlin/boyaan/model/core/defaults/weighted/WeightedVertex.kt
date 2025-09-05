package boyaan.model.core.defaults.weighted

import boyaan.model.core.base.Vertex

public class WeightedVertex<D : Any?>(
    data: D,
    override var weight: Number = 0,
) : Vertex<D>(data),
    Weighted