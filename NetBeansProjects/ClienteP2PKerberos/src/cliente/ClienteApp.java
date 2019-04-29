
/*
 * ClienteApp.java
 */
package cliente;

import cliente.configuracao.ConfiguracaoAmbiente;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class ClienteApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        /* Invoca a classe utilitária para configurar o ambiente */
        ConfiguracaoAmbiente.configurar();

        show(new ClienteView(this));
    }

   /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of ClienteApp
     */
    public static ClienteApp getApplication() {
        return Application.getInstance(ClienteApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(ClienteApp.class, args);
    }
}
