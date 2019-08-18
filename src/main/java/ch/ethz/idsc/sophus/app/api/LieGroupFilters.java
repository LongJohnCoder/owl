// code by ob, jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenterMidSeeded;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** in the current implementation all filters have the same performance for an arbitrary radius */
public enum LieGroupFilters {
  GEODESIC {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
        LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return GeodesicCenter.of(geodesicInterface, smoothingKernel);
    }
  }, //
  GEODESIC_MID {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
        LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return GeodesicCenterMidSeeded.of(geodesicInterface, smoothingKernel);
    }
  }, //
  BIINVARIANT_MEAN {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
        LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return BiinvariantMeanCenter.of(biinvariantMean, smoothingKernel);
    }
  }, //
  ;
  // ---
  public abstract TensorUnaryOperator supply( //
      GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
      LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean);
}