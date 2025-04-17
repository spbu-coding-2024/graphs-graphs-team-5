package boyaan.model.node

import boyaan.model.property.Weight

class WeightedNode<V, T : Number>(id: Int, value: V, override val weight: T) : Node<V>(id, value), Weight<T>
