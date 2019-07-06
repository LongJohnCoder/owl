// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Optional;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;

public class ClothoidPursuitTest extends TestCase {
  public void testSimple() {
    GeodesicPursuitInterface geodesicPursuit;
    Tensor trajectory = Tensors.of( //
        Tensors.vector(0, 0, 0), //
        Tensors.vector(2, 2, Math.PI / 2), //
        Tensors.vector(4, 4, Math.PI / 2));
    // ---
    geodesicPursuit = ClothoidPursuits.fromTrajectory(trajectory, NaiveEntryFinder.INSTANCE, RealScalar.ONE);
    // System.out.println("ratios 1 = " + (geodesicPursuit.firstRatio().isPresent() ? geodesicPursuit.firstRatio().get() : "empty"));
    assertEquals(RationalScalar.of(1, 2), Round._8.apply(geodesicPursuit.firstRatio().orElse(null)));
  }

  public void testCurve() {
    for (int depth = 0; depth < 5; ++depth) {
      Tensor tensor = ClothoidPursuit.curve(Tensors.fromString("{10, 1, 1}"), depth);
      assertEquals(tensor.length(), (1 << depth) + 1);
    }
  }

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
