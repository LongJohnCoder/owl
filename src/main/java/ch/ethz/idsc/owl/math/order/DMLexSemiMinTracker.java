// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import ch.ethz.idsc.tensor.Tensor;

/** Adapted version of the LexicographicSemiorderMinTracker which only tracks elements that
 * have advantageous or beneficial scores, i.e. any element with scores that are worse in
 * all objectives compared to any existing element in the tracker will be discarded regardless
 * whether or not they are within the threshold.
 * 
 * @param <K> key type */
public class DMLexSemiMinTracker<K> extends AbstractLexSemiMinTracker<K> {
  /** @param slacks
   * @return
   * @throws Exception if given slacks is null */
  public static <K> LexSemiMinTracker<K> withList(Tensor slacks) {
    return new DMLexSemiMinTracker<>(slacks, new LinkedList<>());
  }

  /** @param slacks
   * @return
   * @throws Exception if given slacks is null */
  public static <K> LexSemiMinTracker<K> withSet(Tensor slacks) {
    return new DMLexSemiMinTracker<>(slacks, new HashSet<>());
  }

  // ---
  private DMLexSemiMinTracker(Tensor slacks, Collection<Pair<K>> candidateSet) {
    super(slacks, candidateSet);
  }

  @Override // from AbstractLexSemiMinTracker
  protected void trim(Pair<K> applicantPair, Collection<Pair<K>> candidateSet, Collection<K> discardedKeys) {
    Iterator<Pair<K>> iterator = candidateSet.iterator();
    while (iterator.hasNext()) {
      Pair<K> currentPair = iterator.next();
      { // we are only interested in beneficial elements
        OrderComparison productComparison = productComparison(applicantPair, currentPair, dim - 1);
        // if applicantPair is better in all dimensions remove currentPair
        if (productComparison.equals(OrderComparison.STRICTLY_PRECEDES)) {
          discardedKeys.add(currentPair.key());
          iterator.remove();
          continue;
          // if applicantPair is indifferent or worse in all dimension discard applicantPair
        } else //
        if (!productComparison.equals(OrderComparison.INCOMPARABLE)) {
          discardedKeys.add(applicantPair.key());
          return;
        }
      }
      // the code below is identical to LexicographicSemiorderMinTracker::digest
      for (int index = 0; index < dim; ++index) {
        OrderComparison semiorder = semiorderComparators.get(index).compare(applicantPair.value().Get(index), currentPair.value().Get(index));
        // if x strictly precedes the current object and it is strictly preceding
        // in every coordinate until now, then the current object will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_PRECEDES)) {
          if (productComparison(applicantPair, currentPair, index).equals(OrderComparison.STRICTLY_PRECEDES)) {
            discardedKeys.add(currentPair.key());
            iterator.remove();
            break; // leave for loop, continue with while loop
          }
        } else //
        // if x strictly succeeding the current object and it is strictly succeeding
        // in every coordinate until now, then x will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_SUCCEEDS)) {
          if (productComparison(applicantPair, currentPair, index).equals(OrderComparison.STRICTLY_SUCCEEDS)) {
            discardedKeys.add(applicantPair.key());
            return;
          }
        }
      }
    }
    candidateSet.add(applicantPair);
  }
}
