package cdioil.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Representa uma categoria de produtos existente na Estrutura Mercadologica.
 */
@Entity
public class Categoria implements Serializable {

    /**
     * Código de serialização.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_CATEGORIA", nullable = false, updatable = false)
    private Long id;
    /**
     * Designação da Categoria.
     */
    String designacao;

    /**
     * Identificador da Categoria (descritivo + sufixo).
     */
    String descritivo;

    /**
     * Conjunto de produtos contidos nesta categoria.
     */
    @OneToMany
    private Set<Product> produtos = new HashSet<>();

    /**
     * Constante que representa a expressão regular para a validação dos descritivos.
     */
    private final static String REGEX_DESCRITIVO = "-?[0-9]+(" + Sufixos.SUFIXO_CAT + "|" + Sufixos.SUFIXO_SCAT
            + "|" + Sufixos.SUFIXO_UB + "|" + Sufixos.SUFIXO_UN + "|" + Sufixos.SUFIXO_DC + ")";

    /**
     * Sufixos possíveis para o descritivo (DC, UN, CAT, SCAT ou UB).
     */
    public enum Sufixos {
        SUFIXO_DC() {
            @Override
            public String toString() {
                return "DC";
            }
        },
        SUFIXO_UN() {
            @Override
            public String toString() {
                return "UN";
            }
        },
        SUFIXO_CAT() {
            @Override
            public String toString() {
                return "CAT";
            }
        },
        SUFIXO_SCAT() {
            @Override
            public String toString() {
                return "SCAT";
            }
        },
        SUFIXO_UB() {
            @Override
            public String toString() {
                return "UB";
            }
        };
    }

    /**
     * Construtor protegido apenas para uso do JPA.
     */
    protected Categoria() {
    }

    /**
     * Cria uma instância de Categoria, recebendo a sua designação e o descritivo.
     *
     * @param designacao Desginação da Categoria
     * @param descritivo Descritivo da Categoria
     */
    public Categoria(String designacao, String descritivo) {
        if (isDesignacaoValida(designacao) && isDescritivoValido(descritivo)) {
            this.designacao = designacao;
            this.descritivo = descritivo;
            produtos = new HashSet<>();
        } else {
            throw new IllegalArgumentException("Dados de entrada inválidos.");
        }
    }

    /**
     * Verifica se a designação da Categoria é válido.
     *
     * @param designacao String a confirmar
     * @return true, caso a designação seja válido. Caso contrário, retorna false
     */
    private boolean isDesignacaoValida(String designacao) {
        return designacao != null
                && !designacao.trim().isEmpty();
    }

    /**
     * Verifica se o descritivo da Categoria é válido.
     *
     * @param descritivo String a confirmar
     * @return true, caso o descritivo seja válido. Caso contrário, retorna false
     */
    private boolean isDescritivoValido(String descritivo) {
        return descritivo != null && descritivo.matches(REGEX_DESCRITIVO);
    }

    /**
     * Adiciona um produto ao conjunto de produtos desta Categoria.
     *
     * @param p produto a adicionar
     * @return true - se o produto tiver sido adicionado com sucesso<p>
     * false - caso contrário
     */
    public boolean adicionarProduto(Product p) {
        if (p == null) {
            return false;
        }
        return produtos.add(p);
    }

    /**
     * Descreve a Categoria através do seu designacao e descritivo.
     *
     * @return a descrição textual da Categoria.
     */
    @Override
    public String toString() {
        return String.format("Nome: %s\nDescritivo: %s\n", designacao, descritivo);
    }

    /**
     * Gera um índice a partir do dos atributos da Categoria.
     *
     * @return o valor de hash gerado
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.designacao);
        hash = 29 * hash + Objects.hashCode(this.descritivo);
        return hash;
    }

    /**
     * Compara a Categoria com outro objeto.
     *
     * @param obj Objeto a comparar
     * @return true, se os dois objetos tiverem os mesmos atributos. Caso contrário, retorna false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Categoria other = (Categoria) obj;
        if (!Objects.equals(this.designacao, other.designacao)) {
            return false;
        }
        if (!Objects.equals(this.descritivo, other.descritivo)) {
            return false;
        }
        return true;
    }
}
