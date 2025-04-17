package boyaan.model.link

import boyaan.model.node.WeightedNode
import boyaan.model.property.Weight

class WeightedLink<N : WeightedNode<V, T>, T : Number, V>(
    source: N,
    target: N,
    override val weight: T,
) : Link<N, V>(source, target), Weight<T>
