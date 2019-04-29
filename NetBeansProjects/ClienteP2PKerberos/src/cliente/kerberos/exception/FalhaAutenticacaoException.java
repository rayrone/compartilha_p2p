/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.kerberos.exception;

/**
 *
 * @author 2674
 */
public class FalhaAutenticacaoException extends Exception {

    public FalhaAutenticacaoException(String msg) {
        super("Não foi possível fazer sua autenticação junto ao servidor informado. Verifique a sua conexão de rede ou tente novamente mais tarde. ");
    }
}
