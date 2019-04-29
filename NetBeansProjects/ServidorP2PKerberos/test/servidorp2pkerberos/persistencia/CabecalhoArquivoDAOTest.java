/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorp2pkerberos.persistencia;

import br.ufma.rede.CabecalhoArquivo;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import servidorp2pkerberos.configuracao.ConfiguracaoAmbiente;
import static org.junit.Assert.*;

/**
 *
 * @author Rayrone
 */
public class CabecalhoArquivoDAOTest {

    @Before
    public void setUp() {
        ConfiguracaoAmbiente.configurar();
    }

    @Test
    public void testGetInstance() {
        assertNotNull(CabecalhoArquivoDAO.getInstance());
    }

    @Test
    public void testGetCodigo() throws Exception {
    }

    @Test
    public void testGetCabecalhosByLikeNome() {
        String nomeArquivo = "tes";
        List<CabecalhoArquivo> result = CabecalhoArquivoDAO.getInstance().getCabecalhosByLikeNome(nomeArquivo);
        for (CabecalhoArquivo cabecalho : result) {
            System.out.println(cabecalho);
        }
    }
}