package net.coderodde.cskit.graph.flow;

import net.coderodde.cskit.Utilities.Pair;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.DoubleWeightFunction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class tests Edmond-Karp's maximum flow algorithm.
 *
 * @author Rodion Efremov
 * @version 1.61803
 */
public class EdmondKarpFlowFinderTest {

    @Test
    public void testFind() {
        DirectedGraphNode Vancouver = new DirectedGraphNode("Vancover");
        DirectedGraphNode Edmonton = new DirectedGraphNode("Edmonton");
        DirectedGraphNode Calgary = new DirectedGraphNode("Calgary");
        DirectedGraphNode Saskatoon = new DirectedGraphNode("Saskatoon");
        DirectedGraphNode Regina = new DirectedGraphNode("Regina");
        DirectedGraphNode Winnipeg = new DirectedGraphNode("Winnipeg");

        DoubleWeightFunction c = new DoubleWeightFunction();

        /// 1 - 3
        Vancouver.addChild(Edmonton);
        c.put(Vancouver, Edmonton, 16.0);

        Vancouver.addChild(Calgary);
        c.put(Vancouver, Calgary, 13.0);

        Calgary.addChild(Edmonton);
        c.put(Calgary, Edmonton, 4.0);

        /// 4 - 6
        Edmonton.addChild(Saskatoon);
        c.put(Edmonton, Saskatoon, 12.0);

        Saskatoon.addChild(Calgary);
        c.put(Saskatoon, Calgary, 9.0);

        Calgary.addChild(Regina);
        c.put(Calgary, Regina, 14.0);

        /// 7 - 9
        Saskatoon.addChild(Winnipeg);
        c.put(Saskatoon, Winnipeg, 20.0);

        Regina.addChild(Saskatoon);
        c.put(Regina, Saskatoon, 7.0);

        Regina.addChild(Winnipeg);
        c.put(Regina, Winnipeg, 4.0);

        Pair<DoubleWeightFunction, Double> pair =
                new EdmondKarpFlowFinder().find(Vancouver, Winnipeg, c);

        assertEquals(23.0, pair.second, 0.01);
    }
}
