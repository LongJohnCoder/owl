// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** References:
 * "Subdivision Schemes for Positive Definite Matrices"
 * by Uri Itai, Nir Sharon
 * 
 * "Approximation schemes for functions of positive-definite matrix values"
 * by Nir Sharon, Uri Itai
 * 
 * Riemannian Variance Filtering: An Independent Filtering Scheme for Statistical
 * Tests on Manifold-valued Data */
// LONGTERM not implemented yet
/* package */ enum SpdGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from TensorGeodesic
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    throw new UnsupportedOperationException();
  }

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    throw new UnsupportedOperationException();
  }
}
