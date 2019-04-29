/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.dominio.excecoes;

/**
 *
 * @author Rayrone
 */
public class PastaIncomingNaoEncontradaException extends Exception {

    /**
     * Creates a new instance of <code>PastaIncomingNaoEncontradaException</code> without detail message.
     */
    public PastaIncomingNaoEncontradaException() {
        super("A pasta incoming não foi encontrada. Caso a mesma exista reinicie a  aplicação");
    }
}
