/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.dominio.threads;

import br.ufma.rede.Constantes;
import br.ufma.rede.download.Bloco;
import cliente.configuracao.ConfiguracaoAmbiente;
import cliente.dominio.FileUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rayrone
 *
 * Thread responsável por implementar o papel de servidor de arquivos para
 * outros hosts
 */
public class ThreadServidor extends Thread {

    @Override
    public void run() {
        ServerSocket socketServidor = null;

        try {
            socketServidor = new ServerSocket(ConfiguracaoAmbiente.getPortaRecebimentoCliente());
        } catch (IOException e) {
            System.err.println("A porta escolhida já está sendo utilizada.");
            e.printStackTrace();
        }

        while (true) {
            try {
                Socket socketCliente = socketServidor.accept();

                ObjectInputStream streamEntrada = new ObjectInputStream(socketCliente.getInputStream());
                String operacao = streamEntrada.readUTF();

                if (operacao.equals(Constantes.OPERACAO_DOWNLOAD)) {
                    new ThreadEnviarBlocos(socketCliente, streamEntrada).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/*
 * Thread que realiza o envio dos bytes que compõem o bloco de arquivo
 */
class ThreadEnviarBlocos extends Thread {

    private Socket socketCliente;
    private ObjectInputStream streamEntrada;

    public ThreadEnviarBlocos(Socket socketCliente, ObjectInputStream streamEntrada) {
        this.socketCliente = socketCliente;
        this.streamEntrada = streamEntrada;
    }

    @Override
    public void run() {
        super.run();

        ObjectOutputStream streamSaida = null;
        RandomAccessFile randomAccessFile = null;

        try {
            /* Recebe do cliente o nome do arquivo a ser enviado */
            String nomeArquivo = streamEntrada.readUTF();

            /* Recebe a lista de blocos que o cliente deseja receber */
            List<Bloco> blocos = (List<Bloco>) streamEntrada.readObject();

            File arquivoSolicitado = new File(FileUtil.criarNomeArquivoCompleto(nomeArquivo));
            randomAccessFile = new RandomAccessFile(arquivoSolicitado, "r");
            streamSaida = new ObjectOutputStream(socketCliente.getOutputStream());

            for (Bloco bloco : blocos) {
                randomAccessFile.seek(bloco.getOffset());

                int tamanhoBuffer = socketCliente.getReceiveBufferSize();
                byte[] bufferEnvio = new byte[tamanhoBuffer];
                int qtdBytesLidos, totalBytesLidos = 0;

                /* Envia o fluxo de bytes do slot para o host solicitante */
                while (totalBytesLidos < bloco.getLength() && (qtdBytesLidos =
                        randomAccessFile.read(bufferEnvio)) != -1) {
                    streamSaida.write(bufferEnvio, 0, qtdBytesLidos);
                    totalBytesLidos += qtdBytesLidos;
                }
                streamSaida.flush();
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ThreadEnviarBlocos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ThreadEnviarBlocos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(ThreadEnviarBlocos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ThreadEnviarBlocos.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (streamEntrada != null) {
                    streamEntrada.close();
                    streamEntrada = null;
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                    randomAccessFile = null;
                }
            } catch (IOException ex) {
                Logger.getLogger(ThreadEnviarBlocos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

