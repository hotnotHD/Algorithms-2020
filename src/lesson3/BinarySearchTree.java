package lesson3;

import java.util.*;
import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// attention: Comparable is supported but Comparator is not
public class BinarySearchTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        final T value;
        Node<T> left = null;
        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        }
        else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        }
        else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    private Node<T> findParent(Node<T> child) {
        if (root == null) return null;
        return findParent(root, child.value);
    }

    private Node<T> findParent(Node<T> start, T child) {
        int comparison = child.compareTo(start.value);
        if (comparison == 0) {
            return null;
        }
        else if (comparison < 0) {
            if (start.left.value == child) return start;
            return findParent(start.left, child);
        }
        else {
            if (start.right.value == child) return start;
            return findParent(start.right, child);
        }
    }

    private Node<T> maxRight (Node<T> cur){
        if (cur.right == null) return cur;
        return maxRight(cur.right);
    }


    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: {@link Set#add(Object)} (Ctrl+Click по add)
     *
     * Пример
     */
    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        }
        else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        }
        else {
            assert closest.right == null;
            closest.right = newNode;
        }
        size++;
        return true;
        
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: {@link Set#remove(Object)} (Ctrl+Click по remove)
     *
     * Средняя
     */

    // T = O(N) - в худшем случае
    // Т = O(log(N)) - в лучшем случае
    // R = O(1)

    @Override
    public boolean remove(Object o){
        T t = (T)o;
        Node<T> toRemove = find(t);
        if (toRemove == null || t.compareTo(toRemove.value) != 0) return false;
        return remove(toRemove);
    }

    private boolean remove(Node<T> o) {
        Node<T> helper;
        if (o.left == null || o.right == null) {
            if (o.right == null && o.left == null) {
                helper = null;
            }else helper = Objects.requireNonNullElseGet(o.right, () -> o.left);
        }else {
            Node<T> newN = maxRight(o.left);
            newN.right = o.right;
            helper = newN;
            if (newN.value != o.left.value) {
                Objects.requireNonNull(findParent(newN)).right = newN.left;
                newN.left = o.left;
            }
        }

        Node<T> parent = findParent(o);
        if ( parent != null) {
            boolean isLeft = parent.left != null && parent.left.value == o.value;
            changeGens(parent, helper, isLeft);
        }else {
            root = helper;
        }
        size--;
        return true;
    }

    private void changeGens (Node<T> parent, Node<T> to, boolean leftS) {
        if (parent != null) {
            if (leftS) parent.left = to;
            else parent.right = to;
        }
    }

    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinarySearchTreeIterator();
    }

    public class BinarySearchTreeIterator implements Iterator<T> {

        T maxValue;
        Node<T> previousN;
        Node<T> currentN;
        boolean first = true;
        boolean end = false;
        boolean doubleTrouble = false;

        private BinarySearchTreeIterator() {
            // Добавьте сюда инициализацию, если она необходима.
            if (root != null) {
                currentN = root;
                maxValue = BinarySearchTree.this.maxRight(root).value;
            }
        }

        private void up(){
            currentN = BinarySearchTree.this.findParent(currentN);
            if (previousN.value.compareTo(currentN.value) > 0){
                up();
            }
        }

        private Node<T> maxLeft (Node<T> cur){
            if (cur.left == null) return null;
            Node<T> x = cur.left;
            while (x != null){
                cur = x;
                x = x.left;
            }
            return cur;
        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: {@link Iterator#hasNext()} (Ctrl+Click по hasNext)
         *
         * Средняя
         */
        // Т = O(const)
        // R = O(1)
        @Override
        public boolean hasNext() {
            return ((root != null) && (currentN.value.compareTo(maxValue) != 0));
        }

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: {@link Iterator#next()} (Ctrl+Click по next)
         *
         * Средняя
         */
        // T = O(N) - в худшем случае
        // Т = O(log(N)) - в лучшем случае
        // R = O(const)
        @Override
        public T next() {
            if (first){
                if (root.left != null) currentN = maxLeft(root);
                first = false;
                return currentN.value;
            }
            if (end){
                throw new IllegalStateException();
            }
            if (currentN.right == null && currentN.left == null){
                previousN = currentN;
                up();
            }
            if (currentN.right != null) {
                Node<T> x;
                currentN = currentN.right;
                x = maxLeft(currentN);
                if (x != null) {
                    currentN = x;
                }
            }else {
                up();
            }
            if (currentN.value.compareTo(maxValue) == 0) end = true;
            doubleTrouble = false;
            return currentN.value;
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: {@link Iterator#remove()} (Ctrl+Click по remove)
         *
         * Сложная
         */
        // T = O(N) - в худшем случае
        // Т = O(log(N)) - в лучшем случае
        // R = O(1)
        @Override
        public void remove() {
           if ( doubleTrouble || first || !BinarySearchTree.this.remove(currentN)) throw new IllegalStateException();
           doubleTrouble = true;
        }
    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: {@link SortedSet#subSet(Object, Object)} (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: {@link SortedSet#headSet(Object)} (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: {@link SortedSet#tailSet(Object)} (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

}