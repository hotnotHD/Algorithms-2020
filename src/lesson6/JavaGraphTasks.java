package lesson6;

import kotlin.NotImplementedError;

import java.util.*;

@SuppressWarnings("unused")
public class JavaGraphTasks {
    /**
     * Эйлеров цикл.
     * Средняя
     *
     * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
     * Если в графе нет Эйлеровых циклов, вернуть пустой список.
     * Соседние дуги в списке-результате должны быть инцидентны друг другу,
     * а первая дуга в списке инцидентна последней.
     * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
     * Веса дуг никак не учитываются.
     *
     * Пример:
     *
     *      G -- H
     *      |    |
     * A -- B -- C -- D
     * |    |    |    |
     * E    F -- I    |
     * |              |
     * J ------------ K
     *
     * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
     *
     * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
     * связного графа ровно по одному разу
     */

    //Алгоритм решения взят с сайта: https://bit.ly/2UTVo16
    // R = O(2E) V - кол-во вершин
    // T = O(E)   E - кол-во ребер
    public static List<Graph.Edge> findEulerLoop(Graph graph) {
        List<Graph.Edge> list = new ArrayList<>();
        if (graph.getVertices().isEmpty() || !isEuler(graph)){ // R = O(V)
            return list;
        }
        Graph.Vertex first = null;
        for (Graph.Vertex vertex: graph.getVertices()){ // R = O(V)
            first = vertex;
            break;
        }
        Set<Graph.Edge> paths = graph.getEdges();
        Stack<Graph.Vertex> stack = new Stack<>();
        stack.push(first);
        while (!stack.empty()){ // R = O(E+E)
            Graph.Vertex cur = stack.peek();
            boolean found = false;
            for (Graph.Vertex vertex: graph.getNeighbors(cur)) { // стэк заполняется вершинами в нужном порядке
                Graph.Edge edge = graph.getConnection(cur, vertex); // на кол-во E
                if (paths.contains(edge)) {
                    stack.push(vertex);
                    paths.remove(edge);
                    found = true;
                    break;
                }
            }
            if (!found) {   // стэк очищается на кол-во Е
                if(first == cur){
                    stack.pop();
                    cur = stack.peek();
                }
                stack.pop();
                list.add(graph.getConnection(cur, first));
                first = cur;
            }
        }
        if(!paths.isEmpty()){
            list.clear();
        }
        return list;
    }

    public static boolean isEuler(Graph graph) {
        for (Graph.Vertex vertex: graph.getVertices()){
            if (graph.getConnections(vertex).size() % 2 != 0 || graph.getConnections(vertex).size() == 0)
                return false;
        }
       return true;
    }

    /**
     * Минимальное остовное дерево.
     * Средняя
     *
     * Дан связный граф (получатель). Найти по нему минимальное остовное дерево.
     * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
     * вернуть любое из них. Веса дуг не учитывать.
     *
     * Пример:
     *
     *      G -- H
     *      |    |
     * A -- B -- C -- D
     * |    |    |    |
     * E    F -- I    |
     * |              |
     * J ------------ K
     *
     * Ответ:
     *
     *      G    H
     *      |    |
     * A -- B -- C -- D
     * |    |    |
     * E    F    I
     * |
     * J ------------ K
     */
    public static Graph minimumSpanningTree(Graph graph) {
        throw new NotImplementedError();
    }

    /**
     * Максимальное независимое множество вершин в графе без циклов.
     * Сложная
     *
     * Дан граф без циклов (получатель), например
     *
     *      G -- H -- J
     *      |
     * A -- B -- D
     * |         |
     * C -- F    I
     * |
     * E
     *
     * Найти в нём самое большое независимое множество вершин и вернуть его.
     * Никакая пара вершин в независимом множестве не должна быть связана ребром.
     *
     * Если самых больших множеств несколько, приоритет имеет то из них,
     * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
     *
     * В данном случае ответ (A, E, F, D, G, J)
     *
     * Если на входе граф с циклами, бросить IllegalArgumentException
     *
     * Эта задача может быть зачтена за пятый и шестой урок одновременно
     */
    public static Set<Graph.Vertex> largestIndependentVertexSet(Graph graph) {
        throw new NotImplementedError();
    }

    /**
     * Наидлиннейший простой путь.
     * Сложная
     *
     * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
     * Простым считается путь, вершины в котором не повторяются.
     * Если таких путей несколько, вернуть любой из них.
     *
     * Пример:
     *
     *      G -- H
     *      |    |
     * A -- B -- C -- D
     * |    |    |    |
     * E    F -- I    |
     * |              |
     * J ------------ K
     *
     * Ответ: A, E, J, K, D, C, H, G, B, F, I
     */

    public static class Helper {
        Graph graph;
        Path maxPath = new Path();
        List<Graph.Vertex> currentPathL = new ArrayList<>();

        public Helper(Graph graph){
            this.graph = graph;
        }

        public Path search(){ // O(C * V * V) ~ O(V^2)
            for(Graph.Vertex vertex : graph.getVertices()){ // O(V)
                rec(vertex);
            }
            return maxPath;
        }

        public Path pathPacker(List<Graph.Vertex> list){
            Path path = new Path(list.get(0));
            for (int i = 1; i < list.size(); i++){
                path = new Path(path, graph, list.get(i));
            }
            return path;
        }

        public void rec(Graph.Vertex vertex){ // O(C * V) C - кол-во соединений от вершины
            if(!currentPathL.contains(vertex)){
                currentPathL.add(vertex);
                if(maxPath.getLength() + 1 < currentPathL.size()) {
                    maxPath = pathPacker(currentPathL);
                }
                for (Graph.Vertex next : graph.getNeighbors(vertex)){
                    if(!currentPathL.contains(next)) {
                        rec(next);
                    }
                }
                currentPathL.remove(vertex);
            }
        }
    }

    // T = O(V^2) V -кол-во вершин
    // R = O(V^2) максимальное сначение в худшем случае

    public static Path longestSimplePath(Graph graph) {
        if(graph.getVertices().isEmpty() || graph.getEdges().isEmpty()) return new Path();
        Helper solv = new Helper(graph);
        return solv.search();
    }





    /**
     * Балда
     * Сложная
     *
     * Задача хоть и не использует граф напрямую, но решение базируется на тех же алгоритмах -
     * поэтому задача присутствует в этом разделе
     *
     * В файле с именем inputName задана матрица из букв в следующем формате
     * (отдельные буквы в ряду разделены пробелами):
     *
     * И Т Ы Н
     * К Р А Н
     * А К В А
     *
     * В аргументе words содержится множество слов для поиска, например,
     * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
     *
     * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
     * и вернуть множество найденных слов. В данном случае:
     * ТРАВА, КРАН, АКВА, НАРТЫ
     *
     * И т Ы Н     И т ы Н
     * К р а Н     К р а н
     * А К в а     А К В А
     *
     * Все слова и буквы -- русские или английские, прописные.
     * В файле буквы разделены пробелами, строки -- переносами строк.
     * Остальные символы ни в файле, ни в словах не допускаются.
     */
    static public Set<String> baldaSearcher(String inputName, Set<String> words) {
        throw new NotImplementedError();
    }
}
