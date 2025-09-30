package boyaan.model.algorithms.modern

import boyaan.model.core.internals.defaults.DefaultGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ForceAtlas2Test {
    @ParameterizedTest
    @CsvSource(
        "3.0,4.0,5.0",
        "0.0,0.0,0.0",
        "1.0,0.0,1.0",
    )
    fun `norm is computed correctly`(
        x: Double,
        y: Double,
        expected: Double,
    ) {
        val v = Vec2(x, y)
        assertEquals(expected, v.norm(), 1e-6)
    }

    @ParameterizedTest
    @CsvSource(
        "3.0,4.0,1.0",
        "10.0,0.0,1.0",
    )
    fun `normalize returns unit vector`(
        x: Double,
        y: Double,
        expectedNorm: Double,
    ) {
        val v = Vec2(x, y).normalize()
        assertEquals(expectedNorm, v.norm(), 1e-6)
    }

    @ParameterizedTest
    @CsvSource("A,B")
    fun `connected vertices move closer`(
        v1: String,
        v2: String,
    ) {
        val g =
            DefaultGraph<String, String>().apply {
                val vertex1 = addVertex(v1)
                val vertex2 = addVertex(v2)
                addEdge(vertex1.key, vertex2.key, "")
            }

        val keys = g.vertices.map { it.key }
        if (keys.size < 2) return

        val fa2 = ForceAtlas2(g)

        fa2.setPosition(keys[0], Vec2(100.0, 100.0))
        fa2.setPosition(keys[1], Vec2(300.0, 100.0))

        val before = safeDistance(fa2, keys[0], keys[1])
        repeat(500) { fa2.step(null, null) }
        val after = safeDistance(fa2, keys[0], keys[1])

        assertNotNull(before)
        assertNotNull(after)
        if (before != null && after != null) {
            assertTrue(after < before, "Connected vertices should move closer")
        }
    }

    @ParameterizedTest
    @CsvSource("A,B")
    fun `disconnected vertices repel`(
        v1: String,
        v2: String,
    ) {
        val g =
            DefaultGraph<String, String>().apply {
                addVertex(v1)
                addVertex(v2)
            }

        val keys = g.vertices.map { it.key }
        if (keys.size < 2) return

        val fa2 = ForceAtlas2(g)

        fa2.setPosition(keys[0], Vec2(100.0, 100.0))
        fa2.setPosition(keys[1], Vec2(200.0, 100.0))

        val before = safeDistance(fa2, keys[0], keys[1])
        repeat(500) { fa2.step(null, null) }
        val after = safeDistance(fa2, keys[0], keys[1])

        assertNotNull(before)
        assertNotNull(after)
        if (before != null && after != null) {
            assertTrue(after > before, "Disconnected vertices should move apart")
        }
    }

    private fun safeDistance(
        fa2: ForceAtlas2<String, String>,
        u: Int,
        v: Int,
    ): Double? {
        val pos = fa2.positionsSnapshot()
        val pu = pos[u]
        val pv = pos[v]
        return if (pu != null && pv != null) (pu - pv).norm() else null
    }
}
