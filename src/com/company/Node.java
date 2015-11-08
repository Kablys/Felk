package com.company;

import java.util.List;

public class Node{
    private Lexer.Token token;
    private List<Node> children;

    public Node(Lexer.Token token) {
        this.token = token;
    }

    public void addChildren(Node children) {
        this.children.add(children);
    }


}
