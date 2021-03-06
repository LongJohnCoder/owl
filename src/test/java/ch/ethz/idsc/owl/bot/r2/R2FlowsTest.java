// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.opt.ConvexHull;
import junit.framework.TestCase;

public class R2FlowsTest extends TestCase {
  public void testSimple() {
    int n = 100;
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Flow> flows = r2Flows.getFlows(n);
    assertEquals(flows.size(), n);
    Tensor tflow = Tensor.of(flows.stream().map(Flow::getU));
    Tensor hul = ConvexHull.of(tflow);
    assertEquals(Dimensions.of(tflow), Dimensions.of(hul));
  }

  public void testFail() {
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    try {
      r2Flows.getFlows(2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
