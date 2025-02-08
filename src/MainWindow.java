import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.tree.*;
import java.awt.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class MainWindow extends JFrame {
    private CodeEditor editor;
    private JTree fileTree;

    public MainWindow() {
        super("CCharm");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new MenuBar();
        setJMenuBar(menuBar);

        editor = new CodeEditor();
        JPanel statusBar = new StatusBar();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(getFileTree()),
                new JScrollPane(editor));
        splitPane.setDividerLocation(200);

        add(splitPane, "Center");
        add(statusBar, "South");

        addMenuActions();
        addTreeListeners();
    }

    private JTree getFileTree() {
        if (fileTree == null) {
            fileTree = new JTree();
        }
        return fileTree;
    }

    private void addMenuActions() {
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem openFolderItem = new JMenuItem("Open Folder");

        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        openFolderItem.addActionListener(e -> openFolder());

        ((JMenuBar) getJMenuBar()).getMenu(0).add(openItem);
        ((JMenuBar) getJMenuBar()).getMenu(0).add(saveItem);
        ((JMenuBar) getJMenuBar()).getMenu(0).add(openFolderItem);
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
            File file = fileChooser.getSelectedFile();
            openFile(file);
        }
    }

    private void openFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(editor.getText());
            } catch (IOException e) {
                e.printStackTrace();
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
