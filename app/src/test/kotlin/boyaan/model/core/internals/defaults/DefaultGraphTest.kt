package boyaan.model.core.internals.defaults

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

internal typealias SimpleGraph = DefaultGraph<String, String>

@TestInstance(PER_CLASS)
internal class DefaultGraphTest {
    @Nested
    inner class VertexTest {
        @Nested
        inner class AddTest {
            val graph = SimpleGraph()

            @Test
            fun `addVertex adds vertex`() {
                val a = graph.addVertex("a")

                assertEquals(1, graph.vertices.size)
                assertEquals(0, a.key)
                assertEquals("a", a.value)
            }

            @Test
            fun `addVerticesAll adds vertices`() {
                graph.addVerticesAll("a", "b", "c")

                assertEquals(3, graph.vertices.size)
                assertEquals("a", graph[0]?.value)
                assertEquals("b", graph[1]?.value)
                assertEquals("c", graph[2]?.value)
            }

            @Test
            fun `addVertex adds vertices with same values`() {
                graph.addVerticesAll("a", "a")
                assertEquals(2, graph.vertices.size)
                assert(graph[0]?.value == graph[1]?.value)
            }
        }

        @Nested
        inner class GetTest {
            val graph = SimpleGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d")
            }

            @Test
            fun `can get vertices only via valid keys`() {
                assertEquals("a", graph[0]?.value)
                assertEquals("d", graph[3]?.value)
                assertNull(graph[4]?.value)
                assertNull(graph[-1]?.value)
            }
        }

        @Nested
        inner class SetValueTest {
            val graph = SimpleGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d")
            }

            @Test
            fun `can change any vertex value`() {
                graph[0]?.value = "dd"
                graph[1]?.value = "cc"
                graph[2]?.value = "bb"
                graph[3]?.value = "aa"

                assertEquals("dd", graph[0]?.value)
                assertEquals("bb", graph[2]?.value)
                assertEquals("cc", graph[1]?.value)
                assertEquals("aa", graph[3]?.value)
            }
        }

        @Nested
        inner class RemoveTest {
            val graph = SimpleGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c")
            }

            @Test
            fun `removeVertex removes vertex`() {
                assertEquals(3, graph.vertices.size)
                graph.removeVertex(1)
                assertEquals(2, graph.vertices.size)
                assert("b" !in graph.vertices.map { it.value })
            }

            @Test
            fun `removeVertex cannot remove vertex via invalid key`() {
                assertEquals(3, graph.vertices.size)
                val removedTooBigKey = graph.removeVertex(3)
                assertEquals(3, graph.vertices.size)
                assertNull(removedTooBigKey)
                val removedNegativeKey = graph.removeVertex(-1)
                assertEquals(3, graph.vertices.size)
                assertNull(removedNegativeKey)
            }

            @Test
            fun `removeVertex cannot remove vertex via same key twice`() {
                assertEquals(3, graph.vertices.size)
                graph.removeVertex(2)
                assertEquals(2, graph.vertices.size)
                graph.removeVertex(2)
                assertEquals(2, graph.vertices.size)
            }
        }
    }

    @Nested
    inner class EdgeTest {
        @Nested
        inner class AddTest {
            val graph = SimpleGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d")
            }

            @Test
            fun `addEdge adds edge`() {
                val ac = graph.addEdge(0, 2, "ac")
                assertEquals(1, graph.edges.size)
                assertEquals("ac", ac?.value)
            }

            @Test
            fun `addEdge cannot update edge`() {
                val ac = graph.addEdge(0, 2, "ac")
                val acc = graph.addEdge(0, 2, "acc")
                assertEquals(1, graph.edges.size)
                assertEquals("ac", ac?.value)
                assertEquals("ac", acc?.value)
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
            fun `addEdge cannot add edge via reversed key`() {
                val ac = graph.addEdge(0, 2, "ac")
                graph.addEdge(2, 0, "ca")
                assertEquals(1, graph.edges.size)
                assertEquals("ac", ac?.value)
            }

            @Test
            fun `addEdge cannot add loop edge`() {
                assertNull(graph.addEdge(1, 1, "bb"))
            }
        }

        @Nested
        inner class GetTest {
            val graph = SimpleGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d")
                graph.addEdge(0, 2, "ac")
                graph.addEdge(1, 3, "bd")
            }

            @Test
            fun `can get edges only via valid keys`() {
                assertEquals("ac", graph[0, 2]?.value)
                assertEquals("ac", graph[2, 0]?.value)
                assertEquals("bd", graph[3, 1]?.value)
                assertNull(graph[0, 1])
                assertNull(graph[1, 1])
                assertNull(graph[0, 5])
                assertNull(graph[1, -1])
                assertNull(graph[1, 1])
            }
        }

        @Nested
        inner class SetValueTest {
            val graph = SimpleGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d")
                graph.addEdge(0, 2, "ac")
                graph.addEdge(1, 3, "bd")
                graph.addEdge(1, 2, "bc")
            }

            @Test
            fun `can change any edge value`() {
                graph[2, 0]?.value = "bbcc"
                graph[1, 3]?.value = "aacc"
                graph[2, 1]?.value = "bbdd"

                assertEquals("bbcc", graph[0, 2]?.value)
                assertEquals("aacc", graph[1, 3]?.value)
                assertEquals("bbdd", graph[1, 2]?.value)
            }
        }

        @Nested
        inner class RemoveTest {
            val graph = SimpleGraph()

            @BeforeEach
            fun setup() {
                graph.addVerticesAll("a", "b", "c", "d")
                graph.addEdge(0, 2, "ac")
                graph.addEdge(1, 3, "bd")
                graph.addEdge(1, 2, "bc")
            }

            @Test
            fun `removeEdge removes edge`() {
                assertEquals(3, graph.edges.size)
                graph.removeEdge(0, 2)
                assertEquals(2, graph.edges.size)
                graph.removeEdge(3, 1)
                assertEquals(1, graph.edges.size)
            }

            @Test
            fun `removeEdge cannot remove edge via invalid key`() {
                assertNull(graph.removeEdge(0, 5))
                assertNull(graph.removeEdge(1, -1))
                assertNull(graph.removeEdge(0, 0))
            }

            @Test
            fun `removeEdge cannot remove edge via same key twice`() {
                val ac = graph.removeEdge(0, 2)
                assertEquals(2, graph.edges.size)
                assertNotNull(ac)
                assertNull(graph.removeEdge(2, 0))
                assertEquals(2, graph.edges.size)
            }
        }

        @Nested
        inner class MixedTest {
            @Nested
            inner class RemoveVertexTest {
                val graph = SimpleGraph()

                @BeforeEach
                fun setup() {
                    graph.addVerticesAll("a", "b", "c", "d", "e")
                    graph.addEdge(2, 0, "ac")
                    graph.addEdge(1, 3, "bd")
                    graph.addEdge(2, 3, "cd")
                    graph.addEdge(0, 4, "ae")
                }

                @Test
                fun `removeVertex removes vertex and incident edges`() {
                    assertEquals(4, graph.edges.size)
                    graph.removeVertex(0)
                    assertEquals(2, graph.edges.size)
                    assertNull(graph[2, 0])
                    assertNull(graph[4, 0])
                    assertNotNull(graph[1, 3])
                    assertNotNull(graph[2, 3])
                    graph.removeVertex(3)
                    assertEquals(0, graph.edges.size)
                }
            }
        }
    }
}
