/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.dominio;

import br.ufma.rede.Constantes;
import br.ufma.rede.download.Download;
import cliente.configuracao.ConfiguracaoAmbiente;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 2674
 */
public final class PesquisarArquivos {

    private PesquisarArquivos() {
    }

    public static List<Download> pesquisar(final String nomeArquivoProcurado) {

        Socket socket = null;
        ObjectOutputStream streamSaida = null;
        ObjectInputStream streamEntrada = null;
        List<Download> arquivosEncontrados = null;

        try {
            socket = new Socket(ConfiguracaoAmbiente.getNomeServidor(),
                    ConfiguracaoAmbiente.getPortaRecebimentoServidor());

            streamSaida = new ObjectOutputStream(socket.getOutputStream());
            streamSaida.writeUTF(Constantes.OPERACAO_PESQUISAR_ARQUIVO);
            streamSaida.flush();
            streamSaida.writeUTF(nomeArquivoProcurado);
            streamSaida.flush();

            streamEntrada = new ObjectInputStream(socket.getInputStream());
            arquivosEncontrados = (List<Download>) streamEntrada.readObject();

            return arquivosEncontrados;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PesquisarArquivos.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (UnknownHostException ex) {
            Logger.getLogger(PesquisarArquivos.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(PesquisarArquivos.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                if (streamEntrada != null) {
                    streamEntrada.close();
                }
                if (streamSaida != null) {
                    streamSaida.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(GerenteDownload.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
