// code by jph
package ch.ethz.idsc.sophus.crv;

import java.util.Objects;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

/** Generalization of the Ramer-Douglas-Peucker algorithm
 * 
 * Quote: "The Ramer-Douglas-Peucker algorithm decimates a curve composed of line segments
 * to a similar curve with fewer points. [...] The algorithm defines 'dissimilar' based
 * on the maximum distance between the original curve and the simplified curve. [...]
 * The expected complexity of this algorithm can be described by the linear recurrence
 * T(n) = 2 * T(​n/2) + O(n), which has the well-known solution O(n * log n). However, the
 * worst-case complexity is O(n^2)."
 * 
 * https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm */
public enum CurveDecimation {
  ;
  /** @param lieGroup
   * @param tangent mapper
   * @param epsilon non-negative
   * @return
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(LieGroup lieGroup, TensorUnaryOperator tangent, Scalar epsilon) {
    return new CurveDecimationLieGroup( //
        Objects.requireNonNull(lieGroup), //
        Objects.requireNonNull(tangent), //
        Sign.requirePositiveOrZero(epsilon));
  }
}
