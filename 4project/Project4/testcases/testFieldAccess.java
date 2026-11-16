class testFieldAccess {
    public static void main(String[] args) {
        Node n;
        int v;

        n = new Node();
        v = n.value;
        v = n.next.value;
        v = this.count;
    }
}

class Node {
    int value;
    Node next;
}