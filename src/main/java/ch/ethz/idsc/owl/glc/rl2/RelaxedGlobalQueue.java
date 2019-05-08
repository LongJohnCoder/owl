// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Iterator;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.order.LexicographicSemiorderMinTracker;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RelaxedGlobalQueue extends RelaxedPriorityQueue {
  /** holds the node which have not yet been expanded */
  public RelaxedGlobalQueue(Tensor slacks) {
    super(slacks);
  }

  @Override
  public void add(GlcNode glcNode) {
    addSingle(glcNode);
  }

  @Override
  public GlcNode peekBest() {
    LexicographicSemiorderMinTracker<GlcNode> minTracker = LexicographicSemiorderMinTracker.withList(slacks);
    Iterator<GlcNode> iterator = iterator();
    while (iterator.hasNext()) {
      GlcNode currentGlcNode = iterator.next();
      minTracker.digest(currentGlcNode, VectorScalars.vector(currentGlcNode.merit()));
    }
    return minTracker.peekBestKey();
  }

  @Override
  protected GlcNode pollBest() {
    GlcNode glcNode = peekBest();
    remove(glcNode);
    return glcNode;
  }
}