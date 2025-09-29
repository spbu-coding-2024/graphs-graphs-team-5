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
                val v1 = addVertex(v1)
                val v2 = addVertex(v2)
                addEdge(v1.key, v2.key, "")
            }
        val vertexKeys = g.vertices.map { it.key }
        if (vertexKeys.size < 2) return

        val key1 = vertexKeys[0]
        val key2 = vertexKeys[1]

        val fa2 = ForceAtlas2(g)
        val before = safeDistance(fa2, key1, key2)

        repeat(500) { fa2.step(null, null) }

        val after = safeDistance(fa2, key1, key2)
        assertNotNull(before)
        assertNotNull(after)
        if (after == null || before == null) return
        assertTrue(after < before, "Connected vertices should move closer")
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

        val vertexKeys = g.vertices.map { it.key }
        if (vertexKeys.size < 2) return

        val key1 = vertexKeys[0]
        val key2 = vertexKeys[1]

        val fa2 = ForceAtlas2(g)
        val before = safeDistance(fa2, key1, key2)

        repeat(500) { fa2.step(null, null) }

        val after = safeDistance(fa2, key1, key2)
        assertNotNull(before)
        assertNotNull(after)
        if (after == null || before == null) return
        assertTrue(after > before, "Disconnected vertices should move apart")
    }

    private fun safeDistance(
        fa2: ForceAtlas2<String, String>,
        u: Int,
        v: Int,
    ): Double? {
        val pos = fa2.positionsSnapshot()
        val pu = pos[u]
        val pv = pos[v]
        return pu?.let { uPos ->
            pv?.let { vPos ->
                (uPos - vPos).norm()
            }
        }
    }
}
