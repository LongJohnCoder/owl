// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class Hermite3SubdivisionsTest extends TestCase {
  public void testA1MatchString() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 5);
    for (int length = 4; length < 6; ++length) {
      Tensor control = RandomVariate.of(distribution, length, 2);
      TensorIteration ti1 = RnHermite3Subdivisions.a1().string(RealScalar.ONE, control);
      TensorIteration ti2 = Hermite3Subdivisions.a1(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE) //
          .string(RealScalar.ONE, control);
      for (int count = 0; count < 4; ++count) {
        Tensor tensor = ti1.iterate();
        Tensor result = ti2.iterate();
        ExactTensorQ.require(tensor);
        ExactTensorQ.require(result);
        assertEquals(tensor, result);
      }
    }
  }

  public void testA1MatchCyclic() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 5);
    for (int length = 4; length < 6; ++length) {
      Tensor control = RandomVariate.of(distribution, length, 2);
      TensorIteration ti1 = RnHermite3Subdivisions.a1().cyclic(RealScalar.ONE, control);
      TensorIteration ti2 = Hermite3Subdivisions.a1(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE) //
          .cyclic(RealScalar.ONE, control);
      for (int count = 0; count < 4; ++count) {
        Tensor tensor = ti1.iterate();
        Tensor result = ti2.iterate();
        ExactTensorQ.require(tensor);
        ExactTensorQ.require(result);
        assertEquals(tensor, result);
      }
    }
  }

  public void testA2MatchString() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 5);
    for (int length = 4; length < 6; ++length) {
      Tensor control = RandomVariate.of(distribution, length, 2);
      TensorIteration ti1 = RnHermite3Subdivisions.a2().string(RealScalar.ONE, control);
      TensorIteration ti2 = Hermite3Subdivisions.a2(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE) //
          .string(RealScalar.ONE, control);
      for (int count = 0; count < 4; ++count) {
        Tensor tensor = ti1.iterate();
        Tensor result = ti2.iterate();
        ExactTensorQ.require(tensor);
        ExactTensorQ.require(result);
        assertEquals(tensor, result);
      }
    }
  }

  public void testA2MatchCyclic() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 5);
    for (int length = 4; length < 6; ++length) {
      Tensor control = RandomVariate.of(distribution, length, 2);
      TensorIteration ti1 = RnHermite3Subdivisions.a2().cyclic(RealScalar.ONE, control);
      TensorIteration ti2 = Hermite3Subdivisions.a2(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE) //
          .cyclic(RealScalar.ONE, control);
      for (int count = 0; count < 4; ++count) {
        Tensor tensor = ti1.iterate();
        Tensor result = ti2.iterate();
        ExactTensorQ.require(tensor);
        ExactTensorQ.require(result);
        assertEquals(tensor, result);
      }
    }
  }
}