/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.kerberos.exception;

/**
 *
 * @author 2674
 */
public class AcessoNegadoException extends Exception {

    public AcessoNegadoException() {
        super("Você não tem permissão para acessar o serviço/recurso solicitado");
    }
}
