package org.tmu.subenum;

import com.carrotsearch.hppc.LongLongOpenHashMap;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultiset;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Saeed on 4/12/14.
 */
public class SMPEnumerator {

    public static boolean randomStates = false;
    private static boolean useHPPC = true;
    private static int uniqueCap = 4 * 1000 * 1000;
    private static long maxCount = Long.MAX_VALUE;
    private static boolean verbose = true;
    /** Mitra **/

    public static void setUseHPPC(boolean useHPPC) {
        SMPEnumerator.useHPPC = useHPPC;
    }

    public static int getUniqueCap() {
        return uniqueCap;
    }

    public static void setUniqueCap(int cap) {
        uniqueCap = cap;
    }

    public static long getMaxCount() {
        return maxCount;
    }

    public static void setMaxCount(long maxCount) {
        SMPEnumerator.maxCount = maxCount;
    }

    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        SMPEnumerator.verbose = verbose;
    }


//    public static long enumerateNonIsoInParallel(final Graph graph, final int k, final int thread_count, final String out_path) throws IOException, InterruptedException {
//    	final AtomicLong found = new AtomicLong(0);
//    	List<SMPState> sorted = null;
//    	if (randomStates) {
////            if(graph.vertexCount()>7000)
////                sorted = SMPState.getAllOneStates(graph);
////            else
//    		sorted = SMPState.getSeedStatesRandomly(graph);
//    		//Collections.shuffle(sorted);
//    	} else
//    		sorted = SMPState.getSeedStates(graph);
//    	
//    	final ConcurrentLinkedQueue<SMPState> bq = new ConcurrentLinkedQueue<SMPState>(sorted);
//    	sorted.clear();
//    	
//    	System.out.printf("Initial states: %,d\n", bq.size());
//    	
//    	final SignatureRepo signatureRepo = new SignatureRepo(out_path);
//    	signatureRepo.setVerbose(verbose);
//    	
//    	Thread[] threads = new Thread[thread_count];
//    	final AtomicInteger live_threads = new AtomicInteger(thread_count);
//    	for (int i = 0; i < thread_count; i++) {
//    		final int thread_id = i;
//    		threads[i] = new Thread(new Runnable() {
//    			@Override
//    			public void run() {
//    				
//    				final HashMultiset<ByteArray> uniqueMap = HashMultiset.create();
//    				final LongLongOpenHashMap luniqueMap = new LongLongOpenHashMap();
//    				
//    				while (bq.size() > 0) {
//    					if (found.get() > maxCount) return;
//    					SMPState top = null;
//    					try {
//    						top = bq.poll();
//    					} catch (NoSuchElementException exp) {
//    						break;
//    					}
//    					if (top == null) {
//    						break;
//    					}
//    					
//    					Stack<SMPState> stack = new Stack<SMPState>();
//    					stack.push(top);
//    					int[] foundSubGraph = new int[k];
//    					
//    					while (stack.size() > 0) {
//    						SMPState state = stack.pop();
//    						if (state.subgraph.length >= k)
//    							throw new IllegalStateException("This must never HAPPEN!!!");
//    						
//    						while (!state.extension.isEmpty()) {
//    							int w = state.extension.get(state.extension.size() - 1);
//    							state.extension.remove(state.extension.size() - 1);
//    							if (state.subgraph.length == k - 1) {
//    								found.getAndIncrement();
//    								System.arraycopy(state.subgraph, 0, foundSubGraph, 0, k - 1);
//    								foundSubGraph[k - 1] = w;//state.extension[i];
//    								if (useHPPC && k <= 8) {
//    									long subl = graph.getSubGraphAsLong(foundSubGraph);
//    									luniqueMap.putOrAdd(subl, 1, 1);
//    									if (luniqueMap.size() > uniqueCap) {
//    										signatureRepo.add(luniqueMap, k);
//    										luniqueMap.clear();
//    									}
//    								} else {
//    									SubGraphStructure sub = graph.getSubGraph(foundSubGraph);
//    									uniqueMap.add(sub.getAdjacencyArray());
//    									if (uniqueMap.elementSet().size() > uniqueCap) {
//    										signatureRepo.add(uniqueMap);
//    										uniqueMap.clear();
//    									}
//    								}
//    							} else {
//    								SMPState new_state = state.expand(w, graph);
//    								if (new_state.extension.size() > 0)
//    									stack.add(new_state);
//    							}
//    						}
//    					}
//    					if (verbose) {
//    						//lastReport.set(found.get());
////                            System.out.printf("Found: %,d   \t LabelSet: %,d\n", found.get(), signatureRepo.size());
//    					}
//    					
//    				}
//    				signatureRepo.add(uniqueMap);
//    				signatureRepo.add(luniqueMap, k);
//    				uniqueMap.clear();
//    				luniqueMap.clear();
//    				System.out.printf("Thread %d finished. %d threads remaining.\n", thread_id, live_threads.decrementAndGet());
//    			}
//    		});
//    	}
//    	
//    	for (int i = 0; i < thread_count; i++)
//    		threads[i].start();
//    	
//    	for (int i = 0; i < thread_count; i++)
//    		threads[i].join();
//    	
//    	System.out.printf("Flushing LabelMap to disk. Total %,d subgraphs.\n", signatureRepo.size());
//    	signatureRepo.close();
//    	
//    	
//    	return found.get();
//    }
    
    /**
     * This code is modified to use less memory space by changing the BFS search in tree to DFS for expanding tree nodes
     * @param graph
     * @param motifSize
     * @param thread_count
     * @param stopwatch
     * @param writer
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static long enumerateNonIsoInParallel(final Graph graph, final int motifSize, final int thread_count, Stopwatch stopwatch, FileWriter writer) throws IOException, InterruptedException {
        final AtomicLong found = new AtomicLong(0);
        List<SMPState> sorted = null;
        if (randomStates) {
//            if(graph.vertexCount()>7000)
//                sorted = SMPState.getAllOneStates(graph);
//            else
            sorted = SMPState.getSeedStatesRandomly(graph);
            //Collections.shuffle(sorted);
        } else
            sorted = SMPState.getSeedStates(graph);

        final ConcurrentLinkedQueue<SMPState> bq = new ConcurrentLinkedQueue<SMPState>(sorted);
        sorted.clear();

        System.out.printf("Initial states: %,d\n", bq.size());

        final SignatureRepo signatureRepo = new SignatureRepo(writer);
        signatureRepo.motifSize = motifSize;
        signatureRepo.setVerbose(verbose);

        Thread[] threads = new Thread[thread_count];
        final AtomicInteger live_threads = new AtomicInteger(thread_count);
        for (int i = 0; i < thread_count; i++) {
            final int thread_id = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {

                    final HashMultiset<ByteArray> uniqueMap = HashMultiset.create();
                    final LongLongOpenHashMap luniqueMap = new LongLongOpenHashMap();

                    while (bq.size() > 0) {
                        if (found.get() > maxCount) return;
                        SMPState top = null;
                        try {
                            top = bq.poll();
                        } catch (NoSuchElementException exp) {
                            break;
                        }
                        if (top == null) {
                            break;
                        }

                        Stack<SMPState> stack = new Stack<SMPState>();
                        stack.push(top);
                        int[] foundSubGraph = new int[motifSize];

                        while (stack.size() > 0) {
                            SMPState state = stack.pop();
                            if (state.subgraph.length >= motifSize)
                                throw new IllegalStateException("This must never HAPPEN!!!");

                            Stack<SMPState> innerStack = new Stack<SMPState>();
                            
                            while (!state.extension.isEmpty()) {
                            	innerStack.push(new SMPState(state));
                            	int w = state.extension.remove(state.extension.size() - 1);

                            	while(!innerStack.isEmpty()) {
                            		SMPState innerState = innerStack.pop();
                            		w = innerState.extension.remove(innerState.extension.size() - 1);
                                	
	                                if (innerState.subgraph.length == motifSize - 1) {
	                                	while (innerState.extension.size() >= 0) {
		                                    found.getAndIncrement();
		                                    System.arraycopy(innerState.subgraph, 0, foundSubGraph, 0, motifSize - 1);
		                                    foundSubGraph[motifSize - 1] = w;//state.extension[i];
		                                    if (useHPPC && motifSize <= 8) {
		                                        long subl = graph.getSubGraphAsLong(foundSubGraph);
		                                        luniqueMap.putOrAdd(subl, 1, 1);
		                                        if (luniqueMap.size() > uniqueCap) {
		                                            signatureRepo.add(luniqueMap, motifSize);
		                                            luniqueMap.clear();
		                                        }
		                                    } else {
		                                        SubGraphStructure sub = graph.getSubGraph(foundSubGraph);
		                                        uniqueMap.add(sub.getAdjacencyArray());
		                                        if (uniqueMap.elementSet().size() > uniqueCap) {
		                                            signatureRepo.add(uniqueMap);
		                                            uniqueMap.clear();
		                                        }
		                                    }
		                                    if (innerState.extension.size() == 0) break;
		                                    w = innerState.extension.remove(innerState.extension.size() - 1);
	                                	}
	                                	if (state.subgraph.length == motifSize - 1)
	                                		state.extension.clear();
	                                } else {
	                                    SMPState new_state = innerState.expand(w, graph);
	                                    if (new_state.extension.size() > 0)
	                                        innerStack.add(new_state);
	                                }
	                                
                            	}
                            }
                        }
                        if (verbose) {
                            //lastReport.set(found.get());
//                            System.out.printf("Found: %,d   \t LabelSet: %,d\n", found.get(), signatureRepo.size());
                        }

                    }
                    signatureRepo.add(uniqueMap);
                    signatureRepo.add(luniqueMap, motifSize);
                    uniqueMap.clear();
                    luniqueMap.clear();
                    System.out.printf("Thread %d finished. %d threads remaining.\n", thread_id, live_threads.decrementAndGet());
                }
            });
        }

        for (int i = 0; i < thread_count; i++)
            threads[i].start();

        for (int i = 0; i < thread_count; i++)
            threads[i].join();

        signatureRepo.writeToLog("Total enumerated subgraphs: " + found.get() + "\n\n");
        signatureRepo.writeToLog("Enumeration took : " + stopwatch + " \tequal to " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds.\n\n");
        signatureRepo.writeToLog("Total number of non-isomorphic subgraphs : " + signatureRepo.size() + "\n");
        signatureRepo.close();


        return found.get();
    }

}
