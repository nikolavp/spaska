package spaska.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

/**
 * 
 * @author <a href="mailto:vesko.m.georgiev@gmail.com">Vesko Georgiev</a>
 * 
 * @param <T>
 */
public class ComboList<T extends Parametrable> extends JPanel implements
        ItemListener, ActionListener {

    private static final long serialVersionUID = 1L;

    private JComboBox combo;
    private JList list;
    private Map<Class<? extends T>, Map<String, String>> resultParams;
    private Class<? extends T> lastSelectedValue;
    private JPopupMenu popupMenu;
    private ParametersDialog dialog;
    private int maxSize;
    private JMenuItem configure;
    private boolean doNotChange;

    public ComboList(String title, Vector<Class<? extends T>> items, int maxSize) {
        this.maxSize = maxSize;
        getPopupMenu();

        dialog = new ParametersDialog(this);

        combo = new JComboBox(items);
        combo.setActionCommand(Utils.ITEM_SELECTED);
        combo.addActionListener(this);
        combo.addItemListener(this);

        combo.setRenderer(getRenderer());
        combo.setSelectedIndex(-1);

        resultParams = new HashMap<Class<? extends T>, Map<String, String>>();

        list = new JList(new DefaultListModel());
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (list.getSelectedValue() != null) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        Map<String, String> params = resultParams.get(list
                                .getSelectedValue());
                        configure.setEnabled(params != null);
                        getPopupMenu().show(ComboList.this, e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) {
                        showParameters();
                    }
                }
            }
        });
        list.setCellRenderer(getRenderer());
        setPreferredSize(new Dimension(150, 150));

        setLayout(new BorderLayout(2, 2));

        add(new JLabel(title), BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(combo, BorderLayout.SOUTH);
    }

    public ComboList(String title, Vector<Class<? extends T>> objects) {
        this(title, objects, Integer.MAX_VALUE);
    }

    private JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();

            configure = new JMenuItem("Set Parameters");
            configure.addActionListener(this);
            configure.setActionCommand(Utils.SHOW_PARAMETERS);

            JMenuItem remove = new JMenuItem("Remove");
            remove.addActionListener(this);
            remove.setActionCommand(Utils.REMOVE_ITEM);

            popupMenu.add(configure);
            popupMenu.add(remove);
        }
        return popupMenu;
    }

    private ListCellRenderer getRenderer() {
        return new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if (value instanceof Class<?>) {
                    Class<?> cls = (Class<?>) value;
                    value = cls.getSimpleName();
                }
                return super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
            }
        };
    }

    public void showParameters() {
        @SuppressWarnings("unchecked")
        Class<? extends T> cls = (Class<? extends T>) list.getSelectedValue();
        Map<String, String> params = resultParams.get(cls);
        if (params == null) {
            params = Utils.getParamsOfClass(cls);
        }
        dialog.show(params);
    }

    private void add(Class<? extends T> item) {
        ((DefaultListModel) list.getModel()).addElement(item);
        resultParams.put(item, Utils.getParamsOfClass(item));
    }

    private void addToList(Class<? extends T> cls) {
        if (cls != null && list != null) {
            if (list.getModel().getSize() == maxSize && maxSize == 1) {
                ((DefaultListModel) list.getModel()).removeAllElements();
                resultParams.clear();
            }
            if (!resultParams.containsKey(cls)
                    && list.getModel().getSize() < maxSize) {
                add(cls);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(Utils.SHOW_PARAMETERS)) {
            showParameters();
        } else if (e.getActionCommand().equals(Utils.REMOVE_ITEM)) {
            DefaultListModel model = ((DefaultListModel) list.getModel());

            resultParams.remove(list.getSelectedValue());
            model.remove(list.getSelectedIndex());

            lastSelectedValue = null;
            doNotChange = true;
            combo.setSelectedIndex(-1);
        } else if (e.getActionCommand().equals(Utils.ITEM_SELECTED)) {
            addToList(lastSelectedValue);
        } else if (e.getActionCommand().equals(Utils.COMMIT_PARAMETERS)) {
            resultParams.put((Class<? extends T>) list.getSelectedValue(),
                    dialog.getResultParameters());
        }
    }

    public Map<Class<? extends T>, Map<String, String>> getParameters() {
        return resultParams;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (list != null) {
            if (!doNotChange) {
                lastSelectedValue = (Class<? extends T>) e.getItem();
            } else {
                doNotChange = false;
            }
        }
    }

}
