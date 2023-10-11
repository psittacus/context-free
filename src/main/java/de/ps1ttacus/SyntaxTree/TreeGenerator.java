package de.ps1ttacus.SyntaxTree;

import de.ps1ttacus.Ableitungsfolge.Ableitungsfolge;
import de.ps1ttacus.bachelorarbeit.Model;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreeGenerator {
    private int MAX_DEPTH = 500;
    private Model model;
    private HashMap<String, List<String>> productionRules;
    private TreeNode rootNode;
    private ArrayList<String> ableitungsfolge;

    public TreeGenerator(Model model) {
        this.model = model;
        rootNode = new TreeNode(model.getStartsymbol());
        ableitungsfolge = new ArrayList<>();
    }

    public TreeNode generateTree() {
        preprocessProductionRules();
        ableitungsfolge = new ArrayList<>();
        ableitungsfolge.add(this.model.getStartsymbol());
        try {
            if(leftDerivation(rootNode, 0)) {
                rootNode.setAbleitungsfolgeTeilwort(this.model.getStartsymbol());
                return rootNode;
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
        return null;
    }

    /**
     * The algorithm is easier if the production rules follow the following rules:
     * - Only one production per production rule ({S -> aA | e} vs. {S -> aA, S -> e})
     * - Split the left and the right side ({"S -> aA"} --> {"S", "aA"})
     * -
     */
    private void preprocessProductionRules() {
        productionRules = new HashMap();
        for (String pr : this.model.getProduktionen()) {
            if(pr.contains("|")) {
                /* example for better understanding:
                 * "S -> aA | e"
                 */
                var temp = pr.split("\\|"); // ["S -> aA "," e", "a"]
                String leftSide = temp[0].split("->")[0].strip(); //"S"

                addToProductionRules(temp[0]);
                for(int i = 1; i < temp.length; i++) {
                    /* We need the ArrayList as Value because:
                     * S -> aA
                     * S -> e
                     * have the same left side, and two keys with the same value don't work in HashMap
                     */
                    if(!productionRules.containsKey(leftSide)) {
                        productionRules.put(leftSide, new ArrayList<String>());
                    }
                    productionRules.get(leftSide).add(temp[i].strip()); //"S","e"
                }
            } else {
                /* example for this
                 * "S -> aA"
                 */
                addToProductionRules(pr);
            }
        }
    }

    /**
     * @param pr
     * Receives pr which has following value pattern:
     * "LS -> RS"
     */
    private void addToProductionRules(String pr) {
        //For the example "S -> aA"
        String leftSide = pr.split("->")[0].strip(); // "S"
        String rightSide = pr.split("->")[1].strip(); // "aA"
        if(!productionRules.containsKey(leftSide)) {
            productionRules.put(leftSide, new ArrayList<String>());
        }
        productionRules.get(leftSide).add(rightSide);
    }

    /**
     * First strategy: We try to derivate left with backtracking:
     * For that, lets specify some values for the example:
     * Starting Symbol: S
     * testing word: aabb
     * Productions:
     * (1) S -> aA
     * (2) S -> e
     * (3) A -> aA
     * (4) A -> aB
     * (5) B -> bB
     * (6) B -> b
     *
     * Now we try to generate the word aabb
     * Startingproduction: We take the first production rule and apply it
     * computed: "aA"
     *  lets check which is a nonterminal and which is a terminal: a -> terminal, A -> nonterminal
     *      is the first terminal equal to the first terminal in our testing word? _a_abb, _a_ -> YES!
     *  Let's move on: We have nonterminal A here, lets see what we can do with that:
     *  (1) A -> aA
     *  (2) A -> aB
   *|-> We just take the first one
   *|   apply the production rule:
   *|   computed: "aaA"
   *|    lets check which is a nonterminal and which is a terminal: aa -> terminal, A -> nonterminal
   *|        are the first terminals equal to the first terminals in our testing word? _aa_bb, _aa_ -> YES!
   *|   Let's move on: We have nonterminal A here, lets see what we can do with that:
   *|   (1) A -> aA
   *|   (2) A -> aB
   *|   (The same song again? Alright the same song again!)
   *|   We just take the first one
   *|   apply the production rule:
   *|   computed: "aaaA"
   *|    lets check which is a nonterminal and which is a terminal: aaa -> terminal, A -> nonterminal
   *|        are the first terminals equal to the first terminals in our testing word? _aa_bb, _aaa_ -> NO!
   *|        return false! -> Next possible production rule
   *|   We take the second one (A -> aB)
   *|   computed: "aaaB"
   *|    lets check which is a nonterminal and which is a terminal: aaa -> terminal, B -> nonterminal
   *|        are the first terminals equal to the first terminals in our testing word? _aa_bb, _aaa_ -> NO!
   *|        return false! -> Next possible production rule
   *|        Oh...seems like there is no possible production rule anymore... return false!
   *|-> We take the second production rule (A -> aB)
     * apply the production rule:
     * computed: "aaB"
     * ...
     */
    private boolean leftDerivation(TreeNode node, int depth) {
        /*
         * if depth is greater than MAX_DEPTH, then we possibly ended in a left-recursive production rule
         * we return false and bubble back
         */
        if(depth > MAX_DEPTH) {
            return false;
        }
        TreeNode tempNode = node;
        //get the next nonterminal to process
        TreeNode nodeToProcess = getNextNonterminal(tempNode);
        /* If there is no more node which we can work with, there are two reasons:
         * - We finished computing and now got the word right
         * - We finished computing but got just a subword of the testing word and the testing word is NOT in our language. (like this: aabb < aabbaa)
         */
        if(nodeToProcess == null) {
            if(node.getWord().equals(model.getTestwort())) {
                return true;
            } else {
                return false;
            }
        }
        List<String> possibleProductionRules = productionRules.get(nodeToProcess.getName());
        /*
         * We go through all nonterminals...
         */
        while(getNextNonterminal(tempNode) != null) {
            boolean validProductionRuleWasFound = false;
            /*
             * ...with all possible production rules
             * --> If the recursive call returns false, we applied the wrong production rule and continue with the next
             *     possible one
             *     - If it was the last possible production rule: return false
             * * --> If the recursive call returns true, we save this to our node and continue with the next production
             */
            for(String productionRule : possibleProductionRules) {
                //apply the production rule to the tree
                nodeToProcess.setChildren(applyProductionRule(nodeToProcess.getName(), productionRule));
                /*
                 * Now let's check if the beginning of the generated word in the tree matches with the testing word
                 * If this is true, then the production rule we applied possibly was the correct one
                 * --> We move on with the next nonterminal in the recursive call
                 */
                if(computedWordMatchesTestingWordUntilNonterminal(tempNode.getWord())) {
                    String ableitungsteilwort = rootNode.getWord();
                    if(leftDerivation(tempNode, depth + 1)) {
                        //we found the correct production rule
                        validProductionRuleWasFound = true;
                        setAbleitungsteilwort(nodeToProcess, ableitungsteilwort);
                        break;
                    } else {
                        //we have to search further
                        continue;
                    }
                }
                /*
                 * if this was false, then we applied the wrong production rule
                 * We then have to continue with the next production rule in the list
                 */
                else {
                    continue;
                }
            }
            if(!validProductionRuleWasFound) {
                return false;
            }
        }

        node.setChildren(tempNode.getChildren());
        return true;
    }

    private void setAbleitungsteilwort(TreeNode nodeToProcess, String ableitungsteilwort) {
        ableitungsfolge.add(1, ableitungsteilwort);
        nodeToProcess.getChildren().forEach((treeNode -> {
            treeNode.setAbleitungsfolgeTeilwort(ableitungsteilwort);
        }));
    }

    /**
     * Here is where the magic happens
     * The production rule is processed and all the characters get pressed into a TreeNode
     *
     * TODO Check for epsilon productions
     *
     * @param productionRule a string with the production rule
     * @return the child with new children
     */
    private List<TreeNode> applyProductionRule(String leftSide, String productionRule) {
        List<TreeNode> children = new ArrayList<>();
        for(char c : productionRule.toCharArray()) {
            HashMap<String, String> pr = new HashMap<>();
            pr.put(leftSide, productionRule);
            TreeNode newTreeNode = new TreeNode(String.valueOf(c));
            newTreeNode.setProductionRule(pr);
            children.add(newTreeNode);
        }
        return children;
    }


    /**
     * Searches in Tree for the next nonterminal node
     * Next Nonterminal node is, when:
     * - has no children
     * - name is nonterminal
     * @param node
     * @return next nonterminal TreeNode
     */
    private TreeNode getNextNonterminal(TreeNode node) {
        //only return itself if it really is a nonterminal
        if(node.getChildren() == null) {
            if(isNonterminal(node.getName())) {
                return node;
            }
            //if it is not a nonterminal, it is a terminal - we return null
            return null;
        }
        for (TreeNode child :
                node.getChildren()) {
            if(child.getChildren() == null) {
                if(isNonterminal(child.getName())) {
                    return child;
                }
            } else {
                TreeNode temp = getNextNonterminal(child);
                //if temp is null, we did not find a nonterminal among the children
                if(temp == null) {
                    continue;
                }
                return temp;
            }
        }
        //There is no node with no children and a nonterminal as name
        return null;
    }

    /**
     *
     * @param computedWord
     * @return true if the beginning of the computed word matches with the testing word until a nonterminal appears
     */
    private boolean computedWordMatchesTestingWordUntilNonterminal(String computedWord) {
        if(computedWord == model.getTestwort()) {
            return true;
        }
        try {
            for (int i = 0; i < computedWord.length(); i++) {
                /** at i the computed word is a terminal but the words don't match:
                *   return false, we did a wrong production rule
                */
                if(isTerminal(computedWord.toCharArray()[i]) && (computedWord.toCharArray()[i] != model.getTestwort().toCharArray()[i])) {
                    return false;
                }
                /** clearly if there is a nonterminal at i ,the computed word does not equal the testing word
                 *  But if the terminals before the nonterminal did equal eachother, then we are on a good way
                 *  Even if the nonterminal is on the first place, we have to return true here, because this is our only chance.
                 *  TODO think about left recursive production rules and when to interrupt
                  */
                else if(isNonterminal(computedWord.toCharArray()[i])) {
                    return true;
                }
            }
        } catch(IndexOutOfBoundsException indexOutOfBoundsException) {
            return false;
        }

        /**
         * Coming until here would mean, that the words are equal.
         * Normally this is also checked at the beginning of this method, but well
         */
        return true;
    }

    private boolean isTerminal(char c) {
        String strToTest = String.valueOf(c);
        return model.getTerminale().contains(strToTest);
    }

    private boolean isNonterminal(char c) {
        String strToTest = String.valueOf(c);
        return model.getNichtterminale().contains(strToTest);
    }

    private boolean isNonterminal(String s) {
        return model.getNichtterminale().contains(s);
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
        rootNode = new TreeNode(model.getStartsymbol());
    }

    public ArrayList<String> getAbleitungsfolge() {
        return ableitungsfolge;
    }

    public void setAbleitungsfolge(ArrayList<String> ableitungsfolge) {
        this.ableitungsfolge = ableitungsfolge;
    }

    public HashMap<String, List<String>> getProductionRules() {
        preprocessProductionRules();
        return productionRules;
    }

    public void setProductionRules(HashMap<String, List<String>> productionRules) {
        this.productionRules = productionRules;
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(TreeNode rootNode) {
        this.rootNode = rootNode;
    }
}
