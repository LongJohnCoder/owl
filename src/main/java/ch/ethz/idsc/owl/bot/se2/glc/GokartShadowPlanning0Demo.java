// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.util.Arrays;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.StreetScenario;
import ch.ethz.idsc.owl.bot.util.StreetScenarioData;
import ch.ethz.idsc.owl.glc.adapter.ConstraintViolationCost;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.mapping.ShadowMapSpherical;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;

public class GokartShadowPlanning0Demo extends GokartDemo {
  static final StreetScenarioData STREET_SCENARIO_DATA = StreetScenario.S1.load();
  private static final float PED_VELOCITY = 1.2f;
  private static final float PED_RADIUS = 0.3f;
  private static final Color PED_LEGAL_COLOR = new Color(38, 239, 248, 200);
  private static final Color PED_ILLEGAL_COLOR = new Color(38, 100, 248, 200);
  private static final float MAX_A = 0.8f; // [m/s²]
  private static final float REACTION_TIME = 0.3f;
  private static final float CAR_RADIUS = 0.2f;
  private static final Tensor RANGE = Tensors.vector(52, 40);
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-180), Degree.of(180), 72), Subdivide.of(0.0, 20.0, 60));

  @Override
  protected void configure(OwlyAnimationFrame owlyAnimationFrame) {
    // ---
    final StateTime initial = new StateTime(Tensors.vector(36.283, 8.850, 1.571), RealScalar.ZERO);
    GokartVecEntity gokartEntity = new GokartVecEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, Degree.of(18));
      }
    };
    // ---
    Tensor imagePedLegal = STREET_SCENARIO_DATA.imagePedLegal;
    Tensor imagePedIllegal = STREET_SCENARIO_DATA.imagePedIllegal;
    Tensor imageCar = STREET_SCENARIO_DATA.imageCar_extrude(0);
    Tensor imageLid = STREET_SCENARIO_DATA.imageLid;
    ImageRegion irPedLegal = new ImageRegion(imagePedLegal, RANGE, false);
    ImageRegion irPedIllegal = new ImageRegion(imagePedIllegal, RANGE, false);
    ImageRegion irCar = new ImageRegion(imageCar, RANGE, false);
    ImageRegion irLid = new ImageRegion(imageLid, RANGE, false);
    // ---
    ImageRender imageRender = ImageRender.range(STREET_SCENARIO_DATA.render, RANGE);
    owlyAnimationFrame.addBackground(imageRender);
    owlyAnimationFrame.add(gokartEntity);
    // ---
    TrajectoryRegionQuery lidarRay = SimpleTrajectoryRegionQuery.timeInvariant(irLid);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(irCar);
    // ---
    LidarEmulator lidarEmulator = new LidarEmulator( //
        LIDAR_RAYTRACER, gokartEntity::getStateTimeNow, lidarRay);
    owlyAnimationFrame.addBackground(lidarEmulator);
    // ---
    RenderInterface renderInterface = new MouseShapeRender( //
        SimpleTrajectoryRegionQuery.timeInvariant(irCar), //
        GokartEntity.SHAPE, () -> gokartEntity.getStateTimeNow().time());
    owlyAnimationFrame.addBackground(renderInterface);
    // ---
    ShadowMapSpherical smPedLegal = //
        new ShadowMapSpherical(lidarEmulator, irPedLegal, PED_VELOCITY, PED_RADIUS);
    ShadowMapSpherical smPedIllegal = //
        new ShadowMapSpherical(lidarEmulator, irPedIllegal, PED_VELOCITY, PED_RADIUS);
    smPedLegal.setColor(PED_LEGAL_COLOR);
    smPedIllegal.setColor(PED_ILLEGAL_COLOR);
    // ---
    CostFunction pedLegalCost = //
        ConstraintViolationCost.of(new SimpleShadowConstraintCV( //
            smPedLegal, irCar, CAR_RADIUS, MAX_A, REACTION_TIME, false), RealScalar.ONE);
    @SuppressWarnings("unused")
    CostFunction pedIllegalCost = //
        ConstraintViolationCost.of(new SimpleShadowConstraintCV( //
            smPedIllegal, irCar, CAR_RADIUS, MAX_A, REACTION_TIME, false), RealScalar.ONE);
    gokartEntity.setCostVector(Arrays.asList(pedLegalCost), Arrays.asList(0.0));
    gokartEntity.addTimeCost(1, 0.0);
    // ---
    // List<GlcPlannerCallback> callbacks = new ArrayList<>();
    // ShadowEvaluator evaluator = new ShadowEvaluator(smPedLegal);
    // callbacks.add(evaluator.sectorTimeToReact);
    MouseGoal.simple(owlyAnimationFrame, gokartEntity, plannerConstraint);
    // ---
  }

  public static void main(String[] args) {
    new GokartShadowPlanning0Demo().start().jFrame.setVisible(true);
  }
}
