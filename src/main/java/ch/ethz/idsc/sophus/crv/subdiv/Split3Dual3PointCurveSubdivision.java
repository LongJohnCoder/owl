// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** dual scheme */
public class Split3Dual3PointCurveSubdivision extends Dual3PointCurveSubdivision {
  /** @param geodesicInterface
   * @param pq_f
   * @param qr_f
   * @param pqqr
   * @return */
  public static CurveSubdivision of(SplitInterface splitInterface, Scalar pq_f, Scalar qr_f, Scalar pqqr) {
    return new Split3Dual3PointCurveSubdivision(splitInterface, pq_f, qr_f, pqqr);
  }

  // ---
  private final Scalar lo_pq;
  private final Scalar lo_qr;
  private final Scalar lo_pqqr;
  private final Scalar hi_pq;
  private final Scalar hi_qr;
  private final Scalar hi_pqqr;

  private Split3Dual3PointCurveSubdivision( //
      SplitInterface splitInterface, //
      Scalar pq_f, Scalar qr_f, Scalar pqqr) {
    super(splitInterface);
    this.lo_pq = pq_f;
    this.lo_qr = qr_f;
    this.lo_pqqr = pqqr;
    hi_pq = RealScalar.ONE.subtract(qr_f);
    hi_qr = RealScalar.ONE.subtract(pq_f);
    hi_pqqr = RealScalar.ONE.subtract(pqqr);
  }

  // point between p and q but more towards q
  @Override
  protected Tensor lo(Tensor p, Tensor q, Tensor r) {
    Tensor pq = splitInterface.split(p, q, lo_pq);
    Tensor qr = splitInterface.split(q, r, lo_qr);
    return splitInterface.split(pq, qr, lo_pqqr);
  }

  // point between q and r but more towards q
  @Override
  protected Tensor hi(Tensor p, Tensor q, Tensor r) {
    Tensor pq = splitInterface.split(p, q, hi_pq);
    Tensor qr = splitInterface.split(q, r, hi_qr);
    return splitInterface.split(pq, qr, hi_pqqr);
  }
}
