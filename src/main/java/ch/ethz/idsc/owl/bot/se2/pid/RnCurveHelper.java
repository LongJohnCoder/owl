// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum RnCurveHelper {
  ;
  /** @param curve
   * @param pose {x, y, heading}
   * @return position of the closest point on the curve to the current pose */
  public static int closest(Tensor curve, Tensor pose) {
    // TODO MPC Norm._2 only works when all scalars have same unit
    return ArgMin.of(Tensor.of(curve.stream().map(curvePoint -> Norm._2.between(curvePoint, pose))));
  }

  /** @param curve
   * @param point
   * @return angle between two following points of the closest point on the curve to the current pose */
  public static Scalar trajAngle(Tensor curve, Tensor point) {
    int index = closest(curve, point);
    // TODO JPH/MCP the angle is 3rd entry (=heading) of the control point
    // return curve.get(index, 2);
    int nextIndex = index + 1;
    if (nextIndex >= curve.length()) // TODO MCP Write this better
      nextIndex = 0;
    return ArcTan2D.of(curve.get(nextIndex).subtract(curve.get(index)));
  }

  /** @param optionalCurve
   * @return if enough elements in curve */
  public static boolean bigEnough(Tensor optionalCurve) {
    return optionalCurve.length() > 1; // TODO MCP Write this better
  }
}