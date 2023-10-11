module de.ps1ttacus.bachelorarbeit {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.ps1ttacus.bachelorarbeit to javafx.fxml;
    exports de.ps1ttacus.bachelorarbeit;
    exports de.ps1ttacus.SyntaxTree;
    opens de.ps1ttacus.SyntaxTree to javafx.fxml;
}