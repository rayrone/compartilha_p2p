/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.dominio;

import br.ufma.rede.Constantes;
import br.ufma.rede.download.Bloco;
import br.ufma.rede.download.Seed;
import cliente.configuracao.ConfiguracaoAmbiente;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rayrone
 */
public final class GerenteDownload {

    private static final String INICIO_ARQUIVO = "I-ARQUIVO";
    private static final String INICIO_BLOCO = "I-BLOCO";
    private static final String INICIO_BLOCO_BAIXADO = "I-BAIXADO";
    private static final String SEPARADOR = "|";

    private GerenteDownload() {
    }

    public static synchronized void salvarDownload(DownloadDTO download) throws IOException {
        File arquivo = getArquivoRetomada(download.getNome(), download.getExtensao());

        if (arquivo.exists()) {
            return;
        }

        arquivo.createNewFile();

        FileWriter fw = new FileWriter(arquivo, true);
        PrintWriter pw = new PrintWriter(fw, true);
        pw.println(INICIO_ARQUIVO);
        pw.println(doLayout(download));
        pw.println(INICIO_BLOCO);
        for (Bloco bloco : download.getBlocos()) {
            pw.print(doLayout(bloco));
        }
        pw.println();
        pw.println(INICIO_BLOCO_BAIXADO);
        for (Bloco bloco : download.getBlocosBaixados()) {
            pw.print(doLayout(bloco));
        }
        pw.close();
        fw.close();
    }

    public static synchronized void salvarBloco(String arquivo, String extensao,
            Bloco bloco) throws IOException {
        File arquivoFile = getArquivoRetomada(arquivo, extensao);
        FileWriter fw = new FileWriter(arquivoFile, true);
        PrintWriter pw = new PrintWriter(fw, true);
        pw.print(doLayout(bloco));
        pw.close();
        fw.close();
    }

    public static DownloadDTO recuperarDownload(String arquivo, String extensao) throws FileNotFoundException, IOException {
        File arquivoFile = getArquivoRetomada(arquivo, extensao);

        if(!arquivoFile.exists())
            return null;

        FileReader fr = new FileReader(arquivoFile);
        BufferedReader br = new BufferedReader(fr);

        String linha = null;

        DownloadDTO download = null;
        StringTokenizer strTok = null;

        while ((linha = br.readLine()) != null) {
            if (linha.equals(INICIO_ARQUIVO)) {
                linha = br.readLine();
                strTok = new StringTokenizer(linha, SEPARADOR, false);
                int codigoArquivo = Integer.parseInt(strTok.nextToken());
                download = toDownloadDTO(codigoArquivo, strTok);
                linha = br.readLine();
                linha = br.readLine();
                strTok = new StringTokenizer(linha);
                download.setBlocos(toListBlocos(strTok));
                linha = br.readLine();
                linha = br.readLine();
                strTok = new StringTokenizer(linha);
                download.setBlocosBaixados(toListBlocos(strTok));
            }
        }
        br.close();
        fr.close();
        return download;
    }

    public static List<DownloadDTO> recuperarDownloadsIncompletos() throws FileNotFoundException, IOException {
        File arquivo = new File(Constantes.PATH_PASTA_CONF);

        File[] arquivosRetomada = arquivo.listFiles();

        List<DownloadDTO> downloads = new ArrayList<DownloadDTO>();

        for (int i = 0; i < arquivosRetomada.length; i++) {

            if (arquivosRetomada[i].getName().equals("configuracao.properties")) {
                continue;
            }

            FileReader fr = new FileReader(arquivosRetomada[i]);
            BufferedReader br = new BufferedReader(fr);

            String linha = null;

            DownloadDTO download = null;
            StringTokenizer strTok = null;

            while ((linha = br.readLine()) != null) {
                if (linha.equals(INICIO_ARQUIVO)) {

                    linha = br.readLine();
                    strTok = new StringTokenizer(linha, SEPARADOR, false);
                    int codigo = Integer.parseInt(strTok.nextToken());

                    download = toDownloadDTO(codigo, strTok);
                    linha = br.readLine();
                    linha = br.readLine();
                    strTok = new StringTokenizer(linha);
                    download.setBlocos(toListBlocos(strTok));
                    linha = br.readLine();
                    linha = br.readLine();
                    strTok = new StringTokenizer(linha);
                    download.setBlocosBaixados(toListBlocos(strTok));

                    String pathArquivoBaixado = FileUtil.criarNomeArquivoCompleto(download.getNome(), download.getExtensao());
                    File arquivoBaixado = new File(pathArquivoBaixado);
                    if (arquivoBaixado.length() < download.size()) {
                        downloads.add(download);
                    }

                }
            }
            br.close();
            fr.close();
        }
        return downloads;
    }

    public static List<Bloco> calcularBlocosRestantes(DownloadDTO download) {
        List<Bloco> blocosRestantes = new ArrayList<Bloco>();

        for (Bloco bloco : download.getBlocos()) {
            boolean encontrou = false;
            for (Bloco blocoBaixado : download.getBlocosBaixados()) {
                /* Se o bloco foi baixado */
                if (bloco.equals(blocoBaixado)) {
                    encontrou = true;
                    break;
                }
            }
            if(!encontrou)
                blocosRestantes.add(bloco);
        }

        return blocosRestantes;
    }

    public static List<Seed> recuperarSeedsDoServidor(final int codigoArquivo) {
        Socket socket = null;
        ObjectOutputStream streamSaida = null;
        ObjectInputStream streamEntrada = null;
        List<Seed> seedsEncontrados = null;
        try {
            socket = new Socket(ConfiguracaoAmbiente.getNomeServidor(),
                    ConfiguracaoAmbiente.getPortaRecebimentoServidor());
            streamSaida = new ObjectOutputStream(socket.getOutputStream());
            streamSaida.writeUTF(Constantes.OPERACAO_DOWNLOAD);
            streamSaida.flush();
            streamSaida.writeInt(codigoArquivo);
            streamSaida.flush();

            streamEntrada = new ObjectInputStream(socket.getInputStream());
            seedsEncontrados = (List<Seed>) streamEntrada.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GerenteDownload.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(GerenteDownload.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GerenteDownload.class.getName()).log(Level.SEVERE, null, ex);
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

        return seedsEncontrados;
    }

    private static File getArquivoRetomada(String arquivo, String extensao) {
        String nomeArquivo = Constantes.PATH_PASTA_CONF + File.separatorChar +
                arquivo + "." + extensao + ".dat";
        return new File(nomeArquivo);
    }

    private static String doLayout(DownloadDTO download) {
        return download.getCodigo() + SEPARADOR + download.getNome() + SEPARADOR + download.getExtensao() + SEPARADOR + download.getTamanho() + SEPARADOR + download.getProgesso() + SEPARADOR;
    }

    private static String doLayout(Bloco bloco) {
        return bloco.getOffset() + " " + bloco.getLength() + " ";
    }

    private static DownloadDTO toDownloadDTO(int codigoArquivo, StringTokenizer strTok) {
        /* Lendo cabeçalho do arquivo*/
        String nome = strTok.nextToken();
        String extensao = strTok.nextToken();
        long tamanho = Long.parseLong(strTok.nextToken());
        double progresso = Double.parseDouble(strTok.nextToken());
        return new DownloadDTO(codigoArquivo, nome, extensao, tamanho, progresso);
    }

    private static List<Bloco> toListBlocos(StringTokenizer strTok) {
        List<Bloco> blocos = new ArrayList<Bloco>();
        while (strTok.hasMoreTokens()) {
            long offset = Long.parseLong(strTok.nextToken());
            long tamanho = Long.parseLong(strTok.nextToken());
            blocos.add(new Bloco(offset, tamanho));
        }
        return blocos;
    }
}
