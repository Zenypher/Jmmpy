package ro.ase.sebastianiosif.jmp.components;

/** Idempotent cleanup for both user-initiated and programmatic tab removal. */
public interface DisposableTab {
    void dispose();
}
