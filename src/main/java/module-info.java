module ro.ase.sebastianiosif.mmp {
    requires javafx.controls;
    requires javafx.fxml;


    opens ro.ase.sebastianiosif.mmp to javafx.fxml;
    exports ro.ase.sebastianiosif.mmp;
}