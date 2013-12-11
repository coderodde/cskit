package net.coderodde.cskit.graph.p2psp.uniform;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import static net.coderodde.cskit.Utilities.findTouchNode;
import static net.coderodde.cskit.Utilities.tracebackPathBidirectional;
import net.coderodde.cskit.graph.DirectedGraphNode;

/**
 * This class implement parallel bidirectional breadth-first search algorithm.
 *
 * @author Rodion Efremov
 * @version 1.61 (8.12.2013)
 */
public class ParallelBidirectionalBFSFinder implements UniformCostPathFinder {

    private Map<DirectedGraphNode, DirectedGraphNode> parentMapA =
            new HashMap<DirectedGraphNode, DirectedGraphNode>();

    private Map<DirectedGraphNode, DirectedGraphNode> parentMapB =
            new HashMap<DirectedGraphNode, DirectedGraphNode>();

    private Semaphore mutexA = new Semaphore(1, true);
    private Semaphore mutexB = new Semaphore(1, true);

    private Set<DirectedGraphNode> levelA = new HashSet<DirectedGraphNode>();
    private Set<DirectedGraphNode> levelB = new HashSet<DirectedGraphNode>();

    private Map<DirectedGraphNode, Integer> distanceMapA =
            new HashMap<DirectedGraphNode, Integer>();

    private Map<DirectedGraphNode, Integer> distanceMapB =
            new HashMap<DirectedGraphNode, Integer>();

    @Override
    public List<DirectedGraphNode> find(DirectedGraphNode source, DirectedGraphNode target) {
        clear();

        distanceMapA.put(source, 0);
        distanceMapB.put(target, 0);

        parentMapA.put(source, null);
        parentMapB.put(target, null);

        levelA.add(source);
        levelB.add(target);

        ForwardSearchThread threadA = new ForwardSearchThread(source);
        threadA.start();

        BackwardsSearchThread threadB = new BackwardsSearchThread(target,
                                                                  threadA);
        threadA.setBrotherThread(threadB);
        threadB.run();

        try {
            threadA.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
            return null;
        }

        DirectedGraphNode touch = findTouchNode(levelA,
                                                levelB,
                                                parentMapA,
                                                parentMapB,
                                                distanceMapA,
                                                distanceMapB);

        return tracebackPathBidirectional(touch, parentMapA, parentMapB);
    }

    /**
     * This class implements the forward search.
     */
    private class ForwardSearchThread extends Thread {

        private DirectedGraphNode node;
        private BackwardsSearchThread thread;
        private volatile boolean doRun = true;

        ForwardSearchThread(DirectedGraphNode node) {
            this.node = node;
        }

        void setBrotherThread(BackwardsSearchThread thread) {
            this.thread = thread;
        }

        void stopRunning() {
            doRun = false;
        }

        @Override
        public void run() {
            DirectedGraphNode lastA = node;
            Deque<DirectedGraphNode> queueA =
                    new LinkedList<DirectedGraphNode>();
            queueA.add(node);

            while (queueA.isEmpty() == false && doRun) {
                DirectedGraphNode current = queueA.getFirst();

                for (DirectedGraphNode child : current) {
                    if (parentMapA.containsKey(child) == false) {
                        distanceMapA.put(child, distanceMapA.get(current) + 1);
                        queueA.addLast(child);

                        mutexA.acquireUninterruptibly();
                        parentMapA.put(child, current);
                        levelA.add(child);
                        mutexA.release();
                    }
                }

                if (current.equals(lastA)) {
                    mutexB.acquireUninterruptibly();

                    if (Collections.disjoint(levelA,
                                             parentMapB.keySet()) == false) {
                        thread.stopRunning();
                        mutexB.release();
                        return;
                    }

                    mutexB.release();

                    lastA = queueA.getLast();
                }

                queueA.removeFirst();
                levelA.remove(current);
            }
        }
    }

    /**
     * This class implements the backward search.
     */
    private class BackwardsSearchThread extends Thread {

        private DirectedGraphNode node;
        private ForwardSearchThread thread;
        private volatile boolean doRun = true;

        BackwardsSearchThread(DirectedGraphNode node,
                              ForwardSearchThread brotherThread) {
            this.node = node;
            this.thread = brotherThread;
        }

        void stopRunning() {
            doRun = false;
        }

        @Override
        public void run() {
            DirectedGraphNode lastB = node;
            Deque<DirectedGraphNode> queueB =
                    new LinkedList<DirectedGraphNode>();
            queueB.addLast(node);

            while (queueB.isEmpty() == false && doRun) {
                DirectedGraphNode current = queueB.getFirst();

                for (DirectedGraphNode parent : current.parentIterable()) {
                    if (parentMapB.containsKey(parent) == false) {
                        distanceMapB.put(parent, distanceMapB.get(current) + 1);
                        queueB.addLast(parent);

                        mutexB.acquireUninterruptibly();
                        parentMapB.put(parent, current);
                        levelB.add(parent);
                        mutexB.release();
                    }
                }

                if (current.equals(lastB)) {
                    mutexA.acquireUninterruptibly();

                    if (Collections.disjoint(levelB,
                                             parentMapA.keySet()) == false) {
                        thread.stopRunning();
                        mutexA.release();
                        return;
                    }

                    mutexA.release();

                    lastB = queueB.getLast();
                }

                queueB.removeFirst();
                levelB.remove(current);
            }
        }
    }

    private void clear() {
        distanceMapA.clear();
        distanceMapB.clear();
        parentMapA.clear();
        parentMapB.clear();
        levelA.clear();
        levelB.clear();
    }
}
