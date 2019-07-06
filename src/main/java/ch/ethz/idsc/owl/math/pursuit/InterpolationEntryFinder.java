// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Mod;

public final class InterpolationEntryFinder extends TrajectoryEntryFinder {
  public static final TrajectoryEntryFinder INSTANCE = new InterpolationEntryFinder();
  // ---
  private static final Mod MOD_UNIT = Mod.function(1);

  private InterpolationEntryFinder() {
    // ---
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar index) {
    // TODO GJOEL test simpler version below:
    // Clip clip = Clips.positive(waypoints.length() - 1);
    // if (clip.isInside(index))
    // return new TrajectoryEntry(Optional.of(LinearInterpolation.of(waypoints).at(index)), index);
    int index_ = index.number().intValue();
    if (index_ >= 0 && index_ < waypoints.length() - 1) {
      Interpolation interpolation = LinearInterpolation.of(Tensors.of( //
          waypoints.get(index_), //
          waypoints.get(index_ + 1)));
      return new TrajectoryEntry(interpolation.at(MOD_UNIT.apply(index)), index);
    } else if (index_ == waypoints.length() - 1)
      return new TrajectoryEntry(waypoints.get(index_), index);
    return new TrajectoryEntry(null, index);
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    return IntStream.range(0, waypoints.length()).mapToObj(RealScalar::of);
  }
}
