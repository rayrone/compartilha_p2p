/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufma.rede.download;

import br.ufma.rede.CabecalhoArquivo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 2674
 */
public final class Download implements Serializable {

    private final CabecalhoArquivo cabecalhoArquivo;
    private final List<Seed> seeds;
    private double progesso;
    
    public Download(CabecalhoArquivo cabecalhoArquivo) {
        this.cabecalhoArquivo = cabecalhoArquivo;
        this.seeds = new ArrayList<Seed>();
    }

    public CabecalhoArquivo getCabecalhoArquivo() {
        return cabecalhoArquivo;
    }

    public List<Seed> getSeeds() {
        return seeds;
    }

    public double getProgesso() {
        return progesso;
    }

    public void setProgesso(double progesso) {
        this.progesso = progesso;
    }

    public int quantidadeSeeds() {
        return seeds.size();
    }

    public void addSeed(Seed seed) {
        seeds.add(seed);
    }

    public long size() {
        return cabecalhoArquivo.size();
    }

    public double sizeInMB() {
        return ((double) (cabecalhoArquivo.size() / (1024 * 1024)));
    }

    public List<Bloco> getBlocos() {
        List<Bloco> blocos = new ArrayList<Bloco>();
        for (Seed seed : seeds) {
            blocos.addAll(seed.getBlocos());
        }
        return blocos;
    }
}
