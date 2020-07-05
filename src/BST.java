import java.io.*;
import java.util.*;


class BSTNode<T> {
    public int NodeKey; // ключ узла
    public T NodeValue; // значение в узле
    public BSTNode<T> Parent; // родитель или null для корня
    public BSTNode<T> LeftChild; // левый потомок
    public BSTNode<T> RightChild; // правый потомок	

    public BSTNode(int key, T val, BSTNode<T> parent) {
        NodeKey = key;
        NodeValue = val;
        Parent = parent;
        LeftChild = null;
        RightChild = null;
    }
}

// промежуточный результат поиска
class BSTFind<T> {
    // null если в дереве вообще нету узлов
    public BSTNode<T> Node;

    // true если узел найден
    public boolean NodeHasKey;

    // true, если родительскому узлу надо добавить новый левым
    public boolean ToLeft;

    public BSTFind() {
        Node = null;
    }
}

class BST<T> {
    BSTNode<T> Root; // корень дерева, или null

    public BST(BSTNode<T> node) {
        Root = node;
    }

    public BSTFind<T> FindNodeByKey(int key) {
        // ищем в дереве узел и сопутствующую информацию по ключу
        BSTFind<T> bstFindRes = new BSTFind<>();
        BSTNode<T> tempBstNode = Root;

        while (key != tempBstNode.NodeKey) {
            if (key < tempBstNode.NodeKey) {
                if (tempBstNode.LeftChild != null) {
                    tempBstNode = tempBstNode.LeftChild;
                } else {
                    bstFindRes.ToLeft = true;
                    break;
                }
            } else {
                if (tempBstNode.RightChild != null) {
                    tempBstNode = tempBstNode.RightChild;
                } else {
                    bstFindRes.ToLeft = false;
                    break;
                }
            }
        }

        bstFindRes.Node = tempBstNode;
        bstFindRes.NodeHasKey = tempBstNode.NodeKey == key;
        return bstFindRes;
    }

    public boolean AddKeyValue(int key, T val) {
        // добавляем ключ-значение в дерево
        BSTFind<T> bstFindTemp = FindNodeByKey(key);
        if (bstFindTemp.NodeHasKey) return false; // если ключ уже есть

        BSTNode<T> bstNodeNew = new BSTNode<>(key, val, bstFindTemp.Node);
        if (bstFindTemp.ToLeft) bstFindTemp.Node.LeftChild = bstNodeNew;
        else bstFindTemp.Node.RightChild = bstNodeNew;

        return true;
    }

    public BSTNode<T> FinMinMax(BSTNode<T> FromNode, boolean FindMax) {
        // ищем максимальное/минимальное в поддереве
        BSTNode<T> bstNodeResult = FromNode;

        if (FindMax) {
            while (bstNodeResult.RightChild != null) {
                bstNodeResult = bstNodeResult.RightChild;
            }
        } else {
            while (bstNodeResult.LeftChild != null) {
                bstNodeResult = bstNodeResult.LeftChild;
            }
        }
        return bstNodeResult;
    }

    public boolean DeleteNodeByKey(int key) {
        // удаляем узел по ключу

        // находим удаляемый узел, его детей, родителя и какой он child
        BSTFind<T> findNodeDel = FindNodeByKey(key);
        if (!findNodeDel.NodeHasKey) return false; // если узел не найден
        BSTNode<T> nodeDel = findNodeDel.Node;
        BSTNode<T> parentNodeDel = nodeDel.Parent;
        boolean isLeftChild = true;
        if (parentNodeDel != null) {
            isLeftChild = parentNodeDel.LeftChild == nodeDel;
        }

        // ищем узел которым надо заменить
        BSTNode<T> nodeForReplace = nodeDel;
        if (nodeForReplace.RightChild != null) nodeForReplace = nodeForReplace.RightChild;
        while (nodeForReplace.LeftChild != null) nodeForReplace = nodeForReplace.LeftChild;

        // если удаляемый узел лист или один, удаляем его
        if (nodeDel == nodeForReplace) {
            if (parentNodeDel != null) {
                if (!isLeftChild) parentNodeDel.RightChild = null;
                else parentNodeDel.LeftChild = null;
            } else {
                Root = null;
            }
            return true;
        }

        // если удаляемый узел и заменяющий узел не один и тот же, изменяем ...

        // ... ссылку на заменяющий
        BSTNode<T> parentNodeForReplace = nodeForReplace.Parent;
        if (parentNodeForReplace != null) {
            if (parentNodeForReplace.LeftChild == nodeForReplace) parentNodeForReplace.LeftChild = null;
            else parentNodeForReplace.RightChild = null;
        }

        // ... ссылку на родителя
        nodeForReplace.Parent = parentNodeDel;

        // ... у родителя ссылку на удаляемый узел
        if (parentNodeDel != null) {
            if (!isLeftChild) parentNodeDel.RightChild = nodeForReplace;
            else parentNodeDel.LeftChild = nodeForReplace;
        } else {
            Root = nodeForReplace;
        }

        // ... ссылки на детей
        BSTNode<T> nodeDelLeftChild = nodeDel.LeftChild;
        BSTNode<T> nodeDelRightChild = nodeDel.RightChild;
        nodeForReplace.LeftChild = nodeDelLeftChild;
        nodeForReplace.RightChild = nodeDelRightChild;

        // ... у детей ссылку на родителя
        if (nodeDelLeftChild != null) nodeDelLeftChild.Parent = nodeForReplace;
        if (nodeDelRightChild != null) nodeDelRightChild.Parent = nodeForReplace;

        return true;
    }

    public int Count() {
        // находим максимальный и минимальный ключ
        BSTNode<T> bstNodeMax = FinMinMax(Root, true);
        BSTNode<T> bstNodeMin = FinMinMax(Root, false);

        // цикл подстчета найденных узлов от мин до макс
        int count = 0;
        for (int i = bstNodeMin.NodeKey; i <= bstNodeMax.NodeKey; i++) {
            BSTFind<T> bstFind = FindNodeByKey(i);
            if (i == bstFind.Node.NodeKey) {
                count++;
            }
        }

        return count; // количество узлов в дереве
    }

}