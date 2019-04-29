/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufma.rede;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author 2674
 * Classe DTO usada nas respostas as pesquisas por arquivos nos hosts
 */
public class CabecalhoArquivo implements Serializable {

    private int codigo = -1;
    private final String nome;
    private final String extensao;
    private final long tamanho;

    public CabecalhoArquivo(int codigo, String nome, String extensao, long tamanho) {
        this.codigo = codigo;
        this.nome = nome;
        this.extensao = extensao;
        this.tamanho = tamanho;
    }

    public CabecalhoArquivo(String nome, String extensao, long tamanho) {
        this.nome = nome;
        this.extensao = extensao;
        this.tamanho = tamanho;
    }

    public CabecalhoArquivo(File arquivo) {
        int indexOfExtensao = arquivo.getName().lastIndexOf('.');
        this.nome = arquivo.getName().substring(0, indexOfExtensao);
        this.extensao = arquivo.getName().substring(indexOfExtensao + 1);
        this.tamanho = arquivo.length();
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

    public long size() {
        return tamanho;
    }

    public double sizeInMB() {
        return ((double) (tamanho / (1024 * 1024)));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CabecalhoArquivo)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        CabecalhoArquivo other = (CabecalhoArquivo) obj;

        return this.nome.equals(other.getNome()) && this.extensao.equals(other.getExtensao());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.nome != null ? this.nome.hashCode() : 0);
        hash = 97 * hash + (this.extensao != null ? this.extensao.hashCode() : 0);
        hash = 97 * hash + (int) (this.tamanho ^ (this.tamanho >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "[" + codigo + "," + nome + "," + extensao + "," + tamanho + "]";
    }
}
