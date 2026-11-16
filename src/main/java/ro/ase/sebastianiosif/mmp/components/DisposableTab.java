package ro.ase.sebastianiosif.mmp.components;

/** Idempotent cleanup for both user-initiated and programmatic tab removal. */
public interface DisposableTab {
    void dispose();
}
