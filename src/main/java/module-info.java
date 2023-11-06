module ru.axe.abs {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires java.xml;
    requires javafx.swing;

    opens ru.axe.abs to javafx.fxml;
    exports ru.axe.abs;
    exports ru.axe.abs.models;
    opens ru.axe.abs.models to javafx.fxml;
}