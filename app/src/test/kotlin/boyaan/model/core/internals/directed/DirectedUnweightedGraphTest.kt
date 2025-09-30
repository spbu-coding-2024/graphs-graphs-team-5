package boyaan.model.core.internals.directed

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

internal typealias DUGraph = DirectedUnweightedGraph<String, String>

@TestInstance(PER_CLASS)
internal class DirectedUnweightedGraphTest {
    @Nested
    inner class EdgeTest {
        @Nested
        inner class AddTest {
            val graph = DUGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d")
            }

            @Test
            fun `addEdge adds edge only for one direction`() {
                val ac = graph.addEdge(0, 2, "ac")
                assertEquals(1, graph.edges.size)
                assertNull(graph[2, 0])
                assertNotNull(ac)
                val ca = graph.addEdge(2, 0, "ca")
                assertEquals(2, graph.edges.size)
                assertNotNull(ca)
            }

            @Test
            fun `addEdge cannot add edge via invalid key`() {
                val addTooBigKey = graph.addEdge(0, 5, "too big")
                val addNegativeKey = graph.addEdge(-1, 1, "negative")
                assertEquals(0, graph.edges.size)
                assertEquals(0, graph.edges.size)
                assertNull(addTooBigKey)
                assertNull(addNegativeKey)
            }

            @Test
            fun `addEdge cannot add loop edge`() {
                val bb = graph.addEdge(1, 1, "bb")
                assertEquals(0, graph.edges.size)
                assertNull(bb)
            }
        }

        @Nested
        inner class GetTest {
            val graph = DUGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d")
                graph.addEdge(0, 2, "ac")
                graph.addEdge(1, 3, "bd")
            }

            @Test
            fun `can get edges only via valid keys`() {
                assertEquals("ac", graph[0, 2]?.value)
                assertEquals("bd", graph[1, 3]?.value)
                assertNull(graph[2, 0])
                assertNull(graph[3, 1])
            }
        }

        @Nested
        inner class SetValueTest {
            val graph = DUGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d")
                graph.addEdge(0, 2, "ac")
                graph.addEdge(1, 3, "bd")
                graph.addEdge(1, 2, "bc")
            }

            @Test
            fun `can change any edge value`() {
                graph[0, 2]?.value = "bbcc"
                graph[1, 3]?.value = "aacc"
                graph[1, 2]?.value = "bbdd"

                assertEquals("bbcc", graph[0, 2]?.value)
                assertEquals("aacc", graph[1, 3]?.value)
                assertEquals("bbdd", graph[1, 2]?.value)
            }
        }

        @Nested
        inner class RemoveTest {
            val graph = DUGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d")
                graph.addEdge(0, 2, "ac")
                graph.addEdge(1, 3, "bd")
                graph.addEdge(1, 2, "bc")
            }

            @Test
            fun `removeEdge cannot remove edge via reversed key`() {
                assertEquals(3, graph.edges.size)
                val ca = graph.removeEdge(2, 0)
                assertEquals(3, graph.edges.size)
                assertNull(ca)
                val ac = graph.removeEdge(0, 2)
                assertEquals(2, graph.edges.size)
                assertNotNull(ac)
            }
        }
    }

    @Nested
    inner class MixedTest {
        @Nested
        inner class RemoveVertexTest {
            val graph = DUGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d", "e")
                graph.addEdge(0, 2, "ac")
                graph.addEdge(1, 3, "bd")
                graph.addEdge(1, 2, "bc")
            }

            @Test
            fun `removeVertex removes vertex and incident edges`() {
                assertEquals(3, graph.edges.size)
                graph.removeVertex(1)
                assertEquals(1, graph.edges.size)
                assertNull(graph[1, 3])
                assertNull(graph[1, 2])
            }

            @Test
            fun `removeVertex can just remove vertex`() {
                assertEquals(5, graph.vertices.size)
                assertEquals(3, graph.edges.size)
                graph.removeVertex(4)
                assertEquals(4, graph.vertices.size)
                assertEquals(3, graph.edges.size)
            }

            @Test
            fun `removeVertex cannot remove vertex via invalid key`() {
                val removedTooBigKey = graph.removeVertex(5)
                val removedNegativeKey = graph.removeVertex(-1)
                assertNull(removedTooBigKey)
                assertNull(removedNegativeKey)
            }

            @Test
            fun `removeVertex removes both directional incident edges`() {
                graph.addEdge(2, 3, "cd")
                assertEquals(4, graph.edges.size)
                graph.removeVertex(2)
                assertEquals(1, graph.edges.size)
            }
        }
    }
}
