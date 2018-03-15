package cdioil.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Classe que representa a Estrutura Mercadologica que agrega as categorias de produtos.
 *
 * @author António Sousa [1161371]
 */
public class EstruturaMercadologica {

    /**
     * Clase interna que representa um nó na estrutura mercadologica.
     * <p>
     * Um nó contém uma categoria, assim como referencias para outros nós.
     */
    protected static class Node {

        /**
         * Node que se situa acima do node atual na estrutura.
         */
        private Node pai;

        /**
         * Todos os nodes que se encontrem abaixo do atual.
         */
        private List<Node> filhos;

        /**
         * Categoria de produtos contida no node.
         */
        private Categoria elemento;

        protected Node(Node pai, Categoria elemento) {

            this.pai = pai;
            this.elemento = elemento;
            this.filhos = new LinkedList<>();
        }

        /**
         * Retorna a Categoria presente neste node.
         *
         * @return a categoria contida no node
         */
        public Categoria getElemento() {
            return elemento;
        }

        /**
         * Retorna o conjunto de nodes filhos deste node.
         *
         * @return conjunto de nodes filhos
         */
        public List<Node> getFilhos() {
            return filhos;
        }

        /**
         * Adiciona um node ao conjunto de filhos deste node.
         *
         * @param filho node que se pretende adicionar aos filhos
         * @return true - caso tenha sido possível adicionar ao conjunto de filhos<p>
         * false - caso contrario
         */
        public boolean addFilho(Node filho) {
            if (getFilhos().contains(filho)) {
                return false;
            }
            return filhos.add(filho);
        }

        /**
         * Gera um índice a partir da Categoria do Node.
         *
         * @return o valor de hash gerado
         */
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 79 * hash + Objects.hashCode(this.elemento);
            return hash;
        }

        /**
         * Compara o Node com outro objeto.
         *
         * @param obj Objeto a comparar
         * @return true, se os dois objetos tiverem a mesma Categoria. Caso contrário, retorna false
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Node other = (Node) obj;
            if (!Objects.equals(this.elemento, other.elemento)) {
                return false;
            }
            return true;
        }

    }
    /*----------------------------------------------------------
    -------------------FIM DA CLASSE NODE-----------------------*/

    /**
     * Primeiro node da estrutura mercadologica.
     */
    private Node raiz;
    /**
     * Tamanho da estrutura mercadologica (numero de nodes).
     */
    private int tamanho;

    /**
     * Construtor publico para incializar a Estrutura Mercadologica.
     */
    public EstruturaMercadologica() {

        raiz = new Node(null, new Categoria("Todos os Produtos", "-1"));
        tamanho = 1;
    }

    public Node getRaiz() {
        return raiz;
    }

    /**
     * Adiciona uma nova categoria principal à estrutura mercadologica.
     *
     * @param c nova categoria
     * @return true - se for possivel adicionar a categoria<p>
     * false - caso contrário
     */
    public boolean adicionarCategoriaRaiz(Categoria c) {

        if (c == null) {
            throw new IllegalArgumentException("O argumento não pode ser null");
        }

        return adicionarCategoria(raiz.getElemento(), c);
    }

    /**
     * Adiciona uma nova Categoria à Estrutura Mercadologica.
     *
     * @param pai a categoria acima da nova categoria
     * @param c a nova categoria
     * @return true - se for possivel adicionar a categoria<p>
     * false - caso contrário
     */
    public boolean adicionarCategoria(Categoria pai, Categoria c) {
        if (pai == null || c == null) {
            throw new IllegalArgumentException("O argumentos não podem ser null");
        }

        Node nodePai = procuraNode(raiz, pai);
        Node nodeFilho = procuraNode(raiz, c);

        if (nodePai != null && nodeFilho == null) {
            tamanho++;
            return nodePai.addFilho(new Node(nodePai, c));
        }

        return false;
    }

    /**
     * Remove a Categoria da Estrutura Mercadologica.
     *
     * @param c categoria a remover
     */
    public void removerCategoria(Categoria c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Método recursivo usado para para procurar uma dada Categoria na Estrutura Mercadologica.
     *
     * @param node node a partir do qual se pretende iniciar a procura
     * @param c Categoria a procurar
     * @return o node em que a categoria se encontra, null caso não seja encontrada
     */
    private Node procuraNode(Node node, Categoria c) {
        if (node == null) {
            return null;
        }

        if (node.getElemento().equals(c)) {
            return node;
        }

        List<Node> filhos = node.getFilhos();

        for (Node n : filhos) {
            return procuraNode(n, c);
        }

        return null;
    }

    /**
     * Método para verificar se um Node é uma folha da Estrutura Mercadologica.<p>
     * Uma Categoria folha é uma categoria que não alberga sub-categorias, podendo conter produtos.
     *
     * @param node node a verificar se é folha
     * @return true se o node não tiver nodes filhos, false caso tenha
     */
    private boolean isLeaf(Node node) {
        return node.filhos.isEmpty();
    }

    /**
     * Devolve uma coleção de todos os nodes da Estrutura Mercadologica que sejam folhas.
     *
     * @return
     */
    public Iterable<Categoria> getFolhas() {

        List<Categoria> folhas = new LinkedList<>();

        procuraFolhas(folhas, raiz);

        return folhas;
    }

    /**
     * Pesquisa recursivamente por todos os nodes que nao tenham filhos e adiciona-os a lista.
     *
     * @param folhas lista de nodes que não tenham nodes filhos
     * @param node node atual
     */
    private void procuraFolhas(List<Categoria> folhas, Node node) {

        List<Node> filhos = node.getFilhos();

        if (isLeaf(node)) {
            folhas.add(node.getElemento());
            return;
        }

        for (Node filho : filhos) {

            procuraFolhas(folhas, filho);
        }

    }

    /**
     * Adiciona um Produto a Categoria pretendida, podendo apenas adicionar-se a categorias que sejam folhas.
     *
     * @param p produto que se pretende adicionar
     * @param c categoria a qual se pretende adicionar o produto
     * @return true - se a categoria for uma folha<p>
     */
    public boolean adicionarProduto(Produto p, Categoria c) {

        if (p == null || c == null) {
            throw new IllegalArgumentException("Os argumentos não podem ser null");
        }

        Node node = procuraNode(raiz, c);

        if (isLeaf(node)) {
            return c.adicionarProduto(p);
        }

        return false;
    }

    public int tamanho() {
        return tamanho;
    }

    /**
     * Verifica se duas Categorias estao ligadas.
     *
     * @param pai categoria pai
     * @param filho categoria filho
     * @return true - se o pai estiver ligado e o filho ligado ao pai<p>
     * false - caso uma das ligacoes nao se verifique ou se uma das categorias nao se encontrar na estrutura mercadologica
     */
    public boolean verificaLigados(Categoria pai, Categoria filho) {

        if (pai == null || filho == null) {
            throw new IllegalArgumentException("Os argumentos não podem ser null");
        }

        Node nodePai = procuraNode(raiz, pai);

        Node nodeFilho = procuraNode(raiz, filho);

        if (nodePai == null || nodeFilho == null) {
            return false;
        }

        boolean eFilho = false;

        for (Node n : nodePai.filhos) {
            if (n == nodeFilho) {
                eFilho = true;
                break;
            }
        }

        return nodeFilho.pai == nodePai && eFilho;
    }

}
