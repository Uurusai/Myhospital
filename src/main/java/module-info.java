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
    requires java.desktop;

    opens com.hms.model to javafx.base;

    exports com.hms.myhospital;
    exports com.hms.model;

    opens com.hms.myhospital to javafx.fxml;
}