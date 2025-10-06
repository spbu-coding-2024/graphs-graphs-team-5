package boyaan.model.algorithms

import boyaan.model.core.base.Graph

internal abstract class Algorithm<V, E>(
    protected val graph: Graph<V, E>,
)
