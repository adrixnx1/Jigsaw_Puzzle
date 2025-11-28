package com.finalproject.jigsawproject;

public class Edge {
    private EdgeType type;

    public Edge(EdgeType type){
        this.type = type;
    }
    public EdgeType getType() {
        return type;
    }

    public static boolean fitsWith(Edge edgeOne, Edge edgeTwo) {
        EdgeType a = edgeOne.getType();
        EdgeType b = edgeTwo.getType();

        // Flat edges match only with flat edges (puzzle border)
        if (a == EdgeType.FLAT && b == EdgeType.FLAT) {
            return true;
        }

        // Tab must match blank, blank must match tab
        if (a == EdgeType.TAB && b == EdgeType.BLANK) {
            return true;
        }
        if (a == EdgeType.BLANK && b == EdgeType.TAB) {
            return true;
        }

        // Anything else does NOT fit
        return false;
    }
}
