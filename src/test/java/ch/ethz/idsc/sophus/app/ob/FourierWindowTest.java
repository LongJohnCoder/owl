// code by ob
package ch.ethz.idsc.sophus.app.ob;

import junit.framework.TestCase;

public class FourierWindowTest extends TestCase {
  public void testSimple() {
    // TODO OB: Tests
  }

  public void testHighestOneBit() {
    int highestOneBit = Integer.highestOneBit(64 + 3);
    assertEquals(highestOneBit, 64);
  }
}