/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorp2pkerberos;

import br.ufma.rede.Host;
import br.ufma.rede.CabecalhoArquivo;
import br.ufma.rede.Constantes;
import br.ufma.rede.download.Bloco;
import br.ufma.rede.download.Download;
import br.ufma.rede.download.Seed;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidorp2pkerberos.configuracao.ConfiguracaoAmbiente;
import servidorp2pkerberos.persistencia.CabecalhoArquivoDAO;
import servidorp2pkerberos.persistencia.HostDAO;

/**
 *
 * @author 2674
 */
public class Servidor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /* Cria a configuração necessária para a execução do servidor */
        ConfiguracaoAmbiente.configurar();

        ServerSocket socketServidor = null;

        try {
            socketServidor = new ServerSocket(ConfiguracaoAmbiente.getPortaRecebimentoServidor());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Conjunto que armazenas todos os hosts que sincronizaram com o servidor*/

        while (true) {
            Socket socketCliente = null;
            try {
                /* Espera por novas conexões dos clientes */
                socketCliente = socketServidor.accept();

                new TrataSolicitacao(socketCliente).start();
            } catch (IOException e) {
                System.err.println("Erro na thread do cliente " + socketCliente.getInetAddress().getHostAddress() + "\nErro: " + e.getMessage());
            }
        }
    }
}

class TrataSolicitacao extends Thread {

    private final Socket socketCliente;

    public TrataSolicitacao(final Socket socketCliente) {
        super();
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream streamEntrada = new ObjectInputStream(socketCliente.getInputStream());
            ObjectOutputStream streamSaida = new ObjectOutputStream(socketCliente.getOutputStream());

            String operacao = streamEntrada.readUTF();

            if (operacao.equals(Constantes.OPERACAO_CONECTAR)) {
                tratarConexao(streamEntrada);
            } else if (operacao.equals(Constantes.OPERACAO_DESCONECTAR)) {
                tratarDesconexao(streamEntrada);
            } else if (operacao.equals(Constantes.OPERACAO_SINCRONIZAR)) {
                trartarSincronizacao(streamEntrada);
            } else if (operacao.equals(Constantes.OPERACAO_PESQUISAR_ARQUIVO)) {
                tratarPesquisa(streamEntrada, streamSaida);
            } else if (operacao.equals(Constantes.OPERACAO_AUTENTICAR)) {
                // TODO Implementar
            } else if (operacao.equals(Constantes.OPERACAO_DOWNLOAD)) {
                tratarRetomadaDownload(streamEntrada, streamSaida);
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tratarConexao(ObjectInputStream streamEntrada) {
        try {
            String ip = socketCliente.getInetAddress().getHostAddress();
            int portaEnvio = streamEntrada.readInt();
            int portaRecebimento = streamEntrada.readInt();
            List<CabecalhoArquivo> cabecalhos = (List<CabecalhoArquivo>) streamEntrada.readObject();

            /* Cria uma referência para armazenar o host a ser pesquisado */
            Host host = new Host(ip, portaEnvio, portaRecebimento, true, cabecalhos);

            /* Recupera uma instância do DAO do host */
            HostDAO dao = HostDAO.getInstance();

            try {
                /* Verifica se o host conectado no socket a thread atual está cadastrado*/
                if (dao.isExiste(ip)) {
                    /* Caso esteja cadastrado exclui todas as referências
                    a arquivos anteriores e salva as novas */
                    dao.atualizar(host);
                } else {
                    /* Caso não esteja cadastrado inclui o novo host com os seus
                    arquivos */
                    dao.inserir(host);
                }
            } catch (SQLException ex) {
                Logger.getLogger(TrataSolicitacao.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TrataSolicitacao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                streamEntrada.close();
                socketCliente.close();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void tratarDesconexao(ObjectInputStream streamEntrada) {
        try {
            String ip = socketCliente.getInetAddress().getHostAddress();
            HostDAO.getInstance().desconectar(ip);
            streamEntrada.close();
            socketCliente.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrataSolicitacao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TrataSolicitacao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void trartarSincronizacao(ObjectInputStream streamEntrada) {
        try {
            String ip = socketCliente.getInetAddress().getHostAddress();
            List<CabecalhoArquivo> cabecalhos = (List<CabecalhoArquivo>) streamEntrada.readObject();
            HostDAO.getInstance().atualizar(ip, cabecalhos);
            streamEntrada.close();
            socketCliente.close();
        } catch (IOException ex) {
            Logger.getLogger(TrataSolicitacao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TrataSolicitacao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tratarPesquisa(ObjectInputStream streamEntrada, ObjectOutputStream streamSaida) {

        try {
            /* Nome do arquivo solicitado pelo usuário */
            String nomeArquivo = streamEntrada.readUTF();

            /* Lista dos arquivos que tenham nome a palavra nomeArquivo */
            List<CabecalhoArquivo> cabecalhos = CabecalhoArquivoDAO.
                    getInstance().getCabecalhosByLikeNome(nomeArquivo);

            /* Cria lista que downloads que será enviada ao cliente */
            List<Download> downloads = new ArrayList<Download>();

            for (CabecalhoArquivo cabecalho : cabecalhos) {
                /* Recupera todos os hosts que tem um determinado arquivo */
                List<Host> hosts = HostDAO.getInstance().
                        getHostsOnlineByCodigoArquivo(cabecalho.getCodigo());

                /* Criar o download atual */
                Download download = new Download(cabecalho);

                /* Converte a lista de hosts em seeds e as adiciona ao download
                atual*/
                for (Host h : hosts) {
                    download.addSeed(new Seed(h));
                }

                /* Calcula a quantidade de seeds e blocos existentes */
                int qtdSeeds = download.quantidadeSeeds();
                int qtdBlocos = quantidadeDeBlocos(cabecalho.size());

                if (qtdSeeds > 0) {
                    /* Cria os blocos escolhendo os hosts aleatoriamente */
                    for (int i = 1; i < qtdBlocos; i++) {
                        Seed s = escolherHostAleatoriamente(download.getSeeds());
                        s.addBloco(new Bloco(i, (i - 1) * Constantes.TAMANHO_BLOCO, Constantes.TAMANHO_BLOCO));
                    }

                    /* O último bloco pode ter um tamanho menor que o tamanho
                    default */
                    long tamanhoUltimoSlot = cabecalho.size() - (qtdBlocos - 1) * Constantes.TAMANHO_BLOCO;

                    Seed s = escolherHostAleatoriamente(download.getSeeds());

                    s.addBloco(new Bloco(qtdBlocos, (qtdBlocos - 1) * Constantes.TAMANHO_BLOCO, tamanhoUltimoSlot));

                }
                downloads.add(download);
            }

            streamSaida.writeObject(downloads);
        } catch (IOException ex) {
            Logger.getLogger(TrataSolicitacao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tratarRetomadaDownload(ObjectInputStream streamEntrada, ObjectOutputStream streamSaida) {
        try {
            /* Recebe o código do arquivo solicitado pelo usuário */
            int codigoArquivo = streamEntrada.readInt();

            /* Recupera todos os hosts que tem um determinado arquivo */
            List<Host> hosts = HostDAO.getInstance().
                        getHostsOnlineByCodigoArquivo(codigoArquivo);

            List<Seed> seeds = new ArrayList<Seed>();

            for(Host h : hosts){
                seeds.add(new Seed(h));
            }

            /* Envia ao cliente todos os seeds que contém o arquivo */
            streamSaida.writeObject(seeds);
            
        } catch (IOException ex) {
            Logger.getLogger(TrataSolicitacao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* Método responsável por determinar a quantidade mínima de blocos que
     */
    private int quantidadeDeBlocos(long tamanhoArquivo) {
        long quantidadeSlots = tamanhoArquivo / Constantes.TAMANHO_BLOCO;
        if (tamanhoArquivo % Constantes.TAMANHO_BLOCO != 0) {
            quantidadeSlots++;
        }
        return (int) quantidadeSlots;
    }

    /* Método que escolhe aleatoriamente um host para ser um seed na
    solicitação de forma a minimizar a carga de upload nos hosts que irão ser
    servidores de arquivos */
    private Seed escolherHostAleatoriamente(List<Seed> seeds) {
        Collections.shuffle(seeds);
        return seeds.get(0);
    }
}
