/*
 * ClienteView.java
 */
package cliente;

import br.ufma.rede.download.Bloco;
import br.ufma.rede.download.Download;
import br.ufma.rede.download.Seed;
import cliente.dominio.DownloadDTO;
import cliente.dominio.FileUtil;
import cliente.dominio.GerenteDownload;
import cliente.dominio.PesquisarArquivos;
import cliente.dominio.StatusDownload;
import cliente.dominio.threads.Conexao;
import cliente.dominio.threads.ThreadCliente;
import cliente.dominio.threads.ThreadServidor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * The application's main frame.
 */
public class ClienteView extends FrameView {

    public ClienteView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        /* Minha inicialização */
        tablePesquisa.getSelectionModel().addListSelectionListener(
                new TablePesquisarBotaoIniciarListSelectionListener());

        tableDownloads.getSelectionModel().addListSelectionListener(
                new TableDownloadBotaoPausarListSelectionListener());

        tableDownloads.getSelectionModel().addListSelectionListener(
                new TableDownloadBotaoCancelarListSelectionListener());

        inicializarTabelaDownloads();

        /* Inicia a conexão com o servidor de arquivos */
        Conexao.conectar();

//        /* Inicia a sincronização com o servidor de arquivos */
//        Conexao.sincronizar();

        /* Inicia o host como um servidor de arquivo */
        new ThreadServidor().start();

        /* Evento para desconectar o host quando a janela for fechada */
        this.getFrame().addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                Conexao.desconectar();
            }
        });

    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = ClienteApp.getApplication().getMainFrame();
            aboutBox = new ClienteAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        ClienteApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePesquisa = new javax.swing.JTable();
        txtPesquisa = new javax.swing.JTextField();
        btIniciarDownload = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableDownloads = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        btPesquisar = new javax.swing.JButton();
        btCancelarDownload = new javax.swing.JButton();
        btPausarDownload = new javax.swing.JButton();
        barraMenu = new javax.swing.JMenuBar();
        javax.swing.JMenu menuArquivo = new javax.swing.JMenu();
        javax.swing.JMenuItem menuItemSair = new javax.swing.JMenuItem();
        javax.swing.JMenu menuAjuda = new javax.swing.JMenu();
        javax.swing.JMenuItem menuItemSobre = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(800, 500));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tablePesquisa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Arquivo", "Extensão", "Tamanho (MB)", "Seeds"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablePesquisa.setName("tablePesquisa"); // NOI18N
        tablePesquisa.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(tablePesquisa);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(cliente.ClienteApp.class).getContext().getResourceMap(ClienteView.class);
        tablePesquisa.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablePesquisa.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tablePesquisa.columnModel.title0")); // NOI18N
        tablePesquisa.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tablePesquisa.columnModel.title1")); // NOI18N
        tablePesquisa.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tablePesquisa.columnModel.title2")); // NOI18N
        tablePesquisa.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tablePesquisa.columnModel.title3")); // NOI18N

        txtPesquisa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPesquisa.setText(resourceMap.getString("txtPesquisa.text")); // NOI18N
        txtPesquisa.setName("txtPesquisa"); // NOI18N
        txtPesquisa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPesquisaFocusGained(evt);
            }
        });
        txtPesquisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPesquisaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPesquisaKeyTyped(evt);
            }
        });

        btIniciarDownload.setText(resourceMap.getString("btIniciarDownload.text")); // NOI18N
        btIniciarDownload.setEnabled(false);
        btIniciarDownload.setName("btIniciarDownload"); // NOI18N
        btIniciarDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btIniciarDownloadActionPerformed(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tableDownloads.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Arquivo", "Extensão", "Tamanho (MB)", "Status", "Progresso (%)", "Seeds"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableDownloads.setName("tableDownloads"); // NOI18N
        tableDownloads.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tableDownloads);
        tableDownloads.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableDownloads.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tableDownloads.columnModel.title0")); // NOI18N
        tableDownloads.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tableDownloads.columnModel.title1")); // NOI18N
        tableDownloads.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tableDownloads.columnModel.title2")); // NOI18N
        tableDownloads.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tableDownloads.columnModel.title3")); // NOI18N
        tableDownloads.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tableDownloads.columnModel.title4")); // NOI18N
        tableDownloads.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tableDownloads.columnModel.title5")); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        btPesquisar.setText(resourceMap.getString("btPesquisar.text")); // NOI18N
        btPesquisar.setEnabled(false);
        btPesquisar.setName("btPesquisar"); // NOI18N
        btPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPesquisarActionPerformed(evt);
            }
        });

        btCancelarDownload.setText(resourceMap.getString("btCancelarDownload.text")); // NOI18N
        btCancelarDownload.setEnabled(false);
        btCancelarDownload.setName("btCancelarDownload"); // NOI18N
        btCancelarDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarDownloadActionPerformed(evt);
            }
        });

        btPausarDownload.setText(resourceMap.getString("btPausarDownload.text")); // NOI18N
        btPausarDownload.setEnabled(false);
        btPausarDownload.setName("btPausarDownload"); // NOI18N
        btPausarDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPausarDownloadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(btPausarDownload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btCancelarDownload))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 702, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 702, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btPesquisar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btIniciarDownload)))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btIniciarDownload, btPesquisar});

        mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btCancelarDownload, btPausarDownload});

        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCancelarDownload)
                    .addComponent(btPausarDownload))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btPesquisar)
                    .addComponent(btIniciarDownload))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        mainPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btCancelarDownload, btPausarDownload});

        barraMenu.setName("barraMenu"); // NOI18N

        menuArquivo.setText(resourceMap.getString("menuArquivo.text")); // NOI18N
        menuArquivo.setName("menuArquivo"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(cliente.ClienteApp.class).getContext().getActionMap(ClienteView.class, this);
        menuItemSair.setAction(actionMap.get("quit")); // NOI18N
        menuItemSair.setText(resourceMap.getString("menuItemSair.text")); // NOI18N
        menuItemSair.setToolTipText(resourceMap.getString("menuItemSair.toolTipText")); // NOI18N
        menuItemSair.setName("menuItemSair"); // NOI18N
        menuArquivo.add(menuItemSair);

        barraMenu.add(menuArquivo);

        menuAjuda.setText(resourceMap.getString("menuAjuda.text")); // NOI18N
        menuAjuda.setName("menuAjuda"); // NOI18N

        menuItemSobre.setAction(actionMap.get("showAboutBox")); // NOI18N
        menuItemSobre.setText(resourceMap.getString("menuItemSobre.text")); // NOI18N
        menuItemSobre.setName("menuItemSobre"); // NOI18N
        menuAjuda.add(menuItemSobre);

        barraMenu.add(menuAjuda);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 583, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(barraMenu);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void btIniciarDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIniciarDownloadActionPerformed
        if (tablePesquisa.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Selecione pelo menos um arquivo antes de começar a transferência.", "Mensagem Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < tablePesquisa.getSelectedRows().length; i++) {
            int linhaSelecionada = tablePesquisa.getSelectedRows()[i];

            String arquivo = (String) tablePesquisa.getValueAt(linhaSelecionada, 0);
            String extensao = (String) tablePesquisa.getValueAt(linhaSelecionada, 1);
            double tamanhoMB = (Double) tablePesquisa.getValueAt(linhaSelecionada, 2);
            String status = "";
            double progresso = 0.0;
            int seeds = (Integer) tablePesquisa.getValueAt(linhaSelecionada, 3);

            Download download = resultadoPesquisa.get(indexOfResultadoPesquisa(arquivo, extensao));

            /* Caso não haja seeds colocar o status do downlaod como esperando por seeds e
            criar um timer para ficar verificando a sua chegada */
            if (download.getSeeds().size() == 0) {
                status = StatusDownload.ESPERANDO_SEEDS.name();
            /* TODO Inicializar o timer aqui */
            } else {
                /* Criando o arquivo de retomada */
                DownloadDTO dto = new DownloadDTO(
                        download.getCabecalhoArquivo().getCodigo(), arquivo,
                        extensao, download.size(), progresso);
                dto.setBlocos(download.getBlocos());
                try {
                    GerenteDownload.salvarDownload(dto);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Não foi possível dar " +
                            "início ao download. Tente novamente.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(ClienteView.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                status = StatusDownload.BAIXANDO.name();

                File arquivoRecebido = new File(FileUtil.criarNomeArquivoCompleto(arquivo, extensao));
                //arquivosBaixando.add(arquivoRecebido);

                ProgressBarSwingWorker worker = new ProgressBarSwingWorker(arquivoRecebido, download.size(),
                        tableDownloads.getRowCount(), 4);

                worker.execute();

                List<Thread> threads = new ArrayList<Thread>();

                for (Seed seed : download.getSeeds()) {
                    Thread thread = new ThreadCliente(
                            download.getCabecalhoArquivo().getNome(),
                            download.getCabecalhoArquivo().getExtensao(),
                            arquivoRecebido, seed);
                    threads.add(thread);
                    thread.start();
                }

                /* Adiciona as threads deste download a lista de threads */
                mapThreadsDownloads.put(arquivo + "." + extensao, threads);
                mapThreadsSwingWorker.put(arquivo + "." + extensao, worker);

            }

            Object[] linha = {arquivo, extensao, tamanhoMB, status, progresso, seeds};
            ((DefaultTableModel) tableDownloads.getModel()).addRow(linha);

        }
}//GEN-LAST:event_btIniciarDownloadActionPerformed

    private void btPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarActionPerformed
        pesquisar();
}//GEN-LAST:event_btPesquisarActionPerformed

    private void txtPesquisaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPesquisaFocusGained
        if (txtPesquisa.getText().equals("<< Pesquisar Aqui >>")) {
            txtPesquisa.setText("");
        }
    }//GEN-LAST:event_txtPesquisaFocusGained

    private void txtPesquisaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaKeyTyped
        if (txtPesquisa.getText().equals("")) {
            btPesquisar.setEnabled(false);
        } else {
            btPesquisar.setEnabled(true);
        }
    }//GEN-LAST:event_txtPesquisaKeyTyped

    private void btCancelarDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarDownloadActionPerformed
        cancelarDownload();
    }//GEN-LAST:event_btCancelarDownloadActionPerformed

    private void txtPesquisaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaKeyPressed
        /* Caso haja informação no campo de texto da pesquisa e tecla
        pressionada seja enter então executa a pesquisa */
        if (!txtPesquisa.getText().equals("") && evt.getKeyCode() == KeyEvent.VK_ENTER) {
            pesquisar();
        }
    }//GEN-LAST:event_txtPesquisaKeyPressed

    private void btPausarDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPausarDownloadActionPerformed
        if (btPausarDownload.getText().equals("Pausar")) {
            pausarDownload();
            btPausarDownload.setText("Retomar");
        } else {
            retomarDownload();
            btPausarDownload.setText("Pausar");
        }
}//GEN-LAST:event_btPausarDownloadActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar barraMenu;
    private javax.swing.JButton btCancelarDownload;
    private javax.swing.JButton btIniciarDownload;
    private javax.swing.JButton btPausarDownload;
    private javax.swing.JButton btPesquisar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTable tableDownloads;
    private javax.swing.JTable tablePesquisa;
    private javax.swing.JTextField txtPesquisa;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;

    /* Variáveis do usuário */
    private List<Download> resultadoPesquisa = new ArrayList<Download>();
    private List<DownloadDTO> downloadsIncompletos = new ArrayList<DownloadDTO>();
    //private List<File> arquivosBaixando = new ArrayList<File>();
    private Map<String, List<Thread>> mapThreadsDownloads = new HashMap<String, List<Thread>>();
    private Map<String, ProgressBarSwingWorker> mapThreadsSwingWorker = new HashMap<String, ProgressBarSwingWorker>();

    /* ---------------- Método do usuário ----------------------- */
    private void inicializarTabelaDownloads() {
        try {
            downloadsIncompletos = GerenteDownload.recuperarDownloadsIncompletos();
            for (DownloadDTO download : downloadsIncompletos) {
                String arquivo = download.getNome();
                String extensao = download.getExtensao();
                double tamanhoMB = download.sizeInMB();
                String status = StatusDownload.PARADO.name();
                int seeds = 0;

                File arquivoFile = new File(FileUtil.criarNomeArquivoCompleto(arquivo, extensao));

                if (!arquivoFile.exists()) {
                    JOptionPane.showMessageDialog(null,
                            "Arquivo não encontrado.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                long tamanhoAtualArquivo = arquivoFile.length();
                double progresso = ((double) tamanhoAtualArquivo / download.size()) * 100;
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(2);

                Object[] linha = {arquivo, extensao, tamanhoMB, status,
                    nf.format(progresso), seeds};
                ((DefaultTableModel) tableDownloads.getModel()).addRow(linha);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClienteView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void limpar(JTable tabela) {
        DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
        int qtdLinhas = modelo.getRowCount();
        while (qtdLinhas > 0) {
            modelo.removeRow(0);
            qtdLinhas--;
        }
    }

    private int indexOfResultadoPesquisa(String arquivo, String extensao) {
        for (int i = 0; i < resultadoPesquisa.size(); i++) {
            if (resultadoPesquisa.get(i).getCabecalhoArquivo().
                    getNome().equals(arquivo) && resultadoPesquisa.get(i).getCabecalhoArquivo().
                    getExtensao().equals(extensao)) {
                return i;
            }
        }
        return -1;
    }

    private DownloadDTO indexOfDownloadsIncompletos(String arquivo, String extensao) {
        for (DownloadDTO download : downloadsIncompletos) {
            if (download.getNome().equals(arquivo) && download.getExtensao().equals(extensao)) {
                return download;
            }
        }
        return null;
    }

    private void pesquisar() {
        String nomeArquivoProcurado = txtPesquisa.getText().trim();

        if (nomeArquivoProcurado.equals("")) {
            return;
        }

        resultadoPesquisa = PesquisarArquivos.pesquisar(nomeArquivoProcurado);

        int qtdArquivosEncontrado = resultadoPesquisa.size();

        limpar(tablePesquisa);

        for (int i = 0; i < qtdArquivosEncontrado; i++) {
            String nomeArquivo = resultadoPesquisa.get(i).getCabecalhoArquivo().getNome();
            String extensao = resultadoPesquisa.get(i).getCabecalhoArquivo().getExtensao();
            double tamanhoMB = resultadoPesquisa.get(i).sizeInMB();
            int qtdPeers = resultadoPesquisa.get(i).quantidadeSeeds();

            Object[] linha = {nomeArquivo, extensao, tamanhoMB, qtdPeers};
            ((DefaultTableModel) tablePesquisa.getModel()).addRow(linha);
        }

        if (qtdArquivosEncontrado == 0) {
            JOptionPane.showMessageDialog(null, "Nenhum arquivo foi encontrado.", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String iniciarDownload(int linhaSelecionada, DownloadDTO downloadDTO, List<Seed> seeds) {
        /* Caso não haja seeds colocar o status do downlaod como esperando por seeds e
        criar um timer para ficar verificando a sua chegada */
        if (seeds.size() == 0) {
            return StatusDownload.ESPERANDO_SEEDS.name();
        /* TODO Inicializar o timer aqui */
        }

        try {
            GerenteDownload.salvarDownload(downloadDTO);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Não foi possível dar " +
                    "início ao download. Tente novamente.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ClienteView.class.getName()).log(Level.SEVERE, null, ex);
        }

        File arquivoRecebido = new File(FileUtil.criarNomeArquivoCompleto(
                downloadDTO.getNome(), downloadDTO.getExtensao()));
        //arquivosBaixando.add(arquivoRecebido);

        ProgressBarSwingWorker worker = new ProgressBarSwingWorker(arquivoRecebido, downloadDTO.size(),
                linhaSelecionada, 4);

        worker.execute();

        List<Thread> threads = new ArrayList<Thread>();

        for (Seed seed : seeds) {
            Thread thread = new ThreadCliente(downloadDTO.getNome(),
                    downloadDTO.getExtensao(), arquivoRecebido, seed);
            threads.add(thread);
            thread.start();
        }

        String keyThreads = downloadDTO.getNome() + "." + downloadDTO.getExtensao();

        /* Adiciona as threads deste download a lista de threads */
        List<Thread> t = mapThreadsDownloads.get(keyThreads);
        if (t != null) {
            mapThreadsDownloads.remove(t);
            t = null;
        }
        mapThreadsDownloads.put(keyThreads, threads);

        SwingWorker w = mapThreadsSwingWorker.get(keyThreads);
        if (w != null) {
            mapThreadsSwingWorker.remove(w);
            w = null;
        }
        mapThreadsSwingWorker.put(keyThreads, worker);

        return StatusDownload.BAIXANDO.name();

    }

    private void pausarDownload() {
        if (tableDownloads.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Selecione um arquivo antes de pausar uma transferência.", "Mensagem Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int linhasSelecionada = tableDownloads.getSelectedRow();

        String nomeArquivo = (String) tableDownloads.getValueAt(linhasSelecionada, 0);
        String extensao = (String) tableDownloads.getValueAt(linhasSelecionada, 1);
        String status = (String) tableDownloads.getValueAt(linhasSelecionada, 3);

        tableDownloads.setValueAt(StatusDownload.PARADO.name(), linhasSelecionada, 3);

        if (status.equals(StatusDownload.ESPERANDO_SEEDS.name())) {
            /* TODO Pausa timer */
            return;
        }

        /* Chave para recuperar as thread nos maps*/
        String keyThread = nomeArquivo + "." + extensao;

        /* Recupera todas as threads utilizadas por um download */
        List<Thread> threads = mapThreadsDownloads.get(keyThread);
        if (threads != null) {
            for (Thread t : threads) {
                if (t != null && t.isAlive()) {
                    /* Interrompe a thread atual */
                    t.interrupt();
                    System.err.println("Thread " + t.getName() + " do: " + nomeArquivo + " foi interrompida");
                }

            }

            /* Remove as thread de download do map*/
            mapThreadsDownloads.remove(keyThread);
            /*Interrompe a thread de atualização da table de downloads */
            mapThreadsSwingWorker.get(keyThread).interrupt();
            /* Remove a thread de atualização do map*/
            mapThreadsSwingWorker.remove(keyThread);
        }

    }

    private void cancelarDownload() {
        if (tableDownloads.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Selecione um arquivo antes de cancelar uma transferência.", "Mensagem Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int linhasSelecionada = tableDownloads.getSelectedRow();

        String nomeArquivo = (String) tableDownloads.getValueAt(linhasSelecionada, 0);
        String extensao = (String) tableDownloads.getValueAt(linhasSelecionada, 1);

        /* Chave para recuperar as thread nos maps*/
        String keyThread = nomeArquivo + "." + extensao;

        /* Recupera todas as threads utilizadas por um download */
        List<Thread> threads = mapThreadsDownloads.get(keyThread);
        if (threads != null) {
            for (Thread t : threads) {
                if (t != null && t.isAlive()) {
                    /* Interrompe a thread atual */
                    t.interrupt();
                    System.err.println("Thread " + t.getName() + " do: " + nomeArquivo + " foi interrompida");
                }

            }

            /* Remove as thread de download do map*/
            mapThreadsDownloads.remove(keyThread);
            /*Interrompe a thread de atualização da table de downloads */
            mapThreadsSwingWorker.get(keyThread).interrupt();
            /* Remove a thread de atualização do map*/
            mapThreadsSwingWorker.remove(keyThread);
        }

        ((DefaultTableModel) tableDownloads.getModel()).removeRow(linhasSelecionada);
    }

    private void retomarDownload() {
        int linhaSelecionada = tableDownloads.getSelectedRow();

        String arquivo = (String) tableDownloads.getValueAt(linhaSelecionada, 0);
        String extensao = (String) tableDownloads.getValueAt(linhaSelecionada, 1);
        DownloadDTO downloadDTO = null;

        try {
            downloadDTO = GerenteDownload.recuperarDownload(arquivo, extensao);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClienteView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteView.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Seed> seeds = GerenteDownload.recuperarSeedsDoServidor(downloadDTO.getCodigo());

        if (seeds.size() > 0) {
            List<Bloco> blocos = GerenteDownload.calcularBlocosRestantes(downloadDTO);

            for (Bloco blocoAtual : blocos) {
                /* Randomiza a coleção de seeds */
                Collections.shuffle(seeds);
                seeds.get(0).addBloco(blocoAtual);
            }
        }

        File arquivoFile = new File(FileUtil.criarNomeArquivoCompleto(arquivo, extensao));

        if (!arquivoFile.exists()) {
            JOptionPane.showMessageDialog(null,
                    "Arquivo não encontrado.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        long tamanhoAtualArquivo = arquivoFile.length();
        double progresso = ((double) tamanhoAtualArquivo / downloadDTO.size()) * 100;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        tableDownloads.setValueAt(progresso, linhaSelecionada, 4);
        String status = "";
        
        /* Caso não haja seeds colocar o status do downlaod como esperando por seeds e
        criar um timer para ficar verificando a sua chegada */
        if (seeds.size() == 0) {
            status = StatusDownload.ESPERANDO_SEEDS.name();
            /* TODO Inicializar o timer aqui */
            return;
        }else{
            status = StatusDownload.BAIXANDO.name();
        }

        try {
            GerenteDownload.salvarDownload(downloadDTO);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Não foi possível dar " +
                    "início ao download. Tente novamente.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ClienteView.class.getName()).log(Level.SEVERE, null, ex);
        }

        File arquivoRecebido = new File(FileUtil.criarNomeArquivoCompleto(
                downloadDTO.getNome(), downloadDTO.getExtensao()));

        ProgressBarSwingWorker worker = new ProgressBarSwingWorker(arquivoRecebido, downloadDTO.size(),
                linhaSelecionada, 4);

        worker.execute();

        List<Thread> threads = new ArrayList<Thread>();

        for (Seed seed : seeds) {
            Thread thread = new ThreadCliente(downloadDTO.getNome(),
                    downloadDTO.getExtensao(), arquivoRecebido, seed);
            threads.add(thread);
            thread.start();
        }

        String keyThreads = downloadDTO.getNome() + "." + downloadDTO.getExtensao();

        /* Adiciona as threads deste download a lista de threads */
        List<Thread> t = mapThreadsDownloads.get(keyThreads);
        if (t != null) {
            mapThreadsDownloads.remove(t);
            t = null;
        }
        mapThreadsDownloads.put(keyThreads, threads);

        SwingWorker w = mapThreadsSwingWorker.get(keyThreads);
        if (w != null) {
            mapThreadsSwingWorker.remove(w);
            w = null;
        }
        mapThreadsSwingWorker.put(keyThreads, worker);

        tableDownloads.setValueAt(status, linhaSelecionada, 3);
    }

    class TablePesquisarBotaoIniciarListSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();

            if (listSelectionModel.isSelectionEmpty()) {
                btIniciarDownload.setEnabled(false);
            } else {
                btIniciarDownload.setEnabled(true);
            }
        }
    }

    class TableDownloadBotaoPausarListSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();

            if (listSelectionModel.isSelectionEmpty()) {
                btPausarDownload.setEnabled(false);
            } else {
                int linhaSelecionada = tableDownloads.getSelectedRow();
                String status = (String) tableDownloads.getValueAt(linhaSelecionada, 3);

                if (status.equals(StatusDownload.CONCLUIDO.name())) {
                    btPausarDownload.setEnabled(false);
                } else {
                    btPausarDownload.setEnabled(true);
                    if (status.equals(StatusDownload.BAIXANDO.name()) || status.equals(StatusDownload.ESPERANDO_SEEDS.name())) {
                        btPausarDownload.setText("Pausar");
                    } else {
                        btPausarDownload.setText("Retomar");
                    }

                }
            }
        }
    }

    class TableDownloadBotaoCancelarListSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();

            if (listSelectionModel.isSelectionEmpty()) {
                btCancelarDownload.setEnabled(false);
            } else {
                int linhaSelecionada = tableDownloads.getSelectedRow();
                String status = (String) tableDownloads.getValueAt(linhaSelecionada, 3);

                if (status.equals(StatusDownload.BAIXANDO.name()) || status.equals(StatusDownload.ESPERANDO_SEEDS.name()) || status.equals(StatusDownload.PARADO.name())) {
                    btCancelarDownload.setEnabled(true);
                } else {
                    btCancelarDownload.setEnabled(false);
                }
            }
        }
    }

    class ProgressBarSwingWorker extends SwingWorker {

        private File arquivo;
        private long tamanhoFinalArquivo;
        private int linha,  coluna;
        private boolean interrupted;

        public ProgressBarSwingWorker(File arquivo, long tamanhoFinalArquivo, int linha, int coluna) {
            this.arquivo = arquivo;
            this.tamanhoFinalArquivo = tamanhoFinalArquivo;
            this.linha = linha;
            this.coluna = coluna;
            this.interrupted = false;
        }

        @Override
        protected Object doInBackground() {
            long tamanhoAtualArquivo;

            do {
                if (interrupted) {
                    System.err.println("Foi interrompido");
                    break;
                }
                tamanhoAtualArquivo = arquivo.length();
                double progresso = ((double) tamanhoAtualArquivo / tamanhoFinalArquivo) * 100;
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(2);
                tableDownloads.setValueAt(nf.format(progresso), linha, coluna);
                System.err.println("Tamanho Atual: " + tamanhoAtualArquivo);
                System.err.println("Tamanho Final: " + tamanhoFinalArquivo);
                System.err.println("Progresso: " + progresso);
                SwingUtilities.invokeLater(this);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClienteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            } while (tamanhoAtualArquivo < tamanhoFinalArquivo);

            return "";
        }

        @Override
        protected void done() {
            if (interrupted) {
                return;
            }
            System.err.println("Foi interrompido no done");
            System.err.println("Tamanho Final: " + tamanhoFinalArquivo);
            System.err.println("Linha: " + linha);
            tableDownloads.setValueAt(StatusDownload.CONCLUIDO.name(), linha, coluna - 1);
            /* Quando o download tiver terminado temos que avisar ao servidor
            que este host já pode ser um seed do arquivo baixado */
            Conexao.sincronizar();
        }

        public void interrupt() {
            interrupted = true;
        }
    }
}
