// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.RrtsPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.glc.adapter.EntityGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.GoalConsumer;
import ch.ethz.idsc.owl.glc.adapter.SimpleGoalConsumer;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;

public enum MouseGoal {
  ;
  public static void simple( //
      OwlyAnimationFrame owlyAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint) {
    simple(owlyAnimationFrame, trajectoryEntity, plannerConstraint, Collections.emptyList());
  }

  public static void simple( //
      OwlyAnimationFrame owlyAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, //
      List<GlcPlannerCallback> _callbacks) {
    List<GlcPlannerCallback> callbacks = new ArrayList<>(_callbacks);
    if (trajectoryEntity instanceof GlcPlannerCallback)
      callbacks.add((GlcPlannerCallback) trajectoryEntity);
    callbacks.add(EntityGlcPlannerCallback.of(trajectoryEntity));
    supply(owlyAnimationFrame.geometricComponent, //
        new SimpleGoalConsumer(trajectoryEntity, plannerConstraint, callbacks));
  }

  public static void simpleRrts( //
      OwlyAnimationFrame owlyAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint) {
    simpleRrts(owlyAnimationFrame, trajectoryEntity, plannerConstraint, Collections.emptyList());
  }

  public static void simpleRrts( //
      OwlyAnimationFrame owlyAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, //
      List<RrtsPlannerCallback> _callbacks) {
    List<RrtsPlannerCallback> callbacks = new ArrayList<>(_callbacks);
    if (trajectoryEntity instanceof RrtsPlannerCallback)
      callbacks.add((RrtsPlannerCallback) trajectoryEntity);
    supply(owlyAnimationFrame.geometricComponent, //
        new SimpleGoalConsumer(trajectoryEntity, plannerConstraint, callbacks));
  }

  public static void supply(GeometricComponent geometricComponent, GoalConsumer goalConsumer) {
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        final int mods = mouseEvent.getModifiersEx();
        final int mask = InputEvent.CTRL_DOWN_MASK; // 128 = 2^7
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
          if ((mods & mask) == 0) // no ctrl pressed
            goalConsumer.accept(geometricComponent.getMouseSe2State());
        }
      }
    };
    geometricComponent.jComponent.addMouseListener(mouseAdapter);
  }
}
