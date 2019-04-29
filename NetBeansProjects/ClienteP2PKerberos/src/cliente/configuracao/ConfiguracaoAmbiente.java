/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.configuracao;

import br.ufma.rede.Constantes;
import cliente.ClienteApp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Rayrone
 * Classe utilitária que cria as pastas e arquivos de configuração
 */
public final class ConfiguracaoAmbiente {

    private static Properties properties;
    private static final String KEY_CLIENTE_PORTA_ENVIO = "CLIENTE_PORTA_ENVIO";
    private static final String KEY_CLIENTE_PORTA_RECEBIMENTO = "CLIENTE_PORTA_RECEBIMENTO";
    private static final String KEY_SERVIDOR_NOME = "SERVIDOR_NOME";
    private static final String KEY_SERVIDOR_PORTA_RECEBIMENTO = "SERVIDOR_PORTA";

    private ConfiguracaoAmbiente() {
    }

    public static void configurar() {
        File pastaIncoming = new File(Constantes.PATH_PASTA_INCOMING);
        /* Cria a pasta incoming caso a mesma não exista */
        pastaIncoming.mkdir();

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
                properties.put(KEY_CLIENTE_PORTA_ENVIO, Constantes.DEFAULT_CLIENTE_PORTA_ENVIO);
                properties.put(KEY_CLIENTE_PORTA_RECEBIMENTO, Constantes.DEFAULT_CLIENTE_PORTA_RECEBIMENTO);
                properties.put(KEY_SERVIDOR_NOME, Constantes.DEFAULT_SERVIDOR_NOME);
                properties.put(KEY_SERVIDOR_PORTA_RECEBIMENTO, Constantes.DEFAULT_SERVIDOR_PORTA);
                streamSaida = new FileOutputStream(arquivoConfiguracao);
                properties.store(streamSaida, "");
            } catch (IOException ex) {
                Logger.getLogger(ClienteApp.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Um erro ocorreu", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Um erro ocorreu", JOptionPane.ERROR_MESSAGE);
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

    public static String getValorConfiguracao(String key) {
        return properties.getProperty(key);
    }

    public static String getNomeServidor() {
        return properties.getProperty(KEY_SERVIDOR_NOME);
    }

    public static int getPortaRecebimentoServidor() {
        return Integer.parseInt(properties.getProperty(KEY_SERVIDOR_PORTA_RECEBIMENTO));
    }

    public static int getPortaRecebimentoCliente() {
        return Integer.parseInt(properties.getProperty(KEY_CLIENTE_PORTA_RECEBIMENTO));
    }

    public static int getPortaEnvioCliente() {
        return Integer.parseInt(properties.getProperty(KEY_CLIENTE_PORTA_ENVIO));
    }
}
