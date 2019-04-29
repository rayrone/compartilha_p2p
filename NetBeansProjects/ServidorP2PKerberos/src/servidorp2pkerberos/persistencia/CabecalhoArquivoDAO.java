/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorp2pkerberos.persistencia;

import br.ufma.rede.CabecalhoArquivo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rayrone
 */
public class CabecalhoArquivoDAO {

    private static CabecalhoArquivoDAO instance;
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    private CabecalhoArquivoDAO() {
    }

    public static synchronized CabecalhoArquivoDAO getInstance() {
        if (instance == null) {
            instance = new CabecalhoArquivoDAO();
        }
        return instance;
    }

    public int getCodigo(CabecalhoArquivo cabecalhoArquivo) throws SQLException {
        try {
            conn = Conexao.getInstance().conectar();
            ps = conn.prepareStatement("SELECT codigo FROM arquivos WHERE " +
                    "nome = ? and extensao = ?");
            ps.setString(1, cabecalhoArquivo.getNome());
            ps.setString(2, cabecalhoArquivo.getExtensao());
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } catch (SQLException ex) {
            Logger.getLogger(CabecalhoArquivoDAO.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } finally {
            close();
        }
    }

    public List<CabecalhoArquivo> getCabecalhosByLikeNome(String nomeArquivo) {
        try {
            conn = Conexao.getInstance().conectar();
            ps = conn.prepareStatement("SELECT * FROM arquivos WHERE nome LIKE " +
                    "'%" + nomeArquivo + "%' ORDER BY nome");
            rs = ps.executeQuery();
            List<CabecalhoArquivo> cabecalhos = new ArrayList<CabecalhoArquivo>();
            while (rs.next()) {
                int codigo = rs.getInt(1);
                String nome = rs.getString(2);
                String extensao = rs.getString(3);
                long tamanho = rs.getLong(4);
                cabecalhos.add(new CabecalhoArquivo(codigo, nome, extensao, tamanho));
            }

            return cabecalhos;
        } catch (SQLException ex) {
            Logger.getLogger(CabecalhoArquivoDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally{
            try {
                close();
            } catch (SQLException ex) {
                Logger.getLogger(CabecalhoArquivoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isExiste(String nome, String extensao) throws SQLException {
        try {
            conn = Conexao.getInstance().conectar();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM arquivos " +
                    "WHERE nome = ? and extensao = ?");
            ps.setString(1, nome);
            ps.setString(1, extensao);
            rs = ps.executeQuery();
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException ex) {
            Logger.getLogger(HostDAO.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        } finally {
            close();
        }
    }
    private void close() throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (ps != null) {
            ps.close();
        }
    }
}
