package com.bmstu.cg;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.DataBufferByte;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.event.ChangeListener;

import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Launcher extends Canvas {

    private static int workPerforming;
    private static List<ComplexObject> complexObjectList;
    private static List<Source> lightSources;
    private static List<Source> lightSourcesWork;
    private static List<PrimitiveObject> sceneObjects;
    static int counter = 0;
    private final JFrame m_frame;
    private final RenderSceneTriangle m_frameBuffer;
    private final BufferedImage m_displayImage;
    private final byte[] m_displayComponents;
    private final BufferStrategy m_bufferStrategy;
    private final Graphics m_graphics;

    private final Input mInput;
    private JRadioButton antiAliasing1;
    private JRadioButton antiAliasing2;
    private JRadioButton antiAliasing3;
    private JRadioButton radTexture;
    private JRadioButton radColor;
    private JTable objectListPanel;
    private JComboBox comboBox;
    private DefaultTableModel tableModel;
    private ImageJPanel imageTexture;
    private JButton AddTexButton;
    private JButton addСolButton;
    private JButton addСolLightButton;
    private ImageCG curTexture;
    private Color cur_color;
    private Color light_color;
    private boolean active_spinner_litener;
    private int antiAliasing_value;
    private float ambient;
    private Camera camera;

    private RenderSceneTriangle target = GetFrameBuffer();
    private RayTracing rt = new RayTracing();
    private int width;
    private int height;


    public Launcher(int width, int height, String title, int mode) throws IOException {
        this.width = width;
        this.height = height;
        active_spinner_litener = true;
        ambient = 0.1f;
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        workPerforming = mode;

        File f = new File("/Users/admin/Documents/cgcourse/src/main/resources/bricks.jpg");
        curTexture = new ImageCG(f, 0, 0, 0, 0);
        cur_color = Color.WHITE;
//               m_frame.add(this);

        m_frameBuffer = new RenderSceneTriangle(width, height);
        m_displayImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        m_displayComponents =
                ((DataBufferByte) m_displayImage.getRaster().getDataBuffer()).getData();

        m_frameBuffer.Clear((byte) 0x80);
        m_frameBuffer.DrawPixel(100, 100, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF);

        m_frame = new JFrame();
        m_frame.setLayout(null);
        m_frame.add(this);
        m_frame.setVisible(true);

        JPanel panel = new JPanel(new GridLayout(1, 1));
        System.out.println("BOUNDS" + panel.getBounds().height + " - " + panel.getBounds().width + " | " + this.getWidth() + " - " + this.getHeight());

        panel.add(this);
        JButton RenderButton = new JButton("Рендер");
        RenderButton.addActionListener(new RenderRayTracingListener());
        JButton AddButton = new JButton("Добавить");
        AddButton.addActionListener(new AddObjectListener());
        JButton LoadButton = new JButton("Загрузить");
        JButton AddLightButton = new JButton("Добавить");
        JButton DeleteButton = new JButton("Удалить");
        DeleteButton.addActionListener(new DeleteObjectListener());

        ////---------------/////
        //,
        //"Тор"
        String[] items_objects = {
                "Куб",
                "Сфера",
                "Цилиндр",
                "Пирамида",
                "Конус",
                "Плоскость"//,
                //"Тор"
        };
        comboBox = new JComboBox(items_objects);
        String[] items_light = {
                "Точечный источник"
        };
        JComboBox LightСomboBox = new JComboBox(items_light);

        String[] data1 = {"Куб1", "Пирамида1", "Конус2", "Сфера1"};
        JList<String> object_list = new JList<String>(data1);

        //object_list_panel = new JScrollPane(object_list);
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Имя"});
        objectListPanel = new JTable(tableModel);
        JScrollPane scr = new JScrollPane(objectListPanel);
        // Панель настроек объекта
        AddTexButton = new JButton("Добавить");
        addСolButton = new JButton();
        addСolLightButton = new JButton();
        JLabel LabelObjectList = new JLabel("Список объектов:");
        JLabel LabelObjectOption = new JLabel("Параметры объекта:");
        JLabel LabelObjects = new JLabel("Стандартные объекты:");
        JLabel LabelLight = new JLabel("Свет:");
        JLabel LabelLoad = new JLabel("Загрузить объект:");
        JLabel LabelAnti = new JLabel("Сглаживание:");
        JLabel LabelTrans = new JLabel("Позиция:");
        JLabel LabelTrans2 = new JLabel("Позиция:");
        JLabel LabelRot = new JLabel("Поворот:");
        JLabel LabelScale = new JLabel("Масштаб:");
        JLabel LabelX = new JLabel("X:");
        JLabel LabelY = new JLabel("Y:");
        JLabel LabelZ = new JLabel("Z:");
        JLabel LabelX2 = new JLabel("X:");
        JLabel LabelY2 = new JLabel("Y:");
        JLabel LabelZ2 = new JLabel("Z:");
        JLabel LabelIntence = new JLabel("Интенсивность:");
        JLabel LabelColorLight = new JLabel("Цвет:");
        JLabel ColorEx = new JLabel("DD");


        JLabel LabelRefl = new JLabel("Отражение:");
        JLabel LabelRefr = new JLabel("Преломление:");
        JLabel LabelOpacity = new JLabel("Прозрачность:");
        JLabel LabelSpecular = new JLabel("Блеск:");
        JLabel LabelAmbient = new JLabel("Окружающий свет:");

        SpinnerModel numbers = new SpinnerNumberModel();

        SpinnerNumberModel modeltrans1 = new SpinnerNumberModel(0.0, -100.0, 100.0, 0.1);
        JSpinner spinXtrans = new JSpinner(modeltrans1);
        SpinnerNumberModel modeltrans2 = new SpinnerNumberModel(0.0, -100.0, 100.0, 0.1);
        JSpinner spinYtrans = new JSpinner(modeltrans2);
        SpinnerNumberModel modeltrans3 = new SpinnerNumberModel(0.0, -100.0, 100.0, 0.1);
        JSpinner spinZtrans = new JSpinner(modeltrans3);

        SpinnerNumberModel modeltransLight1 = new SpinnerNumberModel(0.0, -100.0, 100.0, 0.1);
        JSpinner spinXtransLight = new JSpinner(modeltransLight1);
        SpinnerNumberModel modeltransLight2 = new SpinnerNumberModel(0.0, -100.0, 100.0, 0.1);
        JSpinner spinYtransLight = new JSpinner(modeltransLight2);
        SpinnerNumberModel modeltransLight3 = new SpinnerNumberModel(0.0, -100.0, 100.0, 0.1);
        JSpinner spinZtransLight = new JSpinner(modeltransLight3);
        SpinnerNumberModel model5 = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
        JSpinner spinIntenceLight = new JSpinner(model5);

        JSpinner spinXrot = new JSpinner();
        JSpinner spinYrot = new JSpinner();
        JSpinner spinZrot = new JSpinner();

        SpinnerNumberModel modelscale1 = new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1);
        JSpinner spinXscale = new JSpinner(modelscale1);
        SpinnerNumberModel modelscale2 = new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1);
        JSpinner spinYscale = new JSpinner(modelscale2);
        SpinnerNumberModel modelscale3 = new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1);
        JSpinner spinZscale = new JSpinner(modelscale3);

        SpinnerNumberModel model6 = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
        JSpinner spinAmbient = new JSpinner(model6);
        spinAmbient.setValue(ambient);

        SpinnerNumberModel model1 = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
        JSpinner spinRefl = new JSpinner(model1);
        SpinnerNumberModel model2 = new SpinnerNumberModel(0.0, 0.0, 10.0, 0.1);
        JSpinner spinRefr = new JSpinner(model2);
        SpinnerNumberModel model3 = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
        JSpinner spinOpacity = new JSpinner(model3);
        SpinnerNumberModel model4 = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
        JSpinner spinSpecular = new JSpinner(model4);


        ChangeListener listener = e -> {
            if (!active_spinner_litener)
                return;
            JSpinner js = (JSpinner) e.getSource();
            if (js == spinAmbient) {
                ambient = Float.parseFloat(spinAmbient.getValue().toString());
            }
            int i = objectListPanel.getSelectedRow();
            if (i < 0 || i >= complexObjectList.size())
                return;

            if (js == spinXtransLight || js == spinYtransLight || js == spinZtransLight || js == spinIntenceLight) {
                complexObjectList.get(i).trans = complexObjectList.get(i).trans.SetPos(new Vector4(Float.parseFloat(spinXtransLight.getValue().toString()), Float.parseFloat(spinYtransLight.getValue().toString()), Float.parseFloat(spinZtransLight.getValue().toString())));
                int k = 0;
                for (int j = 0; j < i; j++) {
                    System.out.println("objects.get(j).type == \"Точечный источник\"");
                    if ("Точечный источник".equals(complexObjectList.get(j).type)) k++;
                }
                lightSources.get(k).SetLightPosition(complexObjectList.get(i).trans.GetPos());
                lightSources.get(k).SetLightIntensive(Float.parseFloat(spinIntenceLight.getValue().toString()));
            }
            if (js == spinXtrans || js == spinYtrans || js == spinZtrans) {
                complexObjectList.get(i).trans = complexObjectList.get(i).trans.SetPos(new Vector4(Float.parseFloat(spinXtrans.getValue().toString()), Float.parseFloat(spinYtrans.getValue().toString()), Float.parseFloat(spinZtrans.getValue().toString())));
            }
            if (js == spinXrot || js == spinYrot || js == spinZrot) {
                complexObjectList.get(i).trans = complexObjectList.get(i).trans.RotateFromNull(Float.parseFloat(spinXrot.getValue().toString()), Float.parseFloat(spinYrot.getValue().toString()), Float.parseFloat(spinZrot.getValue().toString()));

            }
            if (js == spinXscale || js == spinYscale || js == spinZscale) {
                complexObjectList.get(i).trans = complexObjectList.get(i).trans.SetScale(new Vector4(Float.parseFloat(spinXscale.getValue().toString()), Float.parseFloat(spinYscale.getValue().toString()), Float.parseFloat(spinZscale.getValue().toString())));
            }
            if (js == spinRefl || js == spinRefr || js == spinOpacity || js == spinSpecular) {
                if (complexObjectList.get(i).tex_paint) {
                    complexObjectList.get(i).texture.opacity = Float.parseFloat(spinOpacity.getValue().toString());
                    complexObjectList.get(i).texture.refl = Float.parseFloat(spinRefl.getValue().toString());
                    complexObjectList.get(i).texture.refr = Float.parseFloat(spinRefr.getValue().toString());
                    complexObjectList.get(i).texture.specular = Float.parseFloat(spinSpecular.getValue().toString());

                } else {
                    complexObjectList.get(i).color.opacity = Float.parseFloat(spinOpacity.getValue().toString());
                    complexObjectList.get(i).color.special = Float.parseFloat(spinRefl.getValue().toString());
                    complexObjectList.get(i).color.refr_koef = Float.parseFloat(spinRefr.getValue().toString());
                    complexObjectList.get(i).color.specular = Float.parseFloat(spinSpecular.getValue().toString());
                }
            }
        };
        spinXtrans.addChangeListener(listener);
        spinYtrans.addChangeListener(listener);
        spinZtrans.addChangeListener(listener);

        spinXtransLight.addChangeListener(listener);
        spinYtransLight.addChangeListener(listener);
        spinZtransLight.addChangeListener(listener);
        spinIntenceLight.addChangeListener(listener);

        spinXrot.addChangeListener(listener);
        spinYrot.addChangeListener(listener);
        spinZrot.addChangeListener(listener);

        spinXscale.addChangeListener(listener);
        spinYscale.addChangeListener(listener);
        spinZscale.addChangeListener(listener);
        spinAmbient.addChangeListener(listener);

        spinRefl.addChangeListener(listener);
        spinRefr.addChangeListener(listener);
        spinOpacity.addChangeListener(listener);
        spinSpecular.addChangeListener(listener);

        JPanel panel_property = new JPanel(null);
        panel_property.add(spinXtrans);
        panel_property.add(spinYtrans);
        panel_property.add(spinZtrans);

        panel_property.add(spinXrot);
        panel_property.add(spinYrot);
        panel_property.add(spinZrot);


        panel_property.add(spinRefl);
        panel_property.add(spinRefr);
        panel_property.add(spinOpacity);
        panel_property.add(spinSpecular);

        panel_property.add(spinXscale);
        panel_property.add(spinYscale);
        panel_property.add(spinZscale);

        panel_property.add(LabelTrans);
        panel_property.add(LabelRot);
        panel_property.add(LabelScale);

        panel_property.add(LabelX);
        panel_property.add(LabelY);
        panel_property.add(LabelZ);

        panel_property.add(LabelRefl);
        panel_property.add(LabelRefr);
        panel_property.add(LabelOpacity);
        panel_property.add(LabelSpecular);
        panel_property.add(addСolButton);

        panel_property.add(AddTexButton);

        LabelTrans.setBounds(25 + panel_property.getInsets().left, 0 + panel_property.getInsets().top,
                80, 25);
        LabelRot.setBounds(95 + panel_property.getInsets().left, 0 + panel_property.getInsets().top,
                80, 25);
        LabelScale.setBounds(160 + panel_property.getInsets().left, 0 + panel_property.getInsets().top,
                80, 25);


        LabelX.setBounds(0 + panel_property.getInsets().left, 25 + panel_property.getInsets().top,
                25, 25);
        LabelY.setBounds(0 + panel_property.getInsets().left, 50 + panel_property.getInsets().top,
                25, 25);
        LabelZ.setBounds(0 + panel_property.getInsets().left, 75 + panel_property.getInsets().top,
                25, 25);

        spinXtrans.setBounds(25 + panel_property.getInsets().left, 25 + panel_property.getInsets().top,
                60, 25);
        spinYtrans.setBounds(25 + panel_property.getInsets().left, 50 + panel_property.getInsets().top,
                60, 25);
        spinZtrans.setBounds(25 + panel_property.getInsets().left, 75 + panel_property.getInsets().top,
                60, 25);

        spinXrot.setBounds(89 + panel_property.getInsets().left, 25 + panel_property.getInsets().top,
                60, 25);
        spinYrot.setBounds(89 + panel_property.getInsets().left, 50 + panel_property.getInsets().top,
                60, 25);
        spinZrot.setBounds(89 + panel_property.getInsets().left, 75 + panel_property.getInsets().top,
                60, 25);

        spinXscale.setBounds(153 + panel_property.getInsets().left, 25 + panel_property.getInsets().top,
                60, 25);
        spinYscale.setBounds(153 + panel_property.getInsets().left, 50 + panel_property.getInsets().top,
                60, 25);
        spinZscale.setBounds(153 + panel_property.getInsets().left, 75 + panel_property.getInsets().top,
                60, 25);


        radTexture = new JRadioButton("Текстура:");
        radColor = new JRadioButton("Цвет:");
        ButtonGroup group1 = new ButtonGroup();
        group1.add(radTexture);
        group1.add(radColor);
        panel_property.add(radTexture);
        panel_property.add(radColor);
        radTexture.setBounds(0 + panel_property.getInsets().left, 110 + panel_property.getInsets().top,
                90, 25);
        AddTexButton.setBounds(0 + panel_property.getInsets().left, 135 + panel_property.getInsets().top,
                90, 25);

        // Выбор текстуры
        ActionListener sliceActionListener;
        sliceActionListener = actionEvent -> {
            AbstractButton aButton = (AbstractButton) actionEvent.getSource();
            System.out.println("aButton.getText() == \"Цвет:\"");
            if ("Цвет:".equals(aButton.getText())) {
                AddTexButton.setEnabled(false);
                addСolButton.setEnabled(true);
                int i = objectListPanel.getSelectedRow();
                if (i < 0)
                    return;

                //System.out.println(cur_color.getRed());
                ColorCG new_color = new ColorCG(cur_color.getRed() / 255.f, cur_color.getGreen() / 255.f, cur_color.getBlue() / 255.f, 0, 0, 0, 0);
                complexObjectList.get(i).color = new_color;
                complexObjectList.get(i).tex_paint = false;
                complexObjectList.get(i).color.opacity = Float.parseFloat(spinOpacity.getValue().toString());
                complexObjectList.get(i).color.special = Float.parseFloat(spinRefl.getValue().toString());
                complexObjectList.get(i).color.refr_koef = Float.parseFloat(spinRefr.getValue().toString());

            }
            System.out.println("aButton.getText() == \"Текстура:\"");
            if ("Текстура:".equals(aButton.getText())) {
                AddTexButton.setEnabled(true);
                addСolButton.setEnabled(false);
                int i = objectListPanel.getSelectedRow();
                if (i < 0)
                    return;
                complexObjectList.get(i).texture = curTexture;
                complexObjectList.get(i).tex_paint = true;
                complexObjectList.get(i).texture.opacity = Float.parseFloat(spinOpacity.getValue().toString());
                complexObjectList.get(i).texture.refl = Float.parseFloat(spinRefl.getValue().toString());
                complexObjectList.get(i).texture.refr = Float.parseFloat(spinRefr.getValue().toString());
                //objects.get(i).color = null;
            }
            System.out.println("aButton.getText() == \"1 пиксель\"");
            if ("1 пиксель".equals(aButton.getText())) {
                antiAliasing_value = 1;
            }
            if ("2 пикселя".equals(aButton.getText())) {
                antiAliasing_value = 2;
            }
            if ("3 пикселей".equals(aButton.getText())) {
                antiAliasing_value = 3;
            }
        };
        radTexture.addActionListener(sliceActionListener);
        radColor.addActionListener(sliceActionListener);
        AddTexButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "pictures", "jpg", "jpeg", "png");
                JFileChooser fileopen = new JFileChooser();
                fileopen.setFileFilter(filter);
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    String fname = file.getAbsolutePath();
                    if (!fname.endsWith(".png") && !fname.endsWith(".jpg") && !fname.endsWith(".jpeg")) {
                        return;
                    }
                    imageTexture.UploadImage(file);
                    imageTexture.repaint();
                    try {
                        curTexture = new ImageCG(file, 0, 0, 0, 0);
                        int i = objectListPanel.getSelectedRow();
                        if (i < 0)
                            return;
                        complexObjectList.get(i).texture = curTexture;
                        complexObjectList.get(i).color = null;
                        complexObjectList.get(i).texture.opacity = Float.parseFloat(spinOpacity.getValue().toString());
                        complexObjectList.get(i).texture.refl = Float.parseFloat(spinRefl.getValue().toString());
                        complexObjectList.get(i).texture.refr = Float.parseFloat(spinRefr.getValue().toString());
                    } catch (IOException ex) {
                        Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        imageTexture = new ImageJPanel(60, 60);
        panel_property.add(imageTexture);
        try {
            imageTexture.UploadImage(f);
            imageTexture.repaint();
            ImageCG texture = new ImageCG(f, 0, 0, 0, 0);
        } catch (IOException ignored) {
        }
        imageTexture.setBounds(100 + panel_property.getInsets().left, 130 + panel_property.getInsets().top,
                60, 60);
        radColor.setBounds(0 + panel_property.getInsets().left, 190 + panel_property.getInsets().top,
                60, 25);
        addСolButton.setBounds(0 + panel_property.getInsets().left, 218 + panel_property.getInsets().top,
                90, 25);
        addСolButton.setBackground(cur_color);

        int sub = 20;
        spinRefl.setBounds(0 + panel_property.getInsets().left, 300 - sub + panel_property.getInsets().top,
                65, 25);
        spinRefr.setBounds(95 + panel_property.getInsets().left, 300 - sub + panel_property.getInsets().top,
                65, 25);
        spinOpacity.setBounds(0 + panel_property.getInsets().left, 350 - sub + panel_property.getInsets().top,
                65, 25);

        spinSpecular.setBounds(95 + panel_property.getInsets().left, 350 - sub + panel_property.getInsets().top,
                65, 25);

        LabelRefl.setBounds(0 + panel_property.getInsets().left, 280 - sub + panel_property.getInsets().top,
                80, 25);
        LabelRefr.setBounds(95 + panel_property.getInsets().left, 280 - sub + panel_property.getInsets().top,
                85, 25);
        LabelOpacity.setBounds(0 + panel_property.getInsets().left, 325 - sub + panel_property.getInsets().top,
                86, 25);
        LabelSpecular.setBounds(95 + panel_property.getInsets().left, 325 - sub + panel_property.getInsets().top,
                86, 25);

        addСolButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color get_color = JColorChooser.showDialog(null, "Choose", Color.RED);
                if (get_color == null)
                    return;
                cur_color = get_color;
                addСolButton.setBackground(cur_color);
                int i = objectListPanel.getSelectedRow();
                if (i < 0)
                    return;

                complexObjectList.get(i).color = new ColorCG(cur_color.getRed() / 255.f, cur_color.getGreen() / 255.f, cur_color.getBlue() / 255.f, 0, 0, 0, 0);
                complexObjectList.get(i).tex_paint = false;
                complexObjectList.get(i).color.opacity = Float.parseFloat(spinOpacity.getValue().toString());
                complexObjectList.get(i).color.special = Float.parseFloat(spinRefl.getValue().toString());
                complexObjectList.get(i).color.refr_koef = Float.parseFloat(spinRefr.getValue().toString());
                complexObjectList.get(i).color.specular = Float.parseFloat(spinSpecular.getValue().toString());
            }
        });

        JPanel panel_property_light = new JPanel(null);
        panel_property_light.add(spinXtransLight);
        panel_property_light.add(spinYtransLight);
        panel_property_light.add(spinZtransLight);
        panel_property_light.add(spinIntenceLight);
        panel_property_light.add(addСolLightButton);
        panel_property_light.add(LabelTrans2);

        panel_property_light.add(LabelX2);
        panel_property_light.add(LabelY2);
        panel_property_light.add(LabelZ2);
        panel_property_light.add(LabelIntence);
        panel_property_light.add(LabelColorLight);
        LabelTrans2.setBounds(25 + panel_property.getInsets().left, 0 + panel_property.getInsets().top,
                80, 25);

        LabelX2.setBounds(0 + panel_property.getInsets().left, 25 + panel_property.getInsets().top,
                25, 25);
        LabelY2.setBounds(0 + panel_property.getInsets().left, 50 + panel_property.getInsets().top,
                25, 25);
        LabelZ2.setBounds(0 + panel_property.getInsets().left, 75 + panel_property.getInsets().top,
                25, 25);
        LabelIntence.setBounds(90 + panel_property.getInsets().left, 0 + panel_property.getInsets().top,
                100, 25);

        spinXtransLight.setBounds(25 + panel_property.getInsets().left, 25 + panel_property.getInsets().top,
                60, 25);
        spinYtransLight.setBounds(25 + panel_property.getInsets().left, 50 + panel_property.getInsets().top,
                60, 25);
        spinZtransLight.setBounds(25 + panel_property.getInsets().left, 75 + panel_property.getInsets().top,
                60, 25);
        spinIntenceLight.setBounds(90 + panel_property.getInsets().left, 25 + panel_property.getInsets().top,
                60, 25);

        LabelColorLight.setBounds(25 + panel_property.getInsets().left, 110 + panel_property.getInsets().top,
                60, 25);
        addСolLightButton.setBounds(25 + panel_property.getInsets().left, 135 + panel_property.getInsets().top,
                60, 25);
        addСolLightButton.addActionListener(e -> {
            Color get_color = JColorChooser.showDialog(null, "Choose", Color.RED);
            if (get_color == null)
                return;
            light_color = get_color;
            addСolLightButton.setBackground(light_color);
            int i = objectListPanel.getSelectedRow();
            if (i < 0)
                return;

            ColorCG new_color = new ColorCG(light_color.getRed() / 255.f, light_color.getGreen() / 255.f, light_color.getBlue() / 255.f, 0, 0, 0, 0);
            int k = 0;
            for (int j = 0; j < i; j++) {
                System.out.println("objects.get(j).type == \"Точечный источник\" 1");
                if (complexObjectList.get(j).type.equals("Точечный источник")) k++;
            }
            //System.out.println(k+ "  dfdfdfdfddfd   "+  i);
            lightSources.get(k).SetLightColor(new_color);
        });

        objectListPanel.getSelectionModel().addListSelectionListener(event -> {
            int i = objectListPanel.getSelectedRow();
            if (i > -1) {
                active_spinner_litener = false;
                System.out.println("objects.get(j).type == \"Точечный источник\" 2");
                if (!complexObjectList.get(i).type.equals("Точечный источник")) {
                    m_frame.remove(panel_property_light);
                    m_frame.add(panel_property);
                    m_frame.repaint();
                    m_frame.revalidate();
                    spinXtrans.setValue((int) complexObjectList.get(i).trans.GetPos().GetX());
                    spinYtrans.setValue((int) complexObjectList.get(i).trans.GetPos().GetY());
                    spinZtrans.setValue((int) complexObjectList.get(i).trans.GetPos().GetZ());

                    spinXrot.setValue((int) complexObjectList.get(i).trans.GetEulerRot().GetX());
                    spinYrot.setValue((int) complexObjectList.get(i).trans.GetEulerRot().GetY());
                    spinZrot.setValue((int) complexObjectList.get(i).trans.GetEulerRot().GetZ());

                    spinXscale.setValue((int) complexObjectList.get(i).trans.GetScale().GetX());
                    spinYscale.setValue((int) complexObjectList.get(i).trans.GetScale().GetY());
                    spinZscale.setValue((int) complexObjectList.get(i).trans.GetScale().GetZ());


                    if (!complexObjectList.get(i).tex_paint) {
                        radColor.setSelected(true);
                        cur_color = new Color(complexObjectList.get(i).color.red, complexObjectList.get(i).color.green, complexObjectList.get(i).color.blue);
                        addСolButton.setBackground(cur_color);
                        spinRefl.setValue(complexObjectList.get(i).color.special);
                        spinRefr.setValue(complexObjectList.get(i).color.refr_koef);
                        spinOpacity.setValue(complexObjectList.get(i).color.opacity);
                        spinSpecular.setValue(complexObjectList.get(i).color.specular);
                    } else {
                        radTexture.setSelected(true);
                        spinRefl.setValue(complexObjectList.get(i).texture.refl);
                        spinRefr.setValue(complexObjectList.get(i).texture.refr);
                        spinOpacity.setValue(complexObjectList.get(i).texture.opacity);
                        spinSpecular.setValue(complexObjectList.get(i).texture.specular);
                    }
                } else {
                    m_frame.remove(panel_property);
                    m_frame.add(panel_property_light);
                    m_frame.repaint();
                    m_frame.revalidate();
                    spinXtransLight.setValue((int) complexObjectList.get(i).trans.GetPos().GetX());
                    spinYtransLight.setValue((int) complexObjectList.get(i).trans.GetPos().GetY());
                    spinZtransLight.setValue((int) complexObjectList.get(i).trans.GetPos().GetZ());
                    int k = 0;
                    for (int j = 0; j < i; j++) {
                        System.out.println("objects.get(j).type == \"Точечный источник\" 3");
                        if ("Точечный источник".equals(complexObjectList.get(j).type)) k++;
                    }
                    light_color = new Color(lightSources.get(k).getLightColor().red, lightSources.get(k).getLightColor().green, lightSources.get(k).getLightColor().blue);
                    addСolLightButton.setBackground(light_color);
                    spinIntenceLight.setValue(lightSources.get(k).getLightIntensive());
                }
                active_spinner_litener = true;
            }
        });

        // -----------

        antiAliasing1 = new JRadioButton("1 пиксель");
        antiAliasing2 = new JRadioButton("2 пикселя");
        antiAliasing3 = new JRadioButton("3 пикселей");
        antiAliasing_value = 1;
        ButtonGroup group2 = new ButtonGroup();
        group2.add(antiAliasing1);
        group2.add(antiAliasing2);
        group2.add(antiAliasing3);
        antiAliasing1.setSelected(true);
        antiAliasing1.addActionListener(sliceActionListener);
        antiAliasing2.addActionListener(sliceActionListener);
        antiAliasing3.addActionListener(sliceActionListener);

        m_frame.add(panel);
        m_frame.add(RenderButton);
//            m_frame.add(m_graphics);
        m_frame.add(scr);
        m_frame.add(comboBox);
        m_frame.add(LabelObjectList);
        m_frame.add(LabelObjectOption);
        m_frame.add(LabelObjects);
        m_frame.add(AddButton);
        m_frame.add(DeleteButton);
        m_frame.add(LabelAnti);
        m_frame.add(LoadButton);
        m_frame.add(LightСomboBox);
        m_frame.add(AddLightButton);
        m_frame.add(antiAliasing1);
        m_frame.add(antiAliasing2);
        m_frame.add(antiAliasing3);
        m_frame.add(LabelLoad);
        m_frame.add(spinAmbient);
        m_frame.add(LabelAmbient);
        m_frame.add(new JLabel(new ImageIcon(ImageIO.read(new File("/Users/admin/Downloads/course_work_cg-master/CG_programm/image0.jpg")))));


        m_frame.add(LabelLight);

        m_frame.pack();
        m_frame.setResizable(false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int sizeWidth = 1250;
        int sizeHeight = 700;
        int locationX = (screenSize.width - sizeWidth) / 2;
        int locationY = (screenSize.height - sizeHeight) / 2;
        m_frame.setBounds(locationX, locationY, sizeWidth, sizeHeight);
        m_frame.setLocation(0, 0);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.setLocationRelativeTo(null);
        m_frame.setTitle(title);
        //m_frame.setSize(sizeWidth, sizeHeight);
        m_frame.setVisible(true);


        Insets insets = m_frame.getInsets();


        panel.setBounds(180 + insets.left, 0 + insets.top,
                width, height);

        // left part
        int left_borger = 11;
        LabelLoad.setBounds(left_borger + insets.left, 0 + insets.top,
                150, 20);
        LoadButton.setBounds(left_borger + insets.left, 22 + insets.top,
                150, 30);
        ////////
        LabelObjects.setBounds(left_borger + insets.left, 65 + insets.top,
                150, 30);
        comboBox.setBounds(left_borger + insets.left, 95 + insets.top,
                150, 30);
        AddButton.setBounds(left_borger + insets.left, 125 + insets.top,
                150, 30);
        //////////
        int y_border = 155;
        LabelLight.setBounds(left_borger + insets.left, y_border + 10 + insets.top,
                150, 30);
        LightСomboBox.setBounds(left_borger + insets.left, y_border + 40 + insets.top,
                150, 30);
        AddLightButton.setBounds(left_borger + insets.left, y_border + 70 + insets.top,
                150, 30);

        AddLightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("AddLightButton actionPerformed called");
                addNewObject("Точечный источник", "/Users/admin/Documents/cgcourse/src/main/resources/icosphere.obj", new Vector4(0, 6, 0, 1), new Vector4(0.1f, 0.1f, 0.1f, 1), new ColorCG(1.0f, 1.0f, 1.f, 0, 0, 0, 0));
                Light scene_light = new Light(new Vector4(0, 6, 0), new ColorCG(1, 1, 1, 0, 0, 0, 0), 1);
                lightSources.add(scene_light);
            }
        });
        ///////
        LabelAnti.setBounds(left_borger + insets.left, 290 + insets.top,
                150, 20);
        antiAliasing1.setBounds(left_borger + insets.left, 310 + insets.top,
                150, 20);
        antiAliasing2.setBounds(left_borger + insets.left, 330 + insets.top,
                150, 20);
        antiAliasing3.setBounds(left_borger + insets.left, 350 + insets.top,
                150, 20);
        LabelAmbient.setBounds(left_borger + insets.left, 380 + insets.top,
                150, 20);
        spinAmbient.setBounds(left_borger + insets.left, 400 + insets.top,
                60, 25);

        RenderButton.setBounds(left_borger + insets.left, 430 + insets.top,
                150, 30);

        // right part
        int left_botder2 = 1000;
        LoadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("LoadButton actionPerformed called");
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "OBJ", "obj");
                JFileChooser fileopen = new JFileChooser();
                fileopen.setFileFilter(filter);
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    Transform NewTransform = new Transform(new Vector4(0, 0.0f, 0.0f), new Vector4(1, 1, 1, 1));
                    try {
                        String name = "figure" + find_col_name(complexObjectList, "figure");
                        ComplexObject NewMesh = new ComplexObject(file.getPath(), NewTransform, "figure", new ColorCG(1.0f, 1.0f, 1.0f));
                        complexObjectList.add(NewMesh);
                        tableModel.addRow(new String[]{name});
                    } catch (IOException ex) {
                        Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        DeleteButton.setBounds(left_botder2 + 112 + insets.left, 0 + insets.top,
                85, 20);

        LabelObjectList.setBounds(left_botder2 + insets.left, 0 + insets.top,
                150, 20);
        scr.setBounds(left_botder2 + insets.left, 23 + insets.top,
                200, 200);

        LabelObjectOption.setBounds(left_botder2 + insets.left, 220 + insets.top,
                150, 20);

        panel_property.setBounds(left_botder2 + insets.left, 240 + insets.top,
                300, 400);

        panel_property_light.setBounds(left_botder2 + insets.left, 240 + insets.top,
                300, 400);

        createBufferStrategy(1);
        m_bufferStrategy = getBufferStrategy();
        m_graphics = m_bufferStrategy.getDrawGraphics();

        mInput = new Input();
        addKeyListener(mInput);
        addFocusListener(mInput);
        addMouseListener(mInput);
        addMouseMotionListener(mInput);
        setFocusable(true);
        requestFocus();
    }

    private static void AddMesheToObject(List<ComplexObject> objects, List<PrimitiveObject> sceneObjects) { // Matrix4f vp, Matrix4f v убрать
        for (ComplexObject complexObject : objects) {
            System.out.println("objects.get(i).type == \"Точечный источник\" 4 " + complexObject);
            if (complexObject.type.equals("Точечный источник"))
                continue;
            if (complexObject.type.equals("Сфера")) {
                Sphere scene_sphere;
                if (complexObject.tex_paint)
                    scene_sphere = new Sphere(new Vector4(0f, 0f, 0f), 1f, complexObject.trans, complexObject.texture);
                else
                    scene_sphere = new Sphere(new Vector4(0f, 0f, 0f), 1f, complexObject.trans, complexObject.color);
                sceneObjects.add(scene_sphere);
            } else if (complexObject.type.equals("Тор")) {
                Torus tor;
                if (complexObject.tex_paint)
                    tor = new Torus(1, 0.3f, complexObject.trans, complexObject.texture);
                else
                    tor = new Torus(1, 0.3f, complexObject.trans, complexObject.color);
                sceneObjects.add(tor);

            } else {
                complexObject.Add_to_objects(sceneObjects);
            }
        }
    }

    private RenderSceneTriangle GetFrameBuffer() {
        return m_frameBuffer;
    }

    private Input GetInput() {
        return mInput;
    }

    @Override
    public void paint(Graphics g) {
//            System.out.println("paint called");
//            counter++;
//               setBackground(Color.WHITE);
//            g.fillOval(130 + counter, 130 + counter,50, 60);
//            setForeground(Color.RED);
        int x = (getWidth() - m_displayImage.getWidth()) / 2;
        int y = (getHeight() - m_displayImage.getHeight()) / 2;
        g.drawImage(m_displayImage, x, y, this);
    }

    public void drawObjects(RenderSceneTriangle target, Matrix vp, List<ComplexObject> objects, List<Source> lights) throws IOException {
//            System.out.println("drawObjects called");
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).Draw(target, vp, lights);
        }
        Transform tr1 = new Transform(new Vector4(0, 0, 0), new Vector4(1f, 1f, 1f, 1));
        ComplexObject axis1 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/grid1.obj", tr1, "grid", new ColorCG(1, 0, 0));
        Transform tr2 = new Transform(new Vector4(0, 0, 0), new Vector4(1f, 1f, 1f, 1));
        tr2 = tr2.RotateFromNull(90, 0, 0);
        ComplexObject axis2 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/grid1.obj", tr2, "grid", new ColorCG(0, 1, 0));
        Transform tr3 = new Transform(new Vector4(0, 0, 0), new Vector4(1f, 1f, 1f, 1));
        tr3 = tr3.RotateFromNull(0, 90, 0);
        ComplexObject axis3 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/grid1.obj", tr3, "grid", new ColorCG(0, 0, 1));

        axis1.Draw(target, vp, lights);
        axis2.Draw(target, vp, lights);
        axis3.Draw(target, vp, lights);

        int i = objectListPanel.getSelectedRow();
        if (i < 0)
            return;
        Transform object_transform = objects.get(i).trans;

        Transform tr11 = new Transform(new Vector4(0, 0, 0), new Vector4(1f, 1f, 1f, 1));
        tr11 = tr11.SetPos(object_transform.GetPos()).Rotate(object_transform.GetRot());
        if (object_transform.GetScale().GetX() > 1)
            tr11 = tr11.SetScale(object_transform.GetScale());
        ComplexObject axis11 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/axis_ob.obj", tr11, "axis_ob", new ColorCG(0, 0, 1));
        Transform tr21 = new Transform(new Vector4(0, 0, 0), new Vector4(1f, 1f, 1f, 1));
        tr21 = tr21.RotateFromNull(0, 0, 90);

        tr21 = tr21.SetPos(object_transform.GetPos()).Rotate(object_transform.GetRot());
        if (object_transform.GetScale().GetY() > 1)
            tr21 = tr21.SetScale(new Vector4(object_transform.GetScale().GetY(), 1, 1, 1));
        ComplexObject axis21 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/axis_ob.obj", tr21, "axis_ob", new ColorCG(0, 1, 0));
        Transform tr31 = new Transform(new Vector4(0, 0, 0), new Vector4(1f, 1f, 1f, 1));
        tr31 = tr31.RotateFromNull(0, 90, 0);
        tr31 = tr31.SetPos(object_transform.GetPos()).Rotate(object_transform.GetRot());
        if (object_transform.GetScale().GetY() > 1)
            tr31 = tr31.SetScale(new Vector4(object_transform.GetScale().GetZ(), 1, 1, 1));
        ComplexObject axis31 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/axis_ob.obj", tr31, "axis_ob", new ColorCG(1, 0, 0));
        axis11.Draw(target, vp, lights);
        axis21.Draw(target, vp, lights);
        axis31.Draw(target, vp, lights);
    }

    public void Run(int width, int height) throws IOException, InterruptedException {

        complexObjectList = new ArrayList<>();
        lightSources = new ArrayList<>();
        lightSourcesWork = new ArrayList<>();
        sceneObjects = new ArrayList<>();

        Transform SphereTransform2 = new Transform(new Vector4(4, -3, 0), new Vector4(1f, 1f, 1f, 1));
        ComplexObject SphereMesh2 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/sphere.obj", SphereTransform2, "Сфера", new ColorCG(0, 1, 1, 0.5f, 0.5f, 0, 0));

        Transform SphereTransform22 = new Transform(new Vector4(3, 0, 0), new Vector4(1f, 1f, 1f, 1));
        ComplexObject SphereMesh22 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/sphere.obj", SphereTransform22, "Сфера", new ColorCG(1, 0, 1, 0.5f, 0.5f, 0f, 0f));

        Transform SphereTransform23 = new Transform(new Vector4(4, 3, 0), new Vector4(1f, 1f, 1f, 1));
        ComplexObject SphereMesh23 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/sphere.obj", SphereTransform23, "Сфера", new ColorCG(1, 1, 0, 0.5f, 0.5f, 0, 0));

        Transform CubeTransform2 = new Transform(new Vector4(0, 0, 0), new Vector4(1f, 1f, 1f, 1));
        ComplexObject CubeMesh2 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/cube.obj", CubeTransform2, "Куб", new ColorCG(1, 1, 0, 0, 0.5f, 0f, 0f));

        Transform planet = new Transform(new Vector4(0, -6, 0), new Vector4(8f, 8f, 8f, 1));
        ComplexObject plane = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/plane.obj", planet, "Плоскость", new ColorCG(1, 1, 1, 0, 0.5f, 0, 0));

        Transform planet2 = new Transform(new Vector4(3, 6, 0), new Vector4(1f, 1f, 1f, 1));
        ComplexObject plane2 = new ComplexObject("/Users/admin/Documents/cgcourse/src/main/resources/plane.obj", planet2, "Плоскость", new ColorCG(1, 1, 1, 0, 1.9f, 0.5f, 0.5f));

            /*objects.add(CubeMesh2);
            objects.add(SphereMesh2);
            objects.add(SphereMesh22);
            objects.add(SphereMesh23);
            objects.add(plane);*/
        //objects.add(plane2);
        camera = new Camera(new Matrix().CreatePerspective((float) Math.toRadians(50.0f), // 50 поправить бы но хз как
                (float) target.GetWidth() / (float) target.GetHeight(), 0.1f, 100.0f));
        camera.Move(camera.getCameraDirection(), -1);
        camera.Rotate(camera.getCameraRight(), (float) Math.PI * 10.f / 180.f);
        camera.Move(camera.getCameraDirection(), -6);
        //Matrix4f vp = camera.GetViewProjection();
        target.Clear((byte) 0x00);
        target.NewZBuffer();
        ColorCG white_light = new ColorCG(1.0f, 1.0f, 1.0f, 0, 0, 0, 0);
        ColorCG white_light2 = new ColorCG(1.0f, 1.0f, 1.0f, 0, 0, 0, 0);
        Light scene_light = new Light(new Vector4(7, 4f, -5), white_light2, 1);
        Light scene_light2 = new Light(new Vector4(6, 30, 10), white_light, 1);
        Light scene_light3 = new Light(new Vector4(7, -4, 10), white_light, 1);
        //light_sources.add(scene_light);
        //light_sources.add(scene_light3);
        lightSourcesWork.add(scene_light2);

        update();
    }

    private void update() {
        if (workPerforming == 1) {
            long previousTime = System.nanoTime();
            long currentTime = previousTime;
            float deltaCam = (float) ((currentTime - previousTime) / 1000000000.0);
//            previousTime = currentTime;

            camera.Update(this.GetInput(), deltaCam);
            Matrix vp = camera.GetViewProjection();
            target.Clear((byte) 0x00);
            target.NewZBuffer();

            lightSourcesWork.get(0).SetLightPosition(camera.getCameraDirection().Negative());
            try {
                drawObjects(target, vp, complexObjectList, lightSourcesWork);
            } catch (IOException e) {
                e.printStackTrace();
            }

            swapBuffers();
        }

        sceneObjects.clear();
        AddMesheToObject(complexObjectList, sceneObjects);

        target.Clear((byte) 0x00);
        target.NewZBuffer();

        rt.renderRayTracing(target, width, height, camera, sceneObjects, lightSources, ambient, antiAliasing_value);

        swapBuffers();
    }

    public void swapBuffers() {
        System.out.println("swapping");
        BufferStrategy bufferStrategy = m_frame.getBufferStrategy();
        if (bufferStrategy == null) {
            m_frame.createBufferStrategy(4);
            return;
        }
        m_frameBuffer.CopyToByteArray(m_displayComponents);
        m_graphics.drawImage(m_displayImage, 0, 0,
                m_frameBuffer.GetWidth(), m_frameBuffer.GetHeight(), null);
        m_bufferStrategy.show();

//                            g.drawImage(m_displayImage, 0, 0,
//                                    m_frameBuffer.GetWidth(), m_frameBuffer.GetHeight(), null);
//                            g.dispose();
        this.repaint();

//                            panel.paint(g);
//                            this.paint(g);
//                            m_frame.paint(g);
//                            this.paint(g);

//                            m_frame.add(new JLabel(new ImageIcon(m_displayImage)));


//                            m_frame.pack();
    }

    void addNewObject(String str, String path, Vector4 pos, Vector4 scale, ColorCG cl) {
        System.out.println("addNewObject called");
        ComplexObject NewMesh;
        Transform NewTransform;
        NewTransform = new Transform(pos, scale);
        try {
            String name = str + find_col_name(complexObjectList, str);
            NewMesh = new ComplexObject(path, NewTransform, str, cl);
            complexObjectList.add(NewMesh);
            AddMesheToObject(complexObjectList, sceneObjects);
            tableModel.addRow(new String[]{name});
        } catch (IOException ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    int find_col_name(List<ComplexObject> objects, String name) {
        int k = 0;
        for (int i = 0; i < objects.size(); i++)
            if (objects.get(i).type.equals(name)) k++;
        return k;
    }

    public class RenderRayTracingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Кнопка робит " + workPerforming);
            if (workPerforming == 1) {
                workPerforming = 2;
            } else {
                workPerforming = 1;
            }
        }
    }

    public class DeleteObjectListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            int i = objectListPanel.getSelectedRow();

            if (i < 0)
                return;
            tableModel.removeRow(i);
            if (complexObjectList.get(i).type == "Точечный источник") {
                int k = 0;
                for (int j = 0; j < i; j++) {
                    if (complexObjectList.get(j).type == "Точечный источник") k++;
                }
                lightSources.remove(k);
            }
            complexObjectList.remove(i);

        }

    }

    public class AddObjectListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String str = (String) comboBox.getSelectedItem();

            switch (str) {
                case "Куб":
                    addNewObject(str, "/Users/admin/Documents/cgcourse/src/main/resources/cube.obj", new Vector4(0, 0, 0, 1), new Vector4(1, 1, 1, 1), new ColorCG(1.0f, 1.0f, 1.0f));
                    break;
                case "Сфера":
                    addNewObject(str, "/Users/admin/Documents/cgcourse/src/main/resources/sphere.obj", new Vector4(0, 0, 0, 1), new Vector4(1, 1, 1, 1), new ColorCG(1.0f, 1.0f, 1.0f));
                    break;
                case "Конус":
                    addNewObject(str, "/Users/admin/Documents/cgcourse/src/main/resources/conus2.obj", new Vector4(0, 0, 0, 1), new Vector4(1, 1, 1, 1), new ColorCG(1.0f, 1.0f, 1.0f));
                    break;
                case "Цилиндр":
                    addNewObject(str, "/Users/admin/Documents/cgcourse/src/main/resources/cylinder2.obj", new Vector4(0, 0, 0, 1), new Vector4(1, 1, 1, 1), new ColorCG(1.0f, 1.0f, 1.0f));
                    break;
                case "Плоскость":
                    addNewObject(str, "/Users/admin/Documents/cgcourse/src/main/resources/plane.obj", new Vector4(0, 0, 0, 1), new Vector4(1, 1, 1, 1), new ColorCG(1.0f, 1.0f, 1.0f));
                    break;
                case "Пирамида":
                    addNewObject(str, "/Users/admin/Documents/cgcourse/src/main/resources/pyramyd.obj", new Vector4(0, 0, 0, 1), new Vector4(1, 1, 1, 1), new ColorCG(1.0f, 1.0f, 1.0f));
                    break;
                case "Тор":
                    addNewObject(str, "/Users/admin/Documents/cgcourse/src/main/resources/tor2.obj", new Vector4(0, 0, 0, 1), new Vector4(1, 1, 1, 1), new ColorCG(1.0f, 1.0f, 1.0f));
                    break;

            }
        }
    }
}


class ImageJPanel extends JPanel {

    public int width, height;
    private Image image;
    private File filename;

    public ImageJPanel(int wid, int heig) {
        this.setSize(wid, heig);
        try {

            width = wid;
            height = heig;
            image = ImageIO.read(new File("/Users/admin/Documents/cgcourse/src/main/resources/bricks.jpg"));

        } catch (IOException exception) {
            System.out.println("error no image");
        }
    }

    public void UploadImage(File f) {
        filename = f;
        try {

            Image newImage = ImageIO.read(filename);
            if (newImage == null)
                return;
            image = newImage;
            image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (IOException exception) {
            System.out.println("error no image");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

}
