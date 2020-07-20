import java.io.ByteArrayOutputStream;
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
        if (tempBstNode == null) return bstFindRes;

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
        if (Root == null) {
            Root = bstNodeNew;
            return true;
        }
        if (bstFindTemp.ToLeft) bstFindTemp.Node.LeftChild = bstNodeNew;
        else bstFindTemp.Node.RightChild = bstNodeNew;

        return true;
    }

    public BSTNode<T> FinMinMax(BSTNode<T> FromNode, boolean FindMax) {
        // ищем максимальное/минимальное в поддереве
        BSTNode<T> bstNodeResult = FromNode;
        if (FromNode == null) return null;

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
        int count = 0;
        if (Root == null) return count;

        // находим мин и макс ключ
        BSTNode<T> bstNodeMax = FinMinMax(Root, true);
        BSTNode<T> bstNodeMin = FinMinMax(Root, false);

        // цикл подстчета найденных узлов от мин до макс
        for (int i = bstNodeMin.NodeKey; i <= bstNodeMax.NodeKey; i++) {
            BSTFind<T> bstFind = FindNodeByKey(i);
            if (i == bstFind.Node.NodeKey) {
                count++;
            }
        }

        return count; // количество узлов в дереве
    }

    // обход дерева в ширину
    // возвращаем список всех узлов
    public ArrayList<BSTNode> WideAllNodes() {
        ArrayList<BSTNode> listRes = new ArrayList<>(); // результирующий список
        if (Root == null) return listRes;

        BSTNode node = Root;
        ArrayList<BSTNode> listTemp = new ArrayList<>();

        listTemp.add(node);

        while (!listTemp.isEmpty()) {
            node = listTemp.remove(0);
            if (node.LeftChild != null) listTemp.add(node.LeftChild);
            if (node.RightChild != null) listTemp.add(node.RightChild);
            listRes.add(node);
        }

        return listRes;
    }

    // обход дерева в глубину
    // возвращаем список всех узлов
    public ArrayList<BSTNode> DeepAllNodes(int order) {
        // order = 0 - инфиксная форма
        // order = 1 - постфиксная форма
        // order = 2 - префиксная форма поиска

        ArrayList<BSTNode> listRes = new ArrayList<>(); // результирующий список
        if (Root == null) return listRes; // проверка на пустое дерево
        Stack<BSTNode> stackTemp = new Stack<>(); // используем стек как временное хранилище искомых узлов

        // определяем узел с какого будем начинать
        BSTNode node = Root; //
        if (order == 0) {
            /*node = FinMinMax(Root, false);*/
            simMethod(node, listRes);
            return listRes;
        }
        if (order == 1) {
            node = FinMinMax(Root, false);
            if (node.RightChild != null) node = node.RightChild;
        }
        // начинаем перебор узлов используя временное хранилище - стек
        stackTemp.push(node);
        while (!stackTemp.isEmpty()) {
            node = stackTemp.pop();
            if (order == 1) {
                if (node.LeftChild != null && !listRes.contains(node.LeftChild)) {
                    stackTemp.push(node.LeftChild);
                    continue;
                }
                if (node.RightChild != null && !listRes.contains(node.RightChild)) {
                    stackTemp.push(node.RightChild);
                    continue;
                }
                if (node.Parent != null && !listRes.contains(node.Parent)) stackTemp.push(node.Parent);
            }
            if (order == 2) {
                if (node.RightChild != null && !listRes.contains(node.RightChild)) stackTemp.push(node.RightChild);
                if (node.LeftChild != null && !listRes.contains(node.LeftChild)) stackTemp.push(node.LeftChild);
            }
            listRes.add(node);
        }
        return listRes;
    }

    public void simMethod(BSTNode root, ArrayList<BSTNode> res) {
        if (root.LeftChild != null) {
            simMethod(root.LeftChild, res);
        }
        res.add(root);
        if (root.RightChild != null) {
            simMethod(root.RightChild, res);
        }
    }
}
