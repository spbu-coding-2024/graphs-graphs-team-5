package boyaan.model.algorithms.modern

data class Vec2(
    var x: Double,
    var y: Double,
) {
    operator fun plusAssign(other: Vec2) {
        x += other.x
        y += other.y
    }

    operator fun plus(other: Vec2): Vec2 = Vec2(x + other.x, y + other.y)

    operator fun minus(other: Vec2): Vec2 = Vec2(x - other.x, y - other.y)

    operator fun times(k: Double): Vec2 = Vec2(x * k, y * k)

    operator fun unaryMinus(): Vec2 = Vec2(-x, -y)

    fun norm(): Double = kotlin.math.sqrt(x * x + y * y)

    fun normalize(): Vec2 {
        val n = norm()
        return if (n == 0.0) Vec2(0.0, 0.0) else Vec2(x / n, y / n)
    }

    fun lerp(
        target: Vec2,
        t: Double,
    ): Vec2 = Vec2(x * (1 - t) + target.x * t, y * (1 - t) + target.y * t)
}

public class ForceAtlas2<V, E>(
    private val graph: boyaan.model.core.base.Graph<V, E>,
    private val gravity: Double = 0.001,
    private val strongGravity: Boolean = false,
    private val speed: Double = 0.1,
    private val maxForce: Double = 5.0,
) {
    private val positions: MutableMap<Int, Vec2> =
        graph.vertices.associate { it.key to Vec2((100..800).random().toDouble(), (100..600).random().toDouble()) }.toMutableMap()

    private val displacements: MutableMap<Int, Vec2> =
        graph.vertices.associate { it.key to Vec2(0.0, 0.0) }.toMutableMap()

    fun addVertex(vertexKey: Int) {
        if (vertexKey !in positions) {
            positions[vertexKey] = Vec2((100..800).random().toDouble(), (100..600).random().toDouble())
            displacements[vertexKey] = Vec2(0.0, 0.0)
        }
    }

    fun setPosition(
        vertexKey: Int,
        pos: Vec2,
    ) {
        positions[vertexKey] = pos
        displacements.putIfAbsent(vertexKey, Vec2(0.0, 0.0))
    }

    fun positionsSnapshot(): Map<Int, Vec2> = positions.toMap()

    private fun isNeighbor(
        u: Int,
        v: Int,
    ): Boolean = graph[u, v] != null || graph[v, u] != null

    public fun step(
        draggedVertex: Int?,
        dragPos: Vec2?,
    ) {
        displacements.keys.forEach { key ->
            displacements[key] = Vec2(0.0, 0.0)
        }

        val k = 50.0
        val repulsionStrength = 1.0
        val attractionStrength = 0.1

        val verticesList = graph.vertices.toList()

        for (i in verticesList.indices) {
            val u = verticesList[i]
            val uPos = positions[u.key] ?: continue

            for (j in i + 1 until verticesList.size) {
                val v = verticesList[j]
                val vPos = positions[v.key] ?: continue

                val delta = uPos - vPos
                val dist = delta.norm().coerceAtLeast(0.1)

                val force = repulsionStrength * (k * k) / dist
                val vec = delta.normalize() * force

                displacements[u.key]?.plusAssign(vec)
                displacements[v.key]?.plusAssign(-vec)
            }
        }

        for (e in graph.edges) {
            val u = e.key.first
            val v = e.key.second
            val uPos = positions[u] ?: continue
            val vPos = positions[v] ?: continue

            val delta = uPos - vPos
            val dist = delta.norm().coerceAtLeast(0.1)
            val force = attractionStrength * (dist * dist / k)
            val vec = delta.normalize() * (-force)

            displacements[u]?.plusAssign(vec)
            displacements[v]?.plusAssign(-vec)
        }

        for (u in graph.vertices) {
            val pos = positions[u.key] ?: continue
            val dist = pos.norm() + 0.01
            val force = gravity * if (strongGravity) dist else 1.0
            val vec = pos.normalize() * (-force)
            displacements[u.key]?.plusAssign(vec)
        }

        for (u in graph.vertices) {
            val disp = displacements[u.key] ?: Vec2(0.0, 0.0)
            val move = if (disp.norm() > maxForce) disp.normalize() * maxForce else disp
            val currentPos = positions[u.key] ?: Vec2(0.0, 0.0)
            val targetPos = currentPos + move * speed

            positions[u.key] =
                when {
                    u.key == draggedVertex && dragPos != null -> dragPos
                    draggedVertex != null && isNeighbor(u.key, draggedVertex) -> {
                        val draggedPos = positions[draggedVertex] ?: continue
                        targetPos.lerp(draggedPos, 0.05)
                    }
                    else -> targetPos
                }
        }
    }
}
