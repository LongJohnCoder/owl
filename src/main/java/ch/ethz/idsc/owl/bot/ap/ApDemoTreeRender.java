// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.TreeRender;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;

/** simple animation of a landing airplane */
/* package */ enum ApDemoTreeRender {
  ;
  public static void main(String[] args) throws Exception {
    final Scalar INITIAL_X = RealScalar.of(0);
    final Scalar INITIAL_Z = RealScalar.of(40);
    final Scalar INITIAL_VEL = RealScalar.of(70);
    final Scalar INITIAL_GAMMA = Degree.of(2);
    final Tensor INITIAL = Tensors.of(INITIAL_X, INITIAL_Z, INITIAL_VEL, INITIAL_GAMMA);
    // SphericalRegion sphericalRegion = new SphericalRegion(ApTrajectoryPlanner.GOAL.extract(2, 4), ApTrajectoryPlanner.RADIUS_VECTOR.Get(2));
    // StateTimeRaster stateTimeRaster = ApTrajectoryPlanner.stateTimeRaster();
    StandardTrajectoryPlanner standardTrajectoryPlanner = ApTrajectoryPlanner.ApStandardTrajectoryPlanner();
    // ---
    // OwlyFrame owlyFrame = OwlyGui.start();
    // owlyFrame.configCoordinateOffset(33, 416);
    // owlyFrame.addBackground(RegionRenders.create(region));
    // owlyFrame.addBackground(RegionRenders.create(sphericalRegion));
    // owlyFrame.addBackground(RenderElements.create(stateTimeRaster));
    // owlyFrame.addBackground(RenderElements.create(plannerConstraint));
    // owlyFrame.addBackground(new DomainRender(trajectoryPlanner.getDomainMap(), eta));
    // ---
    standardTrajectoryPlanner.insertRoot(new StateTime(INITIAL, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(standardTrajectoryPlanner);
    glcExpand.findAny(1000);
    Optional<GlcNode> optional = standardTrajectoryPlanner.getBest();
    // ---
    System.out.println("Initial starting point: " + INITIAL);
    System.out.println("Final desired point: " + ApTrajectoryPlanner.GOAL);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    // ---
    if (optional.isPresent()) {
      System.out.println(1);
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
      // ---
      // Print flows
      System.out.println("Flows:");
      List<TrajectorySample> trajSamples = GlcTrajectories.detailedTrajectoryTo( //
          standardTrajectoryPlanner.getStateIntegrator(), optional.get());
      trajSamples.stream().skip(1).forEach(a -> System.out.println(a.getFlow().get().getU()));
      // ---
      // Render glc-tree
      OwlyFrame owlyFrame = OwlyGui.start();
      TreeRender treeRender = new TreeRender(standardTrajectoryPlanner.getDomainMap().values(), 0, 1, 20000);
      owlyFrame.addBackground(treeRender);
      owlyFrame.configCoordinateOffset(0, 2900);
      owlyFrame.geometricComponent.jComponent.repaint();
    }
    // OwlyGui.glc(standardTrajectoryPlanner);
  }
}