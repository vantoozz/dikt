package io.github.vantoozz.dikt

import kotlin.reflect.KClass

class AutoClosableContainer(
    private val container: Container,
    private val dependencyGraph: Map<AutoCloseable, Set<AutoCloseable>>,
) : Container, AutoCloseable {

    private var closed = false

    override fun <T : Any> get(klass: KClass<T>): T? {
        if (closed) {
            throw DiktRuntimeException("Container is closed")
        }
        return container[klass]
    }

    override fun close() {
        if (closed) {
            throw DiktRuntimeException("Container is already closed")
        }

        topologicalSort().forEach {
            it.close()
        }

        closed = true
    }

    private fun topologicalSort(): List<AutoCloseable> {
        val inDegree = mutableMapOf<AutoCloseable, Int>()
        val dependents = mutableMapOf<AutoCloseable, MutableList<AutoCloseable>>()

        for ((node, deps) in dependencyGraph) {
            inDegree.putIfAbsent(node, 0)
            for (dep in deps) {
                inDegree.putIfAbsent(dep, 0)
                dependents.getOrPut(dep) { mutableListOf() }.add(node)
                inDegree.merge(node, 1, Int::plus)
            }
        }

        val queue = ArrayDeque<AutoCloseable>()
        for ((node, degree) in inDegree) {
            if (degree == 0) {
                queue.add(node)
            }
        }

        val result = mutableListOf<AutoCloseable>()
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            result.add(node)
            for (dependent in dependents[node] ?: emptyList()) {
                val newDegree = (inDegree.getValue(dependent)) - 1
                inDegree[dependent] = newDegree
                if (newDegree == 0) {
                    queue.add(dependent)
                }
            }
        }

        return result.reversed()
    }
}
