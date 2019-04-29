/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.dominio;

import br.ufma.rede.Constantes;
import java.io.File;

/**
 *
 * @author Rayrone
 */
public abstract class FileUtil {

    public static String criarNomeArquivo(String nome, String extensao) {
        return nome + "." + extensao;
    }

    public static String criarNomeArquivoCompleto(String nome) {
        return Constantes.PATH_PASTA_INCOMING + File.separatorChar + nome;
    }

    public static String criarNomeArquivoCompleto(String nome, String extensao) {
        return Constantes.PATH_PASTA_INCOMING + File.separatorChar + nome + "." + extensao;
    }
    
}
