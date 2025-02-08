import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MainWindow extends JFrame {
    private CodeEditor editor;
    private JTree fileTree;
    private File currentFile;
    private boolean isExplorerVisible = true;
    private boolean isThemePanelVisible = false;
    private float zoomFactor = 1.0f;
    private JSplitPane splitPane;
    private JPanel themePanel;

    public MainWindow() {
        super("CCharm");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new java.awt.Color(45, 45, 45));

        JMenuBar menuBar = new MenuBar();
        setJMenuBar(menuBar);

        editor = new CodeEditor();
        JPanel statusBar = new StatusBar();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(getFileTree()),
                new JScrollPane(getEditorPanel()));
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        add(getThemePanel(), BorderLayout.EAST);

        addMenuActions();
        addTreeListeners();
        addThemePanelToggle();
    }

    private JTree getFileTree() {
        if (fileTree == null) {
            fileTree = new JTree();
            fileTree.setCellRenderer(new DefaultTreeCellRenderer() {
                @Override
                public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                        boolean leaf, int row, boolean hasFocus) {
                    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                    if (value instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                        Object userObject = node.getUserObject();
                        if (userObject instanceof File) {
                            File file = (File) userObject;
                            setText(file.getName());
                        }
                    }
                    return this;
                }
            });
            fileTree.setBackground(new java.awt.Color(45, 45, 45));
            fileTree.setForeground(new java.awt.Color(248, 248, 242));
        }
        return fileTree;
    }

    private JPanel getEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new java.awt.Color(45, 45, 45));

        JTextArea lines = new JTextArea("1");
        lines.setBackground(new java.awt.Color(45, 45, 45));
        lines.setForeground(new java.awt.Color(128, 128, 128));
        lines.setEditable(false);

        editor.getDocument().addDocumentListener(new DocumentListener() {
            public String getText() {
                int caretPosition = editor.getDocument().getLength();
                Element root = editor.getDocument().getDefaultRootElement();
                String text = "1" + System.lineSeparator();
                for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
                    text += i + System.lineSeparator();
                }
                return text;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lines.setText(getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                lines.setText(getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lines.setText(getText());
            }
        });

        JScrollPane scrollPane = new JScrollPane(editor);
        scrollPane.setRowHeaderView(lines);
        panel.add(scrollPane);

        return panel;
    }

    private JPanel getThemePanel() {
        if (themePanel == null) {
            themePanel = new JPanel();
            themePanel.setBackground(new java.awt.Color(45, 45, 45));
            themePanel.setForeground(new java.awt.Color(248, 248, 242));
            themePanel.setPreferredSize(new Dimension(300, getHeight()));
            themePanel.setVisible(false);

            JLabel label = new JLabel("Theme Search");
            label.setForeground(new java.awt.Color(248, 248, 242));
            themePanel.add(label);
        }
        return themePanel;
    }

    private void addMenuActions() {
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem openFolderItem = new JMenuItem("Open Folder");

        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        openFolderItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK));

        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        openFolderItem.addActionListener(e -> openFolder());

        ((JMenuBar) getJMenuBar()).getMenu(0).add(openItem);
        ((JMenuBar) getJMenuBar()).getMenu(0).add(saveItem);
        ((JMenuBar) getJMenuBar()).getMenu(0).add(openFolderItem);

        // Toggle panel visibility with Ctrl + B
        editor.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "togglePanel");
        editor.getActionMap().put("togglePanel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isExplorerVisible = !isExplorerVisible;
                splitPane.getLeftComponent().setVisible(isExplorerVisible);
                splitPane.setDividerLocation(isExplorerVisible ? 200 : 0);
            }
        });
    }

    private void addThemePanelToggle() {
        editor.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "toggleThemePanel");
        editor.getActionMap().put("toggleThemePanel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isThemePanelVisible = !isThemePanelVisible;
                themePanel.setVisible(isThemePanelVisible);
            }
        });
    }

    private void addTreeListeners() {
        fileTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
                if (selectedNode == null)
                    return;

                File selectedFile = (File) selectedNode.getUserObject();
                if (selectedFile.isFile()) {
                    openFile(selectedFile);
                }
            }
        });
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            openFile(currentFile);
        }
    }

    private void openFile(File file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            editor.setText("");
            String line;
            while ((line = reader.readLine()) != null) {
                editor.setText(editor.getText() + line + "\n");
            }
            editor.highlightSyntax();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        if (currentFile != null) {
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(currentFile), StandardCharsets.UTF_8))) {
                writer.write(editor.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Guardar en nuevo archivo si no existe archivo abierto
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                saveFile();
            }
        }
    }

    private void openFolder() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = folderChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File folder = folderChooser.getSelectedFile();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(folder);
            listFiles(folder, root);
            TreeModel treeModel = new DefaultTreeModel(root);
            fileTree.setModel(treeModel);
        }
    }

    private void listFiles(File folder, DefaultMutableTreeNode node) {
        for (File file : folder.listFiles()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file);
            node.add(childNode);
            if (file.isDirectory()) {
                listFiles(file, childNode);
            }
        }
    }
}
