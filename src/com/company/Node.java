package com.company;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static java.nio.file.StandardOpenOption.*;

public class Node{
    String output = "";
    private Lexer.Token token;
    Node parent = null;
    private List<Node> children = new ArrayList<>();

    public Node(Lexer.Token token) {
        this.token = token;
    }

    public void parent(Node parent){this.parent = parent;}

    public void addChildren(Node child) {
        this.children.add(child);
        child.parent(this);
    }

    public String toXml(int indent){
        for(int i = 0; i < indent; i++){
            output += ("\t");
        }
        if(token.toString2().equals("IDENTIFIER")) {
            output += ("<" + token.toXmlString()+ "/>");
            System.out.println("in identifier");
            for (Node i : children) {
                output += i.toXml(indent + 1);
            }
            return output;
        }else{
            output += ("<" + token.toString2() + ">");
            for (Node i : children) {
                output += i.toXml(indent + 1);
            }
        }
        for(int i = 0; i < indent; i++){
            output += ("\t");
        }
        output += ("</" + token.toString2() + ">");
        return output;
    }
//        for(int i = 0; i < indent; i++){
//            System.out.print("\t");
//        }
//        System.out.println("<"+token.toString2()+"> " + token.toString2());
//        for(Node i : children){
//            i.toXml(indent + 1);
//        }
//        for(int i = 0; i < indent; i++){
//            System.out.print("\t");
//        }
//        System.out.println("</"+token.toString2()+">");

    public void toXml2(String output) {
        Path p = Paths.get("./logfile.xml");
        byte[] data = output.getBytes();
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, TRUNCATE_EXISTING, CREATE))) {
            out.write(data, 0, data.length);
            out.close();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

}
