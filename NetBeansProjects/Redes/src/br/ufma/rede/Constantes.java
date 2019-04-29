/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufma.rede;

import java.io.File;

/**
 *
 * @author Rayrone
 */
public abstract class Constantes {

    /* Configuração Básica - Define valores para tamanho do buffer de
     bytes utilizado pelos sockets, pasta onde serão salvo os downloads e
     a pasta onde será salvo o arquivo de configuração */
    public static final int TAMANHO_BUFFER = 8192;
    public static final int TAMANHO_BLOCO = 524288;
    public static final String PATH_PASTA_INCOMING = "incoming";
    public static final String PATH_PASTA_CONF = "conf";
    public static final String PATH_ARQUIVO_CONF = PATH_PASTA_CONF + 
            File.separatorChar + "configuracao.properties";

    /* Configuração Default - Define valores default para as portas no cliente e
     servidor bem como o IP do servidor de arquivos */
    public static final String DEFAULT_CLIENTE_PORTA_ENVIO = "9010";
    public static final String DEFAULT_CLIENTE_PORTA_RECEBIMENTO = "9011";
    public static final String DEFAULT_SERVIDOR_NOME = "localhost";
    public static final String DEFAULT_SERVIDOR_PORTA = "9090";

    /* Operações disponíveis */
    public static final String OPERACAO_AUTENTICAR = "AUTENTICAR";
    public static final String OPERACAO_CONECTAR = "CONECTAR";
    public static final String OPERACAO_DESCONECTAR = "DESCONECTAR";
    public static final String OPERACAO_SINCRONIZAR = "SINCRONIZAR";
    public static final String OPERACAO_PESQUISAR_ARQUIVO = "PESQUISAR_ARQUIVO";
    public static final String OPERACAO_DOWNLOAD = "DOWNLOAD";

    /* Configuração do banco de dados */
    public static final String DEFAULT_BANCO_SERVIDOR_NOME = "localhost";
    public static final String DEFAULT_BANCO_SERVIDOR_PORTA = "3306";
    public static final String DEFAULT_BANCO_NOME = "CompartilhaDB";
    public static final String DEFAULT_BANCO_USUARIO = "root";
    public static final String DEFAULT_BANCO_SENHA = "123456";
    
}
