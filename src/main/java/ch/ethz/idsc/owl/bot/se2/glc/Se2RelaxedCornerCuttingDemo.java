// code by astoll
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.GoalConsumer;
import ch.ethz.idsc.owl.glc.adapter.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.SimpleGoalConsumer;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.CameraEmulator;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.qty.Degree;

public class Se2RelaxedCornerCuttingDemo extends Se2CarDemo {
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-90), Degree.of(90), 32), Subdivide.of(0, 5, 30));

  static R2ImageRegionWrap createResLo() {
    BufferedImage bufferedImage = new BufferedImage(64, 64, BufferedImage.TYPE_BYTE_GRAY);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 64, 64);
    graphics.setColor(Color.BLACK);
    graphics.fillRect(50, 7, 7, 20);
    graphics.fillRect(27, 20, 23, 7);
    graphics.fillRect(27, 27, 7, 20);
    graphics.fillRect(7, 40, 20, 7);
    Tensor image = Transpose.of(ImageFormat.from(bufferedImage));
    Tensor range = Tensors.vector(12, 12);
    int ttl = 2;
    return new R2ImageRegionWrap(image, range, ttl);
  }

  @Override // from Se2CarDemo
  protected final void configure(OwlyAnimationFrame owlyAnimationFrame) {
    StateTime stateTime = new StateTime(Tensors.vector(1.7, 2.2, 0), RealScalar.ZERO);
    Tensor slacks = Tensors.vector(1.5, 0);
    CarRelaxedEntity carRelaxedEntity = CarRelaxedEntity.createDefault(stateTime, slacks);
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = createResLo();
    carRelaxedEntity.setAdditionalCostFunction(r2ImageRegionWrap.costFunction());
    // ---
    ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    PlannerConstraint plannerConstraint = createConstraint(imageRegion);
    TrajectoryRegionQuery trajectoryRegionQuery = //
        SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    // owlyAnimationFrame.addBackground(RegionRenders.create(testImageRegion));
    List<GlcPlannerCallback> list = new ArrayList<>();
    list.add(carRelaxedEntity);
    list.add(new SimpleGlcPlannerCallback(carRelaxedEntity));
    // ---
    GoalConsumer goalConsumer = new SimpleGoalConsumer(carRelaxedEntity, plannerConstraint, list);
    Tensor goal = Tensors.vector(4.3, 4.2, 1.517);
    goalConsumer.accept(goal);
    // ---
    owlyAnimationFrame.add(carRelaxedEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    MouseGoal.simple(owlyAnimationFrame, carRelaxedEntity, plannerConstraint);
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), carRelaxedEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, carRelaxedEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(imageRegion)), //
          CarEntity.SHAPE, () -> carRelaxedEntity.getStateTimeNow().time());
      owlyAnimationFrame.addBackground(renderInterface);
    }
  }

  public static void main(String[] args) {
    new Se2RelaxedCornerCuttingDemo().start().jFrame.setVisible(true);
  }
}
