/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.kerberos;

import cliente.kerberos.exception.AcessoNegadoException;
import cliente.kerberos.exception.FalhaAutenticacaoException;

/**
 *
 * @author 2674
 */
public interface IAuteticacao {

    public void autenticar(String nomeUsuario, String senha) throws FalhaAutenticacaoException;

    public boolean isAutenticado(String nomeUsuario) throws AcessoNegadoException;
}
