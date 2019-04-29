/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorp2pkerberos.persistencia;

import br.ufma.rede.CabecalhoArquivo;
import br.ufma.rede.Host;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import servidorp2pkerberos.configuracao.ConfiguracaoAmbiente;
import static org.junit.Assert.*;

/**
 *
 * @author Rayrone
 */
public class HostDAOTest {

    @Before
    public void setUp() {
        ConfiguracaoAmbiente.configurar();
    }

    
    @Test
    public void testGetInstance() {
        HostDAO result = HostDAO.getInstance();
        assertNotNull("O método getInstance está retornando null", result);
    }

    
    @Test
    public void testInserir() throws SQLException {
        Host host = new Host("192.168.200.1", 9010, 9011, true);
        for (int i = 1; i <= 5; i++) {
            host.addCabecalho(new CabecalhoArquivo("teste" + i, "txt", 1000 * i));
        }
        HostDAO.getInstance().inserir(host);
    }

    
    @Test
    public void testAtualizar() throws SQLException{
        Host host = new Host("192.168.200.1", 8010, 8011, true);
        for (int i = 1; i <= 10; i++) {
            host.addCabecalho(new CabecalhoArquivo("teste" + i, "txt", 1000 * i));
        }
        HostDAO.getInstance().atualizar(host);
    }

    @Test
    public void testGetHostsOnlineByCodigoArquivo() throws SQLException{
        int codigoArquivo = 99;
        List<Host> hosts = HostDAO.getInstance().getHostsOnlineByCodigoArquivo(codigoArquivo);
        for(Host h : hosts){
            System.out.println(h);
        }
    }

    @Test
    public void testIsExiste() throws SQLException{
        assertEquals(true, HostDAO.getInstance().isExiste("192.168.200.1"));
    }

    @Test
    public void testDesconectar() throws SQLException{
        HostDAO.getInstance().desconectar("192.168.200.1");
    }

    @Test
    public void testAtualizarListaArquivos() throws SQLException{
        String ip = "192.168.200.1";
        List<CabecalhoArquivo> arquivos = new ArrayList<CabecalhoArquivo>();
        for (int i = 5; i <= 15; i++) {
            arquivos.add(new CabecalhoArquivo("teste" + i, "txt", 1000 * i));
        }
        HostDAO.getInstance().atualizar(ip, arquivos);
    }
}