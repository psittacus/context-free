package de.ps1ttacus.Beispielwoerter;

import de.ps1ttacus.SyntaxTree.TreeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BeispielwoerterGenerierung {
    private final int MAX_COUNT = 5;
    private final int MAX_DEPTH = 100;
    private TreeGenerator treeGenerator;
    private HashMap<String, List<String>> productionRules;

    public BeispielwoerterGenerierung(TreeGenerator treeGenerator) {
        this.treeGenerator = treeGenerator;
        this.productionRules = treeGenerator.getProductionRules();
    }


    public List<String> generateWords() {
        List<String> generatedWords = new ArrayList<>();
        if(bringToChomksyNormalform()) {
            String startSymbol = treeGenerator.getModel().getStartsymbol();

            generatedWords.add(calculateshortestTerminal(startSymbol, 0));

            int lastLength = generatedWords.get(0).length();
            for(int i = 1; i < MAX_COUNT; i++) {
                String word = calculateWordWithMinLength(lastLength, startSymbol, 0);
                generatedWords.add(word);
                lastLength = word.length();
            }
        }

        return generatedWords;
    }

    /* S -> AB
     * A -> CD
     * A -> EF
     * B -> b
     * C -> GH
     * D -> IJ
     * E -> e
     * F -> f
     * G -> g
     * H -> h
     * I -> i
     * J -> j
     * ---------
     * A -> CD: 2
     */
    public String calculateshortestTerminal(String symbol, int depth) {
        if (depth >= MAX_DEPTH) {
            //we see ourselves in a loop, so we return this sign to be possibly the longest word
            return "%";
        }
        String finalDerivation = "";
        int shortestCount = Integer.MAX_VALUE;
        List<String> list = productionRules.get(symbol);
        List<String> derivations = new ArrayList<>();
        //iterate over every possible derivation
        for (String possibleDerivation :
                list) {
            if (isJustATerminal(possibleDerivation)) {
                derivations.add(possibleDerivation);
            } else {
                String derivation = "";
                for (char c :
                        possibleDerivation.toCharArray()) {
                    derivation += calculateshortestTerminal(String.valueOf(c), depth + 1);
                }
                derivations.add(derivation);
            }
        }

        for (String derivation :
                derivations) {
            if (derivation.length() < shortestCount) {
                shortestCount = derivation.length();
                finalDerivation = derivation;
            }
        }

        return finalDerivation;
    }

    private int getNonterminalProductionRule(String symbol) {
        List<String> derivations = productionRules.get(symbol);
        for (int i = 0; i < derivations.size(); i++) {
            if (!isJustATerminal(derivations.get(i))) {
                return i;
            }
        }

        return -1;
    }

    private String calculateWordWithMinLength(int length, String symbol, int depth) {
        int shortestCount = Integer.MAX_VALUE;
        if (depth >= MAX_DEPTH) {
            //we see ourselves in a loop, so we return this sign to be possibly the longest word
            return "%";
        }
        String finalDerivation = "";

        List<String> list = productionRules.get(symbol);
        List<String> derivations = new ArrayList<>();
        //iterate over every possible derivation

        int index = getNonterminalProductionRule(symbol);
        if (index != -1) {
            if (depth <= length) {
                return derivateNonterminals(length, list, depth, index);
            } else {
                //go through all possible production rules
                return goThroughProductionRules(length, list, depth);
            }
        } else {
            for (String possibleDerivation :
                    list) {
                //it has to be a terminal
                derivations.add(possibleDerivation);
            }
        }

        for (String derivation : derivations) {
            if (derivation.length() < shortestCount) {
                shortestCount = derivation.length();
                finalDerivation = derivation;
            }
        }

        return finalDerivation;
    }

    private String derivateNonterminals(int length, List<String> list, int depth, int index) {
        String derivation = "";
        for (char c : list.get(index).toCharArray()) {
            derivation += calculateWordWithMinLength(length, String.valueOf(c), depth + 2);
        }
        return derivation;
    }

    private String goThroughProductionRules(int length, List<String> list, int depth) {
        String derivation = "";
        for (String rule : list) {
            if(isJustATerminal(rule)) {
                //Now that we have the needed length, we just return a terminal if possible
                return rule;
            } else {
                //If we don't have any terminal production rules here, we just continue the derivations
                for (char c :
                        rule.toCharArray()) {
                    derivation += calculateWordWithMinLength(length, String.valueOf(c), depth + 2);
                }
            }
        }
        return derivation;
    }

    private void getAllTerminals() {
        ArrayList<String> derivations;
        ArrayList<String> returnList = new ArrayList<>();

        derivations = listOfPossibleDerivations();

        derivations.sort((s1, s2) -> s1.length() - s2.length());
        for (int i = 0; i < (MAX_COUNT > derivations.size() ? derivations.size() : MAX_COUNT); i++) {
            returnList.add(derivations.get(i));
        }
    }

    private ArrayList<String> listOfPossibleDerivations() {
        ArrayList<String> derivations = new ArrayList<>();


        return derivations;
    }

    public boolean bringToChomksyNormalform() {
        try {
            if (productionRulesContainEpsilon()) {
                return false;
            }
            removeChainingProductionRules();
            separateTerminals();
            eliminateNonterminalchains();
        } catch (Exception e) {
            System.out.println("Nicht geklappt: " + e.getMessage());
        }
        return true;
    }

    /* S -> ABCDE
     * vvvvvvvvvv
     * S -> ZE
     * Z -> YD
     * Y -> XC
     * X -> AB
     *
     * Now: all terminals should be standing alone, now the nonterminals are distributed in production rules, where
     * they are only alone or paired
     */
    public void eliminateNonterminalchains() throws Exception {
        HashMap<String, List<String>> copy = copyHashMap(productionRules);
        //for every nonterminal on the left side...
        for (String key :
                copy.keySet()) {
            //...iterate over every right side...
            for (String rule :
                    copy.get(key)) {
                if (rule.length() > 2) {
                    String newNonterminal = findNewNonterminal();
                    for (int i = rule.length() - 1; i > 0; i--) {
                        // if i > 1:
                        // so not this:
                        //      _v__
                        // S -> ABCD
                        // take the element at i (start at the end)
                        // create new nonterminal
                        // safe this nonterminal for the next iteration

                        if (i == rule.length() - 1) {
                            // S -> ZD
                            productionRules.get(key).add(newNonterminal + String.valueOf(rule.charAt(i)));
                        } else if (i > 1) {
                            // e.g. Y
                            String nextNonterminal = findNewNonterminal();
                            if (nextNonterminal.isBlank()) {
                                throw new Exception("Ran out of nonterminals");
                            }
                            // Y -> ?
                            productionRules.put(newNonterminal, new ArrayList<>());
                            // Y -> XC
                            productionRules.get(newNonterminal).add(nextNonterminal + String.valueOf(rule.charAt(i)));
                            newNonterminal = nextNonterminal;
                        } else {
                            //add last new production rule
                            // X -> ?
                            productionRules.put(newNonterminal, new ArrayList<>());
                            // X -> AB
                            productionRules.get(newNonterminal).add(String.valueOf(rule.charAt(i - 1)) + String.valueOf(rule.charAt(i)));
                            //and delete the last chain (S -> ABCD)
                            productionRules.get(key).remove(rule);
                        }
                    }
                }
            }
        }

    }

    private HashMap<String, List<String>> copyHashMap(HashMap<String, List<String>> map) {
        HashMap<String, List<String>> copy = new HashMap<>();
        for (String key :
                map.keySet()) {
            copy.put(key, new ArrayList<>());
            for (String value :
                    map.get(key)) {
                copy.get(key).add(value);
            }
        }

        return copy;
    }

    public void separateTerminals() throws Exception {
        HashMap<String, List<String>> copy = copyHashMap(productionRules);
        //for every nonterminal on the left side...
        for (String key :
                copy.keySet()) {
            //...iterate over every right side...
            for (String rule :
                    copy.get(key)) {
                if (!isJustATerminal(rule)) {
                    //... and go through every character of the right side
                    String newRule = rule;
                    for (int i = 0; i < rule.length(); i++) {
                        //if the charAt(i) is a terminal, separate it into an own production rule
                        String symbol = String.valueOf(rule.charAt(i));
                        if (isJustATerminal(symbol)) {

                            //neue Produktionsregel mit neuem Nichtterminal
                            String newNonterminal = existsProductionRule(symbol);
                            if (newNonterminal.isBlank()) {
                                newNonterminal = findNewNonterminal();
                            }
                            if (newNonterminal.isBlank()) {
                                throw new Exception("No more available nonterminals");
                            }
                            productionRules.put(newNonterminal, new ArrayList<>());
                            productionRules.get(newNonterminal).add(symbol);
                            //ersetze Terminal in dieser Produktion durch neues Nichtterminal

                            String newProductionRule = newRule.substring(0, i) + newNonterminal + ((i + 1 < newRule.length()) ? newRule.substring(i + 1) : "");
                            newRule = newProductionRule;
                        }
                    }
                    productionRules.get(key).remove(rule);
                    productionRules.get(key).add(newRule);
                }
            }
        }
    }

    /**
     * Test if there is a production rule, where just a terminal is produced, so we don't have to create a new nonterminal
     *
     * @param rightSide
     * @return
     */
    private String existsProductionRule(String rightSide) {
        for (String key :
                productionRules.keySet()) {
            //if it's JUST for the terminal
            if (productionRules.get(key).size() == 1) {
                for (String pr :
                        productionRules.get(key)) {
                    if (pr.equals(rightSide)) {
                        return key;
                    }
                }
            }
        }
        return "";
    }

    private String findNewNonterminal() {
        ArrayList<String> alphabet = new ArrayList<>(List.of(new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"}));
        for (String nichtterminal :
                treeGenerator.getModel().getNichtterminale()) {
            alphabet.remove(nichtterminal);
        }
        if (alphabet.isEmpty()) {
            return "";
        }
        treeGenerator.getModel().getNichtterminale().add(alphabet.get(0));
        return alphabet.get(0);
    }

    public void removeChainingProductionRules() {
        /* if a chaining rule was solved but the solving created another chaining, this has to be rerun
         * Example:
         * S -> A
         * A -> B
         * vvvvvv
         * S -> B
         */
        boolean hasToBeRerun = false;
        HashMap<String, List<String>> copy = copyHashMap(productionRules);
        //search for instances of chaining production rules
        for (String key :
                copy.keySet()) {
            for (String rule :
                    copy.get(key)) {
                boolean chain = isJustANonterminal(rule);

                if (chain) {
                    //Remove this production rule
                    productionRules.get(key).remove(rule);
                    for (String ruleOfChainingNonterminal : copy.get(rule)) {
                        //here we could create another chaining
                        if (isJustANonterminal(ruleOfChainingNonterminal)) {
                            hasToBeRerun = true;
                        }
                        // S -> A
                        // A -> a
                        // is now:
                        // S -> a
                        productionRules.get(key).add(ruleOfChainingNonterminal);
                    }
                }
            }
        }

        if (hasToBeRerun) {
            removeChainingProductionRules();
        }
    }

    private boolean isJustANonterminal(String str) {
        for (String nonterminal :
                treeGenerator.getModel().getNichtterminale()) {
            if (nonterminal.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean isJustATerminal(String str) {
        for (String terminal :
                treeGenerator.getModel().getTerminale()) {
            if (terminal.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean productionRulesContainEpsilon() {
        for (String key :
                productionRules.keySet()) {
            for (String rule :
                    productionRules.get(key)) {
                if (isEpsilon(rule)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int countOccasions(String right, String key) {
        int count = 0;
        for (int i = 0; i < right.length(); i++) {
            if (String.valueOf(right.charAt(i)).equals(key)) {
                count++;
            }
        }
        return count;
    }

    private boolean calculateFastestWayToTermination(String nonterminal, int depth) {
        //the possibility is high that there is an error
        if (depth >= 100) {
            return false;
        }
        List<String> prs = productionRules.get(nonterminal);


        return true;
    }

    private boolean isEpsilon(String rs) {
        return rs.contains("ε") ? true : false;
    }

    private boolean isTerminalsOnly(String rs) {
        for (String nt :
                treeGenerator.getModel().getNichtterminale()) {
            if (rs.contains(nt)) {
                return false;
            }
        }
        return true;
    }

    private String shortestNonterminalToTermination(String leftSide) {
        return "";
    }

    private int getIndexOfSmallestTerminals(List<String> rightSide) {
        int count = Integer.MAX_VALUE;
        int index = 0;
        int indexOfSmallest = -1;
        for (String rs : rightSide) {
            if (isTerminalsOnly(rs)) {
                if (rs.length() < count) {
                    count = rs.length();
                    indexOfSmallest = index;
                }
            }
            index++;
        }
        return indexOfSmallest;
    }

    public List<String> generateExamplewords() {
        List<String> result = new ArrayList<>();
        return result;
    }
}
