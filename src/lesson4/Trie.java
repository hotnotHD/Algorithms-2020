package lesson4;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Префиксное дерево для строк
 */
public class Trie extends AbstractSet<String> implements Set<String> {

    private static class Node {
        Map<Character, Node> children = new LinkedHashMap<>();
    }

    private Node root = new Node();
    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root.children.clear();
        size = 0;
    }

    private String withZero(String initial) {
        return initial + (char) 0;
    }

    @Nullable
    private Node findNode(String element) {
        Node current = root;
        for (char character : element.toCharArray()) {
            if (current == null) return null;
            current = current.children.get(character);
        }
        return current;
    }

    @Override
    public boolean contains(Object o) {
        String element = (String) o;
        return findNode(withZero(element)) != null;
    }

    @Override
    public boolean add(String element) {
        Node current = root;
        boolean modified = false;
        for (char character : withZero(element).toCharArray()) {
            Node child = current.children.get(character);
            if (child != null) {
                current = child;
            } else {
                modified = true;
                Node newChild = new Node();
                current.children.put(character, newChild);
                current = newChild;
            }
        }
        if (modified) {
            size++;
        }
        return modified;
    }

    @Override
    public boolean remove(Object o) {
        String element = (String) o;
        Node current = findNode(element); // Т = O(N)
        if (current == null) return false;
        if (current.children.remove((char) 0) != null) {
            size--;
            return true;
        }
        return false;
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: {@link Iterator} (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    @NotNull
    @Override
    public Iterator<String> iterator() {
        return new TrieIterator();
    }

    public class TrieIterator implements Iterator<String> {
        Object currentN;
        Queue<String> queue = new LinkedList<>();
        String fulz = "";

        private TrieIterator() {
            allSee(root);
        }

        private void allSee(Node cur){
            Map<Character, Node> x;
            x = cur.children;
            Character key;
            for (Map.Entry<Character, Node> entry : x.entrySet()){
                key = entry.getKey();
                if (key == 0) queue.add(fulz);
                fulz += key;
                allSee(x.get(key));
            }
            if (fulz.length() >= 1) fulz = fulz.substring(0, fulz.length() - 1 );
        }

        // Т = O(const)
        // R = O(1)
        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        // Т = O(const)
        // R = O(1)
        @Override
        public String next() {
            if (queue.peek() == null) throw new IllegalStateException();
            currentN = queue.peek();
            return queue.poll();
        }

        // Т = O(N)
        // R = O(1)
        @Override
        public void remove() {
            if ( currentN == null || !Trie.this.remove(currentN) ) throw new IllegalStateException();
        }
    }

}