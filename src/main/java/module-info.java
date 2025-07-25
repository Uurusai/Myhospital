module com.hms.myhospital {
    requires jbcrypt;
    opens com.hms.utils to javafx.fxml;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens com.hms.myhospital to javafx.fxml;
    exports com.hms.myhospital;
}