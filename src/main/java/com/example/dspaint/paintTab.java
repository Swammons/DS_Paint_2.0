public class paintTab {
    public BorderPane paintTabInstance(FileInputStream fileImputStream){
        BorderPane borderPane = new BorderPane();
        ScrollPane scrollPane = new ScrollPane();
        StackPane stackPane = new StackPane();

    }
    public Toolbar tabToolBar(){
        ColorPicker lineColorPicker = new ColorPicker();
        lineColorPicker.setValue(Color.BLACK);
        Slider lineSizeSlider = new Slider(0, 20, 0);
        lineSizeSlider.setShowTickLabels(true);
        lineSizeSlider.setShowTickMarks(true);
        lineSizeSlider.setMajorTickUnit(5);
        lineSizeSlider.setBlockIncrement(5);
        final double[] line_size = {0};
        Label lineThicknessNum = new Label();
        lineSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                line_size[0] = lineSizeSlider.getValue();
                lineThicknessNum.setText(Integer.toString((int)line_size[0]));
            }
        });
        ToggleButton drawButton = new ToggleButton("Draw");

        Button clearCanvas = new Button("Clear");

        ColorPicker fillColorPicker = new ColorPicker();
        fillColorPicker.setValue(Color.BLACK);
        Button fillButton = new Button("Fill");
        Separator separator1 = new Separator(Orientation.VERTICAL);
        Separator separator2 = new Separator(Orientation.VERTICAL);
        Label drawLabel = new Label("Draw Tools");
        Label fillLabel = new Label("Fill Tools");
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(clearCanvas,
                separator1,
                drawLabel,
                drawButton,
                lineColorPicker,
                lineSizeSlider,
                lineThicknessNum,
                separator2,
                fillLabel,
                fillButton,
                fillColorPicker);
        return toolBar;
    }
}
