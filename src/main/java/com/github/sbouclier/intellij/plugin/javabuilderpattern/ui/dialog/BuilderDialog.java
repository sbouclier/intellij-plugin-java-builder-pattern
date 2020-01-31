package com.github.sbouclier.intellij.plugin.javabuilderpattern.ui.dialog;

import com.github.sbouclier.intellij.plugin.javabuilderpattern.model.BuilderParameter;
import com.github.sbouclier.intellij.plugin.javabuilderpattern.model.BuilderType;
import com.github.sbouclier.intellij.plugin.javabuilderpattern.service.BuilderPrefs;
import com.github.sbouclier.intellij.plugin.javabuilderpattern.ui.AbstractBuilderTableModel;
import com.github.sbouclier.intellij.plugin.javabuilderpattern.ui.ParametersClassicBuilderTableModel;
import com.github.sbouclier.intellij.plugin.javabuilderpattern.ui.ParametersFluentBuilderTableModel;
import com.github.sbouclier.intellij.plugin.javabuilderpattern.util.StringUtils;
import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;

/**
 * Builder dialog box.
 */
public class BuilderDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane jscrollBuilderParams;
    private JTable builderParamsTable;
    private JLabel lbSelectParameters;
    private JRadioButton radioBtnClassic;
    private JRadioButton radioBtnFluent;
    private JCheckBox cbUsePrefix;
    private JTextField txtPrefix;
    private BuilderDialogResult result;
    private BuilderPrefs builderPrefs = ServiceManager.getService(BuilderPrefs.class);

    public BuilderDialog(List<BuilderParameter> parameters) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // listeners
        buttonCancel.addActionListener(e -> onCancel());
        radioBtnClassic.addItemListener(e -> builderParamsTable.setModel(createParametersTableModel(parameters)));
        radioBtnFluent.addItemListener(e -> builderParamsTable.setModel(createParametersTableModel(parameters)));

        // build parameters table
        builderParamsTable.setModel(createParametersTableModel(parameters));
        builderParamsTable.setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
        builderParamsTable.setRowSelectionAllowed(true);

        cbUsePrefix.addItemListener(e -> handleUpdateUsePrefix(parameters));
        txtPrefix.getDocument().addDocumentListener((UpdateDocumentListener) e -> refreshParametersWithPrefix(txtPrefix.getText(), parameters));

        // config
        if(builderPrefs.getBuilderType() == BuilderType.CLASSIC) {
            radioBtnClassic.setSelected(true);
        } else {
            radioBtnFluent.setSelected(true);
        }
        cbUsePrefix.setSelected(builderPrefs.isUsePrefix());
        txtPrefix.setText(builderPrefs.getPrefix());
        handleUpdateUsePrefix(parameters);
    }

    private void onOK() {
        AbstractBuilderTableModel paramsTableModel = (AbstractBuilderTableModel) builderParamsTable.getModel();
        int[] selectedRows = builderParamsTable.getSelectedRows();

        final BuilderType builderType = radioBtnClassic.isSelected() ? BuilderType.CLASSIC : BuilderType.FLUENT;
        this.result = new BuilderDialogResult(builderType);
        builderPrefs.setBuilderType(builderType);

        for (int rowIndex : selectedRows) {
            result.addSelectedParameter(paramsTableModel.getParameters().get(rowIndex));
        }

        builderPrefs.setPrefix(txtPrefix.getText());
        builderPrefs.setUsePrefix(cbUsePrefix.isSelected());

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private AbstractBuilderTableModel createParametersTableModel(List<BuilderParameter> parameters) {
        final AbstractBuilderTableModel tableModel;
        if(radioBtnClassic.isSelected()) {
            tableModel = new ParametersClassicBuilderTableModel();
        } else {
            tableModel = new ParametersFluentBuilderTableModel();
        }

        tableModel.setParameters(parameters);
        return tableModel;
    }

    public void addOkListener(ActionListener listener) {
        buttonOK.addActionListener(listener);
        buttonOK.addActionListener(e -> onOK());
    }

    public BuilderDialogResult getResult() {
        return result;
    }

    private void refreshParametersWithPrefix(String prefix, List<BuilderParameter> params) {
        params.forEach(p -> p.setSetterName(prefix + StringUtils.firstUppercaseLetter(p.getParameterName())));
        builderParamsTable.updateUI();
    }

    private void refreshParametersWithoutPrefix(List<BuilderParameter> params) {
        params.forEach(p -> p.setSetterName(p.getParameterName()));
        builderParamsTable.updateUI();
    }

    private void handleUpdateUsePrefix(List<BuilderParameter> parameters) {
        if (cbUsePrefix.isSelected()) {
            txtPrefix.setEnabled(true);
            refreshParametersWithPrefix(txtPrefix.getText(), parameters);
        } else {
            txtPrefix.setEnabled(false);
            refreshParametersWithoutPrefix(parameters);
        }
    }
}
