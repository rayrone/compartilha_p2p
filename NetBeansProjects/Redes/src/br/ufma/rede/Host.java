/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufma.rede;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rayrone
 */
public final class Host implements Serializable {

    private final String ip;
    private final int portaEnvio;
    private final int portaRecebimento;
    private boolean online;
    private final List<CabecalhoArquivo> cabecalhos;

    public Host(final String ip, final int portaEnvio,
            final int portaRecebimento, boolean online) {
        this.ip = ip;
        this.portaEnvio = portaEnvio;
        this.portaRecebimento = portaRecebimento;
        this.online = online;
        this.cabecalhos = new ArrayList<CabecalhoArquivo>();
    }

    public Host(String ip, int portaEnvio, int portaRecebimento, boolean online, List<CabecalhoArquivo> cabecalhos) {
        this.ip = ip;
        this.portaEnvio = portaEnvio;
        this.portaRecebimento = portaRecebimento;
        this.online = online;
        this.cabecalhos = cabecalhos;
    }

    public String getIp() {
        return ip;
    }

    public int getPortaEnvio() {
        return portaEnvio;
    }

    public int getPortaRecebimento() {
        return portaRecebimento;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public List<CabecalhoArquivo> getCabecalhos() {
        return cabecalhos;
    }

    public void addCabecalho(CabecalhoArquivo cabecalho) {
        this.cabecalhos.add(cabecalho);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Host)) {
            return false;
        }

        Host other = (Host) obj;
        return this.ip.equals(other.ip);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.ip != null ? this.ip.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "[" + ip + ", " + portaEnvio + ", " + portaRecebimento + ", " + online + "]";
    }
}
