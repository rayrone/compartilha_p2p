    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.dominio.threads;

import br.ufma.rede.Constantes;
import br.ufma.rede.download.Bloco;
import br.ufma.rede.download.Seed;
import cliente.dominio.GerenteDownload;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 2674
 */
public class ThreadCliente extends Thread {

    private String nomeArquivo;
    private String extensao;
    private File arquivo;
    private Seed seed;
    private boolean interrupted;

    public ThreadCliente(String nomeArquivo, String extensao, File arquivo,
            Seed seed) {
        this.nomeArquivo = nomeArquivo;
        this.extensao = extensao;
        this.arquivo = arquivo;
        this.seed = seed;
        this.interrupted = false;
    }

    @Override
    public void run() {

        Socket socket = null;
        ObjectInputStream streamEntrada = null;
        ObjectOutputStream streamSaida = null;
        RandomAccessFile streamSaidaArquivo = null;

        try {
            /* Conecta ao host que contém o arquivo desejado */
            socket = new Socket(seed.getIp(), seed.getPorta());

            /* Enviando para o servidor dados da solicitação */
            streamSaida = new ObjectOutputStream(socket.getOutputStream());
            streamSaida.writeUTF(Constantes.OPERACAO_DOWNLOAD);
            streamSaida.flush();
            streamSaida.writeUTF(arquivo.getName());
            streamSaida.flush();
            streamSaida.writeObject(seed.getBlocos());

            /* Cria a stream de saída para o arquivo baixado */
            streamSaidaArquivo = new RandomAccessFile(arquivo, "rw");

            /* Cria uma stream de entrada para obter blocos do servidor */
            streamEntrada = new ObjectInputStream(socket.getInputStream());

            /* Se o arquivo ainda não existe deve-se criá-lo*/
            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }

            for (Bloco bloco : seed.getBlocos()) {
                /* Testa se esta thread foi interrompida*/
                if (interrupted) {
                    System.err.println("Parou no bloco: " + bloco.getNumero());
                    break;
                }

                /* Ajusta a posição do ponteiro do arquivo para o offset do bloco*/
                streamSaidaArquivo.seek(bloco.getOffset());

                byte[] buffer = new byte[Constantes.TAMANHO_BUFFER];
                int qtdBytesLidos;
                int totalBytesLidos = 0;

                /* Recebe o fluxo de bytes do bloco */
                while (totalBytesLidos < bloco.getLength() && (qtdBytesLidos = streamEntrada.read(buffer)) != -1) {
                    streamSaidaArquivo.write(buffer, 0, qtdBytesLidos);
                    totalBytesLidos += qtdBytesLidos;
                }
                /* Se conseguiu escrever um bloco no arquivo então deve atualizar
                 * o arquivo que guarda a lista de blocos
                 */
                GerenteDownload.salvarBloco(nomeArquivo, extensao, bloco);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (streamEntrada != null) {
                    streamEntrada.close();
                    streamEntrada = null;
                }
                if (streamSaidaArquivo != null) {
                    streamSaidaArquivo.close();
                    streamSaida = null;
                }
            } catch (IOException ex) {
                Logger.getLogger(ThreadCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        interrupted = true;
    }

    public void pausar() {
        interrupt();
    }

    public void cancelar() {
        interrupt();
        arquivo.delete();
        arquivo = null;
    }

    @Override
    public boolean isInterrupted() {
        return super.isInterrupted();
    }
}

