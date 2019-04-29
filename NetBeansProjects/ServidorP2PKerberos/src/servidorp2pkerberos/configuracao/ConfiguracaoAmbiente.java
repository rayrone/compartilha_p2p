/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorp2pkerberos.configuracao;

import br.ufma.rede.Constantes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rayrone
 * Classe utilitária que cria os arquivos de configuração
 */
public class ConfiguracaoAmbiente {

    private static Properties properties;
    private static final String KEY_SERVIDOR_PORTA_RECEBIMENTO = "SERVIDOR_PORTA";
    private static final String KEY_DEFAULT_BANCO_SERVIDOR_NOME = "DEFAULT_BANCO_SERVIDOR_NOME";
    private static final String KEY_DEFAULT_BANCO_SERVIDOR_PORTA = "DEFAULT_BANCO_SERVIDOR_PORTA";
    private static final String KEY_DEFAULT_BANCO_NOME = "DEFAULT_BANCO_NOME";
    private static final String KEY_DEFAULT_BANCO_USUARIO = "DEFAULT_BANCO_USUARIO";
    private static final String KEY_DEFAULT_BANCO_SENHA = "DEFAULT_BANCO_SENHA";

    private ConfiguracaoAmbiente() {
    }

    public static void configurar() {
        File pastaConf = new File(Constantes.PATH_PASTA_CONF);
        /* Cria a pasta conf caso a mesma não exista */
        pastaConf.mkdir();

        File arquivoConfiguracao = new File(Constantes.PATH_ARQUIVO_CONF);
        FileOutputStream streamSaida = null;
        FileInputStream streamEntrada = null;
        properties = new Properties();

        if (!arquivoConfiguracao.exists()) {
            try {
                arquivoConfiguracao.createNewFile();
                properties.put(KEY_SERVIDOR_PORTA_RECEBIMENTO, Constantes.DEFAULT_SERVIDOR_PORTA);
                properties.put(KEY_DEFAULT_BANCO_SERVIDOR_NOME, Constantes.DEFAULT_BANCO_SERVIDOR_NOME);
                properties.put(KEY_DEFAULT_BANCO_SERVIDOR_PORTA, Constantes.DEFAULT_BANCO_SERVIDOR_PORTA);
                properties.put(KEY_DEFAULT_BANCO_NOME, Constantes.DEFAULT_BANCO_NOME);
                properties.put(KEY_DEFAULT_BANCO_USUARIO, Constantes.DEFAULT_BANCO_USUARIO);
                properties.put(KEY_DEFAULT_BANCO_SENHA, Constantes.DEFAULT_BANCO_SENHA);
                streamSaida = new FileOutputStream(arquivoConfiguracao);
                properties.store(streamSaida, "");
            } catch (IOException ex) {
                Logger.getLogger(ConfiguracaoAmbiente.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Um erro ocorreu.\n Detalhes: " + ex.getMessage());
                System.exit(0);
            } finally {
                try {
                    streamSaida.close();
                } catch (IOException ex) {
                    Logger.getLogger(ConfiguracaoAmbiente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            try {
                streamEntrada = new FileInputStream(arquivoConfiguracao);
                properties.load(streamEntrada);
            } catch (IOException ex) {
                Logger.getLogger(ConfiguracaoAmbiente.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Um erro ocorreu.\n Detalhes: " + ex.getMessage());
                System.exit(0);
            } finally {
                try {
                    streamEntrada.close();
                } catch (IOException ex) {
                    Logger.getLogger(ConfiguracaoAmbiente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static String getValorProperties(String key) {
        return properties.getProperty(key);
    }

    public static int getPortaRecebimentoServidor() {
        return Integer.parseInt(properties.getProperty(KEY_SERVIDOR_PORTA_RECEBIMENTO));
    }

    public static String getNomeServidorBancoDados(){
        return properties.getProperty(KEY_DEFAULT_BANCO_SERVIDOR_NOME);
    }

    public static int getPortaServidorBancoDados(){
        return Integer.parseInt(properties.getProperty(KEY_DEFAULT_BANCO_SERVIDOR_PORTA));
    }

    public static String getNomeBancoDados(){
        return properties.getProperty(KEY_DEFAULT_BANCO_NOME);
    }

    public static String getUsuarioBancoDados(){
        return properties.getProperty(KEY_DEFAULT_BANCO_USUARIO);
    }

    public static String getSenhaBancoDados(){
        return properties.getProperty(KEY_DEFAULT_BANCO_SENHA);
    }
}
