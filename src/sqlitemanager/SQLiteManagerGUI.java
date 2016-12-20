/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlitemanager;

import datasetjava.DataSet;
import datasetjava.DataTable;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Shawn
 */
public class SQLiteManagerGUI extends javax.swing.JDialog {

    /**
     * Creates new form mainFrame
     */
    public SQLiteManagerGUI() {
        this.setTitle("SQLite Manager");
        initComponents();
    }

    public SQLiteManagerGUI(JFrame host, boolean modal, String title) {
        super(host, modal);
        this.setTitle(title);
        initComponents();
    }

    private DataSet SQLiteDS;
    private List<DataTable> ExcelTables;

    private String currentPath;

    private boolean hasSQLite;

    private boolean hasExcel;

    private FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Excel spreadsheet (.xls, .xlsx)", "XLS", "XLSX");
    private FileNameExtensionFilter SQLiteFilter = new FileNameExtensionFilter("SQLite Database (.db3)", "DB3");
    private FileNameExtensionFilter SQLiteExcelFilter = new FileNameExtensionFilter("All support files (.db3, .xls, .xlsx)", "DB3", "XLS", "XLSX");

    private void buildSQLite(String path) {
        SQLiteDS = new DataSet(path);
        SQLiteDS.save();
        updateSQLiteList();
        checkSave();
        this.btnLoadSQLite.setEnabled(false);
        this.btnCreateSQLite.setEnabled(false);
        this.hasSQLite = true;
        this.jtaDatabase.setText(path);
    }

    private void loadSQLite(String path) {
        if (DataSet.checkIsSQLiteDatebaseValid(path)) {
            SQLiteDS = DataSet.importSQLiteDatabase(path);
            updateSQLiteList();
            this.btnLoadSQLite.setEnabled(false);
            this.btnCreateSQLite.setEnabled(false);
            this.jtaDatabase.setText(path);
            this.hasSQLite = true;
        } else {
            JOptionPane.showMessageDialog(this, "This is not a valid SQLite Database, please double check!", "Notice", JOptionPane.ERROR_MESSAGE);
        }
        checkSave();
    }

    private void load2ndDB(String path) {
        if (this.excelFilter.accept(new File(path))) {
            ExcelTables = Excel2Dataset.readExcel(path, false, 3);
        } else {
            ExcelTables = DataSet.importSQLiteDatabase(path).getTables();
        }
        
        if (ExcelTables != null && !ExcelTables.isEmpty()) {
            updateExcelList();
            checkSave();
            this.btnLoadExcel.setEnabled(false);
            this.jtaExcel.setText(path);
            this.hasExcel = true;
        } else {
            JOptionPane.showMessageDialog(this, "Loading Excel file error!", "Notice", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void add2SQLite(String exTableName, boolean isUpdateList) {
        for (DataTable table : ExcelTables) {
            if (table.getName().equalsIgnoreCase(exTableName)) {
                SQLiteDS.insertTable(table);
                break;
            }
        }
        if (isUpdateList) {
            updateSQLiteList();
        }
    }

    private void addAll2SQLite() {
        DefaultListModel selectedStationsModel = (DefaultListModel) this.listExcel.getModel();
        for (int i = 0; i < selectedStationsModel.getSize(); i++) {
            String name = selectedStationsModel.getElementAt(i).toString().trim();
            this.add2SQLite(name, false);
        }
        updateSQLiteList();
    }

    private void removeSQLiteTable(String sqliteTableName, boolean isUpdateList) {
        SQLiteDS.removeTableIfExists(sqliteTableName);
        if (isUpdateList) {
            updateSQLiteList();
        }
    }

    private void removeAllSQLiteTable() {
        SQLiteDS.clearTable();
        updateSQLiteList();
    }

    private void updateSQLiteList() {
        if (SQLiteDS == null) {
            return;
        }

        DefaultListModel model = new DefaultListModel();
        for (String name : SQLiteDS.getTableNames()) {
            model.addElement(name);
        }
        this.listSQLite.setModel(model);
        this.checkAddRemoveButtons();
    }

    private void checkAddRemoveButtons() {
        boolean removeFlag = listSQLite.getModel().getSize() > 0;
        boolean addFlag = (listExcel.getModel().getSize() > 0 && hasSQLite);

        btnAdd.setEnabled(addFlag);
        btnAddAll.setEnabled(addFlag);
        btnRemove.setEnabled(removeFlag);
        btnRemoveAll.setEnabled(removeFlag);

        this.btnExportSQLite.setEnabled(removeFlag);

        if (addFlag) {
            listExcel.setSelectedIndex(0);
        }

        if (removeFlag) {
            listSQLite.setSelectedIndex(0);
        }
    }

    private void updateExcelList() {
        if (ExcelTables == null) {
            return;
        }

        DefaultListModel model = new DefaultListModel();
        for (DataTable table : this.ExcelTables) {
            model.addElement(table.getName());
        }
        this.listExcel.setModel(model);
        this.checkAddRemoveButtons();
    }

    private void checkSave() {
        boolean isValid = hasSQLite;

        this.updateExcelList();
        this.updateSQLiteList();
    }

    private void save2SQLite() {
        if (SQLiteDS == null) {
            JOptionPane.showMessageDialog(this, "Please create/load database first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            this.setMouseWait();
            this.SQLiteDS.save();
            JOptionPane.showMessageDialog(this, "Saving SQLite database to " + SQLiteDS.getPath() + " SUCCESSFULLY!", "Notice", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(SQLiteManagerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(this, "Saving SQLite database to " + SQLiteDS.getPath() + " ERROR!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.setMouseDefault();
    }

    private void reset() {
        this.SQLiteDS = null;
        this.ExcelTables = null;
        this.initComponents();
        this.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaDatabase = new javax.swing.JTextArea();
        btnLoadSQLite = new javax.swing.JButton();
        btnCreateSQLite = new javax.swing.JButton();
        btnExportSQLite = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtaExcel = new javax.swing.JTextArea();
        btnLoadExcel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listExcel = new javax.swing.JList<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        listSQLite = new javax.swing.JList<>();
        btnAdd = new javax.swing.JButton();
        btnAddAll = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnRemoveAll = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnSave2SQLite = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("SQLite Database"));

        jLabel1.setText("Database:");

        jScrollPane1.setEnabled(false);
        jScrollPane1.setFocusable(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(166, 40));

        jtaDatabase.setEditable(false);
        jtaDatabase.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        jtaDatabase.setColumns(20);
        jtaDatabase.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jtaDatabase.setLineWrap(true);
        jtaDatabase.setRows(3);
        jtaDatabase.setTabSize(3);
        jtaDatabase.setAutoscrolls(false);
        jtaDatabase.setEnabled(false);
        jtaDatabase.setFocusable(false);
        jtaDatabase.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(jtaDatabase);

        btnLoadSQLite.setText("Load");
        btnLoadSQLite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadSQLiteActionPerformed(evt);
            }
        });

        btnCreateSQLite.setText("Create");
        btnCreateSQLite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateSQLiteActionPerformed(evt);
            }
        });

        btnExportSQLite.setText("Export");
        btnExportSQLite.setToolTipText("Export SQLite database to Excel");
        btnExportSQLite.setEnabled(false);
        btnExportSQLite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportSQLiteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCreateSQLite)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLoadSQLite, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnExportSQLite)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLoadSQLite)
                    .addComponent(btnCreateSQLite)
                    .addComponent(btnExportSQLite))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Excel/SQLite File"));

        jLabel2.setText("Import file:");

        jScrollPane2.setEnabled(false);
        jScrollPane2.setFocusable(false);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(166, 40));

        jtaExcel.setEditable(false);
        jtaExcel.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        jtaExcel.setColumns(20);
        jtaExcel.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jtaExcel.setLineWrap(true);
        jtaExcel.setRows(3);
        jtaExcel.setTabSize(3);
        jtaExcel.setAutoscrolls(false);
        jtaExcel.setEnabled(false);
        jtaExcel.setFocusable(false);
        jtaExcel.setRequestFocusEnabled(false);
        jScrollPane2.setViewportView(jtaExcel);

        btnLoadExcel.setText("Load");
        btnLoadExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnLoadExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLoadExcel)
                .addGap(10, 10, 10))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Tables"));

        jScrollPane3.setViewportView(listExcel);

        jScrollPane4.setViewportView(listSQLite);

        btnAdd.setText(">");
        btnAdd.setEnabled(false);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnAddAll.setText(">>");
        btnAddAll.setEnabled(false);
        btnAddAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAllActionPerformed(evt);
            }
        });

        btnRemove.setText("<<");
        btnRemove.setEnabled(false);
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnRemoveAll.setText("<");
        btnRemoveAll.setEnabled(false);
        btnRemoveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAllActionPerformed(evt);
            }
        });

        jLabel3.setText("Imported Tables");

        jLabel4.setText("SQLite Tables");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAddAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRemoveAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnSave2SQLite.setText("Save to SQLite");
        btnSave2SQLite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave2SQLiteActionPerformed(evt);
            }
        });

        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSave2SQLite)
                .addGap(11, 11, 11)
                .addComponent(btnExit)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave2SQLite)
                    .addComponent(btnExit))
                .addGap(0, 5, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        this.removeAllSQLiteTable();
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnCreateSQLiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateSQLiteActionPerformed
        javax.swing.JFileChooser fcBrowse = new javax.swing.JFileChooser();
        if (currentPath != null) {
            fcBrowse.setCurrentDirectory(new File(currentPath));
        }
        fcBrowse.setFileFilter(this.SQLiteFilter);
        int returnVal = fcBrowse.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            try {
                this.setMouseWait();
                String path = fcBrowse.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".db3")) {
                    path += ".db3";
                }
                buildSQLite(path);
                currentPath = new File(path).getParent();
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(SQLiteManagerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(this, "Building SQLite database error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            this.setMouseDefault();
        }
    }//GEN-LAST:event_btnCreateSQLiteActionPerformed

    private void btnLoadSQLiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadSQLiteActionPerformed
        javax.swing.JFileChooser fcBrowse = new javax.swing.JFileChooser();
        if (currentPath != null) {
            fcBrowse.setCurrentDirectory(new File(currentPath));
        }
        fcBrowse.setFileFilter(this.SQLiteFilter);
        int returnVal = fcBrowse.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            try {
                String path = fcBrowse.getSelectedFile().getAbsolutePath();
                loadSQLite(path);
                currentPath = new File(path).getParent();
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(SQLiteManagerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(this, "Loading SQLite database error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnLoadSQLiteActionPerformed

    private void btnLoadExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadExcelActionPerformed
        javax.swing.JFileChooser fcBrowse = new javax.swing.JFileChooser();
        if (currentPath != null) {
            fcBrowse.setCurrentDirectory(new File(currentPath));
        }
        fcBrowse.setFileFilter(this.SQLiteExcelFilter);
        int returnVal = fcBrowse.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            try {
                this.setMouseWait();
                String path = fcBrowse.getSelectedFile().getAbsolutePath();
                load2ndDB(path);
                currentPath = new File(path).getParent();
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(SQLiteManagerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(this, "Loading excel file error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            this.setMouseDefault();
        }
    }//GEN-LAST:event_btnLoadExcelActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        add2SQLite(listExcel.getSelectedValue(), true);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnAddAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAllActionPerformed
        this.addAll2SQLite();
    }//GEN-LAST:event_btnAddAllActionPerformed

    private void btnRemoveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveAllActionPerformed
        this.removeSQLiteTable(listSQLite.getSelectedValue(), true);
    }//GEN-LAST:event_btnRemoveAllActionPerformed

    private void btnSave2SQLiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave2SQLiteActionPerformed
        this.save2SQLite();
    }//GEN-LAST:event_btnSave2SQLiteActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnExportSQLiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportSQLiteActionPerformed
        export2Excel();
    }//GEN-LAST:event_btnExportSQLiteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("systemLAF".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
            String className = UIManager.getSystemLookAndFeelClassName();
            String name = null;
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (className.equals(info.getClassName())) {
                    name = info.getName();
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SQLiteManagerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SQLiteManagerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SQLiteManagerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SQLiteManagerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SQLiteManagerGUI main = new SQLiteManagerGUI();
                main.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddAll;
    private javax.swing.JButton btnCreateSQLite;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnExportSQLite;
    private javax.swing.JButton btnLoadExcel;
    private javax.swing.JButton btnLoadSQLite;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnRemoveAll;
    private javax.swing.JButton btnSave2SQLite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jtaDatabase;
    private javax.swing.JTextArea jtaExcel;
    private javax.swing.JList<String> listExcel;
    private javax.swing.JList<String> listSQLite;
    // End of variables declaration//GEN-END:variables

    private void export2Excel() {
        javax.swing.JFileChooser fcBrowse = new javax.swing.JFileChooser();
        if (currentPath != null) {
            fcBrowse.setCurrentDirectory(new File(currentPath));
        }
        fcBrowse.setFileFilter(this.excelFilter);
        int returnVal = fcBrowse.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {

            try {
                this.setMouseWait();
                String path = fcBrowse.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".xls") || !path.toLowerCase().endsWith(".xlsx")) {
                    path += ".xlsx";
                }
                Excel2Dataset.export2Excel(SQLiteDS, path);
                currentPath = new File(path).getParent();
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(SQLiteManagerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(this, "Building SQLite database error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            this.setMouseDefault();
        }
    }

    public void setMouseDefault() {
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        getGlassPane().removeMouseListener(mouseAdapter);
        getGlassPane().setVisible(true);
    }

    public void setMouseWait() {
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        for (MouseListener ml : getGlassPane().getMouseListeners()) {
            if (ml.equals(mouseAdapter)) {
                getGlassPane().removeMouseListener(mouseAdapter);
            }
        }
        getGlassPane().addMouseListener(mouseAdapter);
        getGlassPane().setVisible(true);
    }
    private final static MouseAdapter mouseAdapter = new MouseAdapter() {
    };
}
