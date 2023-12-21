package nl.node;

import java.util.ArrayList;
import java.util.List;

public class UndefinedNode extends Node {
    private Node node;

    public UndefinedNode(Lang language, Node node) {
        super(language);
        this.node = node;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        if (node instanceof BinaryExpression) {
            return doBinaryExpression(frame);
        } else {
            return node;
        }
    }

    protected Object doBinaryExpression(VirtualFrame frame) {
        Class<? extends BinaryExpression> cls = (Class<? extends BinaryExpression>) node.getClass();
        List<Node> collect = collect(node, cls);
        List<Node>[] pair = split(collect);
        List<Node> hasUndefined = pair[0];
        List<Node> notHas = pair[1];
        if (notHas.size() < 2) {
            return node;
        } else {
            Node res = wrapper(lang, frame, merge(notHas, cls).execute(frame));
            Node und = merge(hasUndefined, cls);
            return BinaryExpression.create(lang, und, res, cls);
        }
    }


    private Node merge(List<Node> nodes, Class<? extends BinaryExpression> cls) {
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        Node first = nodes.get(0);
        for (int i = 1; i < nodes.size(); i++) {
            Node cur = nodes.get(i);
            first = BinaryExpression.create(lang, first, cur, cls);
        }
        return first;
    }

    private List<Node>[] split(List<Node> nodes) {
        List<Node>[] pair = new List[2];
        List<Node> hasUndefined = new ArrayList<>();
        List<Node> notHas = new ArrayList<>();
        for (Node node : nodes) {
            if (node.hasUndefined()) {
                hasUndefined.add(node);
            } else {
                notHas.add(node);
            }
        }
        pair[0] = hasUndefined;
        pair[1] = notHas;
        return pair;
    }

    private List<Node> collect(Node node, Class<?> cls) {
        List<Node> res = new ArrayList<>();
        if (cls.isInstance(node)) {
            for (Node child : node.children()) {
                res.addAll(collect(child, cls));
            }
        } else {
            res.add(node);
        }
        return res;
    }

    @Override
    protected List<Node> children() {
        return List.of(node);
    }

    @Override
    public String toString() {
        return String.valueOf(node);
    }
}
