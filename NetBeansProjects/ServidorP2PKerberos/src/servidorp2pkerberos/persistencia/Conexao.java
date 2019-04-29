package servidorp2pkerberos.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;
import servidorp2pkerberos.configuracao.ConfiguracaoAmbiente;

/**
 * Classe para executar a conexï¿½o do Banco de dados.
 */
public final class Conexao {

    private static Conexao instance;
    private static final String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
    private static String url;
    private static String servidor;
    private static int porta;
    private static String database;
    private static String usuario;
    private static String senha;

    private Conexao(){
        servidor = ConfiguracaoAmbiente.getNomeServidorBancoDados();
        porta = ConfiguracaoAmbiente.getPortaServidorBancoDados();
        database = ConfiguracaoAmbiente.getNomeBancoDados();
        usuario = ConfiguracaoAmbiente.getUsuarioBancoDados();
        senha = ConfiguracaoAmbiente.getSenhaBancoDados();
        url = "jdbc:mysql://" + servidor + ":" + porta + "/" + database;
    }

    public static synchronized Conexao getInstance() {
        if(instance == null)
            instance = new Conexao();
        return instance;
    }

    public Connection conectar() {
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, usuario, senha);
            return conn;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível conectar ao banco de dados!\n Motivo: "
                    + e.getMessage() , "AVISO!", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

}
