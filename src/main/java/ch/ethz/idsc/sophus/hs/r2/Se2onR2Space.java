// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum Se2onR2Space {
  ;
  public static TensorUnaryOperator action(Tensor xya) {
    return tensor -> new Se2GroupElement(xya).combine(tensor.copy().append(RealScalar.ZERO)).extract(0, 2);
  }

  public static Tensor action(Tensor xya, Tensor uv) {
    return new Se2GroupElement(xya).combine(uv.copy().append(RealScalar.ZERO)).extract(0, 2);
  }
}
