// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Optional;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidPursuitTest extends TestCase {
  public void testPointRadius1() {
    GeodesicPursuitInterface geodesicPursuit = new ClothoidPursuit(Tensors.vector(1, 1, Math.PI / 2));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Chop._12.requireClose(optional.get(), RealScalar.ONE);
  }

  public void testPointRadius1Neg() {
    GeodesicPursuitInterface geodesicPursuit = new ClothoidPursuit(Tensors.vector(1, -1, -Math.PI / 2));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Chop._12.requireClose(optional.get(), RealScalar.ONE.negate());
  }

  public void testPointRadiusTwo() {
    GeodesicPursuitInterface geodesicPursuit = new ClothoidPursuit(Tensors.vector(2, 2, Math.PI / 2));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Chop._12.requireClose(optional.get(), RationalScalar.HALF);
  }

  public void testPointRadiusTwoNeg() {
    GeodesicPursuitInterface geodesicPursuit = new ClothoidPursuit(Tensors.vector(2, -2, -Math.PI / 2));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Chop._12.requireClose(optional.get(), RationalScalar.HALF.negate());
  }

  public void testPointRadiusStraight() {
    GeodesicPursuitInterface geodesicPursuit = new ClothoidPursuit(Tensors.vector(10, 0, 0));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Chop._12.requireClose(optional.get(), RealScalar.ZERO);
  }

  public void testQuantity() {
    GeodesicPursuitInterface geodesicPursuit = new ClothoidPursuit(Tensors.fromString("{1[m], 1[m], .3}"));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Clips.interval(Quantity.of(2.75, "m^-1"), Quantity.of(2.77, "m^-1")).requireInside(optional.get());
  }
}
