package org.tmu.subenum;

//import org.tmu.core.Util;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 6/5/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubGraphStructure {
    public static boolean beFast = false;
    public int[] nodes;

    // ADDED
    public byte[] edges;
    
    //REMOVED/CHANGED
//    public boolean[] edges;

    public SubGraphStructure(int size) {
        nodes = new int[size];
//        edges = new boolean[size * size];
        edges = new byte[size * size];

    }

//    public SubGraphStructure(boolean[] adjacency) {
//    	if (Math.sqrt(adjacency.length) != (int) Math.sqrt(adjacency.length))
//    		throw new IllegalArgumentException("Adjacency size must be complete square integer!");
//    	
//    	int size = (int) Math.sqrt(adjacency.length);
//    	nodes = new int[size];
//    	for (int i = 0; i < size; i++)
//    		nodes[i] = i;
//    	edges = adjacency.clone();
//    }
    
    
    /** Changed by Mitra **/
    public SubGraphStructure(byte[] adjacency) {
        if (Math.sqrt(adjacency.length) != (int) Math.sqrt(adjacency.length))
            throw new IllegalArgumentException("Adjacency size must be complete square integer!");

        int size = (int) Math.sqrt(adjacency.length);
        nodes = new int[size];
        for (int i = 0; i < size; i++)
            nodes[i] = i;
        edges = adjacency.clone();
    }

//    public void setEdgeAt(int v, int w) {
//    	edges[v * nodes.length + w] = true;
//    }

    /** Changed by Mitra **/
    public void setEdgeAt(int v, int w, byte value) {
        edges[v * nodes.length + w] = value;
    }

//    public boolean getEdgeAt(int v, int w) {
//    	return edges[v * nodes.length + w];
//    }

    /** Changed by Mitra **/
    public byte getEdgeAt(int v, int w) {
        return edges[v * nodes.length + w];
    }

    @Override
    public String toString() {
        return Arrays.toString(nodes) + "\t" + getAdjacencySignature();
    }


//    public int getOutDegree(int v) {
//    	int index = Util.arrayContains(nodes, v);
//    	if (index == -1)
//    		throw new IllegalArgumentException("The vertex is not available!");
//    	int out_degree = 0;
//    	for (int i = 0; i < nodes.length; i++)
//    		if (getEdgeAt(index, i))
//    			out_degree++;
//    	return out_degree;
//    }
//    
//    public int getInDegree(int v) {
//    	int index = Util.arrayContains(nodes, v);
//    	if (index == -1)
//    		throw new IllegalArgumentException("The vertex is not available!");
//    	int in_degree = 0;
//    	for (int i = 0; i < nodes.length; i++)
//    		if (getEdgeAt(i, index))
//    			in_degree++;
//    	return in_degree;
//    	
//    }
//    
    /** Changed by Mitra **/
    public int getOutDegree(int v) {
        int index = Util.arrayContains(nodes, v);
        if (index == -1)
            throw new IllegalArgumentException("The vertex is not available!");
        int out_degree = 0;
        for (int i = 0; i < nodes.length; i++)
            if (getEdgeAt(index, i) == 1)
                out_degree++;
        return out_degree;
    }

    /** Changed by Mitra **/
    public int getInDegree(int v) {
        int index = Util.arrayContains(nodes, v);
        if (index == -1)
            throw new IllegalArgumentException("The vertex is not available!");
        int in_degree = 0;
        for (int i = 0; i < nodes.length; i++)
            if (getEdgeAt(i, index) == 1)
                in_degree++;
        return in_degree;

    }


//    /**
//     *  MA: Canonical labeling performs here based on a heuristic ranking method
//     *  and associated long value of that label will be computed
//     */
//    final public SubGraphStructure getOrderedForm() {
//    	SubGraphStructure result = new SubGraphStructure(nodes.length);
//    	long[] ranks = new long[nodes.length];
//    	
//    	/** MA: Ranking the vertices based on their in/out degree
//    	 *  Rank of vertex v = (out-degree * motifSize) + in-degree
//    	 *  Sort vertices based on ranked (Ascending order)
//    	 */
//    	result.nodes = nodes.clone();//Arrays.copyOf(nodes, nodes.length);
//    	if (beFast)
//    		for (int v = 0; v < result.nodes.length; v++)
//    			ranks[v] = getOutDegree(result.nodes[v]);
//    	else
//    		for (int v = 0; v < result.nodes.length; v++)
//    			ranks[v] = getOutDegree(result.nodes[v]) * nodes.length + getInDegree(result.nodes[v]);
//    	
//    	Util.rankedInsertionSort(result.nodes, ranks);
//    	
//    	/** MA: Canonical label of given subgraph */
//    	int[] index = new int[result.nodes.length];
//    	for (int i = 0; i < index.length; i++)
//    		index[i] = Util.arrayContains(result.nodes, nodes[i]);
//    	
//    	for (int i = 0; i < nodes.length; i++)
//    		for (int j = 0; j < nodes.length; j++)
//    			if (getEdgeAt(i, j))
//    				result.setEdgeAt(index[i], index[j]);
//    	return result;
//    }
    /**
     *  MA: Canonical labeling performs here based on a heuristic ranking method
     *  and associated long value of that label will be computed
     *  
     *  Changed by Mitra to change ranking procedure for colored vertices
     */
    final public SubGraphStructure getOrderedForm() {
        SubGraphStructure result = new SubGraphStructure(nodes.length);
        long[] ranks = new long[nodes.length];

        /** MA: Ranking the vertices based on their in/out degree
         *  Rank of vertex v = ((out-degree * motifSize) + in-degree) * (color + 1)
         *  Sort vertices based on ranked (Ascending order)
         */
        result.nodes = nodes.clone();
        if (beFast)
            for (int v = 0; v < result.nodes.length; v++)
                ranks[v] = getOutDegree(result.nodes[v]);
        else
            for (int v = 0; v < result.nodes.length; v++)
                ranks[v] = (long) ((getOutDegree(result.nodes[v]) * nodes.length + getInDegree(result.nodes[v])) + (Math.pow(10, edges[(v * nodes.length) + v])));

        Util.rankedInsertionSort(result.nodes, ranks);
        
        /** MA: Canonical label of given subgraph */
        int[] index = new int[result.nodes.length];
        for (int i = 0; i < index.length; i++)
            index[i] = Util.arrayContains(result.nodes, nodes[i]);

        for (int i = 0; i < nodes.length; i++)
            for (int j = 0; j < nodes.length; j++)
            	result.setEdgeAt(index[i], index[j], getEdgeAt(i, j));
        return result;
    }

    
//    public BigInteger getAdjacencyAsBigInt() {
//        BigInteger result = BigInteger.ZERO;
//        for (int i = 0; i < nodes.length; i++)
//            for (int j = 0; j < nodes.length; j++)
//                if (getEdgeAt(i, j))
//                    result = result.setBit(i * nodes.length + j);
//        return result;
//    }

//    public BoolArray getAdjacencyArray() {
//    	return BoolArray.buildFrom(edges);
//    }
    public ByteArray getAdjacencyArray() {
        return ByteArray.buildFrom(edges);
    }

    /**
     * MA: Changed for return values
     * @return
     */
    public String getAdjacencySignature() {
        return Arrays.toString(edges);
    }
}
