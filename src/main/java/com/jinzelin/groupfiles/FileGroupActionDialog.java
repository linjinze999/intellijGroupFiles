package com.jinzelin.groupfiles;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.openapi.fileEditor.impl.PsiAwareFileEditorManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFileSystemItem;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FileGroupActionDialog extends DialogWrapper {
    private JTextField txtInput;
    private final Project project;
    private final VirtualFile virtualFile;

    protected FileGroupActionDialog(Project project, VirtualFile virtualFile) {
        super(project);
        this.setTitle("分组");
        this.project = project;
        this.virtualFile = virtualFile;
        super.init();
    }

    @Override
    protected void doOKAction() {
        if(this.txtInput != null){
            String groupKey = this.txtInput.getText();
            if(!groupKey.isEmpty()){
                if(groupKey.equals(TreeStructureProvider.OtherGroup)){
                    FileGroupState.getInstance(this.project).getData().remove(this.virtualFile.getCanonicalPath());
                } else {
                    FileGroupState.getInstance(this.project).getData().put(this.virtualFile.getCanonicalPath(), groupKey);
                    AbstractProjectViewPane currentProjectViewPane = ProjectView.getInstance(project).getCurrentProjectViewPane();
                    if (currentProjectViewPane != null) {
                        currentProjectViewPane.updateFromRoot(false);
                    }
                }
            }
        }
        super.doOKAction();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new FlowLayout());

        JLabel label = new JLabel("分组名称：");
        dialogPanel.add(label);

        ArrayList<String> items = new ArrayList<>();
        for(String item: FileGroupState.getInstance(this.project).getData().values()){
            if(!items.contains(item)){
                items.add(item);
            }
        }
        if(!items.contains(TreeStructureProvider.OtherGroup)){
            items.add(TreeStructureProvider.OtherGroup);
        }
        this.txtInput = new JTextField();
        this.txtInput.setPreferredSize(new Dimension(300, 30));
        setupInputAutoComplete(txtInput, items);
        dialogPanel.add(txtInput);

        return dialogPanel;
    }

    private static boolean isInputAdjusting(JComboBox cbInput) {
        if (cbInput.getClientProperty("is_adjusting") instanceof Boolean) {
            return (Boolean) cbInput.getClientProperty("is_adjusting");
        }
        return false;
    }

    private static void setInputAdjusting(JComboBox cbInput, boolean adjusting) {
        cbInput.putClientProperty("is_adjusting", adjusting);
    }

    private static void updateInputList(final JComboBox cbInput, final DefaultComboBoxModel model, final JTextField txtInput, final ArrayList<String> items){
        setInputAdjusting(cbInput, true);
        model.removeAllElements();
        String input = txtInput.getText();
        for (String item : items) {
            if (item.toLowerCase().startsWith(input.toLowerCase())) {
                model.addElement(item);
            }
        }
        cbInput.setPopupVisible(model.getSize() > 0);
        setInputAdjusting(cbInput, false);
    }

    public static void setupInputAutoComplete(final JTextField txtInput, final ArrayList<String> items) {
        // 选择组件
        final DefaultComboBoxModel model = new DefaultComboBoxModel();
        final JComboBox cbInput = new JComboBox(model) {
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 0);
            }
        };
        setInputAdjusting(cbInput, false);
        for (String item : items) {
            model.addElement(item);
        }
        cbInput.setSelectedItem(null);
        cbInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isInputAdjusting(cbInput)) {
                    if (cbInput.getSelectedItem() != null) {
                        txtInput.setText(cbInput.getSelectedItem().toString());
                    }
                }
            }
        });
        // 输入框焦点监听
        txtInput.addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent e) {
                cbInput.setPopupVisible(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                cbInput.setPopupVisible(false);
            }
        });
        // 输入框点击监听
        txtInput.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                cbInput.setPopupVisible(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        // 输入框按键监听
        txtInput.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                setInputAdjusting(cbInput, true);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (cbInput.isPopupVisible()) {
                        e.setKeyCode(KeyEvent.VK_ENTER);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.setSource(cbInput);
                    cbInput.dispatchEvent(e);
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        txtInput.setText(cbInput.getSelectedItem().toString());
                        cbInput.setPopupVisible(false);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cbInput.setPopupVisible(false);
                }
                setInputAdjusting(cbInput, false);
            }
        });
        // 输入框输入监听
        txtInput.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateInputList(cbInput, model, txtInput, items);
            }

            public void removeUpdate(DocumentEvent e) {
                updateInputList(cbInput, model, txtInput, items);
            }

            public void changedUpdate(DocumentEvent e) {
                updateInputList(cbInput, model, txtInput, items);
            }

        });
        // 输入框添加选择组件
        txtInput.setLayout(new BorderLayout());
        txtInput.add(cbInput, BorderLayout.SOUTH);
    }
}
