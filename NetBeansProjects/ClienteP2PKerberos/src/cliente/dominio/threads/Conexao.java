/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.dominio.threads;

import br.ufma.rede.CabecalhoArquivo;
import br.ufma.rede.Constantes;
import cliente.configuracao.ConfiguracaoAmbiente;
import cliente.dominio.DownloadDTO;
import cliente.dominio.GerenteDownload;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rayrone
 * Classe responsável por atualizar o servidor com a lista de arquivos existentes neste host
 */
public final class Conexao {

    private Conexao() {
    }

    public static void conectar() {
        Socket socket = null;
        ObjectOutputStream streamSaida = null;
        try {
            socket = new Socket(ConfiguracaoAmbiente.getNomeServidor(),
                    ConfiguracaoAmbiente.getPortaRecebimentoServidor());
            streamSaida = new ObjectOutputStream(socket.getOutputStream());
            /* Envia para o servidor qual a operação requisitada */
            streamSaida.writeUTF(Constantes.OPERACAO_CONECTAR);
            /* Envia o número da porta que o host usa para envio de mensagens */
            streamSaida.writeInt(ConfiguracaoAmbiente.getPortaEnvioCliente());
            /* Envia o número da porta que o host usa para recebimento de mensagens */
            streamSaida.writeInt(ConfiguracaoAmbiente.getPortaRecebimentoCliente());
            streamSaida.flush();

            /* Início da sincronização de arquivos com o servidor */
            File pastaIncoming = new File(Constantes.PATH_PASTA_INCOMING);

            File[] arquivos = pastaIncoming.listFiles();
            List<CabecalhoArquivo> cabecalhos = new ArrayList<CabecalhoArquivo>();

            /* Percorre a lista de arquivos contidos na pasta incoming em
            busca de arquivos não ocultos e concluídos */
            for (int i = 0; i < arquivos.length; i++) {
                if (arquivos[i].isFile() && !arquivos[i].isHidden()) {
                    CabecalhoArquivo cabecalho = new CabecalhoArquivo(arquivos[i]);
                    DownloadDTO download = GerenteDownload.recuperarDownload(
                            cabecalho.getNome(), cabecalho.getExtensao());

                    if(download == null || arquivos[i].length() == download.size())
                        cabecalhos.add(cabecalho);
                }
            }

            /* Envia para o servidor a lista de cabeçalhos de arquivo do host */
            streamSaida.writeObject(cabecalhos);
            streamSaida.flush();

        } catch (UnknownHostException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void desconectar() {
        Socket socket = null;
        ObjectOutputStream streamSaida = null;
        try {
            socket = new Socket(ConfiguracaoAmbiente.getNomeServidor(),
                    ConfiguracaoAmbiente.getPortaRecebimentoServidor());
            streamSaida = new ObjectOutputStream(socket.getOutputStream());
            /* Envia para o servidor qual a operação requisitada */
            streamSaida.writeUTF(Constantes.OPERACAO_DESCONECTAR);
            streamSaida.flush();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sincronizar() {
        Socket socket = null;
        ObjectOutputStream streamSaida = null;
        try {
            socket = new Socket(ConfiguracaoAmbiente.getNomeServidor(),
                    ConfiguracaoAmbiente.getPortaRecebimentoServidor());
            streamSaida = new ObjectOutputStream(socket.getOutputStream());
            /* Envia para o servidor qual a operação requisitada */
            streamSaida.writeUTF(Constantes.OPERACAO_SINCRONIZAR);
            streamSaida.flush();

            /* Início da sincronização de arquivos com o servidor */
            File pastaIncoming = new File(Constantes.PATH_PASTA_INCOMING);

            File[] arquivos = pastaIncoming.listFiles();
            List<CabecalhoArquivo> cabecalhos = new ArrayList<CabecalhoArquivo>();

            /* Percorre a lista de arquivos contidos na pasta incoming em
            busca de arquivos não ocultos */
            for (int i = 0; i < arquivos.length; i++) {
                if (arquivos[i].isFile() && !arquivos[i].isHidden()) {
                    cabecalhos.add(new CabecalhoArquivo(arquivos[i]));
                }
            }

            /* Envia para o servidor a lista de cabeçalhos de arquivo do host */
            streamSaida.writeObject(cabecalhos);
            streamSaida.flush();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
