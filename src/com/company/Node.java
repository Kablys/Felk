package com.company;

import java.util.ArrayList;
import java.util.List;

public class Node{
    private Lexer.Token token;
    Node parent = null;
    private List<Node> children = new ArrayList<Node>();

    public Node(Lexer.Token token) {
        this.token = token;
    }

    public void parent(Node parent){this.parent = parent;}

    public void addChildren(Node child) {
        this.children.add(child);
        child.parent(this);
    }

    public void toXml(int indent){
        for(int i = 0; i < indent; i++){
            System.out.println("\t");
        }
        System.out.println(token.toString2());
        for(Node i : children){
            i.toXml(indent + 1);
        }
    }

}
