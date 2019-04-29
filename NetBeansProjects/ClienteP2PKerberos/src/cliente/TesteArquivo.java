/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import br.ufma.rede.CabecalhoArquivo;
import br.ufma.rede.download.Bloco;
import cliente.dominio.DownloadDTO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Rayrone
 */
public class TesteArquivo {

    private static final String INICIO_ARQUIVO = "I-ARQUIVO";
    private static final String INICIO_BLOCO = "I-BLOCO";
    private static final String INICIO_BLOCO_BAIXADO = "I-BAIXADO";

    private static String doLayout(CabecalhoArquivo cabecalhoArquivo, double progresso) {
        return cabecalhoArquivo.getCodigo() + " " + cabecalhoArquivo.getNome() + " " + cabecalhoArquivo.getExtensao() + " " + cabecalhoArquivo.size() + " " + progresso + " ";
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

    public static DownloadDTO recuperarDownload(File arquivo, int codigoArquivo) throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(arquivo);
        BufferedReader br = new BufferedReader(fr);

        String linha = null;

        DownloadDTO download = null;
        StringTokenizer strTok = null;

        while ((linha = br.readLine()) != null) {
            if (linha.equals(INICIO_ARQUIVO)) {

                linha = br.readLine();
                strTok = new StringTokenizer(linha);
                int codigo = Integer.parseInt(strTok.nextToken());

                if (codigo == codigoArquivo) {
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
        }
        br.close();
        fr.close();
        return download;
    }

    public static List<DownloadDTO> recuperarDownloadIncompletos(File arquivo, int codigoArquivo) throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(arquivo);
        BufferedReader br = new BufferedReader(fr);

        String linha = null;

        List<DownloadDTO> downloads = new ArrayList<DownloadDTO>();
        DownloadDTO download = null;
        StringTokenizer strTok = null;

        while ((linha = br.readLine()) != null) {
            if (linha.equals(INICIO_ARQUIVO)) {

                linha = br.readLine();
                strTok = new StringTokenizer(linha);
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
                if (download.getProgesso() < 100) {
                    downloads.add(download);
                }
            }
        }
        br.close();
        fr.close();

        return downloads;
    }

    public static void salvarDownload(DownloadDTO download) throws IOException {
       
    }

    public static void main(String args[]) throws FileNotFoundException, IOException {
        String nomeArquivo = "C:\\conf.dat";

        File arquivo = new File(nomeArquivo);

        if (!arquivo.exists()) {
            arquivo.createNewFile();
        }

        int codigoArquivo = (int) arquivo.length();

        CabecalhoArquivo cabecalhoArquivo = new CabecalhoArquivo(
                codigoArquivo, "celine", "wmv", 1024);

        double progresso = 15.26;
        Bloco b1 = new Bloco(1, 0, 1024);

        FileWriter fw = new FileWriter(arquivo, true);
        PrintWriter pw = new PrintWriter(fw, true);
        pw.println(INICIO_ARQUIVO);
        pw.println(doLayout(cabecalhoArquivo, progresso));
        pw.println(INICIO_BLOCO);
        for (int i = 1; i <= 10; i++) {
            pw.print(doLayout(b1));
        }
        pw.println();
        pw.println(INICIO_BLOCO_BAIXADO);
        for (int i = 1; i <= 10; i++) {
            pw.print(doLayout(b1));
        }
        pw.println();

        pw.close();
        fw.close();

//        FileReader fr = new FileReader(arquivo);
//        BufferedReader br = new BufferedReader(fr);
//
//        String linha = null;
//
//        while ((linha = br.readLine()) != null) {
//            System.out.println(linha);
//        }
//
//        br.close();
//        fr.close();

//        DownloadDTO d = recuperarDownload(arquivo, 0);
//
//        System.out.println(d.getCodigo() + " " + d.getNome() + " "
//                + d.getExtensao() + " " + d.getTamanho() + " " + d.getProgesso());
//
//        if (d.getBlocos() != null) {
//            System.out.println("Blocos");
//            for (Bloco b : d.getBlocos()) {
//                System.out.println(b.getOffset() + " " + b.getLength());
//            }
//        }
//        if (d.getBlocosBaixados() != null) {
//            System.out.println("Blocos Baixados");
//            for (Bloco b : d.getBlocosBaixados()) {
//                System.out.println(b.getOffset() + " " + b.getLength());
//            }
//        }

        List<DownloadDTO> downloads = recuperarDownloadIncompletos(arquivo, codigoArquivo);

        for (DownloadDTO download : downloads) {
            System.out.println(download.getCodigo() + " " + download.getNome() + " " + download.getExtensao() + " " + download.getTamanho() + " " + download.getProgesso());

            if (download.getBlocos() != null) {
                System.out.println("Blocos");
                for (Bloco b : download.getBlocos()) {
                    System.out.println(b);
                }
            }
            if (download.getBlocosBaixados() != null) {
                System.out.println("Blocos Baixados");
                for (Bloco b : download.getBlocosBaixados()) {
                    System.out.println(b);
                }
            }

            List<Bloco> blocosRestantes = new ArrayList<Bloco>();

            System.out.println("Blocos Restantes");
            for (Bloco bloco : download.getBlocos()) {
                for (Bloco blocoBaixado : download.getBlocosBaixados()) {
                    /* Se o bloco não foi baixado */
                    if (!bloco.equals(blocoBaixado)) {
                        blocosRestantes.add(bloco);
                    }
                }
            }

            for (Bloco bloco : blocosRestantes) {
                System.out.println(bloco);
            }
        }

    }
}
