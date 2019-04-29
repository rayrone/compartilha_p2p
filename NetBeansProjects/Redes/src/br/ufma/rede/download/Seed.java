/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufma.rede.download;

import br.ufma.rede.Host;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 2674
 */
public final class Seed implements Serializable {

    private final String ip;
    private final int porta;
    private final List<Bloco> blocos;

    public Seed(Host host) {
        this.ip = host.getIp();
        this.porta = host.getPortaRecebimento();
        this.blocos = new ArrayList<Bloco>();
    }

    public List<Bloco> getBlocos() {
        return blocos;
    }

    public String getIp() {
        return ip;
    }

    public int getPorta() {
        return porta;
    }

    public void addBloco(Bloco bloco){
        blocos.add(bloco);
    }
}
