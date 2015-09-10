import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

public class PadFrame extends JFrame {
	private JTextAreaClipboard jtaTextBox;

	public PadFrame() {
		setTitle("Simple Notepad");
		setSize(800, 400);
		setLocationRelativeTo(null);
		add(new ScrollPane());
		setJMenuBar(new MenuBar());
		setVisible(true);
	}

	class ScrollPane extends JScrollPane {

		public ScrollPane() {
			jtaTextBox = new JTextAreaClipboard();
			setViewportView(jtaTextBox);
		}
	}

	class MenuBar extends JMenuBar {
		// File Menu
		private JMenu jmFile = new JMenu("File");
		private JMenuItem jmiNew = new JMenuItem("New");
		private JMenuItem jmiOpen = new JMenuItem("Open");
		private JMenuItem jmiSave = new JMenuItem("Save");
		private JMenuItem jmiSaveAs = new JMenuItem("Save as...");
		private JMenuItem jmiExit = new JMenuItem("Exit");

		// Edit Menu
		private JMenu jmEdit = new JMenu("Edit");
		private JMenuItem jmiCut = new JMenuItem("Cut");
		private JMenuItem jmiCopy = new JMenuItem("Copy");
		private JMenuItem jmiPaste = new JMenuItem("Paste");

		// Clipboard
		private Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();

		public MenuBar() {
			jmFile.add(jmiNew);
			jmFile.add(jmiOpen);
			jmFile.add(jmiSave);
			jmFile.add(jmiSaveAs);
			jmFile.add(jmiExit);

			jmEdit.add(jmiCut);
			jmEdit.add(jmiCopy);
			jmEdit.add(jmiPaste);

			add(jmFile);
			add(jmEdit);

			jmiNew.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					jtaTextBox.setText("");
				}
			});

			jmiOpen.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter textFilter = new FileNameExtensionFilter(
							"Text Document", "txt");
					FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
							"Common Image Formats", "jpeg", "jpg", "png",
							"bmp", "gif");
					chooser.addChoosableFileFilter(textFilter);
					chooser.addChoosableFileFilter(imageFilter);
					chooser.setAcceptAllFileFilterUsed(true);
					int returnVal = chooser.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = chooser.getSelectedFile();
						try {
							Scanner input = new Scanner(file);
							String ch = "\n";
							while (input.hasNextLine()) {
								jtaTextBox.append(input.nextLine());
								if (!input.hasNextLine()) {
									ch = "";
								}
								jtaTextBox.append(ch);
							}
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
			});

			jmiSaveAs.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter textFilter = new FileNameExtensionFilter(
							"Text Document", "txt");
					FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
							"Common Image Formats", "jpeg", "jpg", "png",
							"bmp", "gif");
					chooser.addChoosableFileFilter(textFilter);
					chooser.addChoosableFileFilter(imageFilter);
					chooser.setAcceptAllFileFilterUsed(true);
					int returnVal = chooser.showSaveDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = chooser.getSelectedFile();
						try {
							PrintWriter writer = new PrintWriter(
									new FileWriter(file, false));
							writer.append(jtaTextBox.getText());
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});

			jmiExit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});

			jmiCut.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					StringSelection selectedText = new StringSelection(
							jtaTextBox.getSelectedText());
					clipboard.setContents(selectedText, jtaTextBox);
					jtaTextBox.replaceSelection("");
				}
			});

			jmiCopy.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					StringSelection selectedText = new StringSelection(
							jtaTextBox.getSelectedText());
					clipboard.setContents(selectedText, jtaTextBox);
				}
			});

			jmiPaste.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String cbString = "";

					Transferable transferable = clipboard.getContents(null);
					boolean isTransferable = (transferable != null)
							&& transferable
									.isDataFlavorSupported(DataFlavor.stringFlavor);

					if (isTransferable) {
						try {
							cbString = (String) transferable
									.getTransferData(DataFlavor.stringFlavor);
						} catch (UnsupportedFlavorException | IOException ex) {
							ex.printStackTrace();
						}
					}
					try {
						if (jtaTextBox.getSelectedText() == null) {
							jtaTextBox.getDocument().insertString(
									jtaTextBox.getCaretPosition(), cbString,
									null);
						} else {
							jtaTextBox.replaceSelection(cbString);
						}
					} catch (BadLocationException ex) {
						ex.printStackTrace();
					}
				}
			});
		}
	}
}
