import java.util.regex.Pattern

fun main(){
    val latticeTikz = """
    \arrow[from=1-8, to=3-8]
	\arrow[from=1-8, to=3-6]
	\arrow[from=1-8, to=3-10]
	\arrow[from=1-8, to=3-4]
	\arrow[from=1-8, to=3-12]
	\arrow[from=3-4, to=6-1]
	\arrow[from=3-4, to=6-3]
	\arrow[from=3-12, to=6-15]
	\arrow[from=3-12, to=6-13]
	\arrow[from=3-12, to=6-11]
	\arrow[from=3-6, to=6-1]
	\arrow[from=3-6, to=6-7]
	\arrow[from=3-4, to=6-9]
	\arrow[from=3-6, to=6-13]
	\arrow[from=3-8, to=6-1]
	\arrow[from=3-8, to=6-5]
	\arrow[from=3-8, to=6-11]
	\arrow[from=3-10, to=6-3]
	\arrow[from=3-10, to=6-5]
	\arrow[from=3-10, to=6-7]
	\arrow[from=3-10, to=6-15]
	\arrow[from=3-12, to=6-9]
	\arrow[from=6-1, to=9-6]
	\arrow[from=6-3, to=9-8]
	\arrow[from=6-5, to=9-10]
	\arrow[from=6-7, to=9-12]
	\arrow[from=9-4, to=11-8]
	\arrow[from=6-1, to=9-4]
	\arrow[from=9-6, to=11-8]
	\arrow[from=9-8, to=11-8]
	\arrow[from=9-10, to=11-8]
	\arrow[from=9-12, to=11-8]
	\arrow[from=6-13, to=9-12]
	\arrow[from=6-15, to=9-12]
	\arrow[from=6-15, to=9-10]
	\arrow[from=6-15, to=9-8]
	\arrow[from=6-13, to=9-6]
	\arrow[from=6-11, to=9-10]
	\arrow[from=6-11, to=9-6]
	\arrow[from=6-9, to=9-6]
	\arrow[from=6-9, to=9-8]
	\arrow[from=6-7, to=9-4]
	\arrow[from=6-5, to=9-4]
	\arrow[from=6-3, to=9-4]
    """
    println(computeMobiusLastColumn(latticeTikz));
}

class Graph {
    private val adjacencyList: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun addNode(node: String) {
        if (!adjacencyList.containsKey(node)) {
            adjacencyList[node] = mutableListOf()
        }
    }

    fun addArrow(fromNode: String, toNode: String) {
        adjacencyList[fromNode]?.add(toNode)
    }

    fun getArrows(node: String): List<String>? {
        return adjacencyList[node]
    }
}

fun computeMobiusLastColumn(latticeTikz: String) : MutableList<Int>{
    val lastColumn : MutableList<Int> = mutableListOf()
    computeMobius(latticeTikz).forEach{
        lastColumn.add(it.last())
    }
    return lastColumn
}

fun computeMobius(latticeTikz: String): MutableList<MutableList<Int>>{
    val mobiusMatrix: MutableList<MutableList<Int>>
    val pattern = Pattern.compile("from=([^,]*),.*?to=([^]]*)]")
    val latticeElementsSet: MutableSet<String> = mutableSetOf()
    var matcher = pattern.matcher(latticeTikz)

    while (matcher.find()) {
        matcher.group(1)?.let { latticeElementsSet.add(it) }
        matcher.group(2)?.let { latticeElementsSet.add(it) }
    }

    val latticeElements: List<String> = latticeElementsSet.sortedDescending()
    val n = latticeElements.size
    val lattice = Graph()

    latticeElements.forEach { lattice.addNode(it) }

    matcher = pattern.matcher(latticeTikz)

    while (matcher.find()) {
        val fromValue = matcher.group(1)
        val toValue = matcher.group(2)
        lattice.addArrow(fromValue!!, toValue!!)
    }

    mobiusMatrix = MutableList(n) {
        MutableList(n) { 0 }
    }

    for (x in latticeElements){
        for (y in latticeElements){
            if (x == y){
                mobiusMatrix[latticeElements.indexOf(x)][latticeElements.indexOf(y)] = 1
            }
            else if (latticeElements.indexOf(x) > latticeElements.indexOf(y)){
                mobiusMatrix[latticeElements.indexOf(x)][latticeElements.indexOf(y)] = 0
            }
            else{
                mobiusMatrix[latticeElements.indexOf(x)][latticeElements.indexOf(y)] =
                    computeMobiusEntries(lattice, y, x)
            }
        }
    }

    return mobiusMatrix
}

fun computeMobiusEntries(
    lattice: Graph, parent: String, child: String) : Int {
    return if (hasDescendantAs(lattice, parent, child)){
        mobiusRecursiveCompute(lattice, parent, child)
    }
    else{ 0 }
}

fun hasDescendantAs (
    lattice: Graph, parent: String, child: String) : Boolean{
    return if (lattice.getArrows(parent)?.contains(child)!!){
        true
    }
    else{
        var flag = false
        lattice.getArrows(parent)?.forEach{
            flag = flag or hasDescendantAs(lattice, it, child)
        }
        flag
    }
}

fun mobiusRecursiveCompute (lattice: Graph, parent: String, child: String) : Int{
    var sum = 0
    if (lattice.getArrows(parent)?.contains(child)!!){ return -1 }
    for (x in lattice.getArrows(parent)!!){
        if (hasDescendantAs(lattice, x, child)){
            sum += mobiusRecursiveCompute(lattice, x, child)
        }
    }
    return -sum
}