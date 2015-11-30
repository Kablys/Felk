package com.company;

import javax.management.NotificationEmitter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    String output = "";
    static Node ast;
    static Integer nTok = 0;
    static List<Lexer.Token> tokens;
    boolean changed = false;
    //static Lexeme[] arrayOfTypes = {com.company.Lexeme.INT, com.company.Lexeme.FLOAT, com.company.Lexeme.STRING, com.company.Lexeme.CHAR, com.company.Lexeme.BOOL, com.company.Lexeme.VOID};

    List<Lexeme> arrayOfTypes = Arrays.asList(com.company.Lexeme.INT, com.company.Lexeme.FLOAT, com.company.Lexeme.STRING,
            com.company.Lexeme.CHAR, com.company.Lexeme.BOOL, com.company.Lexeme.VOID);
    List<Lexeme> relatOp = Arrays.asList(com.company.Lexeme.EQUAL, com.company.Lexeme.LESS, com.company.Lexeme.MORE,
            com.company.Lexeme.NOTEQUAL, com.company.Lexeme.MOREEQUAL, com.company.Lexeme.LESSEQUAL);
    List<Lexeme> addOp = Arrays.asList(com.company.Lexeme.PLUS, com.company.Lexeme.MINUS, com.company.Lexeme.OROP);
    List<Lexeme> mulOp = Arrays.asList(com.company.Lexeme.MULTIPLY, com.company.Lexeme.DIVISION, com.company.Lexeme.MOD,
            com.company.Lexeme.ANDOP);

    //myList = {Lexeme.INT, Lexeme.FLOAT, Lexeme.STRING, Lexeme.CHAR, Lexeme.BOOL, Lexeme.VOID};
    public Parser(List<Lexer.Token> programsTokens) {
        tokens = programsTokens;
        int i = 0;
        for(Lexer.Token t : tokens) {
            System.out.println(i++ +" "+t);
        }
        ast = programParse(0);
        System.out.println("Baigiau Parseri");
        output = ast.toXml(0);
        ast.toXml2(output);
    }

    public static Lexer.Token getNextToken () {

        if (nTok < tokens.size()) {
            Lexer.Token t = tokens.get(nTok);
            nTok++;
            return t;
        } else {
            nTok = -1;
            return tokens.get(tokens.size() - 1);
        }
    }


    public Node programParse (Integer index){
        Lexer.Token rootToken = new Lexer.Token(Lexeme.PROGRAM, "<program>");
        Node node = new Node (rootToken);
        if (arrayOfTypes.contains(tokens.get(index).t)) {
            node.addChildren(functionParse(index + 1));
            //index = nTok+1;
            //System.out.println(index);
            //Node functionblock = (blockParse(index));
            //node.addChildren(functionblock);
            index = nTok;
            /*if(tokens.get(index+1) != null){
                node.addChildren(blockParse(index+1));
            }
            index = nTok;
            System.out.println(index);
            if(tokens.get(index).t == tokens.size()) {
                if (tokens.get(index + 1).t != Lexeme.MAIN) {
                    System.out.println("repeat");
                    node.addChildren(programParse(index + 1));
                }
            else {
                node.addChildren(programParse(index + 1));
            }
            }*/
        } else if ((tokens.get(index).t) == Lexeme.MAIN) {
            System.out.println("here");
            node.addChildren(mainParse(index));
        } else {
            return node;
        }
        return node;
    }

    public Node functionParse (Integer index){
        Node node = new Node (new Lexer.Token(Lexeme.FUNCTION, "<function>"));
        node.addChildren(new Node (tokens.get(index - 1)));
        if (tokens.get(index).t == Lexeme.IDENTIFIER){
            node.addChildren(new Node (tokens.get(index)));
            node.addChildren(paramParse(index + 1));
            index = nTok;
            node.addChildren(blockParse(index));
            index = nTok;
        }
        nTok = index;
        return node;
    }

    public Node paramParse (Integer index){
        Node node = new Node (new Lexer.Token(Lexeme.PARAMETER, "test"));
        if (tokens.get(index).t == Lexeme.LPAREN) {
            index++;
            while (tokens.get(index).t != Lexeme.RPAREN) {
                node.addChildren(typeParse(index));
                index= index+2;
                if(tokens.get(index).t == Lexeme.COMMA){
                    index++;
                }
            }
        }
        nTok= index;
        return node;
    }

    public Node mainParse (int index){
        Node node = new Node (tokens.get(index));
        node.addChildren(blockParse(index+2));
        return node;
    }

    public Node blockParse (int index){
        Node node = new Node (new Lexer.Token(Lexeme.BLOCK, "Block"));
        index= index + 1;
        while(tokens.get(index).t != Lexeme.RBRACKET) {
            if (arrayOfTypes.contains(tokens.get(index).t)) {
                node.addChildren(typeParse(index));
                index = nTok;
            }
            else if (tokens.get(index).t == Lexeme.FOR){
                node.addChildren(forBlock(index));
            }else if(tokens.get(index+1).t == Lexeme.ASSIG){
                Node assignNode = new Node(tokens.get(index+1));
                node.addChildren(assignNode);
                assignNode.addChildren(new Node(tokens.get(index)));
                assignNode.addChildren(expression(index+2));
            }else if (tokens.get(index).t == Lexeme.WHILE){
                Node whileNode = new Node(tokens.get(index));
                node.addChildren(whileNode);
                whileNode.addChildren(expression(index+1));
                index = nTok;
                whileNode.addChildren(blockParse(index));
                index = nTok;
            }else if(tokens.get(index).t == Lexeme.IF) {
                Node ifNode = new Node(tokens.get(index));
                node.addChildren(ifNode);
                ifNode.addChildren(expression(index + 1));
                index = nTok;
                ifNode.addChildren(blockParse(index));
                index = nTok;
                if(tokens.get(index+1).t == Lexeme.ELSE){
                    //System.out.println("else");
                    Node elseNode = new Node(tokens.get(index+1));
                    ifNode.addChildren(elseNode);
                    elseNode.addChildren(blockParse(index+2));
                    index = nTok;
                }
            }
            index++;
        }
        nTok = index;
        //else{}
        return node;
    }

    public Node ifBlock(int index){
        Node node = new Node(tokens.get(index));
        if(tokens.get(index+1).t == Lexeme.LPAREN){
            Node expressionNode = new Node(tokens.get(index+1));
            node.addChildren(expressionNode);
                expressionNode.addChildren(expression(index));
            index = nTok;
            if(tokens.get(index).t == Lexeme.LPAREN){
                node.addChildren(blockParse(index));
            }

        }
        return node;
    }

    public Node forToNode(int index){
        Node node = new Node(tokens.get(index+1));
        index++;
        node.addChildren(new Node(tokens.get(index-1)));
        node.addChildren(new Node(tokens.get(index+1)));
        nTok = index+1;
        return node;
    }


    public Node forBlock(int index){
        Node node = new Node(tokens.get(index));
        if(tokens.get(index+2).t == Lexeme.ASSIG){
            index = index+2;
            Node forAssigNode = new Node(tokens.get(index));
            node.addChildren(forAssigNode);
            forAssigNode.addChildren(new Node(tokens.get(index-1)));
            forAssigNode.addChildren(forToNode(index+1));
            index = nTok+1;
            node.addChildren(blockParse(index));
            index = nTok+1;
        }
        nTok = index;
        return node;
    }



    public Node typeParse (int index){
        if (tokens.get(index+2).t == Lexeme.ASSIG){
            index = index+2;
            Node node = new Node(tokens.get(index));
            Node nodeInt = new Node(tokens.get(index - 2));
            node.addChildren(nodeInt);
            nodeInt.addChildren(new Node(tokens.get(index - 1)));//index 42 identifier
            node.addChildren(expression(index+1));
            return node;
        }
        else{
            Node node = new Node(tokens.get(index));
            index = index + 1;
            node.addChildren(new Node(tokens.get(index)));
            nTok = index;
            return node;
        }
    }


    public Node expression (int index) {
        Node node = new Node(new Lexer.Token(Lexeme.EXPRESSION, "Expression"));
        while((tokens.get(index).t != Lexeme.SEMICOLON)||(tokens.get(index).t != Lexeme.LBRACKET)|| (tokens.get(index).t != Lexeme.RBRACKET)){
            if(relatOp.contains(tokens.get(index+1).t)){
                node = new Node(tokens.get(index+1));
                node.addChildren(new Node(tokens.get(index)));
                node.addChildren((SimpleExpression1(index+2)));
                break;
            }else if(relatOp.contains(tokens.get(index).t)){
                node = new Node(tokens.get(index));
                node.addChildren(SimpleExpression1(index+1));
                break;
            }else if((tokens.get(index).t == Lexeme.NUMBER || tokens.get(index).t == Lexeme.FLOAT || tokens.get(index).t == Lexeme.STRING || tokens.get(index).t == Lexeme.IDENTIFIER )&& tokens.get(index+1).t == Lexeme.SEMICOLON){
                System.out.println("SEMICOLON");
                node.addChildren(new Node(tokens.get(index)));
                index++;
                nTok = index;
                break;
            }
            else{
                node.addChildren(SimpleExpression1(index));
                break;
            }

        }
        return node;
    }

    public Node SimpleExpression1(int index){
//        Node node = new Node(new Lexer.Token(Lexeme.EXPRESSION, "Expression"));
        Node node = new Node(tokens.get(index));
//        while(tokens.get(index+1).t != Lexeme.SEMICOLON){
            if (relatOp.contains(tokens.get(index).t)){
                index++;
                node.addChildren(new Node(tokens.get(index)));
            }else if(relatOp.contains(tokens.get(index+1).t)) {
                index++;
                node.addChildren(expression(index));
            }else if (addOp.contains(tokens.get(index + 1).t)) {
                if(tokens.get(index).t == Lexeme.RPAREN){
                    node = new Node(tokens.get(index));
                    Node newNode = new Node(tokens.get(index+1));
                    node.addChildren(newNode);
                    newNode.addChildren(addingNode(index+2));
                }else {
                    node = new Node(tokens.get(index + 1));
                    node.addChildren(new Node(tokens.get(index)));
                    node.addChildren(addingNode(index + 2));
//                break;
                }
            } else if (addOp.contains(tokens.get(index).t)) {
                node = new Node(tokens.get(index));
                node.addChildren(addingNode(index + 1));
//                break;
            }else if(mulOp.contains(tokens.get(index+1).t)){
                if(tokens.get(index).t == Lexeme.RPAREN){
                    node = new Node(tokens.get(index));
                    Node newNode = new Node(tokens.get(index+1));
                    node.addChildren(newNode);
                    newNode.addChildren(addingNode(index+2));
                }else {
                    index++;
                    node = new Node(tokens.get(index));
                    node.addChildren(addingNode(index - 1));
                }
            }
            else if (tokens.get(index+1).t == Lexeme.LBRACKET){
                //node.addChildren(new Node(tokens.get(index)));
                nTok = index;
            }
            else {
                node.addChildren(addingNode(index));
//                break;
            }
//        }
        return node;
    }

    public Node addingNode(int index){
        Node node = new Node(tokens.get(index));
        if(mulOp.contains(tokens.get(index+1).t)){
            if(tokens.get(index).t == Lexeme.RPAREN) {
                node = new Node(tokens.get(index));
                Node newNode = new Node(tokens.get(index + 1));
                node.addChildren(newNode);
                newNode.addChildren(mulNode(index + 2));
            }else {
                index++;
                node = new Node(tokens.get(index));
                node.addChildren(new Node(tokens.get(index - 1)));
                node.addChildren(mulNode(index + 1));
            }
        }else if(mulOp.contains(tokens.get(index).t)){
            node = new Node(tokens.get(index));
            node.addChildren(mulNode(index+1));
        }
        else if(addOp.contains(tokens.get(index).t)){
            index++;
            node.addChildren(new Node(tokens.get(index)));
        }
        else if(addOp.contains(tokens.get(index+1).t)){
            index++;
            node.addChildren(expression(index));
//            node.addChildren(SimpleExpression1(index));
        }
        else if (tokens.get(index+1).t == Lexeme.LBRACKET){
            //node.addChildren(new Node(tokens.get(index)));
            nTok = index;
        }
        else if(tokens.get(index+1).t != Lexeme.SEMICOLON){
            node.addChildren(expression(index+1));
//            node.addChildren(SimpleExpression1(index+1));
            nTok = index;
        }
        else if(tokens.get(index+1).t == Lexeme.SEMICOLON){
            nTok = index;
        }
        else{
            nTok = index+1;
        }
        return node;
    }

    public Node mulNode(int index){
        Node node = new Node(tokens.get(index));
        if(tokens.get(index+1).t != Lexeme.SEMICOLON){
            node.addChildren(expression(index+1));
//            node.addChildren(SimpleExpression1(index+1));
        }
        nTok = index;
        return node;
    }





/*

    public Node MultiplicationOperator(int index, Node nodePr){
        Node node = new Node(tokens.get(index));
        node.addChildren(nodePr);
        if(tokens.get(index).t == Lexeme.LPAREN) {
            node.addChildren(Factor(index+1));
        }else{
            node.addChildren(Factor(index+1));
        }
        return node;
    }

    public Node AddingOperator(int index, Node nodePr){
        Node node = new Node(tokens.get(index));
        node.addChildren(nodePr);
        if(tokens.get(index+1).t == Lexeme.LPAREN || tokens.get(index+1).t == Lexeme.RPAREN){
            node.addChildren(Term(index+2));
        }else {
            node.addChildren(Term(index + 1));
        }
        return node;
    }

    public Node RelationOperator(int index, Node nodePr){
        Node node = new Node(tokens.get(index));
        node.addChildren(nodePr);
        if(tokens.get(index+1).t == Lexeme.LPAREN || tokens.get(index+1).t == Lexeme.RPAREN){
            node.addChildren(SimpleExpression(index+2,node));
        }else{
            node.addChildren(SimpleExpression(index+1,node));
        }
        return node;
    }

    public Node ExpressionParse (int index) {
        if(tokens.get(index).t == Lexeme.LPAREN){
            index++;
            Node nodeSave = new Node(tokens.get(index));
            Node node = new Node(new Lexer.Token(Lexeme.EXPRESSION,"Expression"));
            while(tokens.get(index).t != Lexeme.SEMICOLON){
                if(relatOp.contains(tokens.get(index+1))) {
                    node.addChildren(RelationOperator(index+1, nodeSave));
                }
                else{
                    if(tokens.get(index+1).t == Lexeme.RPAREN){
                        node.addChildren(SimpleExpression(index,nodeSave));
                    }else {
                        node.addChildren(SimpleExpression(index));
                    }
                }
                index = nTok;
                nodeSave = node;
            }
            return node;
        }else {

            Node nodeSave = new Node(tokens.get(index));
            Node node = new Node(new Lexer.Token(Lexeme.EXPRESSION, "Expression"));
            while (tokens.get(index).t != Lexeme.SEMICOLON) {
                if (relatOp.contains(tokens.get(index + 1).t)) {
                    node.addChildren(RelationOperator(index+1, nodeSave));
                }else if(changed == true){
                    node.addChildren(SimpleExpression(index,nodeSave));
                }
                else if(changed == false){
                    node.addChildren(SimpleExpression(index,nodeSave));
                    changed = true;
                }
                index = nTok;
                nodeSave = node;
            }
            return node;
        }
    }

    public Node SimpleExpression (int index, Node nodePr){
//        Node node = new Node(new Lexer.Token(Lexeme.SIMPLEEXPRESSION, "SimpleExpression"));
//        Node nodeSave = new Node(tokens.get(index));
        Node nodeSave = nodePr;
        Node node = nodePr;
        index++;
        if(addOp.contains(tokens.get(index).t)){
            node.addChildren(AddingOperator(index, nodeSave));
        }else{
            node.addChildren(Term(index-1));
        }
        return node;
    }
    public Node SimpleExpression (int index){
        Node node = new Node(new Lexer.Token(Lexeme.SIMPLEEXPRESSION, "SimpleExpression"));
        Node nodeSave = new Node(tokens.get(index));
//        Node nodeSave = nodePr;
        index++;
        if(addOp.contains(tokens.get(index).t)){
            node.addChildren(AddingOperator(index, nodeSave));
        }else{
            node.addChildren(Term(index-1));
        }
        return node;
    }

    public Node Term(int index){
        Node node = new Node(new Lexer.Token(Lexeme.TERM, "Term"));
        Node nodeSave = new Node(tokens.get(index));
        index++;
        if(tokens.get(index).t == Lexeme.LPAREN || tokens.get(index).t == Lexeme.RPAREN) {
            index++;
            if (mulOp.contains(tokens.get(index).t)) {
                node.addChildren(MultiplicationOperator(index, nodeSave));
            } else {
                node.addChildren(Factor(index - 2));
            }
            return node;
        }else {
            if (mulOp.contains(tokens.get(index).t)) {
                node.addChildren(MultiplicationOperator(index, nodeSave));
            } else {
                if(tokens.get(index).t == Lexeme.SEMICOLON) {
                    node.addChildren(Factor(index - 1));
                }else {
                    node.addChildren(Factor(index - 2));
                }
            }
            return node;
        }
    }

    public Node Factor(int index){
        Node node = new Node(new Lexer.Token(Lexeme.FACTOR, "Factor"));
        if(tokens.get(index).t == Lexeme.LPAREN){
            node.addChildren(ExpressionParse(index));
        }
        else if(tokens.get(index).t == Lexeme.IDENTIFIER){
            if(tokens.get(index+1).t == Lexeme.LPAREN){
                node.addChildren(functionCall(index));
            }
            else{
                node.addChildren(new Node(tokens.get(index)));
                if(tokens.get(index+1).t == Lexeme.LPAREN || tokens.get(index+1).t == Lexeme.RPAREN){
                    nTok = index + 1;
                }else {
                    if(tokens.get(index + 1).t == Lexeme.SEMICOLON) {
                        nTok = index + 1;
                    }
                    else
                        nTok = index;
                }
            }
        }else if(tokens.get(index).t == Lexeme.STRING){
            node.addChildren(new Node(tokens.get(index)));
            nTok = index+1;
        }else if(tokens.get(index).t == Lexeme.NUMBER){
            node.addChildren(new Node(tokens.get(index)));
            if(tokens.get(index+1).t == Lexeme.LPAREN || tokens.get(index+1).t == Lexeme.RPAREN){
                nTok = index + 1;
            }else {
                nTok = index ;
            }

        }else if(tokens.get(index).t == Lexeme.MINUS){
            Node nodeMinus = new Node(tokens.get(index));
            if(tokens.get(index+1).t == Lexeme.NUMBER || tokens.get(index+1).t == Lexeme.FLOAT) {
                if(tokens.get(index+2).t == Lexeme.RPAREN){
                    nodeMinus.addChildren(new Node(tokens.get(index + 1)));
                    node.addChildren(nodeMinus);
                }else {
                    nodeMinus.addChildren(new Node(tokens.get(index + 1)));
                    node.addChildren(nodeMinus);
                    if(tokens.get(index+2).t == Lexeme.LPAREN || tokens.get(index+2).t == Lexeme.RPAREN){
                        nTok = index + 2;
                    }else {
                        nTok = index + 2;
                    }
                }
            }// Add throw
            nTok = index+1;
        }else if(tokens.get(index).t == Lexeme.FLOAT){
            node.addChildren(new Node(tokens.get(index)));
            nTok = index+1;
        }else if(tokens.get(index).t == Lexeme.BOOL){
            node.addChildren(new Node(tokens.get(index)));
            nTok = index+1;
        }
        else if (tokens.get(index).t == Lexeme.SEMICOLON){
            nTok = index+1;
        }else if(tokens.get(index).t == Lexeme.RPAREN){
            node.addChildren(new Node(tokens.get(index-1)));
            nTok = index;
        }
        //else{
            //node.addChildren(ExpressionParse(index));
        //}
        return node;
    }
    */

    public Node functionCall(int index){
        Node node = new Node(tokens.get(index));

        return node;
    }


}