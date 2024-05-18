import java.util.*;
/*
 * A class representing the huffman coding tree algorithm.
 *
 * @author Binal Dhaliwal and Anagha Krishna
 */
public class CodingTree {
    Map<Character, String> codes;
    List<Byte> bits;
    private final String message;
    private final Map<Character, Integer> charCount;
    private final PriorityQueue<Node> huffmanTree;


    private static class Node implements Comparable {
        char character;
        int appearance;
        Node right, left;

        Node(Character character, Integer appearance){
            this.character = character;
            this.appearance = appearance;

            left = null;
            right = null;
        }


        Node(Integer appearance, Node left, Node right){
            this.appearance = appearance;
            this.left = left;
            this.right = right;
        }
        public char getCharacter() {
            return this.character;
        }
        public int getAppearance() {
            return this.appearance;
        }
        public int compareTo(Object theO) {
            Node n = (Node) theO;
            return Integer.compare(this.appearance, n.getAppearance());
        }
    }
    CodingTree(String message){
        this.message = message;
        charCount = new TreeMap<>();
        codes = new TreeMap<>();
        bits = new ArrayList<>();
        huffmanTree = new PriorityQueue<>();

        frequencyCharCount(this.message);
        createTree();
        merge();
        encodeMessage();
    }
    private void frequencyCharCount(String message) {
        for (char c: message.toCharArray()) {
            if (charCount.containsKey(c)) {
                charCount.put(c, charCount.get(c) + 1);
            } else {
                charCount.put(c, 1);
            }
        }
    }

    private void createTree() {
        for (Map.Entry<Character, Integer> entry: charCount.entrySet()) {
            Node node = new Node(entry.getKey(), entry.getValue());
            huffmanTree.add(node);
        }
    }

    private void merge(){
        while (huffmanTree.size() > 1) {
            Node left = huffmanTree.poll();
            Node right = huffmanTree.poll();
            Node tree = new Node(left.appearance + right.appearance, left, right);
            List<Node> temp = new ArrayList<>();
            boolean b = false;
            while (!huffmanTree.isEmpty()) {
                Node node = huffmanTree.poll();
                if (node.compareTo(tree) > 0 && !b) {
                    temp.add(tree);
                    b = true;
                }
                temp.add(node);
            }
            if (!b) {
                temp.add(tree);
            }
            huffmanTree.addAll(temp);
        }
        if (!huffmanTree.isEmpty()) {
            Node root = huffmanTree.poll();
            countBinary(root, "");
        }
    }

    private void countBinary( Node node, String binary){
        if (node != null) {
            if (node.left != null) {
                countBinary(node.left, binary + "0");
            }
            if (node.right != null) {
                countBinary(node.right, binary + "1");
            }
            if (node.left == null && node.right == null) {
                codes.put(node.getCharacter(), binary);
            }
        }
    }

    private void encodeMessage(){
        StringBuilder message = new StringBuilder();
        for (char character : this.message.toCharArray()) {
            String code = codes.get(character);
            message.append(code);
        }
        int index = 0;
        while (index < message.length()) {
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < 8 && index < message.length(); i++) {
                string.append(message.charAt(index));
                index++;
            }
            while (string.length() < 8) {
                string.append('0');
            }
            bits.add((byte) Integer.parseInt(string.toString(), 2));
        }
    }
    String decode(List<Byte> bits, Map<Character, String> codes, int stopsize) {
        StringBuilder message = new StringBuilder();
        StringBuilder string = new StringBuilder();
        for (byte b : bits) {
            string.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace
                    (' ', '0'));
        }
        int index = 0;
        Map<String, Character> codeLookup = new HashMap<>();
        for (Map.Entry<Character, String> entry : codes.entrySet()) {
            codeLookup.put(entry.getValue(), entry.getKey());
        }
        while (message.length() < stopsize && index < string.length()) {
            StringBuilder code = new StringBuilder();
            while (index < string.length()) {
                code.append(string.charAt(index));
                if (codeLookup.containsKey(code.toString())) {
                    message.append(codeLookup.get(code.toString()));
                    break;
                }
                index++;
            }
            index++;
        }
        return message.toString();
    }
}

