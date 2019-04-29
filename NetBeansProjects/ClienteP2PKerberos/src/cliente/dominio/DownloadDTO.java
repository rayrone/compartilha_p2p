/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.dominio;

import br.ufma.rede.download.Bloco;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 2674
 * Classe responsável por guardar os dados retirados dos arquivos de retomada
 * de download.
 */
public class DownloadDTO {

    private final int codigo;
    private final String nome;
    private final String extensao;
    private final long tamanho;
    private final double progesso;
    private List<Bloco> blocos;
    private List<Bloco> blocosBaixados;

    public DownloadDTO(int codigo, String nome, String extensao, long tamanho, double progesso) {
        this.codigo = codigo;
        this.nome = nome;
        this.extensao = extensao;
        this.tamanho = tamanho;
        this.progesso = progesso;
        this.blocos = new ArrayList<Bloco>();
        this.blocosBaixados = new ArrayList<Bloco>();
    }

    public List<Bloco> getBlocos() {
        return blocos;
    }

    public List<Bloco> getBlocosBaixados() {
        return blocosBaixados;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getExtensao() {
        return extensao;
    }

    public String getNome() {
        return nome;
    }

    public double getProgesso() {
        return progesso;
    }

    public long getTamanho() {
        return tamanho;
    }

    public void setBlocos(List<Bloco> blocos) {
        this.blocos = blocos;
    }

    public void setBlocosBaixados(List<Bloco> blocosBaixados) {
        this.blocosBaixados = blocosBaixados;
    }

     public long size() {
        return tamanho;
    }

    public double sizeInMB() {
        return ((double) (tamanho / (1024 * 1024)));
    }
}
