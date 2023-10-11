package de.ps1ttacus.SyntaxTree;

import java.util.HashMap;
import java.util.List;

public class TreeNode {
    private String name;
    private List<TreeNode> children;
    private HashMap<String, String> productionRule;
    public double widthNeeded;
    private String ableitungsfolgeTeilwort;

    public int total;

    public TreeNode(String name) {
        this.name = name;
        this.widthNeeded = -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }


    /**
     * To recursively get the generated word, we have to ask all the children for their "name"
     * The leafs have the terminals - so from them, we get the generated word
     * @return
     */
    public String getWord() {
        String word = "";
        //if we are not a leaf
        if(children != null && !children.isEmpty()) {
            for (TreeNode child :
                    children) {
                word += child.getWord();
            }
            return word;
        } else {
            //we are a leaf
            if(name.equals("ε")) {
                return "";
            }
            return name;
        }
    }


    public int requiredWidth() {
        this.total = 1;
        if(isLeaf(this)) {
            return 1;
        }
        for (TreeNode child :
                children) {
            total += child.requiredWidth();
        }
        return total;
    }

    public boolean isLeaf(TreeNode treeNode) {
        return (treeNode.getChildren() == null || treeNode.getChildren().isEmpty());
    }

    public HashMap<String, String> getProductionRule() {
        return productionRule;
    }

    public void setProductionRule(HashMap<String, String> productionRule) {
        this.productionRule = productionRule;
    }

    public String getAbleitungsfolgeTeilwort() {
        return ableitungsfolgeTeilwort;
    }

    public void setAbleitungsfolgeTeilwort(String ableitungsfolgeTeilwort) {
        this.ableitungsfolgeTeilwort = ableitungsfolgeTeilwort;
    }
}
