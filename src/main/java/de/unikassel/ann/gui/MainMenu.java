package de.unikassel.ann.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import de.unikassel.ann.controller.ActionJMenuItem;
import de.unikassel.ann.controller.Actions;
import de.unikassel.ann.controller.Settings;

public class MainMenu extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenu fileMenu;
	public JMenu subMenuSession;

	/**
	 * Constructor
	 */
	public MainMenu() {
		super();
		fileMenu = new JMenu(Settings.i18n.getString("menu.file"));
		addMenus();
	}

	/**
	 * Add all menus with their items to this menu.
	 */
	private void addMenus() {
		initFileMenu();
		this.add(fileMenu);

		JMenu mnAnsicht = getAnsichtMenu();
		this.add(mnAnsicht);

		JMenu mnOptions = getOptionsMenu();
		this.add(mnOptions);

		JMenu mnTest = getTestMenu();
		this.add(mnTest);

		JMenu mnHilfe = getHilfeMenu();
		this.add(mnHilfe);
	}

	/**
	 * File menu
	 * 
	 * @return JMenu
	 */
	public void initFileMenu() {

		JMenuItem mntmNeu = new ActionJMenuItem(Settings.i18n.getString("menu.file.new"), Actions.NEW);
		fileMenu.add(mntmNeu);

		JMenuItem mntmImport = new ActionJMenuItem(Settings.i18n.getString("menu.file.import"), Actions.IMPORT);
		fileMenu.add(mntmImport);

		JMenuItem mntmExport = new ActionJMenuItem(Settings.i18n.getString("menu.file.export"), Actions.EXPORT);
		fileMenu.add(mntmExport);

		fileMenu.addSeparator();

		// hier is the Position, if sessions exists
		// addItemToMenu()
		subMenuSession = new JMenu(Settings.i18n.getString("menu.file.submenu.sessions"));
		fileMenu.add(subMenuSession);

		fileMenu.addSeparator();

		JMenuItem mntmCloseCurrentSession = new ActionJMenuItem(Settings.i18n.getString("menu.file.closeCurrentsession"),
				Actions.CLOSE_CURRENT_SESSION);
		fileMenu.add(mntmCloseCurrentSession, -1);

		JMenuItem mntmBeenden = new ActionJMenuItem(Settings.i18n.getString("menu.file.exit"), Actions.EXIT);
		mntmBeenden.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		fileMenu.add(mntmBeenden, -1);
	}

	/**
	 * View menu
	 * 
	 * @return JMenu
	 */
	private JMenu getAnsichtMenu() {
		JMenu mnAnsicht = new JMenu(Settings.i18n.getString("menu.view"));

		JMenuItem mntmDatenvisualisierung = new ActionJMenuItem(Settings.i18n.getString("menu.view.showTrainingData"), Actions.VIEW_DATA);
		mnAnsicht.add(mntmDatenvisualisierung);

		JMenuItem mntmTrainingfehlerverlauf = new ActionJMenuItem(Settings.i18n.getString("menu.view.showTrainingError"),
				Actions.VIEW_TRAINING);
		mnAnsicht.add(mntmTrainingfehlerverlauf);

		return mnAnsicht;
	}

	/**
	 * Options Menu
	 * 
	 * @return JMenu
	 */

	private JMenu getOptionsMenu() {
		JMenu mnOptions = new JMenu(Settings.i18n.getString("menu.options"));

		JMenuItem mntmBackpropagation = new ActionJMenuItem(Settings.i18n.getString("menu.options.backpropagation"),
				Actions.BACKPROPAGATION_VIEW);
		mnOptions.add(mntmBackpropagation);

		JMenuItem mntmSOM = new ActionJMenuItem(Settings.i18n.getString("menu.options.SOM"), Actions.SOM_VIEW);
		mnOptions.add(mntmSOM);

		mnOptions.addSeparator();

		JMenuItem mntmTrainData = new ActionJMenuItem(Settings.i18n.getString("menu.options.trainData"), Actions.NORMALIZE_TRAIN_DATA);
		mnOptions.add(mntmTrainData);

		return mnOptions;
	}

	/**
	 * Test menu
	 * 
	 * @return JMenu
	 */
	private JMenu getTestMenu() {
		JMenu mntmNetzwerk = new JMenu(Settings.i18n.getString("menu.network"));

		JMenuItem mntnORNetwork = new ActionJMenuItem(Settings.i18n.getString("menu.network.or"), Actions.LOAD_OR_NETWORK);
		mntmNetzwerk.add(mntnORNetwork);

		JMenuItem mntnXORNetwork = new ActionJMenuItem(Settings.i18n.getString("menu.network.xor"), Actions.LOAD_XOR_NETWORK);
		mntmNetzwerk.add(mntnXORNetwork);

		JMenuItem mntnANDNetwork = new ActionJMenuItem(Settings.i18n.getString("menu.network.and"), Actions.LOAD_AND_NETWORK);
		mntmNetzwerk.add(mntnANDNetwork);

		JMenuItem mntn2BitAddiererNetwork = new ActionJMenuItem(Settings.i18n.getString("menu.network.2bitaddierer"),
				Actions.LOAD_2_BIT_ADDIERER_NETWORK);
		mntmNetzwerk.add(mntn2BitAddiererNetwork);

		return mntmNetzwerk;
	}

	/**
	 * Help menu
	 * 
	 * @return JMenu
	 */
	private JMenu getHilfeMenu() {
		JMenu mnHilfe = new JMenu(Settings.i18n.getString("menu.help"));

		JMenuItem mntmUeber = new ActionJMenuItem(Settings.i18n.getString("menu.help.about"), Actions.ABOUT);
		mnHilfe.add(mntmUeber);

		return mnHilfe;
	}

}
