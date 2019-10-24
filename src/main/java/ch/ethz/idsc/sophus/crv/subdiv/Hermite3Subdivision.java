// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.util.Objects;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.Nocopy;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.red.Times;

/** Reference 1:
 * "Construction of Hermite subdivision schemes reproducing polynomials", 2017
 * Example 3.7, eq. 28, p. 572
 * by Byeongseon Jeong, Jungho Yoon
 * 
 * Reference 2:
 * "Stirling numbers and Gregory coefficients for the factorization of Hermite
 * subdivision operators"
 * Example 35, p. 26
 * by Moosmueller, Huening, Conti, 2019
 * 
 * Hint:
 * For theta == 0 and omega == 0, the scheme reduces to Hermite1Subdivision
 * 
 * Quote from [2]:
 * "it is proved that H1 reproduces polynomials up to degree 3 and
 * thus it satisfies the spectral condition up to order 3"
 * 
 * Quote from [2]:
 * "H1 with theta = 1/32 provides an example by P 4 (x) = 4! 1 x 4 + 360
 * of an Hermite scheme which does not reproduce polynomials of degree 4, but
 * satisfies the spectral condition of order 4. To the best of our knowledge, this
 * is the first time it is observed that the spectral condition is not equivalent to
 * the reproduction of polynomials."
 * 
 * Quote from [2]:
 * "Computations show that the Hermite scheme H1 is C4 for omega in [-0.12, -0.088] */
public class Hermite3Subdivision implements HermiteSubdivision {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final Scalar _3_2 = RationalScalar.of(3, 2);
  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final BiinvariantMean biinvariantMean;
  /** 1/128 */
  private final Scalar theta;
  private final Scalar omega;
  private final Tensor MGW = Tensors.of(RationalScalar.HALF, RationalScalar.HALF);
  /** {1/128, 63/64, 1/128} */
  private final Tensor cgw;
  /** {-1/16, 3/4, -1/16} */
  private final Tensor cvw;

  /** @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @param theta
   * @param omega */
  public Hermite3Subdivision(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, Scalar theta, Scalar omega) {
    this.lieGroup = Objects.requireNonNull(lieGroup);
    this.lieExponential = Objects.requireNonNull(lieExponential);
    this.biinvariantMean = Objects.requireNonNull(biinvariantMean);
    this.theta = theta;
    this.omega = omega;
    cgw = Tensors.of(theta, RealScalar.ONE.subtract(theta.add(theta)), theta);
    cvw = Tensors.of(omega, RealScalar.ONE.add(omega.multiply(RealScalar.of(4))), omega);
  }

  /** default with theta == 1/128 and omega == -1/16
   * 
   * @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @throws Exception if either parameters is null */
  public Hermite3Subdivision(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
    this(lieGroup, lieExponential, biinvariantMean, //
        RationalScalar.of(1, 128), //
        RationalScalar.of(-1, 16));
  }

  @Override // from HermiteSubdivision
  public TensorIteration string(Scalar delta, Tensor control) {
    return new Control(delta, control).new StringIteration();
  }

  @Override // from HermiteSubdivision
  public TensorIteration cyclic(Scalar delta, Tensor control) {
    return new Control(delta, control).new CyclicIteration();
  }

  private class Control {
    private Tensor control;
    private Scalar rgk;
    private Scalar rvk;
    // ---
    private Scalar cgk;
    private Scalar cvk;

    private Control(Scalar delta, Tensor control) {
      this.control = control;
      rgk = delta.divide(RealScalar.of(8));
      rvk = _3_2.divide(delta);
      // ---
      cgk = Times.of(theta, RationalScalar.HALF, delta);
      cvk = omega.multiply(RealScalar.of(-3).divide(delta));
    }

    private Tensor center(Tensor p, Tensor q, Tensor r) {
      Tensor pg = p.get(0);
      Tensor pv = p.get(1);
      Tensor qg = q.get(0);
      Tensor qv = q.get(1);
      Tensor rg = r.get(0);
      Tensor rv = r.get(1);
      Tensor cg1 = biinvariantMean.mean(Unprotect.byRef(pg, qg, rg), cgw);
      Tensor cg2 = pv.subtract(rv).multiply(cgk);
      Tensor cg = lieGroup.element(cg1).combine(cg2);
      Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(rg)); // r - p
      Tensor cv1 = log.multiply(cvk);
      Tensor cv2 = cvw.dot(Unprotect.byRef(pv, qv, rv));
      Tensor cv = cv1.add(cv2);
      return Tensors.of(cg, cv);
    }

    /** @param p == {pg, pv}
     * @param q == {qg, qv}
     * @return r == {rg, rv} */
    private Tensor midpoint(Tensor p, Tensor q) {
      Tensor pg = p.get(0);
      Tensor pv = p.get(1);
      Tensor qg = q.get(0);
      Tensor qv = q.get(1);
      Tensor rg1 = biinvariantMean.mean(Unprotect.byRef(pg, qg), MGW);
      Tensor rg2 = lieExponential.exp(pv.subtract(qv).multiply(rgk));
      Tensor rg = lieGroup.element(rg1).combine(rg2);
      Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(qg)); // q - p
      Tensor rv1 = log.multiply(rvk);
      Tensor rv2 = qv.add(pv).multiply(_1_4);
      Tensor rv = rv1.subtract(rv2);
      return Tensors.of(rg, rv);
    }

    private class StringIteration implements TensorIteration {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        int length = control.length();
        Nocopy string = new Nocopy(2 * length - 1);
        Tensor p = control.get(0);
        string.append(p); // interpolation
        Tensor q = control.get(1);
        string.append(midpoint(p, q));
        for (int index = 2; index < length; ++index) {
          Tensor r = control.get(index);
          string.append(center(p, q, r));
          p = q;
          q = r;
          string.append(midpoint(p, q));
        }
        string.append(q);
        rgk = rgk.multiply(RationalScalar.HALF);
        rvk = rvk.add(rvk);
        cgk = cgk.multiply(RationalScalar.HALF);
        cvk = cvk.add(cvk);
        return control = string.tensor();
      }
    }

    private class CyclicIteration implements TensorIteration {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        int length = control.length();
        Nocopy string = new Nocopy(2 * length);
        Tensor p = Last.of(control);
        Tensor q = control.get(0);
        for (int index = 1; index <= length; ++index) {
          Tensor r = control.get(index % length);
          string.append(center(p, q, r));
          p = q;
          q = r;
          string.append(midpoint(p, q));
        }
        rgk = rgk.multiply(RationalScalar.HALF);
        rvk = rvk.add(rvk);
        cgk = cgk.multiply(RationalScalar.HALF);
        cvk = cvk.add(cvk);
        return control = string.tensor();
      }
    }
  }
}
