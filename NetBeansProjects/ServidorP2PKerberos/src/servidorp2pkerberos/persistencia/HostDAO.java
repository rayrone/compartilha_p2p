/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorp2pkerberos.persistencia;

import br.ufma.rede.CabecalhoArquivo;
import br.ufma.rede.Host;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rayrone
 */
public final class HostDAO {

    private static HostDAO instance;
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    private HostDAO() {
    }

    public static synchronized HostDAO getInstance() {
        if (instance == null) {
            instance = new HostDAO();
        }
        return instance;
    }

    public void inserir(Host host) throws SQLException {
        try {
            conn = Conexao.getInstance().conectar();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("INSERT INTO hosts VALUES (?, ?, ?, ?)");
            ps.setString(1, host.getIp());
            ps.setInt(2, host.getPortaEnvio());
            ps.setInt(3, host.getPortaRecebimento());
            ps.setBoolean(4, host.isOnline());
            ps.executeUpdate();

            for (CabecalhoArquivo arquivo : host.getCabecalhos()) {
                int codigoArquivo = CabecalhoArquivoDAO.getInstance().getCodigo(arquivo);
                /* Caso o arquivo ainda não esteja cadastrado cadastra-o */
                if (codigoArquivo == -1) {
                    /* Inserindo novos arquivos */
                    ps = conn.prepareStatement("INSERT INTO arquivos(nome, " + "extensao, tamanho) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, arquivo.getNome());
                    ps.setString(2, arquivo.getExtensao());
                    ps.setLong(3, arquivo.size());
                    ps.executeUpdate();

                    rs = ps.getGeneratedKeys();
                    rs.next();
                    ps = conn.prepareStatement("INSERT INTO hosts_arquivos " + "VALUES(?, ?)");
                    ps.setString(1, host.getIp());
                    ps.setInt(2, rs.getInt(1));
                    ps.executeUpdate();
                }
            }

            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            conn.rollback();
            Logger.getLogger(HostDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close();
        }
    }

    public void atualizar(Host host) throws SQLException {
        try {
            conn = Conexao.getInstance().conectar();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("UPDATE hosts SET portae = ?, " + "portar = ?, online = ? WHERE ip = ?");
            ps.setInt(1, host.getPortaEnvio());
            ps.setInt(2, host.getPortaRecebimento());
            ps.setBoolean(3, true);
            ps.setString(4, host.getIp());
            ps.executeUpdate();

            /* Limpando os arquivos dos hosts */
            ps = conn.prepareStatement("DELETE FROM hosts_arquivos WHERE " + "hosts_ip = ?");
            ps.setString(1, host.getIp());
            ps.executeUpdate();

            for (CabecalhoArquivo arquivo : host.getCabecalhos()) {
                int codigoArquivo = CabecalhoArquivoDAO.getInstance().getCodigo(arquivo);
                /* Caso o arquivo ainda não esteja cadastrado cadastra-o */
                if (codigoArquivo == -1) {
                    ps = conn.prepareStatement("INSERT INTO arquivos(nome, " + "extensao, tamanho) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, arquivo.getNome());
                    ps.setString(2, arquivo.getExtensao());
                    ps.setLong(3, arquivo.size());
                    ps.executeUpdate();
                    rs = ps.getGeneratedKeys();
                    rs.next();
                    codigoArquivo = rs.getInt(1);
                }

                ps = conn.prepareStatement("INSERT INTO hosts_arquivos " + "VALUES(?, ?)");
                ps.setString(1, host.getIp());
                ps.setInt(2, codigoArquivo);
                ps.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(HostDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close();
        }
    }

    public List<Host> getHostsOnlineByCodigoArquivo(int codigoArquivo) {
        try {
            conn = Conexao.getInstance().conectar();
            ps = conn.prepareStatement("SELECT h.ip, h.portae, h.portar " + "FROM hosts h INNER JOIN hosts_arquivos ha ON " + "h.ip = ha.Hosts_ip WHERE h.online = 1 and " + "ha.Arquivos_codigo = ?");
            ps.setInt(1, codigoArquivo);
            rs = ps.executeQuery();

            List<Host> hosts = new ArrayList<Host>();

            while (rs.next()) {
                String ip = rs.getString(1);
                int portaEnvio = rs.getInt(2);
                int portaRecebimento = rs.getInt(3);
                hosts.add(new Host(ip, portaEnvio, portaRecebimento, true));
            }

            return hosts;
        } catch (SQLException ex) {
            Logger.getLogger(HostDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean isExiste(String ip) throws SQLException {
        try {
            conn = Conexao.getInstance().conectar();
            ps = conn.prepareStatement("SELECT COUNT(IP) FROM hosts WHERE ip = ?");
            ps.setString(1, ip);
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

    public void desconectar(String ip) throws SQLException {
        try {
            conn = Conexao.getInstance().conectar();
            ps = conn.prepareStatement("UPDATE hosts SET online = ? WHERE ip = ?");
            ps.setBoolean(1, false);
            ps.setString(2, ip);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(HostDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close();
        }
    }

    public void atualizar(String ip, List<CabecalhoArquivo> arquivos) {
        try {
            conn = Conexao.getInstance().conectar();
            conn.setAutoCommit(false);

            /* Limpando os arquivos dos hosts */
            ps = conn.prepareStatement("DELETE FROM hosts_arquivos WHERE " + "hosts_ip = ?");
            ps.setString(1, ip);
            ps.executeUpdate();

            for (CabecalhoArquivo arquivo : arquivos) {
                int codigoArquivo = CabecalhoArquivoDAO.getInstance().getCodigo(arquivo);
                /* Caso o arquivo ainda não esteja cadastrado cadastra-o */
                if (codigoArquivo == -1) {
                    ps = conn.prepareStatement("INSERT INTO arquivos(nome, " + 
                            "extensao, tamanho) VALUES (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, arquivo.getNome());
                    ps.setString(2, arquivo.getExtensao());
                    ps.setLong(3, arquivo.size());
                    ps.executeUpdate();
                    rs = ps.getGeneratedKeys();
                    rs.next();
                    codigoArquivo = rs.getInt(1);
                }
                ps = conn.prepareStatement("INSERT INTO hosts_arquivos " + "VALUES(?, ?)");
                ps.setString(1, ip);
                ps.setInt(2, codigoArquivo);
                ps.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(HostDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void close() throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (ps != null) {
            ps.close();
        }
        if (conn != null) {
            conn.close();
        }
    }
}
