/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufma.rede.download;

import java.io.Serializable;

/**
 *
 * @author 2674
 *
 * Estrutura que representa a menor parte de um arquivo de rede. Normalmente
 * os blocos tem um tamanho fixo de 512Kb, no entanto, em nos últimos blocos
 * podemos ter um tamanho menor.
 */
public class Bloco implements Serializable {

    /* Número que identifica unicamente um bloco em um arquivo */
    private int numero;
    private final long offset;
    private final long length;

    public Bloco(long offset, long length) {
        this.offset = offset;
        this.length = length;
    }

    public Bloco(int numero, long offset, long length) {
        this.numero = numero;
        this.offset = offset;
        this.length = length;
    }

    public long getLength() {
        return length;
    }

    public int getNumero() {
        return numero;
    }

    public long getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "[" + offset + "," + length + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Bloco)) {
            return false;
        }

        Bloco other = (Bloco) obj;

        return this.offset == other.offset && this.length == other.length;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.offset ^ (this.offset >>> 32));
        hash = 53 * hash + (int) (this.length ^ (this.length >>> 32));
        return hash;
    }
}
