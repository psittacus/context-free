package test;

import de.ps1ttacus.Beispielwoerter.BeispielwoerterGenerierung;
import de.ps1ttacus.SyntaxTree.TreeGenerator;
import de.ps1ttacus.bachelorarbeit.Model;


import java.util.ArrayList;
import java.util.List;

public class BeispielwoerterGenerierungTest {
    private BeispielwoerterGenerierung sut;
    private TreeGenerator treeGenerator;
    BeispielwoerterGenerierungTest() {
        Model model = new Model();
        model.setTerminale(new ArrayList<>(List.of(new String[]{"a","b","c"})));
        model.setNichtterminale(new ArrayList<>(List.of(new String[]{"S","A","B", "C"})));
        model.setProduktionen(List.of(new String[]{
                "S -> ABCD",
                "A -> aA",
                "A -> BC",
                "B -> bB",
                "B -> b",
                "C -> cCCCD",
                "C -> c",
                "D -> d"
        }));
        model.setStartsymbol("S");
        treeGenerator = new TreeGenerator(model);
        sut = new BeispielwoerterGenerierung(treeGenerator);
    }
}

