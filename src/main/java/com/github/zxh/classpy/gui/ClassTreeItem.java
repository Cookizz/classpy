package com.github.zxh.classpy.gui;

import com.github.zxh.classpy.classfile.ClassComponent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * Build children lazily.
 * http://download.java.net/jdk8/jfxdocs/javafx/scene/control/TreeItem.html
 */
public class ClassTreeItem extends TreeItem<ClassComponent> {

    private boolean isFirstTimeChildren = true;
    
    public ClassTreeItem(ClassComponent cc) {
        super(cc);
    }

    @Override
    public boolean isLeaf() {
        return getValue().getComponents().isEmpty();
    }
    
    @Override
    public ObservableList<TreeItem<ClassComponent>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;
            System.out.println("get children of " + getValue());

            // First getChildren() call, so we actually go off and 
            // determine the children of the File contained in this TreeItem.
            super.getChildren().setAll(buildChildren());
        }
        
        return super.getChildren();
    }

    private ObservableList<TreeItem<ClassComponent>> buildChildren() {
        ObservableList<TreeItem<ClassComponent>> children = FXCollections.observableArrayList();
        getValue().getComponents().forEach(sub -> {
            children.add(new ClassTreeItem(sub));
        });
        return children;
    }
    
}
