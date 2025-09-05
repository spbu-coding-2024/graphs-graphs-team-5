package boyaan.model.core.defaults.weighted

import boyaan.model.core.base.Edge
import boyaan.model.core.base.Graph

public class VertexWeightedGraph<
        V : WeightedVertex<*>,
        E : Edge<V, *>
        > : Graph<V, E>() {

}